import ElevatorSubsystem.ElevatorController;
import FloorSubsystem.FloorSubsystem;

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
		ElevatorController elevatorController = new ElevatorController();

		FloorSubsystem floorSubsystem = new FloorSubsystem();
	}

}
