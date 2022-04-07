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
	private int elevatorId;
	private int floorNumber;
	private Direction direction;
	private String timestamp;
	private Exception errorState;
	private boolean isDoorOpen;
	
	public ElevatorStatusMessage(int elevatorId, Direction direction, int floorNumber, Exception errorState,
			boolean doorIsOpen) {
		super(MessageType.ELEVATOR_STATUS_MESSAGE);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());

		this.elevatorId = elevatorId;
		this.direction = direction;
		this.floorNumber = floorNumber;
		this.errorState = errorState;
		this.isDoorOpen = doorIsOpen;
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
	 * @return the whether or not the door is open
	 */
	public boolean isDoorOpen() {
		return isDoorOpen;
	}
}
