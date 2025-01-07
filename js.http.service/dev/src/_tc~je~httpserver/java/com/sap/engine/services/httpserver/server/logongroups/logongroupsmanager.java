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
package com.sap.engine.services.httpserver.server.logongroups;

import java.rmi.RemoteException;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.services.httpserver.server.HttpLock;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import static com.sap.engine.services.httpserver.server.HttpLock.READ_LOCK_ITER_COUNT;
import static com.sap.engine.services.httpserver.server.HttpLock.READ_LOCK_WAIT_TIMEOUT;
import static com.sap.engine.services.httpserver.server.properties.HttpPropertiesImpl.GROUP_INFO_LOCATION;
import static com.sap.engine.services.httpserver.server.properties.HttpPropertiesImpl.URL_MAP_LOCATION;

/**
 * The class is a central point for managing the logon groups 
 * It is responsible to dispatch all activities about administration
 * of the logon groups -  reading/writing them in the DB, for parsing
 * the configuration files and changing them via UIs. It also contains
 * runtime structures representing the logon groups. Each logon group
 * is represented as LogonGroupImpl object and an array of them is
 * stored in this object 
 * 
 * @author Violeta Uzunova(I024174)
 * @version 7.10
 */
public class LogonGroupsManager {
  public static final String VERSION_LINE = "version 1.0";
  private static Location traceLocation = Location.getLocation(LogonGroupsManager.class);
  
  private ConfigurationReader configurationReader;
  private ConfigurationWriter configurationWriter;
  //private HttpProviderImpl httpProvider; 
  private String zoneSeparator;
  private ConfigurationHandlerFactory factory;
  private LogonGroupsRequestGenerator requestGenerator;
  private ConcurrentHashMapObjectObject logonGroups;

  public LogonGroupsManager(ConfigurationHandlerFactory factory, String zoneSeparator) {   
    logonGroups = new ConcurrentHashMapObjectObject();    
    this.zoneSeparator = zoneSeparator;    
    this.factory = factory;
    configurationReader = new ConfigurationReader(this, factory);
    configurationWriter = new ConfigurationWriter(factory);
    initFromDB();
    addConfigurationChangedListener();
  }
  
  
  /**
   * The method initializes the logonGroupsManagers by reading the configuration of the
   * logon groups from data base. It is called by the object constructor. This ensures
   * that the logon groups, saved in the previous run in the DB, will be read and will
   * be used; This configuration (from BD) is with higher priority than the one in the
   * configuration files
   */
  private void initFromDB() {
    if (traceLocation.beDebug()) {
      traceLocation.traceThrowableT(Severity.DEBUG, "Reading groups from DB", new Exception("Debug only"));
    }
    for (int count = 0; count < READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationReader.readAll();
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logError("ASJ.http.000128", 
            "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == READ_LOCK_ITER_COUNT - 1) {
          Log.logError("ASJ.http.000129", 
            "Cannot read and initialize HTTP logon groups. Any HTTP logon groups configurations will be ignored. " +
            "Possible reason: cannot obtain cluster lock for HTTP Provider service.", e, null, null, null);
        }
      }
    }
  }//initFromDB()


  private void addConfigurationChangedListener() {
    ConfigurationHandler handler = null;
    try {
      handler = factory.getConfigurationHandler();
      handler.addConfigurationChangedListener(new ConfigurationListener(this, configurationReader), "HttpZones");
      handler.commit();
      if (traceLocation.beDebug()) {
        traceLocation.traceThrowableT(Severity.DEBUG, "addConfigurationChangedListener() : LogonGroups' ConfigurationListener registered.", new Exception("Debug only"));
      }
    } catch (Exception e) {
      Log.logError("ASJ.http.000130", 
        "Cannot register a configuration listener for configuration [HttpZones].", e, null, null, null);
    } finally {
      try {
        if (handler != null) {
          handler.closeAllConfigurations();
        }
      } catch (ConfigurationException e) {
        Log.logError("ASJ.http.000131", 
          "Cannot close all configuration handlers after registering a configuration listener for configuration [HttpZones].", e, null, null, null);
      }
    }
  }//addConfigurationChangedListener()
  
  /**
   * This method initializes the logonGroupsManager from the configuration
   * of the logon groups, specified in the files set in groupInfoLocation
   * and urlMapLocation service properties. Since this configuration is with
   * lower priority, the method first checks if already exists configuration.
   * If yes - the description of the logon groups in property files is ignored.
   * The corresponding message is logged. Otherwise (no logon groups up to now)
   * - the configuration files are passed to the fileGruopsReader to read and
   * parse them.   
   * 
   * This method is invoked in two case:
   *      - the service is started    
   *      - the service properties (both) are changed 
   *      
   * @param groupInfoLocation
   * @param urlMapLocation
   */
  public void initFromServiceProperties(String groupInfoLocation, String urlMapLocation) {
    // checks if there are already logon groups defined
    //[VB]: First DB (existence of subconfigs of HttpZones) is used to distinguish between changed by NWA and configured through properties
    if (!logonGroupsExists()) {
      if (traceLocation.beDebug()) {
        traceLocation.debugT("No Logon Groups in config. groupInfoLocation=" + groupInfoLocation + ", urlMapLocation=" + urlMapLocation);
      }
      // read the configuration from file
      try {
        //[VB]: Now reads logon groups from file in RT structures and then writes them to DB
        LogonGroupsFileReader logonGroupsReader = new LogonGroupsFileReader(this);
        logonGroupsReader.parse(groupInfoLocation, urlMapLocation, zoneSeparator);
      } catch (Exception e) {
        Log.logError("ASJ.http.000132", 
          "Errors during reading logon groups from configuration files.", e, null, null, null);
      }
      if ( groupInfoLocation != null && !groupInfoLocation.equals("")
            && urlMapLocation != null && !urlMapLocation.equals("")) {
        // something is set from the properties or exception occurred - only then update from DB
        initFromDB();
      }      
    } else {
      if (traceLocation.beDebug()) {
        traceLocation.debugT("initFromServiceProperties(groupInfoLocation=" + groupInfoLocation + ", urlMapLocation=" + urlMapLocation + "): There are some logon groups in config.");
      }
      // in standard configuration (developer installation), this log can be found in \cluster\server0\log\system\server.log
      Log.logWarning("ASJ.http.000061", 
    		         "Configuration of the logon groups specified by " +
    		         " [{0}] and [{1}] service properties " +
    		         "will be ignored. Logon Groups were changed via UI and this configuration would be taken into account.",
    		         new Object[]{GROUP_INFO_LOCATION, URL_MAP_LOCATION}, null, null, null);
    }
  }  
  
  /**
   * 
   * @return
   */
  public boolean logonGroupsExists() {
    boolean isConfigurationExist = false;
    
    for (int count = 0; count < READ_LOCK_ITER_COUNT; count++) {
      try {
        isConfigurationExist = configurationReader.isDBConfigurationExist();
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logError("ASJ.http.000133", 
            "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == READ_LOCK_ITER_COUNT - 1) {
          Log.logError("ASJ.http.000134", 
            "Cannot read and initialize HTTP zones. Any HTTP zones configurations will be ignored. " +
            "Possible reason: cannot obtain cluster lock for HTTP Provider service.", e, null, null, null);
        }
      }      
    }
    return isConfigurationExist;
  }
  
  public String getZoneSeparator() {    
    return zoneSeparator;
  }   
  
  public String answerGroupInfoRequest() {
    if (requestGenerator == null) {
      requestGenerator = new LogonGroupsRequestGenerator(this);
    }
    return requestGenerator.answerGroupInfoRequest();
  }

  public String answerUrlMapRequest() {
    if (requestGenerator == null) {
      requestGenerator = new LogonGroupsRequestGenerator(this);
    }
    return requestGenerator.answerUrlMapRequest();
  }
  
  public LogonGroup[] getAllLogonGroups() {
    Object logonGroupsObj[] = logonGroups.toArray();
    if (logonGroupsObj == null) {
      return null;
    } else {
      LogonGroup res[] = new LogonGroup[logonGroupsObj.length];
      System.arraycopy(((Object) (logonGroupsObj)), 0, res, 0, res.length);
      return res;
    }
  }  
  
  public synchronized LogonGroup getLogonGroup(String logonGroupName) {
    return (LogonGroup)logonGroups.get(logonGroupName);
  }
  
  
  public synchronized void registerLogonGroup(String logonGroupName) throws IllegalArgumentException, ConfigurationException {
    if (traceLocation.beDebug()) {
      traceLocation.debugT("Registering logon group " + logonGroupName + "...");
    }
    try {
      configurationReader.readAll();
    } catch (InconsistentReadException e) {
      Log.logWarning("ASJ.http.000062", 
        "The cached logon groups were not updated.", e, null, null, null);
    }
    if (logonGroups.containsKey(logonGroupName)) {
      throw new IllegalArgumentException("A logon group with name [" + logonGroupName + "] already exists");      
    }
    try {
      for (int count = 0; count < READ_LOCK_ITER_COUNT; count++) {
        try {
          configurationWriter.createLogonGroup(logonGroupName);
          break;
        } catch (InconsistentReadException e) {
          try {
            Thread.sleep(READ_LOCK_WAIT_TIMEOUT);
          } catch (InterruptedException ie) {
            Log.logError("ASJ.http.000135", 
              "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
          }
          if (count == READ_LOCK_ITER_COUNT - 1) {
            throw e;
          }
        }
      }
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000136", 
    		"Cannot create logon group [{0}]" +
    		"Probably a configuration access problem has occurred.", 
    		new Object[]{logonGroupName}, e, null, null, null);
      throw e;
    }
    newLogonGroupRegistered(logonGroupName);
  }

  public synchronized void unregisterLogonGroup(String logonGroupName) throws ConfigurationException {
    try {
      for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
        try {
          configurationWriter.deleteLogonGroup(logonGroupName);
          break;
        } catch (InconsistentReadException e) {
          try {
            Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
          } catch (InterruptedException ie) {
            Log.logError("ASJ.http.000137", 
              "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
          }
          if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
            throw e;
          }
        }
      }
    } catch (ConfigurationException e) {
      Log.logError("ASJ.http.000138", 
        "Cannot delete HTTP logon group [{0}].  " +
        "Probably a configuration access problem has occurred.", 
        new Object[]{logonGroupName}, e, null, null, null);
      throw e;
    }
    logonGroupUnregistered(logonGroupName);
  }

  protected synchronized LogonGroup newLogonGroupRegistered(String logonGroupName) {
    LogonGroup newLogonGroup = null;
    try {
      newLogonGroup = new LogonGroup(logonGroupName, configurationWriter, this);
    } catch (RemoteException e) {
      Log.logError("ASJ.http.000139", 
        "Cannot create HTTP logon group [{0}]. Probably a configuration access problem has occurred.", 
        new Object[]{logonGroupName}, e, null, null, null);
      return null;
    }
    logonGroups.put(logonGroupName, newLogonGroup);
    return newLogonGroup;
  }

  //TODO try to remove
  protected synchronized void logonGroupUnregistered(String logonGroupName) {
    logonGroups.remove(logonGroupName);
  }
  
  //TODO try to remove
  protected synchronized void allLogonGroupsUnregistered() {
    logonGroups.clear();
  }
  
  //TODO try to remove; better leave only getLogonGroupNameForAlias
  public boolean isExactAlias(String alias) {
    if (alias == null || logonGroups == null || logonGroups.size() == 0) {
      return false;
    }
    Object logonGroupsObj[] = logonGroups.toArray();
    if (logonGroupsObj == null) {
      return false;
    }
    for (int i = 0; logonGroupsObj != null && i < logonGroupsObj.length; i++) {
      if (((LogonGroup)logonGroupsObj[i]).containExactAlias(alias)) {
        return true;
      }
    }
    return false;
  }
  
  //TODO might have to be changed
  /**
   * Returns the name of the logon group where the alias
   * is added or null if the alias is not add to the logon group
   * 
   * The parameter alias has to be pure alias without logon group (not examples~lg but examples)
   * 
   * @returns the name of the logon group where the alias is added 
   */
  public String getLogonGroupNameForAlias(String alias) {
    if (alias == null || logonGroups == null || logonGroups.size() == 0) {
      return null;
    }
    Object logonGroupsObj[] = logonGroups.toArray();
    if (logonGroupsObj == null) {
      return null;
    }
    for (int i = 0; logonGroupsObj != null && i < logonGroupsObj.length; i++) {
      if (((LogonGroup)logonGroupsObj[i]).containAlias(alias)) {
        return ((LogonGroup)logonGroupsObj[i]).getLogonGroupName();
      }
      
      if (((LogonGroup)logonGroupsObj[i]).containExactAlias(alias)) {
        return ((LogonGroup)logonGroupsObj[i]).getLogonGroupName();
      }
    }
    return null;    
  }


  public ConfigurationHandlerFactory getFactory() {    
    return factory;
  }
}
