package ElevatorSubsystem;

import common.Direction;

/**
 * Entity configuration for an elevator motor.
 * 
 * @author Ryan Fife
 *
 */

public class ElevatorMotor {
	
	private double topSpeed;
	private Direction direction = null;
	private double acceleration = 0;
	private boolean isRunning = false;
	
	public ElevatorMotor(double topSpeed, double acceleration) {
		this.topSpeed = topSpeed;
		this.acceleration = acceleration;
	}
	
	public ElevatorMotor(double topSpeed) {
		this.topSpeed = topSpeed;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public boolean getIsRunning() {
		return isRunning;
	}
	
	public void goUp() {
		this.direction = direction.UP;
		this.isRunning = true;
	}
	
	public void goDown() {
		this.direction = direction.DOWN;
		this.isRunning = true;
	}
	
	public void turnOff() {
		this.direction = null;
		this.isRunning = false;
	}
}
