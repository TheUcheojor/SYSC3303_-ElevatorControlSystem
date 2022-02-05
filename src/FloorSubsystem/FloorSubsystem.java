package FloorSubsystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import common.SimulationFloorInputData;
import common.messages.JobRequest;
import common.messages.MessageChannel;

/**
 * This class simulates the FloorSubsystem thread
 *
 * @author Favour
 * @author Delight
 */
public class FloorSubsystem implements Runnable {
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
	private FloorInfo floor = new FloorInfo();

	/**
	 * The floor subsystem transmission message channel.
	 */
	private MessageChannel floorSubsystemTransmissonChannel;

	/**
	 * The floor subsystem transmission message channel.
	 */
	private MessageChannel floorSubsystemReceiverChannel;

	/**
	 * This is the default constructor of the class
	 *
	 * @param inputFileName       - The input text file
	 * @param floorMessageChannel - The message channel for communicating with the
	 *                            scheduler
	 */
	public FloorSubsystem(String inputFileName, MessageChannel floorSubsystemTransmissonChannel,
			MessageChannel floorSubsystemReceiverChannel) {
		this.inputFileName = inputFileName;
		this.floorSubsystemTransmissonChannel = floorSubsystemTransmissonChannel;
		this.floorSubsystemReceiverChannel = floorSubsystemReceiverChannel;

	}

	/**
	 * This is a secondary constructor for testing purposes
	 *
	 * @param inputData           - A simulation input data object
	 * @param floorMessageChannel - The message channel for communicating with the
	 *                            scheduler
	 */
	public FloorSubsystem(SimulationFloorInputData inputData, MessageChannel floorSubsystemTransmissonChannel,
			MessageChannel floorSubsystemReceiverChannel) {
		floorDataCollection.add(inputData);
		this.floorSubsystemTransmissonChannel = floorSubsystemTransmissonChannel;
		this.floorSubsystemReceiverChannel = floorSubsystemReceiverChannel;
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
		}

		String input = "";

		try {
			while ((input = bufferedReader.readLine()) != null) {
				floorDataCollection.add(new SimulationFloorInputData(input));
			}
		} catch (IOException e) {
			e.printStackTrace();
			// stop runnig the program if input data is unavailable
			System.exit(1);
		}
	}

	/**
	 * This is an overide of the runnable run method
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
				floor.pressFloorButton(floorInputData.getFloorDirectionButton());
				floor.setFloorNumber(floorInputData.getCurrentFloor());

				floor.printFloorStatus();

				// sending the job to the scheduler
				floorSubsystemTransmissonChannel.setMessage(jobRequest);
			}

			// Checking the scheduler has sent a message back
			if (!floorSubsystemReceiverChannel.isEmpty()) {
				JobRequest jobRequest = (JobRequest) floorSubsystemReceiverChannel.getMessage();

				floor.messageRecieved(jobRequest.isJobCompleted());
				floor.setFloorNumber(jobRequest.getFloorId());
			}

		}

	}

}
