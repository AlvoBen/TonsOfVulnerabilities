package com.sap.engine.services.security.migration.authz.impl.security;

import com.sap.engine.services.security.restriction.DomainsPermission;

import com.sap.engine.services.security.migration.authz.InstanceActionMappingEntry;
import com.sap.engine.services.security.migration.authz.ResourceDescriptor;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;

public class DomainsResourceMigrationController extends ResourceMigrationController {
  
  public DomainsResourceMigrationController() {
    super("security", "protection-domains");
  }

  public String getConfigurationPath() {
    return "";
  }

  public String getMigrationPermissionClass() {
    return "com.sap.engine.services.security.restriction.DomainsPermission";
  }

  public String getMigrationPermissionName() {
    return DomainsPermission.NAME;
  }

  public ResourceDescriptor getResourceDescriptor() {
    InstanceActionMappingEntry denyPermission = new InstanceActionMappingEntry("ALL", "deny_permission", new String[]{"administrators"});
    InstanceActionMappingEntry grantPermission = new InstanceActionMappingEntry("ALL", "grant_permission", new String[]{"administrators"});    
    return new ResourceDescriptor(
      new InstanceActionMappingEntry[] {
        denyPermission,
        grantPermission
      }
    );
  }

  public String getActionPrefix() {
    return "domains";
  }

}
