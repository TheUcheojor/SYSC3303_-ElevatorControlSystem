package common.remote_procedure;

import java.net.InetAddress;

/**
 * The class stores the communication information for a given subsystem.
 */
public class SubsystemCommunicationInfo {

	/*
	 * The ip address of the subsystem.
	 */
	private String ipAddress = "";

	/*
	 * THe port number of the subsystem.
	 */
	private int portNumber;

	/**
	 * A SubsystemCommunicationInfo constructor.
	 *
	 * @param ipAddress  the ip address
	 * @param portNumber the port number
	 */
	public SubsystemCommunicationInfo(String ipAddress, int portNumber) {
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}

	/**
	 * A SubsystemCommunicationInfo constructor.
	 *
	 * @param inetAddress the inet-address
	 * @param portNumber  the port number
	 */
	public SubsystemCommunicationInfo(InetAddress inetAddress, int portNumber) {
		this.ipAddress = inetAddress.getHostAddress();
		this.portNumber = portNumber;
	}

	/**
	 * The method gets the ip address
	 *
	 * @return the ip address
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * THe method gets the port number
	 *
	 * @return the port number
	 */
	public int getPortNumber() {
		return portNumber;
	}

}
