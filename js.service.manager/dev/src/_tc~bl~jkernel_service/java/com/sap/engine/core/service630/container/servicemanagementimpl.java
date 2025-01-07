package com.sap.engine.core.service630.container;

import com.sap.engine.core.service630.ServiceManagement;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Set;

/**
 * ServiceManagement implementation.
 *
 * Properties object structure:
 *
 * [name] -> [name of the component]
 * [type] -> [type of the component - valid entry from the types array]
 * [current_status] -> [current component status - started/stopped/error]
 * [default_status] -> [default component status - started/stopped]
 *
 * @see com.sap.engine.core.service630.ServiceManagement
 *
 * @author Dimitar Kostadinov
 * @version 7.10
 */
public class ServiceManagementImpl implements ServiceManagement {

  private final static String INTERFACE = "interface";
  private final static String LIBRARY = "library";
  private final static String SERVICE = "service";
  private final static String TYPE = "type";
  private final static String NAME = "name";
  private final static String CURRENT_STATUS = "current_status";
  private final static String DEFAULT_STATUS = "default_status";
  private final static String STARTED = "started";
  private final static String ERROR = "error";
  private final static String STOPPED = "stopped";
  private final static String HASH = "hash";

  private final static String[] COMPONENT_TYPES = new String[] {INTERFACE, LIBRARY, SERVICE};

  private ServiceContainerImpl serviceContainer;

  public ServiceManagementImpl(ServiceContainerImpl serviceContainer) {
    this.serviceContainer = serviceContainer;
  }

  public String[] getTypes() {
    return COMPONENT_TYPES;
  }

  public String[] getNames(String type) {
    validateType(type);
    Hashtable<String, ? extends ComponentWrapper> components;
    if (type.equals(INTERFACE)) {
      components = serviceContainer.getMemoryContainer().getInterfaces();
    } else if (type.equals(LIBRARY)) {
      components = serviceContainer.getMemoryContainer().getLibraries();
    } else {
      components = serviceContainer.getMemoryContainer().getServices();
    }
    Set<String> keys = components.keySet();
    String[] result = new String[keys.size()];
    keys.toArray(result);
    return result;
  }

  public Properties getStatus(String name, String type) {
    validateType(type);
    Properties result = new Properties();
    if (type.equals(INTERFACE)) {
      Hashtable<String, InterfaceWrapper> components = serviceContainer.getMemoryContainer().getInterfaces();
      validateName(name, components);
      InterfaceWrapper iw = components.get(name);
      result.setProperty(NAME, name);
      result.setProperty(TYPE, type);
      String currentStatus = (iw.getStatus() == ComponentMonitor.STATUS_LOADED) ? STARTED : ERROR;
      result.setProperty(CURRENT_STATUS, currentStatus);
      result.setProperty(DEFAULT_STATUS, STARTED);
    } else if (type.equals(LIBRARY)) {
      Hashtable<String, LibraryWrapper> components = serviceContainer.getMemoryContainer().getLibraries();
      validateName(name, components);
      LibraryWrapper lw = components.get(name);
      result.setProperty(NAME, name);
      result.setProperty(TYPE, type);
      String currentStatus = (lw.getStatus() == ComponentMonitor.STATUS_LOADED) ? STARTED : ERROR;
      result.setProperty(CURRENT_STATUS, currentStatus);
      result.setProperty(DEFAULT_STATUS, STARTED);
    } else if (type.equals(SERVICE)) {
      Hashtable<String, ServiceWrapper> components = serviceContainer.getMemoryContainer().getServices();
      validateName(name, components);
      ServiceWrapper sw = components.get(name);
      result.setProperty(NAME, name);
      result.setProperty(TYPE, type);
      String currentStatus;
      if (sw.getStatus() == ComponentMonitor.STATUS_ACTIVE) {
        currentStatus = STARTED;
      } else if (sw.getInternalStatus() == ServiceWrapper.INTERNAL_STATUS_START_FAIL) {
        currentStatus = ERROR;
      } else {
        currentStatus = STOPPED;
      }
      result.setProperty(CURRENT_STATUS, currentStatus);
      String defaultStatus = (sw.getStartupMode() == ServiceMonitor.ALWAYS_START) ? STARTED : STOPPED;
      result.setProperty(DEFAULT_STATUS, defaultStatus);
    }
    return result;
  }

  public Properties[] getStatuses() {
    String[] types = getTypes();
    String[][] names = new String[types.length][];
    int count = 0;
    for (int i = 0; i < types.length; i++) {
      names[i] = getNames(types[i]);
      count += names[i].length;
    }
    Properties[] result = new Properties[count];
    int pos = 0;
    for (int i = 0; i < names.length; i++) {
      for (int j = 0; j < names[i].length; j++) {
        result[pos++] = getStatus(names[i][j], types[i]);
      }
    }
    return result;
  }

  public Properties getHash(String name, String type) {
    validateType(type);
    Properties result = new Properties();
    if (type.equals(INTERFACE)) {
      Hashtable<String, InterfaceWrapper> components = serviceContainer.getMemoryContainer().getInterfaces();
      validateName(name, components);
      InterfaceWrapper iw = components.get(name);
      result.setProperty(NAME, name);
      result.setProperty(TYPE, type);
      result.setProperty(HASH, iw.getHash());
    } else if (type.equals(LIBRARY)) {
      Hashtable<String, LibraryWrapper> components = serviceContainer.getMemoryContainer().getLibraries();
      validateName(name, components);
      LibraryWrapper lw = components.get(name);
      result.setProperty(NAME, name);
      result.setProperty(TYPE, type);
      result.setProperty(HASH, lw.getHash());
    } else if (type.equals(SERVICE)) {
      Hashtable<String, ServiceWrapper> components = serviceContainer.getMemoryContainer().getServices();
      validateName(name, components);
      ServiceWrapper sw = components.get(name);
      result.setProperty(NAME, name);
      result.setProperty(TYPE, type);
      result.setProperty(HASH, sw.getHash());
    }
    return result;
  }

  public Properties[] getHashes() {
    String[] types = getTypes();
    String[][] names = new String[types.length][];
    int count = 0;
    for (int i = 0; i < types.length; i++) {
      names[i] = getNames(types[i]);
      count += names[i].length;
    }
    Properties[] result = new Properties[count];
    int pos = 0;
    for (int i = 0; i < names.length; i++) {
      for (int j = 0; j < names[i].length; j++) {
        result[pos++] = getHash(names[i][j], types[i]);
      }
    }
    return result;
  }

  public void registerManagementListener(ManagementListener managementListener) {
  }

  private void validateType(String type) {
    if (!type.equals(COMPONENT_TYPES[0]) && !type.equals(COMPONENT_TYPES[1]) && !type.equals(COMPONENT_TYPES[2])) {
      throw new IllegalArgumentException(ResourceUtils.formatString(ResourceUtils.TYPE_IS_NOT_VALID, new Object[] {type}));
    }
  }

  private void validateName(String name, Hashtable components) {
    if (!components.containsKey(name)) {
      throw new IllegalArgumentException(ResourceUtils.formatString(ResourceUtils.NAME_IS_NOT_VALID, new Object[] {name}));
    }
  }

}