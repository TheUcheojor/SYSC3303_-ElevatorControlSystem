package common.messages;

/**
 * This class provides a base function that elevator messages must implement.
 *
 * @author ryanfife
 *
 */
public interface ElevatorMessage {
	/**
	 * Returns the elevator id.
	 *
	 * @return the elevator id
	 */
	public int getId();
}
