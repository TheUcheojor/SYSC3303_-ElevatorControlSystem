package tests.common;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.messages.Message;
import common.messages.MessageType;
import common.remote_procedure.SubsystemCommunicationInfo;
import common.remote_procedure.SubystemCommunicationRPC;

/**
 * This class tests the send and receive functionality of
 * SubystemCommunicationRPC.
 *
 * @author delightoluwayemi
 *
 */
public class SubystemCommunicationRPCTest {

	/**
	 * Create a new SubystemCommunicationRPC object.
	 */
	private SubystemCommunicationRPC subsystemCommunication;

	/**
	 * The target subsystem socket
	 */
	DatagramSocket targetSubsystemSocket;

	/**
	 * The target subsystem socket
	 */
	DatagramSocket sourceSubsystemSendReceiveSocket;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		String hostIpAddress = InetAddress.getLocalHost().getHostAddress();
		int portNumber = 8999;

		targetSubsystemSocket = new DatagramSocket(portNumber);
		sourceSubsystemSendReceiveSocket = new DatagramSocket();

		SubsystemCommunicationInfo targetSubsystemInfo = new SubsystemCommunicationInfo(hostIpAddress, portNumber);
		this.subsystemCommunication = new SubystemCommunicationRPC(sourceSubsystemSendReceiveSocket,
				targetSubsystemInfo);

		/**
		 * The thread simulates the SubystemCommunicationRPC response by sending the
		 * received message.
		 */
		Thread subsystemSimulatorResponse = new Thread() {

			@Override
			public void run() {
				byte[] data = new byte[SubystemCommunicationRPC.MAX_BUFFER_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(data, data.length);
				try {
					targetSubsystemSocket.receive(receivePacket);
					targetSubsystemSocket.send(receivePacket);
					targetSubsystemSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		subsystemSimulatorResponse.start();
	}

	/**
	 * Close sockets
	 *
	 * @throws Exception if errors occur
	 */
	@AfterEach
	void closeSockets() throws Exception {
		targetSubsystemSocket.close();
		sourceSubsystemSendReceiveSocket.close();
	}

	/**
	 * This test case tests that the RPC communication can send a request and
	 * receive a response
	 */
	@Test
	public void testRPCCommunication() {
		Message testMessage = new Message(MessageType.TEST_REQUEST);
		Message responseMessage = subsystemCommunication.sendRequestAndReceiveResponse(testMessage);
		assertTrue(responseMessage.getMessageType() == MessageType.TEST_REQUEST);
	}

}
