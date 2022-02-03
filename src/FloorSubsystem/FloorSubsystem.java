package FloorSubsystem;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;


/**
 * 
 * @author Favour
 * @author Delight
 */
public class FloorSubsystem implements Runnable{
	
	//private Scheduler scheduler;
	private String inputFile;
	//private ArrayList<SimulationFloorInputData> floorDataCollection;
	private FloorInfo floor;

	
	public FloorSubsystem(String inputFile) { // add scheduler
		this.inputFile = inputFile;
		//floorDataCollection = new ArrayList<>();
		floor = new FloorInfo();
		
	}
	
	private void readInputFile() {
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String input = "";
		
		try {
			while((input = bufferedReader.readLine()) != null) {
				
				//floorDataCollection.add(initializeInput(input.split(" ")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private FloorInfo initializeInput(String[] inputLine) {
		
		int timeStampIndex = 0;
		int floorNumberIndex = 1;
		int floorButtonIndex = 2;
		int carNumberIndex = 3;
		
		if(inputLine == null || (inputLine.length < 3)) {
			System.out.println("Faulty line found...Please check input file.");
		}else {
			String timeStamp = inputLine[timeStampIndex];
			int floorNumber = Integer.parseInt(inputLine[floorNumberIndex]);
			String direction = inputLine[floorButtonIndex];;
			int carButton = Integer.parseInt(inputLine[carNumberIndex]);
			//return new FloorInfo(timeStamp, direction, floorNumber, carButton);
		}
		return null;
	}

	public void run() {
		readInputFile();
		//for(FloorInfo f: floorDataCollection) {
			//System.out.println(f.toString());
		//}
	}

	public static void main(String[] args) {
		Thread fss = new Thread(new FloorSubsystem("c:\\Input.txt"));
		fss.start();
	}
}
