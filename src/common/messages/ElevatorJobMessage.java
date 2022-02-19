package common.messages;

import common.Direction;

/**
 * This class represents an elevator job.
 *
 * The elevator job could be a floor request for an elevator or a request to
 * move the passenger in the elevator to a destination floor.
 *
 * @author ryanfife, paulokenne
 */
public abstract class ElevatorJobMessage extends Message {

	/**
	 * The destination floor.
	 */
	private int destinationFloor;

	/**
	 * The direction.
	 */
	private Direction direction;

	/**
	 * A ElevatorJobMessage constructor
	 *
	 * @param messageType the message type
	 */
	public ElevatorJobMessage(MessageType messageType) {
		super(messageType);
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
