/**
 *
 */
package common.exceptions;

/**
 * This class represents an exception where the system configuration input is
 * invalid.
 *
 * @author paulokenne
 *
 */
public class InvalidSystemConfigurationInputException extends Exception {

	/**
	 * A InvalidSystemConfigurationInputException constructor
	 *
	 * @param message
	 */
	public InvalidSystemConfigurationInputException(String message) {
		super(message);
	}
}
