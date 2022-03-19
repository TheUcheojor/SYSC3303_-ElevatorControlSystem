/**
 *
 */
package FloorSubsystem;

import java.util.logging.Logger;

import ElevatorSubsystem.ElevatorMotor;
import common.Direction;
import common.LoggerWrapper;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.remote_procedure.SubsystemCommunicationRPC;

/**
 * This class represents the floor elevator components which include the arrival
 * sensor and elevatorDirection lamp
 *
 * @author paulokenne, Jacob
 *
 */
public class FloorElevatorComponents {

	private Logger logger = LoggerWrapper.getLogger();

	/**
	 * The elevator id
	 */
	private int elevatorId;

	/**
	 * The sensor state. True means an elevator is present and false means the
	 * opposite
	 */
	private boolean arrivalSensorState = false;

	/**
	 * The elevatorDirection lamp for the elevator shaft which denotes the arrival
	 * and elevatorDirection of an elevator at the floor.
	 */
	private DirectionLamp directionLamp = new DirectionLamp();

	/**
	 * A FloorElevatorSensor constructor
	 */
	public FloorElevatorComponents(int elevatorId) {
		this.elevatorId = elevatorId;
	}

	/**
	 * The elevator has arrived at a floor with a given elevator direction
	 *
	 * @param elevatorDirection the elevator's direction
	 * @param floorNumber       the floor number
	 */
	public void elevatorArrivedAtFloor(Direction elevatorDirection, int floorNumber) {
		setArrivalSensorState(true);
		directionLamp.setFloorNumber(floorNumber);
		directionLamp.setElevatorDirection(elevatorDirection);

	}

	/**
	 * Set the elevator arrival sensor state
	 *
	 * @param state the state
	 */
	public void setArrivalSensorState(boolean state) {
		arrivalSensorState = state;
	}

	/**
	 * The elevator is leaving the floor
	 */
	public void elevatorLeavingFloor() {
		setArrivalSensorState(false);
	}

	/**
	 * Return the sensor state
	 *
	 * @return true if an elevator is present and false otherwise
	 */
	public boolean getArrivalSensorState() {
		return arrivalSensorState;
	}

	/**
	 * @return the elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}

	/**
	 * Get the elevator direction lamp
	 *
	 * @return the direction Lamp
	 */
	public DirectionLamp getDirectionLamp() {
		return directionLamp;
	}

	/**
	 * Notifies the elevator that it has arrived at the elevator.
	 *
	 * @param elevatorId                       the elevator id
	 * @param elevatorMotor                    the elevator motor
	 * @param elevatorSubsystemReceiverChannel the elevator subsystem receiver
	 *                                         channel
	 * @param isFloorFinalDestination          the flag indicating whether the floor
	 *                                         is the destination floor
	 */
	public void notifyElevatorAtFloorArrival(int floorNumber, ElevatorMotor elevatorMotor,
			SubsystemCommunicationRPC elevatorUDP, boolean isFloorFinalDestination) {

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
			double distanceToStartDecelerating = FloorSubsystem.FLOOR_TO_FLOOR_DISTANCE
					- (Math.pow(topSpeed, 2) / (2 * acceleration));

			// Find the distance and time spent at top speed .
			double distanceTravelledAtTopSpeed = distanceToStartDecelerating - distanceTraveledWhenAccelerating;
			double timeTravelledAtTopSpeed = distanceTravelledAtTopSpeed / topSpeed;

			// Find the time taken to decelerate (negative acceleration)
			double distanceToDecelerate = FloorSubsystem.FLOOR_TO_FLOOR_DISTANCE - distanceToStartDecelerating;
			double timeSpentDecelerating = (finalSpeed - topSpeed) / (-acceleration);

			double totalTime = timeTravelledWhenAccelerating + timeTravelledAtTopSpeed + timeSpentDecelerating;

			totalTimeInMilliSeconds = (int) (totalTime * 1000);
			newCurrentElevatorSpeed = 0;

		} else {
			// Find the distance and time spent at top speed .
			double distanceTravelledAtTopSpeed = FloorSubsystem.FLOOR_TO_FLOOR_DISTANCE
					- distanceTraveledWhenAccelerating;
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
					logger.fine("(FLOOR_SUBSYSTEM) Elevator " + elevatorId + " sensor for floor "
							+ floorNumber + " is waiting for " + totalTimeInMilliSeconds + "ms.");
					Thread.sleep(totalTimeInMilliSeconds);
				} catch (InterruptedException e) {
					System.out.println(e);
				}

				logger.fine("(FLOOR_SUBSYSTEM) Elevator " + elevatorId + " has reached the floor "
						+ floorNumber);

				// For now, we will assume that the motor's elevatorDirection is where the
				// elevator plans to go
				// TODO Reevaluate the assumption.
				elevatorArrivedAtFloor(elevatorMotor.getDirection(), floorNumber);
				ElevatorFloorArrivalMessage notifyMsg = new ElevatorFloorArrivalMessage(elevatorId, floorNumber,
						newCurrentElevatorSpeed);
				try {
					elevatorUDP.sendMessage(notifyMsg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		notifyElevatorThread.start();
	}
}
