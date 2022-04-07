package common.messages;

import java.util.Objects;

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
	 * The floor input id
	 */
	private int floorInputId;

	/**
	 * A ElevatorJobMessage constructor
	 *
	 * @param messageType
	 * @param destinationFloor
	 * @param direction
	 */
	public ElevatorJobMessage(MessageType messageType, int destinationFloor, Direction direction, int floorInputId) {
		super(messageType);
		this.destinationFloor = destinationFloor;
		this.direction = direction;
		this.floorInputId = floorInputId;
	}
	
	

	/**
	 * @return the floorInputId
	 */
	public int getFloorInputId() {
		return floorInputId;
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

	@Override
	public int hashCode() {
		return Objects.hash(destinationFloor, direction);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElevatorJobMessage other = (ElevatorJobMessage) obj;
		return destinationFloor == other.destinationFloor && direction == other.direction;
	}

}
