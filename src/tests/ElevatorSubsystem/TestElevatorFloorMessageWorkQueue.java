package tests.ElevatorSubsystem;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
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
 * @author Favour
 */
public class TestElevatorFloorMessageWorkQueue {
	private SubsystemCommunicationRPC schedulerElevatorSubsystemCommunication;
	private ElevatorFloorMessageWorkQueue workQueue;
	
	private int ELEVATOR_ID = 1;
	private int ELEVATOR_SPEED = 1000; 
	private int FLOOR_ID = 2;
	
	private Map<Integer, ElevatorCar> elevators;
	
	//private ArrayDeque<Message> receivedFloorMessages = new ArrayDeque<>();
	private ArrayDeque<Message> receivedSchedulerMessages = new ArrayDeque<>();

	@BeforeEach
	void setup() {
		schedulerElevatorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.SCHEDULER,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);
		new SubsystemCommunicationRPC(SubsystemComponentType.FLOOR_SUBSYSTEM,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);
		
		new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
	 		SubsystemComponentType.SCHEDULER);
		new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
				SubsystemComponentType.FLOOR_SUBSYSTEM);
		
		elevators = new HashMap<Integer, ElevatorCar>();
		
		elevators.put(ELEVATOR_ID, new ElevatorCar(ELEVATOR_ID, new ElevatorMotor(ELEVATOR_SPEED), new ElevatorDoor(ELEVATOR_SPEED)));
		
		workQueue = new ElevatorFloorMessageWorkQueue(schedulerElevatorSubsystemCommunication, elevators);
		
		//receivedFloorMessages = new ArrayDeque<>();
		receivedSchedulerMessages = new ArrayDeque<>();
	}
	
	@AfterEach
	void tearDown() {
		schedulerElevatorSubsystemCommunication = null;
		workQueue = null;
		elevators = null;
		
		//receivedFloorMessages = null;
		receivedSchedulerMessages = null;
	}
	

	@Test
	void testWorkQueueArrivalMessageHandler() {
		simulateSchedulerMessageWaiting();
		
		ElevatorFloorArrivalMessage floorMessage = new ElevatorFloorArrivalMessage(ELEVATOR_ID, FLOOR_ID, ELEVATOR_SPEED);
		
		ElevatorStatusMessage statusMessage = null;
		try {
			workQueue.enqueueMessage(floorMessage);
		
			Thread.sleep(1000);
			
			//ElevatorFloorArrivalMessage floorMessageRecieved = (ElevatorFloorArrivalMessage) receivedFloorMessages.pop();
			statusMessage = (ElevatorStatusMessage) receivedSchedulerMessages.pop();
			assertTrue(statusMessage != null);
			assertTrue(statusMessage.getMessageType() == MessageType.ELEVATOR_STATUS_MESSAGE);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
