package tests.ElevatorSubsystem;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import common.messages.ElevatorStatusRequest;
import common.messages.ElevatorStatusResponse;
import common.messages.Message;
import common.messages.MessageChannel;

class TestElevatorCar {
	private ElevatorCar elevatorCar;
	private final static int CAR_ID = 2; 
	private MessageChannel messageChannel;
	
	@BeforeEach
	void setup() {
		messageChannel = new MessageChannel();
		elevatorCar = new ElevatorCar(CAR_ID, messageChannel);
	}
	
	@AfterEach
	void tearDown() {
		messageChannel = null;
		elevatorCar = null;
	}

	@Test
	void testCreateStatusMessage() {
		ElevatorMotor motor = elevatorCar.getMotor();
		ElevatorDoor door = elevatorCar.getDoor();
		door.closeDoor();
		motor.goUp();
		
		ElevatorStatusResponse statusMessage = elevatorCar.createStatusMessage();
		
		// elevator should format status message correctly
		assertTrue(statusMessage.inService);
		assertFalse(statusMessage.isDoorOpen);
		assertEquals(statusMessage.direction, Direction.UP);
	}
	
	@Test
	void testStatusRequestReceived() {	
		try {
			elevatorCar.handleMessage(new ElevatorStatusRequest(CAR_ID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		 Message message = messageChannel.getMessage();
		
		// elevator should place a status response in the channel
		assertEquals(message instanceof ElevatorStatusResponse, true);
	}
	
	@Test
	void testHandleWrongCardId() {
		// handleMessage should throw an exception if the elevator doesn't recognize the elevatorId
		assertThrows(Exception.class, () -> {
			elevatorCar.handleMessage(new ElevatorStatusRequest(CAR_ID+1));
			});
	}

}
