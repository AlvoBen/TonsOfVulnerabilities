/*
 * Copyright (c) 2000-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import com.sap.bc.proj.jstartup.sadm.ShmApplication;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.bc.proj.jstartup.sadm.ShmNoMemoryException;
import com.sap.bc.proj.jstartup.sadm.ShmNotFoundException;
import com.sap.bc.proj.jstartup.sadm.ShmTableInfo;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.shm.ShmConfiguration;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.lib.util.ConcurrentReadHashMap;
import com.sap.engine.services.httpserver.CacheManagementInterface;
import com.sap.engine.services.httpserver.interfaces.HttpHandler;
import com.sap.engine.services.httpserver.interfaces.HttpProvider;
import com.sap.engine.services.httpserver.interfaces.exceptions.HostStoreException;
import com.sap.engine.services.httpserver.interfaces.exceptions.HttpShmException;
import com.sap.engine.services.httpserver.interfaces.exceptions.IllegalHostArgumentException;
import com.sap.engine.services.httpserver.interfaces.properties.HostProperties;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.lib.LRUMap;
import com.sap.engine.services.httpserver.server.errorreport.ErrorReportInfoBean;
import com.sap.engine.services.httpserver.server.hosts.Host;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;
import com.sap.engine.services.httpserver.server.zones.ZoneManagementImpl;

public class HttpProviderImpl implements HttpProvider {
  private static String sapDefaultApplName = "sap.com/com.sap.engine.docs.examples";
  
  private HttpProperties httpProperties = null;
  private HttpServerFrame frame = null;
  private HttpHandler servletJSPService = null;
  private HttpHosts httpManager = null;
  private ConcurrentHashMapObjectObject startedApplications = new ConcurrentHashMapObjectObject();
  private ConcurrentHashMapObjectObject urlSessionTrackingApplications = new ConcurrentHashMapObjectObject();
  private ClusterMonitor clusterMonitor = null;
  private CacheManagementInterface cacheManagement;
  private LRUMap<String, ErrorReportInfoBean> errorReportInfos; 


  public HttpProviderImpl(HttpHosts httpManager, HttpProperties httpProperties,
      HttpServerFrame frame, ClusterMonitor clusterMonitor) {
    this.httpManager = httpManager;
    this.httpProperties = httpProperties;
    this.frame = frame;
    this.clusterMonitor = clusterMonitor;
    errorReportInfos = new LRUMap<String, ErrorReportInfoBean>(4*httpProperties.getFCAServerThreadCount()); 
  }


  public LogonGroupsManager getLogonGroupManager() {
    return (LogonGroupsManager) frame.getLogonGroupsManager();
  }

  public ZoneManagementImpl getZoneManagement() {
    try {
      return (ZoneManagementImpl) frame.getHttpRuntimeInterface().getZoneManagementInterface();
    } catch (RemoteException e) {
      Log.logWarning("ASJ.http.000076", 
        "Cannot initialize remote interface for remote zones administration for logon groups.", e, null, null, null);
      return null;
    }
  }
  
  public int[] getServers(int group) {
    return frame.getServers(group);
  }

  public int getGroup(int server) {
    return frame.getGroup(server);
  }

  public void registerHttpHandler(HttpHandler servletAndJsp) {
    if (servletAndJsp == null) {
      startedApplications.clear();
    }
    servletJSPService = servletAndJsp;
  }

  public void removeSession(String cookie) {
    if (servletJSPService != null) {
      servletJSPService.removeSession(cookie);
    }
  }

  private void shmApplicationStarted(String applicationName, String[] aliases) throws HttpShmException {
  	ShmApplication app = null;
  	try {
    	app = new ShmApplication(applicationName);
    	app.start();
    } catch (ShmNoMemoryException shmEx) {
      throw new HttpShmException(HttpShmException.CANNOT_REGISTER_APP_IN_SHM_DETAILED,
          getDetailedMessageParameters(app, applicationName, aliases, shmEx), shmEx);
    } catch (ShmException shmEx) {
      throw new HttpShmException(HttpShmException.CANNOT_REGISTER_APP_IN_SHM,
          getMessageParameters(applicationName, aliases), shmEx);
    }
  }

  private void shmApplicationStoped(String applicationName, String[] aliases) throws HttpShmException {
    ShmApplication app = null;
    try {
      app = new ShmApplication(applicationName);
    	app.stop();
    } catch (ShmNoMemoryException shmEx) {
      throw new HttpShmException(HttpShmException.CANNOT_UNREGISTER_APP_IN_SHM_DETAILED,
          getDetailedMessageParameters(app, applicationName, aliases, shmEx), shmEx);
    } catch (ShmException shmEx) {
      throw new HttpShmException(HttpShmException.CANNOT_UNREGISTER_APP_IN_SHM,
          getMessageParameters(applicationName, aliases), shmEx);
    } 
  }

  public void changeLoadBalance(String applicationName, String[] aliases, boolean start) throws HttpShmException {
    if (clusterMonitor.getCurrentParticipant().getState() == ClusterElement.DEBUGGING) {
      return;
    }
    if (aliases == null || aliases.length == 0) {
      return;
    }
    if (start) {
      startedApplications.put(applicationName, aliases);
      shmApplicationStarted(applicationName, aliases);
    } else {
      shmApplicationStoped(applicationName, aliases);
      httpManager.notifyStopApplicationAlias(aliases);
      startedApplications.remove(applicationName);
      for (int i = 0; i < aliases.length; i++) {
        urlSessionTrackingApplications.remove(aliases[i]);
      }
    }
  }

  public void urlSessionTracking(String webApplication, boolean isURLSessionTracking) {
    urlSessionTrackingApplications.put(webApplication, Boolean
      .valueOf(isURLSessionTracking));
  }

  public boolean urlSessionTracking(String applicationName, String webApplication, boolean isURLSessionTracking) throws HttpShmException {
    urlSessionTrackingApplications.put(webApplication, Boolean.valueOf(isURLSessionTracking));
    ShmApplication app = null;
    try {
      app = new ShmApplication(applicationName);

      if (!webApplication.startsWith("/")) {
        webApplication = "/" + webApplication;
      }      
      if (applicationName.equalsIgnoreCase(sapDefaultApplName) && webApplication.equals("/")) { 
        // SAP default appplication is starting; check if there is a customer
        // default application is deployed on the system; if there is a customer 
        // default application, SAP default application should not start. This is 
        // indicated here with DuplicatedAliasException
        try {         
          if (ShmApplication.findAlias(webApplication) != null) {            
            // such alias already exists 
            // it is default appilication => throw exception
            return false;
          }          
        } catch (ShmNotFoundException shmEx) {
          if (LOCATION_HTTP.bePath()) {
            Log.tracePath(LOCATION_HTTP, "Alias [/] not found in SHM. applicationName = [" + applicationName + "]; webApplication = ["
                + webApplication + "]; isURLSessionTracking = [" + isURLSessionTracking + "];", shmEx, -1, 
                "HttpProviderImpl", "urlSessionTracking(applicationName, webApplication, isURLSessionTracking)");
          }
        }       
        // either there is no customer default appplication or there is none default alas
      }
      app.addAlias(webApplication, webApplication.hashCode(), isURLSessionTracking);
      return true;
    } catch (ShmNoMemoryException shmEx) {
      throw new HttpShmException(HttpShmException.CANNOT_REGISTER_ALIAS_IN_SHM_DETAILED,
          getDetailedMessageParameters(app, applicationName, webApplication, shmEx), shmEx);
    } catch (ShmException shmEx) {
      throw new HttpShmException(HttpShmException.CANNOT_REGISTER_ALIAS_IN_SHM,
          getMessageParameters(applicationName, webApplication), shmEx);
    }
  }

  public void clearCache() {
    frame.clearRemoteCache();
  }

  public void clearCacheByAlias(String alias) {
    frame.clearRemoteCache(alias);
  }

  public Vector getAllApplicationAliases() {
    Vector result = new Vector();

    Host[] hosts = httpManager.getAllHosts();
    for (int i = 0; hosts != null && i < hosts.length; i++) {
      //Alias name to "true" or "false" for visible or not visible application aliases
      ConcurrentReadHashMap aliases = hosts[i].getHostProperties().getApplications();
      Enumeration enumeration = aliases.keys();
      while (enumeration.hasMoreElements()) {
        String alias = (String) enumeration.nextElement();
        if (!result.contains(alias)) result.add(alias);
      }
    }

    return result;
  }//end of getAllApplicationAliases()

  /**
   * Add the alias to an application. Update the configuration too.
   *
   * @param alias value of the alias
   * @throws HostStoreException           if writing this property configuration throws an ConfigurationException
   * @throws IllegalHostArgumentException if alias or its substring already exists
   * @deprecated use HttpProvider.addApplicationAlias(String alias, boolean persistent)
   *             where you can specify to update or not the configuration.
   */
  public void addApplicationAlias(String alias) throws HostStoreException, IllegalHostArgumentException {
    addApplicationAlias(alias, true);
  }//end of addApplicationAlias(String alias)

  /**
   * Add the alias to an application. Update the configuration depending on persistent property.
   *
   * @param alias      value of the alias
   * @param persistent if true updates the configuration, else - only runtime structures.
   * @throws HostStoreException           if writing this property configuration throws an ConfigurationException
   * @throws IllegalHostArgumentException if alias or its substring already exists
   */
  public void addApplicationAlias(String alias, boolean persistent) throws HostStoreException, IllegalHostArgumentException {
    try {
      httpManager.addApplicationAlias(alias, persistent);
    } catch (ConfigurationException e) {
      throw new HostStoreException(HostStoreException.CANNOT_ADD_APP_ALIAS, new Object[]{alias}, e);
    }
    frame.clearRemoteCache(alias);
  }//end of addApplicationAlias(String alias, boolean persistent)
  
  /**
   * Adds all the aliases of the given application. 
   * Updates the configuration depending on persistent property.
   *
   * @param appAlias value of the application name
   * @param aliasesCanonicalized all the web aliases of the application
   * @param persistent if true updates the configuration, else - only runtime structures.
   * @throws HostStoreException           if writing this property configuration throws an ConfigurationException
   * @throws IllegalHostArgumentException if alias or its substring already exists
   */
  public void addAllApplicationAliases(String applicationName, String[] aliasesCanonicalized, boolean persistent) 
                                          throws HostStoreException, IllegalHostArgumentException {
    try {
      httpManager.addAllApplicationAliases(applicationName, aliasesCanonicalized, persistent);
    } catch (ConfigurationException e) {
      throw new HostStoreException(HostStoreException.CANNOT_ADD_ALL_APP_ALIASES, new Object[]{applicationName}, e);
    }
    //It is not possible to combine the following (CSN 2799735 2006) - they are sending messages to ICM for each web alias
    for (String currentAlias : aliasesCanonicalized) {
      frame.clearRemoteCache(currentAlias);
    }
  }

  /**
   * Checks the alias to an application.
   *
   * @param alias value of the alias.
   * @throws IllegalHostArgumentException if alias or its substring already exists.
   */
  public void checkApplicationAlias(String alias) throws IllegalHostArgumentException {
    httpManager.checkApplicationAlias(alias);
    frame.clearRemoteCache(alias);
  }//end of checkApplicationAlias(String alias)

  /**
   * Adds the path to the alias to an application
   *
   * @param alias value of the alias
   * @param dir   application directory
   */
  public void startApplicationAlias(String alias, String dir) {
    httpManager.startApplicationAlias(alias, dir);
    frame.clearRemoteCache(alias);
  }

  /**
   * Removes some alias to an application. Update the configuration too.
   *
   * @param alias alias an application
   * @throws HostStoreException thrown if cannot remove the application alias from this host
   * @deprecated use HttpProvider.removeApplicationAlias(String alias, boolean persistent)
   *             where you can specify to update or not the configuration.
   */
  public void removeApplicationAlias(String alias) throws HostStoreException {
    removeApplicationAlias(alias, true);
  }

  /**
   * Removes some alias to an application. Update the configuration depending on persistent property.
   *
   * @param alias      alias an application
   * @param persistent if true updates the configuration, else - only runtime structures.
   * @throws HostStoreException thrown if cannot remove the application alias from this host
   */
  public void removeApplicationAlias(String alias, boolean persistent) throws HostStoreException {
    try {
      httpManager.removeApplicationAlias(alias, persistent);
    } catch (ConfigurationException e) {
      throw new HostStoreException(HostStoreException.CANNOT_REMOVE_APP_ALIAS, new Object[]{alias}, e);
    }
    frame.clearRemoteCache(alias);
  }

  public void addHttpAlias(String alias, String value) throws HostStoreException, IllegalHostArgumentException {
    Host[] allHosts = httpManager.getAllHosts();
    for (int i = 0; i < allHosts.length; i++) {
      try {
        allHosts[i].getHostPropertiesModyfier().addHttpAlias(alias, value);
      } catch (ConfigurationException e) {
        throw new HostStoreException(HostStoreException.CANNOT_ADD_HTTP_ALIAS, new Object[]{alias, value}, e);
      }
    }
    frame.clearRemoteCache(alias);
  }

  /**
   * Implements an deprecated method that is never used
   */
  public boolean containsHttpAlias(String alias) {
    Host[] allHosts = httpManager.getAllHosts();
    for (int i = 0; i < allHosts.length; i++) {
      if (allHosts[i].getHostProperties().getHttpAliases().containsKey(alias)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsApplicationAlias(String applicationAlias) {
    Host[] allHosts = httpManager.getAllHosts();
    for (int i = 0; i < allHosts.length; i++) {
      if (allHosts[i].getHostProperties().getApplications().containsKey(applicationAlias)) {
        return true;
      }
    }
    return false;
  }

  public void removeHttpAlias(String alias) throws HostStoreException {
    Host[] allHosts = httpManager.getAllHosts();
    for (int i = 0; i < allHosts.length; i++) {
      try {
        allHosts[i].getHostPropertiesModyfier().removeHttpAlias(alias);
      } catch (ConfigurationException e) {
        throw new HostStoreException(HostStoreException.CANNOT_REMOVE_HTTP_ALIAS, new Object[]{alias}, e);
      }
    }
    frame.clearRemoteCache(alias);
  }

  public HttpProperties getHttpProperties() {
    return httpProperties;
  }

  public HostProperties getHostProperties(String hostName) {
    if (hostName == null || httpManager.getHost(hostName) == null) {
      return null;
    }
    return httpManager.getHost(hostName).getHostProperties();
  }

  public CacheManagementInterface getCacheManagementInterface() {
    if (cacheManagement == null) {
      cacheManagement = new CacheManagementInterfaceImpl();
    }
    return cacheManagement;
  }
  
  // ------------------------ PROTECTED ------------------------

  public HttpHandler getWebContainer() {
    return servletJSPService;
  }

  public void setWebContainer(HttpHandler servletJSPService) {
    this.servletJSPService = servletJSPService;
  }

  // ------------------------ PRIVATE ------------------------

  /**
   * Read the SHM properties from configuration and return a list for the log message.
   * @return list of the properties for the error log message
   */
  private String getShmPropertiesMessage() {
    StringBuffer result = new StringBuffer();
    ServiceContext srvSCtxt = null;
    ApplicationServiceContext apSCtxt = null;
    CoreContext coreCtx = null;
    ConfigurationHandlerFactory configHndlFactory = null; //free this handler after finished
    CommonClusterFactory factory = null;
    String instanceID = null;
    ConfigurationLevel instance = null;
    ShmConfiguration shm = null;
    try {
      //Get the ConfigurationHandlerFactory impl, offline or online configuration manager:
      srvSCtxt = ServiceContext.getServiceContext();
      apSCtxt = srvSCtxt.getApplicationServiceContext();
      coreCtx = apSCtxt.getCoreContext();
      configHndlFactory = coreCtx.getConfigurationHandlerFactory();
      factory = ClusterConfiguration.getClusterFactory(configHndlFactory);
      //Get the ID of the instance that will be set as performer ID, the method expects it in this syntax IDYYYYYYY:
      instanceID = "ID" + apSCtxt.getClusterContext().getClusterMonitor().getCurrentParticipant().getGroupId();
      instance = factory.openConfigurationLevel(CommonClusterFactory.LEVEL_INSTANCE, instanceID);
      shm = instance.getShmAccess();
      Properties prop = shm.getShmProperties();
      for (Enumeration en = prop.keys(); en.hasMoreElements();) {
        String pptyName = (String) en.nextElement();
        result.append(pptyName);
        result.append("=");
        result.append(prop.getProperty(pptyName));
        result.append(",");
      }
      //All properties: BrSessions, RoConnections, EjbSessions, Caches, Threads, EntryPoints, Processes, WebSessions, Aliases, Applications, Ejbs
    } catch (Exception e1) {
      if (LOCATION_HTTP.beDebug()) {
				LOCATION_HTTP.debugT("HttpProviderImpl.getShmPropertiesMessage(): Failed to read SHM properties from configuration: " + e1.getMessage());
			}
    }
    return result.toString();
  }

  /** Get parameters for error messages CANNOT_REGISTER_APP_IN_SHM and CANNOT_UNREGISTER_APP_IN_SHM */
  private Object[] getMessageParameters(String applicationName, String[] aliases) {
    return new Object[]{applicationName, (aliases != null ? Arrays.asList(aliases).toString() : "<>")};
  }

  /** Get parameters for error message CANNOT_REGISTER_ALIAS_IN_SHM */
  private Object[] getMessageParameters(String applicationName, String webApplication) {
    return new Object[]{webApplication, applicationName};
  }

  /** Get parameters for detailed error message CANNOT_REGISTER_APP_IN_SHM_DETAILED and CANNOT_UNREGISTER_APP_IN_SHM_DETAILED */
  private Object[] getDetailedMessageParameters(ShmApplication app, String applicationName, String[] aliases,  ShmNoMemoryException shmEx) {
    Object[] result;
    try {
      ShmTableInfo sti = shmEx.getInfo();
      String aliasesList = (aliases != null ? Arrays.asList(aliases).toString() : "<>");
      if (sti != null) {
        result = new Object[]{applicationName, aliasesList, ShmTableInfo.getName(sti.type), sti.toString(), getShmPropertiesMessage(),
            (app != null) ? app.toString() : "", ShmTableInfo.getName(sti.type)};
      } else {
        result = new Object[]{applicationName, aliasesList, "", "", getShmPropertiesMessage(), (app != null) ? app.toString() : "", ""};
      }
    } catch (Exception e) {
      if (LOCATION_HTTP.beDebug()) {
				LOCATION_HTTP.debugT("HttpProviderImpl.getDetailedMessageParameters(): Failed to read SHM properties from Shared Memory: " + e.getMessage());
			}
			result = new Object[]{applicationName};
    }
    return result;
  }

  /** Get parameters for detailed error message CANNOT_REGISTER_ALIAS_IN_SHM_DETAILED */
  private Object[] getDetailedMessageParameters(ShmApplication app, String applicationName, String webApplication, ShmNoMemoryException shmEx) {
    Object[] result;
    try {
      ShmTableInfo sti = shmEx.getInfo();
      if (sti != null) {
         result = new Object[]{webApplication, applicationName, ShmTableInfo.getName(sti.type), sti.toString(), getShmPropertiesMessage(),
              (app != null) ? app.toString() : "", ShmTableInfo.getName(sti.type)};
       } else {
         result = new Object[]{webApplication, applicationName, "", "", getShmPropertiesMessage(), (app != null) ? app.toString() : "", ""};
       }
    } catch (Exception e) {
      if (LOCATION_HTTP.beDebug()) {
        LOCATION_HTTP.debugT("HttpProviderImpl.getDetailedMessageParameters(): Failed to read SHM properties from Shared Memory: " + e.getMessage());
      }
			result = new Object[]{applicationName};
    }
    return result;
  }

  public LRUMap<String, ErrorReportInfoBean> getErrorReportInfos() {
  	return errorReportInfos;
  }//end of getErrorReportInfos()
  
}
