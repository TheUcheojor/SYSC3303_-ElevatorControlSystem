package common.messages;

import common.Direction;

public interface ElevatorJobMessage {
	public int getDestinationFloor();
	public Direction getDirection();
}
