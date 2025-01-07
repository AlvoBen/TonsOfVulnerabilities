package com.sap.engine.tools.offlinedeploy.rdb;

import java.io.*;
import java.util.*;

/**
 * Object that holds all component properties and represents serializable Service Container repository
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class SCRepository implements Serializable {

  static final long serialVersionUID = -5766632437801409463L;

  static final String INTERFACES = "interface";
  static final String LIBRARIES = "libraries";
  static final String SERVICES = "services";

  private final Properties components = new Properties();

  public SCRepository() {
    components.put(INTERFACES, new Properties());
    components.put(LIBRARIES, new Properties());
    components.put(SERVICES, new Properties());
  }

  /**
   * Returns provider properties of a component or <code>null</code> if not exists.
   *
   * @param name name of the component
   * @param type OfflineComponentDeploy.TYPE_INTERFACE, OfflineComponentDeploy.TYPE_LIBRARY or
   * OfflineComponentDeploy.SERVICE are valid types
   * @return provider properties or <code>null</code>  if not exist
   */
  public Properties getComponentProviderProperties(String name, byte type) {
    return (Properties) getComponentsByType(type).get(name);
  }

  /**
   * Returns component by type
   *
   * @param type OfflineComponentDeploy.TYPE_INTERFACE, OfflineComponentDeploy.TYPE_LIBRARY or
   * OfflineComponentDeploy.SERVICE are valid types
   * @return Properties object containing [component name, component provider properties] pairs
   */
  public Properties getComponentsByType(byte type) {
    switch (type) {
      case OfflineComponentDeploy.TYPE_INTERFACE : {
        return (Properties) components.get(INTERFACES);
      }
      case OfflineComponentDeploy.TYPE_LIBRARY : {
        return (Properties) components.get(LIBRARIES);
      }
      case OfflineComponentDeploy.TYPE_SERVICE : {
        return (Properties) components.get(SERVICES);
      }
      default : return null;
    }
  }

  /**
   * Set component provider properties.
   *
   * @param name name of the component
   * @param type OfflineComponentDeploy.TYPE_INTERFACE, OfflineComponentDeploy.TYPE_LIBRARY or
   * OfflineComponentDeploy.SERVICE are valid types
   * @param providerProperties properties to be set
   * @exception DeploymentException if reference cycle detected
   */
  public void setComponentProviderProperties(String name, byte type, Properties providerProperties) throws DeploymentException {
    getComponentsByType(type).put(name, providerProperties);
    ReferenceCyclesDetector.checkForReferenceCycles(components, name, type, providerProperties);
  }

  /**
   * Remove component provider properties.
   *
   * @param name name of the component
   * @param type OfflineComponentDeploy.TYPE_INTERFACE, OfflineComponentDeploy.TYPE_LIBRARY or
   * OfflineComponentDeploy.SERVICE are valid types
   */
  public void removeComponentProviderProperties(String name, byte type) {
    getComponentsByType(type).remove(name);
  }

}