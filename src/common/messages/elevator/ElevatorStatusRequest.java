package common.messages.elevator;

import java.util.Date;

import common.DateFormat;
import common.messages.Message;
import common.messages.MessageType;

/**
 * An elevator status request
 *
 * @author Ryan Fife
 *
 */
public class ElevatorStatusRequest extends Message {
	private int elevatorId;
	private String timestamp;

	public ElevatorStatusRequest(int elevatorId) {
		super(MessageType.ELEVATOR_STATUS_REQUEST);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());
		this.elevatorId = elevatorId;
	}

	public int getId() {
		return elevatorId;
	}
	
	public String getTimeStamp() {
		return timestamp;
	}
}
