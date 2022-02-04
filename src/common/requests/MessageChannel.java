/**
 *
 */
package common.requests;

/**
 * This class represents a channel whereby threads can transfer data.
 *
 * @author paulokenne, ryanfife
 *
 */
public class MessageChannel {

	/**
	 * The message.
	 */
	private Message message;

	/**
	 * A constructor.
	 */
	public MessageChannel() {
	}

	/**
	 * Sets the message
	 *
	 * @param message the message
	 */
	public synchronized void setMessage(Message message) {
		/**
		 * Wait until the message channel is empty
		 */
		while (this.message != null) {
			try {
				wait();
			} catch (InterruptedException exception) {
			}
		}

		this.message = message;
	}

	/**
	 * Sets the message
	 *
	 * @param message the message
	 */
	public synchronized Message getMessage() {

		/**
		 * Wait until the message channel is full
		 */
		while (this.message == null) {
			try {
				wait();
			} catch (InterruptedException exception) {
			}
		}

		Message tempMessage = this.message;
		this.message = null;

		return tempMessage;
	}

	/**
	 * @return true if there is no message and false otherwise.
	 */
	public boolean isEmpty() {
		return message == null;
	}
}
