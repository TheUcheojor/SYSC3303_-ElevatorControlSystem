package FloorSubsystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import common.Direction;
import common.SimulationFloorInputData;
import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.messages.FloorElevatorTargetedMessage;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorFloorSignalRequestMessage;
import common.messages.elevator.ElevatorTransportRequest;
import common.messages.floor.ElevatorFloorRequest;
import common.messages.scheduler.SchedulerFloorCommand;

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
	private Floor[] floors = new Floor[NUMBER_OF_FLOORS];

	/**
	 * The name of the input text file
	 */
	private String inputFileName;

	/**
	 * Collection of the simulation input objects that have not been sent to the
	 * scheduler. Therefore, they are unassigned.
	 */
	private ArrayList<SimulationFloorInputData> unassignedFloorDataCollection = new ArrayList<>();

	/**
	 * Collection of the simulation input objects that have been sent to the
	 * scheduler. Therefore, they are assigned.
	 */
	private ArrayList<SimulationFloorInputData> assignedFloorDataCollection = new ArrayList<>();

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
				unassignedFloorDataCollection.add(new SimulationFloorInputData(input));
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
			if (floorSubsystemTransmissonChannel.isEmpty() && !unassignedFloorDataCollection.isEmpty()) {

				SimulationFloorInputData floorInputData = unassignedFloorDataCollection.get(0);
				unassignedFloorDataCollection.remove(0);

				ElevatorFloorRequest elevatorFloorRequest = new ElevatorFloorRequest(floorInputData.getCurrentFloor(),
						floorInputData.getFloorDirectionButton());

				// Updating the floor properties(User interacting with the floor button)
				int floorId = floorInputData.getCurrentFloor();
				floors[floorId].pressFloorButton(floorInputData.getFloorDirectionButton());
				floors[floorId].printFloorStatus();

				// sending the job to the scheduler
				floorSubsystemTransmissonChannel.appendMessage(elevatorFloorRequest);

				// Add the floor input data to the assigned floor data collection
				assignedFloorDataCollection.add(floorInputData);

			}

			// Checking if we have a request message
			if (!floorSubsystemReceiverChannel.isEmpty()) {
				handleRequest(floorSubsystemReceiverChannel.popMessage());
			}

		}

	}

	/**
	 * Get the floors
	 */
	public Floor[] getFloors() {
		return floors;
	}

	/**
	 * Handle message accordingly
	 *
	 * @param message the message
	 */
	public void handleRequest(Message message) {

		switch (message.getMessageType()) {

		case ELEVATOR_FLOOR_MESSAGE:
			handleElevatorRequest((FloorElevatorTargetedMessage) message);
			break;

		case SCHEDULER_FLOOR_COMMAND:
			handleSchedulerFloorCommand((SchedulerFloorCommand) message);
			break;

		default:
			break;
		}
	}

	/**
	 * Handle the elevator request appropriately
	 *
	 * @param request the request
	 */
	private void handleElevatorRequest(FloorElevatorTargetedMessage request) {

		int floorId = request.getFloorId();
		int elevatorId = request.getElevatorId();

		// Validate the floor id
		if (!SystemValidationUtil.isFloorNumberInRange(floorId)) {
			return;
		}

		switch (request.getRequestType()) {

		case ELEVATOR_FLOOR_SIGNAL_REQUEST:
			ElevatorFloorSignalRequestMessage floorSignalRequestMessage = (ElevatorFloorSignalRequestMessage) request;

			floors[floorId].notifyElevatorAtFloorArrival(floorId, elevatorId,
					floorSignalRequestMessage.getElevatorMotor(), elevatorSubsystemReceiverChannel,
					floorSignalRequestMessage.isFloorFinalDestination());
			break;

		case ELEVATOR_LEAVING_FLOOR_MESSAGE:
			floors[floorId].elevatorLeavingFloor(elevatorId);
			break;

		default:
			break;

		}
	}

	/**
	 * Handle scheduler floor command
	 *
	 * @param command the command
	 */
	private void handleSchedulerFloorCommand(SchedulerFloorCommand command) {

		int floorId = command.getFloorId();

		// Validate the floor id
		if (!SystemValidationUtil.isFloorNumberInRange(floorId)) {
			return;
		}

		switch (command.getCommand()) {

		case TURN_OFF_FLOOR_LAMP:
			floors[floorId].turnOffLampButton(command.getLampButtonDirection());

			ArrayList<Integer> destinationFloors = getFloorPassengerDestinationFloors(floorId,
					command.getLampButtonDirection());
			// For now, I will only send the one item in the destination floor collection
			// for this iteration.
			// The elevator and scheduler do not support more than one at the moment.
			//
			// TODO Send the full collection of destination floors. Requires cooperation
			// with the elevator and scheduler. Also, the id of the elevator needs to be
			// sent.
			// Since we have one elevator, we will hard code the elevator id of 0.
			int elevatorId = 0;
			ElevatorTransportRequest elevatorTransportRequest = new ElevatorTransportRequest(destinationFloors.get(0),
					elevatorId, command.getLampButtonDirection());
			elevatorSubsystemReceiverChannel.appendMessage(elevatorTransportRequest);
			break;

		default:
			break;
		}

	}

	/**
	 * Given the elevator direction, returns a floor's list of passenger
	 * destinations (car buttons pressed)
	 *
	 * @param floorId           the floor id
	 * @param elevatorDirection
	 * @return
	 */
	private ArrayList<Integer> getFloorPassengerDestinationFloors(int floorId, Direction elevatorDirection) {

		ArrayList<Integer> destinationFloors = new ArrayList<>();

		assignedFloorDataCollection.forEach(floorData -> {
			// Check whether the input data is at the required floor and the requested
			// direction is the same
			if (floorData.getCurrentFloor() == floorId
					&& floorData.getFloorDirectionButton().equals(elevatorDirection)) {
				destinationFloors.add(floorData.getDestinationFloorCarButton());
			}
		});

		return destinationFloors;
	}
}
