/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.logging;

import java.util.Properties;

/**
 * Used for defining the log filter
 *
 * @author Nikolai Neichev
 */
public interface LogFilter {

  /**
   * Gets the filter name
   * @return the name
   */
  public String getName();

  /**
   * Checks if the log should be done
   * @param info a object, which contains all the data necessary to check if the log should be done
   * @return true if it will be loged
   */
  public boolean toLog(Object info);

  /**
   * Sets the filter arguments
   * @param args the arguments
   */
  public void setArgs(Properties args);

  /**
   * Returns a list of the filter arguments
   * @return the arguments
   */
  public Properties listArgs();

  /**
   * Gets the filter description
   * @return the descriptiion
   */
  public String getDescription();

  /**
   * Gets a help message for the filter
   * @return the help message
   */
  public String getHelpMessage();

}