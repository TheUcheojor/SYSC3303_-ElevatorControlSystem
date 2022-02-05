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
	 * A request for testing purposes.
	 */
	TEST_REQUEST
}
