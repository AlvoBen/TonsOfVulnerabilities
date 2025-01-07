package com.sap.engine.services.security.userstore.policy;

import com.sap.engine.interfaces.security.userstore.config.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import java.util.*;

public class DefaultAuthorizationDescriptor implements AuthorizationDescriptor {
  Hashtable userInRole = new Hashtable();
  Hashtable groupInRole = new Hashtable();
  
  public DefaultAuthorizationDescriptor(Node node) {
    String elementName = null;
    Node element = null;
    NodeList list = node.getChildNodes();

    for (int i = 0; i < list.getLength(); i++) {
      element = list.item(i);
      elementName = element.getNodeName();

      if ("security-role".equalsIgnoreCase(elementName)) {
        readSecurityRole(element);
      } else if (element.getNodeType() == Node.ELEMENT_NODE) {
        System.out.println(" unknown element '" + elementName + "' in user-store description.");
      }
    }
  }

  public String[] listSecurityRoles() throws SecurityException {
    Enumeration keys = userInRole.keys();
    String[] roles = new String[userInRole.size()];
    for (int i = 0; i < roles.length; i++) {
      roles[i] = (String) keys.nextElement();
    }
    return roles;
  }
  
  public String[] listUsersInRole(String role) throws SecurityException {
    return (String[]) userInRole.get(role);
  }
  
  public String[] listGroupsInRole(String role) throws SecurityException {
    return (String[]) groupInRole.get(role);
  }

  private void readSecurityRole(Node node) {
    String securityRole = null;
    NamedNodeMap attributes = node.getAttributes();
    Node current = null;
    for (int i = 0; i < attributes.getLength(); i++) {
      current = attributes.item(i);
      if (current.getNodeName().equals("name")) {
        securityRole = current.getNodeValue();
      }
    }
    
    Vector users = new Vector();
    Vector groups = new Vector();
    
    String elementName = null;
    Node element = null;
    NodeList list = node.getChildNodes();

    for (int i = 0; i < list.getLength(); i++) {
      element = list.item(i);
      elementName = element.getNodeName();

      if ("user".equalsIgnoreCase(elementName)) {
        users.addElement(element.getFirstChild().getNodeValue());
      } else if ("group".equalsIgnoreCase(elementName)) {
        groups.addElement(element.getFirstChild().getNodeValue());
      }
    }
    
    String[] roleUsers = new String[users.size()];
    for (int i = 0; i < roleUsers.length; i++) {
      roleUsers[i] = (String) users.elementAt(i);
    }
    
    String[] roleGroups = new String[groups.size()];
    for (int i = 0; i < roleGroups.length; i++) {
      roleGroups[i] = (String) groups.elementAt(i);
    }
    
    userInRole.put(securityRole, roleUsers);
    groupInRole.put(securityRole, roleGroups);
  }
  
  public String toString() {
    String s = "";
    s += "<authorization>\r\n";
    String[] roles = listSecurityRoles();
    for (int i = 0; i < roles.length; i++) {
      s += "<security-role name=" + roles[i] + ">\r\n";
      String[] users = listUsersInRole(roles[i]);
      s += "<users>\r\n";
      for (int j = 0; j < users.length; j++) {
        s += users[j] + "\r\n";
      }
      s += "</users>\r\n";
      String[] groups = listGroupsInRole(roles[i]);
      s += "<groups>\r\n";
      for (int j = 0; j < groups.length; j++) {
        s += groups[j] + "\r\n";
      }
      s += "</groups>\r\n"; 
      s += "</security-role>\r\n";     
    }
    s += "</authorization>\r\n";
    return s;
  }
}