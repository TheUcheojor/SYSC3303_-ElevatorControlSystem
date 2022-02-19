package common.messages.elevator;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.MessageType;

/**
 * This class represents when an elevator is requested to move a passenger to a
 * destination floor
 *
 * @author ryanfife, paulokenne
 *
 */
public class ElevatorTransportRequest extends ElevatorJobMessage {

	/**
	 * The elevator id.
	 */
	private int elevatorId;

	/**
	 * A ElevatorTransportRequest constructor
	 *
	 * @param destinationFloor the destination floor
	 * @param elevatorId       the elevator id
	 * @param direction        the direction
	 */
	public ElevatorTransportRequest(int destinationFloor, int elevatorId, Direction direction) {
		super(MessageType.ELEVATOR_TRANSPORT_REQUEST, destinationFloor, direction);
		this.elevatorId = elevatorId;
	}

	/**
	 * Get the elevator id.
	 *
	 * @return the elevator id
	 */
	public int getElevatorId() {
		return elevatorId;
	}
}
