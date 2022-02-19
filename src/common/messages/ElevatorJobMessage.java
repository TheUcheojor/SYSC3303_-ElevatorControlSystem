package common.messages;

import common.Direction;

public abstract class ElevatorJobMessage extends Message {
	private int destinationFloor;
	private Direction direction;
	
	public ElevatorJobMessage(MessageType messageType, int destinationFloor, Direction direction) {
		super(messageType);
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
