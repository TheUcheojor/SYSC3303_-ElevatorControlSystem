/**
 *
 */
package common.messages.elevator;

import common.messages.IdentifierDrivenMessage;
import common.messages.MessageType;

/**
 * This class represents a message that the elevator sends to the floor to
 * notify it that it is leaving it.
 *
 * @author paulokenne
 *
 */
public class ElevatorLeavingFloorMessage extends IdentifierDrivenMessage {

	/**
	 * A ElevatorLeavingFloorMessage leaving constructor.
	 */
	public ElevatorLeavingFloorMessage(int elevatorId, int floorId) {
		super(elevatorId, floorId, MessageType.EVELATOR_LEAVING_FLOOR_MESSAGE);
	}
}
