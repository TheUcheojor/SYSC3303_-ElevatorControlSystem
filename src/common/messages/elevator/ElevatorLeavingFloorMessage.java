/**
 *
 */
package common.messages.elevator;

import common.messages.FloorElevatorTargetedMessage;

/**
 * This class represents a message that the elevator sends to the floor to
 * notify it that it is leaving it.
 *
 * @author paulokenne
 *
 */
public class ElevatorLeavingFloorMessage extends FloorElevatorTargetedMessage {

	/**
	 * A ElevatorLeavingFloorMessage constructor.
	 */
	public ElevatorLeavingFloorMessage(int elevatorId, int floorId) {
		super(elevatorId, floorId, ElevatorFloorRequestType.ELEVATOR_LEAVING_FLOOR_MESSAGE);
	}
}
