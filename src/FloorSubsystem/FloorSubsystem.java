package FloorSubsystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import common.LoggerWrapper;
import common.SimulationFloorInputData;
import common.messages.Message;
import common.messages.floor.ElevatorFloorRequest;
import common.remote_procedure.SubsystemCommunicationRPC;
import common.remote_procedure.SubsystemComponentType;
import common.work_management.MessageWorkQueue;

/**
 * This class simulates the FloorSubsystem thread
 *
 * @author Favour, Delight, paulokenne, Jacob Charpentier, Ryan Fife
 */
public class FloorSubsystem {

	private Logger logger = LoggerWrapper.getLogger();
	/**
	 * Delay between sending requests from the floor input file to the scheduler.
	 * For more real life simulation purposes.
	 */
	private static final int SIMULATED_INPUT_DELAY_MS = 10000;

	/**
	 * The number of floors
	 */
	public static final int NUMBER_OF_FLOORS = 23;

	/**
	 * The floor to floor distance in meters
	 */
	public static final double FLOOR_TO_FLOOR_DISTANCE = 4.5;

	/**
	 * The floors.
	 */
	private Floor[] floors = new Floor[NUMBER_OF_FLOORS];

	/**
	 * The name of the input text file
	 */
	private String inputFileName;

	/**
	 * Collection of the simulation input objects.
	 */
	private ArrayList<SimulationFloorInputData> floorDataCollection = new ArrayList<>();

	/**
	 * Message queue for received elevator messages
	 */
	private FloorElevatorMessageWorkQueue elevatorMessageQueue;

	/**
	 * Message queue for received scheduler messages
	 */
	private FloorSchedulerMessageWorkQueue schedulerMessageQueue;

	/**
	 * Floor to Elevator UDP Communication
	 */
	private SubsystemCommunicationRPC floorElevatorUDP = new SubsystemCommunicationRPC(
			SubsystemComponentType.FLOOR_SUBSYSTEM, SubsystemComponentType.ELEVATOR_SUBSYSTEM);

	/**
	 * Floor to Scheduler UDP Communication
	 */
	private SubsystemCommunicationRPC floorSchedulerUDP = new SubsystemCommunicationRPC(
			SubsystemComponentType.FLOOR_SUBSYSTEM, SubsystemComponentType.SCHEDULER);

	/**
	 * This is the default constructor of the class
	 *
	 * @param inputFileName       - The input text file
	 * @param floorMessageChannel - The message channel for communicating with the
	 *                            scheduler
	 */
	public FloorSubsystem(String inputFileName, double elevatorFloorToFloorTimeMilliseconds) {

		this.inputFileName = inputFileName;

		// Add floors to the floor subsystem
		for (int i = 0; i < floors.length; i++) {
			floors[i] = new Floor(i, elevatorFloorToFloorTimeMilliseconds);
		}

		elevatorMessageQueue = new FloorElevatorMessageWorkQueue(floorSchedulerUDP, floorElevatorUDP, floors);
		schedulerMessageQueue = new FloorSchedulerMessageWorkQueue(floorSchedulerUDP, floorElevatorUDP, floors,
				floorDataCollection);

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
			int id = 0;
			while ((input = bufferedReader.readLine()) != null) {
				floorDataCollection.add(new SimulationFloorInputData(id, input));
				id++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is the Main function
	 */
	public void runMain() {
		// Only attempt to read file when a file name as been passed
		if (!inputFileName.equals(""))
			readInputFile();

		// initialize the message receiving threads
		setUpMessageQueueing(floorElevatorUDP, elevatorMessageQueue);
		setUpMessageQueueing(floorSchedulerUDP, schedulerMessageQueue);
		(new Thread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				// wait for scheduler messages

				for (SimulationFloorInputData floorInputData : (ArrayList<SimulationFloorInputData>) floorDataCollection
						.clone()) {

					ElevatorFloorRequest elevatorFloorRequest = new ElevatorFloorRequest(
							floorInputData.getCurrentFloor(), floorInputData.getFloorDirectionButton(),
							floorInputData.getInputDataId(), floorInputData.getFault(), floorInputData.getFaultFloor());

					// Updating the floor properties(User interacting with the floor button)
					int floorId = floorInputData.getCurrentFloor();
					floors[floorId].pressFloorButton(floorInputData.getFloorDirectionButton());

					// sending the job to the scheduler
					try {
						floorSchedulerUDP.sendMessage(elevatorFloorRequest);
						Thread.sleep(SIMULATED_INPUT_DELAY_MS);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}).start();

		(new Thread() {
			@Override
			public void run() {
				long startTime = System.nanoTime();
				while (true) {
					synchronized (floorDataCollection) {
						if (floorDataCollection.isEmpty()) {
							break;
						}
					}
				}
				/* … The code being measured ends … */
				long endTime = System.nanoTime();

				// get the difference between the two nano time valuess
				long timeElapsed = endTime - startTime;

				logger.info("Execution time in milliseconds: " + timeElapsed / 1000000);
			}
		}).start();
	}

	/**
	 * Get the floors
	 */
	public Floor[] getFloors() {
		return floors;
	}

	/**
	 * The function sets up the message queue
	 *
	 * @param communication
	 * @param workQueue
	 */
	private void setUpMessageQueueing(SubsystemCommunicationRPC communication, MessageWorkQueue workQueue) {
		(new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Message message = communication.receiveMessage();
						workQueue.enqueueMessage(message);
					} catch (Exception e) {
						System.out.println(e);
						System.exit(1);
					}
				}
			}
		}).start();
	}
	
	public static void main(String[] args) {
		// Only attempt to read file when a file name as been passed
		String inputFileName = "resources/FloorInputFile.txt";
		double elevatorFloorToFloorTimeSeconds = 3.5;

		FloorSubsystem subsystem = new FloorSubsystem(inputFileName, elevatorFloorToFloorTimeSeconds);
		subsystem.runMain();
	}
}
