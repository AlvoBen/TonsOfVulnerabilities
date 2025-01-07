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
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.security.server.SecurityConfigurationPath;

/**
 * Remove the login modules with custom options. For the purpose these login 
 * module must be added in the UME User Store with its options. For display
 * name is used the class name with index at the end. 
 * 
 * @author Krasimira Velikova
 */
public class Change8 implements Change {
  Vector availableConf = new Vector();
  Vector newConf = new Vector();
  
  class LMConfig {//$JL-EQUALS$
    String displayName;
    String className;
    Map options;
    Map suitableMech;
    Map notSuitableMech;
    String description;
    String optionsEditor;
    
    LMConfig(String displayName, String className, Map options, Map suitableMech,
             Map notSuitMech, String description, String optionsEditor) {
      this.displayName = displayName;
      this.className = className;
      this.options = options;
      this.suitableMech = suitableMech;
      this.notSuitableMech = notSuitMech;
      this.description = description;
      this.optionsEditor = optionsEditor;
    }

    LMConfig(String className, Map options) {
      this.className = className;
      this.options = options;
    }
    
    public boolean equals(Object obj) {
      if (!(obj instanceof LMConfig)) {
        return false;
      }
      
      LMConfig conf = (LMConfig) obj;
      
      if (className == null && conf.className != null
          || className != null && !className.equals(conf.className)) {
        return false;
      }
      
      if (options == null && conf.options != null 
          || options != null && conf.options == null) {
        return false;
      }
      
      Set set1 = options.entrySet();
      Set set2 = conf.options.entrySet();
      
      if (!set1.containsAll(set2) || !set2.containsAll(set1)) {
        return false;
      }
            
      if (conf.displayName == null || displayName == null) {
        return true;
      }
      
      return displayName.equals(conf.displayName);
    }
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.services.security.patch.Change#run()
   */
  public void run() throws Exception {
    ConfigurationHandler configHandler = ChangeDaemon.configFactory.getConfigurationHandler();
    
    loadAvailableLM(configHandler);
    scanStacks(configHandler);
    createNewLM(configHandler);
  }
  

  private void loadAvailableLM(ConfigurationHandler handler) throws NameNotFoundException, ConfigurationLockedException, ConfigurationException {
    try {
      Configuration conf = handler.openConfiguration(
          SecurityConfigurationPath.USERSTORES_PATH + "/UME User Store/login-module", 
          ConfigurationHandler.READ_ACCESS);
      
      String[] names = conf.getAllSubConfigurationNames();
      
      if (names == null) {
        return;
      }
      
      for (int i = 0; i < names.length; i++) {
        Configuration lmConf = conf.getSubConfiguration(names[i]);
        
        String className = (String) lmConf.getConfigEntry("class-name");
        String description = (String) lmConf.getConfigEntry("description");
        String optionsEditor = null;
        
        if (lmConf.existsConfigEntry("options-editor")) {
          optionsEditor = (String) lmConf.getConfigEntry("options-editor");
        }
        
        Configuration optionsConf = lmConf.getSubConfiguration("options");
        Map options = optionsConf.getAllConfigEntries();
        
        Configuration tmp = lmConf.getSubConfiguration("suitable-mechanisms");
        Map suitableMech = tmp.getAllConfigEntries();
        
        tmp = lmConf.getSubConfiguration("not-suitable-mechanisms");
        Map notSuitableMech = tmp.getAllConfigEntries();
        
        availableConf.add(new LMConfig(names[i], className, options, suitableMech,
                                       notSuitableMech, description, optionsEditor));
      }
    } finally {
      handler.closeAllConfigurations();
    }
  }

  private void scanStacks(ConfigurationHandler confHandler) throws Exception {
    Set stacks = Change7.getStacks(confHandler);
    
    for (Iterator it = stacks.iterator(); it.hasNext();) {
      String path = (String) it.next();
      readOptionsFromTemplate(confHandler, path);
    }
  }

  private void readOptionsFromTemplate(ConfigurationHandler confHandler,
                                              String path) throws Exception {
    Configuration conf = null;

    try {
      conf = confHandler.openConfiguration(
          path + "/security/authentication/UME User Store", 
          ConfigurationHandler.READ_ACCESS);
    } catch (Exception e) {
      //$JL-EXC$
      //there is no such configuration
    }

    if (conf == null) {
      return;
    }

    try {
      String[] names = conf.getAllSubConfigurationNames();
      
      for (int i = 0; i < names.length; i++) {
        Configuration sub = conf.getSubConfiguration(names[i]);
        String className = (String) sub.getConfigEntry("classname");
        Configuration optionsConf = sub.getSubConfiguration("options");
        Map options = optionsConf.getAllConfigEntries();
        
        LMConfig lmConf = new LMConfig(className, options);
        LMConfig origLM = getAvailableLM(className);
        
        if (origLM == null) {
          //only different variants of login modules must be added to the userstore!
          continue;
        }
        
        if (!availableConf.contains(lmConf) && !newConf.contains(lmConf)) {
          lmConf.notSuitableMech = origLM.notSuitableMech;
          lmConf.suitableMech = origLM.suitableMech;
          lmConf.description = origLM.description;
          lmConf.optionsEditor = origLM.optionsEditor;

          newConf.add(lmConf);
        }
      }
    } finally {
      try {
        confHandler.closeConfiguration(conf);
      } catch (Exception e) {
        throw e;
      }
    }
  }
  
  private LMConfig getAvailableLM(String className) {
    if (className == null) {
      return null;
    }
    
    for (Iterator it = availableConf.iterator(); it.hasNext();) {
      LMConfig conf = (LMConfig) it.next();
      
      if (className.equals(conf.className)) {
        return conf;
      }
    }
    
    return null;
  }

  
  private void createNewLM(ConfigurationHandler configHandler) throws Exception {
    if (newConf.isEmpty()) {
      return;
    }
    
    try {
      Configuration conf = configHandler.openConfiguration(
          SecurityConfigurationPath.USERSTORES_PATH + "/UME User Store/login-module", 
          ConfigurationHandler.WRITE_ACCESS);
      
      for (Iterator it = newConf.iterator(); it.hasNext();) {
        LMConfig lmConfig = (LMConfig) it.next();
        
        String name = lmConfig.className;
        
        if (name.indexOf(".") != -1) {
          name = name.substring(name.lastIndexOf(".") + 1);
        }
        
        int index = 1;
        
        while (conf.existsSubConfiguration(name + index)) {
          index++;
        }
        
        Configuration newConf = conf.createSubConfiguration(name + index);
        newConf.addConfigEntry("class-name", lmConfig.className);
        newConf.addConfigEntry("description", lmConfig.description);
        
        if (lmConfig.optionsEditor != null) {
          newConf.addConfigEntry("options-editor", lmConfig.optionsEditor);
        }
        
        Configuration optionsConf = newConf.createSubConfiguration("options");
        optionsConf.addConfigEntries(lmConfig.options);
        
        Configuration mechConf = newConf.createSubConfiguration("suitable-mechanisms");
        if (lmConfig.suitableMech != null) {
          mechConf.addConfigEntries(lmConfig.suitableMech);
        }
        
        mechConf = newConf.createSubConfiguration("not-suitable-mechanisms");
        if (lmConfig.notSuitableMech != null) {
          mechConf.addConfigEntries(lmConfig.notSuitableMech);
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
