package com.sap.engine.services.security.migration.authz;

public class InstanceActionMappingEntry {
  
  private String[] roles;
  private String action;
  private String instance;

  public InstanceActionMappingEntry(String instance, String action, String[] roles) {
    this.instance = instance;
    this.action = action;
    this.roles = roles;
  }
  
  public String getInstance() {
    return instance;
  }
  
  public String getAction() {
    return action;
  }
  
  public String[] getMappedRoles() {
    return roles;
  }
}
