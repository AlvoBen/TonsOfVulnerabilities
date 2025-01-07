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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

/**
 * Adds the new AnonymousLoginModule to the configured login modules 
 * for the authentication (active) user store.
 *  
 * @author Krasimira Velikova
 */
public class Change10 implements Change {
  /* (non-Javadoc)
   * @see com.sap.engine.services.security.patch.Change#run()
   */
  public void run() throws Exception {
    ConfigurationHandler configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
/*
<login-module>
  <display-name>AnonymousLoginModule</display-name>
  <description>Login module for named anonymous users.</description>
  <class-name>com.sap.security.core.server.jaas.AnonymousLoginModule </class-name>
</login-module>
 */    
    Change6.addActiveUserstoreConfiguration(configHandler, 
        "AnonymousLoginModule",
        "com.sap.security.core.server.jaas.AnonymousLoginModule",
        "Login module for named anonymous users.",
        null,
        null,
        null
        );
    
    renameUMEUserstoreConfiguration(configHandler, 
        "CertMappingLoginModule",
        "CertPersisterLoginModule",
        "com.sap.security.core.server.jaas.CertMappingLoginModule",
        "com.sap.security.core.server.jaas.CertPersisterLoginModule");
    
    Set stacks = Change7.getStacks(configHandler);
    
    for (Iterator it = stacks.iterator(); it.hasNext();) {
      String path = (String) it.next();
      renameStack(configHandler, path, 
          "com.sap.security.core.server.jaas.CertMappingLoginModule",
          "com.sap.security.core.server.jaas.CertPersisterLoginModule");
    }
    
  }
  
  static void renameUMEUserstoreConfiguration(ConfigurationHandler configHandler, 
      String searchDisplayName, String replaceDisplayName,
      String searchClassName, String replaceClassName) throws Exception {
    
    try {
      Configuration configWrite = configHandler.openConfiguration(
            SecurityConfigurationPath.USERSTORES_PATH, 
            ConfigurationHandler.WRITE_ACCESS);
        
      Configuration loginModules = configWrite.getSubConfiguration("UME User Store/login-module");
      
      String[] names = loginModules.getAllSubConfigurationNames();
      
      if (names == null) {
        return;
      }

      Vector deleteSubConf = new Vector(names.length);
      
      for (int i = 0; i < names.length; i++) {
        Configuration conf = loginModules.getSubConfiguration(names[i]);
        String name = null;
        
        if (names[i].equals(searchDisplayName)) {
          name = replaceDisplayName;
        } else if (names[i].startsWith(searchDisplayName)) {
          name = replaceDisplayName + names[i].substring(searchDisplayName.length());
        }
        
        if (name != null) {
          deleteSubConf.add(names[i]);
          
          if (loginModules.existsSubConfiguration(name)) {
            Configuration existingConf = loginModules.getSubConfiguration(name);
            String exClassName = (String) existingConf.getConfigEntry("class-name");
            
            if (exClassName.equals(replaceClassName)
                && match(conf, existingConf, "options")
                && match(conf, existingConf, "suitable-mechanisms")
                && match(conf, existingConf, "not-suitable-mechanisms")) {
              continue;
            }
            
            int add = 1;
            
            while (conf.existsSubConfiguration(name + add)) {
              add++;
            }
            
            name = name + add;
          }
          
          Map entries = conf.getAllConfigEntries();
          
          Configuration oldConf = conf;
          conf = loginModules.createSubConfiguration(name);
          conf.addConfigEntries(entries);
          
          String[] subConfName = oldConf.getAllSubConfigurationNames();
          
          if (subConfName != null) {
            for (int k = 0; k < subConfName.length; k++) {
              Configuration tmp = conf.createSubConfiguration(subConfName[k]);
              Map e = oldConf.getSubConfiguration(subConfName[k]).getAllConfigEntries();
              tmp.addConfigEntries(e);
            }
          }
        }
        
        String className = (String) conf.getConfigEntry("class-name");
        
        if (className.equals(searchClassName)) {
          conf.modifyConfigEntry("class-name", replaceClassName);
        }
      }
      
      String[] delete = new String[deleteSubConf.size()];
      deleteSubConf.copyInto(delete);
      loginModules.deleteSubConfigurations(delete);

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

  
  private static void renameStack(ConfigurationHandler confHandler,
      String path, String searchClassName, String replaceClassName) throws Exception {
    Configuration conf = null;

    try {
      conf = confHandler.openConfiguration(path, ConfigurationHandler.WRITE_ACCESS);
    } catch (Exception e) {
      //$JL-EXC$
      //there is no such configuration
      return;
    }

    String str = "security/authentication/UME User Store";
    
    try {
      Configuration stackConf = conf.getSubConfiguration(str);
  
      String[] names = stackConf.getAllSubConfigurationNames();
      
      for (int i = 0; i < names.length; i++) {
        Configuration sub = stackConf.getSubConfiguration(names[i]);
        String confClassName = (String) sub.getConfigEntry("classname");
        
        if (confClassName.equals(searchClassName)) {
          sub.modifyConfigEntry("classname", replaceClassName);
        }
      }

      confHandler.commit();
    } catch (Exception e) {
      try {
        confHandler.rollback();
      } catch (Exception re) {
        throw re;
      }
    } finally {
      try {
        confHandler.closeConfiguration(conf);
      } catch (Exception e) {
        throw e;
      }
    }
  }
  
  private static boolean match(Configuration conf1, Configuration conf2, String subConfName) throws ConfigurationException {
    boolean missingSubConf1 = !conf1.existsSubConfiguration(subConfName);
    boolean missingSubConf2 = !conf2.existsSubConfiguration(subConfName);
    
    if (missingSubConf1 && missingSubConf2) {
      return true;
    }

    if (missingSubConf1 || missingSubConf2) {
      return false;
    }
    
    Set set1 = conf1.getSubConfiguration(subConfName).getAllConfigEntries().entrySet();
    Set set2 = conf2.getSubConfiguration(subConfName).getAllConfigEntries().entrySet();
    
    return set1.containsAll(set2) && set2.containsAll(set1);
    
  }
}
