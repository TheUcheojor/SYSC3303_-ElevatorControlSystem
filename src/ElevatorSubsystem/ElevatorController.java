package ElevatorSubsystem;

import java.util.HashMap;
import java.util.Map;

import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.messages.Message;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;

/**
 * @author Ryan Fife, Favour
 *
 */
public class ElevatorController {
	/**
	 * The number of elevators in the system
	 */
	public final static int NUMBER_OF_ELEVATORS = 2;
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

	/**
	 * Collection of the elevator cars in this subsystem
	 */

	private Map<Integer, ElevatorCar> elevators;

	/**
	 * Message queue for received floor messages
	 */
	private ElevatorFloorMessageWorkQueue floorMessageQueue;
	
	/**
	 * Message queue for received scheduler messages
	 */
	private ElevatorSchedulerMessageWorkQueue schedulerMessageQueue;

	/**
	 * RPC communications channel for the scheduler
	 */
	private SubsystemCommunicationRPC schedulerSubsystemCommunication;
	
	/**
	 * RPC communications channel for the floor
	 */
	private SubsystemCommunicationRPC floorSubsystemCommunication;
	
	public ElevatorController() { 
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
		for (int i = 1; i <= NUMBER_OF_ELEVATORS; i++) {
			ElevatorDoor door = new ElevatorDoor(DOOR_SPEED);
			ElevatorMotor motor = new ElevatorMotor(MAX_ELEVATOR_SPEED, ELEVATOR_ACCELERATION);
			int carId = i;

			ElevatorCar car = new ElevatorCar(carId, motor, door);

			elevators.put(carId, car);
		}
	
		// initialize the subsystem communication channels
		schedulerSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
				SubsystemComponentType.SCHEDULER);
		floorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
				SubsystemComponentType.FLOOR_SUBSYSTEM);

		// initialize the message queues
		floorMessageQueue = new ElevatorFloorMessageWorkQueue(schedulerSubsystemCommunication,
				elevators);
		schedulerMessageQueue = new ElevatorSchedulerMessageWorkQueue(schedulerSubsystemCommunication,
				floorSubsystemCommunication, elevators);
		
		// initialize the message receiving threads
		(new Thread() {
			@Override
			public void run() {
				// wait for floor messages
				while(true) {
					Message message;
					try {
						message = floorSubsystemCommunication.receiveMessage();
						floorMessageQueue.enqueueMessage(message);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		(new Thread() {
			@Override
			public void run() {
				// wait for scheduler messages
				while(true) {
					Message message;
					try {
						message = schedulerSubsystemCommunication.receiveMessage();
						schedulerMessageQueue.enqueueMessage(message);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
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
			ElevatorStatusRequest statusRequest = (ElevatorStatusRequest) message;
			outgoingSchedulerChannel.appendMessage(elevators.get(statusRequest.getId()).createStatusMessage());
			break;

		case ELEVATOR_DROP_PASSENGER_REQUEST:
			ElevatorTransportRequest transportRequest = (ElevatorTransportRequest) message;
			outgoingSchedulerChannel
					.appendMessage(elevators.get(transportRequest.getElevatorId()).createStatusMessage());
			break;

		case SCHEDULER_ELEVATOR_COMMAND:
			SchedulerElevatorCommand schedulerCommand = (SchedulerElevatorCommand) message;
			handleElevatorCommand(schedulerCommand);
			ElevatorStatusMessage postCommandStatus = elevators.get(schedulerCommand.getElevatorID())
					.createStatusMessage();
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
		switch (message.getRequestType()) {
		case FLOOR_ARRIVAL_MESSAGE:
			ElevatorFloorArrivalMessage arrivalMessage = ((ElevatorFloorArrivalMessage) message);
			floorNumber = arrivalMessage.getFloorId();

			System.out.println("Elevator has reached floor: " + floorNumber);
			ElevatorStatusMessage arrivalStatus = elevators.get(message.getElevatorId()).createStatusMessage();
			outgoingSchedulerChannel.appendMessage(arrivalStatus);
			break;

		default:
			break;
		}
	}

	// For running on stand alone system
	public static void main(String[] args) {
		ElevatorController controller = new ElevatorController();
	}
}
