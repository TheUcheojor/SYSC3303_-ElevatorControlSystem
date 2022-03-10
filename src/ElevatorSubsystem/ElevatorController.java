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
import common.messages.elevator.ElevatorStatusRequest;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;
import common.work_management.ElevatorFloorMessageWorkQueue;
import common.work_management.ElevatorSchedulerMessageWorkQueue;
import common.work_management.MessageWorkQueue;

/**
 * @author Ryan Fife, Favour
 *
 */
public class ElevatorController implements Runnable {
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

	private Map<Integer, ElevatorCar> elevators;
	private int floorNumber = 0;

	private ElevatorFloorMessageWorkQueue floorMessageQueue;
	private ElevatorSchedulerMessageWorkQueue schedulerMessageQueue;

	private SubsystemCommunicationRPC schedulerSubsystemCommunication;
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

		schedulerSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
				SubsystemComponentType.SCHEDULER);
		floorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
				SubsystemComponentType.FLOOR_SUBSYSTEM);

		floorMessageQueue = new ElevatorFloorMessageWorkQueue(schedulerSubsystemCommunication,
				floorSubsystemCommunication);
		schedulerMessageQueue = new ElevatorSchedulerMessageWorkQueue(schedulerSubsystemCommunication,
				floorSubsystemCommunication);
		
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
	 * This method returns the collection of elevator cars
	 */
	public Map<Integer, ElevatorCar> getElevators() {
		return this.elevators;
	}
}
