import ElevatorSubsystem.ElevatorCar;
import FloorSubsystem.FloorSubsystem;
import Scheduler.Scheduler;
import common.SimulationFloorInputData;
import common.messages.MessageChannel;

/**
 *
 */

/**
 * @author paulokenne
 *
 */
public class SystemExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MessageChannel floorSubsystemTransmissonChannel = new MessageChannel("Floor Subsystem Transmisson");
		MessageChannel floorSubsystemReceiverChannel = new MessageChannel("Floor Subsystem Receiver");

		MessageChannel elevatorSubsystemTransmissonChannel = new MessageChannel("Elevator Subsystem Transmisson");
		MessageChannel elevatorSubsystemReceiverChannel = new MessageChannel("Elevator Subsystem Receiver");

		String filePath = "resources/FloorInputFile.txt";
		SimulationFloorInputData floorInputData = new SimulationFloorInputData("14:05:15.0 2 UP 4");

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
