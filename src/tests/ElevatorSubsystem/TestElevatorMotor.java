package tests.ElevatorSubsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorMotor;
import common.Direction;

class TestElevatorMotor {
	private ElevatorMotor elevatorMotor;
	private static final double MOTOR_SPEED = 0.5;
	
	@BeforeEach
	void setup() {
		this.elevatorMotor = new ElevatorMotor(MOTOR_SPEED);
	}
	
	@AfterEach
	void tearDown() {
		this.elevatorMotor = null;
	}
	
	@Test
	void testGoUp() {
		elevatorMotor.goUp();
		assertTrue(elevatorMotor.getIsRunning());
		assertEquals(elevatorMotor.getDirection(), Direction.UP);
	}
	
	@Test
	void testGoDown() {
		elevatorMotor.goDown();
		assertTrue(elevatorMotor.getIsRunning());
		assertEquals(elevatorMotor.getDirection(), Direction.DOWN);
	}
	
	@Test
	void testTurnOff() {
		elevatorMotor.turnOff();
		assertFalse(elevatorMotor.getIsRunning());
		assertEquals(elevatorMotor.getDirection(), Direction.IDLE);
	}

}
