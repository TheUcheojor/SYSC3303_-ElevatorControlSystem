package common.messages.scheduler;

/**
 * This class provides possible elevator commands.
 *
 * @author Favour
 *
 */
public enum ElevatorCommand {

	/**
	 * The stop elevator command.
	 */
	STOP,

	/**
	 * The close doors elevator command.
	 */
	CLOSE_DOORS,

	/**
	 * The open doors elevator command.
	 */
	OPEN_DOORS,

	/**
	 * The move up elevator command.
	 */
	MOVE_UP,

	/**
	 * The move down elevator command.
	 */
	MOVE_DOWN,
	
	/**
	 * The shut down elevator command.
	 */
	SHUT_DOWN,
	
	/**
	 * The restart elevator command.
	 */
	RESTART
}
