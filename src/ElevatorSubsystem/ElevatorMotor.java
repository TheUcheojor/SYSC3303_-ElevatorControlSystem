package ElevatorSubsystem;

import common.Direction;

/**
 * Entity configuration for an elevator motor.
 * 
 * @author Ryan Fife
 *
 */

public class ElevatorMotor {
	
	private float topSpeed;
	private Direction direction = null;
	private float acceleration = 0;
	private boolean isRunning = false;
	
	ElevatorMotor(int topSpeed, int acceleration) {
		this.topSpeed = topSpeed;
		this.acceleration = acceleration;
	}
	
	ElevatorMotor(int topSpeed) {
		this.topSpeed = topSpeed;
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
		this.isRunning = false;
	}

}
