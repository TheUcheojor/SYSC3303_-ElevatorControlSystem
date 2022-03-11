package FloorSubsystem;

import ElevatorSubsystem.ElevatorController;
import ElevatorSubsystem.ElevatorMotor;
import common.Direction;
import common.remote_procedure.SubsystemCommunicationRPC;

/**
 * This class stores all the necessary properties of a floor thread
 *
 * @author Favour, Delight, paulokenne, Jacob
 */
public class Floor {

	/**
	 * The floor number
	 */
	private int floorNumber;

	/**
	 * The up lamp button
	 */
	private DirectionLampButton upLampButton = null;

	/**
	 * The down lamp button
	 */
	private DirectionLampButton downLampButton = null;

	/**
	 * The elevator components (arrival sensor and direction lamp), one per elevator
	 * shaft
	 *
	 * Elevators are given identification from 0 to NUMBER_OF_ELEVATORS - 1. Hence,
	 * they can be mapped with an array.
	 */
	private final static FloorElevatorComponents[] ELEVATOR_COMPONENTS = new FloorElevatorComponents[ElevatorController.NUMBER_OF_ELEVATORS];
	static {
		for (int j = 0; j < ELEVATOR_COMPONENTS.length; j++) {
			ELEVATOR_COMPONENTS[j] = new FloorElevatorComponents(j);
		}
	}

	/**
	 * A FloorInfo constructor
	 *
	 * @param floorNumber the floor number
	 */
	public Floor(int floorNumber) {

		// Create buttons depending on the floor number
		if (floorNumber == 0) {
			downLampButton = new DirectionLampButton(Direction.DOWN);
		} else if (floorNumber == FloorSubsystem.NUMBER_OF_FLOORS - 1) {
			upLampButton = new DirectionLampButton(Direction.UP);
		} else {

			upLampButton = new DirectionLampButton(Direction.UP);
			downLampButton = new DirectionLampButton(Direction.DOWN);
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
	public void notifyElevatorAtFloorArrival(int floorId, int elevatorId, ElevatorMotor elevatorMotor, SubsystemCommunicationRPC elevatorUDP, boolean isFloorFinalDestination) {
		ELEVATOR_COMPONENTS[elevatorId].notifyElevatorAtFloorArrival(floorId, elevatorMotor, elevatorUDP, isFloorFinalDestination);
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

		default:
			break;
		}
	}

	/**
	 * Turn off the lamp button with the given direction
	 *
	 * @param direction the direction of the button
	 */
	public void turnOffLampButton(Direction direction) {
		switch (direction) {
		case UP:
			if (upLampButton != null) {
				upLampButton.turnOff();
			}
			break;

		case DOWN:
			if (downLampButton != null) {
				downLampButton.turnOff();
			}
			break;

		default:
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

		for (FloorElevatorComponents elevatorSensor : ELEVATOR_COMPONENTS) {
			if (elevatorSensor.getArrivalSensorState()) {
				System.out.println("The elevator has arrived at floor: " + floorNumber + " ..");
			}
		}
	}

	/**
	 * The elevator is leaving the floor
	 */
	public void elevatorLeavingFloor(int elevatorId) {
		ELEVATOR_COMPONENTS[elevatorId].elevatorLeavingFloor();
	}

	/**
	 * @return the upLampButton
	 */
	public DirectionLampButton getUpLampButton() {
		return upLampButton;
	}

	/**
	 * @return the downLampButton
	 */
	public DirectionLampButton getDownLampButton() {
		return downLampButton;
	}

	/**
	 * @return the ELEVATOR_COMPONENTS
	 */
	public FloorElevatorComponents[] getElevatorComponents() {
		return ELEVATOR_COMPONENTS;
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
