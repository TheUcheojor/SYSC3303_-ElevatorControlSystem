package FloorSubsystem;
/**
 * 
 * @author Favour
 * @author Delight
 */
public class FloorInfo {
	
	private String direction;
	private int floorNumber;
	private boolean isLampActive;
	private boolean ElevatorSensor;
	private boolean isButtonPressed;


	public FloorInfo() {
		isLampActive = false;
		ElevatorSensor = false;
		isButtonPressed = false;
	}
	
//	public FloorInfo(String timeStamp, String direction, int floorNumber, int carButton) {
//		this.timeStamp = timeStamp;
//		this.direction = direction;
//		this.floorNumber = floorNumber;
//		this.carButton = carButton;
//	}

	public boolean isFloorNotified() {
		return ElevatorSensor;
	}

	public int getFloorNumber() {
		return floorNumber;
	}
	

	public String getDirection() {
		return direction;
	}

	public boolean isLampActive() {
		return isLampActive;
	}

	public boolean isButtonPressed() {
		return isButtonPressed;
	}

	public void pressFloorButton(String direction) {
		this.direction = direction;
		isLampActive = true;
		isButtonPressed = true;
	}
	
	public void elevatorArrival(String direction) {
		// checking for the right direction
		// direction of the button pressed
		if(this.direction.equals(direction)){
            isLampActive = false;
            System.out.println("Elevator has arrived");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
