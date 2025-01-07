/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.usr;

/**
 * Author: Georgi-S
 * Date: 2005-4-13
 */
public class UserContextException extends Exception {

  public UserContextException() {
  }

  public UserContextException(String message) {
    super(message);
  }

  public UserContextException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserContextException(Throwable cause) {
    super(cause);
  }
}
