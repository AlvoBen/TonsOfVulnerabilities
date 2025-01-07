package com.sap.engine.tools.offlinedeploy.rdb;

import java.util.Properties;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Detect reference cycles in Service Container Repository
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ReferenceCyclesDetector {

  /**
   * Check for cycle in component references and throws exception if such exists.
   * Unfortunately possible cycles with double named interface components will be missed.
   *
   * @param components - provider properties of already deployed components
   * @param name - runtime name of the deploying component
   * @param type - type of deploying component
   * @param providerProperties - provider properties of deploying component
   * @exception DeploymentException - if cycle found
   */
  static void checkForReferenceCycles(Properties components, String name, byte type, Properties providerProperties) throws DeploymentException {
    String componentType = null;
    //convert byte type to string
    switch(type) {
      case OfflineComponentDeploy.TYPE_INTERFACE : {
        componentType = "interface";
      } break;
      case OfflineComponentDeploy.TYPE_LIBRARY : {
        componentType = "library";
      } break;
      case OfflineComponentDeploy.TYPE_SERVICE : {
        componentType = "service";
      } break;
    }
    //visited components
    HashSet<String> visited = new HashSet<String>();
    //hold cycle
    ArrayList<String> path = new ArrayList<String>();
    check(components, name, componentType, providerProperties, visited, path);
  }

  private static void check(Properties components, String name, String type, Properties providerProperties, HashSet<String> visited, ArrayList<String> path) throws DeploymentException {
    String typeAndName = type + ':' + name;
    if (path.size() > 0 && path.get(0).equals(typeAndName)) {
      String cycle = getPathAsString(path);
      throw new DeploymentException("Attempt to introduce a cyclic component dependency with component [" + path.get(0) + "] : [" + cycle + "]");
    } else if (visited.contains(typeAndName)) {
      //component is already traversed
      return;
    }
    path.add(typeAndName);
    visited.add(typeAndName);
    String refs = providerProperties.getProperty("references");
    if (refs != null) {
      int count = Integer.parseInt(refs);
      for (int i = 0; i < count; i++) {
        String refName = providerProperties.getProperty("reference_name_" + i);
        String refType = providerProperties.getProperty("reference_type_" + i);
        String refProviderName = providerProperties.getProperty("reference_provider-name_" + i);
        String runtimeRefName = Utils.modifyComponentName(refName, refProviderName);
        //avoid reference to itself
        if (runtimeRefName.equals(name) && refType.equals(type)) {
          continue;
        }
        Properties refProviderProperties = getProviderProperties(components, runtimeRefName, refType);
        if (refProviderProperties != null) {
          check(components, runtimeRefName, refType, refProviderProperties, visited, path);
        }
      }
    }
    path.remove(path.size() - 1);
  }

  /**
   * Returns ref component provider properties or null if not such not exists
   */
  private static Properties getProviderProperties(Properties components, String runtimeRefName, String refType) {
    Properties tmp = null;
    if (refType.equals("interface")) {
      tmp = (Properties) components.get(SCRepository.INTERFACES);
    } else if (refType.equals("library")) {
      tmp = (Properties) components.get(SCRepository.LIBRARIES);
    } else if (refType.equals("service")) {
      tmp = (Properties) components.get(SCRepository.SERVICES);
    }
    return (tmp == null) ? tmp : (Properties) tmp.get(runtimeRefName);
  }

  private static String getPathAsString(ArrayList<String> path) {
    StringBuilder buffer = new StringBuilder(path.size() * 25);
    for (String element : path) {
      buffer.append(element);
      buffer.append(" -> ");
    }
    buffer.append(path.get(0));
    return buffer.toString();
  }

}