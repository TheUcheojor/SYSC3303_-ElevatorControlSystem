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
	 * A subset of message, elevator-floor messages, that enable communication
	 * between the elevator and floor
	 */
	ELEVATOR_FLOOR_MESSAGE,

	/**
	 * A subset of messages, elevator commands, used for communication between the
	 * scheduler and elevator
	 */
	SCHEDULER_ELEVATOR_COMMAND,

	/**
	 * A subset of messages, floor commands, used for communication between the
	 * scheduler and floor
	 */
	SCHEDULER_FLOOR_COMMAND,

	/**
	 * A message indicates that the elevator to arrive at a destination floor
	 */
	ELEVATOR_TRANSPORT_REQUEST,

	/**
	 * A message indicating that the floor passenger requested for an elevator at a
	 * floor
	 */
	ELEVATOR_FLOOR_REQUEST,

	/**
	 * A request for testing purposes.
	 */
	TEST_REQUEST
}
