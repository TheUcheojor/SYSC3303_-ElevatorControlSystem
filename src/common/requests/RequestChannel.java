/**
 *
 */
package common.requests;

/**
 * This class represents a channel whereby threads can transfer data.
 *
 * @author paulokenne, ryanfife
 *
 */
public class RequestChannel {

	/**
	 * The request.
	 */
	private Request request;

	/**
	 * A constructor.
	 */
	public RequestChannel() {
	}

	/**
	 * Sets the request
	 *
	 * @param request the request
	 */
	public synchronized void setRequest(Request request) {
		/**
		 * Wait until the request channel is empty
		 */
		while (this.request != null) {
			try {
				wait();
			} catch (InterruptedException exception) {
			}
		}

		this.request = request;
	}

	/**
	 * Sets the request
	 *
	 * @param request the request
	 */
	public synchronized Request getRequest() {

		/**
		 * Wait until the request channel is full
		 */
		while (this.request == null) {
			try {
				wait();
			} catch (InterruptedException exception) {
			}
		}

		Request tempRequest = this.request;
		this.request = null;

		return request;

	}

	/**
	 * @return true if there is no request and false otherwise.
	 */
	public boolean isEmpty() {
		return request == null;
	}
}
