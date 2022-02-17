package ElevatorSubsystem;

import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorStatusMessage;

/**
 * Entity representing an elevator car, contains composite subcomponents for
 * major functionality.
 *
 * @author Ryan Fife, paulokenne
 *
 */
public class ElevatorCar implements Runnable {
	private int id;
	private boolean inService;
	private MessageChannel elevatorSubsystemTransmissonChannel;
	private MessageChannel elevatorSubsystemReceiverChannel;
	private ElevatorMotor motor;
	private ElevatorDoor door;
	private int floorNumber;

	/**
	 * The number of elevators in the system
	 *
	 * TODO to be relocated to a elevator subsystem level
	 */
	public final static int NUMBER_OF_ELEVATORS = 1;
	/**
	 * The door opening and closing time in seconds
	 */
	private final static double DOOR_SPEED = 3000;

	/**
	 * The elevator speed in meters per second.
	 */
	public final static double MAX_ELEVATOR_SPEED = 3;
	/**
	 * The elevator acceleration in meters per second squared.
	 */
	public final static double ELEVATOR_ACCELERATION = 1.5;

	public ElevatorCar(int id, MessageChannel elevatorSubsystemTransmissonChannel,
			MessageChannel elevatorSubsystemReceiverChannel) {
		// Validate that the elevator values are valid
		try {
			SystemValidationUtil.validateElevatorMaxSpeed(MAX_ELEVATOR_SPEED);
			SystemValidationUtil.validateElevatorAcceleration(ELEVATOR_ACCELERATION);
		} catch (InvalidSystemConfigurationInputException e) {
			System.out.println("InvalidSystemConfigurationInputException: " + e);
			// Terminate if the elevation configuration are invalid.
			System.exit(1);
		}

		this.id = id;
		this.elevatorSubsystemTransmissonChannel = elevatorSubsystemTransmissonChannel;
		this.elevatorSubsystemReceiverChannel = elevatorSubsystemReceiverChannel;
		this.inService = true;
		door = new ElevatorDoor(DOOR_SPEED);
		motor = new ElevatorMotor(MAX_ELEVATOR_SPEED, ELEVATOR_ACCELERATION);
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

	@Override
	public void run() {

		// The elevator is ready
		ElevatorStatusMessage status = createStatusMessage();
		elevatorSubsystemTransmissonChannel.appendMessage(status);

		while (true) {
			// send status message and wait for a response from scheduler response in loop
			Message message = elevatorSubsystemReceiverChannel.popMessage();
			handleMessage(message);
		}
	}

	/**
	 * Elevator message handler. Exterior entities can send various types of request
	 * or commands to the elevator.
	 *
	 * @param message to handle
	 * @throws Exception if the message doesn't belong to this elevator
	 */
	// @PublicForTestOnly
	public void handleMessage(Message message) {

		switch (message.getMessageType()) {

		case JOB_REQUEST:
			elevatorSubsystemTransmissonChannel.appendMessage(message);
			break;

		case ELEVATOR_STATUS_REQUEST:
			ElevatorStatusMessage status = createStatusMessage();
			elevatorSubsystemTransmissonChannel.appendMessage(status);
			break;

		default:
			break;

		}
	}

	/**
	 * Creates a ElevatorStatusMessage message containing all relevant status info
	 * for this elevator.
	 *
	 * @return status response message
	 */
	// @PublicForTestOnly
	public ElevatorStatusMessage createStatusMessage() {
		ElevatorStatusMessage status = new ElevatorStatusMessage(id, this.getMotor().getDirection(), floorNumber);
		status.direction = motor.getDirection();
		// TODO (rfife): Pass current floor

		return status;
	}
}