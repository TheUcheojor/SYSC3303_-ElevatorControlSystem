package ElevatorSubsystem;

import common.exceptions.ElevatorStateException;
import common.messages.elevator.ElevatorStatusMessage;

/**
 * Entity representing an elevator car, composed of subcomponents for major
 * functionality.
 *
 * @author Ryan Fife, paulokenne, Favour Olotu
 *
 */
public class ElevatorCar {
	/*
	 * The elevator Id
	 */
	private int id;

	/*
	 * flag for if the elevator is in service (able to take jobs) or not
	 */
	private boolean inService;

	/*
	 * elevator motor
	 */
	private ElevatorMotor motor;

	/*
	 * elevator door
	 */
	private ElevatorDoor door;

	/*
	 * elevators current floor
	 */
	private int floorNumber = 0;

	/*
	 * if the elevator is in an error state, this defines it
	 */
	private ElevatorStateException errorState;

	/**
	 * A enum indicating whether the elevator car can auto fix transient errors
	 */
	private ElevatorAutoFixing autoFixing = ElevatorAutoFixing.AUTO_FIXING_SUCCESS;

	/**
	 * A flag indicating that the elevator care is resolving an error
	 */
	private boolean isResolvingError = false;

	/**
	 * The elevator car
	 *
	 * @param id    the car id
	 * @param motor the elevator id
	 * @param door  the elevator door
	 */
	public ElevatorCar(int id, ElevatorMotor motor, ElevatorDoor door) {
		this.id = id;
		this.inService = true;
		this.motor = motor;
		this.door = door;
	}

	/**
	 * Creates a ElevatorStatusMessage message containing all relevant status info
	 * for this elevator.
	 *
	 * @return status response message
	 */
	public ElevatorStatusMessage createStatusMessage() {
		ElevatorStatusMessage status = new ElevatorStatusMessage(id, this.getMotor().getDirection(), floorNumber,
				errorState, isResolvingError, door.isOpen());

		return status;
	}

	/**
	 * Creates a ElevatorStatusMessage message containing all relevant status info
	 * for this elevator.
	 *
	 * @return status response message
	 */
	public ElevatorStatusMessage createCommandNonIssuingStatusMessage() {
		ElevatorStatusMessage status = new ElevatorStatusMessage(id, this.getMotor().getDirection(), floorNumber,
				errorState, isResolvingError, false, door.isOpen());

		return status;
	}

	/**
	 * Get the elevator id
	 *
	 * @return the elevator id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return the autoFixingEnabled
	 */
	public ElevatorAutoFixing getAutoFixing() {
		return autoFixing;
	}

	/**
	 * @param autoFixing the autoFixing to set
	 */
	public void setAutoFixing(ElevatorAutoFixing autoFixing) {
		this.autoFixing = autoFixing;
	}

	/**
	 * Get the elevator motor
	 *
	 * @return the motor
	 */
	public ElevatorMotor getMotor() {
		return motor;
	}

	/**
	 * Get the door
	 *
	 * @return the door
	 */
	public ElevatorDoor getDoor() {
		return door;
	}

	/**
	 * Get the in service flag
	 *
	 * @return the in service flag
	 */
	public boolean getInService() {
		return inService;
	}

	/**
	 * Get the floor number
	 *
	 * @return the floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Set the in service flag
	 *
	 * @param service the in service flag
	 */
	public void setInService(boolean service) {
		inService = service;
	}

	/**
	 * Set the error state
	 *
	 * @param exception the exception
	 */
	public void setErrorState(ElevatorStateException exception) {
		this.errorState = exception;
	}

	/**
	 * Get the error state
	 *
	 * @return the error state
	 */
	public ElevatorStateException getErrorState() {
		return errorState;
	}

	/**
	 * Set the floor number
	 *
	 * @param floorNumber the floor number
	 */
	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	/**
	 * Return a flag indicating whether the car is currently resolving an error
	 *
	 * @return the isResolvingError
	 */
	public boolean isResolvingError() {
		return isResolvingError;
	}

	/**
	 * Set the resolving error Flag
	 *
	 * @param isResolvingError the isResolvingError to set
	 */
	public void setResolvingError(boolean isResolvingError) {
		this.isResolvingError = isResolvingError;
	}

}