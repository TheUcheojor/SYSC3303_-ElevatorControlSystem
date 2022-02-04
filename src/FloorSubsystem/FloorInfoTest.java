package FloorSubsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.Direction;

/**
 * Tests the FloorInfo class.
 *
 * @author Favour
 *
 */
class FloorInfoTest {


	/**
	 * Instance of the FloorInfo class
	 */
	FloorInfo floor;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		floor = new FloorInfo();
		
	}

	/**
	 * Test method for Floor Lamp.
	 */
	@Test
	void testisLampActive() {
		assertFalse(floor.isLampActive());
	}
	
	/**
	 * Test method for Floor number
	 */
	@Test
	void testFloorNumber() {
		// testing the default state
		assertTrue(floor.getFloorNumber() == -1);
		floor.setFloorNumber(2);
		assertTrue(floor.getFloorNumber() == 2);
	}
	/**
	 * Test method for button press.
	 */
	@Test
	void testButtonPress() {
		floor.pressFloorButton(Direction.UP);
		assertTrue(floor.getDirection() == Direction.UP);
		assertTrue(floor.isLampActive());
		assertTrue(floor.isButtonPressed());

		
	}
	/**
	 * Test method for recieving information from scheduler.
	 */
	@Test
	void testmessageRecieved() {
		floor.messageRecieved(true);
		assertTrue(floor.isFloorNotified());
		assertFalse(floor.isButtonPressed());
		assertFalse(floor.isLampActive());

		
	}
	


}
