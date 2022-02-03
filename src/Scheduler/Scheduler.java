/**
 *
 */
package Scheduler;

import java.util.ArrayList;

import common.requests.JobRequest;
import common.requests.Request;
import common.requests.RequestChannel;

/**
 * This represents the scheduler which manages the elevator and floor subsystem.
 *
 * @author paulokenne, jacobcharpentier
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
	private RequestChannel floorSubsystemChannel;

	/**
	 * The elevator subsystem channel.
	 */
	private RequestChannel elevatorSubsystemChannel;

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
	public Scheduler(RequestChannel floorSubsystemChannel, RequestChannel elevatorSubsystemChannel) {
		this.floorSubsystemChannel = floorSubsystemChannel;
		this.elevatorSubsystemChannel = elevatorSubsystemChannel;
	}

	@Override
	public void run() {

		while (true) {
			if (!floorSubsystemChannel.isEmpty()) {
				System.out.println("\n" + Thread.currentThread().getName() + " sees the floor subsystem request.");

				Request floorRequest = floorSubsystemChannel.getRequest();
				handleFloorRequest(floorRequest);

				System.out.println(Thread.currentThread().getName() + " has addressed the floor subsystem request.");
			}

			// Only read the data in the channel if the elevator is not ready for a job and
			// the channel is not empty.
			if (!isElevatorSubsystemJobReady && !elevatorSubsystemChannel.isEmpty()) {
				System.out.println("\n" + Thread.currentThread().getName() + " sees the elevator subsystem request.");

				Request elevatorRequest = elevatorSubsystemChannel.getRequest();
				System.out.print("elevatorRequest " + elevatorRequest);
				handleElevatorRequest(elevatorRequest);

				System.out.println(Thread.currentThread().getName() + " has addressed the elevator subsystem request.");
			}

			// If the elevator is ready and there are job requests, send a job request to
			// the elevator system
			if (isElevatorSubsystemJobReady && !jobRequests.isEmpty()) {
				elevatorSubsystemChannel.setRequest(jobRequests.get(0));
				jobRequests.remove(0);
				isElevatorSubsystemJobReady = false;
			}

		}
	}

	/**
	 * Handles floor request accordingly.
	 *
	 * @param request the request
	 */
	private void handleFloorRequest(Request request) {

		switch (request.getRequestType()) {

		case JOB_REQUEST:
			jobRequests.add((JobRequest) request);
			break;

		case ELEVATOR_SUBSYSTEM_READY, TEST_REQUEST:
			break;
		}

	}

	/**
	 * Handles elevator request accordingly.
	 *
	 * @param request the request
	 */
	private void handleElevatorRequest(Request request) {

		switch (request.getRequestType()) {

		case JOB_REQUEST:
			floorSubsystemChannel.setRequest(request);
			break;

		case ELEVATOR_SUBSYSTEM_READY:
			isElevatorSubsystemJobReady = true;
			break;

		case TEST_REQUEST:
			break;

		}

	}
}
