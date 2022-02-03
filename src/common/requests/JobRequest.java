/**
 * This class stores the details for a job request.
 *
 * @author paulokenne, jacobcharpentier
 *
 */

package common.requests;

import common.SimulationFloorInputData;

public class JobRequest extends Request {

	/**
	 * The input data.
	 */
	private SimulationFloorInputData inputData;

	/**
	 * The flood id.
	 */
	private int floorId;

	/**
	 * The elevator Id.
	 */
	private int elevatorId;

	/**
	 * A flag indicating whether a job has been completed.
	 */
	private boolean isJobCompleted;

	/**
	 * A flag indicating whether a job failed.
	 */
	private boolean isJobFailed;

	/**
	 * A Constructor.
	 **/
	public JobRequest(SimulationFloorInputData inputData) {
		super(RequestType.JOB_REQUEST);
		this.inputData = inputData;
		this.floorId = inputData.getCurrentFloor();
	}

	/**
	 * Get the elevator id.
	 *
	 * @return the elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}

	/**
	 * Set the elevator id.
	 *
	 * @param elevatorId the elevatorId to set
	 */
	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	/**
	 * Check if the job is completed.
	 *
	 * @return the isJobCompleted
	 */
	public boolean isJobCompleted() {
		return isJobCompleted;
	}

	/**
	 * Set the isCobCompleted flag.
	 *
	 * @param isJobCompleted the isJobCompleted to set
	 */
	public void setJobCompleted(boolean isJobCompleted) {
		this.isJobCompleted = isJobCompleted;
	}

	/**
	 * Check if the job has failed.
	 *
	 * @return the isJobFailed
	 */
	public boolean isJobFailed() {
		return isJobFailed;
	}

	/**
	 * Set the isJobFailed flag.
	 *
	 * @param isJobFailed the isJobFailed to set
	 */
	public void setJobFailed(boolean isJobFailed) {
		this.isJobFailed = isJobFailed;
	}

	/**
	 * Get the input data.
	 *
	 * @return the inputData
	 */
	public SimulationFloorInputData getInputData() {
		return inputData;
	}

	/**
	 * Get the floor id.
	 *
	 * @return the floorId
	 */
	public int getFloorId() {
		return floorId;
	}

}
