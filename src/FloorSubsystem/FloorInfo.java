package FloorSubsystem;

import common.Direction;

/**
 * This class stores all the necessary properties of a floor thread
 * @author Favour
 * @author Delight
 */
public class FloorInfo {
	
	private Direction direction;
	private int floorNumber;
	private boolean isLampActive;
	private boolean ElevatorSensor;
	private boolean isButtonPressed;


	/**
	 * Default Constructor
	 */
	public FloorInfo() {
		isLampActive = false;
		ElevatorSensor = false;
		isButtonPressed = false;
	}
	

	/**
	 * This method gets the status of the floor's elevator sensor
	 * @return - status of the elevator sensor
	 */
	public boolean isFloorNotified() {
		return ElevatorSensor;
	}

	/**
	 * This method returns the floor number
	 * @return floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}
	
	/**
	 * This method returns the direction selected by the user
	 * @return direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * This method gets the status of the floor lamp
	 * @return state of the floor lamp
	 */
	public boolean isLampActive() {
		return isLampActive;
	}

	
	/**
	 * This method returns the status of the floor's button
	 * @return - 
	 */
	public boolean isButtonPressed() {
		return isButtonPressed;
	}
	
	/**
	 * This method sets the floor number
	 * @param floorNumber
	 */
	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
		
	}
	
	/**
	 * This method simulates a button press to a floor system
	 * @param direction - the direction selected by the user
	 */
	public void pressFloorButton(Direction direction) {
		this.direction = direction;
		isLampActive = true;
		isButtonPressed = true;
	}
	
	/**
	 * Updating the floor state when signal recieved from the scheduler
	 * @param jobRequestComplete
	 */
	public void messageRecieved(boolean jobRequestComplete) {
		// checking for the right direction
		// direction of the button pressed
		if(jobRequestComplete){
            isLampActive = false;
            isButtonPressed = false;
            ElevatorSensor = true;
        }
   
	}
	
	/**
	 * This method prints out the status of the floor depending on its state
	 */
	public void printFloorStatus() {
		if(isLampActive && isButtonPressed) {
			System.out.println("The user has pushed the floor button to go " + direction + "at floor: " + floorNumber +" ..");
		}else if(ElevatorSensor) {
			System.out.println("The elevator has arrived at floor: " + floorNumber + " ..");
            ElevatorSensor = false;
		}else {
			System.out.println("There has been no status change to the floor ..");
		}
	}




}
