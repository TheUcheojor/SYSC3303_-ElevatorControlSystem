package common.messages;

/**
 * The class respresents messages that did not send or could not be received
 * @author delightoluwayemi
 *
 */
public class CommunicationFailureMessage extends Message{

	/**
	 * A CommunicationFailureMessage constructor.
	 */
	public CommunicationFailureMessage() {
		super(MessageType.COMMUNICATION_FAILURE);
	}

}
