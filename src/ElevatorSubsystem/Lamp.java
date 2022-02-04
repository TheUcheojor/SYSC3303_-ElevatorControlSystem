package ElevatorSubsystem;

/**
 * Lamp entity
 * 
 * @author Ryan Fife
 *
 */
public class Lamp {
	private boolean isLit = false;
	
	Lamp() {}
	
	public void turnOn() {
		this.isLit = true;
		
	}
	
	public void turnOff() {
		this.isLit = false;
	}
	
}
