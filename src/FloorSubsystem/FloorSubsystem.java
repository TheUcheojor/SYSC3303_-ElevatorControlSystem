package FloorSubsystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import common.SimulationFloorInputData;
import common.requests.JobRequest;
import common.requests.MessageChannel;


/**
 * This class simulates the FloorSubsystem thread
 * 
 * @author Favour
 * @author Delight
 */
public class FloorSubsystem implements Runnable{
	
	private String inputFileName;
	private ArrayList<SimulationFloorInputData> floorDataCollection;
	private FloorInfo floor;
	private MessageChannel floorMessageChannel;

	/**
	 * This is the default constructor of the class
	 * 
	 * @param inputFileName - The input text file
	 */
	public FloorSubsystem(String inputFileName) {
		this.inputFileName = inputFileName;
		floorDataCollection = new ArrayList<>();
		floor = new FloorInfo();
		floorMessageChannel = new MessageChannel();
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
		}
	}
	

	/**
	 * This is an overide of the runnable run method
	 */
	public void run() {
		readInputFile();
		
		for(SimulationFloorInputData floorInputData: floorDataCollection) {
			// creating a job to be sent to scheduler
			JobRequest jobRequest = new JobRequest(floorInputData);
			
			//updating the floor properties(User interacting with the floor button)
			floor.pressFloorButton(floorInputData.getFloorDirectionButton());
			floor.setFloorNumber(floorInputData.getCurrentFloor());
			
			//sending the job to the scheduler
			floorMessageChannel.setMessage(jobRequest);
			floor.printFloorStatus();
			
			// Checking the scheduler has sent a message back
			if(!floorMessageChannel.isEmpty()) {
				jobRequest = (JobRequest) floorMessageChannel.getMessage();
				floor.messageRecieved(jobRequest.isJobCompleted());
				floor.setFloorNumber(jobRequest.getFloorId());
				floor.printFloorStatus();
			}
		}
	}

}
