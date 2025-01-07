/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 *  The context interfaces functionallity targeted at protection domains.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.interfaces.security.AuthorizationContext
 */
public interface ProtectionDomainContext {

  /**
   *  Returns all registered protection domains.
   *
   * @return  the protection domain of the component.
   *
   * @exception  IllegalStateException  thrown if this context is not for a deployed component.
   */
  public ProtectionDomain getProtectionDomain() throws IllegalStateException;

  /**
   * Clears granted permission {<permissionName>, <permissionTargetName>, <permissionActions>}
   * from the domain <domain> and from the persistent storage. If the permission is not granted,
   * exits silently.
   *
   * @param domain
   * @param permissionName
   * @param permissionTargetName
   * @param permissionActions
   * @throws Exception TODO: !!!!
   */
  public void clearPermission(String domain, String permissionName, String permissionTargetName, String permissionActions) throws SecurityException;

  /**
   * Adds a permission {<permissionName>, <permissionTargetName>, <permissionActions>} to the
   * <domain> permission's collection and saves the change in the persistent storage.
   *
   * @param domain
   * @param permissionName
   * @param permissionTargetName
   * @param permissionActions
   * @throws Exception TODO: !!!!
   */
  public void grantPermission(String domain, String permissionName, String permissionTargetName, String permissionActions) throws SecurityException;

  /**
   *  Returns a registered protection domain with the given name as associated name.
   *
   * @return  a protection domain.
   */
  public ProtectionDomain getProtectionDomain(String name);


  /**
   * @deprecated  Use setCodeSource() for a specific protection domain context.
   *
   *  Returns a protection domain with initialized permissions according to the
   * policy in effect for the specified source code.
   *
   * @param  name        a name to associate with the protection domain.
   * @param  codeSource  the domain's code source.
   *
   * @return  protection domain.
   */
  public ProtectionDomain getProtectionDomain(String name, CodeSource codeSource);


  /**
   *  Returns all registered protection domains.
   *
   * @return  an array of protection domains.
   */
  public ProtectionDomain[] getProtectionDomains();


  /**
   *  Returns the stack of protection domains in the current access control context.
   *
   * @return  an array of protection domains.
   *
   * @see java.security.AccessControlContext
   */
  public ProtectionDomain[] getProtectionDomainStack();


  /**
   *  Specifies the code source of the component
   *
   * @param  codeSource  the domain's code source.
   *
   * @exception  IllegalStateException  thrown if this context is not for a deployed component.
   */
  public void setCodeSource(CodeSource codeSource) throws IllegalStateException;

}

