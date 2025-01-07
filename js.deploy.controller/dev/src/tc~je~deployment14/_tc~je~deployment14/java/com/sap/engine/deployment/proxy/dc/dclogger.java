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
package com.sap.engine.deployment.proxy.dc;

import com.sap.engine.deployment.Logger;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Mariela Todorova
 */
public class DCLogger extends Logger implements DALog.Logger {
	private static final Location location = Location
			.getLocation(DCLogger.class);

	/**
	 * traces <code>massage</code> with <code>severity</code>
	 * 
	 * @param severity
	 * @param message
	 */
	public void trace(int severity, String message) {
		trace(location, severity, message);
	}

	/**
	 * traces <code>Throwable</code> with short <code>message</code>
	 * 
	 * @param message
	 * @param th
	 */
	public void traceThrowable(String message, Throwable th) {
		logThrowable(location, Severity.ERROR, message, th);
	}

	/**
	 * logs <code>massage</code> with <code>severity</code>
	 * 
	 * @param severity
	 * @param message
	 */
	public void log(int severity, String message) {
		log(location, severity, message);
	}

	/**
	 * logs <code>Throwable</code> with short <code>message</code>
	 * 
	 * @param message
	 * @param th
	 */
	public void logThrowable(String message, Throwable th) {
		logThrowable(location, Severity.ERROR, message, th);
	}

	/**
	 * flush logger
	 */
	public void flush() {
	}

	/**
	 * close logger
	 */
	public void close() {
	}

}
