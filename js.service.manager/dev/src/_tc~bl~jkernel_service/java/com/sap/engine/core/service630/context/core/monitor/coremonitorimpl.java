/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.core.service630.context.core.monitor;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.Manager;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.core.service630.container.ServiceContainerImpl;
import com.sap.engine.core.service630.container.ServiceWrapper;
import com.sap.engine.core.service630.container.PropertiesEventHandler;
import com.sap.engine.frame.NestedProperties;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.monitor.DescriptorContainer;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @see com.sap.engine.frame.core.monitor.CoreMonitor
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class CoreMonitorImpl implements CoreMonitor {

  private final ServiceContainerImpl container;
  private ServiceWrapper service;
  //engine versions arestatic variables
  private static String majorVersion = null;
  private static String minorVersion = null;
  private static String microVersion = null;
  private static String buildVersion = null;
  //menager properties is static table
  public static final HashMap<String, NestedProperties> managersCurrentProperties = new HashMap<String, NestedProperties>();

  private static final Location LOCATION = Location.getLocation(CoreMonitorImpl.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Category CATEGORY = Category.SYS_SERVER;

  public CoreMonitorImpl(ServiceContainerImpl container, ServiceWrapper service) {
    this.container = container;
    this.service = service;
  }

  public String getCoreVersion() {
    return Framework.getFramework().getVersion();
  }

  public String getCoreMajorVersion() {
    if (majorVersion == null) {
      // NOTE: Version parsing in all version getter methods is done
      // independently to avoid synchronization
      String version = Framework.getFramework().getVersion();
      int dotIndex = version.indexOf('.');
      majorVersion = version.substring(0, dotIndex);
    }
    return majorVersion;
  }

  public String getCoreMinorVersion() {
    if (minorVersion == null) {
      // NOTE: Version parsing in all version getter methods is done
      // independently to avoid synchronization
      String version = Framework.getFramework().getVersion();
      int dotIndex = version.indexOf('.');
      int dot2Index = version.indexOf('.', dotIndex + 1);
      minorVersion = version.substring(dotIndex + 1, dot2Index);
    }
    return minorVersion;
  }

  public String getCoreMicroVersion() {
    if (microVersion == null) {
      // NOTE: Version parsing in all version getter methods is done
      // independently to avoid synchronization
      String version = Framework.getFramework().getVersion();
      int dotIndex = version.indexOf('.');
      int dot2Index = version.indexOf('.', dotIndex + 1);
      int dot3Index = version.indexOf('.', dot2Index + 1);
      int dot4Index = version.indexOf('.', dot3Index + 1);
      microVersion = version.substring(dot2Index + 1, dot4Index);
    }
    return microVersion;
  }

  public String getCoreBuildVersion() {
    if (buildVersion == null) {
      // NOTE: Version parsing in all version getter methods is done
      // independently to avoid synchronization
      String version = Framework.getFramework().getVersion();
      int dotIndex = version.indexOf('.');
      int dot2Index = version.indexOf('.', dotIndex + 1);
      int dot3Index = version.indexOf('.', dot2Index + 1);
      int dot4Index = version.indexOf('.', dot3Index + 1);
      buildVersion = version.substring(dot4Index + 1);
    }
    return buildVersion;
  }

  public String[] getAllManagers() {
    return Framework.listOnlyManagers();
  }

  public Properties getManagerProperties(String name) throws IllegalArgumentException {
    checkForExistence(name);
    initProperties(name);
    return (Properties) managersCurrentProperties.get(name).clone();
  }

  public String getManagerProperty(String name, String key) throws IllegalArgumentException {
    checkForExistence(name);
    initProperties(name);
    return managersCurrentProperties.get(name).getProperty(key);
  }

  public void updateManagerProperties(String name, Properties properties) throws IllegalArgumentException {
    checkForExistence(name);
    initProperties(name);
    Framework.getManager(name).updateProperties((Properties) properties.clone());
    PropertiesEventHandler.applyChanges(managersCurrentProperties.get(name), properties);
    if (SimpleLogger.isWritable(Severity.INFO, LOCATION)) {
      SimpleLogger.trace(Severity.INFO, LOCATION, "Service [" + service.getComponentName() + "] change maneger [" + name + "] runtime properties to: " + properties.toString());
    }
  }

  private void initProperties(String name) {
    synchronized (managersCurrentProperties) {
      if (!managersCurrentProperties.containsKey(name)) {
        try {
          NestedProperties props = container.getMemoryContainer().getPersistentContainer().getComponentProperties(name, true, false);
          managersCurrentProperties.put(name, props);
        } catch (ServiceException e) {
          LOCATION.traceThrowableT(Severity.WARNING, "getManagerProperties(" + name + ")", e);
          //throw runtime exception
          throw new ServiceRuntimeException(LOCATION, e);
        }
      }
    }
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Method initProperties(" + name + ") called from service " + service.getComponentName() + " initializes : " + managersCurrentProperties.get(name));
    }
  }

  public void shutDown(int exitCode) {
    if (SimpleLogger.isWritable(Severity.INFO, CATEGORY)) {
      SimpleLogger.log(Severity.INFO, CATEGORY, LOCATION, "ASJ.krn_srv.000061", "Service [{0}] initiate shutdown", service.getComponentName());
    }
    Framework.shutDown(exitCode, new Properties());
  }

  public ManagementInterface getManagementInterface(String name) {
    checkForExistence(name);
    return Framework.getManager(name).getManagementInterface();
  }

  public byte getRuntimeMode() {
    return Framework.getRuntimeMode();
  }

  public byte getRuntimeAction() {
    return Framework.getRuntimeAction();
  }

  public String getInstanceType() {
    return container.getInstanceType();
  }

  private void checkForExistence(String managerName) {
    if (Framework.getManager(managerName) == null) {
      throw new IllegalArgumentException(ResourceUtils.formatString(ResourceUtils.NO_SUCH_MANAGER, new Object[] {managerName}));
    }
  }

  ////////////////////////////////////////////// DEPRECATED ////////////////////////////////////////////////////////////

  public Properties getManagerRuntimeProperties(String name) throws IllegalArgumentException {
    checkForExistence(name);
    Manager manager = Framework.getManager(name);
    Properties result = manager.getCurrentProperties();
    if (result == null) result = new Properties();
    return result;
  }

  public String getManagerRuntimeProperty(String name, String key) throws IllegalArgumentException {
    checkForExistence(name);
    Manager manager = Framework.getManager(name);
    return manager.getCurrentProperty(key);
  }

  public boolean setManagerProperty(String name, String key, String value) throws IllegalArgumentException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerProperty(" + name + ", " + key + ", " + value + ")");
    }
    checkForExistence(name);
    boolean result = Framework.getManager(name).setProperty(key, value);
    Properties tmp = new Properties();
    tmp.setProperty(key, value);
    try {
      container.getMemoryContainer().getPersistentContainer().storeComponentProps(name, tmp, false, false, null, true);
    } catch (ServiceException e) {
      LOCATION.traceThrowableT(Severity.INFO, "setManagerProperty(" + name + ", " + key + ", " + value + ")", e);
      //throw runtime exception
      throw new ServiceRuntimeException(LOCATION, e);
    }
    return result;
  }

  public boolean setManagerProperties(String name, Properties properties) throws IllegalArgumentException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerProperties(" + name + ", " + properties + ")");
    }
    checkForExistence(name);
    Properties merge = getManagerRuntimeProperties(name);
    for (Object keyObj : properties.keySet()) {
      String key = (String) keyObj;
      merge.setProperty(key, properties.getProperty(key));
    }
    boolean result = (Framework.getManager(name).setProperties(merge));
    try {
      container.getMemoryContainer().getPersistentContainer().storeComponentProps(name, properties, false, false, null, true);
    } catch (ServiceException e) {
      LOCATION.traceThrowableT(Severity.INFO, "setManagerProperties(" + name + ", " + properties + ")", e);
      //throw runtime exception
      throw new ServiceRuntimeException(LOCATION, e);
    }
    return result;
  }

  public boolean notifyManagerPropertiesChange(String name, Properties properties) {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("notifyManagerPropertiesChange(" + name + ", " + properties + ")");
    }
    checkForExistence(name);
    Properties merge = getManagerRuntimeProperties(name);
    for (Object keyObj : properties.keySet()) {
      String key = (String) keyObj;
      merge.setProperty(key, properties.getProperty(key));
    }
    return (Framework.getManager(name).setProperties(merge));
  }

  public void shutDown() {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("shutDown()");
    }
    shutDown(0);
  }

  public synchronized DescriptorContainer getDescriptorContainer() {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("getDescriptorContainer()");
    }
    return null;
  }

  public Properties getManagerGlobalDefaultProperties(String manager) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("getManagerGlobalDefaultProperties(" + manager + ")");
    }
    checkForExistenceSE(manager);
    return container.getMemoryContainer().getPersistentContainer().getComponentProperties(manager, true, true, true);
  }

  public Properties getManagerGlobalCustomProperties(String manager) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("getManagerGlobalCustomProperties(" + manager + ")");
    }
    checkForExistenceSE(manager);
    return container.getMemoryContainer().getPersistentContainer().getComponentProperties(manager, true, false, true);
  }

  public Properties getManagerLocalDefaultProperties(String manager) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("getManagerLocalDefaultProperties(" + manager + ")");
    }
    checkForExistenceSE(manager);
    return container.getMemoryContainer().getPersistentContainer().getComponentProperties(manager, false, true, true);
  }

  public Properties getManagerLocalCustomProperties(String manager) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("getManagerLocalCustomProperties(" + manager + ")");
    }
    checkForExistenceSE(manager);
    return container.getMemoryContainer().getPersistentContainer().getComponentProperties(manager, false, false, true);
  }

  public void setManagerGlobalDefaultProperties(String manager, Properties properties) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerGlobalDefaultProperties(" + manager + ", " + properties + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().storeComponentProps(manager, properties, true, true, null, true);
  }

  public void setManagerGlobalCustomProperties(String manager, Properties properties) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerGlobalCustomProperties(" + manager + ", " + properties + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().storeComponentProps(manager, properties, true, false, null, true);
  }

  public void setManagerLocalDefaultProperties(String manager, Properties properties) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerLocalDefaultProperties(" + manager + ", " + properties + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().storeComponentProps(manager, properties, false, true, null, true);
  }

  public void setManagerLocalCustomProperties(String manager, Properties properties) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerLocalCustomProperties(" + manager + ", " + properties + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().storeComponentProps(manager, properties, false, false, null, true);
  }

  public void setManagerGlobalDefaultProperties(String manager, Properties properties, Set secured) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerGlobalDefaultProperties(" + manager + ", " + properties + ", " + secured + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().storeComponentProps(manager, properties, true, true, secured, true);
  }

  public void setManagerGlobalCustomProperties(String manager, Properties properties, Set secured) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerGlobalCustomProperties(" + manager + ", " + properties + ", " + secured + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().storeComponentProps(manager, properties, true, false, secured, true);
  }

  public void setManagerLocalDefaultProperties(String manager, Properties properties, Set secured) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerLocalDefaultProperties(" + manager + ", " + properties + ", " + secured + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().storeComponentProps(manager, properties, false, true, secured, true);
  }

  public void setManagerLocalCustomProperties(String manager, Properties properties, Set secured) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("setManagerLocalCustomProperties(" + manager + ", " + properties + ", " + secured + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().storeComponentProps(manager, properties, false, false, secured, true);
  }

  public Set getGlobalSecuredPropertiesKeys(String manager) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("getGlobalSecuredPropertiesKeys(" + manager + ")");
    }
    checkForExistenceSE(manager);
    return container.getMemoryContainer().getPersistentContainer().getSecuredPropertiesKeys(manager, true, true);
  }

  public Set getLocalSecuredPropertiesKeys(String manager) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("getLocalSecuredPropertiesKeys(" + manager + ")");
    }
    checkForExistenceSE(manager);
    return container.getMemoryContainer().getPersistentContainer().getSecuredPropertiesKeys(manager, false, true);
  }

  public void restoreGlobalProperties(String manager) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("restoreGlobalProperties(" + manager + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().restoreComponentProperties(manager, true, null, true);
  }

  public void restoreGlobalProperty(String manager, String key) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("restoreGlobalProperty(" + manager + ", " + key + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().restoreComponentProperties(manager, true, new String[] {key}, true);
  }

  public Set[] restoreGlobalProperties(String manager, String[] keys) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("restoreGlobalProperties(" + manager + ", " + keys + ")");
    }
    checkForExistenceSE(manager);
    return container.getMemoryContainer().getPersistentContainer().restoreComponentProperties(manager, true, keys, true);
  }

  public Set restoreLocalProperties(String manager) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("restoreLocalProperties(" + manager + ")");
    }
    checkForExistenceSE(manager);
    return container.getMemoryContainer().getPersistentContainer().restoreComponentProperties(manager, false, null, true)[0];
  }

  public void restoreLocalProperty(String manager, String key) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("restoreLocalProperty(" + manager + ", " + key + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().restoreComponentProperties(manager, false, new String[] {key}, true);
  }

  public void restoreLocalProperties(String manager, String[] keys) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("restoreLocalProperties(" + manager + ", " + keys + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().restoreComponentProperties(manager, false, keys, true);
  }

  public void removeLocalProperties(String manager, String[] keys) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("removeLocalProperties(" + manager + ", " + keys + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().removeComponentProperties(manager, keys, false, true);
  }

  public void removeGlobalProperties(String manager, String[] keys) throws ServiceException {
    if (LOCATION.beDebug()) {
      logDeprecatedDegugInfo("removeGlobalProperties(" + manager + ", " + keys + ")");
    }
    checkForExistenceSE(manager);
    container.getMemoryContainer().getPersistentContainer().removeComponentProperties(manager, keys, true, true);
  }

  private void checkForExistenceSE(String managerName) throws ServiceException {
    if (Framework.getManager(managerName) == null) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.NO_SUCH_MANAGER), new Object[] {managerName}));
    }
  }

  private void logDeprecatedDegugInfo(String info) {
    LOCATION.debugT("Deprecated method " + info + " is invoked from service " + service.getComponentName());
  }

}