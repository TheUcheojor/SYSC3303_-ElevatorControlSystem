/**
 *
 */
package Scheduler;

import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.remote_procedure.SubsystemCommunicationRPC;

/**
 * This class represents the scheduler floor work queue handler. Floor messages
 * added to the queue will be handled by a worker.
 *
 * @author paulokenne
 *
 */
public class SchedulerFloorWorkHandler extends SchedulerWorkHandler {

	/**
	 * The SchedulerFloorMessageWorkQueue constructor
	 *
	 * @param schedulerFloorCommunication    the scheduler floor UDP communication
	 * @param schedulerElevatorCommunication the scheduler elevator UDP
	 *                                       communication
	 * @param elevatorJobManagements         the elevator job managements
	 * @param elevatorJobManagements
	 */
	public SchedulerFloorWorkHandler(SubsystemCommunicationRPC schedulerFloorCommunication,
			SubsystemCommunicationRPC schedulerElevatorCommunication, ElevatorJobManagement[] elevatorJobManagements) {
		super(schedulerFloorCommunication, schedulerElevatorCommunication, elevatorJobManagements);
	}

	@Override
	protected void handleMessage(Message message) {
		switch (message.getMessageType()) {

		case ELEVATOR_PICK_UP_PASSENGER_REQUEST:
			ElevatorJobMessage job = (ElevatorJobMessage) message;
			System.out.println("Scheduler received floor request: go to floor " + job.getDestinationFloor() + "\n");
			handleElevatorPickUpPassengerRequest(job);
			break;

		default:
			break;
		}
	}

	/**
	 * Handle the elevator floor job
	 *
	 * @param elevatorFloorJob the elevator floor job
	 */
	private void handleElevatorPickUpPassengerRequest(ElevatorJobMessage elevatorFloorJob) {

		// If we do NOT have elevators, there is no point in continuing
		if (elevatorJobManagements.length == 0) {
			return;
		}
		// Considering load balancing, we will assign jobs to the most free and closest
		// elevator.
		//
		// We will assume that the first in-service elevator we find is the most free
		// and closest elevator. We will then iterate through the elevators, comparing
		// them to find the ideal elevator.
		//
		// If there are no in-service elevators, we will discard the request as we do
		// not know how long a elevator may be out of service.

		// Finding an in-service elevator
		ElevatorJobManagement assumedBestElevatorJobManagement = null;
		for (int i = 0; i < elevatorJobManagements.length; i++) {

			if (elevatorJobManagements[i].isReadyForJob()) {
				assumedBestElevatorJobManagement = elevatorJobManagements[i];
				break;
			}
		}

		// If we do not have an in-service elevator, we will discard the request and
		// provide a log
		if (assumedBestElevatorJobManagement == null) {
			System.out.println(
					"No Elevator is available...Scheduler is ingoring the received Passenger-Pick-Up REQUEST @ Floor "
							+ elevatorFloorJob.getDestinationFloor());
			return;
		}

		// Going through the elevators to find the ideal elevator
		for (int i = 0; i < elevatorJobManagements.length; i++) {
			ElevatorJobManagement currentElevatorJobManagement = elevatorJobManagements[i];

			boolean doesCurrentElevatorHaveEqualJobs = currentElevatorJobManagement
					.getNumberOfPrimaryJobs() == assumedBestElevatorJobManagement.getNumberOfPrimaryJobs();
			boolean doesCurrentElevatorHaveLessJobs = currentElevatorJobManagement
					.getNumberOfPrimaryJobs() <= assumedBestElevatorJobManagement.getNumberOfPrimaryJobs();

			boolean isCurrentElevatorInValidDirection = !currentElevatorJobManagement.isRunningJob()
					|| currentElevatorJobManagement.getElevatorDirection() == elevatorFloorJob.getDirection();
			boolean isAssumedBestElevatorInValidDirection = !assumedBestElevatorJobManagement.isRunningJob()
					|| assumedBestElevatorJobManagement.getElevatorDirection() == elevatorFloorJob.getDirection();

			int currentElevatorFloorDistance = Math
					.abs(currentElevatorJobManagement.getCurrentFloorNumber() - elevatorFloorJob.getDestinationFloor());
			int assumedBestFloorDistance = Math.abs(
					assumedBestElevatorJobManagement.getCurrentFloorNumber() - elevatorFloorJob.getDestinationFloor());
			boolean isCurrentElevatorFloorDistanceSmaller = currentElevatorFloorDistance < assumedBestFloorDistance;

			// If the assumed best elevator is not going in a valid direction and the
			// current elevator is, we will change the assumed best elevator
			if (!isAssumedBestElevatorInValidDirection && isCurrentElevatorInValidDirection) {
				assumedBestElevatorJobManagement = currentElevatorJobManagement;

			}

			// If we both elevators have valid directions, we change the assumed best
			// elevator based on other factors
			else if (isAssumedBestElevatorInValidDirection && isCurrentElevatorInValidDirection) {

				// if the current elevator is less busy, we will change the assumed best
				// elevator
				if (doesCurrentElevatorHaveLessJobs) {
					assumedBestElevatorJobManagement = currentElevatorJobManagement;
				}
				// if the current elevator is equally busy, we will change the assumed best
				// elevator if the current elevator is closer.
				else if (doesCurrentElevatorHaveEqualJobs && isCurrentElevatorFloorDistanceSmaller) {
					assumedBestElevatorJobManagement = currentElevatorJobManagement;
				}

			}

		}

		assumedBestElevatorJobManagement.addJob(elevatorFloorJob);

		// If the elevator is not currently running a job, we will update the elevator's
		// direction and issue the appropriate elevator commands
		if (!assumedBestElevatorJobManagement.isRunningJob()) {
			assumedBestElevatorJobManagement.setElevatorDirection(elevatorFloorJob.getDirection());
			executeNextElevatorCommand(assumedBestElevatorJobManagement);
		}
	}

}
