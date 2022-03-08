package tests.common;

import static org.junit.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.messages.Message;
import common.messages.MessageType;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;

/**
 * This class tests the send and receive functionality of
 * SubystemCommunicationRPC.
 *
 * @author delightoluwayemi
 *
 */
public class SubsystemCommunicationRPCTest {

	/**
	 * Create a new SubystemCommunicationRPC object.
	 */
	private SubsystemCommunicationRPC subsystemCommunication;

	/**
	 * The target subsystem socket
	 */
	DatagramSocket sourceSubsystemSendReceiveSocket;

	/**
	 * The received message from target subsystem
	 */
	private Message receivedTargetSystemResponseMessage;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.subsystemCommunication = new SubsystemCommunicationRPC(SubsystemComponentType.SCHEDULER,
				SubsystemComponentType.ELEVATOR_SUBSYSTEM);

		/**
		 * The thread simulates the SubystemCommunicationRPC response by sending the
		 * received message.
		 */
		Thread subsystemSimulatorResponse = new Thread() {

			@Override
			public void run() {
				byte[] data = new byte[SubsystemCommunicationRPC.MAX_BUFFER_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(data, data.length);
				try {
					SubsystemCommunicationRPC subsystemCommunication = new SubsystemCommunicationRPC(
							SubsystemComponentType.ELEVATOR_SUBSYSTEM, SubsystemComponentType.SCHEDULER);

					receivedTargetSystemResponseMessage = subsystemCommunication.receiveMessage();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		subsystemSimulatorResponse.start();
	}

	/**
	 * This test case tests that the RPC communication can send a request and
	 * receive a response
	 */
	@Test
	public void testRPCCommunicationForsendingRequestAndReceivingResponses() {

		(new Thread() {
			@Override
			public void run() {
				try {
					Message testMessage = new Message(MessageType.TEST_REQUEST);
					subsystemCommunication.sendMessage(testMessage);
				} catch (Exception e) {
				}

			}
		}).start();

		// Let the thread work
		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}

		assertTrue(receivedTargetSystemResponseMessage.getMessageType() == MessageType.TEST_REQUEST);
	}

}
