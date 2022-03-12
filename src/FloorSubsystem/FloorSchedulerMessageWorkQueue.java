package FloorSubsystem;

import java.util.ArrayList;

import common.SimulationFloorInputData;
import common.SystemValidationUtil;
import common.messages.Message;
import common.messages.elevator.ElevatorTransportRequest;
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

	private SubsystemCommunicationRPC schedulerSubsystemCommunication;
	private SubsystemCommunicationRPC elevatorSubsystemCommunication;
	private Floor[] floors;
	private ArrayList<SimulationFloorInputData> assignedFloorDataCollection;

	public FloorSchedulerMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication,
			SubsystemCommunicationRPC elevatorSubsystemCommunication, Floor[] floors,
			ArrayList<SimulationFloorInputData> assignedFloorDataCollection) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.elevatorSubsystemCommunication = elevatorSubsystemCommunication;
		this.floors = floors;
		this.assignedFloorDataCollection = assignedFloorDataCollection;
	}

	/**
	 * Given the elevator direction, returns a floor's list of passenger
	 * destinations (car buttons pressed)
	 *
	 * @param floorId           the floor id
	 * @param elevatorDirection
	 * @return
	 */
	private int getFloorPassengerDestinationFloor(int inputDataId) {
		for (SimulationFloorInputData floorData : assignedFloorDataCollection) {
			if (floorData.getInputDataId() == inputDataId) {
				return floorData.getDestinationFloorCarButton();
			}
		}

		return -1;
	}

	@Override
	protected void handleMessage(Message message) {
		SchedulerFloorCommand request = (SchedulerFloorCommand) message;
		int floorId = request.getFloorId();

		// Validate the floor id
		if (!SystemValidationUtil.isFloorNumberInRange(floorId)) {
			return;
		}

		switch (request.getCommand()) {

		case TURN_OFF_FLOOR_LAMP:
			floors[floorId].turnOffLampButton(request.getLampButtonDirection());

			int destinationFloor = getFloorPassengerDestinationFloor(request.getInputDataId());
			int elevatorId = request.getElevatorId();

			ElevatorTransportRequest elevatorTransportRequest = new ElevatorTransportRequest(destinationFloor,
					elevatorId, request.getLampButtonDirection());
			try {
				elevatorSubsystemCommunication.sendMessage(elevatorTransportRequest);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		default:
			break;
		}
	}

}
