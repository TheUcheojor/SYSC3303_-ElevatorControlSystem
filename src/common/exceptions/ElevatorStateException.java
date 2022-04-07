package common.exceptions;

import FloorSubsystem.FloorInputFault;

/**
 * An elevator state exception
 *
 * @author ryanfife, paulokenne
 *
 */
public class ElevatorStateException extends Exception {

	/**
	 * The floor input fault
	 */
	private FloorInputFault fault;

	/**
	 * The floor number
	 */
	private int floorNumber = -1;

	/**
	 * A ElevatorStateException constructor
	 *
	 * @param fault   the fault
	 * @param message the message
	 */
	public ElevatorStateException(FloorInputFault fault, String message) {
		super(message);
		this.fault = fault;
	}

	/**
	 * A ElevatorStateException constructor
	 *
	 * @param fault       the fault
	 * @param floorNumber the floor number
	 * @param message     the message
	 */
	public ElevatorStateException(FloorInputFault fault, int floorNumber, String message) {
		super(message);
		this.fault = fault;
		this.floorNumber = floorNumber;
	}

	/**
	 * @return the fault
	 */
	public synchronized FloorInputFault getFault() {
		return fault;
	}

	/**
	 * @return the floorNumber
	 */
	public synchronized int getFloorNumber() {
		return floorNumber;
	}

	@Override
	public String toString() {
		return ElevatorStateException.class.toString() + " : [" + fault + " near Floor " + floorNumber + "]: "
				+ this.getMessage();
	}
}
