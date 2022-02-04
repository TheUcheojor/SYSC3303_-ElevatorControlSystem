package tests.ElevatorSubsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorDoor;

class TestElevatorDoor {
	private ElevatorDoor elevatorDoor;
	private static final double DOOR_SPEED = 3000;
	
	@BeforeEach
	void setup() {
		elevatorDoor = new ElevatorDoor(DOOR_SPEED);
	}
	
	@AfterEach
	void tearDown() {
		elevatorDoor = null;
	}
	
	@Test
	void testOpenDoor() {
		elevatorDoor.openDoor();
		assertEquals(elevatorDoor.isOpen(), true);
	}
	
	@Test
	void testCloseDoor() {
		elevatorDoor.closeDoor();
		assertEquals(elevatorDoor.isOpen(), false);
	}

}
