/**
 * This class stores the details for a job request.
 *
 * @author paulokenne, jacobcharpentier
 *
 */

package common.messages.floor;

import common.SimulationFloorInputData;
import common.messages.Message;
import common.messages.MessageType;

public class JobRequest extends Message {
	private SimulationFloorInputData inputData;
	private int floorId;
	private int elevatorId;
	private boolean isJobCompleted;
	private boolean isJobFailed;

	public JobRequest(SimulationFloorInputData inputData) {
		super(MessageType.JOB_REQUEST);
		this.inputData = inputData;
		this.floorId = inputData.getCurrentFloor();
	}

	public int getElevatorId() {
		return elevatorId;
	}

	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	public boolean isJobCompleted() {
		return isJobCompleted;
	}

	public void setJobCompleted(boolean isJobCompleted) {
		this.isJobCompleted = isJobCompleted;
	}

	public boolean isJobFailed() {
		return isJobFailed;
	}

	public void setJobFailed(boolean isJobFailed) {
		this.isJobFailed = isJobFailed;
	}

	public SimulationFloorInputData getInputData() {
		return inputData;
	}

	public int getFloorId() {
		return floorId;
	}
}
