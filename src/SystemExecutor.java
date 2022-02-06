import ElevatorSubsystem.ElevatorCar;
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

	/**
	 * Set up and start the application
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		MessageChannel floorSubsystemTransmissonChannel = new MessageChannel("Floor Subsystem Transmisson");
		MessageChannel floorSubsystemReceiverChannel = new MessageChannel("Floor Subsystem Receiver");

		MessageChannel elevatorSubsystemTransmissonChannel = new MessageChannel("Elevator Subsystem Transmisson");
		MessageChannel elevatorSubsystemReceiverChannel = new MessageChannel("Elevator Subsystem Receiver");

		String filePath = "resources/FloorInputFile.txt";

		Thread floorSubsystem = new Thread(
				new FloorSubsystem(filePath, floorSubsystemTransmissonChannel, floorSubsystemReceiverChannel),
				"Floor Subsystem");

		Thread elevatorSubsystem = new Thread(
				new ElevatorCar(1, elevatorSubsystemTransmissonChannel, elevatorSubsystemReceiverChannel),
				"Elevator Car");

		Thread scheduler = new Thread(new Scheduler(floorSubsystemTransmissonChannel, floorSubsystemReceiverChannel,
				elevatorSubsystemTransmissonChannel, elevatorSubsystemReceiverChannel), "Scheduler");

		scheduler.start();
		floorSubsystem.start();
		elevatorSubsystem.start();
	}

}
