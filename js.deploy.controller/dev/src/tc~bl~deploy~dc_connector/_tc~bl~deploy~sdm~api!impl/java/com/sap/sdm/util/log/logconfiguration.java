package com.sap.sdm.util.log;

/**
 * A configuration for <code>Logger</code>.
 * 
 * @author Christian Gabrisch 07.01.2003
 */
public abstract class LogConfiguration {
	private static LogConfiguration instance;

	/**
	 * Sets a <code>LogConfiguration</code>.
	 * 
	 * @param instance
	 *            a <code>LogConfiguration</code>
	 */
	public static void setInstance(LogConfiguration instance) {
		LogConfiguration.instance = instance;
	}

	/**
	 * Gets a <code>LogConfiguration</code>.
	 * 
	 * @return a <code>LogConfiguration</code>
	 */
	public static LogConfiguration getInstance() {
		return instance;
	}

	/**
	 * Configures the logging functionalities such that their corresponding
	 * files will be located in the specified directory.
	 * 
	 * @param logDirName
	 *            the name of the directory in which the log files will be
	 *            created
	 * @throws LogConfigurationException
	 *             if an error occurred while creating the log files
	 */
	public abstract void configureLogging(String logDirName)
			throws LogConfigurationException;

	/**
	 * Specifies a file in which the messages logged to the <code>Logger</code>
	 * returned by <code>Logger.getLogger()</code> will be duplicated.
	 * 
	 * @param logFileName
	 *            the name of the additional log file, which is fully qualified,
	 *            that is, including path name
	 * @return a <code>LogFileHandle</code> for later referencing the additional
	 *         log file
	 * @throws LogConfigurationException
	 *             if an error occurred while creating the additional log file
	 * @see #endLoggingTo(LogFileHandle)
	 */
	public abstract LogFileHandle startLoggingTo(String logFileName)
			throws LogConfigurationException;

	/**
	 * Causes the logging framework to not write logs to the specified log file
	 * any further. The log file is indirectly specified by the
	 * <code>LogFileHandle</code>.
	 * 
	 * @param handle
	 *            the <code>LogFileHandle</code> specifying the log file
	 * @see startLoggingTo(String)
	 */
	public abstract void endLoggingTo(LogFileHandle handle);

	/**
	 * Start writing log entries also to the console output.
	 */
	public abstract void startLoggingToConsole();

	/**
	 * Stop writing log entries to the console output.
	 */
	public abstract void endLoggingToConsole();

	/**
	 * Returns the name of the directory in which the log, trace and GUI log
	 * files are stored.
	 * 
	 * @return a <code>String</code> containing the name of the log directory
	 */
	public abstract String getLogDir();

	/**
	 * Returns the complete name (including the path) of the log file.
	 * 
	 * @return a <code>String</code> containing the path to the log file
	 */
	public abstract String getLogPathName();

	/**
	 * Returns the name (excluding the path) of the log file.
	 * 
	 * @return a <code>String</code> containing the name of the log file
	 */
	public abstract String getLogFileName();

	/**
	 * Returns the complete name (including the path) of the GUI log file.
	 * 
	 * @return a <code>String</code> containing the path to the GUI log file
	 */
	public abstract String getGuiLogPathName();

}
