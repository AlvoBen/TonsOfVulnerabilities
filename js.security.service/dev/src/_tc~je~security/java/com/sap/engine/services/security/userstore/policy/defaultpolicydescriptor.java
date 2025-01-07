package com.sap.engine.services.security.userstore.policy;

import com.sap.engine.interfaces.security.userstore.config.*;
import com.sap.engine.frame.ServiceException;
import com.sap.tc.logging.Location;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import java.util.*;

public class DefaultPolicyDescriptor implements PolicyDescriptor {

  private final static Location LOCATION = Location.getLocation(DefaultPolicyDescriptor.class);

  String userstore = null;
  String anonymousUser = null;
  Hashtable policyConfigurations = new Hashtable();

  public DefaultPolicyDescriptor(Node node) throws ServiceException {
    this.userstore = readAttribute(node, "userstore");
    String elementName = null;
    Node element = null;
    NodeList list = node.getChildNodes();
    String configName = null;

    for (int i = 0; i < list.getLength(); i++) {
      element = list.item(i);
      elementName = element.getNodeName();

      if ("configuration".equalsIgnoreCase(elementName)) {
        configName = readAttribute(element, "name");
        PolicyConfigurationDescriptor config = new PolicyConfigurationDescriptor(element);
        policyConfigurations.put(configName, config);
      } else if (element.getNodeType() == Node.ELEMENT_NODE) {
        System.out.println(" unknown element '" + elementName + "' in policy description.");
      }
    }

    if (policyConfigurations.get(ROOT_POLICY_CONFIGURATION) == null) {
      throw new ServiceException(LOCATION, new Exception("policy.xml file does not have configiration for 'SAP-J2EE-Engine' template."));
    }
  }

  public String getUserStore() throws SecurityException {
    return userstore;
  }

  public String getAnonymousUser() throws SecurityException {
    return anonymousUser;
  }

  public Enumeration listPolicyConfigurations() throws SecurityException {
    return policyConfigurations.keys();
  }

  public AuthorizationDescriptor getAuthorizationDescriptor(String policyConfiguration) throws SecurityException {
    return ((PolicyConfigurationDescriptor) policyConfigurations.get(policyConfiguration)).getAuthorization();
  }

  public AuthenticationDescriptor getAuthenticationDescriptor(String policyConfiguration) throws SecurityException {
    return ((PolicyConfigurationDescriptor) policyConfigurations.get(policyConfiguration)).getAuthentication();
  }

  private void readAnonymousUser(Node node) {
    this.anonymousUser = node.getFirstChild().getNodeValue();
  }

  private String readAttribute(Node node, String name) {
    NamedNodeMap attributes = node.getAttributes();
    Node current = null;
    for (int i = 0; i < attributes.getLength(); i++) {
      current = attributes.item(i);
      if (current.getNodeName().equalsIgnoreCase(name)) {
        return current.getNodeValue();
      }
    }
    return null;
  }
  
  public String getPolicyConfigurationType(String policyConfiguration) throws SecurityException {
    return ((PolicyConfigurationDescriptor) policyConfigurations.get(policyConfiguration)).getType();
  }


  public String toString() {
    String s = "";
    s += "<policy userstore=" + getUserStore() + ">\r\n";
    Enumeration e = listPolicyConfigurations();

    while(e.hasMoreElements()) {
      String configuration = (String)e.nextElement();
      s += "CONFGURATION " + configuration + "\r\n";
      s += policyConfigurations.get(configuration).toString() + "\r\n";
    }
    s += "</policy>\r\n";
    return s;
  }
}