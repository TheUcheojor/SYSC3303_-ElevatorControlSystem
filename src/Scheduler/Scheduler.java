/**
 *
 */
package Scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ElevatorSubsystem.ElevatorCar;
import ElevatorSubsystem.ElevatorController;
import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.FloorCommand;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.messages.scheduler.SchedulerFloorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;
import common.work_management.MessageWorkQueue;

/**
 * This represents the scheduler which manages the elevator and floor subsystem.
 *
 * @author paulokenne, jacobcharpentier, ryanfife, Favour, delightOluwayemi
 *
 */
public class Scheduler {

	/**
	 * The UDP communication between the scheduler and floor
	 */
	private SubsystemCommunicationRPC schedulerFloorCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.SCHEDULER, SubsystemComponentType.FLOOR_SUBSYSTEM);

	/**
	 * The UDP communication between the scheduler and elevator
	 */
	private SubsystemCommunicationRPC schedulerElevatorCommunication = new SubsystemCommunicationRPC(
			SubsystemComponentType.SCHEDULER, SubsystemComponentType.ELEVATOR_SUBSYSTEM);

	/**
	 * The job management for each elevator
	 */
	private ElevatorJobManagement[] elevatorJobManagements = new ElevatorJobManagement[ElevatorController.NUMBER_OF_ELEVATORS];

	/**
	 * The work handler for the elevator
	 */
	private SchedulerElevatorWorkHandler schedulerElevatorWorkHandler;
	
	/**
	 * THe work handler for the floor
	 */
	private SchedulerFloorWorkHandler schedulerFloorWorkhandler;

	/**
	 * The Scheduler constructor
	 */
	public Scheduler() {

		for (int i = 0; i < elevatorJobManagements.length; i++) {
			elevatorJobManagements[i] = new ElevatorJobManagement(i);
		}
	}
	/**
	 * The main function of the Scheduler
	 * @param args
	 */
	public void main(String[] args) {
		Scheduler scheduler =new Scheduler();
		scheduler.runSchedulerProgram();
	}
	
	/**
	 * The function runs the message queue for floor and elevator
	 */
	public void runSchedulerProgram() {
		setUpMessageQueueing(schedulerFloorCommunication, schedulerFloorWorkhandler);
		
		setUpMessageQueueing(schedulerElevatorCommunication, schedulerElevatorWorkHandler);
	}
	
	/**
	 * The function sets up the message queue
	 * @param communication
	 * @param workQueue
	 */
	public void setUpMessageQueueing(SubsystemCommunicationRPC communication, MessageWorkQueue workQueue ) {
		new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						Message message = communication.receiveMessage();
						workQueue.enqueueMessage(message);
					}catch(Exception e) {
						System.out.println(e);
						System.exit(1);
					}
				}
			}
		};
	}
}
