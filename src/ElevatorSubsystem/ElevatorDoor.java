package ElevatorSubsystem;

public class ElevatorDoor {
	// door open/close speed in milliseconds
	private double doorSpeed;
	private boolean isOpen = false;
	
	public ElevatorDoor(double doorSpeed) {
		this.doorSpeed = doorSpeed;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public void openDoor() {
		if(!this.isOpen) {
			try {
				Thread.sleep((long) this.doorSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isOpen = true;
		}
	}
	
	public void closeDoor() {
		if(this.isOpen) {
			try {
				Thread.sleep((long) this.doorSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isOpen = false;
		}
	}
	
	public double getDoorSpeed() {
		return this.doorSpeed;
	}
	
}
