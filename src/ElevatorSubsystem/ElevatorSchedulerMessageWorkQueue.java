package ElevatorSubsystem;

import java.util.Map;
import java.util.logging.Logger;

import FloorSubsystem.FloorInputFault;
import common.LoggerWrapper;
import common.exceptions.ElevatorStateException;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.elevator.ElevatorStatusRequest;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.work_management.MessageWorkQueue;

/**
 * This class serves the elevator related requests received from the scheduler
 * subsystem.
 *
 * @author Ryan Fife
 */
public class ElevatorSchedulerMessageWorkQueue extends MessageWorkQueue {

	/**
	 * The scheduler subsystem communication
	 */
	private SubsystemCommunicationRPC schedulerSubsystemCommunication;

	/**
	 * The floor subsystem communication
	 */
	private SubsystemCommunicationRPC floorSubsystemCommunication;

	/**
	 * The logger
	 */
	private Logger logger = LoggerWrapper.getLogger();

	/**
	 * The elevators
	 */
	private Map<Integer, ElevatorCar> elevators;

	/**
	 * The ElevatorSchedulerMessageWorkQueue constructor
	 *
	 * @param schedulerSubsystemCommunication The scheduler subsystem communication
	 * @param floorSubsystemCommunication     The floor subsystem communication
	 * @param elevators                       the elevators
	 */
	public ElevatorSchedulerMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication,
			SubsystemCommunicationRPC floorSubsystemCommunication, Map<Integer, ElevatorCar> elevators) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.floorSubsystemCommunication = floorSubsystemCommunication;
		this.elevators = elevators;
	}

	/**
	 * This method handles the messages communicated between the elevator and
	 * scheduler subsystem
	 *
	 * @param - The message to be handled
	 */
	@Override
	protected void handleMessage(Message message) {
		try {
			switch (message.getMessageType()) {

			case ELEVATOR_STATUS_REQUEST:
				ElevatorStatusRequest statusRequest = (ElevatorStatusRequest) message;
				schedulerSubsystemCommunication.sendMessage(elevators.get(statusRequest.getId()).createStatusMessage());
				break;

			case SCHEDULER_ELEVATOR_COMMAND:
				SchedulerElevatorCommand schedulerCommand = (SchedulerElevatorCommand) message;
				handleElevatorCommand(schedulerCommand);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method handles elevator commands
	 *
	 * @param command - The command received from the scheduler
	 */
	private void handleElevatorCommand(SchedulerElevatorCommand command) {
		ElevatorLeavingFloorMessage leavingMessage;
		ElevatorFloorSignalRequestMessage comingMessage;

		int elevatorId = command.getElevatorID();
		ElevatorCar car = elevators.get(elevatorId);

		int carFloorNumber = car.getFloorNumber();

		try {
			switch (command.getCommand()) {
			case STOP:
				if (!car.getDoor().isOpen()) {
					logger.fine("(ELEVATOR) Elevator " + elevatorId + " stopping");
					car.getMotor().turnOff();
				} else {
					car.setErrorState(new ElevatorStateException(null, "Attempted to stop while doors open"));
				}
				break;
			case CLOSE_DOORS:
				logger.fine("(ELEVATOR) Elevator " + elevatorId + " door closing");
				car.getDoor().closeDoor();
				break;
			case OPEN_DOORS:
				if (!car.getMotor().getIsRunning()) {
					logger.fine("(ELEVATOR) Elevator " + elevatorId + " door opening");
					car.getDoor().openDoor();
				} else {
					car.setErrorState(new ElevatorStateException(null, "Attempted to open doors while motor running"));
				}

				break;
			case MOVE_UP:
				logger.fine("(ELEVATOR) Elevator " + elevatorId + " door closing");
				boolean isCloseDoorProcessSuccess = closeDoorProcess(car, elevatorId);

				if (!isCloseDoorProcessSuccess)
					return;

				logger.fine("(ELEVATOR) Elevator " + elevatorId + " moving up");
				car.getMotor().goUp();

				leavingMessage = new ElevatorLeavingFloorMessage(elevatorId, carFloorNumber);
				comingMessage = new ElevatorFloorSignalRequestMessage(car.getId(), carFloorNumber + 1, car.getMotor(),
						true);

				floorSubsystemCommunication.sendMessage(leavingMessage);
				floorSubsystemCommunication.sendMessage(comingMessage);

				break;
			case MOVE_DOWN:
				logger.fine("(ELEVATOR) Elevator " + elevatorId + " door closing");
				closeDoorProcess(car, elevatorId);

				logger.fine("(ELEVATOR) Elevator " + elevatorId + " moving down");
				car.getMotor().goDown();

				leavingMessage = new ElevatorLeavingFloorMessage(elevatorId, carFloorNumber);
				comingMessage = new ElevatorFloorSignalRequestMessage(elevatorId, carFloorNumber - 1, car.getMotor(),
						true);

				floorSubsystemCommunication.sendMessage(leavingMessage);
				floorSubsystemCommunication.sendMessage(comingMessage);

				break;

			case SHUT_DOWN:
				car.setInService(false);
				car.setErrorState(command.getException());
				break;

			case RESTART:
				car.setInService(true);
				car.setErrorState(null);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The close door process
	 *
	 * @param car        the car
	 * @param elevatorId the elevator id
	 * @return true if the close door process and false otherwise
	 */
	private boolean closeDoorProcess(ElevatorCar car, int elevatorId) {

		if (car.getErrorState() == null) {
			car.getDoor().closeDoor();
			return true;
		}

		if (car.getErrorState().getFault() == FloorInputFault.DOOR_STUCK_OPEN_FAULT) {
			logger.severe("(Elevator) Elevator " + elevatorId + " failed to close door.");

			switch (car.getAutoFixing()) {

			case AUTO_FIXING_SUCCESS:
				car.setErrorState(null);
				car.getDoor().closeDoor();
				logger.severe(
						"(Elevator) Elevator " + elevatorId + " has addressed FloorInputFault.DOOR_STUCK_OPEN_FAULT.");
				return true;

			case AUTO_FIXING_FAILURE:
				ElevatorStatusMessage statusMessage = car.createStatusMessage();
				logger.severe("(Elevator) Elevator " + elevatorId
						+ " FAILED to addressed FloorInputFault.DOOR_STUCK_OPEN_FAULT. Shutting down....");
				car.setInService(false);

				try {
					schedulerSubsystemCommunication.sendMessage(statusMessage);
				} catch (Exception e) {
				}

				return false;
			}
		}

		// If we are here, it means we have an error state that is not door stuck open
		// Because we have an error state, the door should be out of service and
		// the door-close operation will not work.
		try {
			car.setInService(false);
			// Update the scheduler of the error
			schedulerSubsystemCommunication.sendMessage(car.createStatusMessage());
		} catch (Exception e) {
		}

		return false;

	}
}
