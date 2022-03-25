package common.messages.floor;

import FloorSubsystem.FloorInputFault;
import common.Direction;
import common.messages.Message;
import common.messages.MessageType;

public class ElevatorNotArrived extends Message {
	private int floorNumber;
	private int elevatorId;
	

	/**
	 * A ElevatorFloorRequest constructor
	 *
	 * @param destinationFloor the destination floor
	 * @param direction        the direction
	 * @param inputDataId      the input input data id
	 */
	public ElevatorNotArrived(int floorNumber, int elevatorId) {
		super(MessageType.STUCK_AT_FLOOR_FAULT);
		this.elevatorId = elevatorId;
		this.floorNumber = floorNumber;
	}

	/**
	 * @return the floorNumber
	 */
	public synchronized int getFloorNumber() {
		return floorNumber;
	}


	/**
	 * @return the elevatorId
	 */
	public synchronized int getElevatorId() {
		return elevatorId;
	}	
}
