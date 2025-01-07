package com.sap.engine.services.security.migration.authz.impl.keystore;

import com.sap.engine.services.security.migration.authz.InstanceActionMappingEntry;
import com.sap.engine.services.security.migration.authz.ResourceDescriptor;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;

public class PropertyResourceMigrationController extends ResourceMigrationController {
  private static final String RESOURCE_NAME = "property-actions";
  private static final String KEYSTORE_VIEW_PREFIX = "keystore-view.";

  private String path = null;
  private String keystore = null;
  
  public PropertyResourceMigrationController(String keystore, String path) {
    super(KEYSTORE_VIEW_PREFIX + keystore, RESOURCE_NAME);
    this.path = path;
    this.keystore = keystore;
  }


  public String getConfigurationPath() {
    return path;
  }


  public String getMigrationPermissionClass() {
    return "com.sap.engine.services.keystore.impl.security.KeystorePropertiesPermission";
  }

  public String getMigrationPermissionName() {
    return "keystore-view.properties." + keystore;
  }

  public ResourceDescriptor getResourceDescriptor() {
    return new ResourceDescriptor(new InstanceActionMappingEntry[0]);
  }

  public String getActionPrefix() {
    return RESOURCE_NAME;
  }

}
