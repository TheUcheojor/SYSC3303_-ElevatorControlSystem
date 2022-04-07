/**
 *
 */
package FloorSubsystem;

import java.util.ArrayList;
import java.util.logging.Logger;

import ElevatorSubsystem.ElevatorAutoFixing;
import common.LoggerWrapper;
import common.SimulationFloorInputData;
import common.SystemValidationUtil;
import common.messages.Message;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.PassengerDropoffCompletedMessage;
import common.messages.scheduler.SchedulerFloorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

/**
 * This class represents the floor scheduler message Work Queue. Scheduler
 * messages added to the queue will be handled by a worker.
 *
 * @author Jacob
 *
 */
public class FloorSchedulerMessageWorkQueue extends MessageWorkQueue {

	/**
	 * The scheduler system communication
	 */
	private SubsystemCommunicationRPC schedulerSubsystemCommunication;

	/**
	 * The elevator subsystem communication
	 */
	private SubsystemCommunicationRPC elevatorSubsystemCommunication;

	/**
	 * The floors
	 */
	private Floor[] floors;

	/**
	 * The floor data collection
	 */
	private ArrayList<SimulationFloorInputData> floorDataCollection;

	/**
	 * The logger
	 */
	private static Logger logger = LoggerWrapper.getLogger();

	/**
	 * The FloorSchedulerMessageWorkQueue constructor
	 *
	 * @param schedulerSubsystemCommunication The scheduler subsystem communication
	 * @param elevatorSubsystemCommunication  The elevator subsystem communication
	 * @param floors                          the floors
	 * @param floorDataCollection             the floor data collection
	 */
	public FloorSchedulerMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication,
			SubsystemCommunicationRPC elevatorSubsystemCommunication, Floor[] floors,
			ArrayList<SimulationFloorInputData> floorDataCollection) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.elevatorSubsystemCommunication = elevatorSubsystemCommunication;
		this.floors = floors;
		this.floorDataCollection = floorDataCollection;
	}

	/**
	 * Given the input data id, find the floor data
	 *
	 * @param inputDataId the input data id
	 * @return the input data
	 */
	private SimulationFloorInputData getFloorData(int inputDataId) {
		synchronized(floorDataCollection) {
			for (SimulationFloorInputData floorData : floorDataCollection) {
				if (floorData.getInputDataId() == inputDataId) {
					return floorData;
				}
			}
		}

		return null;
	}

	/**
	 * Handle message method, receives a message and calls the corresponding switch
	 * case. Overrides MessageWorkQueue handleMessage method.
	 *
	 * @param message the message to be handled
	 */
	@Override
	protected void handleMessage(Message message) {
		SchedulerFloorCommand request = (SchedulerFloorCommand) message;
		int floorId = request.getFloorId();

		// Validate the floor id
		if (!SystemValidationUtil.isFloorNumberInRange(floorId)) {
			return;
		}

		// Handle the given command with the appropriate case
		switch (request.getCommand()) {

		case TURN_OFF_FLOOR_LAMP:
			floors[floorId].turnOffLampButton(request.getLampButtonDirection());

			// Get the corresponding floor data
			SimulationFloorInputData floorData = getFloorData(request.getInputDataId());

			if (floorData == null) {
				logger.severe("(FLOOR) Floor data(id=" + request.getInputDataId() + ") could not be found.");
				return;
			}

			int destinationFloor = floorData.getDestinationFloorCarButton();
			FloorInputFault floorFault = floorData.getFault();
			ElevatorAutoFixing elevatorAutoFixing = floorData.getElevatorAutoFixing();
			int floorInputId = floorData.getInputDataId();

			int elevatorId = request.getElevatorId();

			ElevatorTransportRequest elevatorTransportRequest = new ElevatorTransportRequest(destinationFloor,
					elevatorId, request.getLampButtonDirection(), floorFault, elevatorAutoFixing, floorInputId);

			try {
				elevatorSubsystemCommunication.sendMessage(elevatorTransportRequest);
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		case PRODUCE_STUCK_FAULT_WITH_ELEVATOR:
			floors[floorId].setElevatorIdToFault(request.getElevatorId());
			break;
			
		case PASSENGER_DROP_OFF_COMPLETE:
			PassengerDropoffCompletedMessage passengerDropoffCompletedMessage = (PassengerDropoffCompletedMessage) request;
			
			synchronized (floorDataCollection) {
				floorDataCollection.removeIf(floorInputData -> passengerDropoffCompletedMessage.getFloorInputDataId()== floorInputData.getInputDataId());
			
			}
			break;

		default:
			break;
		}
	}

}
