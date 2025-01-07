/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

/**
 * Context for managing security roles.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @deprecated Security role context is deprecated since NW04 AS Java. Use UME API instead.
 * @see com.sap.engine.interfaces.security.SecurityContext
 */
public interface SecurityRoleContext {


  /**
   *  Default name of the administrators' security role.
   * The root policy configuration always has a configured security role with this name.
   *
   * Value is "administrators".
   */
  public final static String ROLE_ADMINISTRATORS = "administrators";


  /**
   *  Default name of the guests' or anonymous security role.
   * The root policy configuration always has a configured security role with this name.
   *
   * Value is "guests".
   */
  public final static String ROLE_GUESTS = "guests";


  /**
   *  Default name of the security role that implies all users
   * The root policy configuration always has a configured security role with this name.
   *
   * Value is "all".
   */
  public final static String ROLE_ALL = "all";


  /**
   *  Adds a security role for the application.
   *
   * @param  securityRole  the name of the security role.
   *
   * @return  the new security role
   */
  public SecurityRole addSecurityRole(String securityRole);


  /**
   *  Adds a security role for the application.
   *
   * @param  roleName             the name of the new security role.
   * @param  policyConfiguration  the policy configuration that contain the security role.
   * @param  securityRole         the name of the security role in the given policy configuration.
   *
   * @return  the new security role
   */
  public SecurityRole addSecurityRoleReference(String roleName, String policyConfiguration, String securityRole);


  /**
   *   Retrieves handle to the security role with the given name.
   *
   * @param  securityRole  the name of the security role
   *
   * @return  security role handle
   */
  public SecurityRole getSecurityRole(String securityRole);


  /**
   *  Lists all registered security roles of this component.
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRoles();


  /**
   *  Lists all registered security roles for the specified group.
   *
   * @param  groupName  group name
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRolesOfGroup(String groupName);


  /**
   *  Lists all registered security roles for the specified user.
   *
   * @param  userName  user name
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRolesOfUser(String userName);


  /**
   *  Removes a security role for the component.
   *
   * @param  securityRole  the name of the security role.
   */
  public void removeSecurityRole(String securityRole);

}

