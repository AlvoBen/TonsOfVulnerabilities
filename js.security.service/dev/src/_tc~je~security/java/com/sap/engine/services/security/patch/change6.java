/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.patch;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

/**
 * @author Krasimira Velikova
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Change6 implements Change {

  public static final String ACTIVE = "active_userstore";
  /* (non-Javadoc)
   * @see com.sap.engine.services.security.patch.Change#run()
   */
  public void run() throws Exception {
    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
/*
 * <login-module>
  <display-name>WSSAMLLoginModule</display-name>
  <description>Login module for Web Service Security SAML Token Profile</description>
  <suitable-mechanism>BASIC</suitable-mechanism>
  <suitable-mechanism>FORM</suitable-mechanism>
  <suitable-mechanism>CLIENT_CERT</suitable-mechanism>
  <suitable-mechanism>DIGEST</suitable-mechanism>
  <class-name>com.sap.security.core.server.wssec.jaas.WSSAMLLoginModule</class-name>
</login-module>

 */    
    addActiveUserstoreConfiguration(configHandler, 
        "WSSAMLLoginModule",
        "com.sap.security.core.server.wssec.jaas.WSSAMLLoginModule",
        "Login module for Web Service Security SAML Token Profile",
        null,
        new String[] {"BASIC", "FORM", "CLIENT_CERT", "DIGEST"},
        null
        );

    addActiveUserstoreConfiguration(configHandler, 
        "CertPersisterLoginModule",
        "com.sap.security.core.server.jaas.CertPersisterLoginModule",
        "Login module for mapping a client certificate to a user.",
        null,
        null,
        null
        );

    Map options = new HashMap();
    options.put("Header", "REMOTE_USER");
    options.put("windows_integrated", "false");
    options.put("domain", "");
    
    addActiveUserstoreConfiguration(configHandler, 
        "HeaderVariableLoginModule",
        "com.sap.security.core.server.jaas.HeaderVariableLoginModule",
        "Login module that reads a user ID from the HTTP header variable and then uses this user ID to authenticate the user.",
        options,
        null,
        null
        );
  }

  
  static void addActiveUserstoreConfiguration(ConfigurationHandler configHandler, 
      String displayName, String className, String description, Map options,
      String[] suitableMechanisms, String[] notSuitableMechanisms) throws Exception {
    
    try {
      Configuration configWrite = configHandler.openConfiguration(
          SecurityConfigurationPath.USERSTORES_PATH, 
          ConfigurationHandler.WRITE_ACCESS);
      
      if (configWrite == null) {
        return;
      }
      
      String activeUserStoreName = null;
      
      if (configWrite.existsConfigEntry(ACTIVE)) {
        activeUserStoreName = (String) configWrite.getConfigEntry(ACTIVE);
      }
      
      if (activeUserStoreName == null || activeUserStoreName.trim().length() == 0) {
        return;
      }

      Configuration activeUserStoreConf = configWrite.getSubConfiguration(activeUserStoreName);
      
      if (activeUserStoreConf.existsSubConfiguration("login-module")) {
        Configuration loginModules = activeUserStoreConf.getSubConfiguration("login-module");
        
        if (!loginModules.existsSubConfiguration(displayName)) {
          Configuration newLoginModuleConf 
              = loginModules.createSubConfiguration(displayName);
          
          String[] subConfigs = new String[] {"not-suitable-mechanisms", 
                                              "options", 
                                              "suitable-mechanisms"};
          
          Map map = newLoginModuleConf.createSubConfigurations(subConfigs);
          newLoginModuleConf.addConfigEntry("class-name", className);
          newLoginModuleConf.addConfigEntry("description", description);
          
          if (options != null && !options.isEmpty()) {
            Configuration optionsConf = (Configuration) map.get("options");
            optionsConf.addConfigEntries(options);
          }
          
          if (suitableMechanisms != null && suitableMechanisms.length > 0) {
            Configuration suitableMechanismsConf 
                = (Configuration) map.get("suitable-mechanisms");
            Object value = new byte[0];
            
            for (int i = 0; i < suitableMechanisms.length; i++) {
              suitableMechanismsConf.addConfigEntry(suitableMechanisms[i], value);
            }
          }
          
          if (notSuitableMechanisms != null && notSuitableMechanisms.length > 0) {
            Configuration notSuitableMechanismsConf 
                = (Configuration) map.get("not-suitable-mechanisms");
            Object value = new byte[0];
        
            for (int i = 0; i < notSuitableMechanisms.length; i++) {
              notSuitableMechanismsConf.addConfigEntry(notSuitableMechanisms[i], 
                                                       value);
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
