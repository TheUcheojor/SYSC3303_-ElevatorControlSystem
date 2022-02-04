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
	 * A request indicating that the elevator subsystem is ready.
	 */
	ELEVATOR_STATUS_REQUEST,
	ELEVATOR_STATUS_RESPONSE,

	/**
	 * A request for testing purposes.
	 */
	TEST_REQUEST
}
