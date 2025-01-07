package com.sap.engine.services.security.migration.authz.impl.security;

import com.sap.engine.services.security.migration.authz.InstanceActionMappingEntry;
import com.sap.engine.services.security.migration.authz.ResourceDescriptor;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;
import com.sap.engine.services.security.restriction.AuthPermission;

public class AuthResourceMigrationController extends ResourceMigrationController {


  public AuthResourceMigrationController() {
    super("security", "authentication");
  }

  public String getConfigurationPath() {
    return "";
  }

  public String getMigrationPermissionClass() {
    return "com.sap.engine.services.security.restriction.AuthPermission";
  }


  public String getMigrationPermissionName() {
    return AuthPermission.NAME;
  }

  public ResourceDescriptor getResourceDescriptor() {
    InstanceActionMappingEntry setAuthProperty = new InstanceActionMappingEntry("ALL", "set_authentication_property", new String[]{"administrators"});
    InstanceActionMappingEntry setAuthUserStore = new InstanceActionMappingEntry("ALL", "set_authentication_user_store", new String[]{"administrators"});
    InstanceActionMappingEntry setLoginModuleHelper = new InstanceActionMappingEntry("ALL", "set_login_module_helper", new String[]{"administrators"});
    InstanceActionMappingEntry setLoginModules = new InstanceActionMappingEntry("ALL", "set_login_modules", new String[]{"administrators"});
    InstanceActionMappingEntry updateAuthentication = new InstanceActionMappingEntry("ALL", "update_authentication", new String[]{"administrators"});
    
    return new ResourceDescriptor(new InstanceActionMappingEntry[]{
      setAuthProperty,
      setAuthUserStore,
      setLoginModuleHelper,
      setLoginModules,
      updateAuthentication
    });
  }

  public String getActionPrefix() {
    return "auth";
  }

}
