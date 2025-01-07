/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Sep 7, 2005
 */
package com.sap.engine.services.dc.api.cmd.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.tc.logging.Severity;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Sep 7, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class CmdLogger implements DALog.Logger {
	public static final String PATTERN_LOG_FILE_BEGIN = Command.EOL
			+ "LOG FILE[ BEGIN ]";
	public static final String PATTERN_LOG_FILE_END = "LOG FILE[ END ]"
			+ Command.EOL;
	public static final String PATTERN_TRACE_FILE_BEGIN = Command.EOL
			+ "TRACE FILE[ BEGIN ]";
	public static final String PATTERN_TRACE_FILE_END = "TRACE FILE[ END ]"
			+ Command.EOL;

	public static final String PATTERN_ERROR_BEGIN = Command.EOL
			+ "ERROR[ BEGIN ]";
	public static final String PATTERN_ERROR_END = "ERROR[ END ]" + Command.EOL;
	public static final String PATTERN_WARNING_BEGIN = Command.EOL
			+ "WARNING[ BEGIN ]";
	public static final String PATTERN_WARNING_END = "WARNING[ END ]"
			+ Command.EOL;
	public static final String PATTERN_EVENT_BEGIN = Command.EOL
			+ "EVENT[ BEGIN ]";
	public static final String PATTERN_EVENT_END = "EVENT[ END ]" + Command.EOL;

	private final static boolean shouldTrace = false;

	private final PrintStream log, trace;
	private int severity = Severity.INFO;

	public CmdLogger(PrintStream log, PrintStream trace) {
		this.log = log;
		this.trace = trace;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public int getSeverity() {
		return this.severity;
	}

	public void logError(String message) {
		log(Severity.ERROR, message);
	}

	public void logInfo(String message) {
		log(Severity.INFO, message);
	}

	public void logWarning(String message) {
		log(Severity.WARNING, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.util.DALog.Logger#traceThrowable(java.
	 * lang.String, java.lang.Throwable)
	 */
	public void traceThrowable(String message, Throwable th) {
		if (this.trace == null) {
			return;
		}
		if (shouldTrace) {
			if (message != null) {
				this.trace.println(" [ TRACE EXCEPTION ] " + " :: " + message);
			}
			if (th != null) {
				th.printStackTrace(this.trace);
			} else {
				this.trace.println("Throwable is null.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.util.DALog.Logger#trace(int,
	 * java.lang.String)
	 */
	public void trace(int aSeverity, String message) {
		if (aSeverity < this.severity) {
			return;
		}
		if (this.trace == null) {
			return;
		}
		if (shouldTrace) {
			this.trace.println(" [ TRACE ] " + getSeverityName(aSeverity)
					+ " :: " + message);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.util.DALog.Logger#log(int,
	 * java.lang.String)
	 */
	public void log(int aSeverity, String message) {
		if (aSeverity < this.severity) {
			return;
		}

		if (this.log == null) {
			return;
		}
		this.log.println("[ " + getSeverityName(aSeverity) + " ] " + message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.util.DALog.Logger#logThrowable(java.lang
	 * .String, java.lang.Throwable)
	 */
	public void logThrowable(String message, Throwable th) {
		if (this.log == null) {
			return;
		}
		if (message != null) {
			this.log.println(" [ LOG EXCEPTION ] " + " :: " + message);
		}
		if (th != null) {
			th.printStackTrace(this.log);
		} else {
			this.log.println("Throwable is null.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.util.DALog.Logger#flush()
	 */
	public void flush() {
		if (this.log != null) {
			this.log.flush();
		}
		if (this.trace != null) {
			this.trace.flush();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.util.DALog.Logger#close()
	 */
	public void close() {
		trace(Severity.ALL, "*** Close ***");
	}

	private final static String getSeverityName(int severityCode) {
		return Severity.toString(severityCode);
	}

	public void msgToTrace(String msg) {
		if (this.trace != null) {
			this.trace.println(msg);
		}
	}

	public static final String getStackTrace(Exception e) {
		if (e == null) {
			return "Exception is 'NULL'";
		}
		StringWriter strWr = new StringWriter();
		e.printStackTrace(new PrintWriter(strWr));
		return strWr.getBuffer().toString();
	}

}
