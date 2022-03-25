/**
 *
 */
package FloorSubsystem;

import common.SystemValidationUtil;
import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

/**
 * This class represents the floor elevator message Work Queue. Elevator
 * messages added to the queue will be handled by a worker.
 *
 * @author Jacob
 *
 */
public class FloorElevatorMessageWorkQueue extends MessageWorkQueue {

	private SubsystemCommunicationRPC schedulerSubsystemCommunication;
	private SubsystemCommunicationRPC elevatorSubsystemCommunication;
	private Floor[] floors;

	/**
	 * Constructor for FloorElevatorMessageWorkQueue
	 * 
	 * @param schedulerSubsystemCommunication the UDP system for floor to/from scheduler
	 * @param elevatorSubsystemCommunication the UDP system for floor to/from elevator
	 * @param floors			The list of all floors
	 */
	public FloorElevatorMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication,
			SubsystemCommunicationRPC elevatorSubsystemCommunication, Floor[] floors) {

		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.elevatorSubsystemCommunication = elevatorSubsystemCommunication;
		this.floors = floors;
	}
	
	/**
	 * Handle message method, receives a message and calls the corresponding switch case. Overrides MessageWorkQueue handleMessage method.
	 * 
	 * @param message the message to be handled
	 */
	@Override
	protected void handleMessage(Message message) {
		
		// As this is the FloorElevator work queue, the only message is a FloorElevatorTargetedMessage
		FloorElevatorTargetedMessage request = (FloorElevatorTargetedMessage) message;

		int floorId = request.getFloorId();
		int elevatorId = request.getElevatorId();

		// Validate the floor id
		if (!SystemValidationUtil.isFloorNumberInRange(floorId)) {
			return;
		}

		// Handle the request with the appropriate case
		switch (request.getMessageType()) {

		case ELEVATOR_FLOOR_SIGNAL_REQUEST:
			ElevatorFloorSignalRequestMessage floorSignalRequestMessage = (ElevatorFloorSignalRequestMessage) request;

			// Call request to notify that the elevator has arrived
			floors[floorId].notifyElevatorAtFloorArrival(floorId, elevatorId,
					floorSignalRequestMessage.getElevatorMotor(), elevatorSubsystemCommunication, schedulerSubsystemCommunication,
					floorSignalRequestMessage.isFloorFinalDestination());
			break;

		case ELEVATOR_LEAVING_FLOOR_MESSAGE:
			
			// Call request to notify that the elevator has left
			floors[floorId].elevatorLeavingFloor(elevatorId);
			break;

		default:
			break;
		}
	}

}
