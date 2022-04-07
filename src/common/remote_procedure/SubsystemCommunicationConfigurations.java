/**
 *
 */
package common.remote_procedure;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * This class provides the configuration values for each subystem
 *
 * @author paulokenne
 *
 */
public final class SubsystemCommunicationConfigurations {

	/**
	 * The scheduler send/receive port for the floor subsystem
	 */
	private static final int SCHEDULER_TO_FLOOR_SEND_RECEIVE_PORT = 11000;

	/**
	 * The scheduler send/receive port for the elevator subsystem
	 */
	private static final int SCHEDULER_TO_ELEVATOR_SEND_RECEIVE_PORT = 11001;
	
	/**
	 * The scheduler send/receive port for the elevator subsystem
	 */
	private static final int SCHEDULER_TO_GUI_SEND_RECEIVE_PORT = 11002;

	/**
	 * The scheduler port mapping that provides the appropriate port for a given
	 * subsystem.
	 */
	public static final Map<SubsystemComponentType, Integer> SCHEDULER_PORT_MAPPING = Map.ofEntries(
			Map.entry(SubsystemComponentType.ELEVATOR_SUBSYSTEM, SCHEDULER_TO_ELEVATOR_SEND_RECEIVE_PORT),
			Map.entry(SubsystemComponentType.FLOOR_SUBSYSTEM, SCHEDULER_TO_FLOOR_SEND_RECEIVE_PORT),
			Map.entry(SubsystemComponentType.GUI, SCHEDULER_TO_GUI_SEND_RECEIVE_PORT));

	/**
	 * The elevator subsystem send/receive port for the floor subsystem
	 */
	private static final int ELEVATOR_TO_FLOOR_SEND_RECEIVE_PORT = 12000;

	/**
	 * The elevator subsystem send/receive port for the scheduler
	 */
	private static final int ELEVATOR_TO_SCHEDULER_SEND_RECEIVE_PORT = 12001;

	/**
	 * The elevator port mapping that provides the appropriate port for a given
	 * subsystem.
	 */
	public static final Map<SubsystemComponentType, Integer> ELEVATOR_PORT_MAPPING = Map.ofEntries(
			Map.entry(SubsystemComponentType.SCHEDULER, ELEVATOR_TO_SCHEDULER_SEND_RECEIVE_PORT),
			Map.entry(SubsystemComponentType.FLOOR_SUBSYSTEM, ELEVATOR_TO_FLOOR_SEND_RECEIVE_PORT));

	/**
	 * The floor subsystem send/receive port for the elevator subsystem
	 */
	private static final int FlOOR_TO_ELEVATOR_SEND_RECEIVE_PORT = 13000;

	/**
	 * The floor subsystem send/receive port for the scheduler
	 */
	private static final int FlOOR_TO_SCHEDULER_SEND_RECEIVE_PORT = 13001;

	/**
	 * The floor port mapping that provides the appropriate port for a given
	 * subsystem.
	 */
	public static final Map<SubsystemComponentType, Integer> FLOOR_PORT_MAPPING = Map.ofEntries(
			Map.entry(SubsystemComponentType.SCHEDULER, FlOOR_TO_SCHEDULER_SEND_RECEIVE_PORT),
			Map.entry(SubsystemComponentType.ELEVATOR_SUBSYSTEM, FlOOR_TO_ELEVATOR_SEND_RECEIVE_PORT));
	

	/**
	 * The ip addresses of the scheduler,floor, and elevator
	 */
	public static String SCHEDULER_IP_ADDRESS, FLOOR_IP_ADDRESS, ELEVATOR_IP_ADDRESS, GUI_IP_ADDRESS;

	static {
		try {
			SCHEDULER_IP_ADDRESS = InetAddress.getLocalHost().getHostAddress();
			FLOOR_IP_ADDRESS = InetAddress.getLocalHost().getHostAddress();
			ELEVATOR_IP_ADDRESS = InetAddress.getLocalHost().getHostAddress();
			GUI_IP_ADDRESS = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	/**
	 * A private SubsystemCommunicationConfiguarations constructor
	 */
	private SubsystemCommunicationConfigurations() {
	}

	/**
	 * Get
	 *
	 * @param sourceSubsystemType the source subsystem type
	 * @param targetSubsystemType the target subsystem type
	 * @return
	 */
	public static SubsystemCommunicationInfo getSourceSubsystemCommunicationInfo(
			SubsystemComponentType sourceSubsystemType, SubsystemComponentType targetSubsystemType) {

		SubsystemCommunicationInfo communicationInfo = null;

		switch (sourceSubsystemType) {
		case SCHEDULER:
			communicationInfo = new SubsystemCommunicationInfo(SCHEDULER_IP_ADDRESS,
					SCHEDULER_PORT_MAPPING.get(targetSubsystemType));
			break;

		case FLOOR_SUBSYSTEM:
			communicationInfo = new SubsystemCommunicationInfo(FLOOR_IP_ADDRESS,
					FLOOR_PORT_MAPPING.get(targetSubsystemType));

			break;

		case ELEVATOR_SUBSYSTEM:
			communicationInfo = new SubsystemCommunicationInfo(ELEVATOR_IP_ADDRESS,
					ELEVATOR_PORT_MAPPING.get(targetSubsystemType));
			break;
		case GUI:
			
			break;
		}
		
		
		return communicationInfo;
	}

}
