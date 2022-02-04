package tests.ElevatorSubsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorCar;
import common.messages.MessageChannel;

class TestElevatorCar {
	private ElevatorCar elevatorCar;
	private final static int CAR_ID = 2; 
	
	@BeforeEach
	void setup() {
		elevatorCar = new ElevatorCar(CAR_ID, new MessageChannel());
	}
	
	@AfterEach
	void tearDown() {
		elevatorCar = null;
	}

	@Test
	void testHandleMessage() {
		
		elevatorCar.handleMessage();
	}

}
