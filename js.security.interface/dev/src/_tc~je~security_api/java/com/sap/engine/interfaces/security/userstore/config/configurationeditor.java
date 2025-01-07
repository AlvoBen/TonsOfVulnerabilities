/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.config;

import java.util.Properties;

/**
 *  Editor for user store configurations. All implementations MUST implement java.awt.Component.
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public interface ConfigurationEditor {

  /**
   *  The GUI container invokes this method with the configuration to be edited.
   *
   * @param  configuration  the user store instance to be configured
   */
  public void setConfiguration(UserStoreConfiguration configuration);

  /**
   *  The GUI container uses this method to get the modified configuration.
   *
   * @return modified userstore properties.
   */
  public Properties getConfiguration();
}

