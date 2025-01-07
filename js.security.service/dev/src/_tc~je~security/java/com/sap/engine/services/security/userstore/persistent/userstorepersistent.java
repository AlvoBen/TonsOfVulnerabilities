package com.sap.engine.services.security.userstore.persistent;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.server.ConfigurationLock;
import com.sap.engine.services.security.server.ModificationContextImpl;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.services.security.userstore.descriptor.LoginModuleConfigurationImpl;
import com.sap.engine.services.security.userstore.descriptor.UserStoreConfigurationImpl;

/**
 *
 * @version 6.30
 * @author  Ekaterina Zheleva
 */
public class UserStorePersistent {
  private boolean initialized = false;
  private ModificationContextImpl modificationCtx = null;
  private ConfigurationLock configurationLock = new ConfigurationLock();
  public static final String ACTIVE = "active_userstore";

  public UserStorePersistent(ConfigurationChangedListener listener) {
    try {
      modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
      modificationCtx.beginModifications();
      Configuration userstoresContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH, true, true);
      ((UserStoreChangedConfigurationListener) listener).setPersistentStorage(this);
      modificationCtx.registerConfigurationListener(listener, SecurityConfigurationPath.USERSTORES_PATH);
      String[] userStores = userstoresContainer.getAllSubConfigurationNames();
      initialized = (userStores != null && userStores.length > 0);
      if (!initialized) {
        if (userstoresContainer.existsConfigEntry(ACTIVE)) {
          userstoresContainer.modifyConfigEntry(ACTIVE, "");
        } else {
          userstoresContainer.addConfigEntry(ACTIVE, "");
        }
      } else {
        try {
          String activeUserStore = (String) userstoresContainer.getConfigEntry(ACTIVE);
          if (activeUserStore == null || activeUserStore.length() == 0) {
            userstoresContainer.modifyConfigEntry(ACTIVE, Util.decode(userStores[0]));
          }
        } catch (Exception _) {
          userstoresContainer.addConfigEntry(ACTIVE, Util.decode(userStores[0]));
        }
      }
      modificationCtx.commitModifications();
    } catch (Exception e) {
      modificationCtx.rollbackModifications();
      throw new SecurityException("Cannot initialize the persistent storage of userstores.", e);
    }
  }

  public void store(UserStoreConfiguration usConfiguration, String classloaderName, ModificationContextImpl modifications) {
    configurationLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    try {
      if (modifications != null) {
        modificationCtx = modifications;
      } else {
        modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
      }
      modificationCtx.beginModifications();
			Configuration usContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH + "/" + Util.encode(usConfiguration.getName()), true, true);
      usContainer.addConfigEntry("classloader", (classloaderName == null) ? "" : classloaderName);
      usContainer.addConfigEntry("description", usConfiguration.getDescription());
      usContainer.addConfigEntry("user-class-name", usConfiguration.getUserSpiClassName());
      String groupClass = usConfiguration.getGroupSpiClassName();
      usContainer.addConfigEntry("group-class-name", (groupClass == null) ? "" : groupClass);
      String editor = usConfiguration.getConfigurationEditorClassName();
      usContainer.addConfigEntry("configuration-editor", (editor == null) ? "" : editor);
      Configuration propsContainer = usContainer.createSubConfiguration("configuration");
      Properties usProps = usConfiguration.getUserStoreProperties();
      Enumeration enumeration = usProps.propertyNames();
      String key = null;
      while (enumeration.hasMoreElements()) {
        key = (String) enumeration.nextElement();
        propsContainer.addConfigEntry(key, usProps.getProperty(key));
      }

      Configuration loginModulesContainer = usContainer.createSubConfiguration("login-module");
      LoginModuleConfiguration[] loginModules = usConfiguration.getLoginModules();
      if (loginModules != null) {
        for (int i = 0; i < loginModules.length; i++) {
          LoginModuleConfigurationImpl.store(loginModulesContainer, loginModules[i]);
        }
      }
      modificationCtx.commitModifications();
    } catch (Exception e) {
      modificationCtx.rollbackModifications();
      throw new SecurityException("Cannot write " + usConfiguration.getName() + " userstore in the persistent storage.", e);
    } finally {
      configurationLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
  }

  public void modify(UserStoreConfiguration usConfiguration, String classloaderName, ModificationContextImpl modifications) {
    configurationLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    try {
      if (modifications != null) {
        modificationCtx = modifications;
      } else {
        modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
      }
      modificationCtx.beginModifications();
			Configuration usContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH + "/" + Util.encode(usConfiguration.getName()), true, false);
      usContainer.modifyConfigEntry("classloader", (classloaderName == null) ? "" : classloaderName);
      usContainer.modifyConfigEntry("description", usConfiguration.getDescription());
      usContainer.modifyConfigEntry("user-class-name", usConfiguration.getUserSpiClassName());
      String groupClass = usConfiguration.getGroupSpiClassName();
      usContainer.modifyConfigEntry("group-class-name", (groupClass == null) ? "" : groupClass);
      String editor = usConfiguration.getConfigurationEditorClassName();
      usContainer.modifyConfigEntry("configuration-editor", (editor == null) ? "" : editor);
      Configuration propsContainer = usContainer.getSubConfiguration("configuration");
      propsContainer.deleteAllConfigEntries();
      Properties usProps = usConfiguration.getUserStoreProperties();
      Enumeration enumeration = usProps.propertyNames();
      String key = null;
      while (enumeration.hasMoreElements()) {
        key = (String) enumeration.nextElement();
        propsContainer.addConfigEntry(key, usProps.getProperty(key));
      }

      Configuration loginModulesContainer = usContainer.getSubConfiguration("login-module");

      // Do not delete deployed configurations
      Set deployedNames = new HashSet();
      String[] loginModuleConfigurationNames = loginModulesContainer.getAllSubConfigurationNames();
      for ( int i = 0 ; i < loginModuleConfigurationNames.length ; i++ ) {
        String loginModuleConfigurationName = loginModuleConfigurationNames[i];
        Configuration loginModuleConfiguration = loginModulesContainer.getSubConfiguration(loginModuleConfigurationName);
        if ( (loginModuleConfiguration.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) !=  Configuration.CONFIG_TYPE_DERIVED ) {
          loginModuleConfiguration.deleteConfiguration();
        } else {
          deployedNames.add(loginModuleConfigurationName);
        }
      }
      
      LoginModuleConfiguration[] loginModules = usConfiguration.getLoginModules();
      if (loginModules != null && loginModules.length > 0) {
        
        // check delete from UI
	    if (!deployedNames.isEmpty()) {
	      Iterator it = deployedNames.iterator();
	      while (it.hasNext()) {
	        String deployedName = (String) it.next();
	        boolean found = false;
	        for (int i = 0; i < loginModules.length; i++) {
	          String moduleName = loginModules[i].getName();
	          if (deployedName.equals(moduleName)) {
	            found = true;
	            break;
	          }
	        }
	        if (!found) {
	          throw new Exception("Deployed login module configuration cannot be deleted.");
	        }
	      }
	    }
      
        for (int i = 0; i < loginModules.length; i++) {
          LoginModuleConfigurationImpl.store(loginModulesContainer, loginModules[i]);
        }
      }
      modificationCtx.commitModifications();
    } catch (Exception e) {
      modificationCtx.rollbackModifications();
      throw new SecurityException("Cannot modify the persistent storage of " + usConfiguration.getName() + " userstore.", e);
    } finally {
      configurationLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
  }

  public void delete(String userStoreName, ModificationContextImpl modifications) {
    configurationLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    try {
      if (modifications != null) {
        modificationCtx = modifications;
      } else {
        modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
      }
      modificationCtx.beginModifications();
			Configuration userstoresContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH + "/" + Util.encode(userStoreName), true, false);
      if (userstoresContainer != null) {
        userstoresContainer.deleteConfiguration();
      }
      modificationCtx.commitModifications();
    } catch (Exception e) {
      modificationCtx.rollbackModifications();
      throw new SecurityException("Cannot delete the persistent storage of " + userStoreName + " userstore.", e);
    } finally {
      configurationLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
  }

  public void modify(String userStoreName) {
    configurationLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    try {
      modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
      modificationCtx.beginModifications();
      Configuration userstoresContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH, true, false);
      userstoresContainer.modifyConfigEntry(ACTIVE, userStoreName);
      modificationCtx.commitModifications();
    } catch (Exception e) {
      modificationCtx.rollbackModifications();
      throw new SecurityException("Cannot change the active userstore in the persistent storage.", e);
    } finally {
      configurationLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
  }

  public boolean isInitialized() {
    return initialized;
  }

  public UserStoreConfiguration[] loadUserStoreConfigurations() {
    configurationLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    UserStoreConfiguration[] configurations = new UserStoreConfiguration[0];
    try {
      modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
      modificationCtx.beginModifications();
      Configuration userstoresContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH, false, false);
      String[] userStores = userstoresContainer.getAllSubConfigurationNames();
      configurations = new UserStoreConfiguration[userStores.length];
      for (int i = 0; i < userStores.length; i++) {
        try {
		  configurations[i] = new UserStoreConfigurationImpl(Util.decode(userStores[i]), userstoresContainer);
        } catch (Exception ex) {
          continue;
        }
      }
    } catch (Exception e) {
      throw new SecurityException("Cannot load the UserStoreConfigurations from the persistent storage.", e);
    } finally {
      modificationCtx.forgetModifications();
      configurationLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
    return configurations;
  }

  public UserStoreConfiguration loadUserStoreConfiguration(String userstoreName) {
    configurationLock.lock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
   UserStoreConfiguration userstoreConfiguration = null;
   try {
      modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
      modificationCtx.beginModifications();
      Configuration userstoresContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH, false, false);
		  userstoreConfiguration = new UserStoreConfigurationImpl(Util.decode(userstoreName), userstoresContainer);
    } catch (Exception e) {
      throw new SecurityException("Cannot load the UserStoreConfiguration of " + userstoreName + " userstore from the persistent storage.", e);
    } finally {
      modificationCtx.forgetModifications();
      configurationLock.releaseLock(ConfigurationLock.USERSTORE_CONFIGURATION_NAME, ConfigurationLock.USERSTORE_LOCK_NAME);
    }
    return userstoreConfiguration;
  }

  public String getActiveUserStore() {
    try {
      modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
      modificationCtx.beginModifications();
      Configuration userstoresContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH, false, false);
      return (String) userstoresContainer.getConfigEntry(ACTIVE);
    } catch (Exception e) {
      throw new SecurityException("Cannot get the active userstore from the persistent storage.", e);
    } finally {
      modificationCtx.forgetModifications();
    }
  }
  
	public HashSet listUserStores(ModificationContextImpl modifications) {
		HashSet stores = new HashSet();
		try {
			if (modifications != null) {
				modificationCtx = modifications;
			} else {
				modificationCtx = (ModificationContextImpl) SecurityContextImpl.getRoot().getModificationContext();
			}
			modificationCtx.beginModifications();
      Configuration userstoresContainer = modificationCtx.getConfiguration(SecurityConfigurationPath.USERSTORES_PATH, false, false);
			String[] userStores = userstoresContainer.getAllSubConfigurationNames();
			for (int i = 0; i < userStores.length; i++) {
				stores.add(Util.decode(userStores[i]));
			}
    } catch (Exception e) {
			throw new SecurityException("Cannot load the UserStoreConfigurations from the persistent storage.", e);
    } finally {
      modificationCtx.forgetModifications();
    }
    return stores;
	}
}