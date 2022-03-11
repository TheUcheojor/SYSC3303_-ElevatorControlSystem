/**
 *
 */
package Scheduler;

import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.remote_procedure.SubsystemCommunicationRPC;

/**
 * @author paulokenne
 *
 */
public class SchedulerElevatorWorkHandler extends SchedulerWorkHandler {

	/**
	 * The SchedulerFloorMessageWorkQueue constructor
	 *
	 * @param schedulerFloorCommunication    the scheduler floor UDP communication
	 * @param schedulerElevatorCommunication the scheduler elevator UDP
	 *                                       communication
	 * @param elevatorJobManagements         the elevator job managements
	 */
	public SchedulerElevatorWorkHandler(SubsystemCommunicationRPC schedulerFloorCommunication,
			SubsystemCommunicationRPC schedulerElevatorCommunication, ElevatorJobManagement[] elevatorJobManagements) {
		super(schedulerFloorCommunication, schedulerElevatorCommunication, elevatorJobManagements);
	}

	@Override
	protected void handleMessage(Message message) {

		int elevatorId;
		switch (message.getMessageType()) {

		case ELEVATOR_STATUS_MESSAGE:
			synchronized (elevatorJobManagements) {
				ElevatorStatusMessage elevatorStatusMessage = (ElevatorStatusMessage) message;
				elevatorId = elevatorStatusMessage.getElevatorId();

				elevatorJobManagements[elevatorId].setCurrentFloorNumber(elevatorStatusMessage.getFloorNumber());

				// For now, if the elevator goes into an error state we will hold its job
				// requests. Once manual actions have be taken on the elevator, the elevator
				// should proceed like normal.
				elevatorJobManagements[elevatorId].setErrorState(elevatorStatusMessage.getErrorState());
				if (elevatorStatusMessage.getErrorState() != null) {
					elevatorJobManagements[elevatorId].setReadyForJob(false);
				} else {
					elevatorJobManagements[elevatorId].setReadyForJob(true);
				}

				System.out.println("Scheduler set internal elevator status: [EF: "
						+ elevatorStatusMessage.getFloorNumber() + ", ED: " + elevatorStatusMessage.getDirection()
						+ ", EID: " + elevatorId + ", ES:" + elevatorStatusMessage.getErrorState() + " ]\n");

				if (elevatorJobManagements[elevatorId].hasJobs()) {
					executeNextElevatorCommand(elevatorJobManagements[elevatorId]);
				}
			}
			break;

		case ELEVATOR_DROP_PASSENGER_REQUEST:
			ElevatorTransportRequest dropPassengerRequest = ((ElevatorTransportRequest) message);
			elevatorId = dropPassengerRequest.getElevatorId();

			synchronized (elevatorJobManagements) {

				// If the elevators has no jobs (direction is IDLE), we will update the elevator
				// direction
				if (!elevatorJobManagements[elevatorId].hasJobs()) {
					elevatorJobManagements[elevatorId].setElevatorDirection(dropPassengerRequest.getDirection());
				}

				elevatorJobManagements[elevatorId].addJob((ElevatorJobMessage) message);

				executeNextElevatorCommand(elevatorJobManagements[elevatorId]);
			}

			break;
		default:
			break;
		}
	}

}
