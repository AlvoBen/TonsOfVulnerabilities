package com.sap.engine.services.security.migration.authz.impl.keystore;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.security.migration.MigrationFramework;
import com.sap.engine.services.security.migration.SecurityMigrationController;

public class KeystoreMigrationController implements SecurityMigrationController {
  public static final String ROOT_CONFIG = "keystore/$$$user-based-security-root$$$";
 
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
    } catch (NameNotFoundException _) {
      return; // nothing to migrate 
    } catch (ConfigurationException ce) {
      throw new SecurityException(ce.getMessage());
    } finally {
      if (configHandler != null) {
        configHandler.closeAllConfigurations();
      }
    } 
          
    if (entries != null && entries.length > 0) {
      KeystoreViewMigrationController viewController = null;
      for (int i = 0; i < entries.length; i++) {
        viewController = new KeystoreViewMigrationController(entries[i], ROOT_CONFIG + '/' + entries[i] + '/');
        viewController.migrate();
      }
    }
    
    migrationFinished();    
  }

  public boolean needsMigration() {
    ConfigurationHandler configHandler = null;
    
    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      String configurationPath = ROOT_CONFIG;
      Configuration configRolesConfiguration = null;
      try {
        configRolesConfiguration = configHandler.openConfiguration(configurationPath, ConfigurationHandler.READ_ACCESS);
      } catch (NameNotFoundException _) {
        return false;
      }
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
        } catch (ConfigurationException e1) {
          //$JL-EXC$
        }
      } 
    }
  }
}
