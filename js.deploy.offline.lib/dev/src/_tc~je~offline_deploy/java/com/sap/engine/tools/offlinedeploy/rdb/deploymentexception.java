/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.tools.offlinedeploy.rdb;

/**
 * Thrown when the deployment of an SDA failed.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class DeploymentException extends Exception {

  /**
   * Constructs an exception with a text message.
   *
   * @param message text message
   */
  public DeploymentException(String message) {
    super(message);
  }

  /**
   * Constructs an exception with a text message
   * and the specified root cause, which has caused this exception.
   *
   * @param message text message
   * @param cause throwable object, which has caused this exception
   */
  public DeploymentException(String message, Throwable cause) {
    super(message, cause);
  }

}