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
	private final static int DEFAULT_QUEUE_SIZE = 1;
	private ArrayDeque<Message> messages;
	private int maxSize;
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

	public synchronized boolean isEmpty() {
		return this.messages.size() == 0;
	}
}
