/**
 *
 */
package Scheduler;

import java.util.ArrayList;
import java.util.logging.Logger;

import common.Direction;
import common.LoggerWrapper;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageType;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.floor.ElevatorFloorRequest;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.FloorCommand;
import common.messages.scheduler.PassengerDropoffCompletedMessage;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.messages.scheduler.SchedulerFloorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

/**
 * * This class represents the scheduler's work queue handler.
 *
 * @author paulokenne, ryanfife
 *
 */
public abstract class SchedulerWorkHandler extends MessageWorkQueue {
	private Logger logger = LoggerWrapper.getLogger();

	/**
	 * The job management for each elevator
	 */
	protected ElevatorJobManagement[] elevatorJobManagements;

	/**
	 * The UDP communication between the scheduler and floor
	 */
	protected SubsystemCommunicationRPC schedulerFloorCommunication;

	/**
	 * The UDP communication between the scheduler and elevator
	 */
	protected SubsystemCommunicationRPC schedulerElevatorCommunication;

	/**
	 * The SchedulerFloorMessageWorkQueue constructor
	 *
	 * @param schedulerFloorCommunication    the scheduler floor UDP communication
	 * @param schedulerElevatorCommunication the scheduler elevator UDP
	 *                                       communication
	 * @param elevatorJobManagements
	 */
	public SchedulerWorkHandler(SubsystemCommunicationRPC schedulerFloorCommunication,
			SubsystemCommunicationRPC schedulerElevatorCommunication, ElevatorJobManagement[] elevatorJobManagements) {
		this.schedulerFloorCommunication = schedulerFloorCommunication;
		this.schedulerElevatorCommunication = schedulerElevatorCommunication;
		this.elevatorJobManagements = elevatorJobManagements;
	}

	/**
	 * Handle the given message appropriately
	 *
	 * @param message the given message
	 */
	@Override
	protected abstract void handleMessage(Message message);

	/**
	 * Execute the next command for the elevator
	 *
	 * @param elevatorJobManagement the elevator job management
	 */
	protected void executeNextElevatorCommand(ElevatorJobManagement elevatorJobManagement) {

		int nearestTargetFloor = -1;
		switch (elevatorJobManagement.getElevatorDirection()) {

		case UP:
			nearestTargetFloor = elevatorJobManagement.getSmallestDestinationFloorInElevatorDirection();
			break;

		case DOWN:
			nearestTargetFloor = elevatorJobManagement.getLargestDestinationFloorInElevatorDirection();
			break;

		default:
			break;
		}

		if (nearestTargetFloor != -1) {
			handleElevatorBehavior(elevatorJobManagement, nearestTargetFloor);
		} else {
			// This should never happen...If it ever occurs, we need to know
			logger.severe("Invalid Nearest Target Floor! Some thing is wrong...");
		}

	}

	/**
	 * Given an elevator management and the nearest target floor, handle the
	 * elevator's behavior
	 *
	 * @param elevatorJobManagement the elevator management
	 * @param nearestTargetFloor    the nearest target floor
	 */
	private void handleElevatorBehavior(ElevatorJobManagement elevatorJobManagement, int nearestTargetFloor) {
		int elevatorId = elevatorJobManagement.getElevatorId();

		try {
			// Move down if we above the target floor
			if (elevatorJobManagement.getCurrentFloorNumber() > nearestTargetFloor) {
				logger.fine("(SCHEDULER) Sending a DOWN command to Elevator " + elevatorId);

				schedulerElevatorCommunication
						.sendMessage(new SchedulerElevatorCommand(ElevatorCommand.MOVE_DOWN, elevatorId));

			}
			// Move up if we below the target floor
			else if (elevatorJobManagement.getCurrentFloorNumber() < nearestTargetFloor) {
				logger.fine("(SCHEDULER) Sending an UP command to Elevator " + elevatorId);
				schedulerElevatorCommunication
						.sendMessage(new SchedulerElevatorCommand(ElevatorCommand.MOVE_UP, elevatorId));

			} else {
				// If we are at a target floor, take action
				// Find all the jobs at this floor and address them appropriately
				ArrayList<ElevatorJobMessage> jobsAtTargetFloor = elevatorJobManagement
						.getPrimaryJobsAtFloor(nearestTargetFloor);

				// If we have one ELEVATOR_PICK_UP_PASSENGER_REQUEST, we turn off the
				// corresponding floor lamps.
				boolean expectingElevatorButtonPress = false;
				for (ElevatorJobMessage elevatorJob : jobsAtTargetFloor) {

					switch (elevatorJob.getMessageType()) {
					
					case ELEVATOR_PICK_UP_PASSENGER_REQUEST:
						ElevatorFloorRequest elevatorFloorRequestJob = (ElevatorFloorRequest) elevatorJob;

						schedulerFloorCommunication
								.sendMessage(new SchedulerFloorCommand(FloorCommand.TURN_OFF_FLOOR_LAMP,
										nearestTargetFloor, elevatorFloorRequestJob.getDirection(), elevatorId,
										elevatorFloorRequestJob.getFloorInputId()));

						expectingElevatorButtonPress = true;

						break;
						
					case ELEVATOR_DROP_PASSENGER_REQUEST:
						ElevatorTransportRequest elevatorTransportRequest = (ElevatorTransportRequest) elevatorJob;
						int floorInputDataId = elevatorTransportRequest.getFloorInputId();
						
						schedulerFloorCommunication
						.sendMessage(new PassengerDropoffCompletedMessage(floorInputDataId));
						break;
					
					}
						
				}

				String addressedJobMessage = "(SCHEDULER) Elevator " + elevatorId + " has addressed: ";
				for (ElevatorJobMessage job : jobsAtTargetFloor) {
					addressedJobMessage += "(" + job.getMessageType() + " job - " + job.getDirection() + " @ floor = "
							+ job.getDestinationFloor() + ")";
				}
				logger.fine(addressedJobMessage);
				// Stop the elevator and open the doors
				schedulerElevatorCommunication
						.sendMessage(new SchedulerElevatorCommand(ElevatorCommand.STOP, elevatorId));
				schedulerElevatorCommunication
						.sendMessage(new SchedulerElevatorCommand(ElevatorCommand.OPEN_DOORS, elevatorId));
				
				// We can delete these jobs as we know we have addressed them
				elevatorJobManagement.removeJobs(jobsAtTargetFloor);

				// If the elevator has no more primary jobs, we will try to focus on secondary
				// jobs. Secondary jobs are jobs that are in the opposite direction of the
				// elevator. To change the elevator's focus, we load secondary jobs by setting
				// the elevator direction to be the opposite of what it currently is.
				if (!elevatorJobManagement.hasPrimaryJobs()) {

					// An elevator button press is a potential primary job and hence, we will not
					// load secondary jobs if we expect one.
					if (!expectingElevatorButtonPress && elevatorJobManagement.hasSecondaryJobs()) {
						elevatorJobManagement.loadSecondaryJobs();

						// We start focusing on address the other jobs
						executeNextElevatorCommand(elevatorJobManagement);
					} else {
						// If there are no secondary jobs, we will set the elevator's direction to idle
						elevatorJobManagement.setElevatorDirection(Direction.IDLE);
					}
				}
			}

		} catch (Exception e) {
			logger.severe("Error occurred in handleElevatorBehavior: " + e);
		}
	}
	
	/**
	 * Given the shutdown elevator, notifies the floor subsystem about the completed messages
	 * @param elevator, the elevator job manager
	 */
	protected void notifyElevatorShutdownCompletedJobs(ElevatorJobManagement elevator) {

		// If the elevator shuts down, notify the floor subsystem that we have addressed
		// the jobs in the elevators
		if (!elevator.isReadyForJob()) {

			// For each job, notify the floor subsystem
			elevator.getElevatorJobs().forEach(elevatorJob -> {
				try {
					schedulerFloorCommunication
					.sendMessage(new PassengerDropoffCompletedMessage(elevatorJob.getFloorInputId()));
				} catch (Exception e) {
					e.printStackTrace();
				}					});
		}

	}

}
