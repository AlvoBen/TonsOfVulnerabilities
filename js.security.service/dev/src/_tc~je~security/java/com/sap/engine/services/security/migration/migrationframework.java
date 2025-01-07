package com.sap.engine.services.security.migration;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;

public class MigrationFramework {
  static ConfigurationHandler configHandler = null;
  
  public static void setConfigurationHandlerFactory(ConfigurationHandlerFactory configFactory) throws Exception {
  	configHandler = configFactory.getConfigurationHandler();
  }
  
  public static ConfigurationHandler getConfigurationHandler() {
    return configHandler;
  }
    
  public static void startMigration() {
    if (!UserManagementUtil.init()) {
      return;
    }

    String[] controllers = getMigrationControllerNames();
    for (int i = 0; i < controllers.length; i++) {
      SecurityMigrationController migrator = null;
      try {
        migrator = (SecurityMigrationController) Class.forName(controllers[i]).newInstance();
      } catch (InstantiationException e) {
        e.printStackTrace();
        //log
      } catch (IllegalAccessException e) {
        e.printStackTrace();
//      log
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
//      log
      } catch (NoClassDefFoundError err) {
        err.printStackTrace();
//      log
      }
      
      try {
        if (migrator.needsMigration()) {
          migrator.migrate();
        }
      } catch (Exception e) {
        e.printStackTrace();
        //log
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
  
  // dummy implementation
  private static String[] getMigrationControllerNames() {
    return new String[] {
      "com.sap.engine.services.security.migration.authz.impl.security.SecurityServiceMigrationController",
      "com.sap.engine.services.security.migration.authz.impl.TelnetMigrationController",
      "com.sap.engine.services.security.migration.authz.impl.NamingMigrationController",
      "com.sap.engine.services.security.migration.authz.impl.jms.JMSMigrationController",
      "com.sap.engine.services.security.migration.authz.impl.keystore.KeystoreMigrationController"
    };
  }
}
