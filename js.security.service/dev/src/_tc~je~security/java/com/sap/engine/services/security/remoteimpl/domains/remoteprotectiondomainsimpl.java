package com.sap.engine.services.security.remoteimpl.domains;

import com.sap.engine.interfaces.security.ProtectionDomainContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.services.security.domains.ProtectionDomainsRuntime;
import com.sap.engine.services.security.remote.domains.RemoteProtectionDomains;
import com.sap.engine.services.security.server.ProtectionDomainContextImpl;

import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import java.util.Vector;

public class RemoteProtectionDomainsImpl extends PortableRemoteObject implements RemoteProtectionDomains {

  ProtectionDomainContext domains = null;
  ProtectionDomainsRuntime runtime = null;

  public RemoteProtectionDomainsImpl(SecurityContext security) throws RemoteException {
    domains = security.getAuthorizationContext().getProtectionDomainContext();
    runtime = ((ProtectionDomainContextImpl) domains).getProtectionDomainsRuntime();
  }

  public void clearPermission(String domain, String permission, String instance, String action) throws RemoteException {
    runtime.clearPermission(domain, permission, instance, action);
  }

  public String[] getDomainsNames() throws RemoteException {
    return runtime.getComponentNames();
  }

  public Vector getAllKnownPermissions() throws RemoteException {
    return runtime.getAllKnownPermissions();
  }

  public void addKnownPermission(String className, String[] names, String[] actions) throws RemoteException {
    runtime.addKnownPermission(className, names, actions);
  }

  public void removeKnownPermission(String className, String[] names, String[] actions) throws RemoteException {
    runtime.addKnownPermission(className, names, actions);
  }

  public void grantPermission(String domain, String permission, String instance, String action) throws RemoteException {
    if (domain != null) {
      runtime.grantPermission(domain, permission, instance, action);
    }
  }

  public Vector getPermissions(String domain) throws RemoteException {
    return runtime.getPermissions(domain);
  }

  public Vector getInheritedPermissions(String domain) throws RemoteException {
    throw new UnsupportedOperationException("Not implemented");
  }

  public Vector getGrantedPermissions(String domain) throws RemoteException {
    throw new UnsupportedOperationException("Not implemented");
  }

  public Vector getDeniedPermissions(String domain) throws RemoteException {
    throw new UnsupportedOperationException("Not implemented");
  }

}