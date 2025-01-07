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
import java.util.Hashtable;

/**
 * Creates a JmxConnector. Exists for each protocol type and is used by the JmxConnectionFactory.
 * @author d025700
 */
public interface JmxConnectorFactory {
  public JmxConnector getJmxConnector(String addressRemainder, Hashtable props) throws IOException;
}
