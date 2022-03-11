package tests.ElevatorSubsystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
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

class TestElevatorSchedulerMessageWorkQueue {
	private SubsystemCommunicationRPC schedulerElevatorSubsystemCommunication;
	private SubsystemCommunicationRPC floorElevatorSubsystemCommunication;
	private SubsystemCommunicationRPC elevatorSchedulerSubsystemCommunication;
	private SubsystemCommunicationRPC elevatorFloorSubsystemCommunication;
	
	private ElevatorSchedulerMessageWorkQueue workQueue;
	
	private int ELEVATOR_ID = 1;
	private int ELEVATOR_SPEED = 1000;
	
	private Map<Integer, ElevatorCar> elevators;
	
	private ArrayDeque<Message> receivedFloorMessages = new ArrayDeque<>();
	private ArrayDeque<Message> receivedSchedulerMessages = new ArrayDeque<>();

	@BeforeEach
	void setup() {
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
		
		receivedFloorMessages = new ArrayDeque<>();
		receivedSchedulerMessages = new ArrayDeque<>();
	}
	
	@AfterEach
	void tearDown() {
		schedulerElevatorSubsystemCommunication = null;
		floorElevatorSubsystemCommunication = null;
		
		elevatorSchedulerSubsystemCommunication = null;
		elevatorFloorSubsystemCommunication = null;
		
		workQueue = null;
		elevators = null;
		
		receivedFloorMessages = null;
		receivedSchedulerMessages = null;
	}
	
	@Test
	void testWorkQueueCloseDoorsHandler() {
		simulateSchedulerMessageWaiting();
		
		SchedulerElevatorCommand elevatorCommand = new SchedulerElevatorCommand(ElevatorCommand.CLOSE_DOORS, ELEVATOR_ID);
		try {
			workQueue.enqueueMessage(elevatorCommand);
		
			Thread.sleep(100);
			
			ElevatorStatusMessage message3 = (ElevatorStatusMessage) receivedSchedulerMessages.pop();
			assertTrue(message3 != null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testWorkQueueMoveDownHandler() {
//		simulateFloorMessageWaiting();
		simulateFloorMessageWaiting();
		simulateSchedulerMessageWaiting();
		
		SchedulerElevatorCommand elevatorCommand = new SchedulerElevatorCommand(ElevatorCommand.MOVE_DOWN, ELEVATOR_ID);
		try {
			workQueue.enqueueMessage(elevatorCommand);
		
			Thread.sleep(2000);
			
			ElevatorLeavingFloorMessage message1 = (ElevatorLeavingFloorMessage) receivedFloorMessages.pop();
//			ElevatorFloorSignalRequestMessage message2 = (ElevatorFloorSignalRequestMessage) receivedFloorMessages.pop();
			ElevatorStatusMessage message3 = (ElevatorStatusMessage) receivedSchedulerMessages.pop();
			assertTrue(message3 != null);
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
