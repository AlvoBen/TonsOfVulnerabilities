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
package com.sap.engine.services.security.userstore.descriptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.DerivedConfiguration;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.server.ModificationContextImpl;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.tc.logging.Severity;

/**
 *
 * @version 6.30
 * @author  Ekaterina Zheleva
 */
public class LoginModuleConfigurationImpl implements LoginModuleConfiguration {

  private String name;
  private String description;
  private String loginModuleClassName;
  private Properties options = new Properties();
  private String[] suitableAuth = new String[0];
  private String[] notSuitableAuth = new String[0];
  private String editor = null;

  public LoginModuleConfigurationImpl(Configuration loginModulesContainer, String loginModuleName) throws Exception {
    name = loginModuleName;
    Configuration container = loginModulesContainer.getSubConfiguration(loginModuleName);
    description = (String) container.getConfigEntry("description");
    loginModuleClassName = (String) container.getConfigEntry("class-name");
    readOptions(container.getSubConfiguration("options"));
    suitableAuth = container.getSubConfiguration("suitable-mechanisms").getAllConfigEntryNames();
    notSuitableAuth = container.getSubConfiguration("not-suitable-mechanisms").getAllConfigEntryNames();

    if (container.existsConfigEntry("options-editor")) {
      editor = (String) container.getConfigEntry("options-editor");
    }
  }

  /**
   *  Returns the description of the login module.
   *
   * @return  printable text.
   */
  public String getDescription() {
    return description;
  }

  /**
   *  Returns the display name of the login module.
   *
   * @return  display name.
   */
  public String getName() {
    return name;
  }

  /**
   *  Hints for common authentication mechanisms this login module is not suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getNotSuitableAuthenticationMechanisms() {
    return notSuitableAuth;
  }

  /**
   *  Returns the class name of the login module.
   *
   * @return  class name.
   */
  public String getLoginModuleClassName() {
    return loginModuleClassName;
  }

  /**
   *  Returns the options of the login module.
   *
   * @return  options.
   */
  public Map getOptions() {
    return options;
  }

  /**
   *  Hints for common authentication mechanisms this login module is suitable for.
   *
   * @return  a list of common authentication mechanisms.
   */
  public String[] getSuitableAuthenticationMechanisms() {
    return suitableAuth;
  }

  /**
   * Gets the special editor suitable for this login module options.
   *
   * @return  the editor for this login module options, or null if the default one should be used.
   */
  public String getOptionsEditor() {
    return editor;
  }

  private void readOptions(Configuration optionsContainer) throws Exception {
    String[] attributes = optionsContainer.getAllConfigEntryNames();
    for (int i = 0; i < attributes.length; i++) {
      try {
        options.setProperty(attributes[i], (String) optionsContainer.getConfigEntry(attributes[i]));
      } catch (ClassCastException cce) {
      	Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Unable to set option <" + attributes[i] + "> of login module <" + name + ">. Not a string value.", cce);
      }
    }
  }

  private static void handleDeployedConfiguration(Configuration lmConfig, LoginModuleConfiguration loginModuleConfig) throws Exception {
    
    String newClassName = loginModuleConfig.getLoginModuleClassName();
    String newDisplayName = loginModuleConfig.getName();
    
    // check rename display name
    if (!lmConfig.existsSubConfiguration(newDisplayName)) {
      throw new Exception("Cannot change deployed login module display name.");
    }
    
    Configuration usLoginModuleConfig = lmConfig.getSubConfiguration(newDisplayName);
    if ( (usLoginModuleConfig.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) !=  Configuration.CONFIG_TYPE_DERIVED ) {
      throw new Exception("Configuration is not instance of linked configuration.");
    }
    
    // check rename class name
    String oldClassName = (String) usLoginModuleConfig.getConfigEntry("class-name");
    if (!oldClassName.equals(newClassName)) {
      throw new Exception("Cannot change deployed login module class name.");  
    }
    
    Map newOptions = loginModuleConfig.getOptions();
    Map oldOptions = new HashMap();
    if (usLoginModuleConfig.existsSubConfiguration("options")) {
      Configuration optionsContainer = usLoginModuleConfig.getSubConfiguration("options");
      String[] attributes = optionsContainer.getAllConfigEntryNames();
      for (int i = 0; i < attributes.length; i++) {
        oldOptions.put(attributes[i], (String) optionsContainer.getConfigEntry(attributes[i]));
      }
    }
    
    // rename options
    if (!oldOptions.equals(newOptions)) {          
      DerivedConfiguration linkedConfig = (DerivedConfiguration) usLoginModuleConfig;
      Configuration appConfig = linkedConfig.getLinkedConfiguration();
      if (appConfig == null) {
        throw new Exception("Configuration has no linked configuration.");
      }
      
      ModificationContextImpl modificationContext = (ModificationContextImpl)SecurityContextImpl.getRoot().getModificationContext();
	  try {
	    modificationContext.beginModifications();
	    appConfig = modificationContext.getConfiguration(appConfig.getPath(), true, false);
	              
	    Configuration optionsContainer = null;
		try {
		  optionsContainer = appConfig.createSubConfiguration("options");
		} catch (NameAlreadyExistsException e) {
		  appConfig.deleteSubConfigurations(new String[] {"options"});
		  optionsContainer = appConfig.createSubConfiguration("options");
		}
		          
		Iterator it = newOptions.entrySet().iterator();
		while (it.hasNext()) {
		  Entry entry = (Entry) it.next();
		  optionsContainer.addConfigEntry((String)entry.getKey(), entry.getValue());
		}
		          
		modificationContext.commitModifications();
      } catch(Exception e) {
        modificationContext.rollbackModifications();
        throw e;
      }
    }
  }
  
  public static void store(Configuration container, LoginModuleConfiguration loginModuleConfig) throws Exception {
    Configuration loginModuleContainer = null;

    try {
      loginModuleContainer = container.createSubConfiguration(loginModuleConfig.getName());
    } catch (Exception e) {
      loginModuleContainer = container.getSubConfiguration(loginModuleConfig.getName());
    }
    
    // deployed login module configurations have special treatment
    if ( (loginModuleContainer.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) ==  Configuration.CONFIG_TYPE_DERIVED ) {
      handleDeployedConfiguration(container, loginModuleConfig);
      return ;
    }

    try {
      loginModuleContainer.addConfigEntry("description", loginModuleConfig.getDescription());
    } catch (Exception e) {
      loginModuleContainer.modifyConfigEntry("description", loginModuleConfig.getDescription());
    }

    try {
      loginModuleContainer.addConfigEntry("class-name", loginModuleConfig.getLoginModuleClassName());
    } catch (Exception e) {
      loginModuleContainer.modifyConfigEntry("class-name", loginModuleConfig.getLoginModuleClassName());
    }

    Configuration optionsContainer = null;
    try {
      optionsContainer = loginModuleContainer.createSubConfiguration("options");
    } catch (Exception e) {
      optionsContainer = loginModuleContainer.getSubConfiguration("options");
    }

    Map options = loginModuleConfig.getOptions();
    Iterator enumeration = options.keySet().iterator();
    String key = null;
    while (enumeration.hasNext()) {
      key = (String) enumeration.next();
      try {
        optionsContainer.addConfigEntry(key, options.get(key));
      } catch (Exception e) {
        optionsContainer.modifyConfigEntry(key, options.get(key));
      }
    }

    String[] suitableMechanisms = loginModuleConfig.getSuitableAuthenticationMechanisms();
    Configuration suitableContainer = null;
    try {
      suitableContainer = loginModuleContainer.createSubConfiguration("suitable-mechanisms");
    } catch (Exception e) {
      suitableContainer = loginModuleContainer.getSubConfiguration("suitable-mechanisms");
    }

    for (int i = 0; i < suitableMechanisms.length; i++) {
      try {
        suitableContainer.addConfigEntry(suitableMechanisms[i], new byte[0]);
      } catch (Exception e) {
        suitableContainer.modifyConfigEntry(suitableMechanisms[i], new byte[0]);
      }
    }

    String[] not_suitableMechanisms = loginModuleConfig.getNotSuitableAuthenticationMechanisms();
    Configuration not_suitableContainer = null;
    try {
      not_suitableContainer = loginModuleContainer.createSubConfiguration("not-suitable-mechanisms");
    } catch (Exception e) {
      not_suitableContainer = loginModuleContainer.getSubConfiguration("not-suitable-mechanisms");
    }
    for (int i = 0; i < not_suitableMechanisms.length; i++) {
      try {
        not_suitableContainer.addConfigEntry(not_suitableMechanisms[i], new byte[0]);
      } catch (Exception e) {
        not_suitableContainer.modifyConfigEntry(not_suitableMechanisms[i], new byte[0]);
      }
    }

    String editor = loginModuleConfig.getOptionsEditor();
    if (editor != null) {
      try {
        loginModuleContainer.addConfigEntry("options-editor", editor);
      } catch (Exception e) {
        loginModuleContainer.modifyConfigEntry("options-editor", editor);
      }
    }
  }
  
  public static void storeLink(Configuration container, Configuration loginModulesConfig, LoginModuleConfiguration loginModuleConfig) throws Exception {
    
    Configuration loginModuleContainer = null;		    
	try {
	  loginModuleContainer = loginModulesConfig.createSubConfiguration(loginModuleConfig.getName());
	} catch (NameAlreadyExistsException e) {
	  loginModuleContainer = loginModulesConfig.getSubConfiguration(loginModuleConfig.getName());
	}
	
	try {
	  loginModuleContainer.addConfigEntry("description", loginModuleConfig.getDescription());
	} catch (NameAlreadyExistsException e) {
	  loginModuleContainer.modifyConfigEntry("description", loginModuleConfig.getDescription());
	}
	
	try {
	  loginModuleContainer.addConfigEntry("class-name", loginModuleConfig.getLoginModuleClassName());
	} catch (NameAlreadyExistsException e) {
	  loginModuleContainer.modifyConfigEntry("class-name", loginModuleConfig.getLoginModuleClassName());
	}
	
	Configuration optionsContainer = null;
	try {
	  optionsContainer = loginModuleContainer.createSubConfiguration("options");
	} catch (NameAlreadyExistsException e) {
	  loginModuleContainer.deleteSubConfigurations(new String[] {"options"});
	  optionsContainer = loginModuleContainer.createSubConfiguration("options");
	}
	
	Map options = loginModuleConfig.getOptions();
	Iterator it = options.entrySet().iterator();
    while (it.hasNext()) {
      Entry entry = (Entry) it.next();
      optionsContainer.addConfigEntry((String)entry.getKey(), entry.getValue());
    }
	
	String[] suitableMechanisms = loginModuleConfig.getSuitableAuthenticationMechanisms();
	Configuration suitableContainer = null;
	try {
	  suitableContainer = loginModuleContainer.createSubConfiguration("suitable-mechanisms");
	} catch (NameAlreadyExistsException e) {
	  suitableContainer = loginModuleContainer.getSubConfiguration("suitable-mechanisms");
	}
	
	for (int i = 0; i < suitableMechanisms.length; i++) {
	  try {
	    suitableContainer.addConfigEntry(suitableMechanisms[i], new byte[0]);
	  } catch (NameAlreadyExistsException e) {
	    suitableContainer.modifyConfigEntry(suitableMechanisms[i], new byte[0]);
	  }
	}
	
	String[] not_suitableMechanisms = loginModuleConfig.getNotSuitableAuthenticationMechanisms();
	Configuration not_suitableContainer = null;
	try {
	  not_suitableContainer = loginModuleContainer.createSubConfiguration("not-suitable-mechanisms");
	} catch (NameAlreadyExistsException e) {
	  not_suitableContainer = loginModuleContainer.getSubConfiguration("not-suitable-mechanisms");
	}
	for (int i = 0; i < not_suitableMechanisms.length; i++) {
	  try {
	    not_suitableContainer.addConfigEntry(not_suitableMechanisms[i], new byte[0]);
	  } catch (NameAlreadyExistsException e) {
	    not_suitableContainer.modifyConfigEntry(not_suitableMechanisms[i], new byte[0]);
	  }
	}
	
	String editor = loginModuleConfig.getOptionsEditor();
	if (editor != null) {
	  try {
	    loginModuleContainer.addConfigEntry("options-editor", editor);
	  } catch (NameAlreadyExistsException e) {
	    loginModuleContainer.modifyConfigEntry("options-editor", editor);
	  }
	}
	
	DerivedConfiguration loginModuleContainerLink = null;		    
	try {
	  loginModuleContainerLink = (DerivedConfiguration) container.createSubConfiguration(loginModuleConfig.getName(), Configuration.CONFIG_TYPE_DERIVED_READ_ONLY );
	  loginModuleContainerLink.setLink(loginModuleContainer.getPath());
	} catch (NameAlreadyExistsException e) {
	  Configuration lmConfig = container.getSubConfiguration(loginModuleConfig.getName());
	  if ( (lmConfig.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) ==  Configuration.CONFIG_TYPE_DERIVED ) {
	    loginModuleContainerLink = (DerivedConfiguration) lmConfig; 
		loginModuleContainerLink.setLink(loginModuleContainer.getPath());  
	  } else {
	    throw new Exception("Login module configuration already exists with the same name.", e);
	  }
	}
  }  
}
