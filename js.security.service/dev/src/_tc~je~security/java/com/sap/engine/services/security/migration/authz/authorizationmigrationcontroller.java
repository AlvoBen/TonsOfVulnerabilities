
package com.sap.engine.services.security.migration.authz;

import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.services.security.migration.MigrationFramework;
import com.sap.engine.services.security.migration.SecurityMigrationController;


/**
 * Migration for service.telnet authorizations.
 * 
 * @author Jako Blagoev
 */
public abstract class AuthorizationMigrationController implements SecurityMigrationController{

  private static final String ACTIVE_USER_STORE = "UME User Store";
  private static final String ROLES_SUFFIX = "/security/roles/" + ACTIVE_USER_STORE;
  
  private static final String MIGRATION_ENTRY_KEY = "migrated";
  private static final String MIGRATION_ENTRY_VALUE = "true";

  private String service_name = null;
  private RoleDescriptor[] defaultDescriptors = null;
  
  private JACCSecurityRoleMappingContext mapper = null;
  
  public AuthorizationMigrationController(String service_name) {
    this.service_name = service_name;
    
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////SPECIFIC PARAMETERS FOR EACH SERVICE///////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  
  public abstract RoleDescriptor[] getDefaultDescriptors();  
 
  public abstract String getConfigurationPath();
  
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////SecurityMigrationController METHODS//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public boolean needsMigration() {
    ConfigurationHandler configHandler = null;
    
    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      String configurationPath = getConfigurationPath();
      Configuration configRolesConfiguration = null;
      try {
        configRolesConfiguration = configHandler.openConfiguration(configurationPath + ROLES_SUFFIX, ConfigurationHandler.READ_ACCESS);
      } catch (NameNotFoundException _) {
      	return false;
      }
      String migrationKey = (String) configRolesConfiguration.getConfigEntry(MIGRATION_ENTRY_KEY);
      return (!migrationKey.equals(MIGRATION_ENTRY_VALUE));
    } catch (ConfigurationException e) {
      return true;
    } finally {
      if (configHandler != null) {
        try {
          configHandler.closeAllConfigurations();
        } catch (ConfigurationException e1) {
          //$JL-EXC$
        }
      }
    }  
  }
  
  public void migrate() throws Exception {   
    RoleDescriptor[] defaultDescriptors = getDefaultDescriptors();
    ConfigurationHandler configHandler = null;

    try {
      configHandler = MigrationFramework.getConfigurationHandler();
      String configurationPath = getConfigurationPath();
      Configuration configRolesConfiguration = configHandler.openConfiguration(configurationPath + ROLES_SUFFIX, ConfigurationHandler.WRITE_ACCESS);
      
      for (int i = 0; i < defaultDescriptors.length; i++) {
        migrateSecurityRole(configRolesConfiguration, defaultDescriptors[i]);
      }
      
      configRolesConfiguration.addConfigEntry(MIGRATION_ENTRY_KEY, MIGRATION_ENTRY_VALUE);
      configHandler.commit();
    } catch (Exception e) {
      if (configHandler != null) {
        configHandler.rollback();
      }
      throw new SecurityException(e.getMessage());
    } finally {
      if (configHandler != null) {
        configHandler.closeAllConfigurations();
      }
    }  
  }
  
//////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////PRIVATE METHODS///////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////  
  
  
  private void migrateSecurityRole(Configuration roleConfig, RoleDescriptor descriptor) throws Exception {
    Configuration config = roleConfig.getSubConfiguration(descriptor.getRoleName());
    String[] users  = getKeys(config, true);
    String[] groups = getKeys(config, false);
   
    if (equalArrays(users, descriptor.getUsers()) &&
        equalArrays(groups, descriptor.getGroups())) {
      return;   
    }
   
    String umeRole = mapper.addUsersAndGroupsToJACCRole(descriptor.getRoleName(), service_name, users, groups);
    mapper.addUMERoleToServiceRole(descriptor.getRoleName(), service_name, umeRole); 
  }
  
  private String[] getKeys(Configuration config, boolean user) throws Exception {
    config = config.getSubConfiguration(((user) ? "users" : "groups"));  
    Set users = config.getAllConfigEntries().keySet(); 
    String[] usersArr = new String[users.size()];
    Iterator iter = users.iterator();
    int i = 0;
    while (iter.hasNext()) {
      usersArr[i++] = (String) iter.next();
    }  
    return usersArr;  
  }
 
  private boolean equalArrays(String[] array1, String[] array2) {
    if (array1.length != array2.length) {
      return false;
    }
   
    for (int i = 0; i < array1.length; i++) {
      boolean found = false;
      for (int j = 0; j < array2.length; j++) {
        if (array1[i].equals(array2[j])) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }   
}
