/**
 *
 */
package common.messages.elevator;

import common.messages.FloorElevatorTargetedMessage;
import common.messages.MessageType;

/**
 * This class represents a message that is sent by the floor to the elevator to
 * indicate that the elevator has reached the floor
 *
 * @author paulokenne, Ryan Fife
 *
 */
public class ElevatorFloorArrivalMessage extends FloorElevatorTargetedMessage {
	

	/**
	 * A ElevatorFloorArrivalMessage constructor.
	 */
	public ElevatorFloorArrivalMessage(int elevatorId, int floorId) {
		super(elevatorId, floorId, MessageType.FLOOR_ARRIVAL_MESSAGE);
	}
}
