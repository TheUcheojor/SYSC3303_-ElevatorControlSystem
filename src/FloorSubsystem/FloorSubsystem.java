package FloorSubsystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import common.SimulationFloorInputData;
import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.messages.IdentifierDrivenMessage;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.floor.JobRequest;

/**
 * This class simulates the FloorSubsystem thread
 *
 * @author Favour, Delight, paulokenne
 */
public class FloorSubsystem implements Runnable {

	/**
	 * The number of floors
	 */
	public static final int NUMBER_OF_FLOORS = 3;

	/**
	 * The floor to floor distance in meters
	 */
	public static final double FLOOR_TO_FLOOR_DISTANCE = 4.5;

	/**
	 * The floors.
	 */
	private Floor[] floors = new Floor[NUMBER_OF_FLOORS];;

	/**
	 * The name of the input text file
	 */
	private String inputFileName = "";

	/**
	 * Collection of the simulation input objects
	 */
	private ArrayList<SimulationFloorInputData> floorDataCollection = new ArrayList<>();

	/**
	 * The object that stores the properties of the floor
	 */
	private Floor floor = new Floor(1);

	/**
	 * The floor subsystem transmission message channel.
	 */
	private MessageChannel floorSubsystemTransmissonChannel;

	/**
	 * The floor subsystem transmission message channel.
	 */
	private MessageChannel floorSubsystemReceiverChannel;

	/**
	 * The elevator subsystem transmission message channel.
	 */
	private MessageChannel elevatorSubsystemReceiverChannel;

	/**
	 * This is the default constructor of the class
	 *
	 * @param inputFileName       - The input text file
	 * @param floorMessageChannel - The message channel for communicating with the
	 *                            scheduler
	 */
	public FloorSubsystem(String inputFileName, MessageChannel floorSubsystemTransmissonChannel,
			MessageChannel floorSubsystemReceiverChannel, MessageChannel elevatorSubsystemReceiverChannel) {
		// Validate that the floor subsystem values are valid
		try {
			SystemValidationUtil.validateFloorToFloorDistance(FLOOR_TO_FLOOR_DISTANCE);
		} catch (InvalidSystemConfigurationInputException e) {
			System.out.println("InvalidSystemConfigurationInputException: " + e);
			// Terminate if the elevation configuration are invalid.
			System.exit(1);
		}

		this.inputFileName = inputFileName;
		this.floorSubsystemTransmissonChannel = floorSubsystemTransmissonChannel;
		this.floorSubsystemReceiverChannel = floorSubsystemReceiverChannel;
		this.elevatorSubsystemReceiverChannel = elevatorSubsystemReceiverChannel;

		// Add floors to the floor subsystem
		for (int i = 0; i < floors.length; i++) {
			floors[i] = new Floor(i);
		}
	}

	/**
	 * This method reads in the input text file and converts it to
	 * SimulationFloorInputData objects as needed
	 *
	 */
	private void readInputFile() {
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new FileReader(inputFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String input = "";

		try {
			while ((input = bufferedReader.readLine()) != null) {
				floorDataCollection.add(new SimulationFloorInputData(input));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is an override of the runnable run method
	 */
	@Override
	public void run() {
		// Only attempt to read file when a file name as been passed
		if (!inputFileName.equals(""))
			readInputFile();

		while (true) {

			/**
			 * Place input data in the transmission channel if we have data to send and the
			 * transmission channel is free.
			 */
			if (floorSubsystemTransmissonChannel.isEmpty() && !floorDataCollection.isEmpty()) {

				SimulationFloorInputData floorInputData = floorDataCollection.get(0);
				floorDataCollection.remove(0);
				JobRequest jobRequest = new JobRequest(floorInputData);

				// updating the floor properties(User interacting with the floor button)
				int floorId = floorInputData.getCurrentFloor();
				floors[floorId].pressFloorButton(floorInputData.getFloorDirectionButton());
				floors[floorId].printFloorStatus();

				// sending the job to the scheduler
				floorSubsystemTransmissonChannel.appendMessage(jobRequest);
			}

			// Checking if we have a request message
			if (!floorSubsystemReceiverChannel.isEmpty()) {
				handleRequest(floorSubsystemReceiverChannel.popMessage());
			}

		}

	}

	/**
	 * Handle message accordingly
	 *
	 * @param message the message
	 */
	public void handleRequest(Message message) {

		int sourceEntityId = -1;
		int floorId = -1;

		// Verifying the validity of the request
		if (message instanceof IdentifierDrivenMessage) {
			sourceEntityId = ((IdentifierDrivenMessage) message).getSourceEntityId();
			floorId = ((IdentifierDrivenMessage) message).getTargetEntityId();

			// Ignore request messages with an invalid floor id
			if (!SystemValidationUtil.isFloorNumberInRange(floorId)) {
				return;
			}
		}

		switch (message.getMessageType()) {

		case EVELATOR_FLOOR_SIGNAL_REQUEST:
			ElevatorFloorSignalRequestMessage floorSignalRequestMessage = (ElevatorFloorSignalRequestMessage) message;

			floors[floorId].notifyElevatorAtFloorArrival(sourceEntityId, floorSignalRequestMessage.getElevatorMotor(),
					elevatorSubsystemReceiverChannel, floorSignalRequestMessage.isFloorFinalDestination());
			break;

		case EVELATOR_LEAVING_FLOOR_MESSAGE:
			floors[floorId].elevatorLeavingFloor(sourceEntityId);
			break;

		default:
			break;
		}
	}

}
