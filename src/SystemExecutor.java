import java.util.logging.Logger;

import javax.swing.JFrame;

import ElevatorSubsystem.ElevatorController;
import FloorSubsystem.FloorSubsystem;
import Scheduler.Scheduler;
import common.LoggerWrapper;
import common.gui.GUI;

/**
 * This class sets up and starts the elevator, floor, and scheduler systems.
 */

/**
 * @author paulokenne
 *
 */
public class SystemExecutor {
	private static Logger logger = LoggerWrapper.getLogger();

	/**
	 * Set up and start the application
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		GUI frame = new GUI();
		
		// Set up and start the scheduler
		Scheduler scheduler = new Scheduler();
		scheduler.runSchedulerProgram();
		
		
		// Set up and start the elevator controller
		logger.info("(SYSTEM) all elevators starting at floor 0");
		ElevatorController elevatorController = new ElevatorController();
        frame.setEle(elevatorController);
        
        
		

		// Set up and start the floor subsystem
		String inputFileName = "resources/FloorInputFile.txt";
		FloorSubsystem subsystem = new FloorSubsystem(inputFileName);
		subsystem.runMain();
		
	}

}
