/**
 *
 */
package common;

import java.security.InvalidParameterException;
import java.util.Date;

import ElevatorSubsystem.ElevatorAutoFixing;
import FloorSubsystem.FloorInputFault;

/**
 * This class stores the properties of a given simulation floor input.
 *
 * @author paulokenne
 *
 */
public class SimulationFloorInputData {

	/**
	 * The floor data input separator
	 */
	private static final String FLOOR_DATA_INPUT_SEPARATOR = " ";

	/**
	 * The time stamp indicating when the passenger arrives.
	 */
	private String arrivalTime;

	/**
	 * The current floor.
	 */
	private Integer currentFloor;

	/**
	 * The input data id.
	 */
	private Integer inputDataId;

	/**
	 * The elevator elevatorDirection that the passenger wishes to go to reach his
	 * or her destination
	 */
	private Direction floorDirectionButton;

	/**
	 * The floor that the passenger wishes to go to.
	 **/
	private Integer destinationFloorCarButton;

	/**
	 * The floor input fault
	 */
	private FloorInputFault fault = null;

	/**
	 * The floor at which the elevator gets stuck
	 */
	private Integer faultFloor = -1;

	/**
	 * The elevator auto fixing mode when addressing a fault
	 */
	private ElevatorAutoFixing elevatorAutoFixing = ElevatorAutoFixing.AUTO_FIXING_SUCCESS;

	/**
	 * Constructor.
	 *
	 * @param inputDataId               the floor input data
	 * @param arrivalTime               the arrival time
	 * @param currentFloor              the current floor
	 * @param floorDirectionButton      the floor elevatorDirection
	 * @param destinationFloorCarButton the target floor
	 */
	public SimulationFloorInputData(Integer inputDataId, String arrivalTime, Integer currentFloor,
			Direction floorDirectionButton, Integer destinationFloorCarButton, FloorInputFault fault,
			Integer faultFloor) {
		this.inputDataId = inputDataId;
		this.arrivalTime = arrivalTime;
		this.currentFloor = currentFloor;
		this.floorDirectionButton = floorDirectionButton;
		this.destinationFloorCarButton = destinationFloorCarButton;
		this.fault = fault;
		this.faultFloor = faultFloor;
	}

	/**
	 * Constructor.
	 *
	 * @param dataString the data line string with formating: "Time Floor
	 *                   FloorButton CarButton"
	 */
	public SimulationFloorInputData(Integer inputDataId, String dataString) throws InvalidParameterException {

		this.inputDataId = inputDataId;
		try {
			// Remove leading and trailing spaces
			dataString = dataString.strip();

			// Format of data String: Time Floor FloorButton CarButton
			String[] data = dataString.split(FLOOR_DATA_INPUT_SEPARATOR);

			Date parsedDate = DateFormat.DATE_FORMAT.parse(data[0]);
			this.arrivalTime = DateFormat.DATE_FORMAT.format(parsedDate);

			this.currentFloor = Integer.parseInt(data[1]);
			this.floorDirectionButton = Direction.valueOf(data[2]);
			this.destinationFloorCarButton = Integer.parseInt(data[3]);

			if (data.length > 4) {
				this.fault = FloorInputFault.valueOf(data[4]);

				switch (this.fault) {

				case STUCK_AT_FLOOR_FAULT:
					this.faultFloor = Integer.parseInt(data[5]);
					if (!SystemValidationUtil.isFloorNumberInRange(faultFloor)) {
						throw new InvalidParameterException();
					}
					break;

				case DOOR_STUCK_OPEN_FAULT:
					this.elevatorAutoFixing = ElevatorAutoFixing.valueOf(data[5]);
					break;
				}

			}

			// Validate the current floor and destination floor inputs are valid.
			if (!SystemValidationUtil.isFloorNumberInRange(currentFloor)
					|| !SystemValidationUtil.isFloorNumberInRange(destinationFloorCarButton)) {
				throw new InvalidParameterException();
			}

		} catch (Exception e) {
			System.out.println(e);
			throw new InvalidParameterException(dataString + " is an invalid floor input simulation data");
		}

	}

	/**
	 * @return the elevatorAutoFixing
	 */
	public ElevatorAutoFixing getElevatorAutoFixing() {
		return elevatorAutoFixing;
	}

	/**
	 * Gets the arrival time
	 *
	 * @return the arrivalTime
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * @return the fault
	 */
	public synchronized FloorInputFault getFault() {
		return fault;
	}

	/**
	 * @return the faultFloor
	 */
	public synchronized Integer getFaultFloor() {
		return faultFloor;
	}

	/**
	 * Gets the current floor
	 *
	 * @return the currentFloor
	 */
	public Integer getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Gets the floor elevatorDirection
	 *
	 * @return the floorDirectionButton
	 */
	public Direction getFloorDirectionButton() {
		return floorDirectionButton;
	}

	/**
	 * Gets the destination floor
	 *
	 * @return the destinationFloorCarButton
	 */
	public Integer getDestinationFloorCarButton() {
		return destinationFloorCarButton;
	}

	/**
	 * Gets the input data id
	 *
	 * @return the inputDataId
	 */
	public Integer getInputDataId() {
		return inputDataId;
	}

}
