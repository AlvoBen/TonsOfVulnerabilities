package com.sap.sdm.util.log;

/**
 * An abstract logging framework. An instance of <code>Logger</code> provides
 * methods for logging info, warning, error and fatal messages.
 * <code>Logger</code> distinguishes between a default and a GUI logger, so that
 * GUI related messages can be logged separately.
 * 
 * @author Christian Gabrisch 03.01.2003
 */
public abstract class Logger {
	private static Logger logger, guiLogger;

	/**
	 * Sets a <code>Logger</code> for logging all but GUI related messages.
	 * 
	 * @param logger
	 *            a <code>Logger</code> for all but GUI related messages
	 */
	public static void setLogger(Logger logger) {
		Logger.logger = logger;

		return;
	}

	/**
	 * Sets a <code>Logger</code> for logging GUI related messages.
	 * 
	 * @param logger
	 *            a <code>Logger</code> for logging GUI related messages
	 */
	public static void setGuiLogger(Logger guiLogger) {
		Logger.guiLogger = guiLogger;

		return;
	}

	/**
	 * Gets a <code>Logger</code> for logging all but GUI related messages.
	 * 
	 * @return a <code>Logger</code> for all but GUI related messages
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Gets a <code>Logger</code> for GUI related messages.
	 * 
	 * @return a <code>Logger</code> for GUI related messages
	 */
	public static Logger getGuiLogger() {
		return guiLogger;
	}

	/**
	 * Logs an info message.
	 * 
	 * @param message
	 *            the message to be logged
	 */
	public abstract void info(String message);

	/**
	 * Logs a warning message.
	 * 
	 * @param message
	 *            a <code>String</code> containing the message to be logged
	 */
	public abstract void warning(String message);

	/**
	 * Logs a warning message together with a <code>Throwable</code>.
	 * 
	 * @param message
	 *            a <code>String</code> containing the message to be logged
	 * @param throwable
	 *            a <code>Throwable</code> to be logged
	 */
	public abstract void warning(String message, Throwable throwable);

	/**
	 * Logs an error message.
	 * 
	 * @param message
	 *            a <code>String</code> containing the message to be logged
	 */
	public abstract void error(String message);

	/**
	 * Logs an error message together with a <code>Throwable</code>.
	 * 
	 * @param message
	 *            a <code>String</code> containing the message to be logged
	 * @param throwable
	 *            a <code>Throwable</code> to be logged
	 */
	public abstract void error(String message, Throwable throwable);

	/**
	 * Logs a fatal message.
	 * 
	 * @param message
	 *            a <code>String</code> containing the message to be logged
	 */
	public abstract void fatal(String message);

	/**
	 * Logs a fatal message together with a <code>Throwable</code>.
	 * 
	 * @param message
	 *            a <code>String</code> containing the message to be logged
	 * @param throwable
	 *            a <code>Throwable</code> to be logged
	 */
	public abstract void fatal(String message, Throwable throwable);
}
