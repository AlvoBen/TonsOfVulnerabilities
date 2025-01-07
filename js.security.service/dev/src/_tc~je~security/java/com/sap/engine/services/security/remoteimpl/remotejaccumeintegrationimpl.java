package com.sap.engine.services.security.remoteimpl;

import java.rmi.RemoteException;

import javax.rmi.PortableRemoteObject;

import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.services.security.remote.RemoteJACCUMEIntegration;


public class RemoteJACCUMEIntegrationImpl extends PortableRemoteObject implements RemoteJACCUMEIntegration {
	private JACCSecurityRoleMappingContext reference = null;

	public RemoteJACCUMEIntegrationImpl(JACCSecurityRoleMappingContext jacc) throws RemoteException {
    this.reference = jacc;
	}
  
	public void addUsersAndGroupsToJACCRole(String jaccRole, String policyConfiguration, String[] users, String[] groups) throws RemoteException {
    try {
      reference.addUsersAndGroupsToJACCRole(jaccRole, policyConfiguration, users, groups);
    } catch (SecurityException se) {
      throw new RemoteException(se.getMessage());
    }      
	}
	
	public void removeUMERole(String umeRole) throws RemoteException {
		try {
		  reference.removeUMERole(umeRole);
		} catch (SecurityException se) {
		  throw new RemoteException(se.getMessage());
		}	  
	}	
  
  public void addUMERoleToJACCRole(String jaccRole, String policyConfiguration, String umeRole) throws RemoteException {
    try {
      reference.addUMERoleToJACCRole(jaccRole, policyConfiguration, umeRole);
    } catch (SecurityException se) {
      throw new RemoteException(se.getMessage());
    }     
  }  
  
  public void addUMERoleToServiceRole(String jaccRole, String policyConfiguration, String umeRole) throws RemoteException {
    try {
      reference.addUMERoleToServiceRole(jaccRole, policyConfiguration, umeRole);
    } catch (SecurityException se) {
      throw new RemoteException(se.getMessage());
    }       	
  } 
  
  public String getRunAsIdentity(String jaccSecurityRole, String policyCOnfiguration) throws RemoteException {
  	try {
      return reference.getRunAsIdentity(jaccSecurityRole, policyCOnfiguration);
    } catch (SecurityException se) {
      throw new RemoteException(se.getMessage());
    }
  }

  public void setRunAsIdentity(String runAsIdentity, String jaccSecurityRole, String policyCOnfiguration) throws RemoteException {
  	try {
      reference.setRunAsIdentity(runAsIdentity, jaccSecurityRole, policyCOnfiguration);
    } catch (SecurityException se) {
      throw new RemoteException(se.getMessage());
    }
  }
}

