/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security.server;

import com.sap.engine.interfaces.security.ProtectionDomainContext;
import com.sap.engine.lib.security.domain.ProtectionDomainFactory;
import com.sap.engine.services.security.domains.ProtectionDomainsRuntime;

import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 *  The context interfaces functionallity targeted at protection domains.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class ProtectionDomainContextImpl implements ProtectionDomainContext {

  private String configuration;
  private ProtectionDomain domain;

  private static ProtectionDomainsRuntime runtime = new ProtectionDomainsRuntime();

  protected ProtectionDomainContextImpl(String configuration) {
    this.configuration = configuration;
  }

  /**
   *  Returns all registered protection domains.
   *
   * @return  the protection domain of the component.
   *
   * @exception  IllegalStateException  thrown if this context is not for a deployed component.
   */
  public ProtectionDomain getProtectionDomain() throws IllegalStateException {
    if (SecurityContextImpl.J2EE_ENGINE_CONFIGURATION.equals(configuration)) {
      throw new IllegalStateException("There is no protection domain for the system.");
    } else if (domain == null) {
      domain = ProtectionDomainFactory.getFactory().getProtectionDomain(configuration);
    }

    return domain;
  }

  public void clearPermission(String domain, String permissionName, String permissionTargetName, String permissionActions) throws SecurityException {
    runtime.clearPermission(domain, permissionName, permissionTargetName, permissionActions);
  }

  public void grantPermission(String domain, String permissionName, String permissionTargetName, String permissionActions) throws SecurityException {
    runtime.grantPermission(domain, permissionName, permissionTargetName, permissionActions);
  }

  /**
   *  Returns a registered protection domain with the given name as associated name.
   *
   * @return  a protection domain.
   */
  public ProtectionDomain getProtectionDomain(String name) {
    return null;
  }

  /**
   *  Returns a protection domain with initialized permissions according to the
   * policy in effect for the specified source code.
   *
   * @param  name        a name to associate with the protection domain.
   * @param  codeSource  the domain's code source.
   *
   * @return  protection domain.
   */
  public ProtectionDomain getProtectionDomain(String name, CodeSource codeSource) {
    return null;
  }

  /**
   *  Returns all registered protection domains.
   *
   * @return  an array of protection domains.
   */
  public ProtectionDomain[] getProtectionDomains() {
    return new ProtectionDomain[0];
  }

  /**
   *  Returns the stack of protection domains in the current access control context.
   *
   * @return  an array of protection domains.
   *
   * @see java.security.AccessControlContext
   */
  public ProtectionDomain[] getProtectionDomainStack() {
    return new ProtectionDomain[0];
  }

  public static ProtectionDomainsRuntime getProtectionDomainsRuntime() {
    return runtime;
  }

  /**
   *  Specifies the code source of the component
   *
   * @param  codeSource  the domain's code source.
   *
   * @exception  IllegalStateException  thrown if this context is not for a deployed component.
   */
  public void setCodeSource(CodeSource codeSource) throws IllegalStateException {
    if (SecurityContextImpl.J2EE_ENGINE_CONFIGURATION.equals(configuration)) {
      throw new IllegalStateException("Cannot set a code source for the root configuration!");
    } else if (domain != null) {
      throw new IllegalStateException("Code source is already specified.");
    } else {
      ProtectionDomainFactory.getFactory().registerProtectionDomain(configuration, codeSource);
      domain = ProtectionDomainFactory.getFactory().getProtectionDomain(configuration);
    }
  }

}

