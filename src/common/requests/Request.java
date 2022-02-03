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
	public RequestType requestType;

	public Request(RequestType requestType) {
		this.requestType = requestType;
	}
}
