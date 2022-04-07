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

	FloorInputFault fault = null;
	int faultFloorNumber = -1;

	/**
	 * A ElevatorFloorRequest constructor
	 *
	 * @param destinationFloor the destination floor
	 * @param direction        the direction
	 * @param floorInputId      the input input data id
	 */
	public ElevatorFloorRequest(int destinationFloor, Direction direction, int floorInputId) {
		super(MessageType.ELEVATOR_PICK_UP_PASSENGER_REQUEST, destinationFloor, direction, floorInputId);
	}

	public ElevatorFloorRequest(int destinationFloor, Direction direction, int floorInputId, FloorInputFault fault, int faultFloorNumber) {
		super(MessageType.ELEVATOR_PICK_UP_PASSENGER_REQUEST, destinationFloor, direction, floorInputId);
		this.fault = fault;
		this.faultFloorNumber = faultFloorNumber;
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
