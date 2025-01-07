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
 *  Configuration of a single user store. It holds a UserContext instance, configuration properties
 * and a set of login modules.
 *
 * @version 6.30
 * @author  Stephan Zlatarev
 * @author  Jako Blagoev
 */
public interface UserStoreConfiguration extends java.io.Serializable {

  public static final String NONE_ANONYMOUS_USER = "none";
  /**
   *  Returns the description of the user store.
   *
   * @return  printable text.
   */
  public String getDescription();


  /**
   *  Returns the display name of the user store.
   *
   * @return  display name.
   */
  public String getName();

   
  /**
   *  Returns the configured login modules for this user store.
   *
   * @return  an array of login module configurations.
   */
  public LoginModuleConfiguration[] getLoginModules();


  /**
   *  Returns the class name of the user context spi for the user store.
   *
   * @return  class name.
   */
  public String getUserSpiClassName();


  /**
   *  Returns the class name of the group context spi for the user store.
   *
   * @return  class name.
   */
  public String getGroupSpiClassName();


  /**
   *  Returns the value of this property of the user store.
   *
   * @param  key  the key of the property.
   *
   * @return  value of the property with this key.
   */
  public Properties getUserStoreProperties();


  /////////////////////////////////////////////////////////////////////////////////
  ///////////////////GUI///////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////
  /**
   *  Returns the class name of the configuration editor for the user store.
   *
   * @return  class name.
   */
  public String getConfigurationEditorClassName();

}

