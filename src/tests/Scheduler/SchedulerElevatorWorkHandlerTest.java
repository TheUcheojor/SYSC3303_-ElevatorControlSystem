/**
 *
 */
package tests.Scheduler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorController;
import Scheduler.ElevatorJobManagement;
import Scheduler.SchedulerElevatorWorkHandler;
import common.Direction;
import common.messages.Message;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;

/**
 * This class tests how the scheduler communicates with the Elevator and manages
 * elevator messages
 *
 * @author paulokenne Favour Olotu
 *
 */
public class SchedulerElevatorWorkHandlerTest {
	/**
	 * The scheduler elevator message work handler
	 */
	private static SchedulerElevatorWorkHandler schedulerElevatorWorkHandler;

	/**
	 * The Scheduler UDP communication between the scheduler and floor
	 */
	private static SubsystemCommunicationRPC schedulerFloorCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.SCHEDULER, SubsystemComponentType.FLOOR_SUBSYSTEM);

	/**
	 * The Scheduler UDP communication between the scheduler and floor
	 */
	private static SubsystemCommunicationRPC schedulerElevatorCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.SCHEDULER, SubsystemComponentType.ELEVATOR_SUBSYSTEM);

	/**
	 * The Elevator UDP communication between the elevator and scheduler
	 */
	private static SubsystemCommunicationRPC elevatorSchedulerCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.ELEVATOR_SUBSYSTEM, SubsystemComponentType.SCHEDULER);

	/**
	 * The job management for each elevator
	 */
	private static ElevatorJobManagement[] elevatorJobManagements = new ElevatorJobManagement[ElevatorController.NUMBER_OF_ELEVATORS];

	/**
	 * The received message
	 */
	private ArrayDeque<Message> elevatorReceivedMessages = new ArrayDeque<>();

	/**
	 * Set up fresh environment for each test
	 *
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUp() throws Exception {

		for (int i = 0; i < elevatorJobManagements.length; i++) {
			elevatorJobManagements[i] = new ElevatorJobManagement(i);
		}

		schedulerElevatorWorkHandler = new SchedulerElevatorWorkHandler(schedulerFloorCommunication,
				schedulerElevatorCommunication, elevatorJobManagements);
	}

	@AfterEach
	void tearDown() {
		elevatorReceivedMessages = new ArrayDeque<>();
	}

	/**
	 * Test that the scheduler can drop off passengers
	 *
	 * In this test case, the elevator has a passenger at floor 2 and is requested
	 * to take the passenger to floor 0. This test case ensures that the scheduler
	 * issues the appropriate commands
	 */
	@Test
	void testSchedulerCanDropOffPassengers() {
		int elevatorId = 0;
		int currentFloorNumber = 2;

		simulateElevatorSubsystemWaitingForCommand();

		// Elevator 0 is ready for a job and is at floor 2
		elevatorJobManagements[elevatorId].setElevatorDirection(Direction.DOWN);
		elevatorJobManagements[elevatorId].setReadyForJob(true);
		elevatorJobManagements[elevatorId].setCurrentFloorNumber(currentFloorNumber);

		// We want to drop off a passenger from floor 2 to 0
		ElevatorTransportRequest elevatorTransportMessage = new ElevatorTransportRequest(0, elevatorId, Direction.DOWN,
				null, null);

		// Let the scheduler work
		schedulerElevatorWorkHandler.enqueueMessage(elevatorTransportMessage);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// Check that an MOVE_DOWN command was sent to the in-service elevator 0
		assertTrue(elevatorReceivedMessages.peek() instanceof SchedulerElevatorCommand);
		SchedulerElevatorCommand receivedSchedulerElevatorCommand = (SchedulerElevatorCommand) elevatorReceivedMessages
				.pop();

		assertTrue(receivedSchedulerElevatorCommand.getElevatorId() == elevatorId);
		assertTrue(receivedSchedulerElevatorCommand.getCommand() == ElevatorCommand.MOVE_DOWN);

		simulateElevatorSubsystemWaitingForCommand();

		// The elevator is at floor 1 and is sending a status message.
		currentFloorNumber = 1;
		ElevatorStatusMessage elevatorStatusMessage = new ElevatorStatusMessage(elevatorId, Direction.DOWN,
				currentFloorNumber, null, false);

		// Add elevator message and let the scheduler work
		schedulerElevatorWorkHandler.enqueueMessage(elevatorStatusMessage);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// Check that an MOVE_DOWN command was sent to the in-service elevator 0
		assertTrue(elevatorReceivedMessages.peek() instanceof SchedulerElevatorCommand);
		receivedSchedulerElevatorCommand = (SchedulerElevatorCommand) elevatorReceivedMessages.pop();

		assertTrue(receivedSchedulerElevatorCommand.getElevatorId() == elevatorId);
		assertTrue(receivedSchedulerElevatorCommand.getCommand() == ElevatorCommand.MOVE_DOWN);

		simulateElevatorSubsystemWaitingForCommand();

		// The elevator is at floor 0 and is sending a status message.
		currentFloorNumber = 0;
		elevatorStatusMessage = new ElevatorStatusMessage(elevatorId, Direction.DOWN, currentFloorNumber, null, false);

		simulateElevatorSubsystemWaitingForCommand();
		simulateElevatorSubsystemWaitingForCommand();

		// Add elevator message and let the scheduler work
		schedulerElevatorWorkHandler.enqueueMessage(elevatorStatusMessage);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		// Check that a STOP command was sent to the in-service elevator 0
		assertTrue(elevatorReceivedMessages.peek() instanceof SchedulerElevatorCommand);
		receivedSchedulerElevatorCommand = (SchedulerElevatorCommand) elevatorReceivedMessages.pop();

		assertTrue(receivedSchedulerElevatorCommand.getElevatorId() == elevatorId);
		assertTrue(receivedSchedulerElevatorCommand.getCommand() == ElevatorCommand.STOP);

		// Check that an OPEN DOORS command was sent to the in-service elevator 0
		assertTrue(elevatorReceivedMessages.peek() instanceof SchedulerElevatorCommand);
		receivedSchedulerElevatorCommand = (SchedulerElevatorCommand) elevatorReceivedMessages.pop();

		assertTrue(receivedSchedulerElevatorCommand.getElevatorId() == elevatorId);
		assertTrue(receivedSchedulerElevatorCommand.getCommand() == ElevatorCommand.OPEN_DOORS);
	}

	/**
	 * Simulate the elevator subsystem waiting for a command. Update the
	 * elevatorReceivedMessage
	 */
	private void simulateElevatorSubsystemWaitingForCommand() {
		(new Thread() {
			@Override
			public void run() {
				try {
					synchronized (elevatorReceivedMessages) {
						elevatorReceivedMessages.add(elevatorSchedulerCommunication.receiveMessage());
					}
				} catch (Exception e) {
					System.out.print("Exception occurred: " + e);
				}
			}
		}).start();
	}

}
