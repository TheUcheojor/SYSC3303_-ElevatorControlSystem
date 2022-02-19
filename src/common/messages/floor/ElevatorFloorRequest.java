package common.messages.floor;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.MessageType;

/**
 * This class represents when a passenger requests for an elevator at a floor
 *
 * @author ryanfire, paulokenne
 *
 */
public class ElevatorFloorRequest extends ElevatorJobMessage {

	/**
	 * A ElevatorFloorRequest constructor
	 *
	 * @param destinationFloor the destination floor
	 * @param direction        the direction
	 */
	public ElevatorFloorRequest(int destinationFloor, Direction direction) {
		super(MessageType.ELEVATOR_FLOOR_REQUEST, destinationFloor, direction);
	}
}
