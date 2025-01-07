/*===========================================================================*/
/*                                                                           */
/*  (C) Copyright SAP AG, Walldorf  1998                                     */
/*                                                                           */
/*===========================================================================*/

package com.sap.sdm.is.cs.cmd.client.impl.ni;

/*===========================================================================*/

import java.io.PrintWriter;

/**
 * The class implements a tracing facility for the classes in the package
 * com.sap.bc.krn.cst.ni . The output stream can be modified by the user. By
 * default, no tracing is done.
 * 
 * @author Harald Mueller
 * @version 1.0
 */
public class Trace {
	/**
	 * Do not print any trace output
	 */
	public static final int NO_TRACE = 0;

	/**
	 * Print message before aborting the program
	 */
	public static final int ABORT = 1;

	/**
	 * Print message in case of an error, which is recovered
	 */
	public static final int ERROR = 2;

	/**
	 * Print message of interesting points in the flow of the program
	 */
	public static final int CONTROL_FLOW = 3;

	/**
	 * Print all messages
	 */
	public static final int ALL = 4;

	/**
	 * the current trace level
	 */
	private static int level = NO_TRACE;

	/**
	 * the output stream to use
	 * 
	 * @see java.lang.System#err
	 * @see java.io.PrintWriter
	 */
	private static PrintWriter outStream = null;

	/**
	 * use the specified PrintWriter to write the trace messages to. If the null
	 * reference is specified, there will be no traces.
	 * 
	 * @param out
	 *            the PrintWriter to use for writing the trace message
	 * @see java.io.PrintWriter
	 */
	public static synchronized void setPrintWriter(PrintWriter out) {
		if (outStream != null) {
			outStream.close();
		}

		outStream = out;
	}

	protected void finalize() throws Throwable {
		try {
			if (outStream != null) {
				outStream.close();
				outStream = null;
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * set the trace level, default is NO_TRACE
	 */
	public static synchronized void setLevel(int traceLevel) {
		level = traceLevel;
	}

	/**
	 * return the trace level currently active
	 */
	public static int getLevel() {
		return level;
	}

	/**
	 * write the text to the current output stream, if the current trace level
	 * is >= the specified trace level
	 * 
	 * @param traceLevel
	 *            if traceLevel is <= the current trace level: write the message
	 * @param text
	 *            the message to write
	 * 
	 * @see Trace#NO_TRACE
	 * @see Trace#setLevel
	 * @see Trace#setPrintWriter
	 */
	public static synchronized void print(int traceLevel, String text) {
		if (traceLevel <= level) {
			if (outStream == null) {
				// discard usage of System.err.println(text);
			} else {
				outStream.println(text);
			}

		}

	}

}
