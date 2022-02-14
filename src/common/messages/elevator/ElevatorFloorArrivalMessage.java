/**
 *
 */
package common.messages.elevator;

import common.messages.IdentifierDrivenMessage;
import common.messages.MessageType;

/**
 * This class represents a message that is sent by the floor to the elevator to
 * indicate that the elevator has reached the floor
 *
 * @author paulokenne
 *
 */
public class ElevatorFloorArrivalMessage extends IdentifierDrivenMessage {

	/**
	 * The current elevator speed.
	 */
	private double newCurrentElevatorSpeed;

	/**
	 * A ElevatorFloorArrivalMessage constructor.
	 */
	public ElevatorFloorArrivalMessage(int floorId, int elevatorId, double newCurrentElevatorSpeed) {
		super(floorId, elevatorId, MessageType.FLOOR_ARRIVAL_MESSAGE);
		this.newCurrentElevatorSpeed = newCurrentElevatorSpeed;
	}

	/**
	 * @return the newCurrentElevatorSpeed
	 */
	public double getNewCurrentElevatorSpeed() {
		return newCurrentElevatorSpeed;
	}
}
