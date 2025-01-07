package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.core.configuration.impl.addons.processors.CacheConfigurationProcessor;
import com.sap.engine.core.configuration.impl.addons.processors.LogConfigurationProcessor;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tc.logging.Severity;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.sap.engine.tools.offlinedeploy.rdb.OfflineComponentDeployImpl.loc;

/**
 * This class is used to deploy components with software type engine-bootstrap and engine-kernel.
 * todo: implement single engine-kernel component undeploy
 *
 * @version 710
 * @author Dimitar Kostadinov
 */
public class KernelDeployer implements Constants {

  /**
   * Deploys bootsrtap SDA in DB.
   *
   * @param handler - current transaction.
   * @param zipFile - SDA archive.
   * @throws IOException - if any I/O error occur.
   * @throws ConfigurationException - if any DB error occur.
   */
  static void deployBootstrap(ConfigurationHandler handler, ZipFile zipFile) throws IOException, ConfigurationException, DeploymentException {
    Configuration bootstrap = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN + "/" + BOOTSTRAP);
    //remove existing files.
    bootstrap.deleteAllFiles();
    bootstrap.deleteAllSubConfigurations();
    //add new content.
    Enumeration enumeration = zipFile.entries();
    while (enumeration.hasMoreElements()) {
      ZipEntry entry = (ZipEntry) enumeration.nextElement();
      String zipEntryName = entry.getName();
      if (!zipEntryName.endsWith("/")) {
        int index = zipEntryName.lastIndexOf('/');
        if (index != -1) {
          String fileName = zipEntryName.substring(index + 1);
          String pathName = zipEntryName.substring(0, index);
          if (!pathName.equals("META-INF")) {
            Configuration cfg = Utils.createSubConfiguration(bootstrap, pathName);
            cfg.addFileAsStream(fileName, zipFile.getInputStream(entry));
          }
        } else {
          if (zipEntryName.equals("jvm.properties")) {
            Utils.addPropertiesInDB(zipFile, "jvm.properties", bootstrap, "jvm");
          } else {
            bootstrap.addFileAsStream(zipEntryName, zipFile.getInputStream(entry));
          }
        }
      }
    }
  }

  /**
   * Removes bootsrtap.
   *
   * @param handler - current transaction.
   * @throws ConfigurationException - if any DB error occur.
   * @throws ComponentNotDeployedException - if component is not deployed.
   */
  static void removeBootstrap(ConfigurationHandler handler) throws ConfigurationException, ComponentNotDeployedException {
    Configuration bootstrap = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN + "/" + BOOTSTRAP);
    if (bootstrap.getAllFileEntryNames().length > 0 || bootstrap.getAllSubConfigurationNames().length > 0) {
      bootstrap.deleteAllFiles();
      bootstrap.deleteAllSubConfigurations();
    } else {
      throw new ComponentNotDeployedException("Bootstrap is not deployed");
    }
  }

  /**
   * Deploys engine-kernel SDA in DB.
   *
   * @param handler - current transaction.
   * @param zipFile - SDA archive.
   * @throws IOException - if any I/O error occur.
   * @throws ConfigurationException - if any DB error occur.
   */
  static void deployKernel(ConfigurationHandler handler, ZipFile zipFile) throws IOException, ConfigurationException, DeploymentException {
    Configuration runtime = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN + "/" + RUNTIME);
    Enumeration entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = (ZipEntry) entries.nextElement();
      String entryName = entry.getName();
      if (!entryName.endsWith("/")) {
        for (String kernelBinary : KERNEL_BINARIES) {
          if (entryName.startsWith(kernelBinary)) {
            int index = entryName.lastIndexOf('/');
            String fileName = entryName.substring(index + 1);
            String pathName = entryName.substring("server/bin/".length(), index);
            Configuration cfg = Utils.createSubConfiguration(runtime, pathName);
            cfg.updateFileAsStream(fileName, zipFile.getInputStream(entry), true);
            break;
          }
        }
      }
    }
    Configuration workernode = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + CLUSTERNODE_CONFIG + "/" + WORKERNODE);
    Utils.addFileInDB(zipFile, JITEXCLUDE, workernode, JITEXCLUDE, true);
    Utils.addFileInDB(zipFile, "server/java.policy", workernode, "java.policy", true);
    Utils.addFileInDB(zipFile, "server/remote.policy", workernode, "remote.policy", true);
    Utils.addFileInDB(zipFile, "server/substitutionvalues.properties", workernode, "substitutionvalues.properties", true);
    //add jvm properties
    uploadJvmParams(zipFile, workernode);
    Utils.addPropertiesInDB(zipFile, "server/cfg/jvm_params/jvm.properties", workernode, "jvm_settings", true);
    //add shared memory configuration values
    // - shmConfiguration.properties contains the shared memory slot sizes themselves
    // - shmMapping.properties contains the mapping of the properties to MONITORING and FUNCTIONAL shared memory values.
    Utils.addPropertiesInDB(zipFile, "server/cfg/shmConfiguration.properties", workernode, "shmConfiguration", true);
    Utils.addPropertiesInDB(zipFile, "server/cfg/shmMapping.properties", workernode, "shmMapping", true);
    //add initial filters
    Utils.addFileInDB(zipFile, "server/cfg/filters.txt", workernode, "filters.txt", true);
    for (String kernelCfg : KERNEL_CFG) {
      entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        String entryName = entry.getName();
        if (entryName.startsWith(kernelCfg)) {
          if (!entryName.endsWith("/")) {
            int index = entryName.lastIndexOf("/");
            String fileName = entryName.substring(index + 1);
            String pathName = entryName.substring(kernelCfg.lastIndexOf("/"), index);
            Configuration cfg = Utils.createSubConfiguration(workernode, pathName);
            if (fileName.endsWith(".properties") && !fileName.equals("kernel.properties")) {
              //maanger properties into <MANAGER_NAME>/properties configuration
              int m_index = fileName.lastIndexOf('M');
              String manager = fileName.substring(0, m_index);
              cfg = Utils.createSubConfiguration(cfg, manager);
              Utils.addPropertiesInDB(zipFile, entryName, cfg, "properties", true);
            } else {
              if (fileName.equals(LOG_CONFIGURATION_XML)) {
                cfg = Utils.createSubConfiguration(cfg, "Log");
                //store log-configuration.xml file as file entry
                cfg.updateFileAsStream(fileName, zipFile.getInputStream(entry), true);
                //log-configuration.xml configuration ->
                cfg = Utils.createSubConfiguration(cfg, LOG_CONFIGURATION_XML);
                LogConfigurationProcessor processor = new LogConfigurationProcessor();
                processor.write(zipFile.getInputStream(entry), cfg);
              } else if (fileName.equals(CACHE_CONFIGURATION_XML)) {
                cfg = Utils.createSubConfiguration(cfg, "Cache");
                //cache-configuration.xml configuration ->
                cfg = Utils.createSubConfiguration(cfg, CACHE_CONFIGURATION_XML);
                CacheConfigurationProcessor processor = new CacheConfigurationProcessor();
                processor.write(zipFile.getInputStream(entry), cfg);
              } else if (fileName.equals("hosts.txt")) {
                cfg = Utils.createSubConfiguration(cfg, "IpVerification");
                cfg.updateFileAsStream(fileName, zipFile.getInputStream(entry), true);
              } else {
                cfg.updateFileAsStream(fileName, zipFile.getInputStream(entry), true);
              }
            }
          }
        }
      }
    }
    //add system info
    Configuration standard_instance = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + TEMPLATES + "/" + BASE + "/" + STANDARD_INSTANCE + "/" + CFG);
    Utils.addPropertiesInDB(zipFile, "server/cfg/SystemInfo.properties", standard_instance, "SystemInfo", true);
  }

  /**
   * Removes all engine-kernel components.
   *
   * @param handler - current transaction.
   * @throws ConfigurationException - if any DB error occur.
   */
  static void removeKernel(ConfigurationHandler handler) throws ConfigurationException {
    Configuration workernode = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + CLUSTERNODE_CONFIG + "/" + WORKERNODE);
    if (workernode.existsFile(JITEXCLUDE)) workernode.deleteFile(JITEXCLUDE);
    workernode.deleteFile("java.policy");
    workernode.deleteFile("remote.policy");
    if (workernode.existsFile("filters.txt")) workernode.deleteFile("filters.txt");
    if (workernode.existsFile("substitutionvalues.properties")) workernode.deleteFile("substitutionvalues.properties");
    if (workernode.existsSubConfiguration(JVM_PARAMS_SUBCONFIGURATION)) {
      workernode.deleteConfiguration(JVM_PARAMS_SUBCONFIGURATION);
    }
    workernode.deleteConfiguration("jvm");
    for (String kernelCfg : KERNEL_CFG) {
      String path = kernelCfg.substring(kernelCfg.lastIndexOf("/") + 1);
      workernode.deleteConfiguration(path);
    }
    Configuration runtime = Utils.openConfiguration(handler, CLUSTER_CONFIG + "/" + GLOBALS + "/" + BIN + "/" + RUNTIME);
    for (String kernelBinary :  KERNEL_BINARIES) {
      String path = kernelBinary.substring("server/bin/".length());
      if (runtime.existsSubConfiguration(path)) {
        runtime.deleteConfiguration(path);
      }
    }
  }

  ////////////////////////////////////////// VM PARAMS /////////////////////////////////////////////////////////////////

  private static void uploadJvmParams(ZipFile zipFile, Configuration workernode) throws IOException, ConfigurationException, DeploymentException {
    ZipEntry jvmProperties = zipFile.getEntry("server/cfg/jvm_params/jvm.properties");
    if (jvmProperties != null) {
      HashMap<String, ZipEntry> memory = new HashMap<String, ZipEntry>();
      HashMap<String, ZipEntry> system = new HashMap<String, ZipEntry>();
      HashMap<String, ZipEntry> additional = new HashMap<String, ZipEntry>();
      Enumeration entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        String entryName = entry.getName();
        if (entryName.startsWith("server/cfg/jvm_params/")) {
          String key = entryName.substring("server/cfg/jvm_params/".length());
          if (key.indexOf('/') == -1 && key.endsWith(".properties")) {
            key = key.substring(0, key.length() - ".properties".length());
            if (key.startsWith("memory")) {
              key = key.substring("memory".length());
              if (key.equals("") || key.startsWith("~")) {
                memory.put(key, entry);
              }
            } else if (key.startsWith("system")) {
              key = key.substring("system".length());
              if (key.equals("") || key.startsWith("~")) {
                system.put(key, entry);
              }
            } else if (key.startsWith("additional")) {
              key = key.substring("additional".length());
              if (key.equals("") || key.startsWith("~")) {
                additional.put(key, entry);
              }
            }
          }
        }
      }
      //check for consistence
      if (!memory.containsKey("") || !system.containsKey("") || !additional.containsKey("")) {
        throw new DeploymentException("Cannot upload jvm params because memory, system or additional properties missing");
      }

      if (workernode.existsSubConfiguration(JVM_PARAMS_SUBCONFIGURATION)) {
        workernode.deleteConfiguration(JVM_PARAMS_SUBCONFIGURATION);
      }
      //upload jvm params
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      //iterate memory
      for (String key : memory.keySet()) {
        String[] vo = getVendorAndOs(key);
        byte[] mb = getEntryByteArray(zipFile, memory.get(key), baos);
        byte[] sb = getEntryByteArray(zipFile, system.remove(key), baos);
        byte[] ab = getEntryByteArray(zipFile, additional.remove(key), baos);
        initializeVMParameters(vo[0], vo[1], mb, sb, ab, workernode);
      }
      //iterate system
      for (String key : system.keySet()) {
        String[] vo = getVendorAndOs(key);
        byte[] sb = getEntryByteArray(zipFile, system.get(key), baos);
        byte[] ab = getEntryByteArray(zipFile, additional.remove(key), baos);
        initializeVMParameters(vo[0], vo[1], null, sb, ab, workernode);
      }
      //iterate additional
      for (String key : additional.keySet()) {
        String[] vo = getVendorAndOs(key);
        byte[] ab = getEntryByteArray(zipFile, additional.get(key), baos);
        initializeVMParameters(vo[0], vo[1], null, null, ab, workernode);
      }
      createVMParamsStructure(workernode, zipFile);
    }
  }

  private static void initializeVMParameters(String vendor, String platform, byte[] memory, byte[] system, byte[] additional, Configuration base) throws DeploymentException, ConfigurationException {
    String path;
    if (vendor == null) {
      if (platform != null) {
        throw new DeploymentException("Cannot initialize VM parameters. Both VM Vendor and Platform must be null for default parameters");
      }
      path = "";
    } else {
      if (platform == null) {
        path = vendor;
      } else {
        path = vendor + "/" + platform;
      }
    }
    Configuration tmp = Utils.createSubConfiguration(base, JVM_PARAMS_SUBCONFIGURATION + "/" + path);
    if (memory != null) {
      PropertySheet memoryPS = Utils.createSubConfiguration(tmp, "memory", Configuration.CONFIG_TYPE_PROPERTYSHEET).getPropertySheetInterface();
      memoryPS.updatePropertyEntries(memory);
    }
    if (system != null) {
      PropertySheet systemPS = Utils.createSubConfiguration(tmp, "system", Configuration.CONFIG_TYPE_PROPERTYSHEET).getPropertySheetInterface();
      systemPS.updatePropertyEntries(system);
    }
    if (additional != null) {
      PropertySheet additionalPS = Utils.createSubConfiguration(tmp, "additional", Configuration.CONFIG_TYPE_PROPERTYSHEET).getPropertySheetInterface();
      additionalPS.updatePropertyEntries(additional);
    }
  }

  private static void createVMParamsStructure(Configuration base, ZipFile zipFile) throws IOException, ConfigurationException {
    // should have already been created by initializeVMParameters()
    Configuration cfg = base.getSubConfiguration(JVM_PARAMS_SUBCONFIGURATION);
    ArrayList<String> vmVendors = getListFromFile(zipFile, "server/cfg/jvm_params/vm_vendors.lst");
    ArrayList<String> osNames = getListFromFile(zipFile, "server/cfg/jvm_params/os_names.lst");
    for (String vmVendor : vmVendors) {
      Configuration vm = Utils.createSubConfiguration(cfg, vmVendor);
      String[] names = new String[] {"maxHeapSize", "globalArea"};
      // create entry for maxHeapSize linked to the default one in each vendor
      initVMParamsPropertySheets(cfg, vm, "memory", names);
      // create empty property sheets for system and additional in each vendor
      initVMParamsPropertySheets(cfg, vm, "system", null);
      initVMParamsPropertySheets(cfg, vm, "additional", null);
      for (String osName : osNames) {
        Configuration os = Utils.createSubConfiguration(vm, osName);
        // create entry for maxHeapSize linked to the default one in each os
        initVMParamsPropertySheets(vm, os, "memory", names);
        // create empty property sheets for system and additional in each os
        initVMParamsPropertySheets(vm, os, "system", null);
        initVMParamsPropertySheets(vm, os, "additional", null);
      }
    }
  }

  private static void initVMParamsPropertySheets(Configuration def, Configuration custom, String psName, String[] entryNames) throws ConfigurationException {
    PropertySheet defPS = def.getSubConfiguration(psName).getPropertySheetInterface();
    PropertySheet customPS = custom.existsSubConfiguration(psName) ?
        custom.getSubConfiguration(psName).getPropertySheetInterface() :
        custom.createSubConfiguration(psName, Configuration.CONFIG_TYPE_PROPERTYSHEET).getPropertySheetInterface();
    if (entryNames != null) {
      for (PropertyEntry propertyEntry : defPS.getAllPropertyEntries()) {
        SimpleLogger.trace(Severity.DEBUG, loc, "ASJ.dpl_off.000010", "Checking entry [{0}]#[{1}]", new Object[] {psName, propertyEntry.getName()});
        for (String entryName : entryNames) {
          if (propertyEntry.getName().startsWith(entryName) && !customPS.existsPropertyEntry(propertyEntry.getName())) {
            SimpleLogger.trace(Severity.DEBUG, loc, "ASJ.dpl_off.000011", "Creating linked [{0}]#[{1}]", new Object[] {psName, propertyEntry.getName()});
            customPS.createPropertyEntry(propertyEntry.getName(), "$link{../../" + psName + "#" + propertyEntry.getName() + "}",
                propertyEntry.getDescription(),
                propertyEntry.getDefaultFlags());
          }
        }
      }
    }
  }

  private static ArrayList<String> getListFromFile(ZipFile zipFile, String entryPath) throws IOException {
    ArrayList<String> list = new ArrayList<String>();
    ZipEntry entry = zipFile.getEntry(entryPath);
    BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));
    String line;
    while ((line = reader.readLine()) != null) {
      if (!line.equals("") && !line.startsWith("#")) {
        list.add(line.trim());
      }
    }
    return list;
  }

  private static String[] getVendorAndOs(String key) {
    String[] result = new String[2];
    result[0] = null;
    result[1] = null;
    if (!key.equals("")) {
      key = key.substring(1);
      int index = key.indexOf('~');
      if (index != -1) {
        result[0] = key.substring(0, index);
        result[1] = key.substring(index + 1);
      } else {
        result[0] = key;
      }
    }
    return result;
  }

  private static byte[] getEntryByteArray(ZipFile zipFile, ZipEntry entry, ByteArrayOutputStream baos) throws IOException {
    byte[] result = null;
    if (entry != null) {
      baos.reset();
      InputStream is;
      byte[] buffer = new byte[1024];
      int read;
      is = zipFile.getInputStream(entry);
      while ((read = is.read(buffer)) != -1) {
        baos.write(buffer, 0, read);
      }
      is.close();
      result = baos.toByteArray();
    }
    return result;
  }

}