package tests.ElevatorSubsystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorCar;
import ElevatorSubsystem.ElevatorDoor;
import ElevatorSubsystem.ElevatorMotor;
import ElevatorSubsystem.ElevatorSchedulerMessageWorkQueue;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;
/**
 * This is a test class for the elevator message work queue
 * @author Ryan Fife, delightoluwayemi
 *
 */
class TestElevatorSchedulerMessageWorkQueue {
	private static SubsystemCommunicationRPC schedulerElevatorSubsystemCommunication;
	private static SubsystemCommunicationRPC floorElevatorSubsystemCommunication;
	private static SubsystemCommunicationRPC elevatorSchedulerSubsystemCommunication;
	private static SubsystemCommunicationRPC elevatorFloorSubsystemCommunication;
	
	private static ElevatorSchedulerMessageWorkQueue workQueue;
	
	private static int ELEVATOR_ID = 1;
	private static int ELEVATOR_SPEED = 1000;
	
	private static Map<Integer, ElevatorCar> elevators;
	
	private ArrayDeque<Message> receivedFloorMessages = new ArrayDeque<>();
	private ArrayDeque<Message> receivedSchedulerMessages = new ArrayDeque<>();

	@BeforeAll
	static void setup() {
		schedulerElevatorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.SCHEDULER,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);
		floorElevatorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.FLOOR_SUBSYSTEM,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);
		
		elevatorSchedulerSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
				SubsystemComponentType.SCHEDULER);
		elevatorFloorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
				SubsystemComponentType.FLOOR_SUBSYSTEM);
		
		elevators = new HashMap<Integer, ElevatorCar>();
		
		elevators.put(ELEVATOR_ID, new ElevatorCar(ELEVATOR_ID, new ElevatorMotor(ELEVATOR_SPEED), new ElevatorDoor(ELEVATOR_SPEED)));
		
		workQueue = new ElevatorSchedulerMessageWorkQueue(elevatorSchedulerSubsystemCommunication, elevatorFloorSubsystemCommunication, elevators);
		
	}
	

	@AfterEach
	void tearDown() {
		
		receivedFloorMessages = new ArrayDeque<>();
		receivedSchedulerMessages = new ArrayDeque<>();
	}


	
	@Test
	void testWorkQueueMoveDownHandler() {
		simulateFloorMessageWaiting();
		simulateFloorMessageWaiting();
		
		SchedulerElevatorCommand elevatorCommand = new SchedulerElevatorCommand(ElevatorCommand.MOVE_DOWN, ELEVATOR_ID);
		try {
			workQueue.enqueueMessage(elevatorCommand);
		
			Thread.sleep(1000);
			
			ElevatorLeavingFloorMessage message1 = (ElevatorLeavingFloorMessage) receivedFloorMessages.pop();
			ElevatorFloorSignalRequestMessage message2 = (ElevatorFloorSignalRequestMessage) receivedFloorMessages.pop();
			assertTrue(message1 != null);
			assertTrue(message2 != null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void simulateFloorMessageWaiting() {
		(new Thread() {
			@Override
			public void run() {
				try {
					synchronized (receivedFloorMessages) {
						receivedFloorMessages.add(floorElevatorSubsystemCommunication.receiveMessage());
					}
				} catch (Exception e) {
					System.out.print("Exception occurred: " + e);
				}
			}
		}).start();
	}
}
