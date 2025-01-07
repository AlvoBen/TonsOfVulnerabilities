package com.sap.engine.services.security.userstore.policy;

import com.sap.engine.interfaces.security.userstore.config.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import javax.security.auth.login.AppConfigurationEntry;

import java.util.Map;
import java.util.Vector;
import java.util.Properties;

public class DefaultAuthenticationDescriptor implements AuthenticationDescriptor {
  private Vector entries = new Vector();
  private String template = null;

  public DefaultAuthenticationDescriptor(Node node) {
    String elementName = null;
    Node element = null;
    NodeList list = node.getChildNodes();

    for (int i = 0; i < list.getLength(); i++) {
      element = list.item(i);
      elementName = element.getNodeName();

      if ("entry".equalsIgnoreCase(elementName)) {
        readAppEntry(element);
      } else if ("template".equalsIgnoreCase(elementName)) {
        readTemplate(element);
      } else if (element.getNodeType() == Node.ELEMENT_NODE) {
        System.out.println(" unknown element '" + elementName + "' in user-store description.");
      }
    }
  }

  public AppConfigurationEntry[] listAppConfigurationEntries() throws SecurityException {
    AppConfigurationEntry[] result = new AppConfigurationEntry[entries.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = (AppConfigurationEntry) entries.elementAt(i);
    }
    return result;
  }

  public String getTemplate() throws SecurityException {
    return template;
  }

  private void readAppEntry(Node node) {
    NodeList list = node.getChildNodes();
    AppConfigurationEntry.LoginModuleControlFlag flag = null;
    String className = null;
    Properties options = new Properties();
    Node element = null;
    String elementName = null;
        
    for (int i = 0; i < list.getLength(); i++) {
      element = list.item(i);
      elementName = element.getNodeName();
      if (elementName.equals("flag")) {
        String theflag = element.getFirstChild().getNodeValue();
        if (theflag.equalsIgnoreCase("requisite")) {
          flag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
        } else if (theflag.equalsIgnoreCase("required")) {
          flag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
        } else if (theflag.equalsIgnoreCase("sufficient")) {
          flag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
        } else if (theflag.equalsIgnoreCase("optional")) {
          flag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
        } else {
          System.out.println("unknown control flag");
          return;
        }
      } else if (elementName.equals("classname")) {
        className = element.getFirstChild().getNodeValue();
      } else if (elementName.equals("option")){
        NamedNodeMap attributes = element.getAttributes();
        Node current = null;
        for (int j = 0; j < attributes.getLength(); j++) {
          current = attributes.item(j);
          options.setProperty(current.getNodeName(), current.getNodeValue());
        }
      }
    }
    AppConfigurationEntry entry = new AppConfigurationEntry(className, flag, (Map) options);
    entries.addElement(entry);
  }

  private void readTemplate(Node element) {
    template = element.getFirstChild().getNodeValue();
  }

  public String toString() {
    String s = "";
    s += "<authentication>\r\n";
    AppConfigurationEntry[] entries = listAppConfigurationEntries();
    for (int i = 0; i < entries.length; i++) {
      s += entries[i].getLoginModuleName() + " " + entries[i].getControlFlag().toString().substring(23) + " " + entries[i].getOptions()+ "\r\n";
    }
    s += "</authentication>\r\n";
    return s;
  }
}