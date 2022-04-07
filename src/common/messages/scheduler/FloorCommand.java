package common.messages.scheduler;

public enum FloorCommand {
	/**
	 * Turn off floor direction button lamp command
	 */
	TURN_OFF_FLOOR_LAMP,
	PRODUCE_STUCK_FAULT_WITH_ELEVATOR,
	
	/**
	 * A message that a passenger has been dropped off at a floor
	 */
	PASSENGER_DROP_OFF_COMPLETE
}
