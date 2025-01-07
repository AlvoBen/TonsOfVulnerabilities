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

import java.util.Map;

/**
 *  Configuration for a single LoginModule. It includes a display-name, description,
 * class-name of the login module, options, as well as hints for suitability for the
 * common authentication mechanisms, such as BASIC, FORM, CLIENT_CERT.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public interface LoginModuleConfiguration
  extends java.io.Serializable {

  /**
   *  Returns the description of the login module.
   *
   * @return  printable text.
   */
  public String getDescription();


  /**
   *  Returns the display name of the login module.
   *
   * @return  display name.
   */
  public String getName();


  /**
   *  Hints for common authentication mechanisms this login module is not suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getNotSuitableAuthenticationMechanisms();


  /**
   *  Returns the class name of the login module.
   *
   * @return  class name.
   */
  public String getLoginModuleClassName();


  /**
   *  Returns the options of the login module.
   *
   * @return  the options of the login module.
   */
  public Map getOptions();


  /**
   *  Hints for common authentication mechanisms this login module is suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getSuitableAuthenticationMechanisms();

  /**
   * Gets the special editor suitable for this login module options.
   *
   * @return  the editor for this login module options, or null if the default one should be used.
   */
  public String getOptionsEditor();

}

