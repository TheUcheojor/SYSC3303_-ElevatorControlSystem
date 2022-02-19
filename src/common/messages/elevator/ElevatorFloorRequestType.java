/**
 *
 */
package common.messages.elevator;

/**
 * This enum contains the different types of requests that may be sent between
 * the elevator and floor
 *
 * @author paulokenne
 *
 */
public enum ElevatorFloorRequestType {

	/**
	 * A message indicating that the elevator has arrived to the floor
	 */
	FLOOR_ARRIVAL_MESSAGE,

	/**
	 * A message indicating that the elevator is coming in the elevatorDirection of
	 * the floor
	 */
	ELEVATOR_FLOOR_SIGNAL_REQUEST,

	/**
	 * A message indicating that the elevator is leaving the floor
	 */
	ELEVATOR_LEAVING_FLOOR_MESSAGE
}
