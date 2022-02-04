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
public class FloorSubsystem implements Runnable{
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
	 * The floor subsystem message channel.
	 */
	private MessageChannel floorMessageChannel;

	/**
	 * This is the default constructor of the class
	 * 
	 * @param inputFileName - The input text file
	 * @param floorMessageChannel - The message channel for communicating with the scheduler
	 */
	public FloorSubsystem(String inputFileName, MessageChannel floorMessageChannel) {
		this.inputFileName = inputFileName;
		this.floorMessageChannel = floorMessageChannel;
	}
	
	/**
	 * This is a secondary constructor for testing purposes 
	 * @param inputData - A simulation input data object
	 * @param floorMessageChannel - The message channel for communicating with the scheduler
	 */
	public FloorSubsystem(SimulationFloorInputData inputData, MessageChannel floorMessageChannel) {
		floorDataCollection.add(inputData);
		this.floorMessageChannel = floorMessageChannel;
	}
	/**
	 * This method reads in the input text file and converts
	 * it to SimulationFloorInputData objects as needed
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
			while((input = bufferedReader.readLine()) != null) {
				
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
	public void run() {
		// Only attempt to read file when a file name as been passed
		if(!inputFileName.equals("")) readInputFile();
		
		boolean messageSent = false;
		for(SimulationFloorInputData floorInputData: floorDataCollection) {
			// creating a job to be sent to scheduler
			JobRequest jobRequest = new JobRequest(floorInputData);
			
			// checking the channel is empty and no message has been sent
			if(floorMessageChannel.isEmpty() && !messageSent) {	
				//updating the floor properties(User interacting with the floor button)
				floor.pressFloorButton(floorInputData.getFloorDirectionButton());
				floor.setFloorNumber(floorInputData.getCurrentFloor());
				
				//sending the job to the scheduler
				floorMessageChannel.setMessage(jobRequest);
				messageSent = true;
				floor.printFloorStatus();
				
			}
		
			
			// Give the scheduler time to get the request.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			// Checking the scheduler has sent a message back
			if(!floorMessageChannel.isEmpty() && messageSent) {
				jobRequest = (JobRequest) floorMessageChannel.getMessage();
				floor.messageRecieved(jobRequest.isJobCompleted());
				floor.setFloorNumber(jobRequest.getFloorId());
				floor.printFloorStatus();
				messageSent = false;
			}
		}
	}

}