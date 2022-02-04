package common.messages;

import java.util.Date;

import common.DateFormat;
import common.Direction;

public class ElevatorStatusResponse extends Message {
	public boolean inService;
	public boolean isDoorOpen;
	public int floorNumber;
	public String timestamp;
	
	public Direction direction;
	
	public ElevatorStatusResponse(boolean inService){
		super(MessageType.ELEVATOR_STATUS_RESPONSE);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());
		this.inService = inService;
	}
}
