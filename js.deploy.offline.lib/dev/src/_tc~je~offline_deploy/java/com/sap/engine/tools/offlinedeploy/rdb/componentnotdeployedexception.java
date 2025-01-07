/*
 * Copyright (c) 2004 by SAP Labs Sofia AG.,
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
 * Defines an exception that is thrown during remove if component doesn't exist.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ComponentNotDeployedException extends DeploymentException {

  /**
   * Constructors a new ComponentNotDeployedException with detailed message.
   *
   * @param message text message
   */
  public ComponentNotDeployedException(String message) {
    super(message);
  }

}