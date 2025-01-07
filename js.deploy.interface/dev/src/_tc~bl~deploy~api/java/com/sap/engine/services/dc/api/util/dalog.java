/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.FileLog;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Log;
import com.sap.tc.logging.LogController;
import com.sap.tc.logging.LoggingUtilities;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Class providing logging and traceing mechanism. There are two possible
 * ways for logging and traceing.
 * <ol>
 * <li>The first one is standart implementation in which case the logs goes to
 * file with name deploy_&lt;timestamp&gt;.log, where timestamp is a time of
 * creation of the object of type:yyyy-MM-dd_HH-mm-ss, where yyyy is the
 * year(e.g. 2004), MM is the month(e.g. 11), dd is day of month(e.g. 01),
 * HHmmss are the hours minutes and seconds(e.g. 132033 which is 13h 20min
 * 33sec) the traces goes to file with name deploy&lt;timestamp&gt;.trc.</li>
 * <li>The second preffered way is to pass <code>Logger</code> implementation
 * when creating new <code>DALog</code>. in this case all traces and log are
 * redirected completely to the Logger.
 * </ol>
 * In order to avoid trace files locking and to release all used resources the
 * clients should invoke {@link DALog#close() }. Invoking the method
 * {@link com.sap.engine.services.dc.api.Client#close()} closes automatic the
 * associated with the client logger.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-1</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class DALog {

	private static final String NOT_CONFIGURED = "Not configured";
	private static final String DC_LOCATION = "com.sap.engine.services.tc~bl~deploy~api";
	private static final String DC_API_CATEGORY = "DeployAPI";
	private static final String DC_API_LOG_SEVERITY_KEY = "com.sap.engine.services.dc.api.severity";

	private static String CUSTOM_LOG_FOLDER = null;

	private final Category category = Category.getCategory(Category.SYS_SERVER,
			DC_API_CATEGORY);
	private final Location location = Location.getLocation(DC_LOCATION);
	private FileLog categoryLogFile = null; 
	private FileLog locationLogFile = null; 
	private Logger logger;

	private DALog(final Logger logger, final boolean logToFile) {
		this.logger = logger;

		if (logToFile || !ClientFactory.isRunningOnServerSide()) {
			String logPath = getLogPath();
			
			String categoryDest = logPath + "deploy_api.log";
			String locationDest = logPath + "deploy_api.trc";
			
			LoggingUtilities.defaultConfiguration(category, categoryDest);
			LoggingUtilities.defaultConfiguration(location, locationDest);
			
			categoryLogFile = getFileLog(category, categoryDest);
			locationLogFile = getFileLog(location, locationDest);
			
			String severityFromProps = System.getProperty(DC_API_LOG_SEVERITY_KEY);
			if (severityFromProps != null && severityFromProps.length() > 0) {

				try {
					int severityAsInt = Integer.parseInt(severityFromProps);

					try {
						Severity.check(severityAsInt);
						category.setEffectiveSeverity(severityAsInt);
						location.setEffectiveSeverity(severityAsInt);
						traceDebug("Severity [{0}] was set to category: [{1}] and location [{2}].",
								new String[]{severityFromProps, category.getName(), location.getName()});
					} catch (IllegalArgumentException e) {
						logError("ASJ.dpl_api.000002",
								"Incorrect default severity [{0}]=[{1}]",
								new Object[] { DC_API_LOG_SEVERITY_KEY,
										new Integer(severityAsInt) });
					}
				} catch (NumberFormatException e) {
					logThrowable(e);
				}
			}

			trace("ASJ.dpl_api.000001",
					"The Log was reinitialized with [{0}] [{1}].",
					Severity.PATH,
					new Object[] { this.category, this.location });
		}
	}

	private FileLog getFileLog(LogController logController, String destination){
		Collection locationLogs = logController.getLogs();
		for(Iterator logItr = locationLogs.iterator(); logItr.hasNext();){
			Log locationLog = (Log)logItr.next(); 
			if(locationLog instanceof FileLog){				
				FileLog logFile = (FileLog)locationLog;
				if(logFile.getPattern().equals(destination)){
					return logFile;
				}
			}
		}
		traceDebug("Not found FileLog with destination: [{0}] in LogController [{1}]", 
				new String[]{destination, logController.getName()});
		return null;
	}
	
	//Ends with File.separator
	private String getLogPath() {
		String logPath;
		if (CUSTOM_LOG_FOLDER != null) {
			logPath = CUSTOM_LOG_FOLDER;
		} else {
			try {
				logPath = new File(".").getCanonicalPath() + File.separator
						+ "log" + File.separator + "dc_log";
			} catch (Exception e) {
				logPath = "defaultLog";
			}
		}
		return logPath + File.separator;
	}

	/**
	 * @return new <code>DALog</code> instance
	 */
	synchronized public static final DALog getInstance() {
		return getInstance(null);
	}

	/**
	 * This method is the preffered way to instantiate new <code>DALog</code>
	 * instance.
	 * 
	 * @param logger
	 * @return new <code>DALog</code> instance with redirected log and trace to
	 *         the <code>logger</code>
	 */
	synchronized public static final DALog getInstance(Logger logger) {
		return getInstance(logger, false);
	}

	/**
	 * This method combines the functionality of the {@link DALog#getInstance() }
	 * and {@link DALog#getInstance(Logger) } methods. In other words create log
	 * and trace files on the local file system and pass all traces and logs to
	 * the <code>logger<code>.
	 * 
	 * @param logger
	 * @param logToFile
	 *            this method provides a way to redirect all the traces to the
	 *            <code>logger</code> and yet create and log to files
	 * @return
	 */
	synchronized public static final DALog getInstance(Logger logger,
			boolean logToFile) {
		return new DALog(logger, logToFile);
	}

	/**
	 * @return used Category
	 */
	public Category getCategory() {
		return this.category;
	}

	/**
	 * @return used Location
	 */
	public Location getLocation() {
		return this.location;
	}

	private String buildMessage(String message, Object[] args) {
		if (args != null) {
			try {
				message = MessageFormat.format(message, args);
			} catch (IllegalArgumentException e) {
				logInfo("", e.toString() + " for message: " + message);
			}
		}
		return message;
	}

	//
	// T R A C E S T U F F
	//
	/**
	 * Traces messages with Severity.DEBUG.
	 * 
	 * @param message
	 *            the message to be traced.
	 */
	public void traceDebug(String message) {
		trace("", message, Severity.DEBUG, null);
	}

	/**
	 * Traces messages with Severity.DEBUG.
	 * 
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void traceDebug(String message, Object[] args) {
		trace("", message, Severity.DEBUG, args);
	}

	/**
	 * Traces messages with Severity.INFO.
	 * 
	 * @param message
	 *            the message to be traced.
	 * @deprecated
	 */
	public void traceInfo(String message) {
		trace("", message, Severity.INFO, null);
	}

	/**
	 * Traces messages with Severity.INFO.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 */
	public void traceInfo(String messageID, String message) {
		trace(messageID, message, Severity.INFO, null);
	}

	/**
	 * Traces messages with Severity.INFO.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void traceInfo(String messageID, String message, Object[] args) {
		trace(messageID, message, Severity.INFO, args);
	}

	/**
	 * Traces messages with Severity.PATH.
	 * 
	 * @param message
	 *            the message to be traced.
	 */
	public void tracePath(String message) {
		trace("", message, Severity.PATH, null);
	}

	/**
	 * Traces messages with Severity.PATH.
	 * 
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void tracePath(String message, Object[] args) {
		trace("", message, Severity.PATH, args);
	}

	/**
	 * Traces messages with Severity.WARNING.
	 * 
	 * @param message
	 *            the message to be traced.
	 * @deprecated
	 */
	public void traceWarning(String message) {
		trace("", message, Severity.WARNING, null);
	}

	/**
	 * Traces messages with Severity.WARNING.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 */
	public void traceWarning(String messageID, String message) {
		trace(messageID, message, Severity.WARNING, null);
	}

	/**
	 * Traces messages with Severity.WARNING.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void traceWarning(String messageID, String message, Object[] args) {
		trace(messageID, message, Severity.WARNING, args);
	}

	/**
	 * Traces messages with Severity.ERROR.
	 * 
	 * @param message
	 *            the message to be traced.
	 * @deprecated
	 */
	public void traceError(String message) {
		trace("", message, Severity.ERROR, null);
	}

	/**
	 * Traces messages with Severity.ERROR.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 */
	public void traceError(String messageID, String message) {
		trace(messageID, message, Severity.ERROR, null);
	}

	/**
	 * Traces messages with Severity.ERROR.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void traceError(String messageID, String message, Object[] args) {
		trace(messageID, message, Severity.ERROR, args);
	}

	private void trace(String messageID, String message, int severity,
			Object[] args) {
		if (this.logger != null) {
			this.logger.trace(severity, buildMessage(message, args));
		}

		SimpleLogger.trace(severity, location, messageID, message, args);
	}

	/**
	 * Logs the message of the given throwable and traces its stack trace.
	 * 
	 * @param th
	 *            Throwable
	 */
	public void traceThrowable(Throwable th) {
		traceThrowable(null, th.getLocalizedMessage(), th, Severity.ERROR, null);
	}

	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param message
	 *            the message to be logged
	 * @param th
	 *            Throwable
	 * @deprecated
	 */
	public void traceThrowable(String message, Throwable th) {
		traceThrowable("", message, th, Severity.ERROR, null);
	}

	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param messageID
	 *            the message ID to be logged
	 * @param message
	 *            the message to be logged
	 * @param th
	 *            Throwable
	 */
	public void traceThrowable(String messageID, String message, Throwable th) {
		traceThrowable(messageID, message, th, Severity.ERROR, null);
	}

	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param messageID
	 *            the message ID to be logged
	 * @param message
	 *            the message to be logged
	 * @param th
	 *            Throwable
	 * @param args
	 *            message arguments
	 */
	public void traceThrowable(String messageID, String message, Throwable th,
			Object[] args) {
		traceThrowable(messageID, message, th, Severity.ERROR, args);
	}

	private void traceThrowable(String messageID, String message, Throwable th,
			int severity, Object[] args) {
		if (this.logger != null) {
			this.logger.traceThrowable(buildMessage(message, args), th);
		}

		if (messageID == null) {
			SimpleLogger.traceThrowable(severity, location, th, "", message,
					args);
		} else {
			SimpleLogger.traceThrowable(severity, location, th, messageID,
					message, args);
		}
	}

	//
	// L O G S T U F F
	//

	/**
	 * logs messages with Severity.DEBUG.
	 * 
	 * @param message
	 *            the message to be traced.
	 */
	public void logDebug(String message) {
		log("", message, Severity.DEBUG, null);
	}

	/**
	 * logs messages with Severity.DEBUG.
	 * 
	 * @param traceInfo
	 *            additionaly information which goes to the trace file
	 * @param message
	 *            the message to be traced.
	 * @deprecated
	 */
	public void logDebug(String traceInfo, String message) {
		log("", traceInfo + message, Severity.DEBUG, null);
	}

	/**
	 * logs messages with Severity.DEBUG.
	 * 
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void logDebug(String message, Object[] args) {
		log("", message, Severity.DEBUG, args);
	}

	/**
	 * Logs messages with Severity.INFO.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 */
	public void logInfo(String messageID, String message) {
		log(messageID, message, Severity.INFO, null);
	}

	/**
	 * Logs messages with Severity.INFO.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void logInfo(String messageID, String message, Object[] args) {
		log(messageID, message, Severity.INFO, args);
	}

	/**
	 * Logs messages with Severity.PATH.
	 * 
	 * @param message
	 *            the message to be traced.
	 */
	public void logPath(String message) {
		log("", message, Severity.PATH, null);
	}

	/**
	 * Logs messages with Severity.PATH.
	 * 
	 * @param traceInfo
	 *            additionaly information which goes to the trace file
	 * @param message
	 *            the message to be traced.
	 * @deprecated
	 */
	public void logPath(String traceInfo, String message) {
		log("", traceInfo + message, Severity.PATH, null);
	}

	/**
	 * Logs messages with Severity.PATH.
	 * 
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void logPath(String message, Object[] args) {
		log("", message, Severity.PATH, args);
	}

	/**
	 * Logs messages with Severity.WARNING
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 */
	public void logWarning(String messageID, String message) {
		log(messageID, message, Severity.WARNING, null);
	}

	/**
	 * Logs messages with Severity.WARNING
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void logWarning(String messageID, String message, Object[] args) {
		log(messageID, message, Severity.WARNING, args);
	}

	/**
	 * Logs messages with Severity.ERROR.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 */
	public void logError(String messageID, String message) {
		log(messageID, message, Severity.ERROR, null);
	}

	/**
	 * Logs messages with Severity.ERROR.
	 * 
	 * @param messageID
	 *            The ID of the message
	 * @param message
	 *            the message to be traced.
	 * @param args
	 *            message arguments
	 */
	public void logError(String messageID, String message, Object[] args) {
		log(messageID, message, Severity.ERROR, args);
	}

	private void log(String messageID, String message, int severity,
			Object[] args) {
		if (this.logger != null) {
			this.logger.log(severity, buildMessage(message, args));
		}

		SimpleLogger
				.log(severity, category, location, messageID, message, args);
	}

	/**
	 * Logs the message of the given throwable and traces its stack trace.
	 * 
	 * @param th
	 *            Throwable
	 */
	public void logThrowable(Throwable th) {
		logThrowableInt(null, "", th, null);
	}

	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param message
	 *            the message to be logged
	 * @param th
	 *            Throwable
	 * @deprecated
	 */
	public void logThrowable(String message, Throwable th) {
		logThrowableInt("", message, th, null);
	}

	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param messageID
	 *            the message ID to be logged
	 * @param message
	 *            the message to be logged
	 * @param th
	 *            Throwable
	 */
	public void logThrowable(String messageID, String message, Throwable th) {
		logThrowableInt(messageID, message, th, null);
	}

	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param messageID
	 *            the message ID to be logged
	 * @param message
	 *            the message to be logged
	 * @param th
	 *            Throwable
	 * @param args
	 *            message arguments
	 */
	public void logThrowable(String messageID, String message, Throwable th,
			Object[] args) {
		logThrowableInt(messageID, message, th, args);
	}

	private void logThrowableInt(String messageID, String message, Throwable th,
			Object[] args) {
		if (this.logger != null) {
			this.logger.logThrowable(buildMessage(message, args), th);
		}
		
		log(messageID, message, Severity.ERROR, args);
		traceThrowable(messageID, message, th, Severity.ERROR, args);
	}

	/**
	 * Checks if the trace for this location is with severity DEBUG.
	 * 
	 * @return true if severity is DEBUG
	 */
	public boolean isDebugTraceable() {
		return location.beDebug();
	}

	/**
	 * Checks if the trace for this location is with severity PATH.
	 * 
	 * @return true if severity is PATH
	 */
	public boolean isPathTraceable() {
		return location.bePath();
	}

	/**
	 * Checks if the trace for this location is with severity INFO.
	 * 
	 * @return true if severity is INFO
	 */
	public boolean isInfoTraceable() {
		return location.beInfo();
	}

	/**
	 * Checks if the trace for this location is with severity WARNING.
	 * 
	 * @return true if severity is WARNING
	 */
	public boolean isWarningTraceable() {
		return location.beWarning();
	}

	/**
	 * Checks if the log for this location is with severity DEBUG.
	 * 
	 * @return true if severity is DEBUG
	 */
	public boolean isDebugLoggable() {
		return category.beDebug();
	}

	/**
	 * Checks if the log for this location is with severity PATH.
	 * 
	 * @return true if severity is PATH
	 */
	public boolean isPathLoggable() {
		return category.bePath();
	}

	// F I N A L I Z A T I O N S T U F F
	/**
	 * invoke this method on exit
	 * 
	 * @deprecated No longer is required to call this method
	 */
	public void flush() {
	}

	/**
	 * @deprecated No longer is required to call this method
	 */
	public synchronized void close() {
	}

	/**
	 * Retrieves trace file path.
	 * 
	 * @return path to location file
	 */
	public String getLocPath() {
		return (locationLogFile == null)?NOT_CONFIGURED:locationLogFile.getPath();
	}

	/**
	 * Retrieves log file path.
	 * 
	 * @return path to category file
	 */
	public String getCatPath() {
		return (categoryLogFile == null)?NOT_CONFIGURED:categoryLogFile.getPath();
	}

	/**
	 * Sets log folder for all traces and logs for the current Java VM
	 * 
	 * @param path
	 *            new log folder
	 * @return old log folder or null if not set.
	 * @throws IOException
	 *             If an I/O error occurs, which is possible because the
	 *             construction of the canonical pathname may require filesystem
	 *             queries
	 * @deprecated Use <i>DALog getInstance(Logger logger)</i> method to log in
	 *             separate file with your own implementation of <i>Logger</i>
	 *             interface
	 */
	public static String setCustomLogFolder(String path) throws IOException {
		File file = new File(path);
		String lodPath = DALog.CUSTOM_LOG_FOLDER;
		DALog.CUSTOM_LOG_FOLDER = file.getCanonicalPath();
		return lodPath;
	}

	/**
	 * Set the verbose flag associated with this logger. Useful for additional
	 * debug messages.
	 * 
	 * @param aVerbose
	 * @deprecated No longer is used
	 */

	public void setVerbose(boolean aVerbose) {
	}

	/**
	 * @return verbose mode or not
	 * @deprecated No longer is used
	 */

	public boolean isVerbose() {
		return false;
	}

	////////////////////////////////////////////////////////////////////////////
	// ////////////
	//
	//
	////////////////////////////////////////////////////////////////////////////
	// ////////////
	/**
	 * 
	 * <DL>
	 * <DT><B>Title: </B></DT>
	 * <DD>J2EE Deployment Team</DD>
	 * <DT><B>Description: </B></DT>
	 * <DD>The aim of the interface is to provide a way all of the traces and
	 * logs produced by the DC API to be handled by the clients.
	 * <UL>
	 * <LI>To handle all the logs use the method:<BR>
	 * <code>DALog.getInstance(customLoggerImpl);</code></LI>
	 * <LI>In order to handle all the logs and traces but still to use the
	 * standart trace and log files produced by the API use :<BR>
	 * <code>DALog.getInstance(customLogger, true); </code></LI>
	 * </UL>
	 * </DD>
	 * <DT><B>Copyright: </B></DT>
	 * <DD>Copyright (c) 2005, SAP-AG</DD>
	 * <DT><B>Date: </B></DT>
	 * <DD>Dec 9, 2005</DD>
	 * </DL>
	 * 
	 * @author Boris Savov( i030791 )
	 * @version 1.0
	 * @since 7.1
	 * 
	 */
	public interface Logger {
		/**
		 * traces <code>massage</code> with <code>severity</code>
		 * 
		 * @param severity
		 * @param message
		 */
		public void trace(int severity, String message);

		/**
		 * traces <code>Throwable</code> with short <code>message</code>
		 * 
		 * @param message
		 * @param th
		 */
		public void traceThrowable(String message, Throwable th);

		/**
		 * logs <code>massage</code> with <code>severity</code>
		 * 
		 * @param severity
		 * @param message
		 */
		public void log(int severity, String message);

		/**
		 * logs <code>Throwable</code> with short <code>message</code>
		 * 
		 * @param message
		 * @param th
		 */
		public void logThrowable(String message, Throwable th);

		/**
		 * flush logger
		 */
		public void flush();

		/**
		 * close logger
		 */
		public void close();
	}
}
