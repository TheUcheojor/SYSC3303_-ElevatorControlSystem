package common.messages.scheduler;

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
	 * The primary constructor
	 * 
	 * @param schedulerCommands
	 */
	public SchedulerElevatorCommand(ElevatorCommand elevatorCommand) {
		super(MessageType.SCHEDULER_ELEVATOR_COMMAND);
		this.elevatorCommand = elevatorCommand;
	}

	/**
	 * This method returns the scheduler command
	 * 
	 * @return - The scheduler command
	 */
	public ElevatorCommand getCommand() {
		return elevatorCommand;
	}
}
