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
	 * The floor subsystem channel.
	 */
	private MessageChannel floorSubsystemChannel;

	/**
	 * The elevator subsystem channel.
	 */
	private MessageChannel elevatorSubsystemChannel;

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
	public Scheduler(MessageChannel floorSubsystemChannel, MessageChannel elevatorSubsystemChannel) {
		this.floorSubsystemChannel = floorSubsystemChannel;
		this.elevatorSubsystemChannel = elevatorSubsystemChannel;
	}

	@Override
	public void run() {

		while (true) {
			if (!floorSubsystemChannel.isEmpty()) {
				System.out.println("\n" + Thread.currentThread().getName() + " sees the floor subsystem request.");

				Message floorRequest = floorSubsystemChannel.getMessage();
				handleFloorRequest(floorRequest);

				System.out.println(Thread.currentThread().getName() + " has addressed the floor subsystem request.");
			}

			// Only read the data in the channel if the elevator is not ready for a job and
			// the channel is not empty.
			if (!isElevatorSubsystemJobReady && !elevatorSubsystemChannel.isEmpty()) {
				System.out.println("\n" + Thread.currentThread().getName() + " sees the elevator subsystem request.");

				Message elevatorRequest = elevatorSubsystemChannel.getMessage();
				System.out.print("elevatorRequest " + elevatorRequest);
				handleElevatorRequest(elevatorRequest);

				System.out.println(Thread.currentThread().getName() + " has addressed the elevator subsystem request.");
			}

			// If the elevator is ready and there are job requests, send a job request to
			// the elevator system
			if (isElevatorSubsystemJobReady && !jobRequests.isEmpty()) {
				elevatorSubsystemChannel.setMessage(jobRequests.get(0));
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

		case ELEVATOR_STATUS_RESPONSE, TEST_REQUEST:
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
			floorSubsystemChannel.setMessage(message);
			break;

		case ELEVATOR_STATUS_RESPONSE:
			isElevatorSubsystemJobReady = true;
			break;

		case TEST_REQUEST:
			break;

		}

	}
}
