/**
 *
 */
package common.requests;

/**
 * This class represents a request entity
 *
 * @author paulokenne
 *
 */
public class Request {

	/**
	 * The request type.
	 */
	private RequestType requestType;

	public Request(RequestType requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return the requestType
	 */
	public RequestType getRequestType() {
		return requestType;
	}

}
