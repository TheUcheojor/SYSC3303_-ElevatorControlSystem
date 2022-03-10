package common.work_management;

import ElevatorSubsystem.ElevatorCar;
import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorStatusRequest;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;

public class ElevatorSchedulerMessageWorkQueue extends MessageWorkQueue{
	private SubsystemCommunicationRPC subsystemCommunicationScheduler;
	private SubsystemCommunicationRPC subsystemCommunicationFloor;
	
	public ElevatorSchedulerMessageWorkQueue(SubsystemCommunicationRPC subsystemCommunicationScheduler, SubsystemCommunicationRPC subsystemCommunicationFloor) {
		this.subsystemCommunicationScheduler = subsystemCommunicationScheduler;
		this.subsystemCommunicationFloor = subsystemCommunicationFloor;
	}

	@Override
	protected void handleMessage(Message message) {
		switch (message.getMessageType()) {

			case ELEVATOR_STATUS_REQUEST:
				ElevatorStatusRequest statusRequest = (ElevatorStatusRequest)message;
				subsystemCommunicationScheduler.appendMessage(elevators.get(statusRequest.getId()).createStatusMessage());
				break;
				
			case ELEVATOR_TRANSPORT_REQUEST:
				ElevatorTransportRequest transportRequest = (ElevatorTransportRequest) message;
				subsystemCommunicationScheduler.appendMessage(elevators.get(transportRequest.getElevatorId()).createStatusMessage());
				break;
				
			case SCHEDULER_ELEVATOR_COMMAND:
				SchedulerElevatorCommand schedulerCommand =(SchedulerElevatorCommand) message;
				handleElevatorCommand(schedulerCommand);
				ElevatorStatusMessage postCommandStatus = elevators.get(schedulerCommand.getElevatorID()).createStatusMessage();
				subsystemCommunicationScheduler.sendMessage(postCommandStatus);
				break;
				
			default:
				break;
			}
	}
	
	private void handleElevatorCommand(SchedulerElevatorCommand command) {
		ElevatorLeavingFloorMessage leavingMessage;
		ElevatorFloorSignalRequestMessage comingMessage;
		ElevatorCar car = elevators.get(command.getElevatorID());
		
		switch(command.getCommand()) {
			case STOP:
				if(!car.getDoor().isOpen()) {
					System.out.println("Elevator stopping\n.");
					car.getMotor().turnOff();
				}else {
					car.setErrorState(new Exception("Attempted to stop while doors open"));
				}
				break;
			case CLOSE_DOORS:
				System.out.println("Elevator door closing\n.");
				car.getDoor().closeDoor();
				break;
			case OPEN_DOORS:
				if(!car.getMotor().getIsRunning()) {
					System.out.println("Elevator door opening\n.");
					car.getDoor().openDoor();
				}else {
					car.setErrorState(new Exception("Attempted to open doors while motor running"));
				}
				break;
			case MOVE_UP:
				System.out.println("Elevator door closing\n.");
				car.getDoor().closeDoor();
				System.out.println("Elevator moving up\n.");
				car.getMotor().goUp();
				
				leavingMessage = new ElevatorLeavingFloorMessage(car.getId(), floorNumber);
				comingMessage = new ElevatorFloorSignalRequestMessage(car.getId(), floorNumber + 1, car.getMotor(), true);
				
				subsystemCommunicationFloor.sendMessage(leavingMessage);
				subsystemCommunicationFloor.sendMessage(comingMessage);
				break;
			case MOVE_DOWN:
				System.out.println("Elevator door closing\n.");
				car.getDoor().closeDoor();
				System.out.println("Elevator moving down\n.");
				car.getMotor().goDown();
				
				leavingMessage = new ElevatorLeavingFloorMessage(car.getId(), floorNumber);
				comingMessage = new ElevatorFloorSignalRequestMessage(car.getId(), floorNumber - 1, car.getMotor(), true);
				
				subsystemCommunicationFloor.sendMessage(leavingMessage);
				subsystemCommunicationFloor.sendMessage(comingMessage);
			
				break;
		}
	}
}
