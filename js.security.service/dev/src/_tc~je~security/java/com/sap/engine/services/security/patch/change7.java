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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

/**
 * Remove ume.configuration.active property from CreateTicketLoginModule, 
 * EvaluateTicketLoginModule, HeaderVariableLoginModule.
 * Remove the following properties - keystore, password, alias, client, validity, 
 * validityMin and inclcert from CreateAssertionTicketLoginModule, 
 * CreateTicketLoginModule, EvaluateAssertionTicketLoginModule.  
 * 
 * @author Krasimira Velikova
 */
public class Change7 implements Change {
  
  /* (non-Javadoc)
   * @see com.sap.engine.services.security.patch.Change#run()
   */
  public void run() throws Exception {
    ConfigurationHandler configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
    
    Set lmNames = new HashSet();
    lmNames.add("com.sap.security.core.server.jaas.CreateTicketLoginModule");
    lmNames.add("com.sap.security.core.server.jaas.EvaluateTicketLoginModule");
    lmNames.add("com.sap.security.core.server.jaas.HeaderVariableLoginModule");
    lmNames.add("com.sap.security.core.server.jaas.CreateAssertionTicketLoginModule");
    lmNames.add("com.sap.security.core.server.jaas.EvaluateAssertionTicketLoginModule");
    
    String[] options = new String[] {
        "ume.configuration.active", "keystore", "password", "alias", "client", 
        "validity", "validityMin", "inclcert" 
    };
    
    removeOptionsFromUMEUserstore(configHandler, lmNames, options);

    Set stacks = getStacks(configHandler);
    
    for (Iterator it = stacks.iterator(); it.hasNext();) {
      String path = (String) it.next();
      removeOptionsFromTemplate(configHandler, path, lmNames, options);
    }

  }
  
  private static void removeOptionsFromUMEUserstore(ConfigurationHandler confHandler, 
                                                Set classNames, 
                                                String[] options) throws Exception {
    try {
      Configuration confWrite = confHandler.openConfiguration(
          SecurityConfigurationPath.USERSTORES_PATH, 
          ConfigurationHandler.WRITE_ACCESS);
      
      if (confWrite == null) {
        return;
      }
      
      Configuration umeConf = confWrite.getSubConfiguration("UME User Store/login-module");
      
      removeOptions(umeConf, classNames, options, "class-name");

      confHandler.commit();
      
    } catch (Exception e) {
      try {
        confHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
      throw e;
    } finally {
      try {
        confHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
  }
  

  private static void removeOptionsFromTemplate(ConfigurationHandler confHandler,
                                         String path,
                                         Set classNames, 
                                         String[] options) throws Exception {
    Configuration conf = null;
    
    try {
      conf = confHandler.openConfiguration(path, ConfigurationHandler.WRITE_ACCESS);
    } catch (Exception e) {
      //$JL-EXC$
      //there is no such configuration
    }
    
    if (conf == null) {
      return;
    }

    try {
      removeOptionsFromStack(conf, classNames, options);
      confHandler.commit();
    } catch (Exception e) {
      try {
        confHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
      throw e;
    } finally {
      try {
        confHandler.closeConfiguration(conf);
      } catch (Exception e) {
        throw e;
      }
    }
  }
  
  static Set getStacks(ConfigurationHandler confHandler) throws Exception {
    try {
      Configuration secConfConf = confHandler.openConfiguration(
          SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, 
          ConfigurationHandler.READ_ACCESS);
      
      if (secConfConf == null) {
        return null;
      }
      
      String[] names = secConfConf.getAllConfigEntryNames();
      
      if (names == null) {
        return null;
      }
      
      Set ret = new HashSet();
      
      for (int i = 0; i < names.length; i++) {
        String path = (String) secConfConf.getConfigEntry(names[i]);
        ret.add(path);
      }
      
      return ret;
    } finally {
      try {
        confHandler.closeAllConfigurations();
      } catch (Exception e) {
        throw e;
      }
    }
  }
  
  private static void removeOptionsFromStack(Configuration confWrite, Set classNames, String[] options) {
    if (confWrite == null) {
      return;
    }

    try {
      Configuration conf = confWrite.getSubConfiguration(
          "security/authentication/UME User Store");
      
      removeOptions(conf, classNames, options, "classname");
    } catch (Exception ex) {
      //$JL-EXC$
      //configuration does not exists
    }
  }
  
  private static void removeOptions(Configuration rootConf, Set classNames,
                             String[] options, String classNameConfEntry) throws InconsistentReadException, ConfigurationException {
    if (rootConf == null || classNames == null || options == null) {
      return;
    }
    
    String[] names = rootConf.getAllSubConfigurationNames();
    
    for (int i = 0; i < names.length; i++) {
      Configuration sub = rootConf.getSubConfiguration(names[i]);
      String confClassName = (String) sub.getConfigEntry(classNameConfEntry);
      
      if (!classNames.contains(confClassName)) {
        continue;
      }
      
      Configuration optionsConf = sub.getSubConfiguration("options");
      optionsConf.deleteConfigEntries(options);
    }
  }
}
