package com.sap.engine.services.security.migration.authz.impl;

import com.sap.engine.services.security.migration.UserManagementUtil;
import com.sap.engine.services.security.migration.authz.AuthorizationMigrationController;
import com.sap.engine.services.security.migration.authz.RoleDescriptor;


/**
 * Migration for naming authorizations.
 * 
 * @author Jako Blagoev
 */
public class NamingMigrationController extends AuthorizationMigrationController {
 
  private static final String SERVICE_NAME = "naming";
  private static final RoleDescriptor initial_context_descriptor = new RoleDescriptor("jndi_get_initial_context", new String[0], UserManagementUtil.getEveryoneGroup());
  private static final RoleDescriptor all_operations_descriptor  = new RoleDescriptor("jndi_all_operations", new String[0], UserManagementUtil.getAdministratorGroup());  
  private static final String CONFIG_PATH = "security/configurations/service.naming";
  
  public NamingMigrationController() {
    super(SERVICE_NAME);
  }
  
  public RoleDescriptor[] getDefaultDescriptors() {
    return new RoleDescriptor[]{
      initial_context_descriptor,
      all_operations_descriptor
    };
  }
   
  public String getConfigurationPath() {
    return CONFIG_PATH;
  }
}

