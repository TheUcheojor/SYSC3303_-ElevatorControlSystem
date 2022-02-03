/**
 *
 */
package Scheduler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.SimulationFloorInputData;
import common.requests.JobRequest;
import common.requests.Request;
import common.requests.RequestChannel;
import common.requests.RequestType;

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
	 * The floor subsystem channel.
	 */
	private RequestChannel floorSubsystemChannel;

	/**
	 * The elevator subsystem channel.
	 */
	private RequestChannel elevatorSubsystemChannel;

	/**
	 * The scheduler.
	 */
	private Thread scheduler;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		floorSubsystemChannel = new RequestChannel();
		elevatorSubsystemChannel = new RequestChannel();

		scheduler = new Thread(new Scheduler(floorSubsystemChannel, elevatorSubsystemChannel), "");
	}

	/**
	 * This test cases tests that the request received from the floor subsystem is
	 * accepted.
	 */
	@Test
	void testFloorSubsystemRequestIsAccepted() {
		SimulationFloorInputData data = new SimulationFloorInputData("14:05:15.0 2 UP 4");
		JobRequest jobRequest = new JobRequest(data);
		floorSubsystemChannel.setRequest(jobRequest);

		Request request = new Request(RequestType.TEST_REQUEST);
		elevatorSubsystemChannel.setRequest(request);

		scheduler.start();

		// Give the scheduler time to get the request.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(floorSubsystemChannel.isEmpty());
	}

	/**
	 * This test cases tests that the request received from the elevator subsystem
	 * is accepted.
	 */
	@Test
	void testElevatorSubsystemRequestIsAccepted() {
		Request request = new Request(RequestType.ELEVATOR_SUBSYSTEM_READY);
		elevatorSubsystemChannel.setRequest(request);

		scheduler.start();

		// Give the scheduler time to get the request.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue(elevatorSubsystemChannel.isEmpty());
	}
}
