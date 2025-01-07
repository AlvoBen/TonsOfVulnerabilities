package com.sap.engine.services.security.login;

/**
 * @deprecated To be removed
 */
public class AuthorizationEntry {
  
  public boolean impliesSecurityRole(String role) {
    return false;
  }
    
  public void addImpliedRole(String role) {
  }
  
  public boolean notImpliesSecurityRole(String role) {
    return false;
  }
    
  public void addNotImpliedRole(String role) {
  }
  
}