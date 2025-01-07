package com.sap.engine.services.security.migration.authz.impl.keystore;

import com.sap.engine.services.security.migration.authz.InstanceActionMappingEntry;
import com.sap.engine.services.security.migration.authz.ResourceDescriptor;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;

public class ViewResourceMigrationController extends ResourceMigrationController {
  private static final String RESOURCE_NAME = "view-actions";
  private static final String KEYSTORE_VIEW_PREFIX = "keystore-view.";

  private String path = null;
  private String keystore = null;
  
  public ViewResourceMigrationController(String keystore, String path) {
    super(KEYSTORE_VIEW_PREFIX + keystore, RESOURCE_NAME);
    this.path = path;
    this.keystore = keystore;
  }


  public String getConfigurationPath() {
    return path;
  }


  public String getMigrationPermissionClass() {
    return "com.sap.engine.services.keystore.impl.security.KeystoreViewPermission";
  }

  public String getMigrationPermissionName() {
    return "keystore-view.view." + keystore;
  }

  public ResourceDescriptor getResourceDescriptor() {
    return new ResourceDescriptor(new InstanceActionMappingEntry[0]);
  }

  public String getActionPrefix() {
    return RESOURCE_NAME;
  }

}
