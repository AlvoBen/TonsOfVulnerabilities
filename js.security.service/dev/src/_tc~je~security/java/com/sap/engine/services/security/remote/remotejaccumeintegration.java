
package com.sap.engine.services.security.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface RemoteJACCUMEIntegration extends Remote {
  
  public void addUsersAndGroupsToJACCRole(String jaccRole, String policyConfiguration, String[] users, String[] groups) throws RemoteException;
  
  public void addUMERoleToJACCRole(String jaccRole, String policyConfiguration, String umeRole) throws RemoteException;
  
  public void addUMERoleToServiceRole(String jaccRole, String policyConfiguration, String umeRole) throws RemoteException;  
  
  public void removeUMERole(String umeRole) throws RemoteException;
  
  public String getRunAsIdentity(String jaccSecurityRole, String policyCOnfiguration) throws RemoteException;

  public void setRunAsIdentity(String runAsIdentity, String jaccSecurityRole, String policyCOnfiguration) throws RemoteException;
}
