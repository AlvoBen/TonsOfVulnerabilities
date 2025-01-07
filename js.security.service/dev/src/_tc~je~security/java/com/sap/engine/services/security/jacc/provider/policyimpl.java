package com.sap.engine.services.security.jacc.provider;

import com.sap.engine.lib.security.ProtectedPermissionCollection;
import com.sap.engine.lib.security.domain.ProtectionDomainFactory;
import static com.sap.engine.lib.security.domain.ProtectionDomainFactory.inDebug;
import static com.sap.engine.lib.security.domain.ProtectionDomainFactory.dump;
import com.sap.engine.services.security.server.ProtectionDomainContextImpl;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;

public class PolicyImpl extends Policy {

  private Policy fallback = null;

  public PolicyImpl() {
    //fallback represents the Policy which is used to run the engine
    //So this statement must be executed before all Policy.setPolicy
    //statements 
    fallback = Policy.getPolicy();
  }

  public PermissionCollection getPermissions(CodeSource codesource) {
    if (inDebug()) dump(" + PolicyImpl: getPermissions(" + codesource + ")");
    return new ProtectedPermissionCollection(codesource);
  }

  public void refresh() {
  }

  public Policy getFallbackPolicy() {
    return fallback;
  }

  public PermissionCollection getPermissions(ProtectionDomain domain) {
    if (inDebug()) dump(" + PolicyImpl: getPermissions(2): " + domain.getCodeSource());
    return new ProtectedPermissionCollection(domain.getCodeSource());
  }

  public boolean implies(ProtectionDomain domain, Permission permission) {
    if (inDebug()) dump(" + PolicyImpl: implies {");

    if (!enterLoopless()) {
      if (inDebug()) dump(" + PolicyImpl: implies } LOOP true");
      return true;
    }
        
    if (inDebug()) dump(" + PolicyImpl:   domain: " + domain);
    if (inDebug()) dump(" + PolicyImpl:   permission: " + permission);

    try {
      boolean result = false;
      CodeSource codeSource = domain.getCodeSource();

      if (inDebug()) dump("[POLICY] fallback to standard policy for domain " + codeSource);
      String componentName = null;
      try {
        componentName = ProtectionDomainFactory.getComponentForCodeSource(codeSource);
      } catch (Exception e) {
        //$JL-EXC$
        // bad codesource?
      }
      if (inDebug()) dump(" + PolicyImpl: component: " + componentName);

      PermissionCollection collection = null;
      if (componentName == null) {
        collection = fallback.getPermissions(codeSource);
      } else {
        collection = ProtectionDomainContextImpl.getProtectionDomainsRuntime().getPermissionsStorage().getStoredPermissionCollectionForComponent(componentName);
      }

      if (inDebug()) dump("[POLICY] check against permission colelction: ");
      result = collection.implies(permission);
      if (inDebug()) dump("[POLICY] permission is implied: " + result);
      if (inDebug()) dump("[POLICY] check against permission colelction: " + result);

      if (!result) {
        if (inDebug()) dump("[POLICY] fallback to Sun's policy for domain " + codeSource);
        collection = fallback.getPermissions(domain);
        if (inDebug()) dump("[POLICY] check against permission collection: " + collection);
        Principal[] principals = domain.getPrincipals();
        for (int i = 0; i < principals.length; i++) {
          if (inDebug()) dump("[POLICY] current principal " + i + " : " + principals[i]);
        }
        result = fallback.implies(domain, permission);
        if (inDebug()) dump("[POLICY] permission is implied: " + result);
      }
      if (inDebug()) dump(" + PolicyImpl: implies } Fallback || Principals: " + result);
      return result;
    } finally {
      exitLoopless();
    }
  }

  private static ThreadLocal<Object> loop = new ThreadLocal<Object>();

  private static boolean enterLoopless() {
    if (loop.get() == null) {
      loop.set(new Object());
      return true;
    } else {
      return false;
    }
  }

  private static void exitLoopless() {
    loop.set(null);
  }

}
