package FloorSubsystem;

import common.SystemValidationUtil;
import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

/**
 * This class represents the floor elevator message Work Queue. Elevator messages
 * added to the queue will be handled by a worker.
 *
 * @author Jacob
 *
 */
public class FloorElevatorMessageWorkQueue extends MessageWorkQueue {
	
	private SubsystemCommunicationRPC schedulerSubsystemCommunication;
	private SubsystemCommunicationRPC elevatorSubsystemCommunication;
	private Floor[] floors;

	public FloorElevatorMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication, SubsystemCommunicationRPC elevatorSubsystemCommunication, Floor[] floors) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.elevatorSubsystemCommunication = elevatorSubsystemCommunication;
		this.floors = floors;
	}
	
	@Override
	protected void handleMessage(Message message) {
		
		FloorElevatorTargetedMessage request = (FloorElevatorTargetedMessage) message;
		
		int floorId = request.getFloorId();
		int elevatorId = request.getElevatorId();

		// Validate the floor id
		if (!SystemValidationUtil.isFloorNumberInRange(floorId)) {
			return;
		}

		switch (request.getRequestType()) {

		case ELEVATOR_FLOOR_SIGNAL_REQUEST:
			ElevatorFloorSignalRequestMessage floorSignalRequestMessage = (ElevatorFloorSignalRequestMessage) request;

			floors[floorId].notifyElevatorAtFloorArrival(floorId, elevatorId,
					floorSignalRequestMessage.getElevatorMotor(), elevatorSubsystemCommunication,
					floorSignalRequestMessage.isFloorFinalDestination());
			break;

		case ELEVATOR_LEAVING_FLOOR_MESSAGE:
			floors[floorId].elevatorLeavingFloor(elevatorId);
			break;

		default:
			break;
		}
	}

}
