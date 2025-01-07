package com.sap.engine.services.security.remote.domains;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 *
 *
 * @author Ilia Kacarov
 */
public interface RemoteProtectionDomains extends Remote {
  /**
   *
   * @param domain
   * @param permissionName
   * @param permissionTargetName
   * @param permissionActions
   * @throws RemoteException
   */
  public void clearPermission(String domain, String permissionName, String permissionTargetName, String permissionActions) throws RemoteException;

  /**
   *
   * @param domain
   * @param permissionName
   * @param permissionTargetName
   * @param permissionActions
   * @throws RemoteException
   */
  public void grantPermission(String domain, String permissionName, String permissionTargetName, String permissionActions) throws RemoteException;

  /**
   *
   * @param domain
   * @return
   * @throws RemoteException
   */
  public Vector getPermissions(String domain) throws RemoteException;

  /**
   *
   * @param domain
   * @return
   * @throws RemoteException
   */
  public Vector getInheritedPermissions(String domain) throws RemoteException;

  /**
   *
   * @param domain
   * @return
   * @throws RemoteException
   */
  public Vector getGrantedPermissions(String domain) throws RemoteException;

  /**
   *
   * @param domain
   * @return
   * @throws RemoteException
   */
  public Vector getDeniedPermissions(String domain) throws RemoteException;

  /**
   *
   * @return
   * @throws RemoteException
   */
  public String[] getDomainsNames() throws RemoteException;

  /**
   *
   * @return
   * @throws RemoteException
   */
  public Vector getAllKnownPermissions() throws RemoteException;

  /**
   *
   * @param className
   * @param instances
   * @param actions
   * @throws RemoteException
   */
  public void addKnownPermission(String className, String[] instances, String[] actions) throws RemoteException;

  /**
   * 
   * @param className
   * @param instance
   * @param actions
   * @throws RemoteException
   */
  public void removeKnownPermission(String className, String[] instance, String[] actions) throws RemoteException;
}