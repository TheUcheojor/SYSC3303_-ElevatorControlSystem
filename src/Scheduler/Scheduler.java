/**
 *
 */
package Scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.Direction;
import common.SchedulerCommand;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.floor.ElevatorFloorRequest;
import common.messages.floor.JobRequest;
import common.messages.scheduler.SchedulerElevatorCommand;

/**
 * This represents the scheduler which manages the elevator and floor subsystem.
 *
 * @author paulokenne, jacobcharpentier, ryanfife, Favour
 *
 */
public class Scheduler implements Runnable {
		/**
	 *  channel that receives messages from floor subsystem
	 */
	private MessageChannel incomingFloorChannel;
	
	/**
	 * floor channel that gets messages from the scheduler 
	 */
	private MessageChannel outgoingFloorChannel;
	
	/**
	 * channel that receives messages from the elevator subsystem
	 */
	private MessageChannel incomingElevatorChannel;
	
	/**
	 * elevator channel that gets messages from the scheduler
	 */
	private MessageChannel outgoingElevatorChannel;
	

	/**
	 * elevator job queue
	 */
	private ArrayDeque<ElevatorJobMessage> elevatorJobQueue;
	private ArrayList<ElevatorJobMessage> unassignedElevatorJobs = new ArrayList<ElevatorJobMessage>();
	
	/**
	 * elevator floor number 
	 */
	private int elevatorFloorNumber;
	
	/**
	 * elevator floor number 
	 */
	public Direction elevatorDirection;
	
	/**
	 * elevator id
	 */
	public int elevatorId;

	/**
	 * a constructor
	 */
	public Scheduler(MessageChannel receiveFloorChannel, MessageChannel floorSubsystemReceiverChannel,
			MessageChannel receiveElevatorChannel, MessageChannel elevatorSubsystemReceiverChannel) {

		this.incomingElevatorChannel = receiveElevatorChannel;
		this.incomingFloorChannel = receiveFloorChannel;
		
		this.outgoingFloorChannel = floorSubsystemReceiverChannel;
		this.outgoingElevatorChannel = elevatorSubsystemReceiverChannel;
		
		// TODO (rfife) for iter 3: scale this to multiple elevators
		this.elevatorJobQueue = new ArrayDeque<ElevatorJobMessage>();
		this.unassignedElevatorJobs = new ArrayList<ElevatorJobMessage>();
	}

	@Override
	public void run() {

		while (true) {

			if (!incomingFloorChannel.isEmpty()) {
				Message floorRequest = incomingFloorChannel.popMessage();
				handleFloorRequest(floorRequest);
			}
			
			// Move unassigned jobs to the elevator
			if(unassignedElevatorJobs.size() != 0) {
				assignUnassignedJobs();
			}

			// Only read the data in the channel if the elevator is not ready for a job and
			// the channel is not empty.
			if (!incomingElevatorChannel.isEmpty()) {
				Message elevatorRequest = incomingElevatorChannel.popMessage();
				handleElevatorMessage(elevatorRequest);
			}
			
			if(elevatorJobQueue.size() > 0) {
				if(elevatorDirection != Direction.IDLE) serveJob();
				else startJob();
			}
		}
	}
	
	private void assignUnassignedJobs() {
		List<ElevatorJobMessage> toRemove = new ArrayList();
		
		unassignedElevatorJobs.forEach((ElevatorJobMessage job) -> {
			elevatorJobQueue.add(job);
			toRemove.add(job);
		});
		unassignedElevatorJobs.removeAll(toRemove);
	}
	
	/**
	 * Serves all elevator job requests for the current floor
	 */
	private void serveJob() {
		Iterator<ElevatorJobMessage> iterator = elevatorJobQueue.iterator();
		boolean jobServed = false;
		
		// iterate over job requests, remove jobs that are completed by arriving at this floor
		while(iterator.hasNext()) {
			ElevatorJobMessage currRequest = iterator.next();
			if(currRequest.getDestinationFloor() == elevatorFloorNumber) {
				elevatorJobQueue.remove(currRequest);
				jobServed = true;
			}
		}
		if(jobServed) {
			stopElevator();
			openElevatorDoors();
		}
	}
	
	/**
	 * Issues the necessary commands to the elevator for starting the first job in the queue.
	 */
	private void startJob() {
		ElevatorJobMessage firstJob = elevatorJobQueue.peekFirst();
		if(firstJob.getDestinationFloor() > elevatorFloorNumber) {
			
			closeElevatorDoors();
			moveElevatorUp();
		} else if(firstJob.getDestinationFloor() < elevatorFloorNumber) {
	
			closeElevatorDoors();
			moveElevatorDown();
		}
	}

	/**
	 * Handles floor request accordingly.
	 *
	 * @param message the request
	 */
	private void handleFloorRequest(Message message) {

		switch (message.getMessageType()) {
	
		case ELEVATOR_FLOOR_REQUEST:
			unassignedElevatorJobs.add((ElevatorJobMessage) message);

		default:
			break;
		}

	}

	/**
	 * Handles elevator messages accordingly.
	 *
	 * @param message the request
	 */
	private void handleElevatorMessage(Message message) {

		switch (message.getMessageType()) {

		case ELEVATOR_STATUS_MESSAGE:
			elevatorFloorNumber = ((ElevatorStatusMessage) message).floorNumber;
			elevatorDirection = ((ElevatorStatusMessage) message).direction;
			elevatorId = ((ElevatorStatusMessage) message).elevatorId;
			break;

		case ELEVATOR_TRANSPORT_REQUEST:
			elevatorJobQueue.add((ElevatorJobMessage) message);
		default:
			break;

		}

	}
	
	/**
	 * This method sends a command to the elevator to stop moving 
	 */
	private void stopElevator() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.STOP));
	}
	
	/**
	 * This method sends a command to the elevator to close elevator doors 
	 */
	private void closeElevatorDoors() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.CLOSE_DOORS));
	}
	
	/**
	 * This method sends a command to the elevator to open elevator doors
	 */
	private void openElevatorDoors () {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.OPEN_DOORS));
	}
	
	/**
	 * This method sends a command to the elevator to start moving up 
	 */
	private void moveElevatorUp() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.MOVE_UP));
	}
	
	/**
	 * This method sends a command to the elevator to start moving down
	 */
	private void moveElevatorDown() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.MOVE_DOWN));
	}	
}
