/**
 *
 */
package FloorSubsystem;

import common.Direction;

/**
 * The class represents a button that turns on a lamp
 *
 * @author paulokenne
 *
 */
public class LampButton {

	/**
	 * The status of the floor lamp
	 */
	private boolean isButtonPressed = false;

	/**
	 * The status of the elevatorDirection lamp
	 */
	private boolean isDirectionLampActive = false;

	/**
	 * The button elevatorDirection
	 */
	private Direction direction;

	/**
	 * A LampButton constructor.
	 */
	public LampButton(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Press the button
	 */
	public void press() {
		isButtonPressed = true;
		isDirectionLampActive = true;
	}

	/**
	 * Reset the button
	 */
	public void reset() {
		isButtonPressed = false;
		isDirectionLampActive = false;
	}

	/**
	 * Return a flag indicating whether the button has been pressed
	 *
	 * @return a flag indicating whether the button has been pressed
	 */
	public boolean isButtonPressed() {
		return isButtonPressed;
	}

	/**
	 * @return the elevatorDirection
	 */
	public Direction getDirection() {
		return direction;
	}

}
