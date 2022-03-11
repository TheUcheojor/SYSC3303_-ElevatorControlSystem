package ElevatorSubsystem;

import java.util.Map;

import common.messages.Message;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;
/**
 * This class handles the communication between the elevator and floor subsystem
 * @author Ryan Fife
 *
 */
public class ElevatorFloorMessageWorkQueue extends MessageWorkQueue {
	private SubsystemCommunicationRPC schedulerSubsystemCommunication;

	private Map<Integer, ElevatorCar> elevators;

	public ElevatorFloorMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication, Map<Integer, ElevatorCar> elevators) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.elevators = elevators;
	}
	
	@Override
	/**
	 * This method handles messages received from the floor subsystem
	 */
	protected void handleMessage(Message message) {
		try {
			switch(message.getMessageType()) {
				
				case FLOOR_ARRIVAL_MESSAGE:
					ElevatorFloorArrivalMessage arrivalMessage = ((ElevatorFloorArrivalMessage) message);
					int floorNumber = arrivalMessage.getFloorId();
					
					System.out.println("Elevator has reached floor: " + floorNumber);
					ElevatorStatusMessage arrivalStatus = elevators.get(arrivalMessage.getElevatorId()).createStatusMessage();
				
					schedulerSubsystemCommunication.sendMessage(arrivalStatus);
					break;
					
				case ELEVATOR_DROP_PASSENGER_REQUEST:
					ElevatorTransportRequest transportRequest = (ElevatorTransportRequest) message;
					schedulerSubsystemCommunication.sendMessage(message);
					schedulerSubsystemCommunication.sendMessage(elevators.get(transportRequest.getElevatorId()).createStatusMessage());
					break;
					
				default:
					break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
