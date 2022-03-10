package ElevatorSubsystem;

import java.util.List;
import java.util.Map;

import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorStatusRequest;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

public class ElevatorFloorMessageWorkQueue extends MessageWorkQueue {
	private SubsystemCommunicationRPC schedulerSubsystemCommunication;
	private SubsystemCommunicationRPC floorSubsystemCommunication;

	private Map<Integer, ElevatorCar> elevators;

	public ElevatorFloorMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication, Map<Integer, ElevatorCar> elevators) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.elevators = elevators;
	}
	
	@Override
	protected void handleMessage(Message message) {
		FloorElevatorTargetedMessage floorMessage = (FloorElevatorTargetedMessage) message;
		try {
			switch(floorMessage.getRequestType()) {
				
				case FLOOR_ARRIVAL_MESSAGE:
					ElevatorFloorArrivalMessage arrivalMessage = ((ElevatorFloorArrivalMessage) message);
					int floorNumber = arrivalMessage.getFloorId();
					
					System.out.println("Elevator has reached floor: " + floorNumber);
					ElevatorStatusMessage arrivalStatus = elevators.get(floorMessage.getElevatorId()).createStatusMessage();
				
					schedulerSubsystemCommunication.sendMessage(arrivalStatus);
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
