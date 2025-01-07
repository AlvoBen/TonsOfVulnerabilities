/*
 * Copyright (c) 2000-2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import com.sap.bc.proj.jstartup.JStartupFramework;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.services.httpserver.exceptions.HttpServiceException;
import com.sap.engine.services.httpserver.exceptions.IllegalHostArgumentsException;
import com.sap.engine.services.httpserver.interfaces.exceptions.IllegalHostArgumentException;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.lib.util.HttpConstants;
import com.sap.engine.services.httpserver.server.hosts.Host;
import com.sap.engine.services.httpserver.server.hosts.impl.HttpHostListener;

import java.io.File;
import java.io.IOException;

/*
 *
 * @author Maria Jurova
 * @version 4.0
 */

public class HttpHosts {
  private Date date = null;
  private byte[] version = null;
  private Host[] hosts = new Host[0];
  private ConcurrentHashMapObjectObject applicationAliases = new ConcurrentHashMapObjectObject();
  private ApplicationServiceContext sc = null;
  private ConfigurationHandlerFactory factory = null;
  private HttpProperties httpProperties = null;

  public synchronized void init(String majorVer, Date date, HttpProperties httpProperties, ApplicationServiceContext sc) throws ServiceException {
    version = (JStartupFramework.getParam("is/server_name") + " " +
        JStartupFramework.getParam("is/server_version") + " / AS Java " + majorVer).getBytes();
    this.date = date;
    this.sc = sc;
    this.httpProperties = httpProperties;
    factory = sc.getCoreContext().getConfigurationHandlerFactory();
    if (factory == null) {
      throw new HttpServiceException(HttpServiceException.CONFIGURATION_MANAGER_IS_NOT_AVAILABLE);
    }

    ConfigurationHandler handler = null;
    try {
      handler = factory.getConfigurationHandler();
      handler.addConfigurationChangedListener(new HttpHostListener(this, factory), HttpConstants.HTTP_HOSTS);
      handler.commit();
    } catch (ConfigurationException ce) {
      throw new ServiceException(ce);
    } finally {
      try {
        handler.closeAllConfigurations();
      } catch (ConfigurationException ce) {
        Log.logError("ASJ.http.000169", "Cannot close configuration handler.", ce, null, null, null);
      }
    }
  }

  public String getApplicationAliasValue(String alias) {
    return (String) applicationAliases.get(alias);
  }

  public void readAllHostsFromConfiguration() throws ConfigurationException, ServiceException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    Configuration hosts = null;
    try {
      hosts = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.READ_ACCESS);
    } catch (NameNotFoundException nnfe) {
      handler.closeAllConfigurations();
      try {
        Host host = createAndStoreHost("default");
        HttpHostListener listener = new HttpHostListener(host.getConfigurationReader());
        handler.addConfigurationChangedListener(listener, HttpConstants.HTTP_HOSTS + "/default");
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        Log.logFatal("ASJ.http.000222", "Cannot create default HTTP virtual host. " +
          "The HTTP Provider service cannot run properly without a virtual host specified. " +
          "Possible reason: cannot access HTTP virtual host configuration in database.", e, null, null, null);
        throw new HttpServiceException(HttpServiceException.UNABLE_TO_INIT_THE_DEFAULT_HTTP_HOST_CANNOT_START_HTTP_SERVICE_ON_SERVER, e);
      }
      return;
    }
    try {
      String hostNames[] = hosts.getAllSubConfigurationNames();
      for (int i = 0; i < hostNames.length; i++) {
        try {
          Host host = createHost(hostNames[i]);
          HttpHostListener listener = new HttpHostListener(host.getConfigurationReader());
          handler.addConfigurationChangedListener(listener, HttpConstants.HTTP_HOSTS + "/" + hostNames[i]);
        } catch (Exception e) {
          if (hostNames[i].equals("default")) {
            Log.logFatal("ASJ.http.000223", "Cannot read and initialize default HTTP virtual host configuration. " +
              "The HTTP Provider service cannot run properly without a virtual host specified. "
              + "Possible reason: incorrect properties specified or cannot access HTTP virtual host configuration in database.", e, null, null, null);
            throw new HttpServiceException(HttpServiceException.UNABLE_TO_INIT_THE_DEFAULT_HTTP_HOST_CANNOT_START_HTTP_SERVICE_ON_SERVER, e);
          }
          Log.logWarning("ASJ.http.000075", 
            "Cannot read HTTP virtual host configuration from database. " +
            "Cannot initialize HTTP virtual host [{0}]. " +
            "Possible reason: incorrect properties specified or cannot access configuration in database.", 
            new Object[]{hostNames[i]}, e, null, null, null);
        }
      }
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        if (getHost("default") == null) {
          try {
            Host host = createAndStoreHost("default");
            HttpHostListener listener = new HttpHostListener(host.getConfigurationReader());
            handler.addConfigurationChangedListener(listener, HttpConstants.HTTP_HOSTS + "/default");
          } catch (OutOfMemoryError e) {
            throw e;
          } catch (ThreadDeath e) {
            throw e;
          } catch (Throwable e) {
            Log.logFatal("ASJ.http.000224", "Cannot create default HTTP virtual host. " +
              "The HTTP Provider service cannot run properly without an HTTP virtual host specified. " +
              "Possible reason: cannot access HTTP virtual host configuration in database.", e, null, null, null);
            throw new HttpServiceException(HttpServiceException.UNABLE_TO_INIT_THE_DEFAULT_HTTP_HOST_CANNOT_START_HTTP_SERVICE_ON_SERVER, e);
          }
        }
      }
    }
  }

  public ApplicationServiceContext getCS() {
    return sc;
  }

  private Host createHost(String hostName) throws ConfigurationException {
    Host host = newHost(hostName, false);
    HttpHostListener listener = new HttpHostListener(host.getConfigurationReader());
    ConfigurationHandler handler = factory.getConfigurationHandler();
    handler.addConfigurationChangedListener(listener, HttpConstants.HTTP_HOSTS + "/" + hostName, ConfigurationChangedListener.MODE_ASYNCHRONOUS);
    return host;
  }
  
  public synchronized Host createHost(String newHostName, boolean writeToDB) throws ConfigurationException, IllegalHostArgumentsException {
    for (int i = 0; i < hosts.length; i++) {
      if (hosts[i].getHostName().equalsIgnoreCase(newHostName)) {
        throw new IllegalHostArgumentsException(IllegalHostArgumentsException.HOST_WITH_NAME_ALREADY_EXISTS, new Object[]{newHostName});
      }
    }
    ConfigurationHandler handler = factory.getConfigurationHandler();
    Host newHost = newHost(newHostName, writeToDB);
    HttpHostListener listener = new HttpHostListener(newHost.getConfigurationReader());
    handler.addConfigurationChangedListener(listener, HttpConstants.HTTP_HOSTS + "/" + newHostName, ConfigurationChangedListener.MODE_ASYNCHRONOUS);
    return newHost;
  }
  
  public synchronized Host createAndStoreHost(String newHostName) throws ConfigurationException, IllegalHostArgumentsException {
    return createHost(newHostName, true);
  }

  private Host newHost(String hostName, boolean toCreate) throws ConfigurationException {
    Host newDescriptor = new Host(hostName, version, httpProperties, this, date, factory, sc);
    if (toCreate) {
      newDescriptor.store();
    }
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        newDescriptor.getConfigurationReader().readConfiguration(false);
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logError("ASJ.http.000170", 
            "A thread was interrupted while waiting for cluster lock for HTTP Provider service.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    if (toCreate && !"default".equals(hostName)) {
      initAppAliases(newDescriptor);
    }
    Host[] temp = new Host[hosts.length + 1];
    System.arraycopy(hosts, 0, temp, 0, hosts.length);
    temp[hosts.length] = newDescriptor;
    hosts = temp;
    return newDescriptor;
  }

  private void initAppAliases(Host newDescriptor) throws ConfigurationException {
    Host defaultHost = getHost("default");
    String[] defaultAliases = defaultHost.getHostProperties().getAliasNames();
    for (int i = 0; defaultAliases != null && i < defaultAliases.length; i++) {
      if (defaultHost.getHostProperties().isApplicationAlias(defaultAliases[i])) {
        newDescriptor.getHostPropertiesModyfier().enableApplicationAlias(defaultAliases[i], true);
      }
    }
  }

  public Host getHost(String host) {
    if (hosts.length == 1) {
      return hosts[0];
    }
    for (int i = 0; i < hosts.length; i++) {
      if (hosts[i].getHostName().equalsIgnoreCase(host)) {
        return hosts[i];
      }
    }
    return null;
  }

  public Host getHost(byte[] host) {
    if (hosts.length == 1) {
      return hosts[0];
    }
    for (int i = 0; i < hosts.length; i++) {
      if (ByteArrayUtils.equalsIgnoreCase(hosts[i].getHostProperties().getHostNameBytes(), host)) {
        return hosts[i];
      }
    }
    return null;
  }

  public synchronized Host[] getAllHosts() {
    Host[] res = new Host[hosts.length];
    for (int i = 0; i < res.length; i++) {
      res[i] = hosts[i];
    }
    return res;
  }

  public synchronized void removeHost(String host, boolean inListener) throws ConfigurationException {
    boolean found = false;
    for (int i = 0; i < hosts.length; i++) {
      if (hosts[i].getHostName().equalsIgnoreCase(host)) {
        found = true;
      }
    }
    if (found) {
      Host[] temp = new Host[hosts.length - 1];
      int tempi = 0;
      for (int i = 0; i < hosts.length; i++) {
        if (!hosts[i].getHostName().equalsIgnoreCase(host)) {
          temp[tempi] = hosts[i];
          tempi++;
        }
      }
      hosts = temp;
      ConfigurationHandler handler = factory.getConfigurationHandler();
      Configuration hostsConfig = null;
      HttpLock httpLock = HttpServerFrame.getHttpLock();
      try {
        httpLock.enterLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
      } catch (Exception e) {
        Log.logError("ASJ.http.000171", 
          "Cannot get a cluster lock for HTTP Provider service for storing HTTP virtual hosts configuration in database. " +
          "Synchronization problems may occur.", e, null, null, null);
      }
      try {
        try {
          hostsConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.WRITE_ACCESS);
        } catch (NameNotFoundException nnfe) {
          Log.logError("ASJ.http.000172", 
            "Cannot remove HTTP virtual host [{0}]. Cannot open configuration [{1}].", 
            new Object[]{host, HttpConstants.HTTP_HOSTS}, nnfe, null, null, null);
        }

        if (hostsConfig.existsSubConfiguration(host)) {
          hostsConfig.deleteConfiguration(host);
        }
        handler.commit();
      } finally {
        try {
          try {
            httpLock.leaveLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
          } catch (Exception e) {
            Log.logError("ASJ.http.000173", "Cannot release a cluster lock for HTTP Provider service for storing HTTP virtual hosts configuration. " +
              "Synchronization problems may occur.", e, null, null, null);
          }
        } finally {
          handler.closeAllConfigurations();
        }
      }

      Configuration configApps = null;
      httpLock = HttpServerFrame.getHttpLock();
      try {
        httpLock.enterLockArea(HttpLock.HTTP_UPLOADED_FILES_LOCK);
      } catch (Exception e) {
        Log.logError("ASJ.http.000174", 
          "Cannot get a cluster lock for HTTP Provider service. Synchronization problems may occur.", e, null, null, null);
      }

      try {
        try {
          configApps = handler.openConfiguration(Constants.HTTP_ALIASES, ConfigurationHandler.WRITE_ACCESS);
        } catch (NameNotFoundException ne) {
          try {
            httpLock.leaveLockArea(HttpLock.HTTP_UPLOADED_FILES_LOCK);
          } catch (Exception e) {
            Log.logError("ASJ.http.000175", 
              "Cannot release a cluster lock for HTTP Provider service. " +
              "Possible synchronization problems may occur.", e, null, null, null);
          }
          return;
        }

        if (configApps.existsSubConfiguration(host)) {
          String subconfigs[] = configApps.getSubConfiguration(host).getAllSubConfigurationNames();
          for (int i = 0; i < subconfigs.length; i++) {
            configApps.getSubConfiguration(host).getSubConfiguration(subconfigs[i]).deleteAllConfigEntries();
            configApps.getSubConfiguration(host).deleteConfiguration(subconfigs[i]);
          }
          configApps.getSubConfiguration(host).deleteAllConfigEntries();
          configApps.deleteConfiguration(host);
          handler.commit();
        }
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        Log.logError("ASJ.http.000176", 
          "Cannot remove HTTP virtual host [{0}]. Cannot delete configuration [{1}/{2}] or its content.", 
          new Object[]{host, Constants.HTTP_ALIASES, host}, e, null, null, null);
      } finally {
        try {
          handler.closeAllConfigurations();
        } finally {
          try {
            httpLock.leaveLockArea(HttpLock.HTTP_UPLOADED_FILES_LOCK);
          } catch (Exception e) {
            Log.logError("ASJ.http.000177", 
              "Cannot release a cluster lock for HTTP Provider service. " +
              "Synchronization problems may occur.", e, null, null, null);
          }
        }
      }
    }
  }

  public void storeAll() throws ConfigurationException {
    for (int i = 0; i < hosts.length; i++) {
      hosts[i].store();
    }
  }

  public byte[] getVersion() {
    return version;
  }

  public void addApplicationAlias(String alias, boolean persistent) throws ConfigurationException, IllegalHostArgumentException {
    for (int i = 0; i < hosts.length; i++) {
      hosts[i].getHostPropertiesModyfier().checkAlias(alias, false);
      hosts[i].getHostPropertiesModyfier().enableApplicationAlias(alias, persistent);
    }
  }
  
  public void addAllApplicationAliases(String applicationName, String[] aliasesCanonicalized, boolean persistent) 
  																										throws ConfigurationException, IllegalHostArgumentException {
    for (int i = 0; i < hosts.length; i++) {
    	for (String currentAlias : aliasesCanonicalized) {
    		hosts[i].getHostPropertiesModyfier().checkAlias(currentAlias, false); //does not access the config
    	}      
      hosts[i].getHostPropertiesModyfier().enableAllApplicationAliases(applicationName, aliasesCanonicalized, persistent); //access the config
    }
  }

  public void checkApplicationAlias(String alias) throws IllegalHostArgumentException {
    for (int i = 0; i < hosts.length; i++) {
      hosts[i].getHostPropertiesModyfier().checkAlias(alias, false);
    }
  }//end of checkApplicationAlias(String alias)

  public void startApplicationAlias(String alias, String filePath) {
    if ("".equals(alias)) {
      return;
    }
    filePath = filePath.replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);
    if (filePath.endsWith(ParseUtils.separator)) {
      filePath = filePath.substring(0, filePath.length() - 1);
    }
    applicationAliases.put(alias, filePath);
  }

  public void notifyStopApplicationAlias(String aliases[]) {
    for (int i = 0; i < aliases.length; i++) {
      applicationAliases.remove(aliases[i]);
    }
  }


  public void removeApplicationAlias(String alias, boolean persistent) throws ConfigurationException {
    applicationAliases.remove(alias);
    for (int i = 0; i < hosts.length; i++) {
      hosts[i].getHostPropertiesModyfier().removeApplicationAlias(alias, persistent);
    }
  }

  public void changeHttpRoot(String newRoot) throws IOException, ConfigurationException {
    for (int i = 0; i < hosts.length; i++) {
      hosts[i].getHostPropertiesModyfier().setRootDir(newRoot);
    }
  }
}

