/**
 *
 */
package Scheduler;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

/**
 * This class represents the scheduler's floor message work queue. Floor
 * messages added to the queue will be handled by a worker.
 *
 * @author paulokenne
 *
 */
public class SchedulerFloorMessageWorkQueue extends MessageWorkQueue {

	/**
	 * The UDP communication between the scheduler and floor
	 */
	private SubsystemCommunicationRPC schedulerFloorCommunication;

	/**
	 * The UDP communication between the scheduler and elevator
	 */
	private SubsystemCommunicationRPC schedulerElevatorCommunication;

	/**
	 * The job management for each elevator
	 */
	private ElevatorJobManagement[] elevatorJobManagements;

	/**
	 * The SchedulerFloorMessageWorkQueue constructor
	 *
	 * @param schedulerFloorCommunication    the scheduler floor UDP communication
	 * @param schedulerElevatorCommunication the scheduler elevator UDP
	 *                                       communication
	 * @param elevatorJobManagements
	 */
	public SchedulerFloorMessageWorkQueue(SubsystemCommunicationRPC schedulerFloorCommunication,
			SubsystemCommunicationRPC schedulerElevatorCommunication, ElevatorJobManagement[] elevatorJobManagements) {
		this.schedulerFloorCommunication = schedulerFloorCommunication;
		this.schedulerElevatorCommunication = schedulerElevatorCommunication;
		this.elevatorJobManagements = elevatorJobManagements;
	}

	@Override
	protected void handleMessage(Message message) {
		switch (message.getMessageType()) {

		case ELEVATOR_FLOOR_REQUEST:
			ElevatorJobMessage job = (ElevatorJobMessage) message;
			System.out.println("Scheduler received floor request: go to floor " + job.getDestinationFloor() + "\n");

			// Iterate through the ElevatorJobManagement

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
	private void handleElevatorFloorRequest(ElevatorJobMessage elevatorFloorJob) {

		// If we do have elevators, there is no point in continuing
		if (elevatorJobManagements.length != 0) {
			return;
		}
		// Considering load balancing, we will assign jobs to the most free and closest
		// elevator up to a threshold. If the elevator is too busy, we will try not give
		// it other jobs.
		//
		// We will assume that the first elevator is the most free and closest
		// elevator. We will then iterate through the elevator, comparing them to find
		// the ideal elevator.
		ElevatorJobManagement elevatorJobManagement = elevatorJobManagements[0];

		for (int i = 0; i < elevatorJobManagements.length; i++) {
			ElevatorJobManagement currentElevatorJobManagement = elevatorJobManagements[i];

			int currentElevatorFloorDistance = Math
					.abs(currentElevatorJobManagement.getCurrentFloorNumber() - elevatorFloorJob.getDestinationFloor());

			int assumedBestfloorDistance = Math
					.abs(elevatorJobManagement.getCurrentFloorNumber() - elevatorFloorJob.getDestinationFloor());

			if (!elevatorJobManagement.isAtElevatorJobThreshold()
					// Check that the elevator is idle or going in the same direction
					&& (currentElevatorJobManagement.getElevatorDirection() == Direction.IDLE
							|| currentElevatorJobManagement.getElevatorDirection() == elevatorFloorJob.getDirection())
					// Check if the current elevator floor distance is favorable
					&& (currentElevatorFloorDistance < assumedBestfloorDistance)) {

				elevatorJobManagement = currentElevatorJobManagement;
			}

		}

		elevatorJobManagement.addJob(elevatorFloorJob);
		if (!elevatorJobManagement.isRunningJob()) {
			executeNextElevatorCommand(elevatorJobManagement);
		}
	}

	/**
	 * Execute the next command for the elevator
	 *
	 * @param elevatorJobManagement the elevator job management
	 */
	private void executeNextElevatorCommand(ElevatorJobManagement elevatorJobManagement) {

	}

}
