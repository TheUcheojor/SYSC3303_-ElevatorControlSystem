package ElevatorSubsystem;

import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.SchedulerElevatorCommand;


/**
 * Entity representing an elevator car, composed of subcomponents for
 * major functionality.
 *
 * @author Ryan Fife, paulokenne, Favour
 *
 */
public class ElevatorCar {
	/*
	 * elevator Id
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
	private Exception errorState;

	public ElevatorCar(int id, ElevatorMotor motor, ElevatorDoor door) {
		this.id = id;
		this.inService = true;
		this.motor = motor;
		this.door = door;
	}

	public int getId() {
		return this.id;
	}

	public ElevatorMotor getMotor() {
		return motor;
	}

	public ElevatorDoor getDoor() {
		return door;
	}

	public boolean getInService() {
		return inService;
	}
	
	public int getFloorNumber() {
		return floorNumber;
	}

	public void setInService(boolean service) {
		inService = service;
	}
	
	public void setErrorState(Exception errorState) {
		this.errorState = errorState;
	}

	/**
	 * Creates a ElevatorStatusMessage message containing all relevant status info
	 * for this elevator.
	 *
	 * @return status response message
	 */
	public ElevatorStatusMessage createStatusMessage() {
		ElevatorStatusMessage status = new ElevatorStatusMessage(
				id,
				this.getMotor().getDirection(),
				floorNumber,
				errorState
				);

		return status;
	}
}