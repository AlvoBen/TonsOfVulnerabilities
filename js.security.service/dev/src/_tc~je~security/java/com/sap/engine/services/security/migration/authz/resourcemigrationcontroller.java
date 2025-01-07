package com.sap.engine.services.security.migration.authz;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.services.security.migration.MigrationFramework;

public abstract class ResourceMigrationController {
  private Hashtable securityRoles = null;
 
  private JACCSecurityRoleMappingContext mapper = null;
  private String resourceName = null;
  private String serviceName = null;
  private static final String RESOURCE_SUFFIX = "security/resource/";
  private static final String ACTIONS_CONFIGURATION = "action";
  private static final String GRANTED_CONFIGURATION = "granted";    
    
  public ResourceMigrationController(String serviceName, String resourceName) {
    this.resourceName = resourceName;
    this.serviceName = serviceName;
    try {
      mapper = (JACCSecurityRoleMappingContext) Class.forName("com.sap.security.core.server.ume.service.jacc.JACCSecurityRoleMapperContextImpl").newInstance();
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
  
  public void init(Hashtable securityRoles) {
    this.securityRoles = securityRoles;  
  }

  public void migrate() throws Exception {
    String configurationPath = getConfigurationPath() + RESOURCE_SUFFIX + resourceName;
    
    ConfigurationHandler configHandler = null;
    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      Configuration config = null;
      try {
        config = configHandler.openConfiguration(configurationPath, ConfigurationHandler.READ_ACCESS);
      } catch (NameNotFoundException _) {
        return; // nothing to migrate
      }
      Configuration actionConfig = config.getSubConfiguration(ACTIONS_CONFIGURATION);
      Map availableActions = actionConfig.getAllSubConfigurations();
      Iterator keys = availableActions.keySet().iterator();
      String action = null;
      Configuration actConfig = null;
      while (keys.hasNext()) {
        action = (String) keys.next();
        actConfig = (Configuration) availableActions.get(action);
        migrateMappingsForAction(action, actConfig); 
      }
    } catch (ConfigurationException ce) {
      throw new SecurityException(ce.getMessage());
    } finally {
      if (configHandler != null) {
        configHandler.closeAllConfigurations();
      }
    } 
      
  }
  
  public abstract String getConfigurationPath();

  public abstract String getMigrationPermissionClass();

  public abstract String getMigrationPermissionName();
  
  public abstract ResourceDescriptor getResourceDescriptor();
  
  public abstract String getActionPrefix();
///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////PRIVATE METHODS//////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////

  private void migrateMappingsForAction(String action, Configuration config) throws ConfigurationException {
    Map map = config.getAllSubConfigurations();
    Iterator instances = map.keySet().iterator();
    String instance = null;
    Configuration instanceConfig = null;
    Configuration grantedConfig = null;
    String[] grantedRoles = null;
    while (instances.hasNext()) {
      instance = (String) instances.next();
      instanceConfig = (Configuration) map.get(instance);
      grantedConfig = (Configuration) instanceConfig.getSubConfiguration(GRANTED_CONFIGURATION);
      grantedRoles = grantedConfig.getAllConfigEntryNames();

      if (grantedRoles.length == 0) {
        continue;
      }
      
      InstanceActionMappingEntry entry = getInstanceActionMappingEntry(action, instance); 
      String[] defaultRoles = (entry != null) ?  entry.getMappedRoles() : new String[0];
      
      for (int i = 0; i < grantedRoles.length; i++) {
        boolean found = false;
        for (int j = 0; j < defaultRoles.length; j++) {
          if (grantedRoles[i].equals(defaultRoles[j])) {
            found = true;
            break;
          }
        }  
        
        if (!found) {
          grantPermission(action, instance, grantedRoles[i]);
        }  
      }
    }
  }
  

  private void grantPermission(String action, String instance, String grantedRole) {
    String umeRole = (String) securityRoles.get(grantedRole);
    
    if (umeRole != null) {
      String actionName = getActionPrefix() + '.' + action + '.' + instance;
      String className = getMigrationPermissionClass();
      String permName = getMigrationPermissionName(); 
      String permValue = action + ":$:" + instance;
      try {
        mapper.addUMERoleToServiceRole(actionName, serviceName, umeRole, className, permName, permValue);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private InstanceActionMappingEntry getInstanceActionMappingEntry(String action, String instance) {
    InstanceActionMappingEntry[] entries = getResourceDescriptor().getDefaultMappings();
    for (int i = 0; i < entries.length; i++) {
      if (entries[i].getAction().equals(action) &&
          entries[i].getInstance().equals(instance)) {
        return entries[i];    
      }
    }
    return null;
  }
}
