package common.messages;

import java.util.Date;

import common.DateFormat;

/**
 * A DS representing an elevator status request
 * 
 * @author Ryan Fife
 *
 */

public class ElevatorStatusRequest extends Message implements ElevatorMessage {
	public int elevatorId;
	public String timestamp;
	
	public ElevatorStatusRequest(int elevatorId){
		super(MessageType.ELEVATOR_STATUS_REQUEST);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());
		this.elevatorId = elevatorId;
	}
	
	public int getId() {
		return elevatorId;
	}
}
