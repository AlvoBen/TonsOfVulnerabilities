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
package com.sap.engine.services.deploy.logging;

import com.sap.engine.services.deploy.DeployResourceAccessor;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ContainerResourceAccessor;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.application.ApplicationTransaction;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizationException;
import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LoggingUtilities;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Provides the wrapped Logging functionality.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DSLog {

	private static final String DS_CATEGORY = "Services/Deploy";

	private static final String DS_LOCATION = "com.sap.engine.services.deploy";
	private static final String DS_TIMESTAT_LOCATION = "com.sap.engine.services.deploy.timestat";

	private static Category category = Category.getCategory(
			Category.SYS_SERVER, DS_CATEGORY);

	private static Location location = Location.getLocation(DS_LOCATION);
	private static Location timestatLocation = Location
			.getLocation(DS_TIMESTAT_LOCATION);

	private static ResourceAccessor resourceAccessor = ContainerResourceAccessor
			.getResourceAccessor();

	static {
		new DeployResourceAccessor().init(category, location);
		new ContainerResourceAccessor().init(category, location);

		
		tracePath(location, "The Log was reinitialized with [{0}] [{1}].",
				category, location);
	}

	private DSLog() {
		// Private constructor to prevent the instantiation.
	}

	/**
	 * Returns the used Category.
	 * 
	 * @return
	 */
	public static Category getCategory() {
		return category;
	}

	/**
	 * Returns the used Location.
	 * 
	 * @return
	 */
	public static Location getLocation() {
		return location;
	}

	public static void logThrowableAlwaysSucceeds(
		final ApplicationTransaction tx, final String operation, 
		final Throwable th) {
		if (th instanceof ThreadDeath) {
			throw (ThreadDeath) th;
		} else if (th instanceof OutOfMemoryError) {
			throw (OutOfMemoryError) th;
		} else if (th instanceof WarningException) {
			tx.addWarnings(((WarningException) th).getWarnings());
		} else if (th instanceof DeploymentException) {
			if (!tx.getDeployServiceContext().isMarkedForShutdown()) {
				SimpleLogger.trace(Severity.ERROR, location, 
					"ASJ.dpl_ds.000068",
					"The [{0}] operation always succeeds, " +
					"therefore the following exception " +
					"thrown from the [{1}] for [{2}] application " +
					"will be ignored.", th, 
				tx.getTransactionType(), operation, tx.getModuleID());
			}
		} else {
			if (!tx.getDeployServiceContext().isMarkedForShutdown()) {
				SimpleLogger.traceThrowable(Severity.ERROR, location,
					new ServerDeploymentException(
						ExceptionConstants.OPERATION_ALWAYS_SUCCEEDS,
						new String[] { tx.getTransactionType(), operation,
							tx.getModuleID() }, th).getMessage(), th);
			}
		}
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// TRACE TRACE TRACE
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	private static void trace(Location location, int severity, Object object, String messageID,
			String message, Object... args) {
		if (object == null) {
			SimpleLogger.trace(severity, location, messageID, message, args);
		} else {
			String dcName;
			if (object instanceof ContainerWrapper) {
				ContainerInterface ci = ((ContainerWrapper) object)
						.getRealContainerInterface();
				dcName = ci != null ? LoggingUtilities
						.getDcNameByClassLoader(ci.getClass().getClassLoader())
						: null;
			} else {
				dcName = LoggingUtilities.getDcNameByClassLoader(object
						.getClass().getClassLoader());
			}
			SimpleLogger.trace(severity, location, dcName, null, messageID,
					message, null, args);
		}
	}
	
	private static void trace(Location location, int severity, String dcName, String messageID,
			String message, Object... args) {
		SimpleLogger.trace(severity, location, dcName, null, messageID,
					message, null, args);
	}
	
	private static void traceThrowable(Location location, int severity, Object object, Throwable th, String messageID,
			String message, Object... args) {
		if (object == null) {
			SimpleLogger.traceThrowable(severity, location, th, messageID, message, args);
		} else {
			String dcName;
			if (object instanceof ContainerWrapper) {
				ContainerInterface ci = ((ContainerWrapper) object)
						.getRealContainerInterface();
				dcName = ci != null ? LoggingUtilities
						.getDcNameByClassLoader(ci.getClass().getClassLoader())
						: null;
			} else {
				dcName = LoggingUtilities.getDcNameByClassLoader(object
						.getClass().getClassLoader());
			}
			SimpleLogger.trace(severity, location, dcName, null, messageID,
					message, th, args);
		}
	}

	// DEBUG
	/**
	 * Traces messages with Severity.DEBUG. and location as parameter
	 * 
	 */
	
	public static void traceDebug(Location location, String message, Object... args) {
		trace(location, Severity.DEBUG, null, "", message, args);
	}

	/**
	 * Should be used in case of special types of objects (Map, Array, File...)
	 * when the com.sap.engine.deploy.server.utils.Convertor.toString(Object
	 * obj, String shift) method is needed to get the string representation of
	 * the object.
	 * 
	 * That object must be the last argument.
	 * @param args
	 * @param location the location, where the traces will be logged
	 */
	
	public static void traceDebugObject(Location location, String message, Object specialObject,
			Object... args) {
			trace(location, Severity.DEBUG, null, "", message + CAConstants.EOL
				+ CAConvertor.toString(specialObject, ""), args);
		}
	// new - added throwable as parameter
	public static void traceDebugThrowable(Location location, Object object, Throwable th, String messageID,
			String message, Object... args) {
		traceThrowable(location, Severity.DEBUG, object, th, messageID, message, args);
	}
	// PATH
	/**
	 * Traces messages with Severity.PATH.
	 * @param location the location, where the traces will be logged
	 */

	public static void tracePath(Location location, String message, Object... args) {
		trace(location, Severity.PATH, null, "", message, args);
	}	
	
	
	// INFO
	/**
	 * Traces messages with Severity.INFO.
	 * @param location the location, where the traces will be logged
	 */

	public static void traceInfo(Location location, String messageID, String message,
			Object... args) {
		trace(location, Severity.INFO, null, messageID, message, args);
	}

	
	// WARNING
	/**
	 * Traces messages with WARNING.
	 * @param location the location, where the traces will be logged
	 */

	public static void traceWarning(Location location, String messageID, String message,
			Object... args) {
		trace(location, Severity.WARNING, null, messageID, message, args);
	}

	
	public static void traceWarningWithFaultyComponentCSN(Location location, Object object,
			String messageID, String message, Object... args) {
		trace(location, Severity.WARNING, object, messageID, message, args);
	}
	
	public static void traceWarningWithFaultyDcName(Location location, String dcName, String messageID,
			String message, Object...args) {
		trace(location, Severity.WARNING, dcName, messageID, message, args);
	}
	
	//original
	// ERROR
	/**
	 * Traces messages with Severity.ERROR.
	 * @param location the location, where the traces will be logged
	 */
	
	public static void traceError(Location location, String messageID, String message,
			Object... args) {
		trace(location, Severity.ERROR, null, messageID, message, args);
	}
	
	// new - added throwable as parameter
	public static void traceError(Location location, String messageID, String message, Throwable th, Object... args){
		SimpleLogger.traceThrowable(Severity.ERROR, location, th, messageID, message, args);
	}
	
	public static void traceErrorWithFaultyComponentCSN(Location location, Object object,
			String messageID, String message, Object... args) {
		trace(location, Severity.ERROR, object, messageID, message, args);
	}
	
	// new - added dcName as parameter
	public static void traceErrorWithFaultyDcName(Location location, String dcName,
			String messageID, String message, Object... args) {
		trace(location, Severity.ERROR, dcName, messageID, message, args);
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// TRACE TRACE TRACE
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// LOG LOG LOG
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	private static void log(Category lCategory, Location location, int severity, String dcName,
			String messageID, String message, Object... args) {
		SimpleLogger.log(severity, lCategory, location, dcName, null,
				messageID, message, args);
	}

	// INFO
	/**
	 * Logs messages with severity Severity.INFO.,  
	 * Info message is informational text to record an event, 
	 * which does not have and also does not need any follow-up activity.
	 * 
	 *  @deprecated 
	 */
	public static void logInfo(String messageID, String message, Object... args) {
		logInfo(category, location, null, messageID, message, args);
	}

	/**
	 * Logs messages with severity Severity.INFO.,  
	 * Info message is informational text to record an event, 
	 * which does not have and also does not need any follow-up activity.
	 *   
	 * @param location the location, where the traces will be logged
	 */
	
	public static void logInfo(Location location, String messageID, String message, Object... args) {
		logInfo(category, location, null, messageID, message, args);
	}

	// Logs messages with severity Severity.INFO in case of foreign issue
	/**
	 * Logs messages with severity Severity.INFO.,  
	 * Info message is informational text to record an event, 
	 * which does not have and also does not need any follow-up activity.   
	 * In this case the event hasn't happened in Deploy Service, 
	 * but in the containers.
	 *   
	 * @param location the location, where the traces will be logged
	 */
	
	public static void logInfoWithDC(Location location, String messageID, String message,
			String dcName, Object... args) {
		logInfo(category, location, dcName, messageID, message, args);
	}
	
	static void logInfo(Category lCategory, Location location, String dcName, String messageID,
			String message, Object... args) {
		log(lCategory, location, Severity.INFO, dcName, messageID, message, args);
	}

	// WARNING
	/**
	 * Logs messages with severity Severity.WARNING  
	 * The Warning message tells that the application processing can and will proceed,
	 * but later follow-up activity is necessary to avoid error situations. 
	 * Information of how to solve the problem in the future should be available. 
	 *   
	 *  @deprecated 
	 */
	public static void logWarning(String messageID, String message,
			Object... args) {
		logWarning(category, location, null, messageID, message, args);
	}

	/**
	 * Logs messages with severity Severity.WARNING  
	 * The Warning message tells that the application processing can and will proceed,
	 * but later follow-up activity is necessary to avoid error situations. 
	 * Information of how to solve the problem in the future should be available. 
	 *   
	 * @param location the location, where the traces will be logged
	 */
	
	public static void logWarning(Location location, String messageID, String message,
			Object... args) {
		logWarning(category, location, null, messageID, message, args);
	}
	
	/**
	 * Logs messages with severity Severity.WARNING  
	 * The Warning message tells that the application processing can and will proceed,
	 * but later follow-up activity is necessary to avoid error situations. 
	 * Information of how to solve the problem in the future should be available. 	
	 * In this case the event hasn't happened in Deploy Service, 
	 * but in the containers. 
	 *   
	 * @param location the location, where the traces will be logged
	 */
	public static void logWarningWithFaultyDcName(Location location, String dcName,
			String messageID, String message, Object... args) {
		logWarning(category, location, dcName, messageID, message, args);
	}

	
	static void logWarning(Category lCategory, Location location, String dcName, String messageID,
			String message, Object... args) {
		log(lCategory, location, Severity.WARNING, dcName, messageID, message, args);
	}

	// ERROR

	/**
	 * Logs messages with severity Severity.ERROR  
	 * The Error message tells that the application processing terminates 
	 * without completing the desired tasks. 
	 * The application is still usable, 
	 * but corrective actions need to be performed 
	 * to avoid the erroneous termination
	 *   
	 * @param location the location, where the traces will be logged
	 */
	
	public static void logError(Location location, String messageID, String message,
			Object... args) {
		logError(category, location, null, messageID, message, args);
	}

	/**
	 * Logs messages with severity Severity.ERROR  
	 * The Error message tells that the application processing terminates 
	 * without completing the desired tasks. 
	 * The application is still usable, 
	 * but corrective actions need to be performed 
	 * to avoid the erroneous termination
	 * In this case the event hasn't happened in Deploy Service, 
	 * but in the containers. 
	 *   
	 * @param location the location, where the traces will be logged
	 */
	public static void logErrorWithFaultyDcName(Location location, String messageID,
			String message, String dcName, Object... args) {
		logError(category, location, dcName, messageID, message, args);
	}
	
	public static void logErrorThrowableWithFaultyDcName(Location location, String dcName, Throwable th,
			String messageID, String message, Object... args) {
		logThrowable(location, Severity.ERROR, dcName, th, messageID, message, args);
	}


	static void logError(Category lCategory, Location location, String dcName, String messageID,
			String message, Object... args) {
		log(lCategory, location, Severity.ERROR, dcName, messageID, message, args);
	}

	
	
	// Log Throwable
	/**
	 * Private method used by all logThrowable methods.
	 */

	private static void logThrowable(Location location, int severity, Throwable th,
			String messageID, String message, Object... args) {
		if (messageID == null) {
			SimpleLogger.traceThrowable(severity, location, message, th);
		} else {
			SimpleLogger.log(severity, category, location, messageID, message,
					args);
			// traced message is simplified because of a possible duplication with
			// the throwable message
			SimpleLogger.traceThrowable(severity, location, th, messageID,
					message, args);
		}
	}
	
	private static void logThrowable(Location location, int severity, String dcName, Throwable th,
			String messageID, String message, Object... args) {

			SimpleLogger.log(severity, category, location, dcName, null, messageID, message,
					args);
			SimpleLogger.trace(severity, location, dcName, null, messageID,
					message, th, args);
	}
	
	
	/**
	 * Logs the message of the given throwable and traces its stack trace.
	 * 
	 * @param th
	 *            Throwable
	 *            
	 * @deprecated           
	 */

	public static void logErrorThrowable(Throwable th) {
		logErrorThrowable(location, null, th.getLocalizedMessage(), th);
	}

	/**
	 * Logs the message of the given throwable and traces its stack trace.
	 * 
	 * @param th
	 *            Throwable
	 * @param location
	 *            Location
	 */

	public static void logErrorThrowable(Location location, Throwable th) {
		logErrorThrowable(location, null, th.getLocalizedMessage(), th);
	}

	
	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param message
	 *            array of stings and objects that contain the message
	 * @param th
	 *            Throwable
	 * 
	 * 
	 */

	public static void logErrorThrowable(Location location, String messageID, String message,
			Throwable th, Object... args) {
		logThrowable(location, Severity.ERROR, th, messageID, message, args);
	}

	
	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param th
	 *            Throwable
	 * 
	 * @param message
	 *            the message to be logged
	 * 
	 * 
	 */
	
	public static void logDebugThrowable(Location location, String messageID, String message,
			Throwable th, Object... args) {
		logThrowable(location, Severity.DEBUG, th, messageID, message, args);
	}

	
	/**
	 * Logs specified message and the message of the given throwable and traces
	 * its stack trace.
	 * 
	 * @param message
	 *            the message to be logged
	 * @param th
	 *            Throwable
	 */

	
	public static void logWarningThrowable(Location location, Throwable th) {
		logWarningThrowable(location, null, th.getLocalizedMessage(), th);
	}
	
	
	
	
	public static void logWarningThrowable(Location location, String messageID, String message,
			Throwable th, Object... args) {
		logThrowable(location, Severity.WARNING, th, messageID, message, args);
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// LOG LOG LOG
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	public static String getLocalizedMessage(String key, Object[] args) {
		LocalizableTextFormatter ltf = new LocalizableTextFormatter(
				resourceAccessor, key, args);
		String tempMessage = key;
		try {
			tempMessage = ltf.format();
			if (tempMessage == null ||
				tempMessage.startsWith("<--Localization failed")) { 
				// Wrong key, treat it as a message and return the key itself.
				tempMessage = key;
			}
		} catch (LocalizationException e) {
			logErrorThrowable(
				location, 
				"ASJ.dpl_ds.006501",
				"Error while getting the message for key: [{0}] and arguments [{1}]",
				e, key, args);
		}
		return tempMessage;
	}

	public static void traceTimeStat(String timeStat) {
		timestatLocation.logT(Severity.DEBUG, timeStat);
	}

	/**
	 * Checks if the trace for this location is with severity DEBUG.
	 * 
	 * @return true if severity is DEBUG
	 */
	public static boolean isDebugTraceable() {
		return location.beDebug();
	}

	/**
	 * Checks if the trace for this location is with severity PATH.
	 * 
	 * @return true if severity is PATH
	 */
	public static boolean isPathTraceable() {
		return location.bePath();
	}

	/**
	 * Checks if the trace for this location is with severity INFO.
	 * 
	 * @return true if severity is INFO
	 */
	public static boolean isInfoTraceable() {
		return location.beInfo();
	}

	/**
	 * Checks if the trace for this location is with severity WARNING.
	 * 
	 * @return true if severity is WARNING
	 */
	public static boolean isWarningTraceable() {
		return location.beWarning();
	}

	/**
	 * Checks if the log for this location is with severity PATH.
	 * 
	 * @return true if severity is PATH
	 */
	public static boolean isPathLoggable() {
		return category.bePath();
	}

}
