package com.sap.engine.services.security.patch;

import java.io.IOException;
import java.io.InputStream;

import java.util.Vector;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.interfaces.security.SecurityContext;

import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.util.config.IUMConfigAdmin;
import com.sap.security.core.util.config.IUMConfigExtended;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class AdditionalChange5 implements Change {

  private static final Location LOCATION = Location.getLocation(AdditionalChange5.class);

  // Constants
  private static final String AUTHSCHEME = "authscheme";
  private static final String AUTHSCHEMES = "authschemes";
  private static final String FRONTENDTARGET = "frontendtarget";
  private static final String AUTHTEMPLATE = "authentication-template";
  private static final String PRIORITY = "priority";
  private static final String AS_REFS = "authscheme-refs";

  private static final String AUTH_XML_FILE_PROPERTY = "login.authschemes.definition.file";
  private static final String AUTH_XML_FILE_PROPERTY_DEFAULT = "authschemes.xml";

  public void run() throws Exception {

    ConfigurationHandler configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
    try {
      IUMConfigExtended configExtended = InternalUMFactory.getConfigExtended();

      String xmlFileName = configExtended.getAllPropertiesDynamic().getProperty(AUTH_XML_FILE_PROPERTY, AUTH_XML_FILE_PROPERTY_DEFAULT);

      //read authschemes
      InputStream xmlInputStream = configExtended.readConfigFile(xmlFileName);
      ArrayList<AuthschemeContent> authschemesArrayList = getAuthSchemesFromFile(xmlInputStream);

      //read authscheme references
      xmlInputStream = configExtended.readConfigFile(xmlFileName);
      ArrayList<AuthschemeReferenceContent> authschemeReferencesArrayList = getAuthSchemeReferencesFromFile(xmlInputStream);

      ArrayList<String> authNames = getAuthEntriesNames(authschemesArrayList, authschemeReferencesArrayList);

      ArrayList<String> existingConfigurations = getAlreadyExistingPolicyConfigurationNames(configHandler, authNames);

      //if some authschemes have the same names as some policy configurations
      //then no migration is done
      if (existingConfigurations != null && !existingConfigurations.isEmpty()) {
        SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000176", "The following authschemes can not be migrated. Policy configurations with the same names already exist: {0}", new Object[]{existingConfigurations.toString()});
        configHandler.rollback();
        return;
      }

      for (AuthschemeContent authschemeContent : authschemesArrayList) {
        addAuthschemePolicyConfiguration(configHandler, authschemeContent);
      }

      for (AuthschemeReferenceContent authschemeReferenceContent : authschemeReferencesArrayList) {
        addAuthschemeReferencePolicyConfiguration(configHandler, authschemeReferenceContent);
      }

      try {
        IUMConfigAdmin configAdmin = InternalUMFactory.getConfigAdmin();
        configAdmin.setString(AUTH_XML_FILE_PROPERTY, "");
      } catch (Exception e) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000177", "Error while setting {0} to \"\".", new Object[]{AUTH_XML_FILE_PROPERTY});
        configHandler.rollback();
        return;
      }
    
      configHandler.commit();
      LOCATION.logT(Severity.INFO, "Authschemes migration completed successfully.");
      
    } catch (Exception e) {
      configHandler.rollback();
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000178", "Authschemes migration failed!", e); 
    } finally {
      configHandler.closeAllConfigurations();
    }

  }

  private ArrayList<AuthschemeContent> getAuthSchemesFromFile(InputStream inputStream) {

    ArrayList<AuthschemeContent> arrayList = new ArrayList<AuthschemeContent>();

    Document document = XMLUtil.loadXMLFile(inputStream);

    //try to access the top node
    Element eAuthSchemes = XMLUtil.getElementChildByName(document.getDocumentElement(), AUTHSCHEMES);
    if (eAuthSchemes != null) {

      NodeList nlAuthschemes = eAuthSchemes.getChildNodes();

      for (int i = 0; i < nlAuthschemes.getLength(); i++) {

        Node nAuthScheme = nlAuthschemes.item(i);

        // authschemes are element nodes
        if (nAuthScheme.getNodeType() == Node.ELEMENT_NODE) {
          // get the login modules                     
          String name = XMLUtil.getAttributeValue(nAuthScheme, "name");

          String auth_template = XMLUtil.getElementChildContentByName(nAuthScheme, AUTHTEMPLATE);
          String priority = XMLUtil.getElementChildContentByName(nAuthScheme, PRIORITY);
          String frontendTarget = XMLUtil.getElementChildContentByName(nAuthScheme, FRONTENDTARGET);

          AuthschemeContent authschemeContent = new AuthschemeContent();
          authschemeContent.name = name;
          authschemeContent.template = auth_template;
          authschemeContent.priority = priority;
          authschemeContent.frontendtarget = frontendTarget;

          arrayList.add(authschemeContent);
        }

      }

    } else {
      LOCATION.warningT("No authschemes section found!");
    }
    return arrayList;

  }

  private ArrayList<AuthschemeReferenceContent> getAuthSchemeReferencesFromFile(InputStream inputStream) {

    ArrayList<AuthschemeReferenceContent> arrayList = new ArrayList<AuthschemeReferenceContent>();

    Document document = XMLUtil.loadXMLFile(inputStream);

    //try to access the top node
    Element eRefAuthSchemes = XMLUtil.getElementChildByName(document.getDocumentElement(), AS_REFS);

    if (eRefAuthSchemes != null) {
      NodeList nlRefs = eRefAuthSchemes.getChildNodes();

      for (int i = 0; i < nlRefs.getLength(); i++) {

        if (nlRefs.item(i).getNodeType() == Node.ELEMENT_NODE) {
          Node nRef = nlRefs.item(i);

          String name = XMLUtil.getAttributeValue(nRef, "name");

          String authschemeName = XMLUtil.getElementChildContentByName(nRef, AUTHSCHEME);

          AuthschemeReferenceContent authschemeReferenceContent = new AuthschemeReferenceContent();
          authschemeReferenceContent.name = name;
          authschemeReferenceContent.referencedAuthschemeName = authschemeName;

          arrayList.add(authschemeReferenceContent);

        }
      }

    } else {
      LOCATION.warningT("No authscheme-references section found!");
    }
    return arrayList;
  }

  private static ArrayList<String> getAuthEntriesNames(ArrayList<AuthschemeContent> authschemesArrayList, ArrayList<AuthschemeReferenceContent> authschemeReferencesArrayList) {

    ArrayList<String> result = new ArrayList<String>(authschemesArrayList.size() + authschemeReferencesArrayList.size());

    for (AuthschemeContent authContent : authschemesArrayList) {
      result.add(authContent.name);
    }

    for (AuthschemeReferenceContent authRefContent : authschemeReferencesArrayList) {
      result.add(authRefContent.name);
    }

    return result;
  }

  private ArrayList<String> getAlreadyExistingPolicyConfigurationNames(ConfigurationHandler configHandler, ArrayList<String> authschemes) throws NameNotFoundException, ConfigurationLockedException,
      ConfigurationException {

    ArrayList<String> result = new ArrayList<String>();
    Configuration securityConfigurations = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, ConfigurationHandler.READ_ACCESS);

    for (String authschemeName : authschemes) {
      if (securityConfigurations.existsConfigEntry(authschemeName)) {
        result.add(authschemeName);
      }
    }

    return result;

  }

  /**
   * Creates a new authscheme configuration for a given name and property names and values.
   * If property names is null then { "template", "priority", "frontendtarget" } is used.
   * 
   * @param configHandler
   * @param authschemeName
   * @param propertyValues
   * @throws Exception
   */

  private void addAuthschemePolicyConfiguration(ConfigurationHandler configHandler, AuthschemeContent authschemeContent) throws Exception {
    final String[] DEFAULT_PROPERTY_NAMES = { "template", "priority", "frontendtarget" };

    String authschemeName = authschemeContent.name;
    String[] propertyNames = DEFAULT_PROPERTY_NAMES;
    final String[] propertyValues = { authschemeContent.template, authschemeContent.priority, authschemeContent.frontendtarget };

    final String policyConfigurationType = "" + SecurityContext.TYPE_AUTHSCHEME;

    try {
      Configuration securityConfigurations = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, ConfigurationHandler.WRITE_ACCESS);

      if (securityConfigurations != null) {
        if (!securityConfigurations.existsConfigEntry(authschemeName)) {
          securityConfigurations.addConfigEntry(authschemeName, SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + "/" + authschemeName);
        } else {
          securityConfigurations.modifyConfigEntry(authschemeName, SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + "/" + authschemeName);
        }

        if (securityConfigurations.existsSubConfiguration(authschemeName)) {
          securityConfigurations.deleteConfiguration(authschemeName);
        }

        Configuration newPolicyConfiguration = securityConfigurations.createSubConfiguration(authschemeName);
        Configuration security = newPolicyConfiguration.createSubConfiguration("security");
        security.addConfigEntry("type", policyConfigurationType);
        Configuration authentication = security.createSubConfiguration("authentication");

        for (int i = 0; i < propertyNames.length; i++) {
          if (propertyValues[i] != null) {
            authentication.addConfigEntry(propertyNames[i], propertyValues[i]);
          }
        }
      }

      configHandler.commit();
    } catch (Exception e) {
      try {
        configHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
      throw e;
    } finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
  }

  /**
   * Creates a new authscheme configuration for a given name and property names and values.
   * If property names is null then { "template", "priority", "frontendtarget" } is used.
   * 
   * @param configHandler
   * @param authschemeReferenceName
   * @param propertyValues
   * @throws Exception
   */

  private void addAuthschemeReferencePolicyConfiguration(ConfigurationHandler configHandler, AuthschemeReferenceContent authschemeReferenceContent) throws Exception {

    String authschemeReferenceName = authschemeReferenceContent.name;
    String referencedAuthschemeName = authschemeReferenceContent.referencedAuthschemeName;

    final String policyConfigurationType = "" + SecurityContext.TYPE_AUTHSCHEME_REFERENCE;

    try {
      Configuration securityConfigurations = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, ConfigurationHandler.WRITE_ACCESS);

      if (securityConfigurations != null) {
        if (!securityConfigurations.existsConfigEntry(authschemeReferenceName)) {
          securityConfigurations.addConfigEntry(authschemeReferenceName, SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + "/" + authschemeReferenceName);
        } else {
          securityConfigurations.modifyConfigEntry(authschemeReferenceName, SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + "/" + authschemeReferenceName);
        }

        if (securityConfigurations.existsSubConfiguration(authschemeReferenceName)) {
          securityConfigurations.deleteConfiguration(authschemeReferenceName);
        }

        Configuration newPolicyConfiguration = securityConfigurations.createSubConfiguration(authschemeReferenceName);
        Configuration security = newPolicyConfiguration.createSubConfiguration("security");
        security.addConfigEntry("type", policyConfigurationType);
        Configuration authentication = security.createSubConfiguration("authentication");
        if (referencedAuthschemeName != null) {
          authentication.addConfigEntry("template", referencedAuthschemeName);
        }
      }

      configHandler.commit();
    } catch (Exception e) {
      try {
        configHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
      throw e;
    } finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
  }

  private class AuthschemeContent {
    String name;

    String template;

    String priority;

    String frontendtarget;
  }

  private class AuthschemeReferenceContent {
    String name;

    String referencedAuthschemeName;
  }

  /**
   * Tools for loading and parsing XML files.
   */
  static class XMLUtil {
    static Document loadXMLFile(InputStream inputStream) {
      String methodName = "XMLUtil.loadXMLFile";
      //initialize XML parser
      DocumentBuilderFactory factory = null;
      ClassLoader current = Thread.currentThread().getContextClassLoader();
      ClassLoader mine = XMLUtil.class.getClassLoader();
      if (mine == null)
        mine = ClassLoader.getSystemClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(mine);
        factory = DocumentBuilderFactory.newInstance();
      } finally {
        Thread.currentThread().setContextClassLoader(current);
      }

      factory.setIgnoringComments(true);
      factory.setIgnoringElementContentWhitespace(true);

      DocumentBuilder parser = null;

      try {
        parser = factory.newDocumentBuilder();
      } catch (ParserConfigurationException pcex) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000179", "Can't create DocumentBuilder.", pcex);
      }

      Document document = null;

      try {
        //build a dom from the content of the configFile
        document = parser.parse(inputStream, "ISO-8859-1");
      } catch (IOException ioex) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, ioex, "ASJ.secsrv.000180", "Error while opening config file: {0}", new Object[]{ioex.getMessage()});
      } catch (SAXException saxex) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000181", "Can't create DocumentBuilder.", saxex);
      }

      return document;

    }

    /**
     * returns all child elements of a given node which is named name
     */
    static Element[] getElementChildrenByName(Node node, String name) {

      Vector<Element> stapel = new Vector<Element>();
      Element element = getFirstChildElement(node);
      String nodeName = null;

      while ((element != null) && ((nodeName = element.getNodeName()) != null)) {
        if (nodeName.equals(name)) {
          stapel.add(element);
        }

        element = getNextSiblingElement(element);
      }

      Element[] rc = new Element[stapel.size()];
      for (int i = 0; i < rc.length; i++) {
        rc[i] = (Element) stapel.get(i);
      }

      return rc;
    }

    /**
     * returns the first child element of a given node which is named name
     */
    static Element getElementChildByName(Node node, String name) {

      Element element = getFirstChildElement(node);
      String nodeName = null;

      while ((element != null) && (((nodeName = element.getNodeName()) == null) || (!nodeName.equals(name)))) {

        element = getNextSiblingElement(element);
      }

      return element;
    }

    /**
     * returns the content of the xml node which is named name
     */
    static String getElementChildContentByName(Node node, String name) {

      Element element = getElementChildByName(node, name);

      if (element != null) {

        Node child = element.getFirstChild();

        if ((child != null) && ((child.getNodeType() == Node.TEXT_NODE) || (child.getNodeType() == Node.CDATA_SECTION_NODE))) {
          String result = ((Text) child).getData();
          return trimWhiteSpace(result);
        }
      }

      return null;
    }

    /**
     * returns the content of the given xml node
     */
    static String getElementContent(Element element) {

      if (element != null) {

        Node child = element.getFirstChild();

        if ((child != null) && ((child.getNodeType() == Node.TEXT_NODE) || (child.getNodeType() == Node.CDATA_SECTION_NODE))) {

          String result = ((Text) child).getData();
          return trimWhiteSpace(result);
        }
      }

      return null;
    }

    /**
     * returns the first child node of a given xml node
     */
    static Element getFirstChildElement(Node node) {

      if (node != null) {

        node = node.getFirstChild();
        if (node == null) {

          return null;
        }

        if (node.getNodeType() == Node.ELEMENT_NODE) {

          return (Element) node;
        } else {

          return getNextSiblingElement(node);
        }
      } else {

        return null;
      }
    }

    /**
     * returns the next sibling node of a given xml node
     */
    static Element getNextSiblingElement(Node node) {

      if (node != null) {

        node = node.getNextSibling();
        while ((node != null) && (node.getNodeType() != Node.ELEMENT_NODE)) {
          node = node.getNextSibling();
        }

        return (Element) node;
      }

      return null;
    }

    static String getAttributeValue(Node node, String name) {
      String rc = null;
      NamedNodeMap attrs = node.getAttributes();
      Node attr = attrs.getNamedItem(name);
      if (attr != null) {
        rc = attr.getNodeValue();
      }

      return rc;
    }

    static boolean isWhiteSpaceChar(char ch) {
      if (ch == 0x20 || ch == 0xD || ch == 0xA || ch == 0x9) {
        return true;
      } else {
        return false;
      }
    }

    static String trimWhiteSpace(String data) {
      int b, e;
      if (data == null) {
        return "";
      }
      for (b = 0; b < data.length() && isWhiteSpaceChar(data.charAt(b)); b++) {
        ;
      }
      for (e = data.length() - 1; e > 0 && e > b && isWhiteSpaceChar(data.charAt(e)); e--) {
        ;
      }
      data = data.substring(b, e + 1);
      return data;
    }
  }

}
