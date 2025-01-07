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
package com.sap.engine.services.httpserver.server;

/*
 *
 * @author Galin Galchev
 * @version 4.0
 */
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.HostPropertiesRuntimeInterface;
import com.sap.engine.services.httpserver.ZoneManagementInterface;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.server.hosts.Host;
import com.sap.engine.services.httpserver.server.hosts.impl.HostPropertiesRuntimeInterfaceImpl;
import com.sap.engine.services.httpserver.exceptions.IllegalHostArgumentsException;
import com.sap.engine.services.httpserver.exceptions.HttpRemoteException;
import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import java.rmi.RemoteException;


public class HttpRuntimeInterfaceImpl extends javax.rmi.PortableRemoteObject implements HttpRuntimeInterface {
  private HttpServerFrame http = null;
  private HttpMonitoring httpMonitoring = null;
  private HttpHosts httpManager = null;
  private HttpProperties httpProperties = null;
  private ZoneManagementInterface zoneManagementInterface = null;

	/**
   * Construgts HttpRuntimeInterfaceImpl
   *
   * @param   http  HttpServerFrame
   */
  public HttpRuntimeInterfaceImpl(HttpServerFrame http, HttpHosts httpManager, HttpMonitoring httpMonitoring,
                                  HttpProperties httpProperties, ZoneManagementInterface zoneManagementInterface) throws java.rmi.RemoteException {
    this.httpManager = httpManager;
    this.http = http;
    this.httpMonitoring = httpMonitoring;
    this.httpProperties = httpProperties;
    this.zoneManagementInterface = zoneManagementInterface;
  }

  /**
   * Returns description of the all virtual hosts on the server.
   *
   * @return     description of the all virtual hosts on the server
   */
  public HostPropertiesRuntimeInterface[] getAllHosts() throws java.rmi.RemoteException {
    Host[] hosts = httpManager.getAllHosts();
    HostPropertiesRuntimeInterface[] res = new HostPropertiesRuntimeInterface[hosts.length];
    for (int i = 0; i < res.length; i++) {
      res[i] = new HostPropertiesRuntimeInterfaceImpl(httpProperties, hosts[i].getHostProperties(), hosts[i].getHostPropertiesModyfier());
    }
    return res;
  }

  public HostPropertiesRuntimeInterface[] getAllHostsTemp() throws java.rmi.RemoteException {
    return getAllHosts();
  }

  /**
   * Creates new virtual host with name <code>hostName</name>
   *
   * @return     description of the new virtual host if is successfully created
   * @throws     IllegalHostArgumentsException if a host with such name already exists or some error occurs
   */
  public HostPropertiesRuntimeInterface createHost(String hostName) throws IllegalHostArgumentsException, ConfigurationException, java.rmi.RemoteException {
    Host newHost = httpManager.createAndStoreHost(hostName);
    HostPropertiesRuntimeInterfaceImpl newHostProperties = new HostPropertiesRuntimeInterfaceImpl(httpProperties, newHost.getHostProperties(), newHost.getHostPropertiesModyfier());
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(HttpRuntimeInterfaceImpl.class), "ASJ.http.000310", 
  				"Virtual host [{0}] was created.", new Object[]{hostName});
  	}    
    return newHostProperties;
  }

  /**
   * Removes virtual host with name <code>hostName</name>.
   * If host with such name does not exists returns with no exception.
   *
   * @throws     IllegalHostArgumentsException if trying to remove the default host or some error occures
   */
  public void removeHost(String hostName) throws IllegalHostArgumentsException, java.rmi.RemoteException {
    if ("default".equalsIgnoreCase(hostName)) {
      throw new IllegalHostArgumentsException(IllegalHostArgumentsException.ILLEGAL_TO_REMOVE_DEFAULT_VIRTUAL_HOST);
    }
    if (httpManager.getCS().getClusterContext().getClusterMonitor().getCurrentParticipant().getState() == ClusterElement.DEBUGGING) {
      return;
    }
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        httpManager.removeHost(hostName, false);
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logError("ASJ.http.000180", 
            "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw new HttpRemoteException(HttpRemoteException.CANNOT_REMOVE_DESCRIPTOR_FOR_HOST, e);
        }
      } catch (ConfigurationException t) {
        //      t.printStackTrace();
        throw new HttpRemoteException(HttpRemoteException.CANNOT_REMOVE_DESCRIPTOR_FOR_HOST, t);
      }
    }
    
  	if (getUser()) {
  		SimpleLogger.log(Severity.INFO, Category.SYS_CHANGES, 
  				Location.getLocation(HttpRuntimeInterfaceImpl.class), "ASJ.http.000311", 
  				"Virtual host [{0}] was removed.", new Object[]{hostName});
  	}    
  }

  /**
   * Clear HTTP cache.
   *
   */
  public void clearCache() throws java.rmi.RemoteException {
    http.clearRemoteCache();
  }

  /**
   * Clear HTTP cache at a specified host.
   *
   * @param   hostName  name of the host
   */
  public void clearCache(String hostName) throws java.rmi.RemoteException {
    http.clearRemoteCache();
  }

  public void clearCacheByAlias(String alias) throws java.rmi.RemoteException {
    http.clearRemoteCache(alias);
  }

  public ZoneManagementInterface getZoneManagementInterface() {
    return zoneManagementInterface;
  }

  public void registerManagementListener( ManagementListener managementListener ) {
    //todo registerManagmentListener
  }

  // ------------------------ MONITORING ------------------------

  public long getAllRequestsCount() throws RemoteException {
    return httpMonitoring.getAllRequestsCount();
  }

  public long getAllResponsesCount() throws RemoteException {
    return httpMonitoring.getAllResponsesCount();
  }

  public long getTotalResponseTime() throws RemoteException {
    return httpMonitoring.getTotalResponseTime();
  }

  public String[] getMethodNames() throws RemoteException {
    return httpMonitoring.getMethodNames();
  }

  public long getRequestsCount(String methodName) throws RemoteException {
    return httpMonitoring.getRequestsCount(methodName);
  }

  public int[] getResponseCodes() throws RemoteException {
    return httpMonitoring.getResponseCodes();
  }

  public long getResponsesCount(int responseCode) throws RemoteException {
    return httpMonitoring.getResponsesCount(responseCode);
  }

  public long getResponsesFromCacheCount() throws RemoteException {
    return httpMonitoring.getResponsesFromCacheCount();
  }
  
  /**
   * Gets the number of threads used currently in request processing
   * 
   * @return
   * The number of active threads
   */
  public int getActiveThreadsCount() {
    return httpMonitoring.getActiveThreadsCount();
  }
  
  /**
   * Gets the number of all configured for request processing threads
   * 
   * @return
   * The total number of threads 
   */
  public int getThreadPoolSize() {
    return httpMonitoring.getThreadPoolSize();
  }
  
  /**
   * Gets the rate of used to configured HTTP threads
   * 
   * @return
   * The ratio of used to configured HTTP threads
   */
  public int getThreadsInProcessRate() {
    return httpMonitoring.getThreadsInProcessRate();
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

