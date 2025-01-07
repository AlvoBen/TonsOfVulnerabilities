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

import javax.management.JMException;
import javax.management.JMRuntimeException;

/**
 * Utilities for JMX exceptions
 *
 * @author Reinhold Kautzleben
 */
public class Util {

  private static Class iBaseException;

  static {
    try {
      iBaseException = Class.forName("com.sap.exception.IBaseException");
    } catch (Throwable e) {
      iBaseException = null;
    }
  }

  /**
   * Tests whether the given Throwable already serializes the stack trace.
   * @param t
   * @return boolean
   */
  public static boolean serializesStackTrace(Throwable t) {
    if (t instanceof JMException || t instanceof JMRuntimeException) {
      return true;
    }
    if (iBaseException != null && iBaseException.isAssignableFrom(t.getClass())) {
      return true;
    }
    return false;
  }
}
