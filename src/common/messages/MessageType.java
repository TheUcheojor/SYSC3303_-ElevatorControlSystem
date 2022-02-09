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
	 * A message indicating that the elevator is coming in the direction of the
	 * floor
	 */
	EVELATOR_FLOOR_SIGNAL_REQUEST,

	/**
	 * A message indicating that the elevator is leaving the floor
	 */
	EVELATOR_LEAVING_FLOOR_MESSAGE,

	/**
	 * A request for testing purposes.
	 */
	TEST_REQUEST
}
