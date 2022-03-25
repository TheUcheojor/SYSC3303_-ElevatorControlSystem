package tests.ElevatorSubsystem;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ElevatorSubsystem.ElevatorCar;
import ElevatorSubsystem.ElevatorDoor;
import ElevatorSubsystem.ElevatorFloorMessageWorkQueue;
import ElevatorSubsystem.ElevatorMotor;
import common.messages.Message;
import common.messages.MessageType;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;

/**
 * This is a test for the elevator/floor message communication
 * @author Favour, delightoluwayemi
 */
public class TestElevatorFloorMessageWorkQueue {
	private static SubsystemCommunicationRPC schedulerElevatorSubsystemCommunication;
	private static SubsystemCommunicationRPC elevatorSchedulerSubsystemCommunication;
	private static ElevatorFloorMessageWorkQueue workQueue;
	
	private static int ELEVATOR_ID = 1;
	private static int ELEVATOR_SPEED = 1000; 
	private static int FLOOR_ID = 2;
	
	private static Map<Integer, ElevatorCar> elevators;
	
	private static ArrayDeque<Message> receivedSchedulerMessages = new ArrayDeque<>();

	@BeforeAll
	static void setup() {
		schedulerElevatorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.SCHEDULER,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);
		
		elevatorSchedulerSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
	 		SubsystemComponentType.SCHEDULER);
		
		elevators = new HashMap<Integer, ElevatorCar>();
		
		elevators.put(ELEVATOR_ID, new ElevatorCar(ELEVATOR_ID, new ElevatorMotor(ELEVATOR_SPEED), new ElevatorDoor(ELEVATOR_SPEED)));
		
		workQueue = new ElevatorFloorMessageWorkQueue(elevatorSchedulerSubsystemCommunication, elevators);
		
		receivedSchedulerMessages = new ArrayDeque<>();
	}
	
	@AfterEach
	void tearDown() {
		schedulerElevatorSubsystemCommunication = null;
		workQueue = null;
		elevators = null;
		
		receivedSchedulerMessages = null;
	}
	

	@Test
	void testWorkQueueArrivalMessageHandler() {
		simulateSchedulerMessageWaiting();
		
		ElevatorFloorArrivalMessage floorMessage = new ElevatorFloorArrivalMessage(ELEVATOR_ID, FLOOR_ID, ELEVATOR_SPEED);
		
		ElevatorStatusMessage statusMessage = null;
		try {
			workQueue.enqueueMessage(floorMessage);
		
			Thread.sleep(100);
			
			statusMessage = (ElevatorStatusMessage) receivedSchedulerMessages.pop();
			assertTrue(statusMessage != null);
			assertTrue(statusMessage.getMessageType() == MessageType.ELEVATOR_STATUS_MESSAGE);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void simulateSchedulerMessageWaiting() {
		(new Thread() {
			@Override
			public void run() {
				try {
					synchronized (receivedSchedulerMessages) {
						receivedSchedulerMessages.add(schedulerElevatorSubsystemCommunication.receiveMessage());
					}
				} catch (Exception e) {
					System.out.print("Exception occurred: " + e);
				}
			}
		}).start();
	}
}
