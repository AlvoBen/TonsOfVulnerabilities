package com.sap.engine.services.security.migration;

import com.sap.engine.interfaces.security.ApplicationSecurityConfigurationAccessor;
import com.sap.engine.interfaces.security.JACCUpdateContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;

import java.util.*;

public class ApplicationSecurityConfigurationAccessorImpl implements ApplicationSecurityConfigurationAccessor {
  
  private static final String SECURITY_CONFIGURATIONS_PATH = "security/configurations";
  private static final String SECURITY_SUFFIX = "/security";
  private static final String SECURITY_ROLES_SUFFIX = "/security/roles/UME User Store";
  private static final String APPLICATION_TYPE = "type";
  
  private String applicationName = null;
  private byte mode = -1;
  
  private HashMap modulesConfigurations = new HashMap();
  private JACCUpdateContext jaccUpdateContext = null;
  
  
  public ApplicationSecurityConfigurationAccessorImpl(String applicationName) {
  	this.applicationName = applicationName;
  }
  
  public void setMode(byte mode) {
    this.mode = mode;
    if (mode == SWITCH_MODE) {
      modulesConfigurations = getApplicationModulesConfigurations();
    } else if (mode == UPDATE_MODE) {
      jaccUpdateContext = getJACCUpdateContext();
    }
  }
  
  public void setApplicationName(String applicationName) {
  	this.applicationName = applicationName;
  	if (mode == SWITCH_MODE) {
      modulesConfigurations = getApplicationModulesConfigurations();
    } else if (mode == UPDATE_MODE) {
      jaccUpdateContext = getJACCUpdateContext();
    }
      
  }
  
  public String getApplicationName() {
  	return applicationName;
  }
  /**
   * @see com.sap.engine.interfaces.security.ApplicationSecurityConfigurationAccessor#getModulesNames(byte)
   */
  public Set getModulesNames(byte type) throws SecurityException {
    HashSet result = new HashSet();
    switch (mode) {
      case SWITCH_MODE: {
      	ConfigurationHandler handler = null;
        try {
          handler = MigrationFramework.getConfigurationHandler();
          Iterator modules = modulesConfigurations.keySet().iterator();
          
          String moduleName = null;
          String modulePath = null;
          Configuration moduleConfig = null;
          while (modules.hasNext()) {
          	moduleName = (String) modules.next();
          	modulePath = (String) modulesConfigurations.get(moduleName);
          	moduleConfig = handler.openConfiguration(modulePath + SECURITY_SUFFIX, ConfigurationHandler.READ_ACCESS);
          	if (moduleConfig.existsConfigEntry(APPLICATION_TYPE)) {
              byte value; 
			  try {
			  	value = new Byte((String) moduleConfig.getConfigEntry(APPLICATION_TYPE)).byteValue();
			  } catch (Exception _) {
			  	value = SecurityContext.TYPE_OTHER;
			  }
              if (SecurityContext.TYPE_EJB_COMPONENT == value) {
              	result.add(moduleName);
              }
          	}
          }
        } catch (ConfigurationException ce) {
          throw new SecurityException(ce.getMessage());
        } finally {
          if (handler != null) {
          	try {
              handler.closeAllConfigurations();
            } catch (Exception _) {
              //$JL-EXC$
            }
          }
        }
        break;
      }
      case UPDATE_MODE: {
      	if (SecurityContext.TYPE_EJB_COMPONENT == type) {
          Iterator modules = jaccUpdateContext.getApplicationModulesNames(applicationName).iterator();
          String moduleName = null;
          while (modules.hasNext()) {
        	moduleName = (String) modules.next();
            if (moduleName.endsWith(".jar")) {
              result.add(moduleName);
        	}
          }
      	}
        break;
      }
    }
    return result;
  }
  
  /**
   * @see com.sap.engine.interfaces.security.ApplicationSecurityConfigurationAccessor#getModuleRolesNames(java.lang.String)
   */
  public Set getModuleRolesNames(String moduleName) throws SecurityException {
    Set result = null;
    switch (mode) {
      case SWITCH_MODE: {
        ConfigurationHandler handler = null;
        try {
          handler = MigrationFramework.getConfigurationHandler();
          String modulePath = (String) modulesConfigurations.get(moduleName);
          Configuration config = handler.openConfiguration(modulePath + SECURITY_ROLES_SUFFIX, ConfigurationHandler.READ_ACCESS);
          Map map = config.getAllSubConfigurations();
          result = map.keySet();
        } catch (ConfigurationException ce) {
          throw new SecurityException(ce.getMessage());
        } finally {
          if (handler != null) {
          	try {
              handler.closeAllConfigurations();
            } catch (Exception _) {
              //$JL-EXC$
            }
          }
        }
        break;
      }
      case UPDATE_MODE: {
      	result = jaccUpdateContext.getApplicationModuleRolesNames(applicationName, moduleName);
        break;
      }
    }
    return result;
  }
  
 /**
  * @see com.sap.engine.interfaces.security.ApplicationSecurityConfigurationAccessor#getModuleMappedServerRoles(java.lang.String, java.lang.String)
  */
  public Set getModuleMappedServerRoles(String moduleName, String j2eeRoleName) throws SecurityException {
    if (mode == UPDATE_MODE) {
      return jaccUpdateContext.getApplicationModuleMappedServerRoles(applicationName, moduleName, j2eeRoleName); 
    }
    return null;
  }  
  
  private HashMap getApplicationModulesConfigurations() {
    HashMap result = new HashMap();
    ConfigurationHandler handler = null;
    try {
      handler = MigrationFramework.getConfigurationHandler();
      Configuration config = handler.openConfiguration(SECURITY_CONFIGURATIONS_PATH, ConfigurationHandler.READ_ACCESS);
      Map map = config.getAllConfigEntries();
      Iterator keys = map.keySet().iterator();
      
      String key = null;
      String moduleName = null;
      String modulePath = null;
      while (keys.hasNext()) {
        key = (String) keys.next();
        if (key.indexOf(applicationName + "*") == 0) {
          moduleName = key.substring(applicationName.length() + 1);
          modulePath = (String) map.get(key);
          result.put(moduleName, modulePath);
        }
      }
      return result;
    } catch (ConfigurationException ce) {
      throw new SecurityException(ce.getMessage());
    } finally {
      if (handler != null) {
      	try {
          handler.closeAllConfigurations();
        } catch (Exception _) {
          //$JL-EXC$
        }
      }
    }
  }
  

  
  private JACCUpdateContext getJACCUpdateContext() {
  	try {
      return (JACCUpdateContext) Class.forName("com.sap.security.core.server.ume.service.jacc.JACCUpdateContextImpl").newInstance();
    } catch (NoClassDefFoundError noClassErr) {
      throw new SecurityException(noClassErr.getMessage());
    } catch (ClassNotFoundException ex) {
      throw new SecurityException(ex.getMessage());     
    } catch (InstantiationException e) {
      throw new SecurityException(e.getMessage()); 
    } catch (IllegalAccessException e) {
      throw new SecurityException(e.getMessage()); 
    }
  }
}