package common.messages.scheduler;

import common.Direction;
import common.messages.Message;
import common.messages.MessageType;

public class SchedulerFloorCommand extends Message {
	/**
	 * The scheduler command for the floor
	 */
	private FloorCommand floorCommand;

	/**
	 * The floor id
	 */
	private int floorId;

	/**
	 * The direction
	 */
	private Direction lampButtonDirection;

	/**
	 * The primary constructor
	 *
	 * @param floorCommand
	 */
	public SchedulerFloorCommand(FloorCommand floorCommand, int floorId, Direction lampButtonDirection) {
		super(MessageType.SCHEDULER_FLOOR_COMMAND);
		this.floorCommand = floorCommand;
		this.floorId = floorId;
		this.lampButtonDirection = lampButtonDirection;
	}

	/**
	 * This method returns the scheduler command
	 *
	 * @return - The scheduler command
	 */
	public FloorCommand getCommand() {
		return floorCommand;
	}

	/**
	 * Get the floor id
	 *
	 * @return the floor id
	 */
	public int getFloorId() {
		return floorId;
	}

	/**
	 * Get the lamp button direction
	 *
	 * @return the lamp button direction
	 */
	public Direction getLampButtonDirection() {
		return lampButtonDirection;
	}

}
