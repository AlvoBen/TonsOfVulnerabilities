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
 * Date: 2005-5-8
 */
public class SessionConfigException extends Exception {

  public SessionConfigException() {
  }

  public SessionConfigException(String message) {
    super(message);
  }

  public SessionConfigException(String message, Throwable cause) {
    super(message, cause);
  }

  public SessionConfigException(Throwable cause) {
    super(cause);
  }
}
