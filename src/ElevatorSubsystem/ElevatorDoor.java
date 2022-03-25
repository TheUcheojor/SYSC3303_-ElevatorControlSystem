package ElevatorSubsystem;

/**
 * This class represents the elevator door
 *
 * @author paulokenne, ryanfife, favourolotu
 *
 */
public class ElevatorDoor {

	/**
	 * The door open/close time in milliseconds
	 */
	private double doorOpenCloseTime;

	/**
	 * A flag that indicates if the door is open
	 */
	private boolean isOpen = false;

	/**
	 * A ElevatorDoor constructor
	 *
	 * @param doorOpenCloseTime the door open/close time
	 */
	public ElevatorDoor(double doorOpenCloseTime) {
		this.doorOpenCloseTime = doorOpenCloseTime;
	}

	/**
	 * Return a flag indicating if the door is open
	 *
	 * @return true if open and false otherwise
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Open the elevator door
	 */
	public void openDoor() {
		if (!this.isOpen) {
			try {
				Thread.sleep((long) this.doorOpenCloseTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isOpen = true;
		}
	}

	/**
	 * Close the elevator door with an error override
	 *
	 * @return true if successful and false otherwise
	 */
	public boolean closeDoor() {
		if (this.isOpen) {
			try {
				Thread.sleep((long) this.doorOpenCloseTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isOpen = false;

		}

		return true;
	}

	/**
	 * Close the elevator door with an error override
	 *
	 * @param errorOverride the error override
	 * @return true if successful and false otherwise
	 */
	public boolean closeDoor(boolean errorOverride) {
		closeDoor();
		return (errorOverride) ? false : true;
	}

	/**
	 * Return the door open close time
	 *
	 * @return the door open close time
	 */
	public double getDoorOpenCloseTime() {
		return this.doorOpenCloseTime;
	}

}
