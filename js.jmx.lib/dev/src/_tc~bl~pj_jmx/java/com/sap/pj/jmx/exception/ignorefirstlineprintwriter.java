/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.pj.jmx.exception;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A PrintWriter on to of another PrintStream or PrintWriter that ignores the first line when
 * printing an Object.
 *
 * @author Reinhold Kautzleben
 */
public class IgnoreFirstLinePrintWriter extends PrintWriter {

  private boolean firstLine;

  /**
   * Constructor for IgnoreFirstLinePrintWriter.
   * @param os
   */
  public IgnoreFirstLinePrintWriter(OutputStream os) {
    super(os, true);
    firstLine = true;
  }

  /**
   * Constructor for IgnoreFirstLinePrintStream.
   * @param w
   */
  public IgnoreFirstLinePrintWriter(Writer w) {
    super(w, true);
    firstLine = true;
  }

  /**
   * @see java.io.PrintStream#println(Object)
   */
  public void println(Object x) {
    if (firstLine) {
      firstLine = false;
    } else {
      super.println(x);
    }
  }

}
