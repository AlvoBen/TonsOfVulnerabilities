package com.sap.engine.services.security.migration.authz;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.interfaces.security.SecurityRoleContext;
import com.sap.engine.services.security.migration.SecurityMigrationController;
import com.sap.engine.services.security.migration.MigrationFramework;

public abstract class J2EERolesToUMERolesMigrationController implements SecurityMigrationController {
  private String path = null;
  private Hashtable mappings = null;
  private JACCSecurityRoleMappingContext mapper = null;
  
  private static final String SECURITY_ROLES_SUFFIX = "security/roles/UME User Store";
  
  private static final String REFERENCE_ROLE = "reference_role";
  private static final String REFERENCE_POLICY = "reference_policy";
  
  private static final String ROOT_POLICY = "SAP-J2EE-Engine";
  
  private static final String USERS_CONFIG = "users";
  private static final String GROUPS_CONFIG = "groups";

  public J2EERolesToUMERolesMigrationController(String path) {
    this.path = path;
    this.mappings = new Hashtable();
 
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
  
  public void migrate() throws Exception {
    ConfigurationHandler handler = null;
    
    try {
      handler = MigrationFramework.getConfigurationHandler();
      Configuration config = handler.openConfiguration(path + SECURITY_ROLES_SUFFIX, ConfigurationHandler.READ_ACCESS);
      Map map = config.getAllSubConfigurations();
      Iterator keys = map.keySet().iterator();
      
      String roleName = null;
      Configuration roleConfig = null;
      while (keys.hasNext()) {
        roleName = (String) keys.next();
        roleConfig = (Configuration) map.get(roleName);
        migrateJ2EERole(roleName, roleConfig);
      }
    } catch (NameNotFoundException _) {
      return; // nothing to migrate
    } catch (ConfigurationException ce) {
      throw new SecurityException(ce.getMessage());
    } finally {
      if (handler != null) {
        handler.closeAllConfigurations();
      }
    }
  }


  public boolean needsMigration() {
    return true;
  }
  
  public Hashtable getResultMappings() {
    return mappings;
  }
  
  public abstract String getPolicyConfiguration();
  
/////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////PRIVATE METHODS///////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

  private void migrateJ2EERole(String roleName, Configuration roleConfig) throws Exception {
    try {
      if (roleConfig.existsConfigEntry(REFERENCE_POLICY)) {
        String refPolicy = (String) roleConfig.getConfigEntry(REFERENCE_POLICY);
        String refRole = (String) roleConfig.getConfigEntry(REFERENCE_ROLE);
        if (refPolicy.equals(ROOT_POLICY)) {
          String umeRole = translateServerRole(refRole);
          mappings.put(roleName, umeRole);
        } else {
          //to do sth?
        }
      } else {
        Configuration usersConfig = roleConfig.getSubConfiguration(USERS_CONFIG);
        Configuration groupsConfig = roleConfig.getSubConfiguration(GROUPS_CONFIG);
        
        String[] users = usersConfig.getAllConfigEntryNames();
        String[] groups = groupsConfig.getAllConfigEntryNames();
        
        if (users.length == 0 && groups.length == 0) {
          return;
        }
        
        try {
          String umeRole = mapper.addUsersAndGroupsToJACCRole(roleName, getPolicyConfiguration(), users, groups);
          mappings.put(roleName, umeRole);
        } catch (Exception e) {
          // to log
          e.printStackTrace();
        }
      }
      
    } catch (NameNotFoundException e) {
      //$JL-EXC$
      
    } 
  }
  
  private String translateServerRole(String role) {
    if (role.equals(SecurityRoleContext.ROLE_ADMINISTRATORS)) {
      return JACCSecurityRoleMappingContext.UME_ADMINSTRATOR_SECURITY_ROLE;
    } else if (role.equals(SecurityRoleContext.ROLE_GUESTS)) {
      return JACCSecurityRoleMappingContext.UME_GUEST_SECURITY_ROLE;
    } if (role.equals(SecurityRoleContext.ROLE_ALL)) {
      return JACCSecurityRoleMappingContext.UME_EVERYONE_SECURITY_ROLE;
    }
    
    return role;        
  }
}
