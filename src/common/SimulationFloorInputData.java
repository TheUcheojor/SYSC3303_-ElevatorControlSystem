/**
 *
 */
package common;

import java.security.InvalidParameterException;
import java.util.Date;

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
	 * The elevator direction that the passenger wishes to go to reach his or her
	 * destination
	 */
	private Direction floorDirectionButton;

	/**
	 * The floor that the passenger wishes to go to.
	 **/
	private Integer destinationFloorCarButton;

	/**
	 * Constructor.
	 *
	 * @param arrivalTime               the arrival time
	 * @param currentFloor              the current floor
	 * @param floorDirectionButton      the floor direction
	 * @param destinationFloorCarButton the target floor
	 */
	public SimulationFloorInputData(String arrivalTime, Integer currentFloor, Direction floorDirectionButton,
			Integer destinationFloorCarButton) {
		this.arrivalTime = arrivalTime;
		this.currentFloor = currentFloor;
		this.floorDirectionButton = floorDirectionButton;
		this.destinationFloorCarButton = destinationFloorCarButton;
	}

	/**
	 * Constructor.
	 *
	 * @param dataString the data line string with formating: "Time Floor
	 *                   FloorButton CarButton"
	 */
	public SimulationFloorInputData(String dataString) throws InvalidParameterException {

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

		} catch (Exception e) {
			System.out.println(e);
			throw new InvalidParameterException(dataString);
		}

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
	 * Gets the current floor
	 *
	 * @return the currentFloor
	 */
	public Integer getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Gets the floor direction
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

}
