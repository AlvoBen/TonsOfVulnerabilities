package com.sap.engine.services.security.userstore.policy;

import com.sap.engine.interfaces.security.userstore.config.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PolicyConfigurationDescriptor {
  AuthenticationDescriptor authentication = null;
  AuthorizationDescriptor  authorization  = null;
  String type = null;
  
  public PolicyConfigurationDescriptor(Node node) {
    String elementName = null;
    Node element = null;
    NodeList list = node.getChildNodes();
  
    for (int i = 0; i < list.getLength(); i++) {
      element = list.item(i);
      elementName = element.getNodeName();

      if ("authentication".equalsIgnoreCase(elementName)) {
        authentication = new DefaultAuthenticationDescriptor(element);
      } else if ("authorization".equalsIgnoreCase(elementName)) {
        authorization = new DefaultAuthorizationDescriptor(element);
      } if ("type".equalsIgnoreCase(elementName)) {
        type = element.getFirstChild().getNodeValue();
      } else if (element.getNodeType() == Node.ELEMENT_NODE) {
        System.out.println(" unknown element '" + elementName + "' in user-store description.");
      }
    }
  }
  
  public AuthenticationDescriptor getAuthentication() {
    return authentication;
  }
  
  public AuthorizationDescriptor getAuthorization() {
    return authorization;
  }
  
  public String getType() {
    return type;
  }
  
  public String toString() {
    String s = "";
    s += "<configuration>\r\n";
    s += "<type>\r\n" + type + "\r\n</type>\r\n";
    if (getAuthentication() != null) {
      s += getAuthentication().toString() + "\r\n";
    } else {
      s += "<authentication>\r\n";
      s += "null\r\n";
      s += "</authentication>\r\n";
    }
    if (getAuthorization() != null) {
      s += getAuthorization().toString() + "\r\n";
    } else {
      s += "<authorization>\r\n";
      s += "null\r\n";
      s += "</authorization>\r\n";
    }        
    s += "</configuration>\r\n";
    return s;
  }
}