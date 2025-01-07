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
 
//empty class
//made for compatibility with 6.40
//see 6.40 AdditionalChange2 and 7.10 Change11
 
package com.sap.engine.services.security.patch;

import java.util.Map;

import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

public class AdditionalChange2 implements Change {

  public static final String ACTIVE = "active_userstore";

  public void run() throws Exception {
  }

/**
 * Adds a login module configuration. If configuration exists change its class-name only
 *
 * @see com.sap.engine.services.security.patch.Change11, method checkLoginModuleExistence(ConfigurationHandler configHandler, String sDisplayName, String sClassName )   
 * @see com.sap.engine.services.security.patch.Change6, method addActiveUserstoreConfiguration   
 */
  public void rudeAddLoginModule (String sDisplayName, String sClassName, String sDescription, Map options,
      String[] suitableMechanisms, String[] notSuitableMechanisms) throws Exception{

    int existence = checkLoginModuleExistence( sDisplayName, sClassName );

    if ( 2 == existence)
      return;//module already exists. do nothing.
    
    
    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();

    if ( 0 == existence ){
      addActiveUserstoreConfiguration(configHandler,
        sDisplayName,
        sClassName,
        sDescription,
        options,
        suitableMechanisms,
        notSuitableMechanisms
        );
    }
    if (1 == existence){
      changeLoginModuleClassName( configHandler,
        sDisplayName,
        sClassName);
    }
  }

/**
 * Checks the level of existence of a login module
 *
 * @param   sDisplayName The name of the "dispaly-name" proerty of the configuration entry
 * @return  level of existence
 *         0 - not found
 *         1 - a login module with same "display-name" found
 *         2 - a login module with same "display-name" and "class-name" found  
 */
  public int checkLoginModuleExistence(String sDisplayName, String sClassName ) throws Exception {
    int res = 0;
    ConfigurationHandler configHandler = null;
    configHandler = ChangeDaemon.configFactory.getConfigurationHandler();

    try{
      Configuration config = configHandler.openConfiguration(
        SecurityConfigurationPath.USERSTORES_PATH, 
        ConfigurationHandler.READ_ACCESS);
      
      if (config == null) {
        return res;
      }
      
      String activeUserStoreName = null;
      
      if (config.existsConfigEntry(ACTIVE)) {
        activeUserStoreName = (String) config.getConfigEntry(ACTIVE);
      }
      
      if (activeUserStoreName == null || activeUserStoreName.trim().length() == 0) {
        return res;
      }
      
      Configuration activeUserStoreConf = config.getSubConfiguration(activeUserStoreName);
      
      if (activeUserStoreConf.existsSubConfiguration("login-module")) {
        Configuration loginModules = activeUserStoreConf.getSubConfiguration("login-module");
      
        if (!loginModules.existsSubConfiguration( sDisplayName )) {
          return res;
        }
        else{ 
          res = 1;
        }
        
        //check display-name
        Configuration theModule = loginModules.getSubConfiguration( sDisplayName );
        Object name = null;
        //check class-name if display-name exists
        if (1 == res)
        {
          try {
            name = theModule.getConfigEntry("class-name");
            if (0 == sClassName.compareTo( name.toString()))
              res = 2;
          }
          catch (NameNotFoundException nnfe){
            return res;
          }
        }//if display-name exists
      }//if exist active userstore conf
    }
    catch (ConfigurationException e){
      configHandler.rollback();
      return 0; 
    }
    finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
    return res;
  }

  public void changeLoginModuleClassName( ConfigurationHandler configHandler,
        String sDisplayName, String sClassName)throws Exception {

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

        if ( loginModules.existsSubConfiguration( sDisplayName ) ) {
          Configuration theModule = loginModules.getSubConfiguration( sDisplayName );
          if ( theModule.existsConfigEntry( "class-name")){
            theModule.modifyConfigEntry( "class-name", sClassName );
          }else{
            theModule.addConfigEntry( "class-name", sClassName );
          }
        }//if sDisplayName exists
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