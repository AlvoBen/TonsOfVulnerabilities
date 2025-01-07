/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.patch;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.services.security.server.AuthenticationContextImpl;

import java.util.Hashtable;

public class Change3 implements Change {

  private Hashtable loginModuleNames = new Hashtable();

  public void run() throws Exception {
    ConfigurationHandler configHandler = null;

    loginModuleNames.put("com.sap.engine.services.security.server.jaas.TicketLoginModule", "com.sap.engine.services.security.server.jaas.SecuritySessionLoginModule");
    loginModuleNames.put("com.sap.engine.services.userstore.jaas.BasicPasswordLoginModule", "com.sap.engine.services.security.server.jaas.BasicPasswordLoginModule");
    loginModuleNames.put("com.sap.engine.services.userstore.jaas.ClientCertLoginModule", "com.sap.engine.services.security.server.jaas.ClientCertLoginModule");
    loginModuleNames.put("com.sap.engine.services.userstore.jaas.DigestLoginModule", "com.sap.engine.services.security.server.jaas.DigestLoginModule");

    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();

    modifyUserstoreConfiguration(configHandler);
    modifyPolicyConfigurations(configHandler);
  }

  private Object[] findConfiguationPaths(ConfigurationHandler configHandler) throws Exception {
    try {
      Configuration configRead = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, ConfigurationHandler.READ_ACCESS);
      return configRead.getAllConfigEntries().values().toArray();
    } catch (Exception e) {
      try {
        configHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
      throw e;
    } finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
  }

  private void modifyPolicyConfigurations(ConfigurationHandler configHandler) throws Exception {
    Object[] entries = findConfiguationPaths(configHandler);

    for (int i = 0; i < entries.length; i ++) {
      try {
        Configuration configWrite = configHandler.openConfiguration((String) entries[i], ConfigurationHandler.WRITE_ACCESS);

        if ((configWrite != null) && configWrite.existsSubConfiguration("security")) {
          Configuration securityConfig = configWrite.getSubConfiguration("security");

          if (securityConfig.existsSubConfiguration("authentication")) {
            Configuration subConfig = securityConfig.getSubConfiguration("authentication");

            if (!subConfig.existsConfigEntry(AuthenticationContextImpl.TEMPLATE_ENTRY_KEY)) {
              Object[] configs = subConfig.getAllSubConfigurations().values().toArray();

              for (int j = 0; j < configs.length; j++) {
                int k = 0;
                String module = new Integer(k).toString();
                Configuration config = ((Configuration) configs[j]);

                while (config.existsSubConfiguration(module)) {
                  Configuration loginModule = config.getSubConfiguration(module);
                  Object moduleName = loginModule.getConfigEntry("classname");
                  Object newName = loginModuleNames.get(moduleName);

                  if (newName != null) {
                    loginModule.modifyConfigEntry("classname", newName);
                  }

                  k++;
                  module = new Integer(k).toString();
                }
              }
            }
          }
        }

        configHandler.commit();
      } catch (Exception e) {
        try {
          configHandler.rollback();
        } catch (Exception re) {
          throw re;
        }
        throw e;
      } finally {
        try {
          configHandler.closeAllConfigurations();
        } catch (Exception e) {
          throw e;
        }
      }
    }
  }

  private void modifyUserstoreConfiguration(ConfigurationHandler configHandler) throws Exception {
    try {
      Configuration configWrite = configHandler.openConfiguration(SecurityConfigurationPath.USERSTORES_PATH, ConfigurationHandler.WRITE_ACCESS);

      if (configWrite != null) {
        Object[] userStores = configWrite.getAllSubConfigurations().values().toArray();

        for (int i = 0; i < userStores.length; i++) {
          Configuration userStore = (Configuration) userStores[i];

          if (userStore.existsSubConfiguration("login-module")) {
            Configuration loginModules = userStore.getSubConfiguration("login-module");
            Object[] subConfigurations =  loginModules.getAllSubConfigurations().values().toArray();

            for (int j = 0; j < subConfigurations.length; j++) {
              Configuration subConfig = (Configuration) subConfigurations[j];
              Object moduleName = subConfig.getConfigEntry("class-name");
              Object newName = loginModuleNames.get(moduleName);

              if (newName != null) {
                subConfig.modifyConfigEntry("class-name", newName);
              }
            }
          }
        }
      }

      configHandler.commit();
    } catch (Exception e) {
      try {
        configHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
      throw e;
    } finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
  }

}