package common.messages.elevator;

import java.util.Date;

import common.DateFormat;
import common.messages.Message;
import common.messages.MessageType;
import common.messages.SchedulerElevatorTargetedMessage;

/**
 * An elevator status request
 *
 * @author Ryan Fife
 *
 */
public class ElevatorStatusRequest extends SchedulerElevatorTargetedMessage {
	public ElevatorStatusRequest(int elevatorId) {
		super(MessageType.ELEVATOR_STATUS_REQUEST, elevatorId);
	}
}
