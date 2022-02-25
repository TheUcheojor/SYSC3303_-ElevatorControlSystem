package tests.common;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import Scheduler.Scheduler;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.MessageType;
import common.remote_procedure.SubsystemCommunicationInfo;
import common.remote_procedure.SubystemCommunicationRPC;
/**
 * This class tests the send and receive functionality of SubystemCommunicationRPC.
 * @author delightoluwayemi
 *
 */
public class SubystemCommunicationRPCTest {

	/**
	 * Create a new SubystemCommunicationRPC object. 
	 */
	private SubystemCommunicationRPC subsystemCommunication;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		DatagramSocket sendReceiveSocket = new DatagramSocket();
		String hostIpAddress = InetAddress.getLocalHost().getHostAddress();
		
		int portNumber = 9999;
		DatagramSocket targetSubsystemSocket = new DatagramSocket(portNumber);
		
		SubsystemCommunicationInfo targetSubsystemInfo = new SubsystemCommunicationInfo(hostIpAddress,portNumber);
		this.subsystemCommunication = new SubystemCommunicationRPC(sendReceiveSocket, targetSubsystemInfo);
		System.out.println(subsystemCommunication);

		
		/**
		 * The thread simulates the SubystemCommunicationRPC response by sending the received message.
		 */
		Thread subsystemSimulatorResponse = new Thread() {
			
			@Override 
			public void run(){
				byte[] data = new byte[SubystemCommunicationRPC.MAX_BUFFER_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(data, data.length);
				try {
					targetSubsystemSocket.receive(receivePacket);
					targetSubsystemSocket.send(receivePacket);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		};
		subsystemSimulatorResponse.start();
	}
	
	/**
	 * This test case tests that the RPC communication can send a request and receive a response
	 */
	@Test
	public void testRPCCommunication() {
		Message testMessage = new Message(MessageType.TEST_REQUEST);
		System.out.println(subsystemCommunication);
		Message responseMessage= subsystemCommunication.sendRequestAndReceiveResponse(testMessage);
		assertTrue(responseMessage.getMessageType() == MessageType.TEST_REQUEST);
	}

}
