package ElevatorSubsystem;

import common.messages.ElevatorStatusResponse;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.MessageType;

/**
 * Entity representing the elevator car
 * 
 * @author Ryan Fife
 *
 */
public class ElevatorCar implements Runnable {
	private int id;
	private boolean inService;
	private MessageChannel messageChannel;
	private ElevatorMotor motor;
	private ElevatorDoor door;
	
	private final static double DOOR_SPEED = 3000;
	private final static double ELEVATOR_SPEED = 0.5;
	
	public ElevatorCar(int id, MessageChannel messageChannel) {
		this.id = id;
		this.messageChannel = messageChannel;
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

	public void run() {
		while(true) {
			Message message = messageChannel.getMessage();
			handleMessage(message);
		}
	}
	
	
	/**
	 * Elevator message handler. Exterior entities can send various types of request or commands to the elevator.
	 * 
	 * @param message
	 */
	//@PublicForTestOnly
	public void handleMessage(Message message) {

		switch (message.getMessageType()) {

		case ELEVATOR_STATUS_REQUEST:
			ElevatorStatusResponse status = createStatusMessage();
			messageChannel.setMessage(status);
			break;

		default:
			break;

		}
	}
	
	/**
	 * Creates a ElevatorStatusResponse message containing all relevant status info
	 * for this elevator.
	 * 
	 * @return status response message
	 */
	//@PublicForTestOnly
	public ElevatorStatusResponse createStatusMessage() {
		ElevatorStatusResponse status = new ElevatorStatusResponse(this.getInService());
		status.direction = motor.getDirection();
		status.isDoorOpen = door.isOpen();
		// TODO (rfife): Pass current floor
		
		return status;
	}
}