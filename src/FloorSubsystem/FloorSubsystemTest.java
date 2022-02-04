package FloorSubsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Scheduler.Scheduler;
import common.SimulationFloorInputData;
import common.messages.MessageChannel;

/**
 * Tests the floorSubsystem based on iteration 1 requirements.
 *
 * @author Favour
 *
 */
class FloorSubsystemTest {


	/**
	 * The floor subsystem channel.
	 */
	private MessageChannel floorSubsystemChannel;

	/**
	 * The scheduler and floor subsystem threads.
	 */
	private Thread scheduler, floorSubsystem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		floorSubsystemChannel = new MessageChannel();
		
		floorSubsystem = new Thread(new FloorSubsystem(new SimulationFloorInputData("14:05:15.0 2 UP 4"), floorSubsystemChannel), "");
		scheduler = new Thread(new Scheduler(floorSubsystemChannel, new MessageChannel()), "Scheduler");
	}

	/**
	 * This tests that the floor subsystem can communicate with the scheduler.
	 */
	@Test
	void testFloorSubsystemCommunication() {
		
		floorSubsystem.start();
		scheduler.start();
		assertTrue(floorSubsystemChannel.isEmpty());
	}



}
