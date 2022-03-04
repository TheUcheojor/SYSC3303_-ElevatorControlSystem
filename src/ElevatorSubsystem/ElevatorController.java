/**
 * 
 */
package ElevatorSubsystem;

import java.util.HashMap;
import java.util.Map;

import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.SchedulerElevatorCommand;

/**
 * @author Ryan Fife
 *
 */
public class ElevatorController implements Runnable {
	/**
	 * The number of elevators in the system
	 */
	public final static int NUMBER_OF_ELEVATORS = 1;
	/**
	 * The door opening and closing time in seconds
	 */
	public final static double DOOR_SPEED = 3000;

	/**
	 * The elevator speed in meters per second.
	 */
	public final static double MAX_ELEVATOR_SPEED = 3;
	/**
	 * The elevator acceleration in meters per second squared.
	 */
	public final static double ELEVATOR_ACCELERATION = 1.5;
	
	private MessageChannel outgoingSchedulerChannel;
	private MessageChannel outgoingFloorChannel;
	private MessageChannel incomingChannel;
	private Map<Integer, ElevatorCar> elevators;
	

	public ElevatorController(
			MessageChannel outgoingSchedulerChannel,
			MessageChannel incomingChannel,
			MessageChannel outgoingFloorChannel
			) {
		// Validate that the elevator values are valid
		try {
			SystemValidationUtil.validateElevatorMaxSpeed(MAX_ELEVATOR_SPEED);
			SystemValidationUtil.validateElevatorAcceleration(ELEVATOR_ACCELERATION);
		} catch (InvalidSystemConfigurationInputException e) {
			System.out.println("InvalidSystemConfigurationInputException: " + e);
			// Terminate if the elevation configuration are invalid.
			System.exit(1);
		}

		// initialize elevator cars
		elevators = new HashMap<Integer, ElevatorCar>();
		for(int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			ElevatorDoor door = new ElevatorDoor(DOOR_SPEED);
			ElevatorMotor motor = new ElevatorMotor(MAX_ELEVATOR_SPEED, ELEVATOR_ACCELERATION);
			int carId = i;
			
			ElevatorCar car = new ElevatorCar(carId, motor, door);
			
			elevators.put(carId, car);
		}

		this.outgoingSchedulerChannel = outgoingSchedulerChannel;
		this.outgoingFloorChannel = outgoingFloorChannel;
		this.incomingChannel = incomingChannel;
	}
	
	@Override
	public void run() {

		// Elevators are ready for jobs
		elevators.forEach((Integer carId, ElevatorCar car) -> {
			ElevatorStatusMessage status = car.createStatusMessage();
			outgoingSchedulerChannel.appendMessage(status);
		});

		while (true) {
			// send status message and wait for a response from scheduler response in loop
			Message message = incomingChannel.popMessage();
			handleMessage(message);
		}
	}
	
	/**
	 * Elevator message handler. Exterior entities can send various types of request
	 * or commands to the elevator.
	 *
	 * @param message to handle
	 * @throws Exception if the message doesn't belong to this elevator
	 */
	// @PublicForTestOnly
	public void handleMessage(Message message) {

		
		switch (message.getMessageType()) {
		

		case ELEVATOR_STATUS_REQUEST:
			ElevatorStatusMessage statusMessage = (ElevatorStatusMessage)message;
			outgoingSchedulerChannel.appendMessage(elevators.get(statusMessage.getElevatorId()).createStatusMessage());
			break;
			
		case ELEVATOR_TRANSPORT_REQUEST:
			ElevatorTransportRequest transportRequest = (ElevatorTransportRequest) message;
			outgoingSchedulerChannel.appendMessage(elevators.get(transportRequest.getElevatorId()).createStatusMessage());
			break;
			
		case SCHEDULER_ELEVATOR_COMMAND:
			SchedulerElevatorCommand schedulerCommand =(SchedulerElevatorCommand) message;
			handleElevatorCommand(schedulerCommand);
			ElevatorStatusMessage postCommandStatus = elevators.get(schedulerCommand.getElevatorID()).createStatusMessage();
			outgoingSchedulerChannel.appendMessage(postCommandStatus);
			break;
			
		case ELEVATOR_FLOOR_MESSAGE:
			handleFloorMessage((FloorElevatorTargetedMessage) message);
			break;
			
		default:
			break;

		}
	}
	
	private void handleFloorMessage(FloorElevatorTargetedMessage message) {
		switch(message.getRequestType()) {
		case FLOOR_ARRIVAL_MESSAGE:
			ElevatorFloorArrivalMessage arrivalMessage = ((ElevatorFloorArrivalMessage) message);
			int floorNumber = arrivalMessage.getFloorId();
			
			System.out.println("Elevator has reached floor: " + floorNumber);
			ElevatorStatusMessage arrivalStatus = elevators.get(message.getElevatorId()).createStatusMessage();
			outgoingSchedulerChannel.appendMessage(arrivalStatus);
			break;
			
		default:
			break;
		}
	}
	
	private void handleElevatorCommand(SchedulerElevatorCommand command) {
		ElevatorLeavingFloorMessage leavingMessage;
		ElevatorFloorSignalRequestMessage comingMessage;
		
		switch(command.getCommand()) {
			case STOP:
				if(!door.isOpen()) {
					System.out.println("Elevator stopping\n.");
					motor.turnOff();
				}else {
					errorState = new Exception("Attempted to stop while doors open");
				}
				break;
			case CLOSE_DOORS:
				System.out.println("Elevator door closing\n.");
				door.closeDoor();
				break;
			case OPEN_DOORS:
				if(!motor.getIsRunning()) {
					System.out.println("Elevator door opening\n.");
					door.openDoor();
				}else {
					errorState = new Exception("Attempted to open doors while motor running");
				}
				break;
			case MOVE_UP:
				System.out.println("Elevator door closing\n.");
				door.closeDoor();
				System.out.println("Elevator moving up\n.");
				motor.goUp();
				
				leavingMessage = new ElevatorLeavingFloorMessage(id, floorNumber);
				comingMessage = new ElevatorFloorSignalRequestMessage(id, floorNumber + 1, motor, true);
				
				outgoingFloorChannel.appendMessage(leavingMessage);
				outgoingFloorChannel.appendMessage(comingMessage);
				break;
			case MOVE_DOWN:
				System.out.println("Elevator door closing\n.");
				door.closeDoor();
				System.out.println("Elevator moving down\n.");
				motor.goDown();
				
				leavingMessage = new ElevatorLeavingFloorMessage(id, floorNumber);
				comingMessage = new ElevatorFloorSignalRequestMessage(id, floorNumber - 1, motor, true);
				
				outgoingFloorChannel.appendMessage(leavingMessage);
				outgoingFloorChannel.appendMessage(comingMessage);
			
				break;
		}
	}

}
