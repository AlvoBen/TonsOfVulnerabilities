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

package com.sap.engine.services.jndi.persistent.exceptions;

import com.sap.exception.BaseException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Severity;
import com.sap.engine.services.jndi.persistent.JNDIResourceAccessor;

/**
 * Base class for exceptions in JNDI
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class JNDIException extends BaseException {

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

  public static String CAN_NOT_ALLOW_OPERATION = "jndi_registry_0000";
  public static String GROUP_NOT_DEFINED_ON_THE_SERVER = "jndi_registry_0001";
  public static String USER_NOT_DEFINED_ON_THE_SERVER = "jndi_registry_0002";
  public static String PERMISSION_NOT_DEFINED_ON_THE_SERVER = "jndi_registry_0003";
  public static String CAN_NOT_DENY_OPERATION = "jndi_registry_0004";
  public static String CAN_NOT_GET_PRINCIPLES_FOR_PERMISSION = "jndi_registry_0005";
  public static String CAN_NOT_SERIALIZE_OBJECT = "jndi_registry_0006";
  public static String CAN_NOT_DESERIALIZE_OBJECT = "jndi_registry_0007";
  public static String OBJECT_ALREADY_BOUND = "jndi_registry_0008";
  public static String CAN_NOT_BIND_OBJECT = "jndi_registry_0009";
  public static String CAN_NOT_UNBIND_OBJECT = "jndi_registry_0010";
  public static String CAN_NOT_REBIND_OBJECT = "jndi_registry_0011";
  public static String CAN_NOT_RENAME_OBJECT = "jndi_registry_0012";
  public static String CAN_NOT_MOVE_OBJECT = "jndi_registry_0013";


  /**
   *  Constructor
   *
   * @param   errorString  The string to be passed to super's constructor
   */
  public JNDIException(String errorString) {
    super(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location,
          new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), errorString), null);
    exceptionType = COMMON;
  }

  public JNDIException(String errorString, Object[] args) {
    super(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location,
          new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), errorString, args), null);
    exceptionType = COMMON;
  }


  public JNDIException(String errorString, Object[] args, Throwable tr) {
    super(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location,
          new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), errorString, args), tr);
    exceptionType = COMMON;
  }


  public JNDIException(String errorString, Throwable tr) {
    super(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location,
          new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), errorString), tr);
    exceptionType = COMMON;
  }


  /**
   *  Constructor
   *
   * @param   errorString  The string to be passed to super's constructor
   * @param   type  Type of the exception
   */
  public JNDIException(String errorString, Object[] args, byte type) {
    super(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location,
          new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), errorString, args), null);
    exceptionType = type;
  }

  /**
   *  Gets the type of the exception
   *
   * @return   The type of the exception
   */
  public byte getExceptionType() {
    return exceptionType;
  }

  /**
   *  Sets the type of the exception
   *
   * @param   type  Type to be set
   */
  public void setExceptionType(byte type) {
    if (exceptionType == COMMON) {
      exceptionType = type;
    }
  }

}

