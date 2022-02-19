package tests.FloorSubsystem;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorMotor;
import FloorSubsystem.FloorSubsystem;
import common.Direction;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.scheduler.FloorCommand;
import common.messages.scheduler.SchedulerFloorCommand;

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
	private Thread floorSubsystemThread;

	/**
	 * The floor subsystem
	 */
	private FloorSubsystem floorSubsystem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		floorSubsystemTransmissonChannel = new MessageChannel("Floor Subsystem Transmisson");
		floorSubsystemReceiverChannel = new MessageChannel("Floor Subsystem Receiver");
		elevatorSubsystemReceiverChannel = new MessageChannel("Elevator Subsystem Receiver");

		floorSubsystem = new FloorSubsystem(TEST_FILE, floorSubsystemTransmissonChannel, floorSubsystemReceiverChannel,
				elevatorSubsystemReceiverChannel);
		floorSubsystemThread = new Thread(floorSubsystem, "");
	}

	/**
	 * Test that the floor notifies the elevator arrives at the floor
	 */
	@Test
	void testFloorNotifiesElevatorAtFloorArrivalWithValidReponse() {

		ElevatorMotor elevatorMotor = new ElevatorMotor(3, 1.5);

		ElevatorFloorSignalRequestMessage floorSignalRequest = new ElevatorFloorSignalRequestMessage(0, 0,
				elevatorMotor, true);

		floorSubsystemReceiverChannel.appendMessage(floorSignalRequest);
		floorSubsystemThread.start();

		// Let the floor work. The notification may take well because of default
		// configurations such speed, acceleration. Wait until the elevator receives the
		// message
		Message floorSentResponseMessage = elevatorSubsystemReceiverChannel.popMessage();

		assertTrue(floorSentResponseMessage instanceof ElevatorFloorArrivalMessage);

		ElevatorFloorArrivalMessage floorResponse = (ElevatorFloorArrivalMessage) floorSentResponseMessage;
		assertTrue(floorResponse.getNewCurrentElevatorSpeed() == 0);

		// Verify that the correct floor sends the response
		System.out.println("getFloorId " + floorResponse.getFloorId());

		assertTrue(floorResponse.getFloorId() == 0);

		// Verify that the floor sends the response to the correct elevator
		System.out.println("getElevatorId " + floorResponse.getElevatorId());
		assertTrue(floorResponse.getElevatorId() == 0);
	}

	/**
	 * Test that when the elevator sends a leaving-floor request, the floor updates
	 * the arrival sensor
	 */
	@Test
	void testFloorUpdatesArrivalSensorAtFloorExit() {

		// Set the arrival sensor to true as the elevator is at floor zero
		floorSubsystem.getFloors()[0].getElevatorComponents()[0].setArrivalSensorState(true);

		// Send a message indicating that the elevator is leaving floor zero
		ElevatorLeavingFloorMessage elevatorLeavingFloorMessage = new ElevatorLeavingFloorMessage(0, 0);
		floorSubsystemReceiverChannel.appendMessage(elevatorLeavingFloorMessage);

		floorSubsystemThread.start();

		// Give floor subsystem thread time to work
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// Verify that the floor arrival sensor is off
		assertTrue(floorSubsystem.getFloors()[0].getElevatorComponents()[0].getArrivalSensorState() == false);
	}

	/**
	 * Test that when the scheduler sends a turn-off-floor-lamp request, the floor
	 * subsystem turns off the appropriate floor lamp
	 *
	 */
	@Test
	void testFloorTurnsOffSpecifiedFloorLampWhenRequested() {

		// Set the DOWN and UP floor lamp
		floorSubsystem.getFloors()[1].getUpLampButton().setButtonPressed(true);
		floorSubsystem.getFloors()[1].getDownLampButton().setButtonPressed(true);

		// Send a floor command to turn off the UP floor lamp
		SchedulerFloorCommand floorCommand = new SchedulerFloorCommand(FloorCommand.TURN_OFF_FLOOR_LAMP, 1,
				Direction.UP);
		floorSubsystemReceiverChannel.appendMessage(floorCommand);

		floorSubsystemThread.start();

		// Give floor subsystem thread time to work
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// Verify that the floor arrival sensor is off
		assertTrue(floorSubsystem.getFloors()[1].getUpLampButton().isButtonPressed() == false);
		assertTrue(floorSubsystem.getFloors()[1].getDownLampButton().isButtonPressed() == true);

	}

}
