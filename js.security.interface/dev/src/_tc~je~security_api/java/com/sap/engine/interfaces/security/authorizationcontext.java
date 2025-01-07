/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

/**
 *  Context of the J2EE Engine or a deployed instance of a component that
 * gives access to the access controls to security sensitive resources.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.interfaces.security.SecurityContext
 */
public interface AuthorizationContext {


  /**
   *  Returns the code-based security controls.
   *
   * @return protection domains management context.
   */
  public ProtectionDomainContext getProtectionDomainContext();


  /**
   *  Returns the user-based security controls.
   *
   * @return security resource management context.
   * 
   * @deprecated Resource context is deprecated since NW04 AS Java. Use UME API instead.
   */
  public ResourceContext getSecurityResourceContext();


  /**
   *  Returns the security roles context for the deployed instance of a
   * component or J2EE Engine.
   *
   * @return security roles management context.
   * 
   * @deprecated Security role context is deprecated since NW04 AS Java. Use UME API instead.
   */
  public SecurityRoleContext getSecurityRoleContext();


  /**
   *  Returns the security roles context for the deployed instance of a
   * component or J2EE Engine with mappings for the specified user store.
   *
   * @param  userstore  the name of a registered user store.
   *
   * @return security roles management context.
   * 
   * @deprecated Security role context is deprecated since NW04 AS Java. Use UME API instead.
   */
  public SecurityRoleContext getSecurityRoleContext(String userstore);


  /**
   *  Starts the runnable object in an application thread.
   *
   * @param  runnable  code to run.
   *
   * @throws SecurityException  thrown by the Runnable object or if this is called
   *         from an application thread
   */
  public void doAsPrivileged(Runnable runnable) throws SecurityException;

}

