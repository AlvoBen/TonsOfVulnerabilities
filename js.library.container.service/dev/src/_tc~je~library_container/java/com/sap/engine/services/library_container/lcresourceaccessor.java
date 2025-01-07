/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.library_container;

import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Library Container class for tracing messages of different severity (info,
 * error, debug)
 * 
 * @author Rumiana Angelova
 * @version 7.1
 * 
 */
public class LCResourceAccessor extends ResourceAccessor {

	static final long serialVersionUID = 3720600693759680712L;

	public static final String LC_LOCATION = "com.sap.engine.services.library_container";
	private static final String BUNDLE_NAME = "com.sap.engine.services.library_container.LCResourceBundle";
	private static ResourceAccessor resourceAccessor = new LCResourceAccessor();
	private static Category category = Category.SYS_SERVER;
	private static Location location = Location.getLocation(LC_LOCATION);

	private LCResourceAccessor() {
		super(BUNDLE_NAME);
	}

	public static ResourceAccessor getResourceAccessor() {
		return resourceAccessor;
	}

	public static Category getCategory() {
		return category;
	}

	public static Location getLocation() {
		return location;
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// TRACE TRACE TRACE
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	public static void traceException(String messageID, String message,
			Throwable th, Object... args) {
		if (location.beError()) {
			if (messageID == null) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, th, "",
						message, args);
			} else {
				SimpleLogger.log(Severity.ERROR, category, location, messageID,
						message, args);
				SimpleLogger.traceThrowable(Severity.ERROR, location, th,
						messageID, (message != null ? message + "\nReason : "
								: "")
								+ (th.getLocalizedMessage() == null ? "" : th
										.getLocalizedMessage()), args);
			}
		}
	}

	/**
	 * @return if traces with severity DEBUG are allowed
	 */
	public static boolean isDebugTraceable() {
		return location.beDebug();
	}

	/**
	 * Traces messages with Severity.DEBUG.
	 * 
	 * @param message
	 *            the message to be logged
	 * @param args
	 *            arguments for the message
	 */
	public static void traceDebug(String message, Object... args) {
		trace("", message, Severity.DEBUG, args);
	}

	/**
	 * @return if traces with severity INFO are allowed
	 */
	public static boolean isInfoTraceable() {
		return location.beInfo();
	}

	/**
	 * Traces messages with Severity.INFO.
	 * 
	 * @param message
	 *            the message to be traced.
	 */
	public static void traceInfo(String messageID, String message,
			Object... args) {
		trace(messageID, message, Severity.INFO, args);
	}

	// Traces messages with the specified severity.
	private static void trace(String messageID, String message, int severity,
			Object... args) {
		SimpleLogger.trace(severity, location, messageID, message, args);
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// TRACE TRACE TRACE
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}
