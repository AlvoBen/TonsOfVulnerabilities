package com.sap.engine.interfaces.security;

import java.util.Set;

public interface JACCUpdateContext {
  
  public void jaccRoleAdded(String securityRole, String[] umeRoles) throws SecurityException;
  
  public void jaccRoleRemoved(String securityRole) throws SecurityException;
  
  public void jaccRoleMappingsChanged(String securityRole, String[] newUmeRoles) throws SecurityException;
  
  /**
   * Retrieve the PolicyConfigurations of the modules with a specified application type, registered within this application
   */
  public Set getApplicationModulesNames(String applicationName) throws SecurityException;
  
  /**
   * Retrieve the names of the j2ee roles currently deployed for a specified module within this application
   */
  public Set getApplicationModuleRolesNames(String applicationName, String moduleName) throws SecurityException;
  
  /**
   * Retrieve the names of the UME roles currently mapped to a j2ee role deployed for a specified module within this application
   */
  public Set getApplicationModuleMappedServerRoles(String applicationName, String moduleName, String j2eeRoleName) throws SecurityException;
  
  /**
   * Migrates the policy configurations that have permissions with extension HTTP methods, according to the new jacc specification  
   */
  public void migrateJaccPolicyConfiguration() throws SecurityException;
}
