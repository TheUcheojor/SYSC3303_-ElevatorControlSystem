package ElevatorSubsystem;

import java.util.Map;
import java.util.logging.Logger;

import FloorSubsystem.FloorInputFault;
import common.LoggerWrapper;
import common.exceptions.ElevatorStateException;
import common.messages.Message;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorLeavingFloorMessage;
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

	private static int FAULT_RETRY_ATTEMPTS = 3;
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
	private ElevatorCar elevator;

	/**
	 * The ElevatorSchedulerMessageWorkQueue constructor
	 *
	 * @param schedulerSubsystemCommunication The scheduler subsystem communication
	 * @param floorSubsystemCommunication     The floor subsystem communication
	 * @param elevators                       the elevators
	 */
	public ElevatorSchedulerMessageWorkQueue(SubsystemCommunicationRPC schedulerSubsystemCommunication,
			SubsystemCommunicationRPC floorSubsystemCommunication, ElevatorCar elevator) {
		this.schedulerSubsystemCommunication = schedulerSubsystemCommunication;
		this.floorSubsystemCommunication = floorSubsystemCommunication;
		this.elevator = elevator;
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
				schedulerSubsystemCommunication.sendMessage(elevator.createStatusMessage());
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

		int elevatorId = elevator.getId();
		int carFloorNumber = elevator.getFloorNumber();

		try {
			switch (command.getCommand()) {
			case STOP:
				if (!elevator.getDoor().isOpen()) {
					logger.fine("(ELEVATOR) Elevator " + elevatorId + " stopping");
					elevator.getMotor().turnOff();
				} else {
					elevator.setErrorState(new ElevatorStateException(null, "Attempted to stop while doors open"));
				}
				break;
			case CLOSE_DOORS:
				logger.fine("(ELEVATOR) Elevator " + elevatorId + " door closing");
				elevator.getDoor().closeDoor();
				break;
			case OPEN_DOORS:
				if (!elevator.getMotor().getIsRunning()) {
					logger.fine("(ELEVATOR) Elevator " + elevatorId + " door opening");
					elevator.getDoor().openDoor();
				} else {
					elevator.setErrorState(new ElevatorStateException(null, "Attempted to open doors while motor running"));
				}

				break;
			case MOVE_UP:
				logger.fine("(ELEVATOR) Elevator " + elevatorId + " door closing");
				boolean isCloseDoorProcessSuccess = closeDoorProcess();

				if (!isCloseDoorProcessSuccess)
					return;

				logger.fine("(ELEVATOR) Elevator " + elevatorId + " moving up");
				elevator.getMotor().goUp();

				leavingMessage = new ElevatorLeavingFloorMessage(elevatorId, carFloorNumber);
				comingMessage = new ElevatorFloorSignalRequestMessage(elevator.getId(), carFloorNumber + 1, elevator.getMotor(),
						true);

				floorSubsystemCommunication.sendMessage(leavingMessage);
				floorSubsystemCommunication.sendMessage(comingMessage);

				break;
			case MOVE_DOWN:
				logger.fine("(ELEVATOR) Elevator " + elevatorId + " door closing");
				boolean isDoorClosed = closeDoorProcess();

				if (!isDoorClosed)
					return;

				logger.fine("(ELEVATOR) Elevator " + elevatorId + " moving down");
				elevator.getMotor().goDown();

				leavingMessage = new ElevatorLeavingFloorMessage(elevatorId, carFloorNumber);
				comingMessage = new ElevatorFloorSignalRequestMessage(elevatorId, carFloorNumber - 1, elevator.getMotor(),
						true);

				floorSubsystemCommunication.sendMessage(leavingMessage);
				floorSubsystemCommunication.sendMessage(comingMessage);

				break;

			case SHUT_DOWN:
				elevator.setInService(false);
				elevator.setErrorState(command.getException());
				break;

			case RESTART:
				elevator.setInService(true);
				elevator.setErrorState(null);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The close door process
	 *
	 * @param elevator        the elevator
	 * @param elevatorId the elevator id
	 * @return true if the close door process and false otherwise
	 */
	private boolean closeDoorProcess() {

		int elevatorId = elevator.getId();
		int currentFloorNumber = elevator.getFloorNumber();

		if (elevator.getErrorState() == null) {
			elevator.getDoor().closeDoor();
			return true;
		}

		if (elevator.getErrorState().getFault() == FloorInputFault.DOOR_STUCK_OPEN_FAULT) {
			logger.severe("(Elevator) Elevator " + elevatorId + " failed to close door at floor " + currentFloorNumber);

			// Notify the scheduler that the elevator is resolving an issue
			elevator.setResolvingError(true);
			try {
				schedulerSubsystemCommunication.sendMessage(elevator.createCommandNonIssuingStatusMessage());
			} catch (Exception e) {
			}

			ElevatorAutoFixing autoFixing = elevator.getAutoFixing();

			// Try to close the elevator
			for (int i = 0; i < FAULT_RETRY_ATTEMPTS; i++) {
				boolean isDoorClosed = elevator.getDoor().closeDoor(autoFixing);

				if (isDoorClosed) {
					elevator.setErrorState(null);
					elevator.setResolvingError(false);

					logger.severe("(Elevator) Elevator " + elevatorId + " has resolved the issue and closed the door.");

					// Notify the scheduler that the elevator has resolved the issue
					try {
						schedulerSubsystemCommunication.sendMessage(elevator.createCommandNonIssuingStatusMessage());
					} catch (Exception e) {
					}

					return true;
				}

			}

			// Being here indicates that the elevator exhausted the fault retry attempts.
			// The elevator will not shut down...
			elevator.setInService(false);
			elevator.setResolvingError(false);

			// Notify the scheduler that the elevator is no longer attempting to resolve an
			// issue and that the elevator has shut down.
			try {
				schedulerSubsystemCommunication.sendMessage(elevator.createCommandNonIssuingStatusMessage());
			} catch (Exception e) {
			}

			logger.severe("(Elevator) Elevator " + elevatorId
					+ " exhausted its retry attempts to close the door. Shutting down....");

			return false;
		}

		// If we are here, it means we have an error state that is not door stuck open
		// Because we have an error state, the door should be out of service and
		// the door-close operation will not work.
		try {
			elevator.setInService(false);
			// Update the scheduler of the error
			schedulerSubsystemCommunication.sendMessage(elevator.createStatusMessage());
		} catch (Exception e) {
		}

		return false;

	}
}
