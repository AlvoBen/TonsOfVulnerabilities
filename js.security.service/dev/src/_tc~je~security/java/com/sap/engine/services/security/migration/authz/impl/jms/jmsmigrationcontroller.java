package com.sap.engine.services.security.migration.authz.impl.jms;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.security.migration.MigrationFramework;
import com.sap.engine.services.security.migration.SecurityMigrationController;

public class JMSMigrationController implements SecurityMigrationController{
  public static final String ROOT_CONFIG = "jms_provider";

  private static final String MIGRATION_ENTRY_KEY = "migrated";
  private static final String MIGRATION_ENTRY_VALUE = "true";
    
  public void migrate() throws Exception {
    String configurationPath = ROOT_CONFIG;
    String[] entries = null;
    
    ConfigurationHandler configHandler = null;
    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      Configuration config = configHandler.openConfiguration(configurationPath, ConfigurationHandler.READ_ACCESS);
      entries = config.getAllSubConfigurationNames();
    } catch (ConfigurationException ce) {
      throw new SecurityException(ce.getMessage());
    } finally {
      if (configHandler != null) {
        configHandler.closeAllConfigurations();
      }
    } 
          
    if (entries != null && entries.length > 0) {
      VirtualProviderMigrationController vp = null;
      for (int i = 0; i < entries.length; i++) {
        if (entries[i].equals("data")) {
          continue;
        }
        vp = new VirtualProviderMigrationController(entries[i]);
        vp.migrate();
      }
    }
    
    migrationFinished();
  }

  public boolean needsMigration() {
    ConfigurationHandler configHandler = null;
    
    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      String configurationPath = ROOT_CONFIG;
      Configuration configRolesConfiguration = configHandler.openConfiguration(configurationPath, ConfigurationHandler.READ_ACCESS);
      String migrationKey = (String) configRolesConfiguration.getConfigEntry(MIGRATION_ENTRY_KEY);
      return (!migrationKey.equals(MIGRATION_ENTRY_VALUE));
    } catch (ConfigurationException e) {
      return true;
    } finally {
      if (configHandler != null) {
        try {
          configHandler.closeAllConfigurations();
        } catch (ConfigurationException e1) {
          //$JL-EXC$
        }
      }
    }  
  }

  private void migrationFinished() {
    ConfigurationHandler configHandler = null;
    
    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      String configurationPath = ROOT_CONFIG;
      Configuration configRolesConfiguration = configHandler.openConfiguration(configurationPath, ConfigurationHandler.WRITE_ACCESS);
      configRolesConfiguration.addConfigEntry(MIGRATION_ENTRY_KEY, MIGRATION_ENTRY_VALUE);
      configHandler.commit();
    } catch (ConfigurationException e) {
      if (configHandler != null) {
        try {
          configHandler.rollback();
        } catch (ConfigurationException e1) {
          //$JL-EXC$
        }
      }     
    } finally {
      if (configHandler != null) {
        try {
          configHandler.closeAllConfigurations();
        } catch (ConfigurationException e) {
          //$JL-EXC$
        }
      }
    }  
  }
}
