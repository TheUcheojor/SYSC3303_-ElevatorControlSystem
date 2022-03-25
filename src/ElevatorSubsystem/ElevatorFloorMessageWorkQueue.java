package ElevatorSubsystem;

import java.util.Map;
import java.util.logging.Logger;

import FloorSubsystem.FloorInputFault;
import common.LoggerWrapper;
import common.exceptions.ElevatorStateException;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

/**
 * This class serves the elevator related requests received from the floor
 * subsystem.
 *
 * @author Ryan Fife
 */
public class ElevatorFloorMessageWorkQueue extends MessageWorkQueue {
	private SubsystemCommunicationRPC schedulerSubsystemCommunication;
	private Logger logger = LoggerWrapper.getLogger();

	private Map<Integer, ElevatorCar> elevators;

	public ElevatorFloorMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication,
			Map<Integer, ElevatorCar> elevators) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.elevators = elevators;
	}

	/**
	 * This method handles messages received from the floor subsystem
	 */
	@Override
	protected void handleMessage(Message message) {
		try {
			ElevatorCar car;
			switch (message.getMessageType()) {

			case FLOOR_ARRIVAL_MESSAGE:
				ElevatorFloorArrivalMessage arrivalMessage = ((ElevatorFloorArrivalMessage) message);
				int carId = arrivalMessage.getElevatorId();
				int floorNumber = arrivalMessage.getFloorId();

				car = elevators.get(carId);
				car.setFloorNumber(floorNumber);

				logger.info("(ELEVATOR) Elevator " + carId + " has reached floor: " + floorNumber);
				ElevatorStatusMessage arrivalStatus = car.createStatusMessage();

				schedulerSubsystemCommunication.sendMessage(arrivalStatus);
				break;

			case ELEVATOR_DROP_PASSENGER_REQUEST:
				ElevatorTransportRequest elevatorTransportRequest = (ElevatorTransportRequest) message;

				int elevatorId = elevatorTransportRequest.getElevatorId();
				car = elevators.get(elevatorId);

				if (elevatorTransportRequest.getFloorFault() == FloorInputFault.DOOR_STUCK_OPEN_FAULT) {
					car.setErrorState(new ElevatorStateException(FloorInputFault.DOOR_STUCK_OPEN_FAULT, elevatorId,
							"The elevator car (id= " + elevatorId + ") door is stuck"));
					car.setAutoFixing(elevatorTransportRequest.getAutoFixing());
				}

				// Receive the message sent and set the errorOverride flag if needed
				schedulerSubsystemCommunication.sendMessage(message);
				break;

			default:
				logger.fine("(ELEVATOR) received improper message from FLOOR of type: " + message.getMessageType());
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
