/**
 *
 */
package Scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import common.Direction;
import common.messages.ElevatorJobMessage;
import common.messages.Message;
import common.messages.MessageChannel;
import common.messages.elevator.ElevatorStatusMessage;
import common.messages.scheduler.ElevatorCommand;
import common.messages.scheduler.FloorCommand;
import common.messages.scheduler.SchedulerElevatorCommand;
import common.messages.scheduler.SchedulerFloorCommand;

/**
 * This represents the scheduler which manages the elevator and floor subsystem.
 *
 * @author paulokenne, jacobcharpentier, ryanfife, Favour
 *
 */
public class Scheduler implements Runnable {
	/**
	 * channel that receives messages from floor subsystem
	 */
	private MessageChannel incomingFloorChannel;

	/**
	 * floor channel that gets messages from the scheduler
	 */
	private MessageChannel outgoingFloorChannel;

	/**
	 * channel that receives messages from the elevator subsystem
	 */
	private MessageChannel incomingElevatorChannel;

	/**
	 * elevator channel that gets messages from the scheduler
	 */
	private MessageChannel outgoingElevatorChannel;

	/**
	 * elevator job queue
	 */
	private ArrayDeque<ElevatorJobMessage> elevatorJobQueue;

	/**
	 * elevator jobs that aren't assigned to a queue
	 */
	private ArrayList<ElevatorJobMessage> unassignedElevatorJobs = new ArrayList<ElevatorJobMessage>();

	/**
	 * elevator floor number
	 */
	private int elevatorFloorNumber;

	/**
	 * elevator floor number
	 */
	public Direction elevatorDirection;

	/**
	 * elevator id
	 */
	public int elevatorId;
	
	/**
	 * elevator error state
	 */
	public Exception elevatorErrorState;

	/**
	 * a constructor
	 */
	public Scheduler(MessageChannel receiveFloorChannel, MessageChannel floorSubsystemReceiverChannel,
			MessageChannel receiveElevatorChannel, MessageChannel elevatorSubsystemReceiverChannel) {

		this.incomingElevatorChannel = receiveElevatorChannel;
		this.incomingFloorChannel = receiveFloorChannel;

		this.outgoingFloorChannel = floorSubsystemReceiverChannel;
		this.outgoingElevatorChannel = elevatorSubsystemReceiverChannel;

		// TODO (rfife) for iter 3: scale this to multiple elevators
		this.elevatorJobQueue = new ArrayDeque<ElevatorJobMessage>();
		this.unassignedElevatorJobs = new ArrayList<ElevatorJobMessage>();
	}

	@Override
	public void run() {
		int i = 0;
		while (true) {
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!incomingFloorChannel.isEmpty()) {
				Message floorRequest = incomingFloorChannel.popMessage();
				handleFloorRequest(floorRequest);
			}

			// Move unassigned jobs to the elevator
			if (unassignedElevatorJobs.size() != 0) {
				assignUnassignedJobs();
			}

			// Only read the data in the channel if the elevator is not ready for a job and
			// the channel is not empty.
			if (!incomingElevatorChannel.isEmpty()) {
				Message elevatorRequest = incomingElevatorChannel.popMessage();
				handleElevatorMessage(elevatorRequest);
			}
			
			if(elevatorErrorState == null) {
				if(elevatorJobQueue.size() > 0) {
					if(elevatorDirection != Direction.IDLE) serveJob();
					else executeFirstJob();
				}
			} else {
				System.out.println("[ERROR] Elevator in error state: " + elevatorErrorState.getMessage());
			}
		}
	}

	private void assignUnassignedJobs() {
		System.out.println("Scheduler assigning unassigned jobs.\n");
		List<ElevatorJobMessage> toRemove = new ArrayList<ElevatorJobMessage>();

		unassignedElevatorJobs.forEach((ElevatorJobMessage job) -> {
			elevatorJobQueue.add(job);
			toRemove.add(job);
		});
		unassignedElevatorJobs.removeAll(toRemove);
	}

	/**
	 * Serves all elevator job requests for the current floor
	 */
	private void serveJob() {
		Iterator<ElevatorJobMessage> iterator = elevatorJobQueue.iterator();
		boolean jobServed = false;
		boolean shouldTurnOffLamp = false;
		List<ElevatorJobMessage> toRemove = new ArrayList<ElevatorJobMessage>();

		// iterate over job requests, remove jobs that are completed by arriving at this
		// floor
		while (iterator.hasNext()) {
			ElevatorJobMessage currRequest = iterator.next();
			boolean shouldRemove = false;

			if (currRequest.getDestinationFloor() == elevatorFloorNumber) {
				switch (currRequest.getMessageType()) {
				case ELEVATOR_FLOOR_REQUEST:
					if (currRequest.getDirection() == elevatorDirection || currRequest == elevatorJobQueue.peekFirst()) {
						shouldRemove = true;
						shouldTurnOffLamp = true;
					}
					break;
				case ELEVATOR_TRANSPORT_REQUEST:
					shouldRemove = true;
					break;
				default:
					break;

				}
			}
			if (shouldRemove) {
				toRemove.add(currRequest);
				jobServed = true;
			}
		}
		if (jobServed) {
			System.out.println("Scheduler serving jobs");
			elevatorJobQueue.removeAll(toRemove);
			stopElevator();
			openElevatorDoors();
		}
		if (shouldTurnOffLamp) {
			turnOffFloorDirectionButtonLamp(elevatorFloorNumber, elevatorDirection);
		}
	}

	/**
	 * Issues the necessary commands to the elevator for starting the first job in
	 * the queue.
	 */
	private void executeFirstJob() {
		System.out.println("Scheduler executing first job");
		ElevatorJobMessage firstJob = elevatorJobQueue.peekFirst();
		if (firstJob.getDestinationFloor() > elevatorFloorNumber) {
			moveElevatorUp();
		} else if (firstJob.getDestinationFloor() < elevatorFloorNumber) {
			moveElevatorDown();
		}
	}

	/**
	 * Handles floor messages accordingly.
	 *
	 * @param message
	 */
	private void handleFloorRequest(Message message) {

		switch (message.getMessageType()) {

		case ELEVATOR_FLOOR_REQUEST:
			ElevatorJobMessage job = (ElevatorJobMessage) message;
			unassignedElevatorJobs.add(job);
			System.out.println("Scheduler received floorRequest: go to floor " + job.getDestinationFloor() + "\n");
			break;

		default:
			break;
		}

	}

	/**
	 * Handles elevator messages accordingly.
	 *
	 * @param message
	 */
	private void handleElevatorMessage(Message message) {

		switch (message.getMessageType()) {

		case ELEVATOR_STATUS_MESSAGE:
			boolean shouldExecFirstJob = false;
			if(elevatorFloorNumber != ((ElevatorStatusMessage) message).getFloorNumber()) shouldExecFirstJob = true;
			elevatorFloorNumber = ((ElevatorStatusMessage) message).getFloorNumber();
			elevatorDirection = ((ElevatorStatusMessage) message).getDirection();
			elevatorId = ((ElevatorStatusMessage) message).getElevatorId();
			elevatorErrorState = ((ElevatorStatusMessage) message).getErrorState();
			System.out.println("Scheduler set internal elevator status: [EF: " + elevatorFloorNumber + ", ED: " + elevatorDirection + ", EID: " + elevatorId + ", ES:" + elevatorErrorState +" ]\n");
			if(shouldExecFirstJob) executeFirstJob();
			break;

		case ELEVATOR_TRANSPORT_REQUEST:
			elevatorJobQueue.add((ElevatorJobMessage) message);
			break;
		default:
			break;

		}

	}

	/**
	 * This method sends a command to the elevator to stop moving
	 */
	private void stopElevator() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(ElevatorCommand.STOP));
	}

	/**
	 * This method sends a command to the elevator to close elevator doors
	 */
	private void closeElevatorDoors() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(ElevatorCommand.CLOSE_DOORS));
	}

	/**
	 * This method sends a command to the elevator to open elevator doors
	 */
	private void openElevatorDoors() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(ElevatorCommand.OPEN_DOORS));
	}

	/**
	 * This method sends a command to the elevator to start moving up
	 */
	private void moveElevatorUp() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(ElevatorCommand.MOVE_UP));
	}

	/**
	 * This method sends a command to the elevator to start moving down
	 */
	private void moveElevatorDown() {
		outgoingElevatorChannel.appendMessage(new SchedulerElevatorCommand(ElevatorCommand.MOVE_DOWN));
	}

	private void turnOffFloorDirectionButtonLamp(int floorId, Direction direction) {
		outgoingFloorChannel
				.appendMessage(new SchedulerFloorCommand(FloorCommand.TURN_OFF_FLOOR_LAMP, floorId, direction));
	}
}
