package com.sap.engine.services.security.migration.authz;

public class RoleDescriptor {
  
  private String role_name = null;
  private String[] users      = null;
  private String[] groups     = null;
  
  public RoleDescriptor(String role_name, String[] users, String[] groups) {
    this.role_name = role_name;
    this.users = users;
    this.groups = groups;
  }
  
  public String getRoleName() {
    return role_name;
  }
  
  public String[] getUsers() {
    return users;
  }
  
  public String[] getGroups() {
    return groups;
  }
}
