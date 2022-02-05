package common.messages;

import java.util.Date;

import common.DateFormat;
import common.Direction;

/**
 * A DS representing an elevator status response
 * 
 * @author Ryan Fife
 *
 */

public class ElevatorStatusMessage extends Message implements ElevatorMessage {
	public int elevatorId;
	public boolean inService;
	public boolean isDoorOpen;
	public int floorNumber;
	public String timestamp;
	
	public Direction direction;
	
	public ElevatorStatusMessage(boolean inService, int elevatorId){
		super(MessageType.ELEVATOR_STATUS_MESSAGE);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());
		this.inService = inService;
		this.elevatorId = elevatorId;
	}
	

	public int getId() {
		return elevatorId;
	}
}
