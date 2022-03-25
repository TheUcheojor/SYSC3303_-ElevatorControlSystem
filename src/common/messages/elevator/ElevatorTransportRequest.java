package common.messages.elevator;

import java.util.Objects;

import ElevatorSubsystem.ElevatorAutoFixing;
import FloorSubsystem.FloorInputFault;
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
	 * A floor fault
	 */
	private FloorInputFault floorFault;

	/**
	 * The elevator auto fixing
	 */
	private ElevatorAutoFixing autoFixing;

	/**
	 * A ElevatorTransportRequest constructor
	 *
	 * @param destinationFloor the destination floor
	 * @param elevatorId       the elevator id
	 * @param direction        the direction
	 * @param floorFault       the floor fault
	 * @param auto             fixing the auto fixing mode
	 */
	public ElevatorTransportRequest(int destinationFloor, int elevatorId, Direction direction,
			FloorInputFault floorFault, ElevatorAutoFixing autoFixing) {
		super(MessageType.ELEVATOR_DROP_PASSENGER_REQUEST, destinationFloor, direction);
		this.elevatorId = elevatorId;
		this.floorFault = floorFault;
		this.autoFixing = autoFixing;
	}

	/**
	 * Get the elevator id.
	 *
	 * @return the elevator id
	 */
	public int getElevatorId() {
		return elevatorId;
	}

	/**
	 * @return the floorFault
	 */
	public FloorInputFault getFloorFault() {
		return floorFault;
	}

	/**
	 * @return the autoFixing
	 */
	public ElevatorAutoFixing getAutoFixing() {
		return autoFixing;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(elevatorId);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElevatorTransportRequest other = (ElevatorTransportRequest) obj;
		return elevatorId == other.elevatorId;
	}
}
