package common.gui;

import common.messages.elevator.ElevatorStatusMessage;

public interface ElevatorControllerObserver {

	public void handleStatusUpdate(ElevatorStatusMessage message);
}
