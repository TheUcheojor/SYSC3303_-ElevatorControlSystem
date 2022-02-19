package common.messages;

import common.Direction;

/**
 * This class represents an elevator job request
 *
 * @author ryanfife, paulokenne
 *
 */
public abstract class ElevatorJobMessage extends Message {
	/**
	 * The destination floor
	 */
	private int destinationFloor;

	/**
	 * The direction
	 */
	private Direction direction;

	/**
	 * A ElevatorJobMessage constructor
	 *
	 * @param messageType
	 * @param destinationFloor
	 * @param direction
	 */
	public ElevatorJobMessage(MessageType messageType, int destinationFloor, Direction direction) {
		super(messageType);
		this.destinationFloor = destinationFloor;
		this.direction = direction;
	}

	/**
	 * Get the destination floor
	 *
	 * @return the destination floor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}

	/**
	 * Get the direction
	 *
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}
}
