import ElevatorSubsystem.ElevatorController;
import FloorSubsystem.FloorSubsystem;
import Scheduler.Scheduler;
import common.messages.MessageChannel;

/**
 * This class sets up and starts the elevator, floor, and scheduler systems.
 */

/**
 * @author paulokenne
 *
 */
public class SystemExecutor {
	public static final int DEFAULT_MESSAGE_QUEUE_SIZE = 10;

	/**
	 * Set up and start the application
	 *
	 * @param args
	 */
	public static void main(String[] args) {
	/*	MessageChannel floorSubsystemTransmissonChannel = new MessageChannel("Floor Subsystem Transmisson", DEFAULT_MESSAGE_QUEUE_SIZE);
		MessageChannel floorSubsystemReceiverChannel = new MessageChannel("Floor Subsystem Receiver", DEFAULT_MESSAGE_QUEUE_SIZE);

		MessageChannel elevatorSubsystemTransmissonChannel = new MessageChannel("Elevator Subsystem Transmisson", DEFAULT_MESSAGE_QUEUE_SIZE);
		MessageChannel elevatorSubsystemReceiverChannel = new MessageChannel("Elevator Subsystem Receiver", DEFAULT_MESSAGE_QUEUE_SIZE);

		String filePath = "resources/FloorInputFile.txt";

		Thread floorSubsystem = new Thread(new FloorSubsystem(filePath, floorSubsystemTransmissonChannel,
				floorSubsystemReceiverChannel, elevatorSubsystemReceiverChannel), "Floor Subsystem");

		Thread elevatorSubsystem = new Thread(
				new ElevatorController(elevatorSubsystemTransmissonChannel, elevatorSubsystemReceiverChannel, floorSubsystemReceiverChannel),
				"Elevator Car");

		Thread scheduler = new Thread(new Scheduler(floorSubsystemTransmissonChannel, floorSubsystemReceiverChannel,
				elevatorSubsystemTransmissonChannel, elevatorSubsystemReceiverChannel), "Scheduler");

		scheduler.start();
		floorSubsystem.start();
		elevatorSubsystem.start();
		*/
	}

}
