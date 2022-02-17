/**
 *
 */
package Scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import common.Direction;
import common.SchedulerCommand;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.floor.JobRequest;
import common.messages.scheduler.SchedulerElevatorCommand;

/**
 * This represents the scheduler which manages the elevator and floor subsystem.
 *
 * @author paulokenne, jacobcharpentier, ryanfife, Favour
 *
 */
public class Scheduler implements Runnable {
	private ArrayList<JobRequest> unassignedJobRequests = new ArrayList<>();
	
	/**
	 *  channel that receives messages from floor subsystem
	 */
	private MessageChannel receiveFloorChannel;
	
	/**
	 * floor channel that gets messages from the scheduler 
	 */
	private MessageChannel floorSubsystemReceiverChannel;
	
	/**
	 * channel that receives messages from the elevator subsystem
	 */
	private MessageChannel receiveElevatorChannel;
	
	/**
	 * elevator channel that gets messages from the scheduler
	 */
	private MessageChannel elevatorSubsystemReceiverChannel;
	

	/**
	 * elevator job queue
	 */
	private ArrayDeque<JobRequest> elevatorJobQueue;
	
	/**
	 * elevator manager thread
	 */
	private Thread elevatorManagerThread;
	
	/**
	 * elevator floor number 
	 */
	private int elevatorFloorNumber;
	
	/**
	 * elevator floor number 
	 */
	public Direction elevatorDirection;
	
	/**
	 * elevator door status 
	 */
	public boolean elevatorIsDoorOpen;
	
	/**
	 * elevator idle status
	 */
	private boolean elevatorIsIdle = false;

	/**
	 * a constructor
	 */
	public Scheduler(MessageChannel receiveFloorChannel, MessageChannel floorSubsystemReceiverChannel,
			MessageChannel receiveElevatorChannel, MessageChannel elevatorSubsystemReceiverChannel) {

		this.receiveElevatorChannel = receiveElevatorChannel;
		this.receiveFloorChannel = receiveFloorChannel;
		
		this.floorSubsystemReceiverChannel = floorSubsystemReceiverChannel;
		this.elevatorSubsystemReceiverChannel = elevatorSubsystemReceiverChannel;
		
		// TODO (rfife) for iter 3: scale this to multiple elevators
		this.elevatorJobQueue = new ArrayDeque<>();
	}

	@Override
	public void run() {

		while (true) {

			if (!receiveFloorChannel.isEmpty()) {
				Message floorRequest = receiveFloorChannel.popMessage();
				handleFloorRequest(floorRequest);
			}
			
			// Move unassigned jobs to the elevator
			if(unassignedJobRequests.size() != 0) {
				unassignedJobRequests.forEach((JobRequest job) -> {
					elevatorJobQueue.add(job);
					unassignedJobRequests.remove(job);
				});
			}

			// Only read the data in the channel if the elevator is not ready for a job and
			// the channel is not empty.
			if (!receiveElevatorChannel.isEmpty()) {
				Message elevatorRequest = receiveElevatorChannel.popMessage();
				handleElevatorMessage(elevatorRequest);
			}
			
			if(!elevatorManagerThread.isAlive() && elevatorJobQueue.size() > 0) {
				if(elevatorIsIdle) serveJob();
				else startJob();
			}
		}
	}
	
	/**
	 * Serves all elevator job requests for the current floor
	 */
	private void serveJob() {
		Iterator<JobRequest> iterator = elevatorJobQueue.iterator();
		boolean jobServed = false;
		
		// iterate over job requests, remove jobs that are completed by arriving at this floor
		while(iterator.hasNext()) {
			JobRequest currRequest = iterator.next();
			if(currRequest.getFloorId() == elevatorFloorNumber) {
				elevatorJobQueue.remove(currRequest);
				jobServed = true;
			}
		}
		if(jobServed) {
			elevatorManagerThread = new Thread() {	
				@Override
				public void run() {
					stopElevator();
					openElevatorDoors();
					// TODO (rfife): send turn off lamp to floor
				}
			};
			elevatorManagerThread.start();
		}
	}
	
	/**
	 * Issues the necessary commands to the elevator for starting the first job in the queue.
	 */
	private void startJob() {
		JobRequest firstJob = elevatorJobQueue.peekFirst();
		if(firstJob.getFloorId() > elevatorFloorNumber) {
			elevatorManagerThread = new Thread() {	
				@Override
				public void run() {
					closeElevatorDoors();
					moveElevatorUp();
				}
			};
			elevatorManagerThread.start();
		} else if(firstJob.getFloorId() < elevatorFloorNumber) {
			elevatorManagerThread = new Thread() {	
				@Override
				public void run() {
					closeElevatorDoors();
					moveElevatorDown();
				}
			};
			elevatorManagerThread.start();
		}
	}

	/**
	 * Handles floor request accordingly.
	 *
	 * @param message the request
	 */
	private void handleFloorRequest(Message message) {

		switch (message.getMessageType()) {

		case JOB_REQUEST:
			unassignedJobRequests.add((JobRequest) message);
			break;

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
			elevatorIsIdle = ((ElevatorStatusMessage) message).inService;
			elevatorFloorNumber = ((ElevatorStatusMessage) message).floorNumber;
			elevatorDirection = ((ElevatorStatusMessage) message).direction;
			elevatorIsDoorOpen = ((ElevatorStatusMessage) message).isDoorOpen;
			break;

		default:
			break;

		}

	}
	
	/**
	 * This method sends a command to the elevator to stop moving 
	 */
	private void stopElevator() {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.STOP));
	}
	
	/**
	 * This method sends a command to the elevator to close elevator doors 
	 */
	private void closeElevatorDoors() {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.CLOSE_DOORS));
	}
	
	/**
	 * This method sends a command to the elevator to open elevator doors
	 */
	private void openElevatorDoors () {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.OPEN_DOORS));
	}
	
	/**
	 * This method sends a command to the elevator to start moving up 
	 */
	private void moveElevatorUp() {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.MOVE_UP));
	}
	
	/**
	 * This method sends a command to the elevator to start moving down
	 */
	private void moveElevatorDown() {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.MOVE_DOWN));
	}	
}
