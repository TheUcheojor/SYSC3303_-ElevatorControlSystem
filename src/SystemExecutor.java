import java.util.logging.Logger;

import ElevatorSubsystem.ElevatorController;
import FloorSubsystem.FloorSubsystem;
import Scheduler.Scheduler;
import common.LoggerWrapper;
import common.gui.GUI;

/**
 * This class sets up and starts the elevator, floor, and scheduler systems.
 */

/**
 * @author paulokenne, favour olotu
 *
 */
public class SystemExecutor {
	private static Logger logger = LoggerWrapper.getLogger();
	
	private static double DOOR_OPEN_CLOSE_TIME_MILLISECONDS = 3000;
	private static double ELEVATOR_MOVE_BETWEEN_FLOOR_TIME_MILLISECONDS = 1000;

	/**
	 * Set up and start the application
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		GUI frame = new GUI();
		frame.recieveUpdates();
		
		// Set up and start the scheduler
		Scheduler scheduler = new Scheduler();
		scheduler.runSchedulerProgram();
		
		

		// Set up and start the elevator controller
		logger.info("(SYSTEM) all elevators starting at floor 0");
		ElevatorController elevatorController = new ElevatorController(DOOR_OPEN_CLOSE_TIME_MILLISECONDS, frame.getNumberOfElevators());

		// Set up and start the floor subsystem
		String inputFileName = "resources/FloorInputFile.txt";
		FloorSubsystem subsystem = new FloorSubsystem(inputFileName,
				ELEVATOR_MOVE_BETWEEN_FLOOR_TIME_MILLISECONDS);
		subsystem.runMain();
		

	}
}
