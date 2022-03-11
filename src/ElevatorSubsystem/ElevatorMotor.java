package ElevatorSubsystem;

import java.io.Serializable;

import common.Direction;

/**
 * Entity configuration for an elevator motor.
 *
 * @author Ryan Fife, paulokenne
 *
 */

public class ElevatorMotor implements Serializable {

	/**
	 * The top speed of the elevator
	 */
	private double topSpeed;

	/**
	 * The elevatorDirection of the motor is moving the elevator
	 */
	private Direction direction = Direction.IDLE;

	/**
	 * The elevator acceleration in meter per second squared
	 */
	private double acceleration;

	/**
	 * A flag indicating whether the motor is running
	 */
	private boolean isRunning = false;

	/**
	 * The current velocity of the elevator
	 */
	private double currentVelocity = 0;

	public ElevatorMotor(double topSpeed, double acceleration) {
		this.topSpeed = topSpeed;
		this.acceleration = acceleration;
	}

	public ElevatorMotor(double topSpeed) {
		this.topSpeed = topSpeed;
	}

	/**
	 * Get the top speed
	 *
	 * @return the top speed
	 */
	public double getTopSpeed() {
		return topSpeed;
	}

	/**
	 * Get the acceleration
	 *
	 * @return the acceleration
	 */
	public double getAcceleration() {
		return acceleration;
	}

	/**
	 * Get current velocity
	 *
	 * @return the current velocity
	 */
	public double getCurrentVelocity() {
		return currentVelocity;
	}

	/**
	 * Set current velocity
	 *
	 * @return
	 */
	public void setCurrentVelocity(double currentVelocity) {
		this.currentVelocity = currentVelocity;
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean getIsRunning() {
		return isRunning;
	}

	public void goUp() {
		this.direction = Direction.UP;
		this.isRunning = true;
	}

	public void goDown() {
		this.direction = Direction.DOWN;
		this.isRunning = true;
	}

	public void turnOff() {
		this.direction = Direction.IDLE;
		this.isRunning = false;
	}
}
