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

import java.util.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import com.sap.engine.interfaces.security.userstore.config.*;

/**
 *  Configuration of a single user store. It holds a UserContext instance, configuration properties
 * and a set of login modules.
 *
 * @version 6.30
 * @author  Stephan Zlatarev
 */
public class DefaultUserStoreConfiguration implements UserStoreConfiguration {

  private boolean isActive;
  private String name;
  private String description;
  private String editorClassName;
  private String groupContextClassName;
  private String userContextClassName;
  private Properties configuration = new Properties();
  private Properties startupConfiguration = new Properties();
  private LoginModuleConfiguration[] modules;
  private String anonymous = null;

  public DefaultUserStoreConfiguration(Node node) {
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
      } else if ("user-class-name".equalsIgnoreCase(elementName)) {
        readUserContextClassName(element);
      } else if ("group-class-name".equalsIgnoreCase(elementName)) {
        readGroupContextClassName(element);
      } else if ("configuration-editor".equalsIgnoreCase(elementName)) {
        readConfigurationEditorClassName(element);
      } else if ("configuration".equalsIgnoreCase(elementName)) {
        readConfiguration(element, false);
      } else if ("startup-configuration".equalsIgnoreCase(elementName)) {
        readConfiguration(element, true);
      } else if ("login-module".equalsIgnoreCase(elementName)) {
        readLoginModule(element);
      } else if ("anonymous-user".equalsIgnoreCase(elementName)) {
        readAnonymousUser(element);
      } else if (element.getNodeType() == Node.ELEMENT_NODE) {
        System.out.println(" unknown element '" + elementName + "' in user-store description.");
      }
    }

    readIsActive(node);

    if (anonymous == null) {
      anonymous = configuration.getProperty("anonymous-user");
    } else {
      configuration.setProperty("anonymous-user", anonymous);
    }
  }

  public String getConfigurationEditorClassName() {
    return editorClassName;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public String getAnonymousUser() throws SecurityException {
    return anonymous;
  }

  public LoginModuleConfiguration[] getLoginModules() {
    return modules;
  }

  public String getUserSpiClassName() {
    return userContextClassName;
  }

  public String getGroupSpiClassName() {
    return groupContextClassName;
  }

  public String getProperty(String key) {
    return configuration.getProperty(key);
  }

  public Properties getUserStoreProperties() {
    if (startupConfiguration.size() == 0) {
      return configuration;
    }

    Properties result = new Properties();
    result.putAll(configuration);
    result.putAll(startupConfiguration);
    return result;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  public void clearStartupConfiguration() {
    startupConfiguration.clear();
  }

  private void readIsActive(Node node) {
    Node isActiveNode = node.getAttributes().getNamedItem("active");

    if (isActiveNode != null) {
      isActive = "yes".equalsIgnoreCase(isActiveNode.getNodeValue());
    }
  }

  private void readConfiguration(Node node, boolean startup) {
    NamedNodeMap attributes = node.getAttributes();
    Node current = null;
    for (int i = 0; i < attributes.getLength(); i++) {
      current = attributes.item(i);
      if (!startup) {
        configuration.setProperty(current.getNodeName(), current.getNodeValue());
      } else {
        startupConfiguration.setProperty(current.getNodeName(), current.getNodeValue());
      }
    }
  }

  private void readDisplayName(Node node) {
    name = node.getFirstChild().getNodeValue();
  }

  private void readDescription(Node node) {
    description = node.getFirstChild().getNodeValue();
  }

  private void addLoginModule(LoginModuleConfiguration module) {
    if (modules == null) {
      modules = new LoginModuleConfiguration[1];
    } else {
      LoginModuleConfiguration[] temp = new LoginModuleConfiguration[modules.length + 1];
      System.arraycopy(modules, 0, temp, 0, modules.length);
      modules = temp;
    }

    modules[modules.length - 1] = module;
  }

  private void readLoginModule(Node node) {
    addLoginModule(new DefaultLoginModuleConfiguration(node));
  }

  private void readAnonymousUser(Node node) {
    this.anonymous = node.getFirstChild().getNodeValue();
  }

  private void readUserContextClassName(Node node) {
    userContextClassName = node.getFirstChild().getNodeValue();
  }

  private void readGroupContextClassName(Node node) {
    groupContextClassName = node.getFirstChild().getNodeValue();
  }

  private void readConfigurationEditorClassName(Node node) {
    editorClassName = node.getFirstChild().getNodeValue();
  }

  public String toString() {
    return "configuration \r\n" + "name : " + name + "\r\n" + "UserSpiClass : " + userContextClassName + "\r\n" + "GroupSpiClass : " + groupContextClassName + "\r\n";
  }
}

