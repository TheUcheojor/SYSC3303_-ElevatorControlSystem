package ElevatorSubsystem;

import java.util.HashMap;
import java.util.Map;

import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.gui.ElevatorControllerObserver;
import common.messages.Message;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.GUIStatusMessage;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;

/**
 * Controller class that instantiates the required entities for managing the
 * elevator entity behaviour. Additionally, the controller is the central hub
 * for receiving elevator related messages from other subsystems.
 *
 * @author Ryan Fife, Favour
 *
 */
public class ElevatorController{
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
	
//	private ElevatorControllerObserver observer;
	
	public ElevatorController() {//ElevatorControllerObserver observer) {
		//this.observer = observer;
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
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
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
		floorMessageQueue = new ElevatorFloorMessageWorkQueue(schedulerSubsystemCommunication, elevators);
		schedulerMessageQueue = new ElevatorSchedulerMessageWorkQueue(schedulerSubsystemCommunication,
				floorSubsystemCommunication, elevators);

		// initialize the message receiving threads
		(new Thread() {
			@Override
			public void run() {
				// wait for floor messages
				while (true) {
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
				while (true) {
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

		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			// send initial status message to scheduler
			ElevatorCar car = elevators.get(i);
			ElevatorStatusMessage status = car.createStatusMessage();
			//GUIStatusMessage statusGUI = car.createGUIStatusMessage();
			try {
				schedulerSubsystemCommunication.sendMessage(status);
				//observer.handleStatusUpdate(statusGUI);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ElevatorCar getElevatorCar(int elevatorId) {
		return elevators.get(elevatorId);
	} 
	//	public void addObserver(ElevatorControllerObserver observer) {
//		this.observer = observer;
//		System.out.println(observer);
//	}

	// For running on stand alone system
//	public static void main(String[] args) {
//		ElevatorController controller = new ElevatorController();
//	}

}
