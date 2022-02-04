package ElevatorSubsystem;

import common.messages.ElevatorStatusMessage;
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
	private MessageChannel messageChannel;
	private boolean inService;
	private ElevatorMotor elevatorMotor;
	private ElevatorDoor elevatorDoor;
	
	private final static double DOOR_SPEED = 3000;
	private final static double ELEVATOR_SPEED = 0.5;
	
	ElevatorCar(int id, MessageChannel messageChannel) {
		this.id = id;
		this.messageChannel = messageChannel;
		this.inService = true;
		elevatorDoor = new ElevatorDoor(DOOR_SPEED);
		elevatorMotor = new ElevatorMotor(ELEVATOR_SPEED);
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean getInService() {
		return inService;
	}
	
	public void setInService(boolean service) {
		inService = service;
	}
	
	private ElevatorStatusMessage createStatusMessage() {
		ElevatorStatusMessage status = new ElevatorStatusMessage(this.getInService());
		status.direction = elevatorMotor.getDirection();
		status.floorNumber = 1337; // TODO: fix this
		status.isDoorOpen = elevatorDoor.isOpen();
		
		return status;
	}

	public void run() {
		while(true) {
			Message message = messageChannel.getMessage();
			handleMessage(message);
		}
	}
	
	private void handleMessage(Message message) {

		switch (message.getMessageType()) {

		case ELEVATOR_STATUS_REQUEST:
			ElevatorStatusMessage status = createStatusMessage();
			messageChannel.setMessage(message);
			break;

		default:
			break;

		}
	}
}