/**
 *
 */
package common.messages;

/**
 * A message specifying the id of the entity sending the request and the target
 * entity id
 *
 * @author paulokenne
 *
 */
public class IdentifierDrivenMessage extends Message {

	/*
	 * The id of the requesting entity which could be a specific elevator or floor.
	 */
	private int sourceEntityId = -1;

	/*
	 * The id of the target entity which could be a specific elevator or floor.
	 */
	private int targetEntityId;

	/**
	 * A IdentifierDrivenMessage constructor
	 *
	 * @param requestingEntityId the request entity id
	 * @param targetEntityId     the target entity id
	 * @param messageType        the message type
	 */
	public IdentifierDrivenMessage(int requestingEntityId, int targetEntityId, MessageType messageType) {
		super(messageType);
		this.sourceEntityId = requestingEntityId;
		this.targetEntityId = targetEntityId;
	}

	/**
	 * A IdentifierDrivenMessage constructor
	 *
	 * @param targetEntityId the target entity id
	 * @param messageType    the message type
	 */

	public IdentifierDrivenMessage(int targetEntityId, MessageType messageType) {
		super(messageType);
		this.targetEntityId = targetEntityId;
	}

	/**
	 * @return the requestingEntityId;
	 *
	 */
	public int getSourceEntityId() {
		return sourceEntityId;
	}

	/**
	 * @return the targetEntityId;
	 *
	 */
	public int getTargetEntityId() {
		return targetEntityId;
	}

}
