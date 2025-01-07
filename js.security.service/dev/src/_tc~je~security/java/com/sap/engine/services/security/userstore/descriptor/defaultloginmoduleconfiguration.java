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
package com.sap.engine.services.security.userstore.descriptor;

import java.util.Properties;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;

public class DefaultLoginModuleConfiguration implements LoginModuleConfiguration {

  private String name;
  private String description;
  private String loginModuleClassName;
  private Properties options = new Properties();
  private String[] suitableAuth = new String[0];
  private String[] notSuitableAuth = new String[0];
  private String editor = null;

  public DefaultLoginModuleConfiguration(Node node) {
    String elementName = null;
    Node element = null;
    NodeList list = node.getChildNodes();

    for (int i = 0; i < list.getLength(); i++) {
      element = list.item(i);
      elementName = element.getNodeName();

      if ("display-name".equalsIgnoreCase(elementName)) {
        readDisplayName(element);
      } else if ("description".equalsIgnoreCase(elementName)) {
        readDescription(element);
      } else if ("class-name".equalsIgnoreCase(elementName)) {
        readLoginModuleClassName(element);
      } else if ("options".equalsIgnoreCase(elementName)) {
        readOption(element);
      } else if ("suitable-mechanism".equalsIgnoreCase(elementName)) {
        readSuitableMechanism(element);
      } else if ("not-suitable-mechanism".equalsIgnoreCase(elementName)) {
        readNotSuitableMechanism(element);
      } else if ("options-editor".equalsIgnoreCase(elementName)) {
        readOptionsEditor(element);
      } else if (element.getNodeType() == Node.ELEMENT_NODE) {
        System.out.println(" unknown element '" + elementName + "' in login-module description.");
      }
    } 
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
   *  Hints for common authentication mechanisms this login module is not suitable for.
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
   * @return  options.
   */
  public Map getOptions() {
    return options;
  }

  /**
   *  Hints for common authentication mechanisms this login module is suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getSuitableAuthenticationMechanisms() {
    return suitableAuth;
  }

  /**
   * Gets the special editor suitable for this login module options.
   *
   * @return  the editor for this login module options, or null if the default one should be used.
   */
  public String getOptionsEditor() {
    return editor;
  }

  private void readOption(Node node) {
    NamedNodeMap attributes = node.getAttributes();
    Node current = null;
    for (int i = 0; i < attributes.getLength(); i++) {
      current = attributes.item(i);
      options.setProperty(current.getNodeName(), current.getNodeValue());
    }
  }

  private void readDisplayName(Node node) {
    name = node.getFirstChild().getNodeValue();
  }

  private void readDescription(Node node) {
    description = node.getFirstChild().getNodeValue();
  }

  private void readLoginModuleClassName(Node node) {
    loginModuleClassName = node.getFirstChild().getNodeValue();
  }

  private void readOptionsEditor(Node node) {
    editor = node.getFirstChild().getNodeValue();
  }

  private void readSuitableMechanism(Node node) {
    String auth = node.getFirstChild().getNodeValue();
    String[] temp = new String[suitableAuth.length + 1];
    System.arraycopy(suitableAuth, 0, temp, 0, suitableAuth.length);
    temp[suitableAuth.length] = auth;
    suitableAuth = temp;
  }

  private void readNotSuitableMechanism(Node node) {
    String auth = node.getFirstChild().getNodeValue();
    String[] temp = new String[notSuitableAuth.length + 1];
    System.arraycopy(notSuitableAuth, 0, temp, 0, notSuitableAuth.length);
    temp[notSuitableAuth.length] = auth;
    notSuitableAuth = temp;
  }

}

