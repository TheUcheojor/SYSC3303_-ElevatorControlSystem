/**
 *
 */
package Scheduler;

import java.util.ArrayList;

import common.messages.JobRequest;
import common.messages.Message;
import common.messages.MessageChannel;

/**
 * This represents the scheduler which manages the elevator and floor subsystem.
 *
 * @author paulokenne, jacobcharpentier, ryanfife
 *
 */
public class Scheduler implements Runnable {

	/**
	 * The job requests.
	 */
	private ArrayList<JobRequest> jobRequests = new ArrayList<>();

	/**
	 * The floor subsystem transmission channel.
	 */
	private MessageChannel floorSubsystemTransmissonChannel;

	/**
	 * The floor subsystem receiver channel.
	 */
	private MessageChannel floorSubsystemReceiverChannel;

	/**
	 * The elevator subsystem transmission channel.
	 */
	private MessageChannel elevatorSubsystemTransmissonChannel;

	/**
	 * The elevator subsystem receiver channel.
	 */
	private MessageChannel elevatorSubsystemReceiverChannel;

	/**
	 * A flag indicating whether the elevator subsystem is ready to take a job
	 * request.
	 */
	private boolean isElevatorSubsystemJobReady = false;

	/**
	 * A constructor.
	 *
	 * @param floorSubsystemChannel    the floor subsystem channel
	 * @param elevatorSubsystemChanell the elevator subsystem channel
	 */
	public Scheduler(MessageChannel floorSubsystemTransmissonChannel, MessageChannel floorSubsystemReceiverChannel,
			MessageChannel elevatorSubsystemTransmissonChannel, MessageChannel elevatorSubsystemReceiverChannel) {

		this.floorSubsystemTransmissonChannel = floorSubsystemTransmissonChannel;
		this.floorSubsystemReceiverChannel = floorSubsystemReceiverChannel;

		this.elevatorSubsystemTransmissonChannel = elevatorSubsystemTransmissonChannel;
		this.elevatorSubsystemReceiverChannel = elevatorSubsystemReceiverChannel;
	}

	@Override
	public void run() {

		while (true) {
			if (!floorSubsystemTransmissonChannel.isEmpty()) {
//				System.out.println("\n" + Thread.currentThread().getName() + " sees the floor subsystem message.");
				Message floorRequest = floorSubsystemTransmissonChannel.getMessage();
				handleFloorRequest(floorRequest);
			}

			// Only read the data in the channel if the elevator is not ready for a job and
			// the channel is not empty.
			if (!isElevatorSubsystemJobReady && !elevatorSubsystemTransmissonChannel.isEmpty()) {
//				System.out.println("\n" + Thread.currentThread().getName() + " sees the elevator subsystem message.");

				Message elevatorRequest = elevatorSubsystemTransmissonChannel.getMessage();
				handleElevatorRequest(elevatorRequest);
			}

			// If the elevator is ready and there are job requests, send a job request to
			// the elevator system
			if (isElevatorSubsystemJobReady && !jobRequests.isEmpty()) {
				elevatorSubsystemReceiverChannel.setMessage(jobRequests.get(0));
				jobRequests.remove(0);
				isElevatorSubsystemJobReady = false;
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
			jobRequests.add((JobRequest) message);
			break;

		default:
			break;
		}

	}

	/**
	 * Handles elevator request accordingly.
	 *
	 * @param message the request
	 */
	private void handleElevatorRequest(Message message) {

		switch (message.getMessageType()) {

		case JOB_REQUEST:
			floorSubsystemReceiverChannel.setMessage(message);
			break;

		case ELEVATOR_STATUS_MESSAGE:
			isElevatorSubsystemJobReady = true;
			break;

		default:
			break;

		}

	}
}
