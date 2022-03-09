package Scheduler;

import java.util.ArrayList;

import common.Direction;
import common.messages.ElevatorJobMessage;

/**
 * This class represents the job management for an elevator.
 *
 * @author bamideleoluwayemi
 *
 */
public class ElevatorJobManagement {

	/**
	 * The max job that a elevator can manage
	 */
	public static int ELEVATOR_JOB_THRESHOLD = 5;

	/**
	 * The elevator's jobs
	 */
	private ArrayList<ElevatorJobMessage> elevatorJobs = new ArrayList<>();

	/**
	 * The elevator's current floor
	 *
	 * Our system assumes that at start up, elevators begin at the ground floor
	 */
	private int currentFloorNumber = 0;

	/**
	 * The elevator's directon
	 */
	private Direction elevatorDirection = Direction.IDLE;

	/**
	 * The elevator's id
	 */
	private int elevatorId;

	/**
	 * The error state
	 */
	private Exception errorState = null;

	/**
	 * An idicator signifying if an elevator is ready for a job or not.
	 */
	private boolean readyForJob;

	/**
	 * THe constructor for ElevatorJobManagement
	 *
	 * @param elevatorId
	 */
	public ElevatorJobManagement(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	/**
	 * Add an elevator job
	 *
	 * @param elevatorJob the elevator job
	 */
	public synchronized void addJob(ElevatorJobMessage elevatorJob) {
		elevatorJobs.add(elevatorJob);
		notifyAll();
	}

	/**
	 * Return a flag indicating whether the elevator is in an error state
	 *
	 * @return true if elevator is in an error state; otherise, return false
	 */
	public synchronized boolean isElevatorInError() {
		notifyAll();
		return errorState != null;
	}

	/**
	 * Return a flag indicating whether the elevator is running a job
	 *
	 * @return true if the elevator is running a job; otherwise, return false
	 */
	public synchronized boolean isRunningJob() {
		notifyAll();
		return elevatorDirection != Direction.IDLE;
	}

	/**
	 * Return a flag indicating whether the elevator is ready for a job
	 * 
	 * @return true if the elevator is ready for a job; otherwise, return false
	 */
	public synchronized boolean isReadyForJob() {
		notifyAll();
		return readyForJob;
	}
	
	/**
	 * Return a flag indicating whether the elevator is at or greater than the job
	 * Threshold
	 *
	 * @return true if the elevator is at or greater than the job Threshold;
	 *         otherwise, return false
	 */
	public synchronized boolean isAtElevatorJobThreshold() {
		notifyAll();
		return elevatorJobs.size() >= ELEVATOR_JOB_THRESHOLD;
	}

	/**
	 * Return the elevator direction
	 *
	 * @return the elevatorDirection
	 */
	public synchronized Direction getElevatorDirection() {
		notifyAll();
		return elevatorDirection;
	}
	
	/**
	 * Set the current direction that the elevator is heading towards
	 * 
	 * @param elevatorDirection
	 */
	public synchronized void setElevatorDirection(Direction elevatorDirection) {
		this.elevatorDirection = elevatorDirection;
		notifyAll();
	}

	/**
	 * Set that the elevator is ready for a job
	 * 
	 * @param readyForJob the readyForJob to set
	 */
	public void setReadyForJob(boolean readyForJob) {
		this.readyForJob = readyForJob;
		notifyAll();
	}

	/**
	 * Get the elevator id
	 *
	 * @return the elevatorId
	 */
	public synchronized int getElevatorId() {
		notifyAll();
		return elevatorId;
	}

	/**
	 * Set the error state
	 *
	 * @param errorState the error state to set
	 */
	public synchronized void setErrorState(Exception errorState) {
		this.errorState = errorState;
		notifyAll();
	}

	/**
	 * Get the largest floor destination floor
	 *
	 * @return the largest floor destination floor
	 *
	 */
	public synchronized int getLargestDestinationFloor() {
		if (elevatorJobs.isEmpty()) {
			return -1;
		}

		int largestDestinationFloor = elevatorJobs.get(0).getDestinationFloor();

		for (int i = 0; i < elevatorJobs.size(); i++) {
			if (elevatorJobs.get(i).getDestinationFloor() > largestDestinationFloor) {
				largestDestinationFloor = elevatorJobs.get(i).getDestinationFloor();
			}
		}
		notifyAll();
		return largestDestinationFloor;
	}

	/**
	 * Get the smallest floor destination floor
	 *
	 * @return the smallest floor destination floor
	 *
	 */
	public synchronized int getSmallestDestinationFloor() {
		if (elevatorJobs.isEmpty()) {
			return -1;
		}

		int smallestDestinationFloor = elevatorJobs.get(0).getDestinationFloor();

		for (int i = 0; i < elevatorJobs.size(); i++) {
			if (elevatorJobs.get(i).getDestinationFloor() < smallestDestinationFloor) {
				smallestDestinationFloor = elevatorJobs.get(i).getDestinationFloor();
			}
		}
		notifyAll();
		return smallestDestinationFloor;

	}

	/**
	 * Get the jobs at a given floor number
	 *
	 * @param floorNumber the floor number
	 * @return the jobs at a given floor number
	 */
	public synchronized ArrayList<ElevatorJobMessage> getJobsAtFloorNumber(int floorNumber) {

		ArrayList<ElevatorJobMessage> jobsAtFloorNumber = new ArrayList<>();

		for (int i = 0; i < elevatorJobs.size(); i++) {
			if (elevatorJobs.get(i).getDestinationFloor() == floorNumber) {
				jobsAtFloorNumber.add(elevatorJobs.get(i));
			}
		}

		notifyAll();
		return jobsAtFloorNumber;
	}

	/**
	 * Remove the given jobs from the elevator's jobs
	 *
	 * @param ElevatorJobMessage
	 */
	public synchronized void removeJobs(ArrayList<ElevatorJobMessage> jobs) {
		elevatorJobs.removeAll(jobs);
		notifyAll();
	}

	/**
	 * Get the elevator's current floor number
	 *
	 * @return the currentFloorNumber
	 */
	public synchronized int getCurrentFloorNumber() {
		notifyAll();
		return currentFloorNumber;

	}

	/**
	 * Set the elevator's current floor number
	 *
	 * @param currentFloorNumber the currentFloorNumber to set
	 */
	public synchronized void setCurrentFloorNumber(int currentFloorNumber) {
		this.currentFloorNumber = currentFloorNumber;
		notifyAll();
	}

}
