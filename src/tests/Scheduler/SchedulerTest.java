/**
 *
 */
package tests.Scheduler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Scheduler.Scheduler;
import common.Direction;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.MessageType;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.floor.ElevatorFloorRequest;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.FloorCommand;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.messages.scheduler.SchedulerFloorCommand;

/**
 * Tests the scheduler based on iteration 1 requirements.
 *
 * @author paulokenne
 *
 */
class SchedulerTest {

	/**
	 * The test scheduler name.
	 */
	private final String SCHEDULER_NAME = "Test Scheduler";

	/**
	 * A sample floor input data.
	 */
	private final String SAMPLE_FLOOR_INPUT_DATA = "14:05:15.0 2 UP 4";

	/**
	 * The floor subsystem transmission channel.
	 */
	private MessageChannel floorSubsystemTransmissonChannel;

	/**
	 * The floor subsystem receiver channel.
	 */
	private MessageChannel floorSubsystemReceiverChannel;

	/**
	 * The elevator subsystem transmission channel.
	 */
	private MessageChannel elevatorSubsystemTransmissonChannel;

	/**
	 * The elevator subsystem receiver channel.
	 */
	private MessageChannel elevatorSubsystemReceiverChannel;
	/**
	 * The scheduler.
	 */
	private Thread scheduler;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.floorSubsystemTransmissonChannel = new MessageChannel("Floor Subsystem Transmisson", 2);
		this.floorSubsystemReceiverChannel = new MessageChannel(" Floor Subsystem Receiever", 2);

		this.elevatorSubsystemTransmissonChannel = new MessageChannel(" Elevator Subsystem Transmisson", 2);
		this.elevatorSubsystemReceiverChannel = new MessageChannel(" Elevator Subsystem Receiever", 2);

		scheduler = new Thread(new Scheduler(floorSubsystemTransmissonChannel, floorSubsystemReceiverChannel,
				elevatorSubsystemTransmissonChannel, elevatorSubsystemReceiverChannel), SCHEDULER_NAME);
	}

	@AfterEach
	void tearDown() {
		floorSubsystemTransmissonChannel = null;
		floorSubsystemReceiverChannel = null;
		elevatorSubsystemTransmissonChannel = null;
		elevatorSubsystemReceiverChannel = null;
	}

	/**
	 * Test that requests received from both channels are received.
	 */
	@Test
	void testFloorSubsystemRequestIsAccepted() {
		ElevatorFloorRequest request = new ElevatorFloorRequest(2, Direction.UP);
		floorSubsystemTransmissonChannel.appendMessage(request);

		scheduler.start();

		// Give the scheduler time to get the request.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(floorSubsystemTransmissonChannel.isEmpty());
	}

	/**
	 * Test that the request received from the elevator subsystem is accepted.
	 */
	@Test
	void testElevatorSubsystemRequestIsAccepted() {
		Message message = new Message(MessageType.ELEVATOR_STATUS_REQUEST);
		elevatorSubsystemTransmissonChannel.appendMessage(message);

		scheduler.start();

		// Give the scheduler time to get the request.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// The elevator ELEVATOR_STATUS_REQUEST should have been taken by the
		// scheduler
		assertTrue(elevatorSubsystemTransmissonChannel.isEmpty());
	}

	@Test
	void testSchedulerIssuesMoveUpToFloorCommandsToElevator() {
		int floorDest = 3;
		Direction directionRequested = Direction.UP;
		ElevatorFloorRequest floorRequest = new ElevatorFloorRequest(floorDest, directionRequested);

		int elevatorId = 1;
		int currFloor = 1;
		Direction currDirection = Direction.IDLE;

		ElevatorStatusMessage elevatorStatus = new ElevatorStatusMessage(elevatorId, currDirection, currFloor);

		floorSubsystemTransmissonChannel.appendMessage(floorRequest);
		elevatorSubsystemTransmissonChannel.appendMessage(elevatorStatus);

		scheduler.start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SchedulerElevatorCommand message1 = (SchedulerElevatorCommand) elevatorSubsystemReceiverChannel.popMessage();
		SchedulerElevatorCommand message2 = (SchedulerElevatorCommand) elevatorSubsystemReceiverChannel.popMessage();

		System.out.println(message1.getCommand() + " " + message2.getCommand());
		assertTrue(message1.getCommand() == ElevatorCommand.CLOSE_DOORS);
		assertTrue(message2.getCommand() == ElevatorCommand.MOVE_UP);
	}

	@Test
	void testSchedulerIssuesStopAtFloorCommandsToElevator() {
		int floorDest = 1;
		Direction directionRequested = Direction.UP;
		ElevatorFloorRequest floorRequest = new ElevatorFloorRequest(floorDest, directionRequested);

		int elevatorId = 1;
		int currFloor = 1;
		Direction currDirection = Direction.DOWN;

		ElevatorStatusMessage elevatorStatus = new ElevatorStatusMessage(elevatorId, currDirection, currFloor);

		floorSubsystemTransmissonChannel.appendMessage(floorRequest);
		elevatorSubsystemTransmissonChannel.appendMessage(elevatorStatus);

		scheduler.start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SchedulerElevatorCommand message1 = (SchedulerElevatorCommand) elevatorSubsystemReceiverChannel.popMessage();
		SchedulerElevatorCommand message2 = (SchedulerElevatorCommand) elevatorSubsystemReceiverChannel.popMessage();

		boolean floorChannelIsEmpty = floorSubsystemReceiverChannel.isEmpty();

		assertTrue(message1.getCommand() == ElevatorCommand.STOP);
		assertTrue(message2.getCommand() == ElevatorCommand.OPEN_DOORS);
		assertTrue(floorChannelIsEmpty);
	}

	@Test
	void testSchedulerIssuesTurnOffDirectionLampToFloor() {
		Direction direction = Direction.UP;
		int floor = 1;
		ElevatorFloorRequest floorRequest = new ElevatorFloorRequest(floor, direction);

		int elevatorId = 1;
		ElevatorStatusMessage elevatorStatus = new ElevatorStatusMessage(elevatorId, direction, floor);

		floorSubsystemTransmissonChannel.appendMessage(floorRequest);
		elevatorSubsystemTransmissonChannel.appendMessage(elevatorStatus);

		scheduler.start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SchedulerElevatorCommand message1 = (SchedulerElevatorCommand) elevatorSubsystemReceiverChannel.popMessage();
		SchedulerElevatorCommand message2 = (SchedulerElevatorCommand) elevatorSubsystemReceiverChannel.popMessage();
		SchedulerFloorCommand message3 = (SchedulerFloorCommand) floorSubsystemReceiverChannel.popMessage();

		assertTrue(message1.getCommand() == ElevatorCommand.STOP);
		assertTrue(message2.getCommand() == ElevatorCommand.OPEN_DOORS);
		assertTrue(message3.getCommand() == FloorCommand.TURN_OFF_FLOOR_LAMP);
		assertTrue(message3.getDirection() == direction);
		assertTrue(message3.getFloorId() == floor);
	}
}
