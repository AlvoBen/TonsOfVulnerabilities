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
package com.sap.engine.interfaces.security.userstore.config;

import java.util.Properties;
import java.util.Map;
import java.util.Hashtable;

/**
 *  Class for configuring a single LoginModule at runtime (on the fly).
 * It includes a display-name, description, class-name of the login module,
 * options, as well as hints for suitability for the common authentication
 * mechanisms, such as BASIC, FORM, CLIENT_CERT.
 *
 * @author Svetlana Stancheva
 * @version 7.0
 */
public class RuntimeLoginModuleConfiguration implements LoginModuleConfiguration {//$JL-SER$

  private String name;
  private String description;
  private String loginModuleClassName;
  private Map options = new Hashtable();
  private String[] suitableAuth = new String[0];
  private String[] notSuitableAuth = new String[0];
  private String editor = null;

  /**
   *  Constructor for the login module configuration
   *
   * @param name                  display name of the login module
   * @param description           description of the login module
   * @param loginModuleClassName  class name of login module
   * @param options               default options of the login module
   * @param suitableAuth          authentication mechanisms this login
   *                              module fits best
   * @param notSuitableAuth       authentication mechanisms this login
   *                              module does not fit
   * @param editor                class name of GUI (options) editor
   */
  public RuntimeLoginModuleConfiguration(String name, String description, String loginModuleClassName, Map options, String[] suitableAuth, String[] notSuitableAuth, String editor) {
    this.name = name;
    this.description = description;
    this.loginModuleClassName = loginModuleClassName;
    this.options = options;
    this.suitableAuth = suitableAuth;
    this.notSuitableAuth = notSuitableAuth;
    this.editor = editor;
  }

  /**
   *  Returns the description of the login module.
   *
   * @return  printable text.
   */
  public String getDescription() {
    return description;
  }


  /**
   *  Returns the display name of the login module.
   *
   * @return  display name.
   */
  public String getName() {
    return name;
  }


  /**
   *  Hints for common authentication mechanisms this login module is not
   * suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getNotSuitableAuthenticationMechanisms() {
    return notSuitableAuth;
  }


  /**
   *  Returns the class name of the login module.
   *
   * @return  class name.
   */
  public String getLoginModuleClassName() {
    return loginModuleClassName;
  }


  /**
   *  Returns the options of the login module.
   *
   * @return  the options of the login module.
   */
  public Map getOptions() {
    return options;
  }


  /**
   *  Hints for common authentication mechanisms this login module is
   * suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getSuitableAuthenticationMechanisms() {
    return suitableAuth;
  }

  /**
   * Gets the special editor suitable for this login module options.
   *
   * @return  the editor for this login module options, or null if the
   *          default one should be used.
   */
  public String getOptionsEditor() {
    return editor;
  }

  /**
   *  Sets the description of the login module.
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   *  Sets the display name of the login module.
   */
  public void setName(String name) {
    this.name = name;
  }


  /**
   *  Sets common authentication mechanisms this login module is not
   * suitable for.
   */
  public void setNotSuitableAuthenticationMechanisms(String[] notSuitableAuth) {
    this.notSuitableAuth = notSuitableAuth;
  }


  /**
   *  Sets the class name of the login module.
   */
  public void setLoginModuleClassName(String loginModuleClassName) {
    this.loginModuleClassName = loginModuleClassName;
  }


  /**
   *  Sets the options of the login module.
   */
  public void setOptions(Properties options) {
    this.options = options;
  }


  /**
   *  Sets common authentication mechanisms this login module is suitable for.
   */
  public void setSuitableAuthenticationMechanisms(String[] suitableAuth) {
    this.suitableAuth = suitableAuth;
  }


  /**
   *  Sets the GUI (options) editor of the login module. All Java Swing tools
   * that work with the configuration of login modules may attempt to visualize
   * instance of this class to display and modify options of the login module.
   *
   * @param editor  class name of the editor.
   */
  public void setOptionsEditor(String editor) {
    this.editor = editor;
  }

}
