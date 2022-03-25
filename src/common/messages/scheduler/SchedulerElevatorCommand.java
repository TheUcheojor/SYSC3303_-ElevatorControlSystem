package common.messages.scheduler;

import common.exceptions.ElevatorStateException;
import common.messages.Message;
import common.messages.MessageType;
/**
 * Elevator job requests from elevators
 * 
 * @author Favour
 *
 */
public class SchedulerElevatorCommand extends Message{
	/**
	 * The scheduler command for the elevator 
	 */
	private ElevatorCommand elevatorCommand;
	
	/**
	 * The ID of the elevator getting the command
	 */
	private int elevatorID;
	/**
	 * The primary constructor
	 * 
	 * @param schedulerCommands
	 */
	private ElevatorStateException elevatorException;
	
	public SchedulerElevatorCommand(ElevatorCommand elevatorCommand, int elevatorID) {
		super(MessageType.SCHEDULER_ELEVATOR_COMMAND);
		this.elevatorCommand = elevatorCommand;
		this.elevatorID = elevatorID;
	}
	
	public SchedulerElevatorCommand(ElevatorCommand elevatorCommand, int elevatorID, ElevatorStateException exception) {
		super(MessageType.SCHEDULER_ELEVATOR_COMMAND);
		this.elevatorCommand = elevatorCommand;
		this.elevatorID = elevatorID;
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

	/**
	 * This method returns the elevator ID number
	 * @return - The elevator ID
	 */
	public int getElevatorID() {
		return elevatorID;
	}

	public ElevatorStateException getException() {
		return elevatorException;
	}
	
	
}
