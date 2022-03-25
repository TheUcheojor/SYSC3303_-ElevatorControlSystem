package common.messages.floor;

import FloorSubsystem.FloorInputFault;
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
	FloorInputFault fault = null;
	int faultFloorNumber = -1;

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

	public ElevatorFloorRequest(int destinationFloor, Direction direction, int inputDataId, FloorInputFault fault, int faultFloorNumber) {
		super(MessageType.ELEVATOR_PICK_UP_PASSENGER_REQUEST, destinationFloor, direction);
		this.inputDataId = inputDataId;
		this.fault = fault;
		this.faultFloorNumber = faultFloorNumber;
	}
	
	/**
	 * Get the input data id
	 *
	 * @return the inputDataId
	 */
	public int getInputDataId() {
		return inputDataId;
	}

	/**
	 * @return the fault
	 */
	public synchronized FloorInputFault getFault() {
		return fault;
	}

	/**
	 * @return the faultFloorNumber
	 */
	public synchronized int getFaultFloorNumber() {
		return faultFloorNumber;
	}
	
	
}
