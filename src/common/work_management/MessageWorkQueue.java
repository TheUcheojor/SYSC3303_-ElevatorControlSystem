/**
 *
 */
package common.work_management;

import java.util.ArrayDeque;

import common.messages.Message;

/**
 * This class is a work queue whereby items are addressed by a worker thread
 *
 * @author paulokenne
 *
 */
public abstract class MessageWorkQueue {

	/**
	 * The message work queue
	 */
	private ArrayDeque<Message> messageWorkQueue = new ArrayDeque<>();

	/**
	 * The MessageWorkQueue constructor.
	 */
	protected MessageWorkQueue() {
		new MessageWorkerThread().start();
	}

	/**
	 * Handle the given message appropriately
	 *
	 * @param message the given message
	 */
	protected abstract void handleMessage(Message message);

	/**
	 * Enqueue the given message
	 *
	 * @param message the given message
	 */
	protected void enqueueMessage(Message message) {

		synchronized (messageWorkQueue) {
			messageWorkQueue.add(message);
			messageWorkQueue.notify();
		}

	}

	/**
	 * This private class is a message worker thread that handles the messages in
	 * the queue.
	 *
	 * @author paulokenne
	 *
	 */
	private class MessageWorkerThread extends Thread {

		@Override
		public void run() {

			// The worker thread will check if we have work and address the work
			// appropriately if we do.
			while (true) {
				Message message = null;
				synchronized (messageWorkQueue) {

					while (messageWorkQueue.isEmpty()) {
						try {
							wait();
						} catch (Exception e) {
							return;
						}
					}

					message = messageWorkQueue.pop();
				}

				handleMessage(message);
			}
		}
	}
}
