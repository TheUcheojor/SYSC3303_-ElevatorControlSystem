package ElevatorSubsystem;

import java.util.Map;

import common.messages.Message;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorStatusRequest;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

public class ElevatorSchedulerMessageWorkQueue extends MessageWorkQueue{
	private SubsystemCommunicationRPC schedulerSubsystemCommunication;
	private SubsystemCommunicationRPC floorSubsystemCommunication;
	
	private Map<Integer, ElevatorCar> elevators;
	
	public ElevatorSchedulerMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication, SubsystemCommunicationRPC floorSubsystemCommunication, Map<Integer, ElevatorCar> elevators) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.floorSubsystemCommunication = floorSubsystemCommunication;
		this.elevators = elevators;
	}

	@Override
	protected void handleMessage(Message message) {
		try {
			switch (message.getMessageType()) {
	
				case ELEVATOR_STATUS_REQUEST:
					ElevatorStatusRequest statusRequest = (ElevatorStatusRequest)message;
					schedulerSubsystemCommunication.sendMessage(elevators.get(statusRequest.getId()).createStatusMessage());
					break;
					
				case ELEVATOR_TRANSPORT_REQUEST:
					ElevatorTransportRequest transportRequest = (ElevatorTransportRequest) message;
					schedulerSubsystemCommunication.sendMessage(elevators.get(transportRequest.getElevatorId()).createStatusMessage());
					break;
					
				case SCHEDULER_ELEVATOR_COMMAND:
					SchedulerElevatorCommand schedulerCommand =(SchedulerElevatorCommand) message;
					handleElevatorCommand(schedulerCommand);
					ElevatorStatusMessage postCommandStatus = elevators.get(schedulerCommand.getElevatorID()).createStatusMessage();
					schedulerSubsystemCommunication.sendMessage(postCommandStatus);
					break;
					
				default:
					break;
				}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleElevatorCommand(SchedulerElevatorCommand command) {
		ElevatorLeavingFloorMessage leavingMessage;
		ElevatorFloorSignalRequestMessage comingMessage;
		ElevatorCar car = elevators.get(command.getElevatorID());
		int carFloorNumber = car.getFloorNumber();
		
		try {
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
					
					leavingMessage = new ElevatorLeavingFloorMessage(car.getId(), carFloorNumber);
					comingMessage = new ElevatorFloorSignalRequestMessage(car.getId(), carFloorNumber + 1, car.getMotor(), true);
					
					floorSubsystemCommunication.sendMessage(leavingMessage);
					floorSubsystemCommunication.sendMessage(comingMessage);
					break;
				case MOVE_DOWN:
					System.out.println("Elevator door closing\n.");
					car.getDoor().closeDoor();
					System.out.println("Elevator moving down\n.");
					car.getMotor().goDown();
					
					leavingMessage = new ElevatorLeavingFloorMessage(car.getId(), carFloorNumber);
					comingMessage = new ElevatorFloorSignalRequestMessage(car.getId(), carFloorNumber - 1, car.getMotor(), true);

					floorSubsystemCommunication.sendMessage(leavingMessage);
					floorSubsystemCommunication.sendMessage(comingMessage);
				
					break;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}