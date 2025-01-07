package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tc.logging.Severity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.sap.engine.tools.offlinedeploy.rdb.OfflineComponentDeployImpl.loc;

/**
 * Deploy or remove components native parts.
 *
 * @version 710
 * @author Dimitar Kostadinov
 */
public class NativeDeployer implements Constants {

  /**
   * Deploys component native parts.
   *
   * @param handler - current transaction.
   * @param zipFile - SDA archive.
   * @param natives - SearchRules XML document.
   * @param typeString - string representation of component (interfaces, ext, services).
   * @param runtimeName - component name.
   * @throws IOException - if any I/O error occur.
   * @throws ConfigurationException - if any DB error occur.
   */
  static void uploadNatives(ConfigurationHandler handler, ZipFile zipFile, Document natives, String typeString, String runtimeName) throws IOException, ConfigurationException {
    Configuration undeployRoot = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
    Configuration undeploy = Utils.createSubConfiguration(undeployRoot, typeString);
    Configuration binRoot = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN);
    Configuration root = Utils.createSubConfiguration(binRoot, NATIVE);
    HashMap<String, String[]> nativeDescriptor;
    if (OfflineComponentDeployImpl.autoCommit) {
      nativeDescriptor = readNativeDescriptor(root);
    } else {
      nativeDescriptor = OfflineComponentDeployImpl.commonData.nativeDescriptorMap;
      if (nativeDescriptor == null) {
        nativeDescriptor = readNativeDescriptor(root);
        OfflineComponentDeployImpl.commonData.nativeDescriptorMap = nativeDescriptor;
      }
    }

    HashSet<String> oldUndeployList = readUndeployList(undeploy, runtimeName);

    //decrease ussage of old natives and delete them if ussages = 0;
    boolean hasDeletedFiles = false;
    for (String key : oldUndeployList) {
      String[] parsedLine = nativeDescriptor.get(key);
      //key may not exist in native descriptor because of issue with obsolete migration controller
      if (parsedLine != null) {
        int num = Integer.parseInt(parsedLine[3]) - 1;
        parsedLine[3] = Integer.toString(num);
        if (num == 0) {
          if (root.existsFile(parsedLine[4])) {
            root.deleteFile(parsedLine[4]);//detete!
          }
          parsedLine[4] = "<deleted>";//mark!
          hasDeletedFiles = true;
        } else {
          // else another component also use this native -> remains
        }
      }
    }

    // Parse the new search rules xml; create new undeploy list
    HashSet<String> undeployList = new HashSet<String>();
    //add natives
    HashMap<String, ZipEntry> nameToZipEntry = parseSearchRulesXML(zipFile, natives, nativeDescriptor, undeployList);
    for (String key : nameToZipEntry.keySet()) {
      ZipEntry entry = nameToZipEntry.get(key);
      root.updateFileAsStream(key, zipFile.getInputStream(entry), true);
    }

    // Update the native.descriptor, if there is any change in natives
    if (hasDeletedFiles || !nameToZipEntry.isEmpty()) {
      //store native descriptor, undeploy info and update os lib version
      if (OfflineComponentDeployImpl.autoCommit) {
        storeNativeDescriptor(nativeDescriptor, root);
      } else {
        // else the file will be stored on commit
      }

      // Save the undeploy list even if it is empty, to indicate empty search xml
      storeUndeployList(undeployList, undeploy, runtimeName);

      int osLibVersion;
      if (binRoot.existsConfigEntry(OS_LIB_VERSION)) {
        osLibVersion = (Integer) binRoot.getConfigEntry(OS_LIB_VERSION) + 1;
      } else {
        osLibVersion = 1;
      }
      binRoot.modifyConfigEntry(OS_LIB_VERSION, osLibVersion, true);
    }
  }

  /**
   * Removes component native parts.
   *
   * @param handler - current transaction.
   * @param typeString - string representation of component (interfaces, ext, services).
   * @param runtimeName - component name.
   * @throws IOException - if any I/O error occur.
   * @throws ConfigurationException - if any DB error occur.
   */
  static void removeNatives(ConfigurationHandler handler, String typeString, String runtimeName) throws IOException, ConfigurationException {
    Configuration undeployRoot = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + UNDEPLOY);
    Configuration undeploy = Utils.createSubConfiguration(undeployRoot, typeString);
    if (undeploy.existsFile(runtimeName + "_native_undeploy_info")) {
      HashSet<String> undeployList = readUndeployList(undeploy, runtimeName);
      if (undeployList.size() > 0) {
        Configuration binRoot = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN);
        Configuration root = Utils.createSubConfiguration(binRoot, NATIVE);
        HashMap<String, String[]> nativeDescriptor;
        if (OfflineComponentDeployImpl.autoCommit) {
          nativeDescriptor = readNativeDescriptor(root);
        } else {
          nativeDescriptor = OfflineComponentDeployImpl.commonData.nativeDescriptorMap;
          if (nativeDescriptor == null) {
            nativeDescriptor = readNativeDescriptor(root);
            OfflineComponentDeployImpl.commonData.nativeDescriptorMap = nativeDescriptor;
          }
        }
        for (String key : undeployList) {
          String[] parsedLine = nativeDescriptor.get(key);
          if (parsedLine != null) {
            int num = Integer.parseInt(parsedLine[3]);
            if (num == 0) {
              // This situation is not possible because the natives was used till current undeploy,  bootstrap must remove all '0' used file from os_libs directory!
              SimpleLogger.trace(Severity.DEBUG, loc, "ASJ.dpl_off.000012", "Cannot remove native [row = {0}, {1}, {2}, {3}, {4}, {5}], because of 0 usages", new Object[] {parsedLine[0], parsedLine[1], parsedLine[2], parsedLine[3], parsedLine[4], parsedLine[5]});
            } else if (num == 1) {
              // if current usages = 1 set usages to '0' and delete native file if it is not already exist
              parsedLine[3] = "0";
              if (root.existsFile(parsedLine[4])) {
                root.deleteFile(parsedLine[4]);
              }
              parsedLine[4] = "<deleted>";
            } else {
              // if usages > 1 decrease counter with '1'
              parsedLine[3] = Integer.toString(num - 1);
            }
          }
        }
        if (OfflineComponentDeployImpl.autoCommit) {
          storeNativeDescriptor(nativeDescriptor, root);
        } else {
          // else the file will be stored on commit
        }
        int osLibVersion;
        if (binRoot.existsConfigEntry(OS_LIB_VERSION)) {
          osLibVersion = (Integer) binRoot.getConfigEntry(OS_LIB_VERSION) + 1;
        } else {
          osLibVersion = 1;
        }
        binRoot.modifyConfigEntry(OS_LIB_VERSION, osLibVersion, true);
      }
      undeploy.deleteFile(runtimeName + "_native_undeploy_info");
    }
  }

  /**
   * Stores native.descriptor in DB.
   */
  static void storeNativeDescriptor(HashMap<String, String[]> map, Configuration cfg) throws ConfigurationException {
    StringBuilder buffer = new StringBuilder();
    for (String[] parsedLine : map.values()) {
      buffer.append(parsedLine[0]);
      buffer.append(',');
      buffer.append(parsedLine[1]);
      buffer.append(',');
      buffer.append(parsedLine[2]);
      buffer.append(',');
      buffer.append(parsedLine[3]);
      buffer.append(',');
      buffer.append(parsedLine[4]);
      buffer.append(',');
      buffer.append(parsedLine[5]);
      buffer.append('\n');
    }
    buffer.deleteCharAt(buffer.length() - 1);
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
    cfg.updateFileAsStream(NATIVE_DESCRIPTOR, bais, true);
  }

  /**
   * Parse SearchRules XML and returns a map save name to zip entry.
   *
   * <!ELEMENT native-parts (native-part+)>
   * <!ELEMENT native-part (path+)>
   * <!ELEMENT path (#PCDATA)>
   * <!ATTLIST path
   *      unicode (true|false) #IMPLIED
   *      platform (#PCDATA) #REQUIRED
   *      jvm-bitlength (32|64) #IMPLIED
   *      supported (true|false) #REQUIRED
   * >
   *
   * @param zipFile - SDA archive.
   * @param natives - SearchRules XML document.
   * @param nativeDescriptor - native descriptor.
   * @param undeployList - undeploy info list.
   * @return save name to zip entry mapping.
   */
  private static HashMap<String, ZipEntry> parseSearchRulesXML(ZipFile zipFile, Document natives, HashMap<String, String[]> nativeDescriptor, HashSet<String> undeployList) {
    HashMap<String, ZipEntry> result = new HashMap<String, ZipEntry>();
    Element nativePartrs = natives.getDocumentElement();
    NodeList nativeParts = nativePartrs.getElementsByTagName("native-part");
    long currentTime = System.currentTimeMillis();
    //zip entry path to save name mapping
    HashMap<String, String> entryPathToSavedName = new HashMap<String, String>();
    for (int i = 0; i < nativeParts.getLength(); i++) {
      Element nativePart = (Element) nativeParts.item(i);
      NodeList paths = nativePart.getElementsByTagName("path");
      for (int j = 0; j < paths.getLength(); j++) {
        Element path = (Element) paths.item(j);
        String entryPath = Utils.getTextValue(path);
        if (entryPath != null && path.hasAttribute("supported") && path.hasAttribute("platform")) {
          if (path.getAttribute("supported").trim().equals("true")) {
            String platform = path.getAttribute("platform").trim();
            String unicode = path.getAttribute("unicode").trim();
            String jvm_bitlength = path.getAttribute("jvm-bitlength").trim();
            entryPath = entryPath.replace('\\', '/');
            if (entryPath.charAt(0) == '/') {
              entryPath = entryPath.substring(1);
            }
            ZipEntry nativeLib = zipFile.getEntry(entryPath);
            if (nativeLib == null && !entryPath.startsWith(OS_LIBS)) {
              //add OS_libs prefix
              entryPath = OS_LIBS + entryPath;
              nativeLib = zipFile.getEntry(entryPath);
            }
            if (nativeLib != null) {
              String realFileName = entryPath.substring(entryPath.lastIndexOf('/') + 1);
              String saveFileName;
              if (entryPathToSavedName.containsKey(entryPath)) {
                saveFileName = entryPathToSavedName.get(entryPath);
              } else {
                saveFileName = currentTime++ + "_" + realFileName;
                entryPathToSavedName.put(entryPath, saveFileName);
              }
              if (unicode.equals("") && jvm_bitlength.equals("")) {
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), "32", "nuc", saveFileName, realFileName);
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), "32", "uc", saveFileName, realFileName);
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), "64", "nuc", saveFileName, realFileName);
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), "64", "uc", saveFileName, realFileName);
              } else if (unicode.equals("")) {
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), jvm_bitlength.toLowerCase(), "nuc", saveFileName, realFileName);
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), jvm_bitlength.toLowerCase(), "uc", saveFileName, realFileName);
              } else if (jvm_bitlength.equals("")) {
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), "32", unicode.toLowerCase(), saveFileName, realFileName);
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), "64", unicode.toLowerCase(), saveFileName, realFileName);
              } else {
                updateLine(nativeDescriptor, undeployList, platform.toLowerCase(), jvm_bitlength.toLowerCase(), unicode.toLowerCase(), saveFileName, realFileName);
              }
              result.put(saveFileName, nativeLib);
            } else {
              Utils.warning("ASJ.dpl_off.000013", "File [" + entryPath + "], declared in SearchRules.xml, not found in [" + zipFile.getName() + "]");
            }
          }
        } else {
          Utils.warning("ASJ.dpl_off.000014", "File [" + entryPath + "], declared in SearchRules.xml, not found in [" + zipFile.getName() + "]");
        }
      }
    }
    return result;
  }

  /**
   * Reads native descriptor data in HashMap where the key is "platform,bitlength,unicode,fileName" and value is
   * String[] {platform, bitlength, unicode, ussages, storeName, fileName};
   *
   * @param cfg - native.descriptor configuration.
   * @return native hash map.
   * @throws IOException - if any I/O error occur.
   * @throws ConfigurationException - if any DB error occur.
   */
  private static HashMap<String, String[]> readNativeDescriptor(Configuration cfg) throws IOException, ConfigurationException {
    HashMap<String, String[]> result = new HashMap<String, String[]>();
    if (cfg.existsFile(NATIVE_DESCRIPTOR)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(cfg.getFile(NATIVE_DESCRIPTOR)));
      try {
        String line;
        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (!line.equals("")) {
            StringTokenizer token = new StringTokenizer(line, ",");
            if (token.countTokens() == 6) {
              String[] parsedLine = new String[6];
              for (int i = 0; i < 6; i++) {
                parsedLine[i] = token.nextToken().trim();
              }
              String key = parsedLine[0] + "," + parsedLine[1] + "," + parsedLine[2] + "," + parsedLine[5];
              result.put(key, parsedLine);
            }
          }
        }
      } finally {
        reader.close();
      }
    }
    return result;
  }

  /**
   * Reads component undeploy info as a set containing keys "platform,bitlength,unicode,fileName" in native descriptor.
   *
   * @param cfg - undeploy info configuration.
   * @param runtimeName - component runtime name.
   * @return undeploy info set.
   * @throws IOException - if any I/O error occur.
   * @throws ConfigurationException - if any DB error occur.
   */
  private static HashSet<String> readUndeployList(Configuration cfg, String runtimeName) throws IOException, ConfigurationException {
    //contains keys from native descriptor
    String undeployDescriptor = runtimeName + "_native_undeploy_info";
    HashSet<String> result = new HashSet<String>();
    if (cfg.existsFile(undeployDescriptor)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(cfg.getFile(undeployDescriptor)));
      try {
        String line;
        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (!line.equals("")) {
            result.add(line);
          }
        }
      } finally {
        reader.close();
      }
    }
    return result;
  }

  /**
   * Stores undeploy info for component in DB.
   */
  private static void storeUndeployList(HashSet<String> set, Configuration cfg, String runtimeName) throws ConfigurationException {
    StringBuilder buffer = new StringBuilder();
    for (String line : set) {
      buffer.append(line);
      buffer.append('\n');
    }
    buffer.deleteCharAt(buffer.length() - 1);
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
    cfg.updateFileAsStream(runtimeName + "_native_undeploy_info", bais, true);
  }

  /**
   * Updates or create line in native.descriptor.
   */
  private static void updateLine(HashMap<String, String[]> nativeDescriptor, HashSet<String> undeployList, String platform, String bitlength, String unicode, String saveFileName, String realFileName) {
    String key = platform + "," + bitlength + "," + unicode + "," + realFileName;
    String[] parsedLine = nativeDescriptor.get(key);
    if (parsedLine != null) {
      //increase ussage with 1 if this key is not already updated
      if (!undeployList.contains(key)) {
        int num = Integer.parseInt(parsedLine[3]) + 1;
        parsedLine[3] = Integer.toString(num);
      }
      //always use newly generated save name!
      parsedLine[4] = saveFileName;
    } else {
      parsedLine = new String[6];
      parsedLine[0] = platform;
      parsedLine[1] = bitlength;
      parsedLine[2] = unicode;
      parsedLine[3] = "1";
      parsedLine[4] = saveFileName;
      parsedLine[5] = realFileName;
      nativeDescriptor.put(key, parsedLine);
    }
    undeployList.add(key);
  }

}