package common.messages.floor;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageType;

public class ElevatorFloorRequest extends ElevatorJobMessage {
	public ElevatorFloorRequest(int destinationFloor, Direction direction) {
		super(MessageType.ELEVATOR_FLOOR_REQUEST, destinationFloor, direction);
	}
}
