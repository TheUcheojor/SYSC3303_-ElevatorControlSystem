/**
 *
 */
package tests.Scheduler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Scheduler.Scheduler;
import common.SimulationFloorInputData;
import common.messages.JobRequest;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.MessageType;

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
		this.floorSubsystemTransmissonChannel = new MessageChannel("Floor Subsystem Transmisson");
		this.floorSubsystemReceiverChannel = new MessageChannel(" Floor Subsystem Receiever");

		this.elevatorSubsystemTransmissonChannel = new MessageChannel(" Elevator Subsystem Transmisson");
		this.elevatorSubsystemReceiverChannel = new MessageChannel(" Elevator Subsystem Receiever");

		scheduler = new Thread(new Scheduler(floorSubsystemTransmissonChannel, floorSubsystemReceiverChannel,
				elevatorSubsystemTransmissonChannel, elevatorSubsystemReceiverChannel), SCHEDULER_NAME);
	}

	/**
	 * Test that the request received from the floor subsystem is accepted.
	 */
	@Test
	void testFloorSubsystemRequestIsAccepted() {
		SimulationFloorInputData data = new SimulationFloorInputData(SAMPLE_FLOOR_INPUT_DATA);
		JobRequest jobRequest = new JobRequest(data);
		floorSubsystemTransmissonChannel.setMessage(jobRequest);

		Message message = new Message(MessageType.TEST_REQUEST);
		elevatorSubsystemTransmissonChannel.setMessage(message);

		scheduler.start();

		// Give the scheduler time to get the request.
		try {
			Thread.sleep(2000);
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
		elevatorSubsystemTransmissonChannel.setMessage(message);

		scheduler.start();

		// Give the scheduler time to get the request.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// The elevator ELEVATOR_STATUS_REQUEST should have been taken by the
		// scheduler
		assertTrue(elevatorSubsystemTransmissonChannel.isEmpty());
	}

	/**
	 * Test that job-request sent by the floor subsystem is passed to the elevator
	 * subsystem is accepted.
	 */
	@Test
	void testFloorSubsystemJobRequestIsTransferredToElevatorSubsystem() {
		SimulationFloorInputData data = new SimulationFloorInputData(SAMPLE_FLOOR_INPUT_DATA);
		JobRequest jobRequest = new JobRequest(data);
		floorSubsystemTransmissonChannel.setMessage(jobRequest);

		Message message = new Message(MessageType.ELEVATOR_STATUS_MESSAGE);
		elevatorSubsystemTransmissonChannel.setMessage(message);

		scheduler.start();

		// Give the scheduler time to work.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// The elevator ELEVATOR_STATUS_REQUEST should have been taken by the
		// scheduler
		assertTrue(elevatorSubsystemTransmissonChannel.isEmpty());

		// The scheduler should know that the elevator is ready. Hence, the job request
		// from the floor channel should be in elevator's receiver channel
		assertFalse(elevatorSubsystemReceiverChannel.isEmpty());
	}

	/**
	 * Test that job-request sent by the elevator subsystem is passed to the floor
	 * subsystem is accepted.
	 */
	@Test
	void testElevatorSubsystemJobRequestIsTransferredToFloorSubsystem() {
		SimulationFloorInputData data = new SimulationFloorInputData(SAMPLE_FLOOR_INPUT_DATA);
		JobRequest jobRequest = new JobRequest(data);

		elevatorSubsystemTransmissonChannel.setMessage(jobRequest);

		scheduler.start();

		// Give the scheduler time to work.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// The scheduler should send the job request from the elevator channel to the
		// floor's receiver channel
		assertFalse(floorSubsystemReceiverChannel.isEmpty());
	}

}
