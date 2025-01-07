/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.zones;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Vector;

import javax.rmi.PortableRemoteObject;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.services.httpserver.Zone;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroup;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Class used only in ZoneManagementInterface. The methods are rewriten only in order
 * to keep the backward compatibility
 * 
 * The real data about the logon groups/zones is stored in the
 * LogonGroupImpl/LogonGroupsManager structure
 *  
 * @deprecated
 * @author 
 *
 */
public class ZoneImpl extends PortableRemoteObject implements Zone {
  private static Location traceLocation = Location.getLocation(ZoneImpl.class);
  private ZoneManagementImpl zoneManagement = null;
  private transient LogonGroupsManager logonGroupsManager = null;
  private String zoneName = null;
  
  public ZoneImpl(String zoneName, ZoneManagementImpl zoneManagement, LogonGroupsManager logonGroupsManager) throws RemoteException {
    this.zoneName = zoneName;
    this.zoneManagement = zoneManagement;
    this.logonGroupsManager = logonGroupsManager;
  }
  
  public String getZoneName() {
    return zoneName;
  }
  
  public synchronized int[] getInstances() {
    if (logonGroupsManager.getLogonGroup(zoneName) == null) {
      Log.logError("ASJ.http.000160", 
        "Configuration error. A logon group with name [{0}] does not exist.", 
        new Object[]{zoneName}, new Exception("DebugException"), null, null, null);
      //TODO throw new IllegalStateException("Configuration error. A logon group with name [" + zoneName + "] does not exist.");
    }
    Vector instances = logonGroupsManager.getLogonGroup(zoneName).getInstances();
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.getInstances(" + zoneName + ")"
          + "->" + (instances != null ? instances : "null"), new Object[0]);
    }
    if (instances == null) {      
      return null;
    }
    String instancesArray[] = new String[instances.size()];
    instances.toArray(instancesArray);
    if (instancesArray == null) {
      return null;
    } else {
      int res[] = new int[instancesArray.length];
      for (int i = 0; i < instancesArray.length; i++) {
        res[i] = new Integer(instancesArray[i]).intValue();
      }      
      return res;
    }
  }

	/**
	 * @deprecated
	 */
  public synchronized int[] getServers() {
    return zoneManagement.getAllServersForGroups(getInstances());
  }

  public synchronized String[] getAliases() {
    if (logonGroupsManager.getLogonGroup(zoneName) == null) {
      Log.logError("ASJ.http.000161", 
        "Configuration error. A logon group with name [{0}] does not exist.", 
        new Object[]{zoneName}, new Exception("DebugException"), null, null, null);
      //TODO throw new IllegalStateException("Configuration error. A logon group with name [" + zoneName + "] does not exist.");
    }    
    Vector aliases = logonGroupsManager.getLogonGroup(zoneName).getAliases();
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.getAliases(" + zoneName + ")"
          + "->" + (aliases != null ? aliases : "null"), new Object[0]);
    }
    if (aliases == null) {
      return null;
    }
    Object aliasesArray[] = new String[aliases.size()];
    aliases.toArray(aliasesArray);    
    if (aliasesArray == null) {
      return null;
    } else {
      String res[] = new String[aliasesArray.length];
      System.arraycopy(((Object) (aliasesArray)), 0, res, 0, res.length);
      return res;
    }
  }
  
  public synchronized String[] getExactAliases() {    
    if (logonGroupsManager.getLogonGroup(zoneName) == null) {
      Log.logError("ASJ.http.000162", 
        "Configuration error. A logon group with name [{0}] does not exist.", 
        new Object[]{zoneName}, new Exception("DebugException"), null, null, null);
      //TODO throw new IllegalStateException("Configuration error. A logon group with name [" + zoneName + "] does not exist.");
    }
    Vector aliases = logonGroupsManager.getLogonGroup(zoneName).getExactAliases();
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.getExactAliases(" + zoneName + ")"
          + "->" + (aliases != null ? aliases : "null"), new Object[0]);
    }
    if (aliases == null) {
      return null;
    }
    Object aliasesArray[] = new String[aliases.size()];
    aliases.toArray(aliasesArray);    
    if (aliasesArray == null) {
      return null;
    } else {
      String res[] = new String[aliasesArray.length];
      System.arraycopy(((Object) (aliasesArray)), 0, res, 0, res.length);
      return res;
    }
  }

	/**
	 * @deprecated
	 * @see com.sap.engine.services.httpserver.Zone#addServer(int)
	 */
  public synchronized void addServer(int serverId) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.traceThrowableT(Severity.DEBUG, "ZoneImpl.addServer(" + serverId + ")", new Exception("DEBUG ONLY"));
    }
    addInstance(zoneManagement.getGroupId(serverId));
  }

  public synchronized void addInstance(int groupId) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.addInstance(" + groupId + ")");
    }
    logonGroupsManager.getLogonGroup(zoneName).addInstance(new Integer(groupId).toString());
    
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000322", 
  				"Instance [{0}] was added for [{1}] logon group.", new Object[]{groupId, zoneName});
  	}    
  }

	/**
	 * @see com.sap.engine.services.httpserver.Zone#removeServer(int)
	 * @deprecated
	 */
  public synchronized void removeServer(int serverId) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.traceThrowableT(Severity.DEBUG, "ZoneImpl.removeServer(" + serverId + ")", new Exception("DEBUG ONLY"));
    }
    removeInstance(zoneManagement.getGroupId(serverId));
  }

  public synchronized void removeInstance(int groupId) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.traceThrowableT(Severity.DEBUG, "ZoneImpl.removeInstance(" + groupId + ")", new Exception("DEBUG ONLY"));
    }
    logonGroupsManager.getLogonGroup(zoneName).removeInstance(new Integer(groupId).toString());
    
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000321", 
  				"Instance [{0}] was removed for [{1}] logon group.", new Object[]{groupId, zoneName});
  	}    
  }

	/**
	 * @deprecated
	 */
  public synchronized void addServers(int serverIds[]) throws ConfigurationException {
    for (int i = 0; serverIds != null && i < serverIds.length; i++) {
      addServer(serverIds[i]);
    }
  }

  public synchronized void addInstances(int groupIds[]) throws ConfigurationException {
    for (int i = 0; groupIds != null && i < groupIds.length; i++) {
      addInstance(groupIds[i]);
    }
  }

	/**
	 * @deprecated
	 */
  public synchronized void clearAllServers() throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.traceThrowableT(Severity.DEBUG, "ZoneImpl.clearAllServers()", new Exception("DEBUG ONLY"));
    }
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(zoneName);
    logonGroup.clearAllInstances();
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000320", 
  				"All servers were removed for [{0}] logon group.", new Object[]{zoneName});
  	}    
  }

  public synchronized void clearAllInstances() throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.clearAllInstances()");
    }
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(zoneName);
    logonGroup.clearAllInstances();
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000319", 
  				"All instances were removed for [{0}] logon group.", new Object[]{zoneName});
  	}    
  }

  public synchronized void addAlias(String alias) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.addAlias(" + alias + ")");
    }
    logonGroupsManager.getLogonGroup(zoneName).addAlias(alias);
    
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000317", 
  				"Alias [{0}] was added for [{1}] logon group.", new Object[]{alias, zoneName});
  	}    
  }
  
  public synchronized void addExactAlias(String alias) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.addExactAlias(" + alias + ")");
    }
    logonGroupsManager.getLogonGroup(zoneName).addExactAlias(alias);
    
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000316", 
  				"Exact alias [{0}] was added for [{1}] logon group.", new Object[]{alias, zoneName});
  	}    
  }

  public synchronized void removeAlias(String alias) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.removeAlias(" + alias + ")");
    }
    logonGroupsManager.getLogonGroup(zoneName).removeAlias(alias);
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000315", 
  				"Alias [{0}] was removed for [{1}] logon group.", new Object[]{alias, zoneName});
  	}    
  }
  
  public synchronized void removeExactAlias(String alias) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.removeExactAlias(" + alias + ")");
    }
    logonGroupsManager.getLogonGroup(zoneName).removeExactAlias(alias);
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000314", 
  				"Exact alias [{0}] was removed for [{1}] logon group.", new Object[]{alias, zoneName});
  	}    
  }

  public synchronized void addAliases(String aliases[]) throws ConfigurationException {
    if (traceLocation.beDebug()) {
        traceLocation.debugT("ZoneImpl.addAliases(" + aliases + ")");
      }
    logonGroupsManager.getLogonGroup(zoneName).addAliases(aliases);
    if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000398", 
  				"Alias [{0}] were added for [{1}] logon group.", new Object[]{aliases, zoneName});
  	}    
  }
  
  public synchronized void addExactAliases(String aliases[]) throws ConfigurationException {
	  if (traceLocation.beDebug()) {
	        traceLocation.debugT("ZoneImpl.addExactAliases(" + aliases + ")");
	      }
	    logonGroupsManager.getLogonGroup(zoneName).addExactAliases(aliases);
	    if (getUser()) {
	  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
	  				Location.getLocation(ZoneImpl.class), "ASJ.http.000399", 
	  				"Exact aliases [{0}] were added for [{1}] logon group.", new Object[]{aliases, zoneName});
	  	} 
  }

  /**
   * This method is used by the NWA to update the content of the aliases and
   * exact aliases.
   */
  public synchronized void clearAllAliases() throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneImpl.clearAllAliases()", new Object[0]);
    }
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(zoneName);
    logonGroup.clearAllAliases();
    //Do not delete. method is used for updating both aliases and exact aliases
    logonGroup.clearAllExactAliases();
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneImpl.class), "ASJ.http.000318", 
  				"All aliases (and exact aliases) were removed for [{0}] logon group.", new Object[]{zoneName});
  	}    
  }
  
  private boolean getUser() {
		ThreadContext localTC = ServiceContext.getServiceContext().getThreadSystem().getThreadContext();
		SecurityContextObject securityContextObject = (SecurityContextObject)localTC.getContextObject(localTC.getContextObjectId(SecurityContextObject.NAME));
		SecuritySession ss = securityContextObject.getSession();
		if ((ss != null) && (ss.getAuthenticationConfiguration() != null)) {
			String user = ss.getPrincipal().getName();
			if (user != null) {
				return true;
			}
		}
		return false;
	}//end getUser()
}