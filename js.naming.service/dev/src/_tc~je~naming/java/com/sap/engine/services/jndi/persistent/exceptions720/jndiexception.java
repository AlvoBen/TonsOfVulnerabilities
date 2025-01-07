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

package com.sap.engine.services.jndi.persistent.exceptions720;


/**
 * Base class for exceptions in JNDI
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */


public class JNDIException extends javax.naming.NamingException {

  /**
   * Common exceptions constant
   */
  public static final byte COMMON = -1;
  /**
   * Name already bound constant
   */
  public static final byte NAME_ALREADY_BOUND = 0;
  /**
   * Exception's type
   */
  private byte exceptionType;

  /**
   * Constructor
   *
   * @param errorString The string to be passed to super's constructor
   */

  public JNDIException(String errorString) {
    super(errorString);
    exceptionType = COMMON;
  }

  public JNDIException(String errorString, Throwable tr) {
    super(errorString);
    setRootCause(tr);
    exceptionType = COMMON;
  }


  /**
   * Gets the type of the exception
   *
   * @return The type of the exception
   */
  public byte getExceptionType() {
    return exceptionType;
  }

  /**
   * Sets the type of the exception
   *
   * @param type Type to be set
   */
  public void setExceptionType(byte type) {
    if (exceptionType == COMMON) {
      exceptionType = type;
    }
  }

}

