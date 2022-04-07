/**
 *
 */
package FloorSubsystem;

import java.util.logging.Logger;

import ElevatorSubsystem.ElevatorMotor;
import common.Direction;
import common.LoggerWrapper;
import common.messages.elevator.ElevatorFloorArrivalMessage;
import common.messages.floor.ElevatorNotArrived;
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
	 * 
	 * @param isFloorFinalDestination          the flag indicating whether the floor
	 *                                         is the destination floor
	 * 
	 * @param produceFloorFault                An optional param for simulating
	 *                                         elevators stuck between floors
	 */
	public void notifyElevatorAtFloorArrival(int floorNumber, ElevatorMotor elevatorMotor, double elevatorFloorToFloorTimeSeconds,
			SubsystemCommunicationRPC elevatorUDP, SubsystemCommunicationRPC schedulerUDP,
			boolean produceFloorFault) {

		// Notify the elevator when it has arrived
		Thread notifyElevatorThread = new Thread() {

			@Override
			public void run() {
				long sleepTimeMilli = (long) elevatorFloorToFloorTimeSeconds;
				try {
					logger.fine("(FLOOR_SUBSYSTEM) Elevator " + elevatorId + " sensor for floor " + floorNumber
							+ " is waiting for " + sleepTimeMilli + "ms.");
					Thread.sleep(sleepTimeMilli);
				} catch (InterruptedException e) {
					System.out.println(e);
				}


				try {
					if (produceFloorFault) {
						logger.fine("(FLOOR_SUBSYSTEM) Elevator " + elevatorId + " never reached floor " + floorNumber);
						ElevatorNotArrived brokenMsg = new ElevatorNotArrived(floorNumber, elevatorId);

						schedulerUDP.sendMessage(brokenMsg);
					} else {
						logger.fine("(FLOOR_SUBSYSTEM) Elevator " + elevatorId + " has reached the floor " + floorNumber);
						elevatorArrivedAtFloor(elevatorMotor.getDirection(), floorNumber);
						ElevatorFloorArrivalMessage notifyMsg = new ElevatorFloorArrivalMessage(elevatorId, floorNumber);

						elevatorUDP.sendMessage(notifyMsg);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		notifyElevatorThread.start();
	}
}
