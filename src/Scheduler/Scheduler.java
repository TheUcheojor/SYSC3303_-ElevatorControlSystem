/**
 *
 */
package Scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;

import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.floor.JobRequest;

/**
 * This represents the scheduler which manages the elevator and floor subsystem.
 *
 * @author paulokenne, jacobcharpentier, ryanfife
 *
 */
public class Scheduler implements Runnable {
	private ArrayList<JobRequest> unassignedJobRequests = new ArrayList<>();
	private MessageChannel floorSubsystemTransmissonChannel;
	private MessageChannel floorSubsystemReceiverChannel;
	private MessageChannel elevatorSubsystemTransmissonChannel;
	private MessageChannel elevatorSubsystemReceiverChannel;
	private boolean isElevatorRunning = false;
	private int elevatorFloor;
	private ArrayDeque<JobRequest> elevatorJobQueue;

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
				Message floorRequest = floorSubsystemTransmissonChannel.getMessage();
				handleFloorRequest(floorRequest);
			}
			
			// Move unassigned jobs to the elevator
			if(isElevatorRunning && unassignedJobRequests.size() != 0) {
				unassignedJobRequests.forEach((JobRequest job) -> {
					elevatorJobQueue.add(job);
					unassignedJobRequests.remove(job);
				});
			}

			// Only read the data in the channel if the elevator is not ready for a job and
			// the channel is not empty.
			if (!elevatorSubsystemTransmissonChannel.isEmpty()) {
				Message elevatorRequest = elevatorSubsystemTransmissonChannel.getMessage();
				handleElevatorMessage(elevatorRequest);
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
			elevatorFloor = ((ElevatorStatusMessage) message).floorNumber;
			break;

		default:
			break;

		}

	}
	
	private void stopElevator() {
		
	}
	
	private void closeElevatorDoors() {
		
	}
	
	private void openElevatorDoors () {
		
	}
	
	private void moveElevatorUp() {
		
	}
	
	private void moveElevatorDown() {
		
	}
	
	
}
