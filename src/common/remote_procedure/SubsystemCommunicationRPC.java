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
import java.net.SocketException;
import java.util.Base64;

import common.messages.Message;
import common.messages.MessageType;

/**
 * This class enables communication between the subsystems using remote
 * procedure calls.
 *
 * @author paulokenne, delight
 *
 */
public class SubsystemCommunicationRPC {

	/**
	 * The maximum buffer size
	 */
	public static final int MAX_BUFFER_SIZE = 1200;

	/**
	 * The receive socket
	 */
	private DatagramSocket receiveSocket;

	/**
	 * The target subsystem communication info which includes the port and ip
	 */
	private SubsystemCommunicationInfo targetSubsystemInfo;

	/**
	 * A SubystemCommunicationRPC constructor
	 *
	 * @param sourceSubsystemType the source subsystem type
	 * @param targetSubsystemType the target subsystem type
	 */
	public SubsystemCommunicationRPC(SubsystemComponentType sourceSubsystemType,
			SubsystemComponentType targetSubsystemType) {

		try {
			// Set up the source's send and receive socket
			SubsystemCommunicationInfo sourceCommunicationInfo = SubsystemCommunicationConfigurations
					.getSourceSubsystemCommunicationInfo(sourceSubsystemType, targetSubsystemType);
			this.receiveSocket = new DatagramSocket(sourceCommunicationInfo.getPortNumber());

			targetSubsystemInfo = SubsystemCommunicationConfigurations
					.getSourceSubsystemCommunicationInfo(targetSubsystemType, sourceSubsystemType);

		} catch (SocketException e) {
			System.out.println(e);
		}
	}

	/**
	 * Send the given message
	 *
	 * @param message the message to be sent
	 */
	public void sendMessage(Message message) throws Exception {

		byte[] messageBytes = getByteArrayFromMessage(message);

		String targetSubsystemIpAddress = targetSubsystemInfo.getIpAddress();
		int targetSubsystemPort = targetSubsystemInfo.getPortNumber();

		try {
			DatagramPacket sendPacket = new DatagramPacket(messageBytes, messageBytes.length,
					InetAddress.getByName(targetSubsystemIpAddress), targetSubsystemPort);

			// Send the message
			DatagramSocket sendReceiveSocket = new DatagramSocket();
			sendReceiveSocket.send(sendPacket);

			// Expect an acknowledgement message
			byte[] data = new byte[MAX_BUFFER_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			Message receiveMessage = receiveMessage(sendReceiveSocket, receivePacket);

			// Close the socket
			sendReceiveSocket.close();

			if (receiveMessage == null || receiveMessage.getMessageType() != MessageType.ACKNOWLEDGEMENT_RESPONSE) {
				throw new Exception("No acknowledgement message!");
			}

		} catch (Exception e) {
			System.out.print(e);
		}
	}

	/**
	 * Receive a response message the subsystem receive socket.
	 *
	 * @param sendReceiveSocket the send and receive socket
	 * @return the response message
	 */
	public Message receiveMessage() throws Exception {
		byte[] data = new byte[MAX_BUFFER_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		Message receivedMessage = receiveMessage(receiveSocket, receivePacket);

		// Send the acknowledgement message
		sendAcknowledgmentMessage(receiveSocket, receivePacket);
		return receivedMessage;
	}

	/**
	 * Receive a response message on the given socket.
	 *
	 * @param receiveSocket the socket
	 * @param receivePacket the receievePacket
	 * @return the response message
	 */
	private Message receiveMessage(DatagramSocket receiveSocket, DatagramPacket receivePacket) throws Exception {
		try {
			// Receive a message
			receiveSocket.receive(receivePacket);
			return getMessageFromPacket(receivePacket);

		} catch (Exception excepetion) {
			System.out.print(excepetion);
			throw excepetion;
		}
	}

	/**
	 * Send an acknowledgment message.
	 *
	 * @param receiveSocket the socket
	 * @return the response message
	 */
	private void sendAcknowledgmentMessage(DatagramSocket receiveSocket, DatagramPacket receivePacket)
			throws Exception {

		try {
			// Creating the acknowledgement message bytes
			Message acknowledgmentMessage = new Message(MessageType.ACKNOWLEDGEMENT_RESPONSE);
			byte[] acknowledgmentMessageBytes = getByteArrayFromMessage(acknowledgmentMessage);

			// Send the acknowledgement message bytes
			DatagramPacket sendPacket = new DatagramPacket(acknowledgmentMessageBytes,
					acknowledgmentMessageBytes.length, receivePacket.getAddress(), receivePacket.getPort());

			DatagramSocket sendSocket = new DatagramSocket();
			sendSocket.send(sendPacket);
			sendSocket.close();

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
			System.out.println(e + ": " + e.getMessage());
			throw new Exception("Cannot create byte array from message!\n");
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
