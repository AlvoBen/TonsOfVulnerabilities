package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertyEntryMetaData;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.sap.engine.tools.offlinedeploy.rdb.OfflineComponentDeployImpl.loc;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Utils provides various methods used from all deployers.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class Utils {

  /**
   * If component names starts with SAP provider (engine.sap.com or sap.com) the provider is removed
   * and all '/' are replaced with '~'
   *
   * @param componentName
   * @return modified component name
   */
  static String modifyComponentName(String componentName) {
    for (String aSAP_PROVIDERS : OfflineComponentDeploy.SAP_PROVIDERS) {
      if (componentName.startsWith(aSAP_PROVIDERS + "/")) {
        componentName = componentName.substring(aSAP_PROVIDERS.length() + 1);
        break;
      }
    }
    componentName = componentName.replace('/', '~');
    return componentName;
  }

  /**
   * If component names starts with SAP provider (engine.sap.com or sap.com) the provider is removed
   * and all '/' are replaced with '~' and if providerName is not SAP provider it is set as prefix
   * concatenated with '~'.
   *
   * @param componentName
   * @param providerName
   * @return modified component name
   */
  static String modifyComponentName(String componentName, String providerName) {
    componentName = modifyComponentName(componentName);
    if (providerName == null || providerName.equals("")) {
      providerName = OfflineComponentDeploy.SAP_PROVIDERS[0];
    }
    if (!isSapProvider(providerName)) {
      providerName = providerName.replace('/', '~');
      componentName = providerName + "~" + componentName;
    }
    return componentName;
  }

  /**
   * Returns true if provider is 'engine.sap.com' or 'sap.com'
   *
   * @param providerName
   * @return true if providerName is SAP provider
   */
  static boolean isSapProvider(String providerName) {
    for (String aSAP_PROVIDERS : OfflineComponentDeploy.SAP_PROVIDERS) {
      if (providerName.equals(aSAP_PROVIDERS)) return true;
    }
    return false;
  }

  /**
   * Reads keyvendor and keyname from sap manifest file.
   *
   * @param zipFile - SDA archive.
   * @return string array - String[] {keyvendor, keyname}.
   * @throws IOException - if any I/O errors ocurr.
   * @throws DeploymentException - if sap manifest or names are missing.
   */
  static String[] getKeyVendorKeyName(ZipFile zipFile) throws IOException, DeploymentException {
    ZipEntry entry = zipFile.getEntry(Constants.SAP_MANIFEST);
    if (entry == null) {
      throw new DeploymentException("Required file [" + Constants.SAP_MANIFEST + "] not found in SDA [" + zipFile.getName() + "]");
    }
    InputStream is = zipFile.getInputStream(entry);
    try {
      Manifest manifest = new Manifest(is);
      String[] result = new String[2];
      Attributes attributes = manifest.getMainAttributes();
      result[0] = attributes.getValue("keyvendor");
      if (result[0] != null) {
        result[0] = result[0].trim();
      }
      result[1] = attributes.getValue("keyname");
      if (result[1] != null) {
        result[1] = result[1].trim();
      }
      if (result[0] == null || result[1] == null) {
        throw new DeploymentException("Attributes keyname or keyvendor from META-INF/SAP_MANIFEST.MF not defined in template SDA [" + zipFile.getName() + "]");
      }
      return result;
    } finally {
      is.close();
    }
  }

  /**
   * Reads versions from sap manifest file.
   *
   * @param zipFile - SDA archive.
   * @return string array - String[] {major, minor, micro} or null if not exist.
   * @throws IOException - if any I/O errors ocurr.
   * @throws DeploymentException - if sap manifest is missing.
   */
  static String[] getMajorMinorMicroVersions(ZipFile zipFile) throws IOException, DeploymentException {
    ZipEntry entry = zipFile.getEntry(Constants.SAP_MANIFEST);
    if (entry == null) {
      throw new DeploymentException("Required file [" + Constants.SAP_MANIFEST + "] not found in SDA [" + zipFile.getName() + "]");
    }
    InputStream is = zipFile.getInputStream(entry);
    try {
      Manifest manifest = new Manifest(is);
      Attributes attributes = manifest.getMainAttributes();
      String keycounter = attributes.getValue("keycounter");
      if (keycounter != null) {
        String[] result = new String[3];
        keycounter = keycounter.trim();
        int index1 = keycounter.indexOf('.');
        if (index1 != -1) {
          result[0] = keycounter.substring(0, index1);
          int index2 = keycounter.indexOf('.', index1 + 1);
          if (index2 != -1) {
            result[1] = keycounter.substring(index1 + 1, Math.min(index1 + 3, index2));
            int index3 = keycounter.indexOf('.', index2 + 1);
            if (index3 != -1) {
              result[2] = keycounter.substring(index2 + 1, index3);
              return result;
            }
          }
        }
      }
      return null;
    } finally {
      is.close();
    }
  }

  /**
   * Reads csncomponent from sap manifest file.
   *
   * @param zipFile - SDA archive.
   * @return csncomponent value or null if not exist.
   * @throws IOException - if any I/O errors ocurr.
   * @throws DeploymentException - if sap manifest is missing.
   */
  static String getCSNComponent(ZipFile zipFile) throws IOException, DeploymentException {
    ZipEntry entry = zipFile.getEntry(Constants.SAP_MANIFEST);
    if (entry == null) {
      throw new DeploymentException("Required file [" + Constants.SAP_MANIFEST + "] not found in SDA [" + zipFile.getName() + "]");
    }
    InputStream is = zipFile.getInputStream(entry);
    try {
      Manifest manifest = new Manifest(is);
      Attributes attributes = manifest.getMainAttributes();
      String csncomponent = attributes.getValue("csncomponent");
      if (csncomponent != null) {
        csncomponent = csncomponent.trim();
      }
      return csncomponent;
    } finally {
      is.close();
    }
  }

  /**
   * Gets first child text node value.
   *
   * @param element
   * @return first child text value or <code>null</code> if child does not exist.
   */
  static String getTextValue(Element element) {
    String result = "";
    Node node = element.getFirstChild();
    if ((node != null) && (node.getNodeType() == Node.TEXT_NODE)) {
      result = node.getNodeValue();
      if (result != null) {
        result = result.trim();
      }
    }
    return result;
  }

  /**
   * Same as <code>print(severity, <b>null</b>, msgId, message)</code>.
   * @see #print(int, Throwable, String, String)
   */
  static void print(int severity, String msgId, String message) {
    print(severity, null, msgId, message);
  }

  /**
   * Prints the message and the stacktrace, if any, to <code>System.out</code> and to
   * trace file using <code>SimpleLogger</code>.
   * <p>If the severity is <code>WARNING</code>, the message is also added in
   * <code>OfflineComponentDeployImpl.warningList</code>.</p>
   *
   * @param severity  message severity, as per <code>com.sap.tc.logging.Severity</code>
   * @param t  print its stacktrace, ignore if <code>null</code>
   * @param msgId   trace ID for this message (<code>ASJ.dpl_off.XXXXXX</code>)
   * @param message  plain text
   * @see com.sap.tc.logging.Severity
   */
  static void print(int severity, Throwable t, String msgId, String message) {
    //$JL-SYS_OUT_ERR$

    // Print the message and all args, if any, to System.out
    System.out.println(message);

    // Trace the message and all args, if any, in SimpleLogger
    // Also print the Throwable, if any, to System.out and SimpleLogger
    if (t == null) {
      SimpleLogger.trace(severity, loc, msgId, message);
    } else {
      t.printStackTrace(System.out);
      SimpleLogger.traceThrowable(severity, loc, t, msgId, message);
    }

    // Add warning to warning list
    if (severity == Severity.WARNING) {
      OfflineComponentDeployImpl.warningList.add(message);
    }
  }

  static void info(String msgId, String message) {
    print(Severity.INFO, null, msgId, message);
  }

  static void warning(String msgId, String message) {
    warning(null, msgId, message);
  }

  static void warning(Throwable t, String msgId, String message) {
    print(Severity.WARNING, t, msgId, message);
  }

  /**
   * Creates standard sub configuration or returns the existing one
   *
   * @param root - root cfg
   * @param path - relative path
   * @return requested sub configuration
   * @throws ConfigurationException if error occurs
   */
  static Configuration createSubConfiguration(Configuration root, String path) throws ConfigurationException {
    return createSubConfiguration(root, path, Configuration.CONFIG_TYPE_STANDARD);
  }

  /**
   * Creates sub configuration or returns the existing one
   *
   * @param root - root cfg
   * @param path - relative path
   * @param type - configuration type see Configuration.CONFIG_TYPE_*
   * @return requested sub configuration
   * @throws ConfigurationException if error occurs
   */
  static Configuration createSubConfiguration(Configuration root, String path, int type) throws ConfigurationException {
    Configuration cfg = root;
    if (path.indexOf('/') != -1) {
      StringTokenizer tokenizer = new StringTokenizer(path, "/");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        if (!cfg.existsSubConfiguration(token)) {
          cfg = cfg.createSubConfiguration(token, type);
        } else {
          cfg = cfg.getSubConfiguration(token);
        }
      }
    } else {
      if (!cfg.existsSubConfiguration(path)) {
        cfg = cfg.createSubConfiguration(path, type);
      } else {
        cfg = cfg.getSubConfiguration(path);
      }
    }
    return cfg;
  }

  /**
   * Opens configuration with write access. In online case if the configuration is locked it tries
   * to obtain the lock for 3 minutes.
   *
   * @param handler
   * @param configurationName
   * @return requested configuration
   * @throws ConfigurationException
   */
  static Configuration openConfiguration(ConfigurationHandler handler, String configurationName) throws ConfigurationException {
    if (OfflineComponentDeployImpl.isOffline) {
      return handler.openConfiguration(configurationName, ConfigurationHandler.WRITE_ACCESS, true);
    } else {
      int count = 1800;
      while (true) {
        try {
          return handler.openConfiguration(configurationName, ConfigurationHandler.WRITE_ACCESS, true);
        } catch (ConfigurationLockedException cle) {
          try {
            Thread.sleep(100);
          } catch(InterruptedException ie) {
            // $JL-EXC$
          }
          if (count-- == 0) {
            throw cle;
          }
        }
      }
    }
  }

  /**
   * Upload file in DB.
   *
   * @param zipFile - SDA archive
   * @param zipEntryName - zip entry representing the file
   * @param cfg - configuration root
   * @throws IOException
   * @throws ConfigurationException
   * @throws DeploymentException
   */
  static void addFileInDB(ZipFile zipFile, String zipEntryName, Configuration cfg) throws IOException, ConfigurationException, DeploymentException {
    String fileName = zipEntryName;
    if (zipEntryName.indexOf('/') != -1) {
      fileName = zipEntryName.substring(zipEntryName.lastIndexOf('/') + 1);
    }
    addFileInDB(zipFile, zipEntryName, cfg, fileName, true, false, false);
  }

  /**
   * Upload file in DB.
   *
   * @param zipFile - SDA archive
   * @param zipEntryName - zip entry representing the file
   * @param cfg - configuration root
   * @param fileName - the name of the file entry
   * @param ignorIfMissing - if true no warning is logged
   * @throws IOException
   * @throws ConfigurationException
   * @throws DeploymentException
   */
  static void addFileInDB(ZipFile zipFile, String zipEntryName, Configuration cfg, String fileName, boolean ignorIfMissing) throws IOException, ConfigurationException, DeploymentException {
    addFileInDB(zipFile, zipEntryName, cfg, fileName, false, false, ignorIfMissing);
  }

  private static void addFileInDB(ZipFile zipFile, String zipEntryName, Configuration cfg, String fileName, boolean require, boolean secured, boolean ignorIfMissing) throws IOException, ConfigurationException, DeploymentException {
    ZipEntry zipEntry = zipFile.getEntry(zipEntryName);
    if (zipEntry != null) {
      InputStream in = zipFile.getInputStream(zipEntry);
      if (!secured) {
        cfg.updateFileAsStream(fileName, in, true);
      } else {
        if (cfg.existsFile(fileName)) {
          cfg.updateFileAsStream(fileName, in, Configuration.ENTRY_TYPE_SECURE);
        } else {
          cfg.addFileAsStream(fileName, in, Configuration.ENTRY_TYPE_SECURE);
        }
      }
    } else {
      if (require) {
        throw new DeploymentException("Required file [" + zipEntryName + "] not found in SDA [" + zipFile.getName() + "]");
      } else {
        if (!ignorIfMissing) {
          String message = "File [" + zipEntryName + "] not found in SDA [" + zipFile.getName() + "]";
          warning("ASJ.dpl_off.000015", message);
        }
      }
    }
  }

  /**
   * Upload directory in DB.
   *
   * @param zipFile - SDA archive
   * @param dirName - name of the directory
   * @param root - configuration root
   * @param securedFiles - set containing secured files
   * @throws IOException
   * @throws ConfigurationException
   * @throws DeploymentException
   */
  static void addDirectoryInDB(ZipFile zipFile, String dirName, Configuration root, Set securedFiles) throws IOException, ConfigurationException, DeploymentException {
    Enumeration en = zipFile.entries();
    while (en.hasMoreElements()) {
      ZipEntry ze = (ZipEntry) en.nextElement();
      String zipEntryName = ze.getName();
      if (zipEntryName.startsWith(dirName)) {
        if (!zipEntryName.endsWith("/")) {
          int index = zipEntryName.lastIndexOf('/');
          String fileName = zipEntryName.substring(index + 1);
          String pathName = zipEntryName.substring(dirName.length(), index);
          boolean isSecured = false;
          if (securedFiles != null) {
            isSecured = securedFiles.contains(zipEntryName) || securedFiles.contains(zipEntryName.substring(dirName.length() + 1));
          }
          Configuration cfg = pathName.equals("") ? root : createSubConfiguration(root, pathName);
          addFileInDB(zipFile, zipEntryName, cfg, fileName, false, isSecured, false);
        }
      }
    }
  }

  /**
   * Uploads properties.xml into DB.
   *
   * @param zipFile - SDA archive
   * @param zipEntry - zip entry representing properties.xml
   * @param parser - XML parser
   * @param cfg - configuration root
   * @param propertiesName - name of the properties configuration.
   * @throws IOException
   * @throws SAXException
   * @throws ConfigurationException
   */
  static void addPropertiesXmlInDB(ZipFile zipFile, ZipEntry zipEntry, DOMParseHelper parser, Configuration cfg, String propertiesName) throws IOException, SAXException, ConfigurationException {
    Document document = parser.parse(zipEntry, zipFile);
    Configuration props;
    if (cfg.existsSubConfiguration(propertiesName)) {
      cfg.deleteConfiguration(propertiesName);
    }
    props = cfg.createSubConfiguration(propertiesName, Configuration.CONFIG_TYPE_PROPERTYSHEET);
    Element baseElement = document.getDocumentElement();
    baseElement.normalize();
    processPropertyElement(props, baseElement);
  }

  private static void processPropertyElement(Configuration root, Element elenemt) throws ConfigurationException {
    NodeList list = elenemt.getChildNodes();
    Element element;
    String tag;
    PropertySheet ps = root.getPropertySheetInterface();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        element = (Element) list.item(i);
        tag = element.getTagName();
        if (tag.equals("property")) {
          String name = element.getAttribute("name");
          String value = element.getAttribute("value");
          int typeMask = 0;
          //make property type mask
          if (element.hasAttribute("secure") && element.getAttribute("secure").equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_SECURE;
          if (element.hasAttribute("parameterized") && element.getAttribute("parameterized").equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_PARAMETERIZED;
          if (element.hasAttribute("contains_link") && element.getAttribute("contains_link").equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_CONTAINS_LINK;
          if (element.hasAttribute("computed") && element.getAttribute("computed").equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_COMPUTED;
          if (element.hasAttribute("final") && element.getAttribute("final").equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_FINAL;
          if (element.hasAttribute("disabled") && element.getAttribute("disabled").equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_DISABLED;
          if (element.hasAttribute("onlinemodifiable") && element.getAttribute("onlinemodifiable").equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_ONLINE_MODIFIABLE;
          String description = element.getAttribute("description");
          String shortDescription = element.getAttribute("short_description");
          byte dataTypeByte = PropertyEntry.DATA_TYPE_ANY;
          if (element.hasAttribute("type")) {
            String dataType = element.getAttribute("type");
            if (dataType.equals("ANY")) {
              dataTypeByte = PropertyEntry.DATA_TYPE_ANY;
            } else if (dataType.equals("STRING")) {
              dataTypeByte = PropertyEntry.DATA_TYPE_STRING;
            } else if (dataType.equals("LONG")) {
              dataTypeByte = PropertyEntry.DATA_TYPE_LONG;
            } else if (dataType.equals("DOUBLE")) {
              dataTypeByte = PropertyEntry.DATA_TYPE_DOUBLE;
            } else if (dataType.equals("BOOLEAN")) {
              dataTypeByte = PropertyEntry.DATA_TYPE_BOOLEAN;
            }
          }
          String range = PropertyEntry.RANGE_ANY;
          if (element.hasAttribute("range")) {
            range = element.getAttribute("range");
          }
          byte visibilityByte = PropertyEntry.VISIBILITY_ANY;
          if (element.hasAttribute("visibility")) {
            String visibility = element.getAttribute("visibility");
            if (visibility.equals("ANY")) {
              visibilityByte = PropertyEntry.VISIBILITY_ANY;
            } else if (visibility.equals("EXPERT")) {
              visibilityByte = PropertyEntry.VISIBILITY_EXPERT;
            } else if (visibility.equals("NOVICE")) {
              visibilityByte = PropertyEntry.VISIBILITY_NOVICE;
            }
          }
          PropertyEntryMetaData metaData = new PropertyEntryMetaData(typeMask, dataTypeByte, visibilityByte, range);
          ps.createPropertyEntry(name, value, description, shortDescription, metaData);
        } else if (tag.equals("nested_property")) {
          String name = element.getAttribute("name");
          Configuration nested;
          if (root.existsSubConfiguration(name)) {
            nested = root.getSubConfiguration(name);
            PropertySheet propSheet = nested.getPropertySheetInterface();
            propSheet.deleteAllPropertyEntries();
          } else {
            nested = root.createSubConfiguration(name, Configuration.CONFIG_TYPE_PROPERTYSHEET);
          }
          processPropertyElement(nested, element);
        }
      }
    }
  }

  /**
   * Upload properties file in DB.
   *
   * @param zipFile - SDA archive
   * @param zipEntryName - zip entry representing the properties
   * @param cfg - configuration root
   * @param subConfigName - the name of the property sub configuration
   * @throws IOException
   * @throws ConfigurationException
   * @throws DeploymentException
   */
  static void addPropertiesInDB(ZipFile zipFile, String zipEntryName, Configuration cfg, String subConfigName) throws IOException, ConfigurationException, DeploymentException {
    addPropertiesInDB(zipFile, zipEntryName, cfg, subConfigName, true, false);
  }

  /**
   * Upload properties file in DB.
   *
   * @param zipFile - SDA archive
   * @param zipEntryName - zip entry representing the properties
   * @param cfg - configuration root
   * @param subConfigName - the name of the property sub configuration
   * @param ignorIfMissing - if true no warning is logged
   * @throws IOException
   * @throws ConfigurationException
   * @throws DeploymentException
   */
  static void addPropertiesInDB(ZipFile zipFile, String zipEntryName, Configuration cfg, String subConfigName, boolean ignorIfMissing) throws IOException, ConfigurationException, DeploymentException{
    addPropertiesInDB(zipFile, zipEntryName, cfg, subConfigName, false, ignorIfMissing);
  }

  private static void addPropertiesInDB(ZipFile zipFile, String zipEntryName, Configuration cfg, String subConfigName, boolean require, boolean ignorIfMissing) throws IOException, ConfigurationException, DeploymentException {
    Configuration subCfg;
    PropertySheet propSheet;
    ZipEntry zipEntry = zipFile.getEntry(zipEntryName);
    if (zipEntry != null) {
      // decompress the file and read it in memory
      InputStream is = zipFile.getInputStream(zipEntry);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[512];
      int r;
      while ((r = is.read(buffer)) != -1) {
        baos.write(buffer, 0, r);
      }
      baos.close();
      is.close();
      //complete properties file, including the descriptive comments, is stored in this byte[]
      byte[] propertyFileByteArray = baos.toByteArray();
      //the properties will NOT be updated anymore -> delete entries if exists
      if (cfg.existsSubConfiguration(subConfigName)) {
        //delete all entries instead of delete configuration because of issue with linked properties deploy
        subCfg = cfg.getSubConfiguration(subConfigName);
        propSheet = subCfg.getPropertySheetInterface();
        propSheet.deleteAllPropertyEntries();
        propSheet.updatePropertyEntries(propertyFileByteArray);
      } else {
        subCfg = cfg.createSubConfiguration(subConfigName, Configuration.CONFIG_TYPE_PROPERTYSHEET);
        propSheet = subCfg.getPropertySheetInterface();
        propSheet.createPropertyEntries(propertyFileByteArray);
      }
    } else {
      if (require) {
        throw new DeploymentException("Required file [" + zipEntryName + "] not found in SDA [" + zipFile.getName() + "]");
      } else {
        if (!ignorIfMissing) {
          String message = "File [" + zipEntryName + "] not find in SDA [" + zipFile.getName() + "]";
          warning("ASJ.dpl_off.000016", message);
        }
      }
    }
  }

  /**
   * Stores properties object as file. This method is used for storing template mapping and undeploy mapping.
   */
  static void storePropertiesAsFile(Configuration cfg, Properties properties, String fileName) throws IOException, ConfigurationException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    properties.store(baos, "mapping list");
    baos.close();
    cfg.updateFileAsStream(fileName, new ByteArrayInputStream(baos.toByteArray()), true);
  }

  /**
   * Creates properties object from file. This method is used for loading template mapping and undeploy mapping.
   */
  static Properties loadPropertiesFromDB(Configuration cfg, String fileName) throws IOException, ConfigurationException {
    Properties result = new Properties();
    if (cfg.existsFile(fileName)) {
      InputStream is = cfg.getFile(fileName);
      try {
        result.load(is);
      } finally {
        is.close();
      }
    }
    return result;
  }

  /**
   * Loads Service Container Repository.
   */
  static SCRepository loadServiceRepository(Configuration cfg) throws IOException, ClassNotFoundException, ConfigurationException, DeploymentException {
    SCRepository result;
    if (cfg.existsFile(Constants.SCREPOSITORY_FILE)) {
      InputStream is = cfg.getFile(Constants.SCREPOSITORY_FILE);
      try {
        ObjectInputStream objectStream = new ObjectInputStream(is);
        result = (SCRepository) objectStream.readObject();
      } finally {
        is.close();
      }
    } else {
      //regeneration mechanism if file doesn't exist
      result = SCRepositoryRegenerator.regenerate(cfg);
    }
    return result;
  }

  /**
   * Stores Service Container Repository.
   */
  static void storeServiceRepository(Configuration cfg, SCRepository serviceRepository) throws IOException, ConfigurationException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oas = new ObjectOutputStream(baos);
    oas.writeObject(serviceRepository);
    oas.close();
    cfg.updateFileAsStream(Constants.SCREPOSITORY_FILE, new ByteArrayInputStream(baos.toByteArray()), true);
  }

}