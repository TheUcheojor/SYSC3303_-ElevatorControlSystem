/**
 *
 */
package common;

import ElevatorSubsystem.ElevatorCar;
import FloorSubsystem.FloorSubsystem;
import common.exceptions.InvalidSystemConfigurationInputException;

/**
 * This class provides useful functionality relating to system validation
 *
 * @author paulokenne
 *
 */
public final class SystemValidationUtil {

	/*
	 * A private SystemValidationUtil constructor. We do not want to instances of
	 * this class
	 */
	private SystemValidationUtil() {
	}

	/**
	 * Validate the elevator max speed with constraints derived from kinematic
	 * equations
	 *
	 * @param evelatorMaxSpeed
	 * @throws InvalidSystemConfigurationInputException
	 */
	public static void validateElevatorMaxSpeed(double evelatorMaxSpeed)
			throws InvalidSystemConfigurationInputException {

		if (evelatorMaxSpeed < 0) {
			throw new InvalidSystemConfigurationInputException("The elevator max speed cannot be negative.");
		}

		// Verify that within floor-to-floor distance, we can slow down from the top
		// speed in time.
		if (evelatorMaxSpeed > Math
				.sqrt(FloorSubsystem.FLOOR_TO_FLOOR_DISTANCE * 2 * ElevatorCar.ELEVATOR_ACCELERATION)) {
			throw new InvalidSystemConfigurationInputException("The elevator max speed cannot be negative.");
		}

		// Verify that we can reach the top speed at some distance between adjacent
		// floors.
		double maxDistanceTraveledWhenAcceleratingToTopSpeed = (Math.pow(evelatorMaxSpeed, 2))
				/ (2 * ElevatorCar.ELEVATOR_ACCELERATION);
		if (maxDistanceTraveledWhenAcceleratingToTopSpeed > FloorSubsystem.FLOOR_TO_FLOOR_DISTANCE) {
			throw new InvalidSystemConfigurationInputException(
					"The elevator max speed cannot be reached between adjacent floors");
		}
	}

	/**
	 * Validate the elevator acceleration with constraints derived from kinematic
	 * equations
	 *
	 * @param evelatorMaxSpeed
	 * @throws InvalidSystemConfigurationInputException
	 */
	public static void validateElevatorAcceleration(double evelatorAcceleration)
			throws InvalidSystemConfigurationInputException {

		if (evelatorAcceleration < 0) {
			throw new InvalidSystemConfigurationInputException("The elevator acceleration cannot be negative.");
		}

		// Verify that we can slow down from the top speed in time.
		if (evelatorAcceleration < Math.pow(ElevatorCar.MAX_ELEVATOR_SPEED, 2)
				/ (2 * FloorSubsystem.FLOOR_TO_FLOOR_DISTANCE)) {
			throw new InvalidSystemConfigurationInputException("The elevator acceleration is invalid.");
		}

	}

	/**
	 * Validate the floor to floor distance with constraints derived from kinematic
	 * equations
	 *
	 * @param evelatorMaxSpeed
	 * @throws InvalidSystemConfigurationInputException
	 */
	public static void validateFloorToFloorDistance(double floorToFloorDistance)
			throws InvalidSystemConfigurationInputException {

		if (floorToFloorDistance < 0) {
			throw new InvalidSystemConfigurationInputException("The floor to floor distance cannot be negative.");
		}

		// Verify that floor to floor distance is large enough to ensure the elevator
		// can reach the top speed and decelerate to 0.
		if (floorToFloorDistance < Math.pow(ElevatorCar.MAX_ELEVATOR_SPEED, 2)
				/ (2 * ElevatorCar.ELEVATOR_ACCELERATION)) {
			throw new InvalidSystemConfigurationInputException("The floor to floor distance is invalid.");
		}

	}

	/**
	 * Return a flag indicating whether the given floor id is in the acceptable
	 * range of the system
	 *
	 * @param floorId
	 * @return
	 */
	public static boolean isFloorNumberInRange(int floorId) {
		return floorId >= 0 && floorId < FloorSubsystem.NUMBER_OF_FLOORS;
	}

	/**
	 * Return a flag indicating whether the given elevator id is in the acceptable
	 * range of the system
	 *
	 * @param elevatorId the elevator id
	 * @return
	 */
	public static boolean isElevatorNumberInRange(int elevatorId) {
		return elevatorId >= 0 && elevatorId < ElevatorCar.NUMBER_OF_ELEVATORS;
	}
}
