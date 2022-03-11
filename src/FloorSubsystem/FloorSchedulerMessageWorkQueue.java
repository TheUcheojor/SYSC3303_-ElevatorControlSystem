package FloorSubsystem;

import java.util.ArrayList;

import common.Direction;
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
	private ArrayList<Integer> getFloorPassengerDestinationFloors(int floorId, Direction elevatorDirection) {

		ArrayList<Integer> destinationFloors = new ArrayList<>();

		assignedFloorDataCollection.forEach(floorData -> {
			// Check whether the input data is at the required floor and the requested
			// direction is the same
			if (floorData.getCurrentFloor() == floorId && floorData.getFloorDirectionButton() == elevatorDirection) {
				destinationFloors.add(floorData.getDestinationFloorCarButton());
			}

		});

		return destinationFloors;
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

			ArrayList<Integer> destinationFloors = getFloorPassengerDestinationFloors(floorId,
					request.getLampButtonDirection());
			// For now, I will only send the one item in the destination floor collection
			// for this iteration.
			// The elevator and scheduler do not support more than one at the moment.
			int elevatorId = request.getElevatorId();

			System.out.println("TURN_OFF_FLOOR_LAMP");

			for (int destinationFloor : destinationFloors) {
				ElevatorTransportRequest elevatorTransportRequest = new ElevatorTransportRequest(destinationFloor,
						elevatorId, request.getLampButtonDirection());
				try {
					elevatorSubsystemCommunication.sendMessage(elevatorTransportRequest);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			break;

		default:
			break;
		}
	}

}
