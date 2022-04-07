/**
 *
 */
package Scheduler;

import java.util.logging.Logger;

import FloorSubsystem.FloorInputFault;
import common.LoggerWrapper;
import common.exceptions.ElevatorStateException;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.floor.ElevatorFloorRequest;
import common.messages.floor.ElevatorNotArrived;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.FloorCommand;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.messages.scheduler.SchedulerFloorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;

/**
 * This class represents the scheduler floor work queue handler. Floor messages
 * added to the queue will be handled by a worker.
 *
 * @author paulokenne
 *
 */
public class SchedulerFloorWorkHandler extends SchedulerWorkHandler {
	private Logger logger = LoggerWrapper.getLogger();

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
			logger.info("(SCHEDULER) ->>> Pickup passenger at floor " + job.getDestinationFloor());
			handleElevatorPickUpPassengerRequest(job);
			break;
			
		case STUCK_AT_FLOOR_FAULT:
			ElevatorNotArrived stuckMessage = (ElevatorNotArrived) message;
			logger.severe("(SCHEDULER) Floor " + stuckMessage.getFloorNumber() + " never received elevator " + stuckMessage.getElevatorId() + 
					", elevator must be stuck");
			logger.info("(SCHEDULER) Shutting down elevator " + stuckMessage.getElevatorId());
			
			try {
				ElevatorStateException exception = new ElevatorStateException(FloorInputFault.STUCK_AT_FLOOR_FAULT, stuckMessage.getFloorNumber(), "Elevator is stuck");
				elevatorJobManagements[stuckMessage.getElevatorId()].setErrorState(exception);
				
				notifyElevatorShutdownCompletedJobs(elevatorJobManagements[stuckMessage.getElevatorId()]);
				
				schedulerElevatorCommunication.sendMessage(new SchedulerElevatorCommand(ElevatorCommand.SHUT_DOWN, stuckMessage.getElevatorId()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e.toString());
			}

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

		synchronized (elevatorJobManagements) {

			for (int i = 0; i < elevatorJobManagements.length; i++) {

				if (elevatorJobManagements[i].isReadyForJob()) {
					assumedBestElevatorJobManagement = elevatorJobManagements[i];
					break;
				}
			}

			// If we do not have an in-service elevator, we will discard the request and
			// provide a log
			if (assumedBestElevatorJobManagement == null) {
				logger.fine(
						"No Elevator is available...Scheduler is ingoring the received Passenger-Pick-Up REQUEST @ Floor "
								+ elevatorFloorJob.getDestinationFloor());
				return;
			}

			// Going through the elevators to find the ideal elevator
			for (int i = 0; i < elevatorJobManagements.length; i++) {
				ElevatorJobManagement currentElevatorJobManagement = elevatorJobManagements[i];

				// If the current elevator job management is not ready for a job, there is no
				// reason to compare it with the assumed best elevator
				if (!currentElevatorJobManagement.isReadyForJob())
					continue;

				boolean doesCurrentElevatorHaveEqualJobs = currentElevatorJobManagement
						.getNumberOfJobs() == assumedBestElevatorJobManagement.getNumberOfJobs();
				boolean doesCurrentElevatorHaveLessJobs = currentElevatorJobManagement
						.getNumberOfJobs() < assumedBestElevatorJobManagement.getNumberOfJobs();

				boolean isCurrentElevatorInValidDirection = !currentElevatorJobManagement.isRunningJob()
						|| currentElevatorJobManagement.getElevatorDirection() == elevatorFloorJob.getDirection();
				boolean isAssumedBestElevatorInValidDirection = !assumedBestElevatorJobManagement.isRunningJob()
						|| assumedBestElevatorJobManagement.getElevatorDirection() == elevatorFloorJob.getDirection();

				int currentElevatorFloorDistance = Math.abs(
						currentElevatorJobManagement.getCurrentFloorNumber() - elevatorFloorJob.getDestinationFloor());
				int assumedBestFloorDistance = Math.abs(assumedBestElevatorJobManagement.getCurrentFloorNumber()
						- elevatorFloorJob.getDestinationFloor());
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
			ElevatorFloorRequest floorRequest = (ElevatorFloorRequest) elevatorFloorJob;
			
			try {
				// if we want to produce a elevator stuck fault, inform floor what elevator to stop
				if(floorRequest.getFault() == FloorInputFault.STUCK_AT_FLOOR_FAULT && floorRequest.getFaultFloorNumber() >= 0) {
					schedulerFloorCommunication.sendMessage(
							new SchedulerFloorCommand(FloorCommand.PRODUCE_STUCK_FAULT_WITH_ELEVATOR,
									floorRequest.getFaultFloorNumber(),
									assumedBestElevatorJobManagement.getElevatorId()));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e.toString());
			}

			logger.fine("(SCHEDULER) Assigning PICK_UP_PASSENGER Job (Direction = "
					+ elevatorFloorJob.getDirection() + " @ floor = " + elevatorFloorJob.getDestinationFloor()
					+ ") to Elevator " + assumedBestElevatorJobManagement.getElevatorId());

			// If the elevator is not currently running a job, we will update the elevator's
			// direction and issue the appropriate elevator commands
			if (!assumedBestElevatorJobManagement.isRunningJob()) {
				assumedBestElevatorJobManagement.setElevatorDirection(elevatorFloorJob.getDirection());
				executeNextElevatorCommand(assumedBestElevatorJobManagement);
			}

		}

	}

}
