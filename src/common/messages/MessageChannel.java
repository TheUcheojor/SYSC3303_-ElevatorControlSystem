/**
 *
 */
package common.messages;

import java.util.ArrayDeque;

/**
 * This class represents a channel whereby threads can transfer data.
 *
 * @author paulokenne, Ryan Fife
 *
 */
public class MessageChannel {
	/**
	 * default queue size
	 */
	private final static int DEFAULT_QUEUE_SIZE = 1;
	/**
	 * message queue
	 */
	private ArrayDeque<Message> messages;
	/**
	 * max size for queue
	 */
	private int maxSize;
	/**
	 * channel name
	 */
	private String channelName;

	public MessageChannel(String channelName, int maxSize) {
		this.channelName = channelName;
		this.maxSize = maxSize;
		this.messages = new ArrayDeque<Message>();
	}
	
	public MessageChannel(String channelName) {
		this.channelName = channelName;
		this.maxSize = DEFAULT_QUEUE_SIZE;
		this.messages = new ArrayDeque<Message>();
	}
	
	/**
	 * Appends message to channel queue
	 */
	public synchronized void appendMessage(Message message) {
		// Wait if channel full 
		while (messages.size() >= maxSize) {
			try {
				System.out.println(Thread.currentThread().getName() + " is waiting in " + channelName + " channel.\n");
				wait();
			} catch (InterruptedException exception) {
			}
		}

		this.messages.add(message);
		System.out.println(Thread.currentThread().getName() + " has sent a " + message.getMessageType()
				+ " message in the " + channelName + " channel.\n");

		notifyAll();
	}

	/**
	 * Pops first message from channel queue
	 * 
	 * @return popped message
	 */
	public synchronized Message popMessage() {
		// Wait if channel empty
		while (messages.size() == 0) {
			try {
				System.out.println(Thread.currentThread().getName() + " is waiting in " + channelName + " channel.\n");

				wait();
			} catch (InterruptedException exception) {
			}
		}

		Message tempMessage = this.messages.pop();

		System.out.println(
				Thread.currentThread().getName() + " has received message from the " + channelName + " channel.\n");

		notifyAll();
		return tempMessage;
	}

	/**
	 * @return true if message queue is empty, false otherwise
	 */
	public synchronized boolean isEmpty() {
		return this.messages.size() == 0;
	}
}
