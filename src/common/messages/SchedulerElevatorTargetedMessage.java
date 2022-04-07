package common.messages;

/**
 * This superclass is for communication between the scheduler and a specific elevator
 *
 * @author Ryan Fife
 *
 */
public abstract class SchedulerElevatorTargetedMessage extends Message {

	/**
	 * The ID of the elevator getting the command
	 */
	private int elevatorId;
	
	public SchedulerElevatorTargetedMessage(MessageType requestType, int elevatorId) {
		super(requestType);
		this.elevatorId = elevatorId;
	}
	
	/**
	 * This method returns the elevator ID number
	 * @return - The elevator ID
	 */
	public int getElevatorId() {
		return elevatorId;
	}
}
