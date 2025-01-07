package com.sap.engine.services.security.migration.authz;

public class ResourceDescriptor {
  
  private InstanceActionMappingEntry[] entries = null;
  
  public ResourceDescriptor(InstanceActionMappingEntry[] entries) {
    this.entries = entries;
  }
  
  public InstanceActionMappingEntry[] getDefaultMappings() {
    return entries;
  }
}
