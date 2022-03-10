/**
 *
 */
package tests.Scheduler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorController;
import Scheduler.ElevatorJobManagement;
import Scheduler.SchedulerFloorMessageWorkQueue;
import common.Direction;
import common.messages.Message;
import common.messages.floor.ElevatorFloorRequest;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;

/**
 * This class tests how the scheduler communicates with the Floor and manages
 * floor messages
 *
 * @author paulokenne
 *
 */
public class SchedulerFloorMessageWorkQueueTest {

	/**
	 * The scheduler floor message work queue
	 */
	SchedulerFloorMessageWorkQueue schedulerFloorMessageWorkQueue;

	/**
	 * The Scheduler UDP communication between the scheduler and floor
	 */
	private SubsystemCommunicationRPC schedulerFloorCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.SCHEDULER, SubsystemComponentType.FLOOR_SUBSYSTEM);

	/**
	 * The Scheduler UDP communication between the scheduler and floor
	 */
	private SubsystemCommunicationRPC schedulerElevatorCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.SCHEDULER, SubsystemComponentType.ELEVATOR_SUBSYSTEM);

	/**
	 * The Elevator UDP communication between the elevator and scheduler
	 */
	private SubsystemCommunicationRPC elevatorSchedulerCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.ELEVATOR_SUBSYSTEM, SubsystemComponentType.SCHEDULER);

	/**
	 * The job management for each elevator
	 */
	private ElevatorJobManagement[] elevatorJobManagements = new ElevatorJobManagement[ElevatorController.NUMBER_OF_ELEVATORS];

	/**
	 * The received message
	 */
	private Message receivedMessage = null;

	/**
	 * Set up fresh environment for each test
	 *
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		for (int i = 0; i < elevatorJobManagements.length; i++) {
			elevatorJobManagements[i] = new ElevatorJobManagement(i);
		}

		this.schedulerFloorMessageWorkQueue = new SchedulerFloorMessageWorkQueue(schedulerFloorCommunication,
				schedulerElevatorCommunication, elevatorJobManagements);
	}

	/**
	 * Test that the scheduler sends an in-service elevator in the right direction
	 * to pick up a passenger.
	 *
	 */
	@Test
	void testSchedulerSendsInServiceElevatorInCorrectDirection() {

		// This thread simulates an elevator subsystem which is waiting for a message
		// from the scheduler.
		(new Thread() {
			@Override
			public void run() {
				try {
					receivedMessage = elevatorSchedulerCommunication.receiveMessage();
				} catch (Exception e) {
					System.out.print("Excpetion occurred: " + e);
				}
			}
		}).start();

		// Since elevators start at the ground floor 0, we expect the elevator to
		// receive a go up command
		int elevatorId = 1;

		// By default all elevators are not ready for jobs so we will make a elevator
		// (id = 1) ready
		elevatorJobManagements[elevatorId].setReadyForJob(true);

		(new Thread() {
			@Override
			public void run() {
				ElevatorFloorRequest elevatorFloorRequest = new ElevatorFloorRequest(2, Direction.DOWN);
				schedulerFloorMessageWorkQueue.enqueueMessage(elevatorFloorRequest);
			}
		}).start();

		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		System.out.println("receivedMessage: " + receivedMessage);
		assertTrue(receivedMessage instanceof SchedulerElevatorCommand);

		// Check that an MOVE_UP command was sent to the in-service elevator 1
		SchedulerElevatorCommand receivedSchedulerElevatorCommand = (SchedulerElevatorCommand) receivedMessage;
		assertTrue(receivedSchedulerElevatorCommand.getElevatorID() == elevatorId);
		assertTrue(receivedSchedulerElevatorCommand.getCommand() == ElevatorCommand.MOVE_UP);

	}

}
