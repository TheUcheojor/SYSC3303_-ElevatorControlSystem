package FloorSubsystem;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
	 * The floor subsystem transmission message channel.
	 */
	private MessageChannel floorSubsystemTransmissonChannel;

	/**
	 * The floor subsystem transmission message channel.
	 */
	private MessageChannel floorSubsystemReceiverChannel;

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

		floorSubsystem = new Thread(new FloorSubsystem(new SimulationFloorInputData("14:05:15.0 2 UP 4"),
				floorSubsystemTransmissonChannel, floorSubsystemTransmissonChannel), "");

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

}
