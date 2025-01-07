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
package com.sap.engine.interfaces.security.userstore;

import com.sap.engine.interfaces.security.userstore.config.*;
import com.sap.engine.interfaces.security.userstore.context.*;

public interface UserStore {

  public UserStoreConfiguration getConfiguration() throws SecurityException;

  public UserContext getUserContext() throws SecurityException;

  public GroupContext getGroupContext() throws SecurityException;

  public boolean isActive() throws SecurityException;

  public UserStoreFactory getFactory() throws SecurityException;;

  /**
   *  Returns an instance of the configuration editor for the user store.
   *
   * @return  instance of ConfigurationEditor or null if it cannot be instanciated.
   */
  public ConfigurationEditor getConfigurationEditor() throws SecurityException;

  public void setActive(boolean active);

}

