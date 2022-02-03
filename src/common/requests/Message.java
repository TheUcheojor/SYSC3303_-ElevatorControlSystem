/**
 *
 */
package common.requests;

/**
 * This class represents a request entity
 *
 * @author paulokenne, ryanfife
 *
 */
public class Message {

	/**
	 * The request type.
	 */
	private MessageType messageType;

	public Message(MessageType messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the messageType
	 */
	public MessageType getMessageType() {
		return messageType;
	}

}
