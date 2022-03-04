package tests.ElevatorSubsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorCar;
import ElevatorSubsystem.ElevatorController;
import ElevatorSubsystem.ElevatorDoor;
import ElevatorSubsystem.ElevatorMotor;
import common.Direction;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorStatusRequest;

/**
 * This is the test class for the elevatorcontroller class
 * @author favour
 *
 */
class TestElevatorController {

	private ElevatorController elevatorController;
	private final static int CAR_ID = 2;
	private MessageChannel outgoingSchedulerChannel;
	private MessageChannel outgoingFloorChannel;
	private MessageChannel incomingChannel;

	@BeforeEach
	void setup() {
		this.outgoingSchedulerChannel = new MessageChannel("Elevator Subsystem Transmisson Channel");
		this.incomingChannel = new MessageChannel("Elevator Subsystem Receiver Channel");

		elevatorController = new ElevatorController(outgoingSchedulerChannel, incomingChannel, outgoingFloorChannel);
	}

	@AfterEach
	void tearDown() {
		outgoingSchedulerChannel = null;
		incomingChannel = null;

		elevatorController = null;
	}
	
	@Test
	void testElevatorCarsSetup() {
		ElevatorCar elevatorCar = elevatorController.getElevators().get(CAR_ID);
		assertTrue(elevatorCar.getDoor().getDoorSpeed() == ElevatorController.DOOR_SPEED);
		assertTrue(elevatorCar.getMotor().getAcceleration() == ElevatorController.ELEVATOR_ACCELERATION);
		assertTrue(elevatorCar.getMotor().getTopSpeed() == ElevatorController.MAX_ELEVATOR_SPEED);
	}

	@Test
	void testCreateStatusMessage() {
		ElevatorCar elevatorCar = elevatorController.getElevators().get(CAR_ID); 
		elevatorCar.getMotor().goUp();;
		elevatorCar.getDoor().closeDoor();

		ElevatorStatusMessage statusMessage = elevatorCar.createStatusMessage();

		// elevator should format status message correctly
		assertEquals(statusMessage.getDirection(), Direction.UP);
	}

	@Test
	void testStatusRequestReceived() {
		try {
			elevatorController.handleMessage(new ElevatorStatusRequest(CAR_ID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Message message = outgoingSchedulerChannel.popMessage();

		// elevator should place a status response in the channel
		assertEquals(message instanceof ElevatorStatusMessage, true);
	}

}
