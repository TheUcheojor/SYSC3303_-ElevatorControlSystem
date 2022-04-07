package common.messages.scheduler;

import common.Direction;
import common.messages.Message;
import common.messages.MessageType;

/**
 * This class is used to pass the completed drop off request from the scheduler to the floor
 * @author bamideleoluwayemi
 *
 */
public class PassengerDropoffCompletedMessage extends SchedulerFloorCommand{

	/**
	 * The floor id
	 */
	int floorInputDataId;
	
	/** 
	 * The PassengerDropoffCompletedMessage constructor
	 * @param floorInputDataId
	 */
	public PassengerDropoffCompletedMessage(int floorInputDataId) {
		super(FloorCommand.PASSENGER_DROP_OFF_COMPLETE, 0, null);
		this.floorInputDataId = floorInputDataId;
	}

	/**
	 * @return the floorInputDataId
	 */
	public int getFloorInputDataId() {
		return floorInputDataId;
	}
	
	
	
}
