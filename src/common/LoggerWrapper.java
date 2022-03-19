package common;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Logger wrapper for app.
 * 
 * Logger levels breakdown (subject to change)
 * 
 * severe (highest priority) = critical system errors
 * info = high level app functionality
 * fine (lowest priority) = non essential behaviour messages, used for debug purposes
 * 
 * @author Ryan Fife
 */

public class LoggerWrapper {
	private static String OUTPUT_FILENAME = "./logs/log.txt";
	private static Logger logger = null;
	
	/**
	 * Instantiate the global logger singleton
	 * 
	 * @return logger
	 */
	private static Logger instantiateLogger() {
		Logger logger = Logger.getLogger("global");
		
		try {
	        // setup the proper log format
	        SimpleFormatter format = new SimpleFormatter() {
	            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

	            @Override
	            public synchronized String format(LogRecord lr) {
	                return String.format(format,
	                        new Date(lr.getMillis()),
	                        lr.getLevel().getLocalizedName(),
	                        lr.getMessage()
	                );
	            }
	        };
	        
	        // console handler outputs to sys.err by default, extend so we can output to sys.out instead
	        class StdoutConsoleHandler extends ConsoleHandler {
	        	  protected void setOutputStream(OutputStream out) throws SecurityException {
	        	    super.setOutputStream(System.out);
	        	  }
        	}
	        
	        // setup both output handler
	        StdoutConsoleHandler consoleHandler = new StdoutConsoleHandler();
			FileHandler fileHandler = new FileHandler(OUTPUT_FILENAME);
	        consoleHandler.setFormatter(format);
	        fileHandler.setFormatter(format);
	         
	        // console will output only high level info about system state
	        consoleHandler.setLevel(Level.INFO);
	        // file output will contain all level info, for debugging purposes
	        fileHandler.setLevel(Level.ALL);
	        
	        // set logger level
	        logger.setLevel(Level.ALL);
	        // attach handlers to loggers
	        logger.addHandler(fileHandler);
	        logger.addHandler(consoleHandler);
			// use only defined handlers
			logger.setUseParentHandlers(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return logger;
	}
	
	/**
	 * Gets the logger singleton, instantiating on first access
	 * 
	 * @return logger
	 */
	public static Logger getLogger() {
		// only want to use one logger instantiation app-wide
		if(logger == null) {
			logger = instantiateLogger();
		}
		return logger;
	}
}
