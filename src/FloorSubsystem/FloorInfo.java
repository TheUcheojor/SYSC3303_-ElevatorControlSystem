package FloorSubsystem;

import ElevatorSubsystem.ElevatorMotor;
import common.Direction;
import common.SystemValidationUtil;
import common.exceptions.InvalidSystemConfigurationInputException;
import common.messages.ElevatorFloorArrivalMessage;
import common.messages.MessageChannel;

/**
 * This class stores all the necessary properties of a floor thread
 *
 * @author Favour, Delight, paulokenne
 */
public class FloorInfo {

	/**
	 * The floor to floor distance in meters
	 */
	public static final double FLOOR_TO_FLOOR_DISTANCE = 4.5;

	/**
	 * Direction selected by user
	 */
	private Direction direction = null;

	/**
	 * The floor number
	 */
	private int floorNumber = 0;

	/**
	 * The status of the floor lamp
	 */
	private boolean isLampActive = false;

	/**
	 * The status of the floor elevator sensor
	 */
	private boolean elevatorSensor = false;

	/**
	 * The status of the floor's button
	 */
	private boolean isButtonPressed = false;

	/**
	 * A FloorInfo constructor
	 */
	public FloorInfo() {

		// Validate that the elevator values are valid
		try {
			SystemValidationUtil.validateFloorToFloorDistance(FLOOR_TO_FLOOR_DISTANCE);
		} catch (InvalidSystemConfigurationInputException e) {
			System.out.println("InvalidSystemConfigurationInputException: " + e);
			// Terminate if the elevation configuration are invalid.
			System.exit(1);
		}

	}

	/**
	 * Set the elevator send to false as the elevator is leaving the floor.
	 */
	public void elevatorLeavingFloorNotification() {
		this.elevatorSensor = false;
	}

	/**
	 * Notifies the elevator that it has arrived at the elevator.
	 *
	 * @param elevatorMotor                    the elevator motor
	 * @param elevatorSubsystemReceiverChannel the elevator subsystem receiver
	 *                                         channel
	 * @param isFloorFinalDestination          the flag indicating whether the floor
	 *                                         is the destination floor
	 */
	public void notifyElevatorAtFloorArrival(ElevatorMotor elevatorMotor,
			MessageChannel elevatorSubsystemReceiverChannel, boolean isFloorFinalDestination) {

		double topSpeed = elevatorMotor.getTopSpeed();
		double intialSpeed = elevatorMotor.getCurrentVelocity();
		double finalSpeed = 0;

		double acceleration = elevatorMotor.getAcceleration();

		int totalTimeInMilliSeconds;
		double newCurrentElevatorSpeed;

		// Find the time it needs to get to the maximum speed
		double timeToAccelerateToTopSpeed = (topSpeed - intialSpeed) / acceleration;

		// Find the distance traveled as the elevator accelerates to the top speed.
		double distanceTraveledWhenAccelerating = (Math.pow(topSpeed, 2) - Math.pow(intialSpeed, 2))
				/ (2 * acceleration);

		// Find the time spent when accelerating to top speed.
		double timeTravelledWhenAccelerating = (topSpeed - intialSpeed) / acceleration;

		// Check if the current floor is the destination floor
		if (isFloorFinalDestination) {
			// Find the distance at which we need to start to decelerate
			double distanceToStartDecelerating = FLOOR_TO_FLOOR_DISTANCE - (Math.pow(topSpeed, 2) / (2 * acceleration));

			// Find the distance and time spent at top speed .
			double distanceTravelledAtTopSpeed = distanceToStartDecelerating - distanceTraveledWhenAccelerating;
			double timeTravelledAtTopSpeed = distanceTravelledAtTopSpeed / topSpeed;

			// Find the time taken to decelerate (negative acceleration)
			double distanceToDecelerate = FLOOR_TO_FLOOR_DISTANCE - distanceToStartDecelerating;
			double timeSpentDecelerating = (finalSpeed - topSpeed) / (-acceleration);

			double totalTime = timeTravelledWhenAccelerating + timeTravelledAtTopSpeed + timeSpentDecelerating;

			totalTimeInMilliSeconds = (int) (totalTime * 1000);
			newCurrentElevatorSpeed = 0;

		} else {
			// Find the distance and time spent at top speed .
			double distanceTravelledAtTopSpeed = FLOOR_TO_FLOOR_DISTANCE - distanceTraveledWhenAccelerating;
			double timeTravelledAtTopSpeed = distanceTravelledAtTopSpeed / topSpeed;

			double totalTime = timeTravelledWhenAccelerating + timeTravelledAtTopSpeed;
			totalTimeInMilliSeconds = (int) (totalTime * 1000);

			newCurrentElevatorSpeed = finalSpeed;
		}

		// Notify the elevator when it has arrived
		Thread notifyElevatorThread = new Thread() {

			@Override
			public void run() {
				try {
					System.out.println("\nThe evelator sensor for floor " + floorNumber + " is waiting for "
							+ totalTimeInMilliSeconds + "ms.");
					Thread.sleep(totalTimeInMilliSeconds);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
				System.out.println("\nThe evelator has reached the floor.");
				elevatorSensor = true;
				elevatorSubsystemReceiverChannel.setMessage(new ElevatorFloorArrivalMessage(newCurrentElevatorSpeed));
			}
		};

		notifyElevatorThread.start();
	}

	/**
	 * This method gets the status of the floor's elevator sensor
	 *
	 * @return - status of the elevator sensor
	 */
	public boolean isFloorNotified() {
		return elevatorSensor;
	}

	/**
	 * This method returns the floor number
	 *
	 * @return floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * This method returns the direction selected by the user
	 *
	 * @return direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * This method gets the status of the floor lamp
	 *
	 * @return state of the floor lamp
	 */
	public boolean isLampActive() {
		return isLampActive;
	}

	/**
	 * This method returns the status of the floor's button
	 *
	 * @return the status of the button
	 */
	public boolean isButtonPressed() {
		return isButtonPressed;
	}

	/**
	 * This method sets the floor number
	 *
	 * @param floorNumber
	 */
	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;

	}

	/**
	 * This method simulates a button press to a floor system
	 *
	 * @param direction - the direction selected by the user
	 */
	public void pressFloorButton(Direction direction) {
		this.direction = direction;
		isLampActive = true;
		isButtonPressed = true;
	}

	/**
	 * Updating the floor state when signal received from the scheduler
	 *
	 * @param jobRequestComplete
	 */
	public void messageRecieved(boolean jobRequestComplete) {
		// checking for the right direction
		// direction of the button pressed
		if (jobRequestComplete) {
			isLampActive = false;
			isButtonPressed = false;
			elevatorSensor = true;
		}

	}

	/**
	 * This method prints out the status of the floor depending on its state
	 */
	public void printFloorStatus() {
		if (isLampActive && isButtonPressed) {
			System.out.println(
					"The user has pushed the floor button to go " + direction + " at floor: " + floorNumber + " ..");
		} else if (elevatorSensor) {
			System.out.println("The elevator has arrived at floor: " + floorNumber + " ..");
			elevatorSensor = false;
		} else {
			System.out.println("There has been no status change to the floor ..");
		}
	}

}
