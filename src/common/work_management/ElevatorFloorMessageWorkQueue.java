package common.work_management;

import ElevatorSubsystem.ElevatorCar;
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

public class ElevatorFloorMessageWorkQueue extends MessageWorkQueue {
	private SubsystemCommunicationRPC subsystemCommunicationScheduler;
	private SubsystemCommunicationRPC subsystemCommunicationFloor;

	public ElevatorFloorMessageWorkQueue(SubsystemCommunicationRPC subsystemCommunicationScheduler, SubsystemCommunicationRPC subsystemCommunicationFloor) {
		this.subsystemCommunicationScheduler = subsystemCommunicationScheduler;
		this.subsystemCommunicationFloor = subsystemCommunicationFloor;
	}
	
	@Override
	protected void handleMessage(Message message) {
		switch(((FloorElevatorTargetedMessage) message).getRequestType()) {
			case FLOOR_ARRIVAL_MESSAGE:
				ElevatorFloorArrivalMessage arrivalMessage = ((ElevatorFloorArrivalMessage) message);
				floorNumber = arrivalMessage.getFloorId();
				
				System.out.println("Elevator has reached floor: " + floorNumber);
				ElevatorStatusMessage arrivalStatus = elevators.get(message.getElevatorId()).createStatusMessage();
				subsystemCommunicationScheduler.sendMessage(arrivalStatus);
				break;
				
			default:
				break;
		}
	}
}
