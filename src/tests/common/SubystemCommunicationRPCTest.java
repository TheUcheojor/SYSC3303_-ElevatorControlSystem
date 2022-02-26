package tests.common;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.messages.Message;
import common.messages.MessageType;
import common.remote_procedure.SubsystemCommunicationConfiguarations;
import common.remote_procedure.SubsystemComponentType;
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
		this.subsystemCommunication = new SubystemCommunicationRPC(SubsystemComponentType.SCHEDULER,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);
		this.targetSubsystemSocket = new DatagramSocket(
				SubsystemCommunicationConfiguarations.ELEVATOR_PORT_MAPPING.get(SubsystemComponentType.SCHEDULER));

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
	}

	/**
	 * This test case tests that the RPC communication can send a request and
	 * receive a response
	 */
	@Test
	public void testRPCCommunicationForsendingRequestAndReceivingResponses() {
		Message testMessage = new Message(MessageType.TEST_REQUEST);
		Message responseMessage = subsystemCommunication.sendRequestAndReceiveResponse(testMessage);
		assertTrue(responseMessage.getMessageType() == MessageType.TEST_REQUEST);
	}

}
