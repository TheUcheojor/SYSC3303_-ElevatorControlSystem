package Scheduler;

import java.util.ArrayList;

import common.Direction;
import common.messages.ElevatorJobMessage;

/**
 * 
 * @author bamideleoluwayemi
 *
 */
public class ElevatorJobManagement {
	
	private ArrayList<ElevatorJobMessage> elevatorJobs= new ArrayList<>();
	
	private int currentFloorNumber;
	
	private Direction elevatorDirection;
	
	private int elevatorId;
	
	private Exception errorState;
	
	private boolean readyForJob;
	
	public static int ELEVATOR_JOB_THRESHOLD = 5;
	
	
	/**
	 * THe constructor for ElevatorJobManagement
	 * @param elevatorId
	 */
	public ElevatorJobManagement(int elevatorId ) {
		currentFloorNumber = 0;
		elevatorDirection = Direction.IDLE;
		this.elevatorId = elevatorId;
		readyForJob = true;
		

	}


	public synchronized boolean isElevatorInError() {
		notifyAll();
		return errorState != null;
	}

	/**
	 * @return the readyForJob
	 */
	public synchronized boolean isReadyForJob() {
		notifyAll();
		return readyForJob;
	}


	/**
	 * @param readyForJob the readyForJob to set
	 */
	public synchronized void setReadyForJob(boolean readyForJob) {
		this.readyForJob = readyForJob;
		notifyAll();
	}



	public synchronized boolean isAtElevatorJobThreshold() {
		notifyAll();
		return elevatorJobs.size() >= ELEVATOR_JOB_THRESHOLD;
	}


	/**
	 * @return the elevatorDirection
	 */
	public synchronized Direction getElevatorDirection() {
		notifyAll();
		return elevatorDirection;
	}


	/**
	 * @return the elevatorId
	 */
	public synchronized int getElevatorId() {
		notifyAll();
		return elevatorId;
	}


	/**
	 * @param errorState the errorState to set
	 */
	public synchronized void setErrorState(Exception errorState) {
		this.errorState = errorState;
		notifyAll();
	}
	
	/**
	 * 
	 * @return
	 */
	public synchronized int getLargestDestinationFloor() {
		if (elevatorJobs.isEmpty()) {
			return -1;
		}
		
		int largestDestinationFloor = elevatorJobs.get(0).getDestinationFloor();
		
		for (int i = 0 ; i < elevatorJobs.size(); i++) {
			if (elevatorJobs.get(i).getDestinationFloor() > largestDestinationFloor) {
				largestDestinationFloor = elevatorJobs.get(i).getDestinationFloor();
			}
		}
		notifyAll();
		return largestDestinationFloor;
	}
	
	/**
	 * 
	 * @return
	 */
	public synchronized int getSmallestDestinationFloor() {
		if (elevatorJobs.isEmpty()) {
			return -1;
		}
		
		int smallestDestinationFloor = elevatorJobs.get(0).getDestinationFloor();
		
		for (int i = 0 ; i < elevatorJobs.size(); i++) {
			if (elevatorJobs.get(i).getDestinationFloor() < smallestDestinationFloor) {
				smallestDestinationFloor = elevatorJobs.get(i).getDestinationFloor();
			}
		}
		notifyAll();
		return smallestDestinationFloor;
		
	}
	
	public synchronized ArrayList<ElevatorJobMessage> getJobsAtFloorNumber(int floorNumber){
		
		ArrayList<ElevatorJobMessage> jobsAtFloorNumber = new ArrayList<>();
		
		for (int i = 0 ; i < elevatorJobs.size(); i++) {
			if (elevatorJobs.get(i).getDestinationFloor() == floorNumber) {
				jobsAtFloorNumber.add(elevatorJobs.get(i));		}
		}

		
		notifyAll();
		return jobsAtFloorNumber;
	}

	
	/**
	 * 
	 * @param ElevatorJobMessage
	 */
	public void removeJob(ElevatorJobMessage job) {
		elevatorJobs.remove(job);
		notifyAll();
	}


	/**
	 * @return the currentFloorNumber
	 */
	public int getCurrentFloorNumber() {
		notifyAll();
		return currentFloorNumber;
		
	}


	/**
	 * @param currentFloorNumber the currentFloorNumber to set
	 */
	public void setCurrentFloorNumber(int currentFloorNumber) {
		this.currentFloorNumber = currentFloorNumber;
		notifyAll();
	}
	
}
