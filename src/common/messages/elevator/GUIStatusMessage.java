package common.messages.elevator;


import java.util.Date;

import ElevatorSubsystem.ElevatorDoor;
import common.DateFormat;
import common.Direction;
import common.messages.Message;
import common.messages.MessageType;

public class GUIStatusMessage extends Message{

	private int elevatorId;
	private int floorNumber;
	private Direction direction;
	private ElevatorDoor elevatorDoor;
	private Exception errorState;
	private String timestamp;

	
	
	public GUIStatusMessage(int elevatorId, Direction direction, int floorNumber, Exception errorState, ElevatorDoor elevatorDoor) {
		super(MessageType.GUI_MESSAGE);
		this.timestamp = DateFormat.DATE_FORMAT.format(new Date());
		this.elevatorId = elevatorId;
		this.direction = direction;
		this.floorNumber = floorNumber;
		this.errorState = errorState;
		this.elevatorDoor = elevatorDoor;
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
	 * @return the errorState
	 */
	public Exception getErrorState() {
		return errorState;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}


	/**
	 * 
	 * @return Elevator door status
	 */
	public ElevatorDoor getElevatorDoor() {
		return elevatorDoor;
	}
	}
