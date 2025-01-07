/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import com.sap.engine.frame.core.configuration.Configuration;

/**
 * 
 * Interface for mapping the UME Security Roles to the JACC Security Roles.
 * This interface must be implemented by the JACC Policy provider (UME), and will be used 
 * by the application containers - ejb and web container to define mappings between the 
 * users/groups and the J2EE security roles. This interface may be used also for 
 * administration. 
 * 
 * The interface defines an additional method which must be used only for migration from 
 * 6.40 versions.
 * 
 * @version 7.00
 * @author Jako Blagoev
 */
public interface JACCSecurityRoleMappingContext {
  
	/**
	 *  Default name of the administrators' ume security role.
	 * UME always has a configured security role with this name.
	 *
	 * Value is "Administrator".
	 */  
  public static final String UME_ADMINSTRATOR_SECURITY_ROLE = "Administrator";

  /**
   *  Default name of the guests' ume security role.
   * UME always has a configured security role with this name.
   *
   * Value is "Guest".
   */    
  public static final String UME_GUEST_SECURITY_ROLE        = "Guest";
  
  /**
   *  Default name of the everyones' ume security role.
   * UME always has a configured security role with this name.
   *
   * Value is "Everyone".
   */    
  public static final String UME_EVERYONE_SECURITY_ROLE     = "Everyone"; 
  
  /**
   * Method for mapping the jacc security role to the UME role.
   * This method must be used from the containers.
   * 
   * 
   * @param jaccSecurityRole  the name of the JACC Security Role
   * @param jaccPolicyConfiguration  the name of the JACC Policy Configuration 
   * @param umeRole  the name of the target UME Role which will be mapped
   * @throws SecurityException  thrown if invalid parameter is passed
   */
  public abstract void addUMERoleToJACCRole(String jaccSecurityRole, String jaccPolicyConfiguration, String umeRole) throws SecurityException; 
  
  public abstract void addUMERoleToServiceRole(String securityRole, String serviceName, String umeRole) throws SecurityException;

  public abstract void addUMERoleToServiceRole(String securityRole, String serviceName, String umeRole, String permissionClass, String permissionName, String permissionValue) throws SecurityException;
  
  /**
   * This method will be used only for the migration from 6.40 versions of the j2ee engine.
   * This implementation must create a new ume role, and map it to the jacc security role.
   * 
   * 
   * @param jaccSecurityRole  the name of the JACC Security Role
   * @param jaccPolicyConfiguration  the name of the JACC Policy Configuration 
   * @param users  list of the users that are mapped to the J2EE Role, before the migration.
   * @param groups  list of the groups that are mapped in the J2EE Role, before the migration.
   * @return return the unique name of the newly created ume role
   * @throws SecurityException  thrown if invalid parameter is passed
   */
  public abstract String addUsersAndGroupsToJACCRole(String jaccSecurityRole, String jaccPolicyConfiguration, String[] users, String[] groups) throws SecurityException;

  /**
   *  Removes ume role with the specified name.  
   * 
   * @param roleName the unique name of the UME Role
   * @throws SecurityException thrown if incorrect parameter is given
   */
  public abstract void removeUMERole(String roleName) throws SecurityException; 

  /**
   *  Method for retrieval a 'run_as identity' user mapped to the jacc security role.
   * 
   * @param jaccSecurityRole the unique name of the security role
   * @param policyConfiguration the unique name of the policy configuration
   * @return the unique name of a user mapped to mapped to the given security role
   * @throws SecurityException thrown if incorrect parameter is given
   */
  public abstract String getRunAsIdentity(String jaccSecurityRole, String policyCOnfiguration) throws SecurityException;
  
  /**
   *  Method for setup a 'run_as identity' user mapped to the jacc security role
   * 
   * @param runAsIdentity the unique name of the 'run_as identity' user
   * @param jaccSecurityRole the unique name of the security role
   * @param policyCOnfiguration the unique name of the policy configuration
   *
   * @throws SecurityException thrown if incorrect parameter is given
   */
  public abstract void setRunAsIdentity(String runAsIdentity, String jaccSecurityRole, String policyCOnfiguration) throws SecurityException;

  /**
   * The method is used to specify the way the security generates a dedicated user account for the
   * run-as identity of the jacc security role. If not called the default value is 2 - creation forbidden.
   * @param type
   */
  public abstract void setRunAsAccountGenerationPolicy(byte type, String jaccSecurityRole, String policyConfiguration) throws SecurityException;

}
