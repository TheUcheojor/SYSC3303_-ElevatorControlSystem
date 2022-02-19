package common.messages.elevator;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageType;

public class ElevatorTransportRequest extends ElevatorJobMessage  {
	private int elevatorId;
	
	public ElevatorTransportRequest(int destinationFloor, int elevatorId, Direction direction) {
		super(MessageType.ELEVATOR_TRANSPORT_REQUEST, destinationFloor, direction);
		this.elevatorId = elevatorId;
	}

	public int getElevatorId() {
		return elevatorId;
	}
}
