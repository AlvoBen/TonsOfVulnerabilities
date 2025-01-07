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

public interface Logger {

  public String getName();


  public boolean setLoggingProperty(String key, String value);


  public String getLoggingProperty(String key);


  public Properties getLoggingProperties();


  public void log(byte level, String message);


  public void log(byte level, byte[] message);


  public void log(byte level, byte[] message, int off, int len);


  public void log(byte level, String message, String user, String clientIp);


  public void log(byte level, byte[] message, String user, String clientIp);


  public void log(byte level, byte[] message, byte[] user, byte[] clientIp);


  public void log(byte level, byte[] message, int off, int len, byte[] user, byte[] clientIp);


  public void logThrowable(byte level, Throwable t);


  public void logThrowable(byte level, Throwable t, String user, String clientIp);


  public void log(LogRecord record);


  public boolean flush();

}

