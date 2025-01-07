package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.frame.core.configuration.*;

/**
 * This class provides configuration structure verification. This check must not be done at runtime when
 * offline component deploy is used for library deployment.
 */
public class StructureConsistentcy implements Constants {

  private static boolean checkPerformed = false;

  /**
   * Verify configuration structure and create it if necessary. Only offline deploy sould verify the structure
   * on first deploy invocation.
   *
   * @throws ConfigurationException - if any error occur.
   */
  static void checkStructureConsistentcy(ConfigurationHandler handler) throws ConfigurationException {
    if (!checkPerformed) {
      checkPerformed = true;
      Configuration root;
      //Cluster config creation
      try {
        root = handler.openConfiguration(CLUSTER_CONFIG, ConfigurationHandler.WRITE_ACCESS, true);
      } catch (NameNotFoundException e) {
        // $JL-EXC$ create cfg root if not exist
        root = handler.createRootConfiguration(CLUSTER_CONFIG);
      }
      //cluster_config/globals creation
      Configuration globals;
      if (!root.existsSubConfiguration(GLOBALS)) {
        globals = root.createSubConfiguration(GLOBALS);
      } else {
        globals = root.getSubConfiguration(GLOBALS);
      }
      //creation of cluster_config/globals/bin and all the sub-levels of bin
      Configuration tmp;
      if (!globals.existsSubConfiguration(BIN)) {
        tmp = globals.createSubConfiguration(BIN);
      } else {
        tmp = globals.getSubConfiguration(BIN);
      }
      if (!tmp.existsSubConfiguration(BOOTSTRAP)) {
        tmp.createSubConfiguration(BOOTSTRAP, Configuration.CONFIG_TYPE_INDEXED);
      }
      if (!tmp.existsSubConfiguration(NATIVE)) {
        tmp.createSubConfiguration(NATIVE);
      }
      if (!tmp.existsSubConfiguration(RUNTIME)) {
        tmp.createSubConfiguration(RUNTIME, Configuration.CONFIG_TYPE_INDEXED);
      }
      //creation of cluster_config/globals/clusternode_config and all the sub-levels of clusternode_config
      if (!globals.existsSubConfiguration(CLUSTERNODE_CONFIG)) {
        tmp = globals.createSubConfiguration(CLUSTERNODE_CONFIG);
      } else {
        tmp = globals.getSubConfiguration(CLUSTERNODE_CONFIG);
      }
      if (!tmp.existsSubConfiguration(WORKERNODE)) {
        tmp = tmp.createSubConfiguration(WORKERNODE);
      } else {
        tmp = tmp.getSubConfiguration(WORKERNODE);
      }
      if (!tmp.existsSubConfiguration(APPS)) {
        tmp.createSubConfiguration(APPS);
      }
      //creation of cluster_config/undeploy
      if (!globals.existsSubConfiguration(UNDEPLOY)) {
        globals.createSubConfiguration(UNDEPLOY);
      }
      //cration of cluster_config/templates
      Configuration templates;
      if (!root.existsSubConfiguration(TEMPLATES)) {
        templates = root.createSubConfiguration(TEMPLATES);
      } else {
        templates = root.getSubConfiguration(TEMPLATES);
      }
      //creation of cluster_config/templates/base and the standard instance structure inside
      if (!templates.existsSubConfiguration(BASE)) {
        tmp = templates.createSubConfiguration(BASE);
      } else {
        tmp = templates.getSubConfiguration(BASE);
      }
      if (!tmp.existsSubConfiguration(STANDARD_INSTANCE)) {
        tmp = tmp.createSubConfiguration(STANDARD_INSTANCE);
      } else {
        tmp = tmp.getSubConfiguration(STANDARD_INSTANCE);
      }
      if (!tmp.existsSubConfiguration(BIN)) {
        DerivedConfiguration derived = (DerivedConfiguration) tmp.createSubConfiguration(BIN, Configuration.CONFIG_TYPE_DERIVED_READ_ONLY);
        derived.setLink(globals.getPath() + "/" + BIN);
      }
      if (!tmp.existsSubConfiguration(CFG)) {
        DerivedConfiguration derived = (DerivedConfiguration) tmp.createSubConfiguration(CFG, Configuration.CONFIG_TYPE_DERIVED);
        derived.setLink(globals.getPath() + "/" + CLUSTERNODE_CONFIG + "/" + WORKERNODE);
      }
      if (!tmp.existsConfigEntry(NUMBER_OF_NODES)) {
        tmp.addConfigEntry(NUMBER_OF_NODES, DEFAULT_NUMBER_OF_NODES_VALUE, Configuration.ENTRY_TYPE_COMPUTED | Configuration.ENTRY_TYPE_PARAMETERIZED | Configuration.ENTRY_TYPE_CONTAINS_LINK);
      } else {
        tmp.modifyConfigEntry(NUMBER_OF_NODES, DEFAULT_NUMBER_OF_NODES_VALUE);
      }
      if (!tmp.existsConfigEntry(INSTANCE_TYPE)) {
        tmp.addConfigEntry(INSTANCE_TYPE, DEFAULT_INSTANCE_TYPE_VALUE);
      }
      //creation of cluster_config/templates/default
      if (!templates.existsSubConfiguration(DEFAULT_TEMPLATES)) {
        templates.createSubConfiguration(DEFAULT_TEMPLATES);
      }
      //cluster_config/system creation
      Configuration system;
      if (!root.existsSubConfiguration(SYSTEM)) {
        system = root.createSubConfiguration(SYSTEM);
      } else {
        system = root.getSubConfiguration(SYSTEM);
      }
      //creation of cluster_config/system/cluster_global custom global level settings
      if (!system.existsSubConfiguration(CUSTOM_GLOBAL)) {
        DerivedConfiguration derived = (DerivedConfiguration) system.createSubConfiguration(CUSTOM_GLOBAL, Configuration.CONFIG_TYPE_DERIVED);
        derived.setLink(CUSTOM_GLOBAL_LINK);
      }
      //cluster_config/system/custom_templates
      if (!system.existsSubConfiguration(CUSTOM_TEMPLATES)) {
        system.createSubConfiguration(CUSTOM_TEMPLATES);
      }
      //cluster_config/system/instances
      if (!system.existsSubConfiguration(INSTANCES)) {
        tmp = system.createSubConfiguration(INSTANCES);
      } else {
        tmp = system.getSubConfiguration(INSTANCES);
      }
      //cluster_config/system/instances/current_instance
      if (!tmp.existsSubConfiguration(CURRENT_INSTANCE)) {
        DerivedConfiguration derived = (DerivedConfiguration) system.getSubConfiguration(INSTANCES).createSubConfiguration(CURRENT_INSTANCE, Configuration.CONFIG_TYPE_DERIVED_READ_ONLY);
        derived.setLink(CURRENT_INSTANCE_LINK);
      }
      //src.zip creation
      try {
        handler.openConfiguration(SRC_ZIP, ConfigurationHandler.WRITE_ACCESS, true);
      } catch (NameNotFoundException e) {
        // $JL-EXC$ create src.zip root if not exist
        handler.createRootConfiguration(SRC_ZIP);
      }
    } else {
      //open root to avoid configuration intersection errors
      handler.openConfiguration(CLUSTER_CONFIG, ConfigurationHandler.WRITE_ACCESS, true);
    }
  }

}