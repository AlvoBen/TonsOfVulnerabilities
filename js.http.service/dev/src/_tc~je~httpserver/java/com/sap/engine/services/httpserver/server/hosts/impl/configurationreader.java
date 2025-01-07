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

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.lib.util.HttpConstants;
import com.sap.engine.lib.util.ConcurrentReadHashMap;

import java.io.File;
import java.io.IOException;

public class ConfigurationReader {
  private static final String default_key = "default";
  private String hostName = null;
  private HostPropertiesImpl hostProperties = null;
  private String currentID = null;
  private ConfigurationHandlerFactory factory = null;

  public ConfigurationReader(HostPropertiesImpl hostProperties, int serverId, ConfigurationHandlerFactory factory) {
    this.hostProperties = hostProperties;
    this.hostName = hostProperties.getHostName();
    this.factory = factory;
    currentID = Integer.toString(serverId);
  }

  public synchronized void readConfiguration(boolean inListener) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    Configuration hostConfig = null;
    try {
      hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS + "/" + hostName, ConfigurationHandler.READ_ACCESS);
    } catch (NameNotFoundException e) {
      return;
    }
    try {
      setProperties(hostConfig, inListener);
    } finally {
      handler.closeAllConfigurations();
    }
  }

  private void setProperties(Configuration configuration, boolean inListener) throws InconsistentReadException {
    setSimpleProperty(configuration);
    try {
      setStartPage(configuration);
    } catch (InconsistentReadException e) {
      throw e;
    } catch (ConfigurationException e) {
      Log.logWarning("ASJ.http.000004", 
        "Cannot initialize property  [{0}] of the virtual host [{1}]", 
        new Object[]{HostPropertiesImpl.start_page_key, hostName}, e, null, null, null);
    }
    try {
      setRootDir(configuration);
    } catch (InconsistentReadException e) {
      throw e;
    } catch (ConfigurationException e) {
      Log.logWarning("ASJ.http.000005", 
        "Cannot initialize property [{0}] of the virtual host [{1}].", 
        new Object[]{HostPropertiesImpl.root_key, hostName}, e, null, null, null);
    }
    try {
      setHttpAliases(configuration);
    } catch (InconsistentReadException e) {
      throw e;
    } catch (ConfigurationException e) {
      Log.logWarning("ASJ.http.000006", 
        "Cannot initialize HTTP aliases of the virtual host [{0}].", new Object[]{hostName}, e, null, null, null);
    }
    try {
      setApplicationAliases(configuration, inListener);
    } catch (InconsistentReadException e) {
      throw e;
    } catch (ConfigurationException e) {
      Log.logWarning("ASJ.http.000007", 
        "Cannot initialize application aliases of the virtual host [{0}].", new Object[]{hostName}, e, null, null, null);
    }
  }

  private void setSimpleProperty(Configuration configuration) throws InconsistentReadException {
    try {
      hostProperties.list = new Boolean((String)configuration.getConfigEntry(HostPropertiesImpl.dir_list_key)).booleanValue();
    } catch (InconsistentReadException e) {
      throw e;
    } catch (ConfigurationException e) {
      Log.logWarning("ASJ.http.000008", 
    		"Unable to initialize DirList for the virtual host [{0}]. The default value will be used.",
    		new Object[]{hostName}, e, null, null, null);
    }
    try {
      hostProperties.useCache = Boolean.valueOf((String)configuration.getConfigEntry(HostPropertiesImpl.use_cache_key)).booleanValue();
    } catch (InconsistentReadException e) {
      throw e;
    } catch (ConfigurationException e) {
      Log.logWarning("ASJ.http.000009", 
        "Unable to initialize UseCache for the virtual host [{0}]. The default value will be used.",
        new Object[]{hostName}, e, null, null, null);
    }
    try {
      hostProperties.enableLog = Boolean.valueOf((String)configuration.getConfigEntry(HostPropertiesImpl.enable_log_key)).booleanValue();
    } catch (InconsistentReadException e) {
      throw e;
    } catch (ConfigurationException e) {
      Log.logWarning("ASJ.http.000010", 
    		"Unable to initialize EnableLogging for the virtual host [{0}]. The default value will be used.", 
    		new Object[]{hostName}, e, null, null, null);
    }
    try {
      hostProperties.keepAliveEnabled = Boolean.valueOf((String)configuration.getConfigEntry(HostPropertiesImpl.keep_alive_enabled_key)).booleanValue();
    } catch (InconsistentReadException e) {
      throw e;
    } catch (ConfigurationException e) {
      Log.logWarning("ASJ.http.000011", 
    		"Unable to initialize KeepAliveEnabled for the virtual host [{0}]. The default value will be used.",
    		new Object[]{hostName}, e, null, null, null);
    }
  }

  private void setStartPage(Configuration configuration) throws ConfigurationException,InconsistentReadException {
    Configuration subConfig = null;
    try {
      subConfig = configuration.getSubConfiguration(HostPropertiesImpl.start_page_key);
    } catch (NameNotFoundException e) {
      Log.logWarning("ASJ.http.000012", 
    		         "Sub configuration with name [{0}] not found. HTTP virtual host [{1}] will have no start page.", 
    		         new Object[]{HostPropertiesImpl.start_page_key, hostName}, null, null, null);
      return;
    }
    String property = null;
    try {
      property = (String)subConfig.getConfigEntry(currentID);
    } catch (NameNotFoundException nnfe) {
      try {
        property = (String)subConfig.getConfigEntry(default_key);
      } catch (NameNotFoundException e) {
        return;
      }
    }
    if (property != null && property.trim().equals("")) {
      property = null;
    }
    hostProperties.startPage = property;
  }

  private void setRootDir(Configuration configuration) throws ConfigurationException {
    Configuration subConfig = null;
    try {
      subConfig = configuration.getSubConfiguration(HostPropertiesImpl.root_key);
    } catch (NameNotFoundException e) {
      Log.logWarning("ASJ.http.000015", 
    		    	 "Sub configuration with name [{0}] not found. HTTP virtual host [{1}] will have no root directory.", 
    		    	 new Object[]{HostPropertiesImpl.root_key, hostName}, null, null, null);
      try {
        hostProperties.initRootDir(null);
      } catch (Exception t) {
        Log.logWarning("ASJ.http.000013", 
        			   "Cannot initialize [{0}] property.", 
        		       new Object[]{HostPropertiesImpl.root_key}, t, null, null, null);
      }
      return;
    }
    String property = null;
    try {
      property = (String)subConfig.getConfigEntry(currentID);
    } catch (NameNotFoundException nnfe) {
      try {
        property = (String)subConfig.getConfigEntry(default_key);
      } catch (NameNotFoundException e) {
        Log.logWarning("ASJ.http.000014", 
        		       "Value of the host property [{0}] not found into the configuration. HTTP virtual host [{1}] will have no root directory.", 
                       new Object[]{ HostPropertiesImpl.root_key, hostName }, null, null, null);
      }
    }
    try {
      hostProperties.initRootDir(property);
    } catch (Exception t) {
      Log.logWarning("ASJ.http.000016", "Cannot initialize [{0}] property.", 
    		         new Object[]{HostPropertiesImpl.root_key,}, t, null, null, null);
    }
  }

 private void setHttpAliases(Configuration configuration) throws ConfigurationException {
    Configuration subConfig = null;
    try {
      subConfig = configuration.getSubConfiguration(HostPropertiesImpl.aliases_key);
    } catch (NameNotFoundException e) {
      Log.logWarning("ASJ.http.000017", 
    		         "Sub configuration with name [{0}] not found. HTTP aliases on virtual host [{1}] will not be initialized.", 
          			new Object[]{HostPropertiesImpl.aliases_key, hostName }, null, null, null);
      return;
    }
    ConcurrentReadHashMap aliases = null;
    try {
      Configuration currentIdConfig = subConfig.getSubConfiguration(currentID);
      aliases = configurationEntriesToAliases(currentIdConfig);
    } catch (NameNotFoundException nnfe) {
      if (LOCATION_HTTP.beDebug()) { 
				LOCATION_HTTP.debugT("ConfigurationReader.setHttpAliases(): Sub configuration with name " + currentID + " not found. " 
						+ "HTTP aliases on virtual host [" + hostName + "] could not be initialized.");
			}
    }
    ConcurrentReadHashMap defaultAliases = null;
    try {
      Configuration defaultKeyConfig = subConfig.getSubConfiguration(default_key);
      defaultAliases = configurationEntriesToAliases(defaultKeyConfig);
      aliases = merge(aliases, defaultAliases);
    } catch (NameNotFoundException nnfe) {
      //$JL-EXC$ - ok, no global values
    }
    setAliases(aliases);
    hostProperties.aliases = aliases;
  }

  private void setApplicationAliases(Configuration configuration, boolean inListener) throws ConfigurationException {
    if (inListener) {
      return;
    }
    Configuration subConfig = null;
    try {
      subConfig = configuration.getSubConfiguration(HostPropertiesImpl.web_applications_key);
    } catch (NameNotFoundException e) {
      return;
    }
    hostProperties.applications = configurationEntriesToApplAliases(subConfig);
  }

  private void setAliases(ConcurrentReadHashMap aliases) {
    Object[] enumObjects = aliases.getAllKeys();
    for (int i = 0; i < enumObjects.length; i++) {
      String alKey = (String) enumObjects[i];
      String alValue = (String)aliases.get(alKey); 
      
      aliases.put(alKey, convertHttpAliasPath(alValue));    
    }
    try {
      aliases = (ConcurrentReadHashMap) aliases.clone();
    } catch (CloneNotSupportedException e) {
      Log.logWarning("ASJ.http.000018", "Cannot clone aliases table.", e, null, null, null);
    }
  }

  private static String getAbsolutePath(String filePath) {
    File file = new File(filePath + File.separator);
    String result = null;
    try {
      result = file.getCanonicalPath().replace('/', File.separatorChar).replace('\\', File.separatorChar);
    } catch (IOException io) {
      Log.logWarning("ASJ.http.000019", 
    		"Cannot get the canonical path of the file [{0}].", 
    		new Object[]{filePath}, io, null, null, null);
      
      result = file.getAbsolutePath().replace('/', File.separatorChar).replace('\\', File.separatorChar);
    }
    if (result.charAt(result.length() - 1) == File.separatorChar) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  static String convertHttpAliasPath(String path) {
    if (path.equals("")) {
      path = path + File.separator;
    }
    path = getAbsolutePath(path.replace('/', File.separatorChar).replace('\\', File.separatorChar));
    if (path.startsWith(File.separator + File.separator)) {
      //WIN - shared drive
      path = "\\\\" + path.substring(2);
    }
    if (path.endsWith(File.separator)) {
      return path.substring(0, path.length() - 1).replace(File.separatorChar, ParseUtils.separatorChar);
    } else {
      return path.replace(File.separatorChar, ParseUtils.separatorChar);
    }
  }  
  
  private ConcurrentReadHashMap configurationEntriesToApplAliases(Configuration config) throws ConfigurationException {
    String aliases[] = null;
    ConcurrentReadHashMap hReturn = new ConcurrentReadHashMap();
    if (config == null) {
      return hReturn;
    } else {
      aliases = config.getAllConfigEntryNames();
      if (aliases == null || aliases.length == 0) {
        return hReturn;
      }
    }
    for (int i = 0; i < aliases.length; i++) {
      String alias = (String)config.getConfigEntry(aliases[i]);
      if (alias.equals("true")) {
        hReturn.put(aliases[i].trim(), "true");
      } else {
        hReturn.put(aliases[i].trim(), "false");
      }
    }
    return hReturn;
  }

  private ConcurrentReadHashMap configurationEntriesToAliases(Configuration config) throws ConfigurationException {
    ConcurrentReadHashMap hReturn = new ConcurrentReadHashMap();
    String aliases[] = null;
    if (config == null) {
      return hReturn;
    } else {
      aliases = config.getAllConfigEntryNames();
      if (aliases == null || aliases.length == 0) {
        return hReturn;
      }
    }
    for (int i = 0; i < aliases.length; i++) {
      String alias = (String)config.getConfigEntry(aliases[i]);
      hReturn.put(aliases[i].trim(), alias.trim());
    }
    return hReturn;
  }

  private ConcurrentReadHashMap merge(ConcurrentReadHashMap one, ConcurrentReadHashMap two) {
    if (two == null) {
      return one;
    }
    if (one == null) {
      return two;
    }
    Object[] keysTwo = two.getAllKeys();
    for (int i = 0; keysTwo != null && i < keysTwo.length; i++) {
      if (!one.containsKey(keysTwo[i])) {
        one.put(keysTwo[i], two.get(keysTwo[i]));
      }
    }
    return one;
  }
}
