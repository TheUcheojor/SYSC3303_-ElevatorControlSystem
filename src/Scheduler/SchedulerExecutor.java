/**
 *
 */
package Scheduler;

import common.SimulationFloorInputData;

/**
 * @author paulokenne
 *
 */
public class SchedulerExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Testing the data class

		SimulationFloorInputData data = new SimulationFloorInputData("14:05:15.0 2 UP 4");

		System.out.println(data.getArrivalTime() + " " + data.getCurrentFloor() + " " + data.getFloorDirectionButton()
				+ " " + data.getDestinationFloorCarButton());

	}

}
