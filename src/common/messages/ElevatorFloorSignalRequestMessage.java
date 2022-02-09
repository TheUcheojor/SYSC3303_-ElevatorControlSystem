/**
 *
 */
package common.messages;

import ElevatorSubsystem.ElevatorMotor;

/**
 * This class represents a request that sent to the floor that indicates the
 * elevator is coming in its direction
 *
 * @author paulokenne
 *
 */
public class ElevatorFloorSignalRequestMessage extends Message {

	/**
	 * A flag indicating whether the floor is the final destination
	 */
	private boolean isFloorFinalDestination;

	/**
	 * The elevator motor
	 */
	private ElevatorMotor elevatorMotor;

	/**
	 * A ElevatorFloorSignalRequestMessage constructor
	 */
	public ElevatorFloorSignalRequestMessage(boolean isFloorFinalDestination, ElevatorMotor elevatorMotor) {
		super(MessageType.EVELATOR_FLOOR_SIGNAL_REQUEST);
		this.isFloorFinalDestination = isFloorFinalDestination;
		this.elevatorMotor = elevatorMotor;
	}

	/**
	 * @return the isFloorFinalDestination flag
	 */
	public boolean isFloorFinalDestination() {
		return isFloorFinalDestination;
	}

	/**
	 * @return the elevatorMotor
	 */
	public ElevatorMotor getElevatorMotor() {
		return elevatorMotor;
	}
}
