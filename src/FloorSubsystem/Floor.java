package FloorSubsystem;

import ElevatorSubsystem.ElevatorCar;
import ElevatorSubsystem.ElevatorMotor;
import common.Direction;
import common.messages.MessageChannel;

/**
 * This class stores all the necessary properties of a floor thread
 *
 * @author Favour, Delight, paulokenne
 */
public class Floor {

	/**
	 * The floor number
	 */
	private int floorNumber;

	/**
	 * The up lamp button
	 */
	private LampButton upLampButton = null;

	/**
	 * The down lamp button
	 */
	private LampButton downLampButton = null;

	/**
	 * The elevator components (arrival sensor and elevatorDirection lamp), one per elevator
	 * shaft
	 *
	 * Elevators are given identification from 0 to NUMBER_OF_ELEVATORS. Hence, they
	 * can be mapped with an array.
	 */
	private FloorElevatorComponents[] elevatorComponents = new FloorElevatorComponents[ElevatorCar.NUMBER_OF_ELEVATORS];

	/**
	 * A FloorInfo constructor
	 *
	 * @param floorNumber the floor number
	 */
	public Floor(int floorNumber) {

		for (int i = 0; i < elevatorComponents.length; i++) {
			elevatorComponents[i] = new FloorElevatorComponents(floorNumber, i);
		}

		// Create buttons depending on the floor number
		if (floorNumber == 0) {
			downLampButton = new LampButton(Direction.DOWN);
		} else if (floorNumber == FloorSubsystem.NUMBER_OF_FLOORS - 1) {
			upLampButton = new LampButton(Direction.UP);
		} else {

			upLampButton = new LampButton(Direction.UP);
			downLampButton = new LampButton(Direction.DOWN);
		}
	}

	/**
	 * Notifies the elevator that it has arrived at the elevator.
	 *
	 * @param elevatorId                       the elevator id
	 * @param elevatorMotor                    the elevator motor
	 * @param elevatorSubsystemReceiverChannel the elevator subsystem receiver
	 *                                         channel
	 * @param isFloorFinalDestination          the flag indicating whether the floor
	 *                                         is the destination floor
	 */
	public void notifyElevatorAtFloorArrival(int elevatorId, ElevatorMotor elevatorMotor,
			MessageChannel elevatorSubsystemReceiverChannel, boolean isFloorFinalDestination) {
		elevatorComponents[elevatorId].notifyElevatorAtFloorArrival(elevatorId, elevatorMotor,
				elevatorSubsystemReceiverChannel, isFloorFinalDestination);
	}

	/**
	 * This method simulates a button press to a floor system
	 *
	 * @param elevatorDirection - the elevatorDirection selected by the user
	 */
	public void pressFloorButton(Direction direction) {
		switch (direction) {
		case UP:
			if (upLampButton != null) {
				upLampButton.press();
			}
			break;
		case DOWN:
			if (downLampButton != null) {
				downLampButton.press();
			}
			break;
		}
	}

	/**
	 * This method prints out the status of the floor depending on its state
	 */
	public void printFloorStatus() {

		if (upLampButton != null && upLampButton.isButtonPressed()) {
			System.out.println("The user has pushed the UP floor button at floor: " + floorNumber + " ..");
		}

		if (downLampButton != null && downLampButton.isButtonPressed()) {
			System.out.println("The user has pushed the DOWN floor button at floor: " + floorNumber + " ..");
		}

		for (FloorElevatorComponents elevatorSensor : elevatorComponents) {
			if (elevatorSensor.getArrivalSensorState()) {
				System.out.println("The elevator has arrived at floor: " + floorNumber + " ..");
			}
		}
	}

	/**
	 * The elevator is leaving the floor
	 */
	public void elevatorLeavingFloor(int elevatorId) {
		elevatorComponents[elevatorId].elevatorLeavingFloor();
	}

	/**
	 * @return the upLampButton
	 */
	public LampButton getUpLampButton() {
		return upLampButton;
	}

	/**
	 * @return the downLampButton
	 */
	public LampButton getDownLampButton() {
		return downLampButton;
	}

	/**
	 * @return the elevatorComponents
	 */
	public FloorElevatorComponents[] getElevatorComponents() {
		return elevatorComponents;
	}

	/**
	 * This method returns the floor number
	 *
	 * @return floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

}
