package common.messages.floor;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageType;

/**
 * This class represents when a passenger requests for an elevator at a floor
 *
 * @author ryanfire, paulokenne
 *
 */
public class ElevatorFloorRequest extends Message implements ElevatorJobMessage {

	/**
	 * The floor that the passenger is requesting from.
	 */
	private int destinationFloor;

	/**
	 * The direction the passenger wishes to go
	 */
	private Direction direction;

	/**
	 * A ElevatorFloorRequest constructor
	 *
	 * @param destinationFloor the destination floor
	 * @param direction        the direction
	 */
	public ElevatorFloorRequest(int destinationFloor, Direction direction) {
		super(MessageType.ELEVATOR_FLOOR_REQUEST);
		this.destinationFloor = destinationFloor;
		this.direction = direction;
	}

	@Override
	public int getDestinationFloor() {
		return destinationFloor;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}
}
