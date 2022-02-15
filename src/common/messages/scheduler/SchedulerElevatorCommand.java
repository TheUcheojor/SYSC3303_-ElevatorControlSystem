package common.messages.scheduler;

import common.SchedulerCommand;
import common.messages.Message;
import common.messages.MessageType;
/**
 * This class stores a command to be sent to the elevator from the scheduler
 * 
 * @author Favour
 *
 */
public class SchedulerElevatorCommand extends Message{
	/**
	 * The scheduler command for the elevator 
	 */
	private SchedulerCommand schedulerCommand;
	
	/**
	 * The primary constructor
	 * 
	 * @param schedulerCommands
	 */
	public SchedulerElevatorCommand(SchedulerCommand schedulerCommands) {
		super(MessageType.SCHEDULER_ELEVATOR_COMMAND);
		this.schedulerCommand = schedulerCommands;
	}

	/**
	 * This method returns the scheduler command
	 * 
	 * @return - The scheduler command
	 */
	public SchedulerCommand getCommand() {
		return schedulerCommand;
	}
}
