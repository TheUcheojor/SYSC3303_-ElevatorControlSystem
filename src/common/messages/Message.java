/**
 *
 */
package common.messages;

/**
 * This class represents a message entity
 *
 * @author paulokenne, ryanfife
 *
 */
public class Message {

	/**
	 * The message type.
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
