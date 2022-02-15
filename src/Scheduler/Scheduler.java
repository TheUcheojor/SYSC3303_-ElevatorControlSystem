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
	private MessageChannel floorSubsystemTransmissonChannel;
	private MessageChannel floorSubsystemReceiverChannel;
	private MessageChannel elevatorSubsystemTransmissonChannel;
	private MessageChannel elevatorSubsystemReceiverChannel;
	private boolean isElevatorRunning = false;
	private int elevatorFloorNumber;
	private ArrayDeque<JobRequest> elevatorJobQueue;
	private Thread elevatorManagerThread;

	public Direction diretion;
	public boolean isDoorOpen;

	public Scheduler(MessageChannel floorSubsystemTransmissonChannel, MessageChannel floorSubsystemReceiverChannel,
			MessageChannel elevatorSubsystemTransmissonChannel, MessageChannel elevatorSubsystemReceiverChannel) {

		this.floorSubsystemTransmissonChannel = floorSubsystemTransmissonChannel;
		this.floorSubsystemReceiverChannel = floorSubsystemReceiverChannel;

		this.elevatorSubsystemTransmissonChannel = elevatorSubsystemTransmissonChannel;
		this.elevatorSubsystemReceiverChannel = elevatorSubsystemReceiverChannel;
		
		// TODO (rfife) for iter 3: scale this to multiple elevators
		this.elevatorJobQueue = new ArrayDeque<>();
	}

	@Override
	public void run() {

		while (true) {

			if (!floorSubsystemTransmissonChannel.isEmpty()) {
				Message floorRequest = floorSubsystemTransmissonChannel.popMessage();
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
			if (!elevatorSubsystemTransmissonChannel.isEmpty()) {
				Message elevatorRequest = elevatorSubsystemTransmissonChannel.popMessage();
				handleElevatorMessage(elevatorRequest);
			}
			
			// iterate over job requests to figure out if elevator should stop at this floor
			
			if(!elevatorManagerThread.isAlive() && isElevatorRunning && elevatorJobQueue.size() > 0) {
				Iterator<JobRequest> iterator = elevatorJobQueue.iterator();
				boolean jobServed = false;
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
						}
					};
					elevatorManagerThread.start();
				}
			} else if(!elevatorManagerThread.isAlive() && !isElevatorRunning && elevatorJobQueue.size() > 0) {
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
			isElevatorRunning = ((ElevatorStatusMessage) message).inService;
			elevatorFloorNumber = ((ElevatorStatusMessage) message).floorNumber;
			diretion = ((ElevatorStatusMessage) message).direction;
			isDoorOpen = ((ElevatorStatusMessage) message).isDoorOpen;
			break;

		default:
			break;

		}

	}
	
	/**
	 * This method sends a comand to the elevator to stop moving 
	 * Then recieves a status message back
	 */
	private void stopElevator() {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.STOP));
		handleElevatorMessage(elevatorSubsystemTransmissonChannel.popMessage());
	}
	
	/**
	 * This method sends a comand to the elevator to close elevator doors 
	 * Then recieves a status message back
	 */
	private void closeElevatorDoors() {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.CLOSE_DOORS));
		handleElevatorMessage(elevatorSubsystemTransmissonChannel.popMessage());
	}
	
	/**
	 * This method sends a comand to the elevator to open elevator doors
	 * Then recieves a status message back
	 */
	private void openElevatorDoors () {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.OPEN_DOORS));
		handleElevatorMessage(elevatorSubsystemTransmissonChannel.popMessage());
	}
	
	/**
	 * This method sends a comand to the elevator to start moving up 
	 * Then recieves a status message back
	 */
	private void moveElevatorUp() {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.MOVE_UP));
		handleElevatorMessage(elevatorSubsystemTransmissonChannel.popMessage());
	}
	
	/**
	 * This method sends a comand to the elevator to start moving down
	 * Then recieves a status message back
	 */
	private void moveElevatorDown() {
		elevatorSubsystemReceiverChannel.appendMessage(new SchedulerElevatorCommand(SchedulerCommand.MOVE_DOWN));
		handleElevatorMessage(elevatorSubsystemTransmissonChannel.popMessage());
	}	
}
