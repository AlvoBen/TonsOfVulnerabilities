/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.jmx.monitoring.api;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.sap.pj.jmx.exception.IgnoreFirstLinePrintWriter;
import com.sap.pj.jmx.exception.Util;

/**
 * MBeanManagerException
 *
 * @author d025700
 */
public class MBeanManagerException extends Exception {

  private Throwable cause;
  private String causeStackTrace;
	private String actionStackTrace;

	/** Creates a MBeanManagerException. */
	public MBeanManagerException() {
		super();
		cause = null;
	}

	/** Creates a MBeanManagerException. */
	public MBeanManagerException(String message) {
		super(message);
		cause = null;
	}

  /** Creates a MBeanManagerException that wraps the causing <code>Throwable</code>. */
  public MBeanManagerException(Throwable cause) {
    super();
    this.cause = cause;
  }

  /**
   * Creates a MBeanManagerException that wraps the causing <code>Throwable</code> with a detailed message.
   * @param message
   */
  public MBeanManagerException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }

  /**
   * Returns the cause of the MBeanManagerException.
   * @return Throwable
   */
  public Throwable getCause() {
    return cause;
  }

  /** @see Throwable#printStackTrace() */
  public void printStackTrace() {
  //commented by i043319@19.02.2008 to suppress JLin warnings	
   // synchronized (System.err) {
   //   PrintWriter pw = new PrintWriter(System.err, true);
   //   printStackTrace(pw);
   // }
  }

  /** @see Throwable#printStackTrace(PrintStream) */
  public void printStackTrace(PrintStream ps) {
    synchronized (ps) {
      PrintWriter pw = new PrintWriter(ps, true);
      printStackTrace(pw);
    }
  }

  /** @see Throwable#printStackTrace(PrintWriter) */
  public void printStackTrace(PrintWriter pw) {
    synchronized (pw) {
			writeActionStackTrace(pw);
      writeCauseStackTrace(pw, true);
    }
  }

	/**
	 * Method writeActionStackTrace.
	 * @param pw
	 */
	private void writeActionStackTrace(PrintWriter pw) {
		if (actionStackTrace != null) {
			pw.print(actionStackTrace);
			pw.println("-------- predecessor system --------");
			super.printStackTrace(new IgnoreFirstLinePrintWriter(pw));
		}
		else {
			super.printStackTrace(pw);
		}
	}

  /**
   * Method writeCauseStackTrace.
   * @param pw
   */
  private void writeCauseStackTrace(PrintWriter pw, boolean writeSeparator) {
    if (cause == null) {
      return;
    }
    if (writeSeparator) {
      pw.println("-------- caused by --------");
    }
    if (causeStackTrace != null) {
      pw.print(causeStackTrace);
      pw.println("-------- predecessor system --------");
      cause.printStackTrace(new IgnoreFirstLinePrintWriter(pw));
    }
    else {
      cause.printStackTrace(pw);
    }
  }

  /**
   * Method writeObject.
   * @param stream
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    if (cause != null
      && !Util.serializesStackTrace(cause)
      && !(cause instanceof MBeanManagerException)) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      writeCauseStackTrace(pw, false);
      causeStackTrace = sw.toString();
    }
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		writeActionStackTrace(pw);
		actionStackTrace = sw.toString();

		stream.defaultWriteObject();
  }

}
