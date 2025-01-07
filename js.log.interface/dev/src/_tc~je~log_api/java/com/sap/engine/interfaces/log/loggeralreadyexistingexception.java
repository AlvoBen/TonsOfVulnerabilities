/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.log;

public class LoggerAlreadyExistingException extends Exception {//$JL-SER$

  private Logger logger;

  public LoggerAlreadyExistingException(Logger logger) {
    this.logger = logger;
  }

  public LoggerAlreadyExistingException() {
    this(null);
  }

  public Logger getExistingLogger() {
    return logger;
  }

}

