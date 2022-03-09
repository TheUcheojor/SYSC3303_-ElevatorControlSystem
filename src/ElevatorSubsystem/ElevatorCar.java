package ElevatorSubsystem;

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
 * Entity representing an elevator car, contains composite subcomponents for
 * major functionality.
 *
 * @author Ryan Fife, paulokenne
 *
 */
public class ElevatorCar implements Runnable {
	private int id;
	private boolean inService;
	private MessageChannel outgoingSchedulerChannel;
	private MessageChannel outgoingFloorChannel;
	private MessageChannel incomingChannel;
	private ElevatorMotor motor;
	private ElevatorDoor door;
	private int floorNumber = 0;
	private Exception errorState;

	/**
	 * The number of elevators in the system
	 *
	 * TODO to be relocated to a elevator subsystem level
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
	

	public ElevatorCar(int id,
			MessageChannel outgoingSchedulerChannel,
			MessageChannel incomingChannel,
			MessageChannel outgoingFloorChannel) {
		// Validate that the elevator values are valid
		try {
			SystemValidationUtil.validateElevatorMaxSpeed(MAX_ELEVATOR_SPEED);
			SystemValidationUtil.validateElevatorAcceleration(ELEVATOR_ACCELERATION);
		} catch (InvalidSystemConfigurationInputException e) {
			System.out.println("InvalidSystemConfigurationInputException: " + e);
			// Terminate if the elevation configuration are invalid.
			System.exit(1);
		}

		this.id = id;
		this.outgoingSchedulerChannel = outgoingSchedulerChannel;
		this.outgoingFloorChannel = outgoingFloorChannel;
		this.incomingChannel = incomingChannel;
		this.inService = true;
		door = new ElevatorDoor(DOOR_SPEED);
		motor = new ElevatorMotor(MAX_ELEVATOR_SPEED, ELEVATOR_ACCELERATION);
	}

	public int getId() {
		return this.id;
	}

	public ElevatorMotor getMotor() {
		return motor;
	}

	public ElevatorDoor getDoor() {
		return door;
	}

	public boolean getInService() {
		return inService;
	}

	public void setInService(boolean service) {
		inService = service;
	}

	@Override
	public void run() {

		// The elevator is ready
		ElevatorStatusMessage status = createStatusMessage();
		outgoingSchedulerChannel.appendMessage(status);

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
			ElevatorStatusMessage status = createStatusMessage();
			outgoingSchedulerChannel.appendMessage(status);
			break;
			
		case ELEVATOR_DROP_PASSENGER_REQUEST:
			outgoingSchedulerChannel.appendMessage((ElevatorTransportRequest) message);
			break;
			
		case SCHEDULER_ELEVATOR_COMMAND:
			handleElevatorCommand((SchedulerElevatorCommand) message);
			ElevatorStatusMessage postCommandStatus = createStatusMessage();
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
			floorNumber = arrivalMessage.getFloorId();
			
			System.out.println("Elevator has reached floor: " + floorNumber);
			ElevatorStatusMessage arrivalStatus = createStatusMessage();
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

	/**
	 * Creates a ElevatorStatusMessage message containing all relevant status info
	 * for this elevator.
	 *
	 * @return status response message
	 */
	// @PublicForTestOnly
	public ElevatorStatusMessage createStatusMessage() {
		ElevatorStatusMessage status = new ElevatorStatusMessage(
				id,
				this.getMotor().getDirection(),
				floorNumber,
				errorState
				);
		// TODO (rfife): Pass current floor

		return status;
	}
}