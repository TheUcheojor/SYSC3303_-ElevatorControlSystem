package tests.ElevatorSubsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorCar;
import ElevatorSubsystem.ElevatorDoor;
import ElevatorSubsystem.ElevatorMotor;
import common.Direction;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorStatusRequest;

class TestElevatorCar {
	private ElevatorCar elevatorCar;
	private final static int CAR_ID = 2;
	private MessageChannel elevatorSubsystemTransmissonChannel;
	private MessageChannel elevatorSubsystemReceiverChannel;

	@BeforeEach
	void setup() {
		this.elevatorSubsystemTransmissonChannel = new MessageChannel("Elevator Subsystem Transmisson Channel");
		this.elevatorSubsystemReceiverChannel = new MessageChannel("Elevator Subsystem Receiver Channel");

		elevatorCar = new ElevatorCar(CAR_ID, elevatorSubsystemTransmissonChannel, elevatorSubsystemReceiverChannel);
	}

	@AfterEach
	void tearDown() {
		elevatorSubsystemTransmissonChannel = null;
		elevatorSubsystemReceiverChannel = null;

		elevatorCar = null;
	}

	@Test
	void testCreateStatusMessage() {
		ElevatorMotor motor = elevatorCar.getMotor();
		ElevatorDoor door = elevatorCar.getDoor();
		door.closeDoor();
		motor.goUp();

		ElevatorStatusMessage statusMessage = elevatorCar.createStatusMessage();

		// elevator should format status message correctly
		assertEquals(statusMessage.direction, Direction.UP);
	}

	@Test
	void testStatusRequestReceived() {
		try {
			elevatorCar.handleMessage(new ElevatorStatusRequest(CAR_ID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Message message = elevatorSubsystemTransmissonChannel.popMessage();

		// elevator should place a status response in the channel
		assertEquals(message instanceof ElevatorStatusMessage, true);
	}
}
