package Scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import FloorSubsystem.FloorSubsystem;
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
	private Set<ElevatorJobMessage> elevatorJobs = new HashSet<>();

	/**
	 * The elevator's current floor
	 *
	 * Our system assumes that at start up, elevators begin at the ground floor
	 */
	private int currentFloorNumber = 0;

	/**
	 * The direction the elevator plans to travel to drop off the passenger
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
	 * An indicator signifying if an elevator is ready for a job or not.
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
	 * Add an elevator job to the primary jobs
	 *
	 * @param elevatorJob the elevator job
	 */
	public void addJob(ElevatorJobMessage elevatorJob) {
		elevatorJobs.add(elevatorJob);
	}

	/**
	 * Return a flag indicating whether the elevator is in an error state
	 *
	 * @return true if elevator is in an error state; otherwise, return false
	 */
	public boolean isElevatorInError() {
		return errorState != null;
	}

	/**
	 * Return a flag indicating whether the elevator is running a job
	 *
	 * @return true if the elevator is running a job; otherwise, return false
	 */
	public boolean isRunningJob() {
		return elevatorDirection != Direction.IDLE;
	}

	/**
	 * Return a flag indicating whether the elevator is ready for a job
	 *
	 * @return true if the elevator is ready for a job; otherwise, return false
	 */
	public boolean isReadyForJob() {
		return readyForJob;
	}

	/**
	 * Return a flag indicating whether the elevator is at or greater than the job
	 * Threshold
	 *
	 * @return true if the elevator is at or greater than the job Threshold;
	 *         otherwise, return false
	 */
	public boolean isAtElevatorJobThreshold() {
		return elevatorJobs.size() >= ELEVATOR_JOB_THRESHOLD;
	}

	/**
	 * Return the elevator direction
	 *
	 * @return the elevatorDirection
	 */
	public Direction getElevatorDirection() {
		return elevatorDirection;
	}

	/**
	 * Set the current direction that the elevator is heading towards
	 *
	 * @param elevatorDirection
	 */
	public void setElevatorDirection(Direction elevatorDirection) {
		this.elevatorDirection = elevatorDirection;
	}

	/**
	 * Set that the elevator is ready for a job
	 *
	 * @param readyForJob the readyForJob to set
	 */
	public void setReadyForJob(boolean readyForJob) {
		this.readyForJob = readyForJob;
	}

	/**
	 * Get the elevator id
	 *
	 * @return the elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}

	/**
	 * Set the error state
	 *
	 * @param errorState the error state to set
	 */
	public void setErrorState(Exception errorState) {
		this.errorState = errorState;
	}

	/**
	 * Get the largest floor destination floor below the current elevator floor. The
	 * job must be going in the same direction as the elevator.
	 *
	 * @return the largest floor destination floor
	 *
	 */
	public int getLargestDestinationFloorInElevatorDirection() {
		if (elevatorJobs.isEmpty()) {
			return -1;
		}

		int largestDestinationFloor = -1;

		Iterator<ElevatorJobMessage> elevatorJobsIterator = elevatorJobs.iterator();

		while (elevatorJobsIterator.hasNext()) {
			ElevatorJobMessage currentelevatorJob = elevatorJobsIterator.next();
			int jobDestinationFloor = currentelevatorJob.getDestinationFloor();

			// Find jobs that are in same direction as the elevator. We will
			// then check that the job destination floor is above than the current
			// largest destination floor. If so, we will replace the current smallest
			// destination floor.
			if (currentelevatorJob.getDirection() == elevatorDirection
					&& jobDestinationFloor > largestDestinationFloor) {
				largestDestinationFloor = jobDestinationFloor;
			}
		}
		return largestDestinationFloor;
	}

	/**
	 * Get the smallest job floor destination above the current elevator floor. The
	 * job must be going in the same direction as the elevator.
	 *
	 * @return the smallest floor destination floor
	 *
	 */
	public int getSmallestDestinationFloorInElevatorDirection() {
		if (elevatorJobs.isEmpty()) {
			return -1;
		}

		int smallestDestinationFloor = FloorSubsystem.NUMBER_OF_FLOORS;

		Iterator<ElevatorJobMessage> elevatorJobsIterator = elevatorJobs.iterator();
		while (elevatorJobsIterator.hasNext()) {
			ElevatorJobMessage currentelevatorJob = elevatorJobsIterator.next();
			int jobDestinationFloor = currentelevatorJob.getDestinationFloor();

			// Find jobs that are in same direction as the elevator. We will
			// then check that the job destination floor is smaller than the current
			// smallest destination floor. If so, we will replace the current smallest
			// destination floor.
			if (currentelevatorJob.getDirection() == elevatorDirection
					&& jobDestinationFloor < smallestDestinationFloor) {
				smallestDestinationFloor = jobDestinationFloor;
			}
		}

		return smallestDestinationFloor;
	}

	/**
	 * Get the jobs at a given floor number.
	 *
	 * @param floorNumber the floor number
	 * @param direction   the direction of the job
	 *
	 * @return the jobs at a given floor number
	 */
	public ArrayList<ElevatorJobMessage> getPrimaryJobsAtFloor(int floorNumber) {
		ArrayList<ElevatorJobMessage> jobsAtFloorNumber = new ArrayList<>();

		Iterator<ElevatorJobMessage> elevatorJobsIterator = elevatorJobs.iterator();

		while (elevatorJobsIterator.hasNext()) {
			ElevatorJobMessage currentelevatorJob = elevatorJobsIterator.next();

			if (currentelevatorJob.getDestinationFloor() == floorNumber) {
				jobsAtFloorNumber.add(currentelevatorJob);
			}
		}

		return jobsAtFloorNumber;
	}

	/**
	 * Remove the given jobs from the elevator's jobs
	 *
	 * @param ElevatorJobMessage
	 */
	public void removeJobs(ArrayList<ElevatorJobMessage> jobs) {
		elevatorJobs.removeAll(jobs);
	}

	/**
	 * Get the elevator's current floor number
	 *
	 * @return the currentFloorNumber
	 */
	public int getCurrentFloorNumber() {
		return currentFloorNumber;
	}

	/**
	 * Set the elevator's current floor number
	 *
	 * @param currentFloorNumber the currentFloorNumber to set
	 */
	public void setCurrentFloorNumber(int currentFloorNumber) {
		this.currentFloorNumber = currentFloorNumber;
	}

	/**
	 * Return whether an elevator has jobs
	 *
	 *
	 * @return true if the elevator has jobs; otherwise, return false
	 */
	public boolean hasJobs() {
		return elevatorDirection != Direction.IDLE;
	}

	/**
	 * Return whether an elevator has primary jobs,which are jobs that in the same
	 * direction of the elevator
	 *
	 *
	 * @return true if the elevator has primary jobs; otherwise, return false
	 */
	public boolean hasPrimaryJobs() {
		return elevatorJobs.stream().anyMatch(elevatorJobs -> elevatorJobs.getDirection() == elevatorDirection);
	}

	/**
	 * Return whether an elevator has secondary jobs, which are jobs that in the
	 * opposite direction of the elevator
	 *
	 * @return true if the elevator has secondary jobs
	 */
	public void loadSecondaryJobs() {
		Direction oppositeElevatorDirection = getOppositeElevatorDirection();
		setElevatorDirection(oppositeElevatorDirection);
	}

	/**
	 * Return whether an elevator has secondary jobs, which are jobs that in the
	 * opposite direction of the elevator
	 *
	 * @return true if the elevator has secondary jobs
	 */
	public boolean hasSecondaryJobs() {

		final Direction oppositeElevatorDirection = getOppositeElevatorDirection();

		// An idle elevator cannot have jobs
		if (oppositeElevatorDirection == Direction.IDLE) {
			return false;
		}

		// Return true if we have secondary jobs
		return elevatorJobs.stream().anyMatch(elevatorJobs -> elevatorJobs.getDirection() == oppositeElevatorDirection);
	}

	/**
	 * Get the direction opposite of the elevator's
	 *
	 * @return the opposite direction
	 */
	private Direction getOppositeElevatorDirection() {

		Direction oppositeElevatorDirection = Direction.IDLE;
		switch (elevatorDirection) {
		case UP:
			oppositeElevatorDirection = Direction.DOWN;
			break;
		case DOWN:
			oppositeElevatorDirection = Direction.UP;
			break;
		default:
			break;
		}

		return oppositeElevatorDirection;
	}

	/**
	 * Return the number of jobs in the direction of the elevator
	 *
	 * @return the number of primary jobs
	 */
	public int getNumberOfPrimaryJobs() {
		return (int) elevatorJobs.stream().filter(elevatorJobs -> elevatorJobs.getDirection() == elevatorDirection)
				.count();
	}

	/**
	 * Return the number of jobs
	 *
	 * @return the number of primary jobs
	 */
	public int getNumberOfJobs() {
		return elevatorJobs.size();
	}

}
