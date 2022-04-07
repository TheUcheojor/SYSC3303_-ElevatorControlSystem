package common.messages.elevator;

import java.util.Date;

import common.DateFormat;
import common.Direction;
import common.messages.Message;
import common.messages.MessageType;

/**
 * A DS representing an elevator status response
 *
 * @author Ryan Fife
 *
 */

public class ElevatorStatusMessage extends Message {

	/**
	 * The elevator id
	 */
	private int elevatorId;

	/**
	 * The flood number
	 */
	private int floorNumber;

	/**
	 * The direction
	 */
	private Direction direction;

	/**
	 * A time stamp
	 */
	private String timestamp;

	/**
	 * The exception
	 */
	private Exception errorState;

	/**
	 * A flag indicating that the elevator is resolving an error
	 */
	private boolean isResolvingError;

	/**
	 * A flag that indicates that the scheduler should issue the next command
	 */
	private boolean issueNextCommand = true;

	/**
	 * A ElevatorStatusMessage constructor
	 *
	 * @param elevatorId       the elevator id
	 * @param direction        the direction
	 * @param floorNumber      the floor number
	 * @param errorState       the error state
	 * @param isResolvingError a flag indicating that the elevator is resolving an
	 *                         error
	 */
	public ElevatorStatusMessage(int elevatorId, Direction direction, int floorNumber, Exception errorState,
			boolean isResolvingError) {
		super(MessageType.ELEVATOR_STATUS_MESSAGE);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());

		this.elevatorId = elevatorId;
		this.direction = direction;
		this.floorNumber = floorNumber;
		this.errorState = errorState;
		this.isResolvingError = isResolvingError;
	}

	/**
	 * A ElevatorStatusMessage constructor
	 *
	 * @param elevatorId       the elevator id
	 * @param direction        the direction
	 * @param floorNumber      the floor number
	 * @param errorState       the error state
	 * @param isResolvingError a flag indicating that the elevator is resolving an
	 *                         error
	 */
	public ElevatorStatusMessage(int elevatorId, Direction direction, int floorNumber, Exception errorState,
			boolean isResolvingError, boolean issueNextCommand) {
		super(MessageType.ELEVATOR_STATUS_MESSAGE);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());

		this.elevatorId = elevatorId;
		this.direction = direction;
		this.floorNumber = floorNumber;
		this.errorState = errorState;
		this.isResolvingError = isResolvingError;
		this.issueNextCommand = issueNextCommand;
	}

	/**
	 * @return the elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}

	/**
	 * @return the floorNumber
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the errorState
	 */
	public Exception getErrorState() {
		return errorState;
	}

	/**
	 * @return the isResolvingError
	 */
	public boolean isResolvingError() {
		return isResolvingError;
	}

	/**
	 * Return a flag indicating whether the scheduler should issue the next commant
	 *
	 * @return the issueNextCommand
	 */
	public boolean shouldIssueNextCommand() {
		return issueNextCommand;
	}

}
