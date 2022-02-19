/**
 *
 */
package FloorSubsystem;

import common.Direction;

/**
 * The class represents the direction lamp which is used to denote the arrival
 * and direction of an elevator at a floor
 *
 * @author paulokenne
 *
 */
public class DirectionLamp {

	/**
	 * The floor number of the elevator.
	 *
	 * In our application, all elevators start at floor zero.
	 */
	private int floorNumber = 0;

	/**
	 * The direction of the elevator.
	 */
	private Direction direction = null;

	/**
	 * A DirectionLamp constructor.
	 */
	public DirectionLamp() {
	}

	/**
	 * Get the elevator floor.
	 *
	 * @return the elevator floor
	 */
	public int getElevatorFloor() {
		return floorNumber;
	}

	/**
	 * Get the floor direction.
	 *
	 * @return the floor direction
	 */
	public Direction getFloorDirection() {
		return direction;
	}

	/**
	 * Set the elevator direction
	 *
	 * @param direction the direction
	 */
	public void setElevatorDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Set the floor number.
	 *
	 * @param floorNumber the floor
	 */
	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}
}
