/**
 *
 */
package common.messages;

import common.messages.elevator.ElevatorFloorRequestType;

/**
 * This class represents for communication between the elevator and floor
 *
 * @author paulokenne
 *
 */
public abstract class FloorElevatorTargetedMessage extends Message {

	/**
	 * The floor id
	 */
	private int floorId;

	/**
	 * The elevator id.
	 */
	private int elevatorId;


	/**
	 * A FloorTargetedElevatorMessage constructor
	 *
	 * @param elevatorId  the elevator id
	 * @param floorId     the floor id
	 * @param messageType the message type
	 */
	public FloorElevatorTargetedMessage(int elevatorId, int floorId, MessageType requestType) {
		super(requestType);
		this.elevatorId = elevatorId;
		this.floorId = floorId;
	}

	/**
	 * Get the floor id.
	 *
	 * @return the floorId
	 */
	public int getFloorId() {
		return floorId;
	}

	/**
	 * Get the elevatorId.
	 *
	 * @return the elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}
}
