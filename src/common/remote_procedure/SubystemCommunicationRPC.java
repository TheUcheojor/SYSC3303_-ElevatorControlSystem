/**
 *
 */
package common.remote_procedure;

import java.net.DatagramSocket;

import common.messages.Message;

/**
 * This class enables communication between the subsystems using remote
 * procedure calls.
 *
 * @author paulokenne, delight
 *
 */
public class SubystemCommunicationRPC {

	/**
	 * The send and receive socket
	 */
	private DatagramSocket sendReceiveSocket;

	/**
	 * A SubystemCommunicationRPC constructor
	 *
	 * @param sendReceiveSocket the send and receive socket
	 */
	public SubystemCommunicationRPC(DatagramSocket sendReceiveSocket) {
		this.sendReceiveSocket = sendReceiveSocket;
	}

	public Message sendAndReceiveData() {
		return null;
	}

}
