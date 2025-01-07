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
package com.sap.engine.services.httpserver.server.hosts.impl;

import static com.sap.engine.services.httpserver.server.Log.CATEGORY_CHANGE_LOG_PROPERTIES;

import com.sap.engine.services.httpserver.HostPropertiesRuntimeInterface;
import com.sap.engine.services.httpserver.interfaces.properties.HostProperties;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.interfaces.exceptions.IllegalHostArgumentException;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.server.hosts.HostPropertiesModifier;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;

import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;

public class HostPropertiesRuntimeInterfaceImpl extends PortableRemoteObject implements HostPropertiesRuntimeInterface {
  private HostProperties hostProperties = null;
  private HostPropertiesModifier hostPropertiesModifier = null;
  private HttpProperties httpProperties = null;

  public HostPropertiesRuntimeInterfaceImpl(HttpProperties httpProperties, HostProperties hostProperties, HostPropertiesModifier hostPropertiesModifier) throws RemoteException {
    this.httpProperties = httpProperties;
    this.hostProperties = hostProperties;
    this.hostPropertiesModifier = hostPropertiesModifier;
  }

  // GETTERS

  public String getHostName() throws java.rmi.RemoteException {
    return hostProperties.getHostName();
  }

  public boolean isKeepAliveEnabled() throws java.rmi.RemoteException {
    return hostProperties.isKeepAliveEnabled();
  }

  public boolean isList() throws java.rmi.RemoteException {
    return hostProperties.isList();
  }

  public boolean isLogEnabled() throws java.rmi.RemoteException {
    return hostProperties.isLogEnabled();
  }

  public boolean isUseCache() throws java.rmi.RemoteException {
    return hostProperties.isUseCache();
  }

  public String getStartPage() throws java.rmi.RemoteException {
    return hostProperties.getStartPage();
  }

  public String getRootDir() throws java.rmi.RemoteException {
    return hostProperties.getRootDir();
  }

  public String[] getAliasNames() throws java.rmi.RemoteException {
    return hostProperties.getAliasNames();
  }

  public String getAliasValue(String key) throws java.rmi.RemoteException {
    String res = hostProperties.getAliasValue(key);
    if (res != null) {
      return res;
    }
    int zoneInd = -1;
    if (httpProperties.getZoneSeparator() != null) {
      zoneInd = key.indexOf(httpProperties.getZoneSeparator());
    }
    if (zoneInd == -1) {
      return null;
    } else {
      return hostProperties.getAliasValue(key.substring(0, zoneInd));
    }
  }

  public boolean isApplicationAlias(String key) throws java.rmi.RemoteException {
    return hostProperties.isApplicationAlias(key);
  }

  public boolean isApplicationAliasEnabled(String key) throws java.rmi.RemoteException {
    return hostProperties.isApplicationAliasEnabled(key);
  }

  // SETTERS

  public void setKeepAliveEnabled(boolean keepAliveEnabled) throws ConfigurationException, java.rmi.RemoteException {
    boolean oldValue = hostProperties.isKeepAliveEnabled();
  	hostPropertiesModifier.setKeepAliveEnabled(keepAliveEnabled);
  	if ((oldValue != keepAliveEnabled) && getUser()) {
  		SimpleLogger.log(Severity.INFO, CATEGORY_CHANGE_LOG_PROPERTIES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000299", 
  				"Property [keep alive] for [{0}] host was changed. Old version [{1}], new version [{2}].", 
  				new Object[]{hostProperties.getHostName(), oldValue, keepAliveEnabled});
  	}
  }

  public void setList(boolean list) throws ConfigurationException, java.rmi.RemoteException {
  	boolean oldValue = hostProperties.isList();
    hostPropertiesModifier.setList(list);
  	if ((oldValue != list) && getUser()) {
  		SimpleLogger.log(Severity.INFO, CATEGORY_CHANGE_LOG_PROPERTIES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000300", 
  				"Property [directory listing] for [{0}] host was changed. Old version [{1}], new version [{2}].", 
  				new Object[]{hostProperties.getHostName(), oldValue, list});
  	}
  }

  public void setLogEnabled(boolean enableLog) throws ConfigurationException, java.rmi.RemoteException {
  	boolean oldValue = hostProperties.isLogEnabled();
    hostPropertiesModifier.setLogEnabled(enableLog);
  	if ((oldValue != enableLog) && getUser()) {
  		SimpleLogger.log(Severity.INFO, CATEGORY_CHANGE_LOG_PROPERTIES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000301", 
  				"Property [log enabled] for [{0}] host was changed. Old version [{1}], new version [{2}].", 
  				new Object[]{hostProperties.getHostName(), oldValue, enableLog});
  	}
  }

  public void setUseCache(boolean useCache) throws ConfigurationException, java.rmi.RemoteException {
  	boolean oldValue = hostProperties.isUseCache();
    hostPropertiesModifier.setUseCache(useCache);
  	if ((oldValue != useCache) && getUser()) {
  		SimpleLogger.log(Severity.INFO, CATEGORY_CHANGE_LOG_PROPERTIES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000302", 
  				"Property [use cache] for [{0}] host was changed. Old version [{1}], new version [{2}].", 
  				new Object[]{hostProperties.getHostName(), oldValue, useCache});
  	}
  }

  public void setStartPage(String startPage) throws ConfigurationException, java.rmi.RemoteException {
  	String oldValue = hostProperties.getStartPage();
    hostPropertiesModifier.setStartPage(startPage);
  	if (((oldValue == null && startPage != null) || (oldValue != null && startPage == null) || 
  			(oldValue != null && startPage != null && !startPage.equals(oldValue))) && getUser()) {
  		SimpleLogger.log(Severity.INFO, CATEGORY_CHANGE_LOG_PROPERTIES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000303", 
  				"Property [start page] for [{0}] host was changed. Old version [{1}], new version [{2}].", 
  				new Object[]{hostProperties.getHostName(), oldValue, startPage});
  	}
  }

  public void setRootDir(String vDir) throws ConfigurationException, java.rmi.RemoteException {
  	String oldValue = hostProperties.getRootDir();
    hostPropertiesModifier.setRootDir(vDir);
  	if (((oldValue == null && vDir != null) || (oldValue != null && vDir == null) || 
  			(oldValue != null && vDir != null && !vDir.equals(oldValue))) && getUser()) {
  		SimpleLogger.log(Severity.INFO, CATEGORY_CHANGE_LOG_PROPERTIES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000304", 
  				"Property [root directory] for [{0}] host was changed. Old version [{1}], new version [{2}].", 
  				new Object[]{hostProperties.getHostName(), oldValue, vDir});
  	}
  }

  public void addHttpAlias(String alias, String value) throws ConfigurationException, IllegalHostArgumentException, java.rmi.RemoteException {
    hostPropertiesModifier.addHttpAlias(alias, value);
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000305", 
  				"HTTP alias [{0}] with path [{1}] was added for [{2}] host.", 
  				new Object[]{alias, value, hostProperties.getHostName()});
  	}
  }

  public void removeHttpAlias(String alias) throws ConfigurationException, java.rmi.RemoteException {
    hostPropertiesModifier.removeHttpAlias(alias);
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000306", 
  				"HTTP alias [{0}] was removed for [{1}] host.", 
  				new Object[]{alias, hostProperties.getHostName()});
  	}
  }

  public void changeHttpAlias(String alias, String value) throws ConfigurationException, java.rmi.RemoteException {
  	String oldValue = hostProperties.getAliasValue(alias);
    hostPropertiesModifier.changeHttpAlias(alias, value);
  	if (((oldValue == null && value != null) || (oldValue != null && value == null) || 
  			(oldValue != null && value != null && !value.equals(oldValue))) && getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000307", 
  				"Path for HTTP alias [{0}] was changed for [{1}] host. Old version [{2}], new version [{3}].", 
  				new Object[]{alias, hostProperties.getHostName(), oldValue, value});
  	}
  }

  public void enableApplicationAlias(String alias) throws ConfigurationException, java.rmi.RemoteException {
    hostPropertiesModifier.enableApplicationAlias(alias, true);
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000308", 
  				"Application alias [{0}] was enabled for [{1}] host.", 
  				new Object[]{alias, hostProperties.getHostName()});
  	}
  }

  public void disableApplicationAlias(String alias) throws ConfigurationException, java.rmi.RemoteException {
    hostPropertiesModifier.disableApplicationAlias(alias);
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(HostPropertiesRuntimeInterfaceImpl.class), "ASJ.http.000309", 
  				"Application alias [{0}] was disabled for [{1}] host.", 
  				new Object[]{alias, hostProperties.getHostName()});
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

}//end of class