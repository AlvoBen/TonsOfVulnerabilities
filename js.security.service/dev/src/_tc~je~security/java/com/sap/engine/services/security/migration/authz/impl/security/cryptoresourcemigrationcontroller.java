package com.sap.engine.services.security.migration.authz.impl.security;

import com.sap.engine.services.security.restriction.CryptoPermission;

import com.sap.engine.services.security.migration.authz.InstanceActionMappingEntry;
import com.sap.engine.services.security.migration.authz.ResourceDescriptor;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;

public class CryptoResourceMigrationController extends ResourceMigrationController {
  
  public CryptoResourceMigrationController() {
    super("security", "cryptography-providers");
  }

  public String getConfigurationPath() {
    return "";
  }

  public String getMigrationPermissionClass() {
    return "com.sap.engine.services.security.restriction.CryptoPermission";
  }

  public String getMigrationPermissionName() {
    return CryptoPermission.NAME;
  }

  public ResourceDescriptor getResourceDescriptor() {
    InstanceActionMappingEntry changeProviderAll = new InstanceActionMappingEntry("ALL", "change_providers", new String[]{"administrators"});
    return new ResourceDescriptor(new InstanceActionMappingEntry[]{changeProviderAll});
  }

  public String getActionPrefix() {
    return "crypto";
  }

}
