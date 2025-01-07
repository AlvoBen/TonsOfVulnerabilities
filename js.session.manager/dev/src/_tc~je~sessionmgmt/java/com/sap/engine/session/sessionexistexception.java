/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session;

/**
 * Author: georgi-s
 * Date: Jun 29, 2004
 */
public class SessionExistException extends SessionException {
  public SessionExistException() {
  }

  public SessionExistException(String message) {
    super(message);
  }

  public SessionExistException(String message, Throwable cause) {
    super(message, cause);
  }

  public SessionExistException(Throwable cause) {
    super(cause);
  }
}
