package common.messages.scheduler;

import common.Direction;
import common.messages.Message;
import common.messages.MessageType;

/*
 * Elevator job requests from floors
 *
 * @author Ryan Fife
 */

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
	 * An optional field , elevator id, that is set when requested to turned off
	 * floor lamps.
	 *
	 * Turning off floor lamps indicates that the request elevator has arrived.
	 */
	private int elevatorId;

	/**
	 * An optional field, input data id, that is set when requested to turned off
	 * floor lamps.
	 *
	 * Turning off floor lamps indicates that the request elevator has arrived.
	 */
	private int inputDataId;

	/**
	 * The primary constructor
	 *
	 * @param floorCommand        the floor command
	 * @param floorId             the floor id
	 * @param lampButtonDirection the lamp button direction
	 */
	public SchedulerFloorCommand(FloorCommand floorCommand, int floorId, Direction lampButtonDirection) {
		super(MessageType.SCHEDULER_FLOOR_COMMAND);
		this.floorCommand = floorCommand;
		this.floorId = floorId;
		this.lampButtonDirection = lampButtonDirection;
	}

	/**
	 * The primary constructor
	 *
	 * @param floorCommand
	 */
	public SchedulerFloorCommand(FloorCommand floorCommand, int floorId, Direction lampButtonDirection, int elevatorId,
			int inputDataId) {
		super(MessageType.SCHEDULER_FLOOR_COMMAND);
		this.floorCommand = floorCommand;
		this.floorId = floorId;
		this.lampButtonDirection = lampButtonDirection;
		this.elevatorId = elevatorId;
		this.inputDataId = inputDataId;
	}
	
	public SchedulerFloorCommand(FloorCommand floorCommand, int floorId, int elevatorId) {
		super(MessageType.SCHEDULER_FLOOR_COMMAND);
		this.floorCommand = floorCommand;
		this.floorId = floorId;
		this.elevatorId = elevatorId;
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

	/**
	 * Get the elevator id.
	 *
	 * @return the elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}

	/**
	 * Get the input data id.
	 *
	 * @return the input data id
	 */
	public int getInputDataId() {
		return inputDataId;
	}
}
