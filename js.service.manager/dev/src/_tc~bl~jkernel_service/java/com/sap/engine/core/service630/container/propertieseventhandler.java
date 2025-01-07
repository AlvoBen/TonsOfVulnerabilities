package com.sap.engine.core.service630.container;

import java.util.*;

import com.sap.engine.core.Framework;
import com.sap.engine.core.Manager;
import com.sap.engine.core.Names;
import com.sap.engine.core.service630.context.core.monitor.CoreMonitorImpl;
import com.sap.engine.frame.NestedProperties;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.engine.frame.core.configuration.*;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This singleton class is used for updating services online modifiable properties.
 *
 * @author Dimitar Kostadinov
 * @version 7.10
 */
public class PropertiesEventHandler implements ConfigurationChangedListener {

  private static final String CLUSTER_CONFIG_SERVICES_PATH = "cluster_config/system/instances/current_instance/cfg/services";
  private static final String CLUSTER_CONFIG_MANAGERS_PATH = "cluster_config/system/instances/current_instance/cfg/kernel";
  private static final String SUFFIX = "/properties";

  private ConfigurationHandler cfgHandler;
  private MemoryContainer memoryContainer;

  private static final Category CATEGORY = Category.SYS_SERVER;
  private static final Location LOCATION = Location.getLocation(PropertiesEventHandler.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  PropertiesEventHandler(MemoryContainer memoryContainer) {
    this.memoryContainer = memoryContainer;
    try {
      ConfigurationHandlerFactory factory = (ConfigurationHandlerFactory) Framework.getManager(Names.CONFIGURATION_MANAGER);
      cfgHandler = factory.getConfigurationHandler();
      //listen for managers properties change
      cfgHandler.addConfigurationChangedListener(this, CLUSTER_CONFIG_MANAGERS_PATH);
    } catch (ConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "Unable to register a configuration level change listener", e);
      }
    }
  }

  void destroy() {
    cfgHandler.removeConfigurationChangedListener(this, CLUSTER_CONFIG_MANAGERS_PATH);
  }

  /**
   * Registers listener for a service
   *
   * @param wrapper ServiceWrapper of the service
   */
  void addConfigurationChangedListener(ServiceWrapper wrapper) {
    try {
      ConfigurationHandlerFactory factory = (ConfigurationHandlerFactory) Framework.getManager(Names.CONFIGURATION_MANAGER);
      ConfigurationHandler cfgHandler = factory.getConfigurationHandler();
      //listen for services properties change
      cfgHandler.addConfigurationChangedListener(this, CLUSTER_CONFIG_SERVICES_PATH + "/" +
                                                       wrapper.getComponentName() + "/properties");
    } catch (ConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "Unable to register a configuration level change listener", e);
      }
    }
  }

  /**
   * Unregisters listener for a service
   *
   * @param wrapper ServiceWrapper of the service
   */
  void removeConfigurationChangedListener(ServiceWrapper wrapper) {
    try {
      ConfigurationHandlerFactory factory = (ConfigurationHandlerFactory) Framework.getManager(Names.CONFIGURATION_MANAGER);
      ConfigurationHandler cfgHandler = factory.getConfigurationHandler();
      //listen for services properties change
      cfgHandler.removeConfigurationChangedListener(this, CLUSTER_CONFIG_SERVICES_PATH + "/" +
                                                          wrapper.getComponentName() + "/properties");
    } catch (ConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "Unable to register a configuration level change listener", e);
      }
    }
  }

  /**
   * If a services or managers configuration tree is changed the event will be received. Only events related to
   * components online modifiable properties changes will be processed
   *
   * @param event - configuration change event
   */
  public void configurationChanged(ChangeEvent event) {
    String path = event.getPath();
    if (path.startsWith(CLUSTER_CONFIG_SERVICES_PATH)) {
      processServicesConfigurationChange(event);
    } else if (path.equals(CLUSTER_CONFIG_MANAGERS_PATH)) {
      processManagersConfigurationChange(event);
    }
  }

  private void processServicesConfigurationChange(ChangeEvent event) {
    Set<String> targetServiceNames = new HashSet<String>(1);
    //1. Iterate detailed events and find target services names.
    ChangeEvent[] events = event.getDetailedChangeEvents();
    for (ChangeEvent e : events) {
      String path = e.getPath();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Received configuration change event for path [" + path + "]");
      }
      if (path.startsWith(CLUSTER_CONFIG_SERVICES_PATH + "/")) {
        path = path.substring(CLUSTER_CONFIG_SERVICES_PATH.length() + 1);
        if (path.endsWith(SUFFIX)) {
          path = path.substring(0, path.length() - SUFFIX.length());
          if (path.indexOf('/') == -1) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT("Service " + path + " configuration changed. Add service to the target list");
            }
            targetServiceNames.add(path);
          }
        }
      }
    }
    //2. Fire event for each service
    for (String serviceName : targetServiceNames) {
      //get service wrapper and runtime configuration
      ServiceWrapper serviceWrapper = memoryContainer.getServices().get(serviceName);
      if (serviceWrapper == null) {
        if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.krn_srv.000044", "Unable to get service {0}. No such service", serviceName);
        }
        continue;
      }
      RuntimeConfiguration runtimeConfiguration = serviceWrapper.getRuntimeConfiguration();
      NestedProperties currentProperties = serviceWrapper.getCurrentProperties();
      NestedProperties onlineModifiable;
      try {
        onlineModifiable = memoryContainer.getPersistentContainer().getComponentProperties(serviceName, false, true);
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Online modifiable properties set for service " + serviceName + ": " + onlineModifiable);
        }
      } catch (ServiceException e) {
        if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
          SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "Unable to apply updated properties to service [" + serviceName + "]. Reported reason: [" + e.getMessage() + "]", e);
        }
        continue;
      }
      //init updated properties
      NestedProperties updatedProperties;
      if (currentProperties == null) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Service " + serviceName + " current properties are not initialized!");
        }
        updatedProperties = onlineModifiable;
      } else {
        updatedProperties = new NestedProperties();
        initUpdatedProperties(currentProperties, onlineModifiable, updatedProperties, "service", runtimeConfiguration == null);
      }
      updatedProperties.normalize();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Runtime configuration for service " + serviceName + ": " + runtimeConfiguration);
        LOCATION.debugT("Updated properties set for service " + serviceName + ": " + updatedProperties);
      }
      //apply updated properties
      if (updatedProperties.size() > 0 || updatedProperties.getAllNestedPropertiesKeys().length > 0) {
        if (runtimeConfiguration != null) {
          try {
            runtimeConfiguration.updateProperties(updatedProperties);
            if (LOCATION.beDebug()) {
              LOCATION.debugT("Properties to service " + serviceName + " updated.");
            }
          } catch (ServiceException e) {
            if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
              SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "Unable to apply updated properties to service [" + serviceName + "]. Reported reason: [" + e.getMessage() + "]", e);
            }
          }
        } else {
          //log error only if target service is started
          if ((serviceWrapper.getStatus() == ComponentMonitor.STATUS_ACTIVE) && SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
            SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.krn_srv.000045", "No runtime configuration available for the service [{0}]. Unable to apply updated properties", serviceName);
          }
        }
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("No online modifiable properties of service " + serviceName + " is changed. Nothing updated.");
        }
      }
    }
  }

  private void processManagersConfigurationChange(ChangeEvent event) {
    Set<String> targetManagerNames = new HashSet<String>(1);
    //1. Iterate detailed events and find target manager names.
    ChangeEvent[] events = event.getDetailedChangeEvents();
    for (ChangeEvent e : events) {
      String path = e.getPath();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Received configuration change event for path [" + path + "]");
      }
      if (path.startsWith(CLUSTER_CONFIG_MANAGERS_PATH + "/")) {
        path = path.substring(CLUSTER_CONFIG_MANAGERS_PATH.length() + 1);
        if (path.endsWith(SUFFIX)) {
          path = path.substring(0, path.length() - SUFFIX.length());
          if (path.indexOf('/') == -1) {
            String managerName = path + "Manager";
            if (LOCATION.beDebug()) {
              LOCATION.debugT("Manager " + managerName + " configuration changed. Add manager to the target list");
            }
            targetManagerNames.add(managerName);
          }
        }
      }
    }
    //2. Fire event for each manager
    for (String managerName : targetManagerNames) {
      //get manager object
      Manager manager = Framework.getManager(managerName);
      if (manager == null) {
        if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.krn_srv.000062", "Unable to get manager {0}. No such manager", managerName);
        }
        continue;
      }
      //get current from hash map & online modifiable from DB
      NestedProperties currentProperties = getManagerCurrentProperties(managerName);
      NestedProperties onlineModifiable;
      try {
        onlineModifiable = memoryContainer.getPersistentContainer().getComponentProperties(managerName, true, true);
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Online modifiable properties set for manager " + managerName + ": " + onlineModifiable);
        }
      } catch (ServiceException e) {
        if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
          SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "Unable to apply updated properties to manager [" + managerName + "]. Reported reason: [" + e.getMessage() + "]", e);
        }
        continue;
      }
      //init updated properties
      NestedProperties updatedProperties;
      if (currentProperties == null) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Manager " + managerName + " current properties are not initialized!");
        }
        updatedProperties = onlineModifiable;
      } else {
        updatedProperties = new NestedProperties();
        initUpdatedProperties(currentProperties, onlineModifiable, updatedProperties, "manager", false);
      }
      updatedProperties.normalize();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Updated properties set for manager " + managerName + ": " + updatedProperties);
      }
      //apply updated properties
      if (updatedProperties.size() > 0 || updatedProperties.getAllNestedPropertiesKeys().length > 0) {
        try {
          manager.updateProperties((Properties) updatedProperties.clone());
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Properties to manager " + managerName + " updated.");
          }
          if (currentProperties != null) {
            applyChanges(currentProperties, updatedProperties);//keep manager properties consistent
          }
        } catch (IllegalArgumentException e) {
          if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
            SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "Unable to apply updated properties to manager [" + managerName + "]. Reported reason: [" + e.getMessage() + "]", e);
          }
        }
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("No online modifiable properties of manager " + managerName + " is changed. Nothing updated.");
        }
      }
    }
  }

  private void initUpdatedProperties(NestedProperties current, NestedProperties online, NestedProperties updated, String componentType, boolean updateCurrent) {
    for (Object keyObj : online.keySet()) {
      String key = (String) keyObj;
      String value1 = online.getProperty(key);
      String value2 = current.getProperty(key);
      if (value2 == null) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Property [" + key + "] found in the database but not in the [" + componentType + "] runtime properties. Ignoring.");
        }
      } else if (value1.equals(value2)) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Property [" + key + "] is equal to the [" + componentType + "] runtime property (no change). Ignoring.");
        }
      } else {
        updated.setProperty(key, value1);
        if (updateCurrent) {
          current.setProperty(key, value1); //keep current properties consistent! (for services only)
        }
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Property [" + key + "] for [" + componentType + "] has changed: old value [" + value2 + "], new value [" + value1 + "]");
        }
      }
    }
    //process nested entries
    String[] keys = online.getAllNestedPropertiesKeys();
    for (String key : keys) {
      NestedProperties props = current.getNestedProperties(key);
      if (props != null) {
        initUpdatedProperties(props, online.getNestedProperties(key), updated.getNestedProperties(key, true), componentType, updateCurrent);
      } else {
        LOCATION.debugT("NestedProperty [" + key + "] found in the database but not in the [" + componentType + "] runtime properties. Ignoring.");
      }
    }
  }

  public static void applyChanges(NestedProperties current, Properties updated) {
    for (Object keyObj : updated.keySet()) {
      String key = (String) keyObj;
      current.setProperty(key, updated.getProperty(key)); //keep current properties consistent!
    }
    if (updated instanceof NestedProperties) {
      //process nested levels
      NestedProperties updatedNested = (NestedProperties) updated;
      String[] nestedKeys = updatedNested.getAllNestedPropertiesKeys();
      for (String key : nestedKeys) {
        //current must have 'key' nested property
        applyChanges(current.getNestedProperties(key), updatedNested.getNestedProperties(key));
      }
    }
  }

  /**
   * Initialize manager properties
   *
   * @param managerName manager name
   * @return current manager properties or <code>null</code> if any error occurs
   */
  private NestedProperties getManagerCurrentProperties(String managerName) {
    NestedProperties currentProperties;
    synchronized (CoreMonitorImpl.managersCurrentProperties) {
      currentProperties = CoreMonitorImpl.managersCurrentProperties.get(managerName);
      if (currentProperties == null) {
        try {
          currentProperties = memoryContainer.getPersistentContainer().getComponentProperties(managerName, true, false);
          CoreMonitorImpl.managersCurrentProperties.put(managerName, currentProperties);
        } catch (ServiceException e) {
          LOCATION.traceThrowableT(Severity.WARNING, "getManagerCurrentProperties(" + managerName + ")", e);
        }
      }
      return currentProperties;
    }
  }

}