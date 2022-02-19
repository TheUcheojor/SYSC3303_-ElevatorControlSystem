/**
 *
 */
package FloorSubsystem;

import common.Direction;

/**
 * The class represents a button that turns on a direction lamp
 *
 * @author paulokenne
 *
 */
public class DirectionLampButton {

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
	 * A DirectionLampButton constructor.
	 */
	public DirectionLampButton(Direction direction) {
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
	 * Turn the button
	 */
	public void turnOff() {
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
	 * Set the button pressed flag
	 *
	 * @param isButtonPressed the button pressed flag
	 */
	public void setButtonPressed(boolean isButtonPressed) {
		this.isButtonPressed = isButtonPressed;
	}

	/**
	 * @return the elevatorDirection
	 */
	public Direction getDirection() {
		return direction;
	}

}
