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
import ElevatorSubsystem.ElevatorSchedulerMessageWorkQueue;
import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorFloorRequestType;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;

/**
 * This is a test for the elevator/floor message communication
 * @author Favour
 */
public class TestElevatorFloorMessageWorkQueue {
	private SubsystemCommunicationRPC schedulerElevatorSubsystemCommunication;
	private SubsystemCommunicationRPC floorElevatorSubsystemCommunication;
	//private SubsystemCommunicationRPC elevatorSchedulerSubsystemCommunication;
	private SubsystemCommunicationRPC elevatorFloorSubsystemCommunication;
	
	private ElevatorFloorMessageWorkQueue workQueue;
	
	private int ELEVATOR_ID = 1;
	private int ELEVATOR_SPEED = 1000; 
	private int FLOOR_ID = 2;
	
	private Map<Integer, ElevatorCar> elevators;
	
	private ArrayDeque<Message> receivedFloorMessages = new ArrayDeque<>();
	private ArrayDeque<Message> receivedSchedulerMessages = new ArrayDeque<>();

	@BeforeEach
	void setup() {
		schedulerElevatorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.SCHEDULER,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);
		floorElevatorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.FLOOR_SUBSYSTEM,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);
		
	//	elevatorSchedulerSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
		//		SubsystemComponentType.SCHEDULER);
		elevatorFloorSubsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.ELEVATOR_SUBSYSTEM,
				SubsystemComponentType.FLOOR_SUBSYSTEM);
		
		elevators = new HashMap<Integer, ElevatorCar>();
		
		elevators.put(ELEVATOR_ID, new ElevatorCar(ELEVATOR_ID, new ElevatorMotor(ELEVATOR_SPEED), new ElevatorDoor(ELEVATOR_SPEED)));
		
		workQueue = new ElevatorFloorMessageWorkQueue(floorElevatorSubsystemCommunication, elevators);
		
		receivedFloorMessages = new ArrayDeque<>();
		receivedSchedulerMessages = new ArrayDeque<>();
	}
	
	@AfterEach
	void tearDown() {
		//schedulerElevatorSubsystemCommunication = null;
		floorElevatorSubsystemCommunication = null;
		
		//elevatorSchedulerSubsystemCommunication = null;
		elevatorFloorSubsystemCommunication = null;
		
		workQueue = null;
		elevators = null;
		
		receivedFloorMessages = null;
		//receivedSchedulerMessages = null;
	}
	

	@Test
	void testWorkQueueArrivalMessageHandler() {
		simulateFloorMessageWaiting();
		simulateFloorMessageWaiting();
		//simulateSchedulerMessageWaiting();
		
		FloorElevatorTargetedMessage floorMessage = (FloorElevatorTargetedMessage) new Message(ELEVATOR_ID, FLOOR_ID, ElevatorFloorRequestType.ELEVATOR_FLOOR_SIGNAL_REQUEST);
		try {
			workQueue.enqueueMessage(floorMessage);
		
			Thread.sleep(100);
			
			ElevatorFloorArrivalMessage message1 = (ElevatorFloorArrivalMessage) receivedFloorMessages.pop();
			assertTrue(message1 != null);
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
