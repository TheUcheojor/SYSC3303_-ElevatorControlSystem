import java.util.logging.Logger;

import ElevatorSubsystem.ElevatorController;
import FloorSubsystem.FloorSubsystem;
import Scheduler.Scheduler;
import common.LoggerWrapper;

/**
 * This class sets up and starts the elevator, floor, and scheduler systems.
 */

/**
 * @author paulokenne
 *
 */
public class SystemExecutor {
	private static Logger logger = LoggerWrapper.getLogger();
	
	private static int OPEN_DOOR_TIME_SECONDS = 3;
	private static int ELEVATOR_MOVE_BETWEEN_FLOOR_TIME_SECONDS = 1;

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
		logger.info("(SYSTEM) all elevators starting at floor 0");
		ElevatorController elevatorController = new ElevatorController(OPEN_DOOR_TIME_SECONDS);

		// Set up and start the floor subsystem
		String inputFileName = "resources/FloorInputFile.txt";
		FloorSubsystem subsystem = new FloorSubsystem(inputFileName,
				ELEVATOR_MOVE_BETWEEN_FLOOR_TIME_SECONDS);
		subsystem.runMain();
	}

}
