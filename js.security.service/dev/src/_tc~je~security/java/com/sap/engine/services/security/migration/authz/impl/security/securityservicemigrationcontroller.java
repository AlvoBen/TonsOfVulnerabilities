package com.sap.engine.services.security.migration.authz.impl.security;

import java.util.Hashtable;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.security.migration.MigrationFramework;
import com.sap.engine.services.security.migration.SecurityMigrationController;
import com.sap.engine.services.security.migration.authz.J2EERolesToUMERolesMigrationController;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;

public class SecurityServiceMigrationController implements SecurityMigrationController {
  private static final String MIGRATION_ENTRY_KEY = "migrated";
  private static final String MIGRATION_ENTRY_VALUE = "true";
  
  J2EERolesToUMERolesMigrationController serverRolesController = null;

  ResourceMigrationController cryptoController = null;
  ResourceMigrationController authController = null;
  ResourceMigrationController domainsController = null;  
  ResourceMigrationController umController = null;
     
  public SecurityServiceMigrationController() {
    serverRolesController = new ServerRolesMigrationController();
    
    cryptoController = new CryptoResourceMigrationController();
    authController = new AuthResourceMigrationController();
    domainsController = new DomainsResourceMigrationController();
    umController = new UMResourceMigrationController();        
  }

  public void migrate() throws Exception {
    serverRolesController.migrate();
    Hashtable umeRoles = serverRolesController.getResultMappings();
    
    cryptoController.init(umeRoles);    
    cryptoController.migrate();
    
    authController.init(umeRoles);
    authController.migrate();
    
    domainsController.init(umeRoles);
    domainsController.migrate();
    
    umController.init(umeRoles);
    umController.migrate();
    
    migrationFinished();
  }


  public boolean needsMigration() {
    ConfigurationHandler configHandler = null;
    
    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      String configurationPath = "security";
      Configuration configRolesConfiguration = configHandler.openConfiguration(configurationPath, ConfigurationHandler.READ_ACCESS);
      String migrationKey = (String) configRolesConfiguration.getConfigEntry(MIGRATION_ENTRY_KEY);
      return (!migrationKey.equals(MIGRATION_ENTRY_VALUE));
    } catch (ConfigurationException e) {
      e.printStackTrace();
      return true;
    } finally {
      if (configHandler != null) {
        try {
          configHandler.closeAllConfigurations();
        } catch (ConfigurationException e1) {
          e1.printStackTrace();
        }
      }
    }  
  }

  private void migrationFinished() {
    ConfigurationHandler configHandler = null;
    
    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      String configurationPath = "security";
      Configuration configRolesConfiguration = configHandler.openConfiguration(configurationPath, ConfigurationHandler.WRITE_ACCESS);
      configRolesConfiguration.addConfigEntry(MIGRATION_ENTRY_KEY, MIGRATION_ENTRY_VALUE);
      configHandler.commit();
    } catch (ConfigurationException e) {
      e.printStackTrace();
      if (configHandler != null) {
        try {
          configHandler.rollback();
        } catch (ConfigurationException e1) {
          e.printStackTrace();
        }
      }     
    } finally {
      if (configHandler != null) {
        try {
          configHandler.closeAllConfigurations();
        } catch (ConfigurationException e) {
          e.printStackTrace();
        }
      }
    } 
  }
}
