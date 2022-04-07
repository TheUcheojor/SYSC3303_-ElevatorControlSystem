package common.messages.scheduler;

import common.exceptions.ElevatorStateException;
import common.messages.Message;
import common.messages.MessageType;
import common.messages.SchedulerElevatorTargetedMessage;
/**
 * Elevator job requests from elevators
 * 
 * @author Favour
 *
 */
public class SchedulerElevatorCommand extends SchedulerElevatorTargetedMessage {
	/**
	 * The scheduler command for the elevator 
	 */
	private ElevatorCommand elevatorCommand;

	/**
	 * The primary constructor
	 * 
	 * @param schedulerCommands
	 */
	private ElevatorStateException elevatorException;
	
	public SchedulerElevatorCommand(ElevatorCommand elevatorCommand, int elevatorID) {
		super(MessageType.SCHEDULER_ELEVATOR_COMMAND, elevatorID);
		this.elevatorCommand = elevatorCommand;
	}
	
	public SchedulerElevatorCommand(ElevatorCommand elevatorCommand, int elevatorID, ElevatorStateException exception) {
		super(MessageType.SCHEDULER_ELEVATOR_COMMAND, elevatorID);
		this.elevatorCommand = elevatorCommand;
		this.elevatorException = exception;
	}

	/**
	 * This method returns the scheduler command
	 * 
	 * @return - The scheduler command
	 */
	public ElevatorCommand getCommand() {
		return elevatorCommand;
	}

	public ElevatorStateException getException() {
		return elevatorException;
	}
	
	
}
