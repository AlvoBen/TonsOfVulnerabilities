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

import java.util.Properties;

public interface LogInterface {

  public final static byte DEBUG     = 0;
  public final static byte TRACE     = 1;
  public final static byte INFO      = 2;
  public final static byte NOTICE    = 3;
  public final static byte WARNING   = 4;
  public final static byte ERROR     = 5;
  public final static byte CRITICAL  = 6;
  public final static byte ALERT     = 7;
  public final static byte EMERGENCY = 8;
  public final static byte MIN_VALUE = DEBUG;
  public final static byte MAX_VALUE = EMERGENCY;


  /**
   * @deprecated  goggers are activated automatically.
   */
  public void activateLogger(String name);

  /**
   * @deprecated  use logging API directly
   */
  public Logger createLogger(String name, Properties properties) throws LoggerAlreadyExistingException;


  /**
   * @deprecated  use logging API directly
   */
  public Logger getLogger(String name);


  /**
   * @deprecated  use logging API directly
   */
  public boolean destroyLogger(String name);


  /**
   * @deprecated  use logging API directly
   */
  public String[] getLoggerNames();


  public Properties getDefaultLoggingProperties();


  /**
   * @deprecated  use logging API directly
   */
  public String getDefaultLoggingProperty(String key);


  /**
   * @deprecated  use logging API directly
   */
  public boolean setDefaultLoggingProperty(String key, String value);


  /**
   * @deprecated  use logging API directly
   */
  public Properties getConfigurationProperties();


  /**
   * @deprecated  use logging API directly
   */
  public String getConfigurationProperty(String key);


  /**
   * @deprecated  use logging API directly
   */
  public boolean setConfigurationProperty(String key, String value);

}

