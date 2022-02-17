package common.messages.floor;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageType;

public class ElevatorFloorRequest extends Message implements ElevatorJobMessage {
	private int destinationFloor;
	private Direction direction;
	
	public ElevatorFloorRequest(int destinationFloor, Direction direction) {
		super(MessageType.ELEVATOR_FLOOR_REQUEST);
		this.destinationFloor = destinationFloor;
		this.direction = direction;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}

	public Direction getDirection() {
		return direction;
	}
}
