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

public class ElevatorStatusMessage extends Message implements ElevatorMessage {
	public int elevatorId;
	public int floorNumber;
	public Direction direction;
	
	public String timestamp;

	public ElevatorStatusMessage(int elevatorId, Direction direction, int floorNumber) {
		super(MessageType.ELEVATOR_STATUS_MESSAGE);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());

		this.elevatorId = elevatorId;
		this.direction = direction;
		this.floorNumber = floorNumber;
	}

	@Override
	public int getId() {
		return elevatorId;
	}
}
