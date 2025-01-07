package com.sap.engine.services.security.migration.authz.impl.security;

import com.sap.engine.services.security.restriction.UMPermission;

import com.sap.engine.services.security.migration.authz.InstanceActionMappingEntry;
import com.sap.engine.services.security.migration.authz.ResourceDescriptor;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;

public class UMResourceMigrationController extends ResourceMigrationController {
  
  public UMResourceMigrationController() {
    super("security", "user-management");
  }

  public String getConfigurationPath() {
    return "";
  }

  public String getMigrationPermissionClass() {
    return "com.sap.engine.services.security.restriction.UMPermission";
  }

  public String getMigrationPermissionName() {
    return UMPermission.NAME;
  }

  public ResourceDescriptor getResourceDescriptor() {
    InstanceActionMappingEntry changeConfiguration = new InstanceActionMappingEntry("ALL", "change_configuration", new String[]{"administrators"});
    InstanceActionMappingEntry createAccount = new InstanceActionMappingEntry("ALL", "create_account", new String[]{"administrators"});    
    InstanceActionMappingEntry groupAccount = new InstanceActionMappingEntry("ALL", "group_account", new String[]{"administrators"});
    InstanceActionMappingEntry readAttribute = new InstanceActionMappingEntry("ALL", "read_attribute", new String[]{"administrators"});
    InstanceActionMappingEntry removeAccount = new InstanceActionMappingEntry("ALL", "remove_account", new String[]{"administrators"});
    InstanceActionMappingEntry removeCredentials = new InstanceActionMappingEntry("ALL", "remove_credentials", new String[]{"administrators"});
    InstanceActionMappingEntry viewCredentials = new InstanceActionMappingEntry("ALL", "view_credentials", new String[]{"administrators"});
    InstanceActionMappingEntry writeAttribute = new InstanceActionMappingEntry("ALL", "write_attribute", new String[]{"administrators"});    
    
    return new ResourceDescriptor(
      new InstanceActionMappingEntry[] {
        changeConfiguration,
        createAccount,
        groupAccount,
        readAttribute,
        removeAccount,
        removeCredentials,
        viewCredentials,
        writeAttribute
      }
    );
  }

  public String getActionPrefix() {
    return "um";
  }

}
