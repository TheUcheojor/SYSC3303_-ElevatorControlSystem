package common.exceptions;

import FloorSubsystem.FloorInputFault;

public class ElevatorStateException extends Exception {

	private FloorInputFault fault;
	private int floorNumber = -1;
	//
	
	public ElevatorStateException(FloorInputFault fault, String message) {
		super(message);
		this.fault = fault;
	}
	
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

	public String toString() {
		return ElevatorStateException.class.toString() +" : [" + fault +" near Floor " + floorNumber + "]: " + this.getMessage();
	}
}
