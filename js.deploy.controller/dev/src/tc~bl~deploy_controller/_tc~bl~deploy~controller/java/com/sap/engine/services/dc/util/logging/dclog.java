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
package com.sap.engine.services.dc.util.logging;

import static com.sap.engine.services.dc.util.PerformanceUtil.isBoostPerformanceDisabled;
import static com.sap.engine.services.dc.util.ThreadUtil.popTask;
import static com.sap.engine.services.dc.util.ThreadUtil.pushTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import com.sap.engine.services.dc.util.CallerInfo;
import com.sap.engine.services.dc.util.SystemProfileManager;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LoggingUtilities;
import com.sap.tc.logging.PropertiesConfigurator;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tc.logging.FileLog;
import com.sap.tc.logging.ListFormatter;
import com.sap.engine.services.dc.util.Constants;

/**
 * Provides the wrapped Logging functionality.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class DCLog {

	private static final String OFFLINE_LOG_CONFIG_FILE = "offline_log.properties";
	private static final String OFFLINE_LOG_CONFIG_RESOURCE = "com/sap/engine/services/dc/util/logging/resources/offline_log.properties";

	private static final String DC_DEPLOYMENT_CATEGORY = "Deployment";
	private static final String DC_LOCATION = "com.sap.engine.services.dc";

	private static Category category = Category.getCategory(
			Category.SYS_SERVER, DC_DEPLOYMENT_CATEGORY);
	private static Location location = Location.getLocation(DC_LOCATION);

	private static String prefix = "";

	private static String logPath = null;
	
	
	static {
		try {
			logPath = SystemProfileManager
					.getSysParamValue(SystemProfileManager.DIR_HOME);
		} catch (java.lang.NoClassDefFoundError err) {
			logPath = ".";
		}		
		
		final FileLog logTrc = new FileLog(logPath + File.separator + "deploy.trc",
				15000000, 30, new ListFormatter());
//		logTrc.setEffectiveSeverity(Severity.PATH);
		location.addLog(logTrc);
				
		final FileLog logLog = new FileLog(logPath + File.separator + "deploy.log",
				10000000, 30, new ListFormatter());
		logLog.setEffectiveSeverity(Severity.INFO);
		category.addLog(logLog);

	}


	private DCLog() {
	}


	/**
	 * Initializes the Category and Location used from the logging.
	 * 
	 */
	public static void initLogging(String _prefix) {

		prefix = _prefix;

		if (isDebugTraceable()) {
			traceDebug(location, "Log was reinitialized [Common(online):{0}].",
					new Object[] { logPath });
		}
	}

	public static void initOfflineLogging(String _prefix) {
		
		prefix = _prefix;

		final File offlineLogConfigFile = bootStrapLoggingProperties();

		if (!offlineLogConfigFile.exists()) {
			logWarning(location, 
					"ASJ.dpl_dc.005402",
					"Log was not reinitialized because the file [{0}] does not exist.",
					new Object[] { offlineLogConfigFile.getAbsolutePath() });

			return;
		}

		final PropertiesConfigurator propConfig = new PropertiesConfigurator(
				offlineLogConfigFile);
		propConfig.configure();

		logInfo(location, "ASJ.dpl_dc.005403",
				"Log was reinitialized [Common(offline):{0}].",
				new Object[] { logPath });
	}

	/**
	 * Get the log configuration file from the classloader and bootstrap it to
	 * the file system so it could be passed to the logging configurator
	 * 
	 * @return a file pointing to the bootstrapped logging config file or
	 *         nonexisting file
	 */
	private static File bootStrapLoggingProperties() {

		final File offlineLogConfigFile = new File(OFFLINE_LOG_CONFIG_FILE);
		BufferedInputStream bis = new BufferedInputStream(DCLog.class
				.getClassLoader().getResourceAsStream(
						OFFLINE_LOG_CONFIG_RESOURCE));
		BufferedOutputStream bos = null;

		try {

			if (bis == null) {
				logError(location, 
						"ASJ.dpl_dc.005404",
						"An error occured. The resource [{0}] used to configure the offline logging does not exist in the classpath.",
						new Object[] { OFFLINE_LOG_CONFIG_RESOURCE });
				if (offlineLogConfigFile.exists()) {
					logError(location,
							"ASJ.dpl_dc.005405",
							"An old configuration file exists in the file system. Falling back to it.");
				} else {
					logError(location,
							"ASJ.dpl_dc.005406",
							"There is no old configuration file in the file system either. Logging configuration will fail.");
				}
				return offlineLogConfigFile;
			}

			// if the file exist on the file system replace it with the new one
			if (offlineLogConfigFile.exists()) {
				boolean result = offlineLogConfigFile.delete();
				if (result == false) {
					logError(location,
							"ASJ.dpl_dc.005407",
							"There is an old configuration file in the file system that could not be deleted and replaced with the new one. The old one will be used.");
					return offlineLogConfigFile;
				}
			}
			bos = new BufferedOutputStream(new FileOutputStream(
					offlineLogConfigFile));
			final int buffSize = 2048;
			byte[] buffer = new byte[buffSize];
			int read;
			while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
				bos.write(buffer, 0, read);
			}
			bos.flush();

		} catch (java.io.FileNotFoundException e) {
			logError(location,
					"ASJ.dpl_dc.005408",
					"An error occured while creating logging properties file [{0}]. [{1}]",
					new Object[] { offlineLogConfigFile.getPath(), e });
		} catch (java.io.IOException e) {
			logError(location,
					"ASJ.dpl_dc.005409",
					"An error occured during read/write operation, while bootstrapping the logging properties file [{0}]. [{1}]");
		} finally {
			try {
				close(bis);
				close(bos);
			} catch (IOException ioe) {
				logError(location,
						"ASJ.dpl_dc.005410",
						"An error occured while closing the file streams during the creation of the logging properties file [{0}]. [{1}]",
						new Object[] { offlineLogConfigFile.getAbsolutePath(),
								ioe });
			}
		}
		return offlineLogConfigFile;
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

	
	/**
	 * Returns the used Location.
	 * 
	 * @return
	 */
	public static Location getLocation(Class<?> c) {
		return Location.getLocation(c.getPackage().getName());
	}


	
	private static String formatMessage(String message) {
		StringBuffer buffer = new StringBuffer("");
		if (prefix != null) {
			buffer.append(" [").append(prefix).append("]");
		}
		String sessionId = getSessionId();
		if (sessionId != null && sessionId.length() > 0) {
			buffer.append(" (").append(sessionId).append(")");
		}
		buffer.append(" :").append(message);
		return buffer.toString();
	}

	private static String getSessionId() {
		try {
			return Session.getSessionId();
		} catch (Exception e) {
			return "";
		}
	}

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// Severity's check methods
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// trace
	public static boolean isDebugTraceable() {
		return location.beDebug();
	}

	public static boolean isPathTraceable() {
		return location.bePath();
	}

	public static boolean isInfoTraceable() {
		return location.beInfo();
	}

	public static boolean isWarningTraceable() {
		return location.beWarning();
	}

	// log
	public static boolean isDebugLoggable() {
		return category.beDebug();
	}

	public static boolean isPathLoggable() {
		return category.bePath();
	}

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// TRACE TRACE TRACE
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// Traces messages with the specified severity.
	private static void trace(Location location, String messageID, String message, int severity,
			 Object[] args) {
		
		message = formatMessage(message);
		SimpleLogger.trace(severity, location, messageID, message, args);
	}
		
	private static void trace(Location location, String messageID, String message, int severity,
			boolean traceToDedicatedLogger, String dcName, Object[] args) {
		if (dcName == null) {
			trace(location, messageID, message, severity, args);
		} else {
			message = formatMessage(message);
			SimpleLogger.trace(severity, location, dcName, null, messageID,
					message, null, args);
		}
	}

	// DEBUG
	public static void traceDebug(Location location, String message) {
		traceDebug(location, message, null);
	}

	public static void traceDebug(Location location, String message, Object[] args) {
		trace(location, "", message, Severity.DEBUG, args);
	}

	// PATH
	public static void tracePath(Location location, String message) {
		tracePath(location, message, null);
	}
	
	public static void tracePath(Location location, String message, Object[] args) {
		trace(location, "", message, Severity.PATH, args);
	}
	
	// WARNING
	public static void traceWarning(Location location, String messageID, String message) {
		traceWarning(location, messageID, message, null);
	}

	public static void traceWarning(Location location, String messageID, String message,
			Object[] args) {
		trace(location, messageID, message, Severity.WARNING, args);
	}

	private static void traceWarningWithDC(Location location, String messageID, String message,
			String dcName, Object[] args) {
		trace(location, messageID, message, Severity.WARNING, true, dcName, args);
	}

	public static void traceWarningWithFaultyDcName(Location location, String messageID,
			String message, Object object, Object[] args) {
		final String dcName;
		if (object != null) {
			dcName = LoggingUtilities.getDcNameByClassLoader(object.getClass()
					.getClassLoader());
		} else {
			dcName = null;
		}
		traceWarningWithDC(location, messageID, message, dcName, args);
	}

	private static void trace(Location location, String messageID, String message, int severity, 
					String dcName, String csnComponent, Object[] args){
		if(dcName == null && csnComponent == null){
			trace( location, messageID, message, severity, args);
		}
		message = formatMessage(message);
		SimpleLogger.trace(severity, location, dcName, csnComponent, messageID, message, null, args);		
	}

	// INFO
	public static void traceInfo(Location location, String message) {
		traceInfo(location, "", message, null);
	}
	public static void traceInfo(Location location, String message, Object[] args) {
		traceInfo(location, "", message, args);
	}

	public static void traceInfo(Location location, String messageId, String message, Object[] args){
		trace(location, messageId, message, Severity.INFO, args);
	}

	private static void traceWarningWithDCAndCSNComponent(Location location, String messageID, String message, String dcName
			, String csnComponent, Object[] args){
		trace(location, messageID, message, Severity.WARNING, dcName, csnComponent, args);
	}

	public static void traceWarningWithCSNComponentAndFaultyDCName(Location location, String messageID, String message
			, Object object, Object[] args){
		final String dcName;
		final String csnComponent;
		if (object != null) {
			dcName = LoggingUtilities.getDcNameByClassLoader(object.getClass()
					.getClassLoader());
			csnComponent = LoggingUtilities.getCsnComponentByDCName(dcName);
		} else {
			dcName = null;
			csnComponent = null;
		}
		
		traceWarningWithDCAndCSNComponent(location, messageID, message, dcName, csnComponent, args);
	}
	
	
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// LOG LOG LOG
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// **************************************************************************
	// *****************************************************
	// COOMMON
	// **************************************************************************
	// *****************************************************

	// Logs messages with the specified severity.

	private static void log(Location location, String messageID, String message,
			Category lCategory, int severity,
			Object[] args) {
		message = formatMessage(message);
		SimpleLogger.log(severity, lCategory, location, messageID, message,
				args);
	}

	private static void log(Location location, String messageID, String message,
			Category lCategory, int severity,
			String dcName, Object... args) {
		if (dcName == null) {
			log(location, messageID, message, lCategory, severity, args);
		} else {
			message = formatMessage(message);
			SimpleLogger.log(severity, lCategory, location, dcName, null,
					messageID, message, args);
		}
	}

	public static String buildExceptionMessage(String messageID, String message) {
		return messageID + " " + message;
	}

	public static String buildExceptionMessage(String messageID,
			String message, Object[] args) {
		return messageID + " " + buildMessage(message, args);
	}

	private static String buildMessage(String message, Object[] args) {
		if (args != null) {
			try {
				message = MessageFormat.format(message, args);
			} catch (IllegalArgumentException e) {
				logInfo(location, "", e.toString() + " for message: " + message);
			}
		}
		return message;
	}

	// **************************************************************************
	// *****************************************************
	// LOG INFO
	// **************************************************************************
	// *****************************************************

	static void logInfo(Location location, Category lCategory, String messageID, String message,
			Object[] args) {
		log(location, messageID, message, lCategory, Severity.INFO, args);
	}

	public static void logInfo(Location location, String messageID, String message) {
		logInfo(location, category, messageID, message, null);
	}

	public static void logInfo(Location location, String messageID, String message,
			Object[] args) {
		logInfo(location, category, messageID, message, args);
	}

	// **************************************************************************
	// *****************************************************
	// LOG WARNING
	// **************************************************************************
	// *****************************************************
	private static void logWarningWithDC(Location location, String messageID, String message,
			 String dcName, Object[] args) {
		log(location, messageID, message, category, Severity.WARNING,
				 dcName, args);
	}

	public static void logWarning(Location location, String messageID, String message) {
		log(location, messageID, message, category, Severity.WARNING, null);
	}
	public static void logWarning(Location location, String messageID, String message,
			Object[] args) {
		log(location, messageID, message, category, Severity.WARNING, args);
	}

	public static void logWarningWithFaultyDcName(Location location, String dcName,
			String messageID, String message, Object[] args) {
		logWarningWithDC(location, messageID, message, dcName, args);
	}

	// **************************************************************************
	// *****************************************************
	// LOG ERROR
	// **************************************************************************
	// *****************************************************

	private static void logErrorWithDC(Location location, Category lCategory, String messageID,
			String message, String dcName,
			Object[] args) {
		log(location, messageID, message, lCategory, Severity.ERROR, dcName, args);
	}

	
	static void logError(Location location, Category lCategory, String messageID, String message,
			Object[] args) {
		log(location, messageID, message, lCategory, Severity.ERROR, args);
	}

	public static void logError(Location location, String messageID, String message) {
		logError(location, category, messageID, message,  null);
	}

	public static void logError(Location location, String messageID, String message,
			Object[] args) {
		logError(location, category, messageID, message, args);
	}

	public static void logErrorWithDC(Location location, String messageID, String message,
			String dcName, Object[] args) {
		logErrorWithDC(location, category, messageID, message, dcName, args);
	}

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// THROWABLE THROWABLE THROWABLE
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// Logs the message of the given throwable and traces its stack trace.
	private static void logThrowable(Location location, String messageID, String message,
			Throwable th, int severity,
			Object[] args) {
		String localizedMessage;
		String reason;

		if (th.getLocalizedMessage() == null) {
			localizedMessage = "";
		} else {
			localizedMessage = th.getLocalizedMessage();
		}

		if (message != null) {
			if ((messageID + " " + message).toString().equals(localizedMessage)) {
				reason = message;
			} else {
				reason = message + "\nReason : " + localizedMessage;
			}
		} else {
			reason = localizedMessage;
		}

		if (messageID == null) {
			SimpleLogger.traceThrowable(severity, location, th, "", message,
					args);
		} else {
			SimpleLogger.log(severity, category, location, messageID, message,
					args);
			reason = formatMessage(reason);
			SimpleLogger.traceThrowable(severity, location, th, messageID,
					reason, args);
		}
	}

	// **************************************************************************
	// *****************************************************
	// ERROR
	// **************************************************************************
	// *****************************************************

	public static void logErrorThrowable(Location location, Throwable th) {
		logErrorThrowable(location, null, th.getLocalizedMessage(), th);
	}

	public static void logErrorThrowable(Location location, String messageID, String message,
			Throwable th) {
		logThrowable(location, messageID, message, th, Severity.ERROR, null);
	}

	public static void logErrorThrowable(Location location, String messageID, String message,
			Object[] args, Throwable th) {
		logThrowable(location, messageID, message, th, Severity.ERROR, args);
	}

	// **************************************************************************
	// *****************************************************
	// Warning
	// **************************************************************************
	// *****************************************************
	
	public static void logWarningThrowable(Location location, Throwable th) {
		logWarningThrowable(location, null, th.getLocalizedMessage(), th);
	}

	public static void logWarningThrowable(Location location, String messageID, String message,
			Throwable th) {
		logThrowable(location, messageID, message, th, Severity.WARNING, null);
	}

	
	public static void logWarningThrowable(Location location, String messageID, String message,
			Object[] args, Throwable th) {
		logThrowable(location, messageID, message, th, Severity.WARNING, args);
	}


	// **************************************************************************
	// *****************************************************
	// Debug
	// **************************************************************************
	// *****************************************************
	public static void logDebugThrowable(Location location, String messageID, String message,
			Object[] args, Throwable th) {
		logThrowable(location, messageID, message, th, Severity.DEBUG, args);
	}

	public static void logDebugThrowable(Location location, Throwable th) {
		logDebugThrowable(location, null, th.getLocalizedMessage(), th);
	}

	public static void logDebugThrowable(Location location, String messageID, String message,
			Throwable th) {
		logThrowable(location, messageID, message, th, Severity.DEBUG, null);
	}

	public static void logDebugThrowableExt(Location location, String messageID, String message,
			Object[] args, Throwable th) {
		logThrowable(location, messageID, message, th, Severity.DEBUG, args);
	}

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// LOG LOG LOG
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	private static void close(OutputStream os) throws IOException {
		if (os != null) {
			os.close();
		}
	}

	private static void close(InputStream is) throws IOException {
		if (is != null) {
			is.close();
		}
	}

	public static class TimeWatcher {
		private static long counter = 0;
		private long id;
		private long time, start;

		public synchronized static TimeWatcher getInstance() {
			return new TimeWatcher();
		}

		/**
		 * When the instance is created the time beging to run.
		 * 
		 */
		private TimeWatcher() {
			this.id = counter++;
			if (counter == Long.MAX_VALUE) {
				counter = 0;
			}
			clearElapsed();
			this.start = this.time;
		}

		/**
		 * 
		 * @return the time between creation or last invoking the method
		 */
		public String getElapsedTimeAsString() {
			long tmp = System.currentTimeMillis();
			long elapsed = (tmp - this.time) /* / 1000L */;
			String ret = "[id:#" + this.id + ", elapsed: " + elapsed + " ms.]";
			this.time = tmp;
			return ret;
		}

		/**
		 * clears elapsed time
		 */
		public void clearElapsed() {
			this.time = System.currentTimeMillis();
		}

		/**
		 * @return elapsed time since the timer was created
		 */
		public String getTotalElapsedTimeAsString() {
			double totalElapsed = (System.currentTimeMillis() - this.start) / 1000D;
			return "[#" + this.id + ": " + totalElapsed + " sec]";
		}

		public long getId() {
			return this.id;
		}
	}

	public static class Session extends ThreadLocal {
		private static Session instance = new Session();

		public static void begin(String sessionId) {
			instance.set(sessionId);
			if (isBoostPerformanceDisabled()) {
				pushTask("[Deploy Controller] - performing (un)deployment operation with session id ["
						+ sessionId + "]");
			}
			String host = CallerInfo.getHost();
			if(host == null){
				host = Constants.UNKNOWN;
			}
			
			DCLog.traceDebug(
			location, 
							"The host [{0}] owns session id [{1}]. This session id is associated with transaction id [{2}]. Use this transaction id to filter trace and log messages.",
							new Object[] { host, sessionId,
									ThreadWrapper.getTransactionId() + "" });
		}

		public static void clear() {
			instance.set(null);
			if (isBoostPerformanceDisabled()) {
				popTask();
			}
		}

		public String toString() {
			return getSessionId();
		}

		public static String getSessionId() {
			return (String) instance.get();
		}
	}
}
