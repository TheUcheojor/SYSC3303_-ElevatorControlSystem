/**
 *
 */
package common.messages;

/**
 * The different types of request.
 *
 * @author paulokenne
 *
 */
public enum MessageType {

	/**
	 * A job request which indicates a passenger wishes to go from floor A to B.
	 */
	JOB_REQUEST,

	/**
	 * A request to ask about the elevator status.
	 */
	ELEVATOR_STATUS_REQUEST,

	/**
	 * A message providing the elevator status.
	 */
	ELEVATOR_STATUS_MESSAGE,

	/**
	 * A message indicating that the elevator has arrived to the floor
	 */
	FLOOR_ARRIVAL_MESSAGE,

	/**
	 * A message indicating that the elevator is coming in the elevatorDirection of the
	 * floor
	 */
	EVELATOR_FLOOR_SIGNAL_REQUEST,

	/**
	 * A message indicating that the elevator is leaving the floor
	 */
	EVELATOR_LEAVING_FLOOR_MESSAGE,
	
	/**
	 * A message indicating that the elevator is leaving the floor
	 */
	SCHEDULER_ELEVATOR_COMMAND,
	SCHEDULER_FLOOR_COMMAND,
	
	/**
	 * A request sent by the elevator asking to move to a floor
	 */
	ELEVATOR_TRANSPORT_REQUEST,
	
	/**
	 * A request sent by the floor asking for an elevator to move to its floor
	 */
	ELEVATOR_FLOOR_REQUEST,

	/**
	 * A request for testing purposes.
	 */
	TEST_REQUEST
}
