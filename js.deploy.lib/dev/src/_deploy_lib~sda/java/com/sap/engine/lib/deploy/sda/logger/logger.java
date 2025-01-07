/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.deploy.sda.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import com.sap.engine.lib.deploy.sda.constants.Constants;
import com.sap.engine.lib.deploy.sda.propertiesholder.PropertiesHolder;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.PropertiesConfigurator;
import com.sap.tc.logging.Severity;

/**
 * @author Mariela Todorova
 */
public class Logger implements Constants {

	public static final Category category = Category.getCategory(
			Category.SYS_SERVER, "Deployment");
	private static final Location location = Location.getLocation(Logger.class);

	/**
	 * Traces messages of the specified severity.
	 * 
	 * @param severity
	 *            the severity of the message.
	 * @param message
	 *            the message to be traced.
	 */
	public static void trace(Location location, int severity, String message) {
		if (location != null && location.beLogged(severity)) {
			location.logT(severity, message);
		}
	}

	/**
	 * Logs messages of the specified severity.
	 * 
	 * @param severity
	 *            the severity of the message.
	 * @param message
	 *            the message to be traced.
	 */
	public static void log(Location location, int severity, String message) {
		if (category != null && location != null && category.beLogged(severity)) {
			category.logT(severity, location, message);
		}
	}

	public static void logThrowable(Location location, int severity,
			String msg, Throwable th) {
		if (category != null && location != null && category.beLogged(severity)) {
			category.logThrowableT(severity, location, msg, th);
		}
	}

	public static void logThrowable(Location location, int severity,
			String msg, String[] args, Throwable th) {
		if (category != null && location != null && category.beLogged(severity)) {
			category.logThrowableT(severity, location, msg, args, th);
		}
	}

	/**
	 * Initializes the logging properties if they are not initialized bofore
	 * 
	 * @throws IOException
	 */
	public static void initLogging() throws IOException {

		if (null != location.getLogs() && 0 < location.getLogs().size()) {
			trace(location, Severity.DEBUG, "Logging already initialized");
			return;
		}

		trace(location, Severity.DEBUG, "Logging will be initialized ...");

		InputStream inStream = null;

		try {
			Properties logProps = new Properties();
			String j2eeDir = PropertiesHolder
					.getProperty(PropertiesHolder.J2EE_HOME);

			if (j2eeDir == null || !(new File(j2eeDir).isDirectory())) {
				j2eeDir = PropertiesHolder
						.getProperty(PropertiesHolder.J2EE_DIR);

				if (j2eeDir == null || !(new File(j2eeDir).isDirectory())) {
					j2eeDir = ".." + sep + "..";
				}
			}

			String logging = sep + DEPLOYMENT + sep + CFG + sep + LOGGING;
			if (new File(j2eeDir + logging).isFile()){
				inStream = new FileInputStream(j2eeDir + logging);
			} else {
				inStream = Logger.class.getClassLoader().getResourceAsStream(LOGGING);
			}
			logProps.load(inStream);
			
			String logDir = PropertiesHolder
					.getProperty(PropertiesHolder.CTS_WORK_DIR);

			if (logDir == null || !(new File(logDir).isDirectory())) {
				logDir = PropertiesHolder.getProperty(PropertiesHolder.LOG_DIR);

				if (logDir == null || !(new File(logDir).isDirectory())) {
					logDir = PropertiesHolder
							.getProperty(PropertiesHolder.WORK_DIR);
				}

				if (logDir == null || !(new File(logDir).isDirectory())) {
					logDir = "..";
				}
			}

			String key = null;
			String value = null;

			for (Enumeration propNamesEnum = logProps.propertyNames(); propNamesEnum
					.hasMoreElements();) {
				key = (String) propNamesEnum.nextElement();

				if (key.endsWith("pattern")) {
					value = logProps.getProperty(key);

					if (!new File(value).isAbsolute()) {
						logProps.setProperty(key, logDir + sep + value);
					}

					new File(logProps.getProperty(key)).getParentFile()
							.mkdirs();
				}
			}

			PropertiesConfigurator logConfig = new PropertiesConfigurator(
					logProps);
			logConfig.configure();
			trace(location, Severity.DEBUG, "Logging initialized");
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException ioe) {// $JL-EXC$
					// do nothing
				}
			}
		}
	}

	public static String parseNestedMessages(String messages) {
		if (messages == null) {
			return "";
		}

		// message1: message2: message3
		ArrayList result = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(messages, ":");
		String token = null;

		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken().trim();

			if (!"".equals(token)) {
				// in case of messages containing ':'
				// like
				// "Cannot open connection on host: 10.55.71.51 and port: 5004"
				// make a partial workaround
				try {
					Integer.parseInt(token.substring(0, 1));
				} catch (NumberFormatException nfe) {// $JL-EXC$
					result.add(token);
					continue;
				}

				String weird = (String) result.remove(result.size() - 1);
				weird += ": " + token;
				result.add(weird);
			}
		}

		return (String) result.get(result.size() - 1);
	}

}
