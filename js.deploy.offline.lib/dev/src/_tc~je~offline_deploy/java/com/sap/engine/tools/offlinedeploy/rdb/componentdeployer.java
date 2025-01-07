package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sap.tc.logging.Severity;

/**
 * This class is used to deploy components with software type primary-interface, primary-library,
 * library and primary-service.
 *
 * @version 710
 * @author Dimitar Kostadinov
 */
public class ComponentDeployer implements Constants {

  static Properties deploy(ConfigurationHandler handler, ZipFile zipFile, DOMParseHelper parser, String csnComponent, String dcName, byte componentType, boolean isOffline) throws IOException, SAXException, ConfigurationException, DeploymentException {
    boolean isService = componentType == OfflineComponentDeploy.TYPE_SERVICE;
    Properties provider = getProviderProperties(zipFile, parser, isService);
    String runtimeName = Utils.modifyComponentName(provider.getProperty("component-name"), provider.getProperty("provider-name"));
    provider.setProperty("runtime-name", runtimeName);
    provider.setProperty("dc-name", dcName);
    if (csnComponent != null) {
      provider.setProperty("csn-component", csnComponent);
    }
    // upload native parts
    ZipEntry searchRules = zipFile.getEntry(SEARCH_RULES_XML);
    if (searchRules != null) {
      if (isOffline) {
        Document natives = parser.parse(searchRules, zipFile);
        NativeDeployer.uploadNatives(handler, zipFile, natives, getTypeBase(componentType), runtimeName);
      } else {
        throw new DeploymentException("Online SDA [" + zipFile.getName() + "] can not contain native parts. Please remove META-INF/SearchRules.xml and corresponding natives under OS_libs");
      }
    } else {
      // Remove natives for the component
      // This is in case the old deployed version had natives; the new one does not
      NativeDeployer.removeNatives(handler, getTypeBase(componentType), runtimeName);
    }

    // upload jars
    Configuration runtime = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN + "/" + RUNTIME);
    Configuration typeRoot = Utils.createSubConfiguration(runtime, getTypeBase(componentType));
    if (typeRoot.existsSubConfiguration(runtimeName)) {
      typeRoot.deleteConfiguration(runtimeName);
    }
    Configuration jarsRoot = Utils.createSubConfiguration(typeRoot, runtimeName);
    HashSet<String> jars = new HashSet<String>();
    if (provider.containsKey("jars")) {
      int count = Integer.parseInt(provider.getProperty("jars"));
      for (int i = 0; i < count; i++) {
        jars.add(provider.getProperty("jar-name_" + i));
      }
    }
    for (String jarName : jars) {
      jarName = jarName.replace('\\', '/');
      if (jarName.charAt(0) == '/') {
        jarName = jarName.substring(1);
      }
      Configuration tmp = jarsRoot;
      int index = jarName.lastIndexOf('/');
      if (index != -1) {
        tmp = Utils.createSubConfiguration(tmp, jarName.substring(0, index));
      }
      Utils.addFileInDB(zipFile, jarName, tmp);
    }
    //upload cfgs
    Configuration workernode = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + CLUSTERNODE_CONFIG + "/" + WORKERNODE);
    Configuration cfg = Utils.createSubConfiguration(workernode, getTypeBase(componentType) + "/" + runtimeName);
    Utils.addFileInDB(zipFile, SERVER + "/" + PROVIDER_XML, cfg);
    //add provider properties
    if (cfg.existsSubConfiguration(PROVIDER)) {
      cfg.deleteConfiguration(PROVIDER);
    }
    Configuration tmp = Utils.createSubConfiguration(cfg, PROVIDER, Configuration.CONFIG_TYPE_PROPERTYSHEET | Configuration.CONFIG_TYPE_FINAL);
    PropertySheet providerPropertySheet = tmp.getPropertySheetInterface();
    providerPropertySheet.createPropertyEntries(provider);
    Configuration descriptors = Utils.createSubConfiguration(cfg, DESCRIPTORS_BASE);
    Utils.addFileInDB(zipFile, SAP_MANIFEST, descriptors);
    HashSet<String> securedFiles = readSecuredFileList(zipFile);
    Utils.addDirectoryInDB(zipFile, SERVER + "/" + DESCRIPTORS_BASE, descriptors, securedFiles);
    if (isService) {
      ZipEntry properties = zipFile.getEntry(SERVER + "/" + PROPERTIES);
      ZipEntry propertiesXML = zipFile.getEntry(SERVER + "/" + PROPERTIES_XML);
      if (propertiesXML != null) {
        Utils.addPropertiesXmlInDB(zipFile, propertiesXML, parser, cfg, PROPERTIES);
        if (properties != null) {
          Utils.warning("ASJ.dpl_off.000009", "SDA [" + zipFile.getName() + "] contains both properties and properties.xml");
        }
      } else {
        if (properties != null) {
          Utils.addPropertiesInDB(zipFile, SERVER + "/" + PROPERTIES, cfg, PROPERTIES, false);
        } else {
          //create empty properties sheet, but delete the old one if such exists
          if (cfg.existsSubConfiguration(PROPERTIES)) {
            cfg.deleteConfiguration(PROPERTIES);
          }
          cfg.createSubConfiguration(PROPERTIES, Configuration.CONFIG_TYPE_PROPERTYSHEET);
        }
      }
      Configuration persistent = Utils.createSubConfiguration(cfg, PERSISTENT_BASE);
      Utils.addDirectoryInDB(zipFile, SERVER + "/" + PERSISTENT_BASE, persistent, securedFiles);
    }
    return provider;
  }

  static void remove(ConfigurationHandler handler, String runtimeName, byte componentType) throws IOException, ConfigurationException, ComponentNotDeployedException {
    Configuration workernode = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + CLUSTERNODE_CONFIG + "/" + WORKERNODE);
    Configuration cfg = Utils.createSubConfiguration(workernode, getTypeBase(componentType));
    if (cfg.existsSubConfiguration(runtimeName)) {
      cfg.deleteConfiguration(runtimeName);
    } else {
      throw new ComponentNotDeployedException("Component [" + runtimeName + "] does not exists");
    }
    Configuration runtime = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN + "/" + RUNTIME);
    cfg = Utils.createSubConfiguration(runtime, getTypeBase(componentType));
    if (cfg.existsSubConfiguration(runtimeName)) {
      cfg.deleteConfiguration(runtimeName);
    }
    NativeDeployer.removeNatives(handler, getTypeBase(componentType), runtimeName);
  }

  static void storeSrcZip(final ConfigurationHandler handler, final ZipFile sda, final String componentName, final String vendor) throws ConfigurationException, IOException, DeploymentException {
    Configuration srcZipRoot = Utils.openConfiguration(handler, SRC_ZIP);
    Configuration vendorConf = Utils.createSubConfiguration(srcZipRoot, vendor);
    Utils.addFileInDB(sda, SRC_ZIP, vendorConf, componentName + ".zip", true);
  }

  static void removeSrcZip(final ConfigurationHandler handler, final String componentName, final String vendor) throws ConfigurationException {
    Configuration srcZipRoot = Utils.openConfiguration(handler, SRC_ZIP);
    Configuration vendorConf = Utils.createSubConfiguration(srcZipRoot, vendor);
    try {
      vendorConf.deleteFile(componentName + ".zip");
    } catch (NameNotFoundException ignored) {
      // $JL-EXC$  No sources to delete on component remove
    }
  }

  /**
   * <!ELEMENT provider-descriptor (display-name, component-name, description?, communication-frame?, application-frame, runtime-editor?, major-version?,
   *                               minor-version?, micro-version?, provider-name, group-name?, provided-interfaces?, references?, jars)>
   * <!ELEMENT display-name (#PCDATA)>
   * <!ELEMENT component-name (#PCDATA)>
   * <!ELEMENT description (#PCDATA)>
   * <!ELEMENT communication-frame (#PCDATA)>
   * <!ELEMENT application-frame (#PCDATA)>
   * <!ELEMENT runtime-editor (#PCDATA)>
   * <!ELEMENT major-version (#PCDATA)>
   * <!ELEMENT minor-version (#PCDATA)>
   * <!ELEMENT micro-version (#PCDATA)>
   * <!ELEMENT provider-name (#PCDATA)>
   * <!ELEMENT group-name (#PCDATA)>
   */
  private static Properties getProviderProperties(ZipFile zipFile, DOMParseHelper parser, boolean isService) throws IOException, SAXException, DeploymentException {
    ZipEntry entry = zipFile.getEntry(SERVER + "/" + PROVIDER_XML);
    if (entry == null) {
      throw new DeploymentException("Required file [" + SERVER + "/" + PROVIDER_XML + "] not found in SDA [" + zipFile.getName() + "]");
    }
    Properties result = new Properties();
    /** todo take versions from SAP_MANIFEST.MF - currently not possible because of issues with portal version service
    String[] versions = Utils.getMajorMinorMicroVersions(zipFile);
    if (versions != null) {
      result.setProperty("major-version", versions[0]);
      result.setProperty("minor-version", versions[1]);
      result.setProperty("micro-version", versions[2]);
    }
    */
    Document document = parser.parse(entry, zipFile);
    Element component = document.getDocumentElement();
    NodeList list = component.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) list.item(i);
        String tagName = element.getTagName();
        if (tagName.equals("display-name")) {
          result.setProperty("display-name", Utils.getTextValue(element));
        } else if (tagName.equals("component-name")) {
          result.setProperty("component-name", Utils.getTextValue(element));
        } else if (tagName.equals("description")) {
          result.setProperty("description", Utils.getTextValue(element));
        //--------read versions from provider xml todo @deprecated
        } else if (tagName.equals("major-version")) {
          result.setProperty("major-version", Utils.getTextValue(element));
        } else if (tagName.equals("minor-version")) {
          result.setProperty("minor-version", Utils.getTextValue(element));
        } else if (tagName.equals("micro-version")) {
          result.setProperty("micro-version", Utils.getTextValue(element));
        //--------read versions from provider xml todo @deprecated
        } else if (tagName.equals("provider-name")) {
          result.setProperty("provider-name", Utils.getTextValue(element));
        } else if (tagName.equals("group-name")) {
          result.setProperty("group-name", Utils.getTextValue(element)); //todo @deprecated
        } else if (tagName.equals("references")) {
          parseReferences(element, result);
        } else if (tagName.equals("jars")) {
          parseJars(element, result);
        } else {
          // start service spesific
          if (isService) {
            if (tagName.equals("application-frame")) {
              result.setProperty("application-frame", Utils.getTextValue(element));
            } else if(tagName.equals("runtime-editor")) {
              result.setProperty("runtime-editor", Utils.getTextValue(element)); //todo @deprecated
            } else if(tagName.equals("provided-interfaces")) {
              getProvidedInterfaces(element, result);
            }
          }
        }
      }
    }
    //validity check - result must contains following keys:
    boolean valid = result.containsKey("component-name");
    valid &= result.containsKey("provider-name");
    if (isService) {
      valid &= result.containsKey("application-frame");
    }
    if (!valid) {
      throw new DeploymentException("Cannot define component because provider.xml is not valid");
    }
    validateName(result.getProperty("provider-name"));
    validateName(result.getProperty("component-name"));
    return result;
  }

  /**
   * <!ELEMENT references (reference*)>
   * <!ELEMENT reference (#PCDATA)>
   * <!ATTLIST reference
   *         type (service|library|interface) #REQUIRED
   *         strength (weak|hard|strong|notify) #REQUIRED
   *         provider-name CDATA #IMPLIED
   * >
   */
  private static void parseReferences(Element references, Properties properties) {
    NodeList referenceList = references.getElementsByTagName("reference");
    int lenght = referenceList.getLength();
    properties.setProperty("references", Integer.toString(lenght));
    for (int i = 0; i < lenght; i++) {
      Element reference = (Element) referenceList.item(i);
      properties.setProperty("reference_name_" + i, Utils.getTextValue(reference));
      properties.setProperty("reference_type_" + i, reference.getAttribute("type").trim());
      properties.setProperty("reference_strength_" + i, reference.getAttribute("strength").trim());
      properties.setProperty("reference_provider-name_" + i, reference.getAttribute("provider-name").trim());
    }
  }

  /**
   * <!ELEMENT jars (jar-name*)>
   * <!ELEMENT jar-name (#PCDATA)>
   */
  private static void parseJars(Element jars, Properties properties) {
    NodeList jarList = jars.getElementsByTagName("jar-name");
    int lenght = jarList.getLength();
    properties.setProperty("jars", Integer.toString(lenght));
    for (int i = 0; i < lenght; i++) {
      Element jar = (Element) jarList.item(i);
      properties.setProperty("jar-name_" + i, Utils.getTextValue(jar));
    }
  }

  /**
   * <!ELEMENT provided-interfaces (interface*)>
   * <!ELEMENT interface (#PCDATA)>
   * <!ATTLIST interface
   *         provider-name CDATA #IMPLIED
   * >
   */
  private static void getProvidedInterfaces(Element providedInterfaces, Properties properties) {
    NodeList interfaces = providedInterfaces.getElementsByTagName("interface");
    int lenght = interfaces.getLength();
    properties.setProperty("provided-interfaces", Integer.toString(lenght));
    for(int i = 0; i < lenght; i++) {
      Element element = (Element) interfaces.item(i);
      properties.setProperty("interface_name_" + i, Utils.getTextValue(element));
      properties.setProperty("interface_provider-name_" + i, element.getAttribute("provider-name").trim());
    }
  }

  private static void validateName(String name) throws DeploymentException {
    for (int i = 0; i < FORBIDDEN_NAME_SYMBOLS.length(); i++) {
      if (name.indexOf(FORBIDDEN_NAME_SYMBOLS.charAt(i)) != -1) {
        throw new DeploymentException("Forbidden symbol [" + FORBIDDEN_NAME_SYMBOLS.charAt(i) + "] found in name [" + name + "]");
      }
    }
  }

  private static String getTypeBase(byte componentType) {
    switch (componentType) {
      case OfflineComponentDeploy.TYPE_INTERFACE : return INTERFACE_BASE;
      case OfflineComponentDeploy.TYPE_LIBRARY : return LIBRARY_BASE;
      case OfflineComponentDeploy.TYPE_SERVICE : return SERVICE_BASE;
      default : return null;
    }
  }

  private static HashSet<String> readSecuredFileList(ZipFile zipFile) throws IOException {
    HashSet<String> result = null;
    ZipEntry secured = zipFile.getEntry(SECURED_FILES);
    if (secured != null) {
      result = new HashSet<String>();
      BufferedReader in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(secured)));
      try {
        String line;
        while ((line = in.readLine()) != null) {
          line = line.trim();
          line = line.replace('\\', '/');
          if (line.charAt(0) == '/') {
            line = line.substring(1);
          }
          if (!line.equals("")) {
            result.add(line);
          }
        }
      } finally {
        in.close();
      }
    }
    return result;
  }

}