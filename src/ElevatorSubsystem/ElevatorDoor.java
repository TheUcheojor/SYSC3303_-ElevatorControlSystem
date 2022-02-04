package ElevatorSubsystem;

public class ElevatorDoor {
	// door open/close speed in milliseconds
	private long doorSpeed;
	private boolean isOpen = false;
	
	ElevatorDoor(long doorSpeed) {
		this.doorSpeed = doorSpeed;
	}
	
	public void openDoor() {
		if(!this.isOpen) {
			try {
				Thread.sleep(this.doorSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.isOpen = true;
		}
	}
	
	public void closeDoor() {
		if(!this.isOpen) {
			try {
				Thread.sleep(this.doorSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.isOpen = false;
		}
	}
	
	
}
