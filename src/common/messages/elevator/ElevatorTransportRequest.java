package common.messages.elevator;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageType;

public class ElevatorTransportRequest extends Message implements ElevatorJobMessage {
	private int destinationFloor;
	private int elevatorId;
	private Direction direction;
	
	ElevatorTransportRequest(int destinationFloor, int elevatorId, Direction direction) {
		super(MessageType.ELEVATOR_TRANSPORT_REQUEST);
		this.destinationFloor = destinationFloor;
		this.elevatorId = elevatorId;
		this.direction = direction;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

	public int getElevatorId() {
		return elevatorId;
	}

	public Direction getDirection() {
		return direction;
	}
}
