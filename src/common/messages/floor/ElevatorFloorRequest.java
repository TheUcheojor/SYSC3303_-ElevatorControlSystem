package common.messages.floor;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.MessageType;

/**
 * This class represents when a passenger requests for an elevator at a floor
 *
 * @author ryanfife, paulokenne
 *
 */
public class ElevatorFloorRequest extends ElevatorJobMessage {

	/**
	 * The input data id
	 */
	int inputDataId;

	/**
	 * A ElevatorFloorRequest constructor
	 *
	 * @param destinationFloor the destination floor
	 * @param direction        the direction
	 * @param inputDataId      the input input data id
	 */
	public ElevatorFloorRequest(int destinationFloor, Direction direction, int inputDataId) {
		super(MessageType.ELEVATOR_PICK_UP_PASSENGER_REQUEST, destinationFloor, direction);
		this.inputDataId = inputDataId;
	}

	/**
	 * Get the input data id
	 *
	 * @return the inputDataId
	 */
	public int getInputDataId() {
		return inputDataId;
	}
}
