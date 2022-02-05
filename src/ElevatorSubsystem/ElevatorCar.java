package ElevatorSubsystem;

import common.messages.ElevatorStatusMessage;
import common.messages.Message;
import common.messages.MessageChannel;

/**
 * Entity representing an elevator car, contains composite subcomponents for
 * major functionality.
 *
 * @author Ryan Fife
 *
 */
public class ElevatorCar implements Runnable {
	private int id;
	private boolean inService;
	private MessageChannel elevatorSubsystemTransmissonChannel;
	private MessageChannel elevatorSubsystemReceiverChannel;
	private ElevatorMotor motor;
	private ElevatorDoor door;

	private final static double DOOR_SPEED = 3000;
	private final static double ELEVATOR_SPEED = 0.5;

	public ElevatorCar(int id, MessageChannel elevatorSubsystemTransmissonChannel,
			MessageChannel elevatorSubsystemReceiverChannel) {
		this.id = id;
		this.elevatorSubsystemTransmissonChannel = elevatorSubsystemTransmissonChannel;
		this.elevatorSubsystemReceiverChannel = elevatorSubsystemReceiverChannel;
		this.inService = true;
		door = new ElevatorDoor(DOOR_SPEED);
		motor = new ElevatorMotor(ELEVATOR_SPEED);
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
		elevatorSubsystemTransmissonChannel.setMessage(status);

		while (true) {
			// send status message and wait for a response from scheduler response in loop
			Message message = elevatorSubsystemReceiverChannel.getMessage();
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
			elevatorSubsystemTransmissonChannel.setMessage(message);
			break;

		case ELEVATOR_STATUS_REQUEST:
			ElevatorStatusMessage status = createStatusMessage();
			elevatorSubsystemTransmissonChannel.setMessage(status);
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
		ElevatorStatusMessage status = new ElevatorStatusMessage(this.getInService(), id);
		status.direction = motor.getDirection();
		status.isDoorOpen = door.isOpen();
		// TODO (rfife): Pass current floor

		return status;
	}
}