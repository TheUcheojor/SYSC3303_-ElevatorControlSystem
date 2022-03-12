package tests.ElevatorSubsystem;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorCar;
import ElevatorSubsystem.ElevatorDoor;
import ElevatorSubsystem.ElevatorMotor;
import common.Direction;
import common.messages.elevator.ElevatorStatusMessage;

class TestElevatorCar {
	private ElevatorCar elevatorCar;
	private final static int CAR_ID = 2;
	private static final double MOTOR_SPEED = 0.5;
	private static final double DOOR_SPEED = 3000;

	@BeforeEach
	void setup() {
		ElevatorDoor elevatorDoor = new ElevatorDoor(DOOR_SPEED);
		ElevatorMotor elevatorMotor = new ElevatorMotor(MOTOR_SPEED);
		elevatorCar = new ElevatorCar(CAR_ID, elevatorMotor, elevatorDoor);
	}

	@AfterEach
	void tearDown() {
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
		assertEquals(statusMessage.getDirection(), Direction.UP);
		assertEquals(statusMessage.getElevatorId(), elevatorCar.getId());
	}
}
