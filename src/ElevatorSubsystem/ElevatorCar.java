package ElevatorSubsystem;

import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.SchedulerElevatorCommand;


/**
 * Entity representing an elevator car, contains composite subcomponents for
 * major functionality.
 *
 * @author Ryan Fife, paulokenne, Favour
 *
 */
public class ElevatorCar {
	private int id;
	private boolean inService;
	private ElevatorMotor motor;
	private ElevatorDoor door;
	private int floorNumber = 0;
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