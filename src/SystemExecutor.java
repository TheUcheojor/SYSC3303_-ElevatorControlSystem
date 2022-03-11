import ElevatorSubsystem.ElevatorController;
import FloorSubsystem.FloorSubsystem;
import Scheduler.Scheduler;

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

		// Set up and start the scheduler
		Scheduler scheduler = new Scheduler();
		scheduler.runSchedulerProgram();

		// Set up and start the elevator controller
		ElevatorController elevatorController = new ElevatorController();

		// Set up and start the floor subsystem
		String inputFileName = "resources/FloorInputFile.txt";
		FloorSubsystem subsystem = new FloorSubsystem(inputFileName);
		subsystem.runMain();
	}

}
