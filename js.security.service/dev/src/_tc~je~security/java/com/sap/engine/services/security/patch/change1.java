package com.sap.engine.services.security.patch;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.interfaces.security.SecurityRoleContext;

import java.util.Map;
import java.util.Iterator;

public class Change1 implements Change {
  private static final String TELNET_LOGIN_ROLE = "telnet_login";
  private final static String TELNET_POLICY_CONFIGURATION = "service.telnet";

  public void run() throws Exception {
    ConfigurationHandler configHandler = null;

    try {
      configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
      Configuration configRead = configHandler.openConfiguration(SecurityConfigurationPath.ROLES_PATH, ConfigurationHandler.READ_ACCESS);
      Configuration configWrite = null;
      try {
        configWrite = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + "/" + TELNET_POLICY_CONFIGURATION + "/" + SecurityConfigurationPath.ROLES_PATH, ConfigurationHandler.READ_ACCESS);
        return;
      } catch (NameNotFoundException nnfe) {
        configWrite = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + "/" + TELNET_POLICY_CONFIGURATION, ConfigurationHandler.WRITE_ACCESS);
      }
      if (configWrite != null) {
        Configuration rolesConfig = getSubConfiguration(configWrite, SecurityConfigurationPath.ROLES_PATH);
        String[] userstoresRoles = configRead.getAllSubConfigurationNames();
        Configuration userstoreRolesWriteConfig = null;
        Configuration userstoreRolesReadConfig = null;
        for (int i = 0; i < userstoresRoles.length; i++) {
          userstoreRolesWriteConfig = rolesConfig.createSubConfiguration(userstoresRoles[i]);
          userstoreRolesReadConfig = configRead.getSubConfiguration(userstoresRoles[i]);
          userstoreRolesWriteConfig = userstoreRolesWriteConfig.createSubConfiguration(TELNET_LOGIN_ROLE);
          userstoreRolesReadConfig = userstoreRolesReadConfig.getSubConfiguration(SecurityRoleContext.ROLE_ADMINISTRATORS);
          copyConfiguration(userstoreRolesReadConfig, userstoreRolesWriteConfig);
        }
        configHandler.commit();
      }
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

  private Configuration getSubConfiguration(Configuration config, String path) throws Exception {
    String subconfig = "";
    Configuration resConfig = config;

    while (path.length() > 0 && path.indexOf("/") > 0) {
      subconfig = path.substring(0, path.indexOf("/"));

      if (resConfig.existsSubConfiguration(subconfig)) {
        resConfig = resConfig.getSubConfiguration(subconfig);
      } else {
        resConfig = resConfig.createSubConfiguration(subconfig);
      }
      path = path.substring(path.indexOf("/") + 1);
    }

    if (path.length() > 0) {
      if (resConfig.existsSubConfiguration(path)) {
        resConfig = resConfig.getSubConfiguration(path);
      } else {
        resConfig = resConfig.createSubConfiguration(path);
      }
    }

    return resConfig;
  }

  public void copyConfiguration(Configuration readConfig, Configuration writeConfig) throws Exception {
    Map allEntries = readConfig.getAllConfigEntries();
    Iterator keys = allEntries.keySet().iterator();
    String key = null;

    while (keys.hasNext()) {
      key = (String) keys.next();
      writeConfig.addConfigEntry(key, allEntries.get(key));
    }

    Map allSubConfigs = readConfig.getAllSubConfigurations();

    if (allSubConfigs.size() == 0) {
      return;
    }

    keys = allSubConfigs.keySet().iterator();
    key = null;

    while (keys.hasNext()) {
      key = (String) keys.next();
      copyConfiguration((Configuration) allSubConfigs.get(key), writeConfig.createSubConfiguration(key));
    }
  }
}
