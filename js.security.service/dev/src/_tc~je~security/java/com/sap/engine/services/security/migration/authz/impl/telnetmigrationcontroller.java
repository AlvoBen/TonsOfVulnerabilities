package com.sap.engine.services.security.migration.authz.impl;

import com.sap.engine.services.security.migration.UserManagementUtil;
import com.sap.engine.services.security.migration.authz.AuthorizationMigrationController;
import com.sap.engine.services.security.migration.authz.RoleDescriptor;


/**
 * Migration for service.telnet authorizations.
 * 
 * @author Jako Blagoev
 */
public class TelnetMigrationController extends AuthorizationMigrationController {
 
  private static final String SERVICE_NAME = "telnet";
  private static final RoleDescriptor descriptor = new RoleDescriptor("telnet_login", new String[0], UserManagementUtil.getAdministratorGroup());
  private static final String CONFIG_PATH = "security/configurations/service.telnet";
  
  public TelnetMigrationController() {
    super(SERVICE_NAME);
  }
  
  public RoleDescriptor[] getDefaultDescriptors() {
    return new RoleDescriptor[]{descriptor};
  }
   
  public String getConfigurationPath() {
    return CONFIG_PATH;
  }
}

