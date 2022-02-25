/**
 *
 */
package common.remote_procedure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Base64;

import common.messages.CommunicationFailureMessage;
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
	 * The maximum buffer size
	 */
	public static final int MAX_BUFFER_SIZE = 1000;
	/**
	 * The send and receive socket
	 */
	private DatagramSocket sendReceiveSocket;

	/**
	 * The target subsystem communication info which includes the port and ip
	 */
	private SubsystemCommunicationInfo targetSubsystemInfo;

	/**
	 * A SubystemCommunicationRPC constructor
	 *
	 * @param sendReceiveSocket   the send and receive socket
	 * @param targetSubsystemInfo the target subsystem communication info
	 */
	public SubystemCommunicationRPC(DatagramSocket sendReceiveSocket, SubsystemCommunicationInfo targetSubsystemInfo) {
		this.sendReceiveSocket = sendReceiveSocket;
		this.targetSubsystemInfo = targetSubsystemInfo;
	}

	/**
	 * Sends a given request message and returns the response
	 *
	 * @param message the message to be sent
	 * @return the response messages
	 */
	public Message sendRequestAndReceiveResponse(Message message) {
		try {
			sendMessage(message);
			return receiveMessage();
		} catch (Exception e) {
			return new CommunicationFailureMessage();
		}
	}

	/**
	 * Send the given message
	 *
	 * @param message the message to be sent
	 */
	private void sendMessage(Message message) throws Exception {

		byte[] messageBytes = getByteArrayFromMessage(message);

		String targetSubsystemIpAddress = targetSubsystemInfo.getIpAddress();
		int targetSubsystemPort = targetSubsystemInfo.getPortNumber();

		try {
			DatagramPacket sendPacket = new DatagramPacket(messageBytes, messageBytes.length,
					InetAddress.getByName(targetSubsystemIpAddress), targetSubsystemPort);

			sendReceiveSocket.send(sendPacket);
		} catch (Exception e) {
			System.out.print(e);
		}
	}

	/**
	 * Receive the response message.
	 *
	 * @return the response message
	 */
	private Message receiveMessage() throws Exception {

		byte[] data = new byte[MAX_BUFFER_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		try {
			sendReceiveSocket.receive(receivePacket);

			return getMessageFromPacket(receivePacket);
		} catch (Exception excepetion) {
			System.out.print(excepetion);
			throw excepetion;
		}
	}

	/**
	 * Turn a given message into a byte array
	 *
	 * @return a byte array representation of the message
	 */
	private byte[] getByteArrayFromMessage(Message message) throws Exception {

		try {
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutputStream);

			objectOutStream.writeObject(message);
			objectOutStream.flush();

			return Base64.getEncoder().encode(byteOutputStream.toByteArray());
		} catch (IOException e) {
			System.out.println(e);
			throw e;
		}
	}

	/**
	 * Return the message that is in the packet
	 *
	 * @return the received message
	 */
	private Message getMessageFromPacket(DatagramPacket receivePacket) throws Exception {

		try {
			byte[] receivedData = Base64.getDecoder().decode(getSentByteArray(receivePacket));

			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(receivedData);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);

			return (Message) objectInputStream.readObject();

		} catch (IOException e) {
			System.out.println(e);
			throw new Exception("Cannot create byte array from message!");
		}
	}

	/**
	 * Given a packet, this function returns the actual data byte array that was
	 * sent
	 *
	 * @param receivePacket the received packet
	 * @return the sent data byte array
	 */
	private byte[] getSentByteArray(DatagramPacket receivePacket) {
		byte[] sentData = new byte[receivePacket.getLength()];
		byte[] receivedData = receivePacket.getData();

		for (int i = 0; i < receivePacket.getLength(); i++) {
			sentData[i] = receivedData[i];
		}

		return sentData;
	}
}
