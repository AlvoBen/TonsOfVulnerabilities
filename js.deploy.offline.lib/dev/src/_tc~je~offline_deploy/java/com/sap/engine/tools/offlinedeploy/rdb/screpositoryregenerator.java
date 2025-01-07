package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.frame.core.configuration.*;
import java.util.Properties;

/**
 * Regenerate Service Container Repository if file cluster_config/global/clusternode_config/workernode/components.properties
 * doesn't exist.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class SCRepositoryRegenerator implements Constants {

  /**
   * Regenerate SCRepository
   *
   * @param cfg - root cfg
   * @return SCRepository regenerated repository
   * @throws ConfigurationException - if DB error ocurs
   * @throws DeploymentException - if reference cycle detected
   */
  public static SCRepository regenerate(Configuration cfg) throws ConfigurationException, DeploymentException {
    //if configuration is inherited go to root (cluster_config/global/clusternode_config/workernode)
    while ((cfg.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) == Configuration.CONFIG_TYPE_DERIVED) {
      cfg = ((DerivedConfiguration) cfg).getLinkedConfiguration();
    }
    //create empty repository
    SCRepository scr = new SCRepository();
    //add interfaces
    if (cfg.existsSubConfiguration(INTERFACE_BASE)) {
      Configuration root = cfg.getSubConfiguration(INTERFACE_BASE);
      String[] names = root.getAllSubConfigurationNames();
      for (String name : names) {
        Configuration tmp = root.getSubConfiguration(name);
        if (tmp.existsSubConfiguration(PROVIDER)) {
          tmp = tmp.getSubConfiguration(PROVIDER);
          if ((tmp.getConfigurationType() & Configuration.CONFIG_TYPE_PROPERTYSHEET) == Configuration.CONFIG_TYPE_PROPERTYSHEET) {
            Properties providerProperties = tmp.getPropertySheetInterface().getProperties();
            scr.setComponentProviderProperties(name, OfflineComponentDeploy.TYPE_INTERFACE, providerProperties);
          }
        }
      }
    }
    //add libraries
    if (cfg.existsSubConfiguration(LIBRARY_BASE)) {
      Configuration root = cfg.getSubConfiguration(LIBRARY_BASE);
      String[] names = root.getAllSubConfigurationNames();
      for (String name : names) {
        Configuration tmp = root.getSubConfiguration(name);
        if (tmp.existsSubConfiguration(PROVIDER)) {
          tmp = tmp.getSubConfiguration(PROVIDER);
          if ((tmp.getConfigurationType() & Configuration.CONFIG_TYPE_PROPERTYSHEET) == Configuration.CONFIG_TYPE_PROPERTYSHEET) {
            Properties providerProperties = tmp.getPropertySheetInterface().getProperties();
            scr.setComponentProviderProperties(name, OfflineComponentDeploy.TYPE_LIBRARY, providerProperties);
          }
        }
      }
    }
    //add services
    if (cfg.existsSubConfiguration(SERVICE_BASE)) {
      Configuration root = cfg.getSubConfiguration(SERVICE_BASE);
      String[] names = root.getAllSubConfigurationNames();
      for (String name : names) {
        Configuration tmp = root.getSubConfiguration(name);
        if (tmp.existsSubConfiguration(PROVIDER)) {
          tmp = tmp.getSubConfiguration(PROVIDER);
          if ((tmp.getConfigurationType() & Configuration.CONFIG_TYPE_PROPERTYSHEET) == Configuration.CONFIG_TYPE_PROPERTYSHEET) {
            Properties providerProperties = tmp.getPropertySheetInterface().getProperties();
            scr.setComponentProviderProperties(name, OfflineComponentDeploy.TYPE_SERVICE, providerProperties);
          }
        }
      }
    }
    return scr;
  }

}