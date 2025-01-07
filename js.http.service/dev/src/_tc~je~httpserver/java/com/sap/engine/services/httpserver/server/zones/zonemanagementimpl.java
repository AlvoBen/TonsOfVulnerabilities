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

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.lib.util.ArrayInt;
import com.sap.engine.services.httpserver.Zone;
import com.sap.engine.services.httpserver.ZoneManagementInterface;
import com.sap.engine.services.httpserver.server.HttpProviderImpl;
import com.sap.engine.services.httpserver.server.HttpRuntimeInterfaceImpl;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroup;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;


import java.rmi.RemoteException;
import javax.rmi.PortableRemoteObject;

public class ZoneManagementImpl extends PortableRemoteObject implements ZoneManagementInterface {
  private static Location traceLocation = Location.getLocation(ZoneManagementImpl.class);
  private HttpProviderImpl httpProvider;
  private LogonGroupsManager logonGroupsManager;

  public ZoneManagementImpl(HttpProviderImpl httpProvider, LogonGroupsManager logonGroupsManager) throws RemoteException {
    this.httpProvider = httpProvider;
    this.logonGroupsManager = logonGroupsManager;
  }

  public synchronized Zone[] getAllZones() {
    LogonGroup logonGroups[] = logonGroupsManager.getAllLogonGroups();
    Zone zones[] = new Zone[logonGroups.length];
    for (int i = 0; i < logonGroups.length; i++) {
      zones[i] = getZone(logonGroups[i].getLogonGroupName());
//      try {
//        String components[] = new String[logonGroups[i].getAliases().size()];
//        logonGroups[i].getAliases().toArray(components);
//        zones[i].addAliases(components);
//        components =  new String[logonGroups[i].getExactAliases().size()];
//        logonGroups[i].getExactAliases().toArray(components);
//        zones[i].addExactAliases(components);
////      int components[] = null;
////      logonGroups[i].getInstances().toArray(components);
////      zones[i].addInstances(components);
//      } catch (ConfigurationException e) {
//        Log.logError("Cannot retrieve components of for the zone object with name [" + logonGroups[i].getLogonGroupName() + "]", e, null);        
//      }
    }
    return zones;
  }

  public synchronized Zone getZone(String zoneName) {
    try {
      if (logonGroupsManager == null) {
        Log.logError("ASJ.http.000163", 
          "Configuration error. LogonGroupsManager variable is null.", null, null, null);        
      }
      if (logonGroupsManager.getLogonGroup(zoneName) == null) {
        return null;
      } else {
        return new ZoneImpl(zoneName, this, logonGroupsManager);
      }      
    } catch (RemoteException e) {
      Log.logError("ASJ.http.000164", 
        "Cannot retrieve the zone object with name [{0}].", new Object[]{zoneName}, e, null, null, null);
      return null;
    }    
  }

  public synchronized void registerZone(String zoneName) throws IllegalArgumentException, ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneManagementImpl.registerZone(" + zoneName + ")", new Object[0]);
    }
    logonGroupsManager.registerLogonGroup(zoneName);
    
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneManagementImpl.class), "ASJ.http.000313", 
  				"Logon group [{0}] was created.", new Object[]{zoneName});
  	}    
  }

  public synchronized void unregisterZone(String zoneName) throws ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("ZoneManagementImpl.unregisterZone(" + zoneName + ")", new Object[0]);
    }
    logonGroupsManager.unregisterLogonGroup(zoneName);
    
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(ZoneManagementImpl.class), "ASJ.http.000312", 
  				"Logon group [{0}] was removed.", new Object[]{zoneName});
  	}    
  }

  protected int[] getAllServersForGroups(int[] groups) {
    ArrayInt servers = new ArrayInt();
    for (int i = 0; i < groups.length; i++) {
      servers.addAll(httpProvider.getServers(groups[i]));
    }
    return servers.toArray();
  }

  protected int getGroupId(int serverId) {
    return httpProvider.getGroup(serverId);
  }
  
  public boolean isExactAlias(String alias) {
    return logonGroupsManager.isExactAlias(alias);
  }
  
  public String getZoneName(String alias) {
    return logonGroupsManager.getLogonGroupNameForAlias(alias);
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
