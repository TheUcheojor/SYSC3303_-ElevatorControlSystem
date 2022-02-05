/**
 * This class provides a date format resources
 *
 * @author ryanfife, paulokenne
 */
package common;

import java.text.SimpleDateFormat;

public class DateFormat {

	/**
	 * The default date format
	 */
	private static final String DEFAULT = "hh:mm:ss.mmm";

	/**
	 * A date formatter used to ensure that given inputs follow the required format.
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DEFAULT);
}
