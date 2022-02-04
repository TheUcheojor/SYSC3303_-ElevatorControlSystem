package common.messages;

import java.util.Date;
import java.text.SimpleDateFormat;

import common.DateFormat;
import common.Direction;

public class ElevatorStatusMessage extends Message {
	public boolean isRunning;
	public boolean isDoorOpen;
	
	public Direction direction;
	
	public int floorNumber;

	public String timestamp;
	
	public ElevatorStatusMessage(boolean isRunning){
		super(MessageType.ELEVATOR_STATUS_RESPONSE);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());
		this.isRunning = isRunning;
	}
}
