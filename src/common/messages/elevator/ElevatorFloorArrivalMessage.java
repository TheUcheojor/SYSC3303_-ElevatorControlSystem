/**
 *
 */
package common.messages.elevator;

import common.messages.FloorElevatorTargetedMessage;

/**
 * This class represents a message that is sent by the floor to the elevator to
 * indicate that the elevator has reached the floor
 *
 * @author paulokenne
 *
 */
public class ElevatorFloorArrivalMessage extends FloorElevatorTargetedMessage {

	/**
	 * The current elevator speed at the floor.
	 */
	private double newCurrentElevatorSpeed;

	/**
	 * A ElevatorFloorArrivalMessage constructor.
	 */
	public ElevatorFloorArrivalMessage(int elevatorId, int floorId, double newCurrentElevatorSpeed) {
		super(elevatorId, floorId, ElevatorFloorRequestType.FLOOR_ARRIVAL_MESSAGE);
		this.newCurrentElevatorSpeed = newCurrentElevatorSpeed;
	}

	/**
	 * Return the new elevator speed at the floor
	 *
	 * @return the new elevator speed
	 */
	public double getNewCurrentElevatorSpeed() {
		return newCurrentElevatorSpeed;
	}
}
