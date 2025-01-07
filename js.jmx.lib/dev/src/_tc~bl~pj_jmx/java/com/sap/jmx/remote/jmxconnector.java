/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.jmx.remote;

import java.io.IOException;

import javax.management.MBeanServerConnection;

/**
 * A JmxConnector exists for each protocol type and is used by the JmxConnectionFactory to obtain a MBeanServerConnection.
 * @author d025700
 */
public interface JmxConnector {
	
  /**
   * Returns a MBean server connection for this connector.
   * Uses Thread.currentThread.getContextClassLoader() that was specified when
   * obtaining the JmxConnector for return value de-serialization.
   */
  public MBeanServerConnection getMBeanServerConnection() throws IOException;

  /**
   * Returns a MBean server connection for this connector.
   * Uses the given class loader for return value de-serialization.
   */
  public MBeanServerConnection getMBeanServerConnection(ClassLoader loader) throws IOException;

}
