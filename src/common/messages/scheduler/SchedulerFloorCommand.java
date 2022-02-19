package common.messages.scheduler;

import common.Direction;
import common.messages.Message;
import common.messages.MessageType;

public class SchedulerFloorCommand extends Message {
	/**
	 * The scheduler command for the floor 
	 */
	private FloorCommand floorCommand;
	private int floorId;
	private Direction direction;
	
	/**
	 * The primary constructor
	 * 
	 * @param floorCommand
	 */
	public SchedulerFloorCommand(FloorCommand floorCommand, int floorId, Direction direction) {
		super(MessageType.SCHEDULER_FLOOR_COMMAND);
		this.floorCommand = floorCommand;
		this.floorId = floorId;
		this.direction = direction;
	}

	/**
	 * This method returns the scheduler command
	 * 
	 * @return - The scheduler command
	 */
	public FloorCommand getCommand() {
		return floorCommand;
	}
	
	public int getFloorId() {
		return floorId;
	}

	public Direction getDirection() {
		return direction;
	}
	
}
