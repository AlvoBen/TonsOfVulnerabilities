/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 *
 * @deprecated use {@link com.sap.security.api.UMFactory} instead.
 */
package com.sap.engine.interfaces.security.userstore;

import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.config.PolicyDescriptor;
import com.sap.engine.interfaces.security.userstore.listener.UserStoreListener;

/**
 *  User store configurations are registered with a user store instance.
 * There is a default static user store instance which should be used. Creating
 * a different user store instance is also allowed.
 *
 * @deprecated use {@link com.sap.security.api.UMFactory} instead.
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public interface UserStoreFactory {
  /**
   *  Returns the active configuration.
   *
   * @return  the active configuration.
   */
  public UserStore getActiveUserStore();
   
  /**
   * Activates the userstore with the given alias.  
   */
  public void setActiveUserStore(String name);

  /**
   *  Sets the active userstore to be the given one, and applies the given policy.
   */
  public void setActiveUserStore(String name, PolicyDescriptor policy);
  /**
   *  Returns the configuration with the name.
   *
   * @param  name  the name of a registered configuration
   *
   * @return  the configuration with the given name or null if such does not exist.
   */
  public UserStore getUserStore(String name);

  /**
   *  Returns all registered configurations.
   *
   * @return  all configurations.
   */
  public UserStore[] listUserStores();

  public void registerUserStore(UserStoreConfiguration config, ClassLoader classLoader);

  public void updateUserStore(UserStoreConfiguration config, ClassLoader classLoader);

  public void unregisterUserStore(String name);
  
  public void renameUserStore(String name, String newName);

  public void registerListener(UserStoreListener listener);

  public void unregisterListener(UserStoreListener listener);

}

