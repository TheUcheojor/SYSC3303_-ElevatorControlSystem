package common.messages;

import java.util.Date;

import common.DateFormat;

public class ElevatorStatusRequest extends Message {
	public int elevatorId;
	public String timestamp;
	
	public ElevatorStatusRequest(int elevatorId){
		super(MessageType.ELEVATOR_STATUS_REQUEST);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());
		this.elevatorId = elevatorId;
	}
}
