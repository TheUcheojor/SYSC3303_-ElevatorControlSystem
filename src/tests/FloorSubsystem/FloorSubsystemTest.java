package tests.FloorSubsystem;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorMotor;
import FloorSubsystem.FloorSubsystem;
import Scheduler.Scheduler;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;

/**
 * Tests the floorSubsystem based on iteration 1 requirements.
 *
 * @author Favour, paulokenne
 *
 */
class FloorSubsystemTest {

	String TEST_FILE = "resources/TestInputFile.txt";

	/**
	 * The floor subsystem transmission message channel.
	 */
	private MessageChannel floorSubsystemTransmissonChannel;

	/**
	 * The floor subsystem receiver message channel.
	 */
	private MessageChannel floorSubsystemReceiverChannel;

	/**
	 * The elevator subsystem receiver message channel.
	 */
	private MessageChannel elevatorSubsystemReceiverChannel;

	/**
	 * The scheduler and floor subsystem threads.
	 */
	private Thread scheduler, floorSubsystem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		floorSubsystemTransmissonChannel = new MessageChannel("Floor Subsystem Transmisson");
		floorSubsystemReceiverChannel = new MessageChannel("Floor Subsystem Receiver");
		elevatorSubsystemReceiverChannel = new MessageChannel("Elevator Subsystem Receiver");

		floorSubsystem = new Thread(new FloorSubsystem(TEST_FILE, floorSubsystemTransmissonChannel,
				floorSubsystemReceiverChannel, elevatorSubsystemReceiverChannel), "");

		scheduler = new Thread(new Scheduler(floorSubsystemTransmissonChannel, floorSubsystemTransmissonChannel,
				new MessageChannel(""), new MessageChannel("")), "Scheduler");
	}

	/**
	 * This tests that the floor subsystem can communicate with the scheduler.
	 */
	@Test
	void testFloorSubsystemCommunication() {
		floorSubsystem.start();
		scheduler.start();
		assertTrue(floorSubsystemTransmissonChannel.isEmpty());
	}

	/**
	 * Test that the floor notifies the elevator arrives at the floor
	 */
	@Test
	void testFloorNotifiesElevatorAtFloorArrivalWithValidReponse() {

		ElevatorMotor elevatorMotor = new ElevatorMotor(3, 1.5);

		ElevatorFloorSignalRequestMessage floorSignalRequest = new ElevatorFloorSignalRequestMessage(0, 1,
				elevatorMotor, true);

		floorSubsystemReceiverChannel.setMessage(floorSignalRequest);
		floorSubsystem.start();

		// Let the floor work. The notification may take well because of default
		// configurations such speed, acceleration. Wait until the elevator receives the
		// message
		Message floorSentResponseMessage = elevatorSubsystemReceiverChannel.getMessage();

		assertTrue(floorSentResponseMessage instanceof ElevatorFloorArrivalMessage);

		ElevatorFloorArrivalMessage floorResponse = (ElevatorFloorArrivalMessage) floorSentResponseMessage;
		assertTrue(floorResponse.getNewCurrentElevatorSpeed() == 0);

		// Verify that the correct floor sends the response
		assertTrue(floorResponse.getSourceEntityId() == 1);

		// Verify that the floor sends the response to the correct elevator
		assertTrue(floorResponse.getTargetEntityId() == 0);

	}

}
