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
import com.sap.engine.services.httpserver.server.HttpLock;
import com.sap.engine.services.httpserver.server.HttpServerFrame;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.Constants;
import com.sap.engine.services.httpserver.lib.util.HttpConstants;
import com.sap.engine.lib.util.ConcurrentReadHashMap;

public class ConfigurationWriter {
  private static final String default_key = "default";
  private String currentID = null;
  private String hostName = null;
  private HostPropertiesImpl hostProperties = null;
  private ConfigurationHandlerFactory factory = null;

  public ConfigurationWriter(HostPropertiesImpl hostProperties, int serverId, ConfigurationHandlerFactory factory) {
    this.hostProperties = hostProperties;
    this.hostName = hostProperties.getHostName();
    this.currentID = Integer.toString(serverId);
    this.factory = factory;
  }

  public void writeToConfiguration() throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000020", 
        "Cannot get cluster lock for storing virtual hosts settings.", e, null, null, null);
    }
    try {
      Configuration hostConfiguration = null;
      try {
        hostConfiguration = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.WRITE_ACCESS);
      } catch (NameNotFoundException e) {
        hostConfiguration = handler.createSubConfiguration(HttpConstants.HTTP_HOSTS);
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      try {
        hostConfiguration = hostConfiguration.getSubConfiguration(hostName);
      } catch (InconsistentReadException e) {
        throw e;
      } catch (NameNotFoundException ex) {
        hostConfiguration = hostConfiguration.createSubConfiguration(hostName);
        hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }

      writeAllSingleProperties(hostConfiguration);
      writeRootDir(hostConfiguration);
      writeStartPage(hostConfiguration);
      writeHttpAliases(hostConfiguration);
      writeApplicationAliases(hostConfiguration);

      handler.commit();
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000021", 
            "Cannot close cluster lock for storing virtual hosts settings.", e, null, null, null);
        }
      }
    }
  }

  public synchronized void modifyProperty(String key, String value) throws ConfigurationException {
    if (value == null) {
      value = "";
    }
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000022", 
        "Cannot get cluster lock for storing virtual hosts settings.", e, null, null, null);
    }
    try {
      Configuration hostConfig = null;
      try {
        hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS + "/" + hostName, ConfigurationHandler.WRITE_ACCESS);
      } catch (NameNotFoundException ex) {
        try {
          hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.WRITE_ACCESS);
        } catch (NameNotFoundException e) {
          hostConfig = handler.createSubConfiguration(HttpConstants.HTTP_HOSTS);
          hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        hostConfig = hostConfig.createSubConfiguration(hostName);
        hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      if (key.equals(HostPropertiesImpl.dir_list_key)) {
        if (LOCATION_HTTP.bePath()) { 
					LOCATION_HTTP.pathT("ConfigurationWriter.modifyProperty(): Directory Listing value set to [" + value + "] for host [" + hostName + "] and server [" + 
							currentID + "]. Previous value was [" + ((String) hostConfig.getConfigEntry(HostPropertiesImpl.dir_list_key)) + "]");
				}
				hostConfig.modifyConfigEntry(HostPropertiesImpl.dir_list_key, value, true);
      } else if (key.equals(HostPropertiesImpl.enable_log_key)) {
        hostConfig.modifyConfigEntry(HostPropertiesImpl.enable_log_key, value, true);
      } else if (key.equals(HostPropertiesImpl.keep_alive_enabled_key)) {
        hostConfig.modifyConfigEntry(HostPropertiesImpl.keep_alive_enabled_key, value, true);
      } else if (key.equals(HostPropertiesImpl.use_cache_key)) {
        hostConfig.modifyConfigEntry(HostPropertiesImpl.use_cache_key, value, true);
      } else if (key.equals(HostPropertiesImpl.root_key)) {
        Configuration rootDirConfig = null;
        try {
          rootDirConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.root_key);
        } catch (InconsistentReadException e) {
          throw e;
        } catch (NameNotFoundException e) {
          rootDirConfig = hostConfig.createSubConfiguration(HostPropertiesImpl.root_key);
          rootDirConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        rootDirConfig.modifyConfigEntry(currentID, value, true);
      } else if (key.equals(HostPropertiesImpl.start_page_key)) {
        Configuration startPageConfig = null;
        try {
          startPageConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.start_page_key);
        } catch (InconsistentReadException e) {
          throw e;
        } catch (NameNotFoundException e) {
          startPageConfig = hostConfig.createSubConfiguration(HostPropertiesImpl.start_page_key);
          startPageConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        startPageConfig.modifyConfigEntry(currentID, value, true);
      }
      handler.commit();
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000023", 
            "Cannot close cluster lock for storing virtual hosts settings.", e, null, null, null);
        }
      }
    }
  }

  public synchronized void modifyHttpAlias(String alias, String value, boolean add) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000024", 
        "Cannot get cluster lock for storing virtual hosts settings.", e, null, null, null);
    }
    try {
      Configuration hostConfig = null;
      try {
        hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS + "/" + hostName, ConfigurationHandler.WRITE_ACCESS);
      } catch (NameNotFoundException ex) {
        try {
          hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.WRITE_ACCESS);
        } catch (NameNotFoundException e) {
          hostConfig = handler.createSubConfiguration(HttpConstants.HTTP_HOSTS);
          hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        hostConfig = hostConfig.createSubConfiguration(hostName);
        hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      Configuration aliasesConfig = null;
      try {
        aliasesConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.aliases_key + "/" + currentID);
      } catch (InconsistentReadException e) {
        throw e;
      } catch (NameNotFoundException e) {
        try {
          aliasesConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.aliases_key);
        } catch (InconsistentReadException ire) {
          throw ire;
        } catch (NameNotFoundException ee) {
          aliasesConfig = hostConfig.createSubConfiguration(HostPropertiesImpl.aliases_key);
          aliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        aliasesConfig = aliasesConfig.createSubConfiguration(currentID);
        aliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      if (add) {
        aliasesConfig.modifyConfigEntry(alias, value, true);
      } else {
        try {
          aliasesConfig.deleteConfigEntry(alias);
        } catch (NameNotFoundException e) {
          //$JL-EXC$ - ok, it's already removed
        }
      }
      try {
        aliasesConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.aliases_key + "/" + default_key);
      } catch (InconsistentReadException e) {
        throw e;
      } catch (NameNotFoundException e) {
        aliasesConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.aliases_key);
        aliasesConfig = aliasesConfig.createSubConfiguration(default_key);
        aliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      if (add) {
        aliasesConfig.modifyConfigEntry(alias, value, true);
      } else {
        try {
          aliasesConfig.deleteConfigEntry(alias);
        } catch (NameNotFoundException e) {
          //$JL-EXC$ - ok, it's already removed
        }
      }
      handler.commit();
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000025", 
            "Cannot close cluster lock for storing virtual hosts settings.", e, null, null, null);
        }
      }
    }
    if (!add) {
      Configuration configApps = null;
      httpLock = HttpServerFrame.getHttpLock();
      try {
        httpLock.enterLockArea(HttpLock.HTTP_UPLOADED_FILES_LOCK);
      } catch (Exception e) {
        Log.logWarning("ASJ.http.000026", 
          "Cannot get cluster lock for removing uploaded filess in virtual hosts.", e, null, null, null);
      }

      try {
        try {
          configApps = handler.openConfiguration(Constants.HTTP_ALIASES, ConfigurationHandler.WRITE_ACCESS);
        } catch (NameNotFoundException ne) {
          try {
            httpLock.leaveLockArea(HttpLock.HTTP_UPLOADED_FILES_LOCK);
          } catch (Exception e) {
            Log.logWarning("ASJ.http.000027", 
              "Cannot get cluster lock for removing uploaded files in virtual hosts.", e, null, null, null);
          }
          return;
        }

        if (configApps.existsSubConfiguration(hostName)) {
          Configuration hostConfig = configApps.getSubConfiguration(hostName);
          if (hostConfig.existsSubConfiguration(alias)) {
            hostConfig.getSubConfiguration(alias).deleteAllConfigEntries();
            hostConfig.deleteConfiguration(alias);
            handler.commit();
          }
        }
      } catch (InconsistentReadException e) {
        throw e;
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
    	  String dbConf = Constants.HTTP_ALIASES + "/" + hostName + "/" + alias;
        Log.logWarning("ASJ.http.000028", 
        	"Cannot delete database configuration [{0}] or its contents.", 
        	new Object[]{dbConf}, e, null, null, null);
      } finally {
        try {
          handler.closeAllConfigurations();
        } finally {
          try {
            httpLock.leaveLockArea(HttpLock.HTTP_UPLOADED_FILES_LOCK);
          } catch (Exception e) {
            Log.logWarning("ASJ.http.000029", 
              "Cannot get cluster lock for removing uploaded files in virtual hosts.", e, null, null, null);
          }
        }
      }
    }
  }

  public synchronized void changeHttpAlias(String alias, String value) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000030", 
        "Cannot get cluster lock for storing virtual hosts settings.", e, null, null, null);
    }
    try {
      Configuration hostConfig = null;
      try {
        hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS + "/" + hostName, ConfigurationHandler.WRITE_ACCESS);
      } catch (NameNotFoundException ex) {
        try {
          hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.WRITE_ACCESS);
        } catch (NameNotFoundException e) {
          hostConfig = handler.createSubConfiguration(HttpConstants.HTTP_HOSTS);
          hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        hostConfig = hostConfig.createSubConfiguration(hostName);
        hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      Configuration aliasesConfig = null;
      try {
        aliasesConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.aliases_key + "/" + currentID);
      } catch (InconsistentReadException e) {
        throw e;
      } catch (NameNotFoundException e) {
        try {
          aliasesConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.aliases_key);
        } catch (InconsistentReadException ire) {
          throw ire;
        } catch (NameNotFoundException ee) {
          aliasesConfig = hostConfig.createSubConfiguration(HostPropertiesImpl.aliases_key);
          aliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        aliasesConfig = aliasesConfig.createSubConfiguration(currentID);
        aliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      aliasesConfig.modifyConfigEntry(alias, value, true);
      try {
        aliasesConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.aliases_key + "/" + default_key);
      } catch (InconsistentReadException e) {
        throw e;
      } catch (NameNotFoundException e) {
        aliasesConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.aliases_key);
        aliasesConfig = aliasesConfig.createSubConfiguration(default_key);
        aliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      aliasesConfig.modifyConfigEntry(alias, value, true);
      handler.commit();
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000031", 
            "Cannot close cluster lock for storing virtual hosts settings.", e, null, null, null);
        }
      }
    }
  }

  public synchronized void modifyApplicationAliases(String alias, boolean enable, boolean remove) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000032", 
        "Cannot close cluster lock for storing virtual hosts settings.", e, null, null, null);
    }
    try {
      Configuration hostConfig = null;
      try {
        hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS + "/" + hostName, ConfigurationHandler.WRITE_ACCESS);
      } catch (NameNotFoundException ex) {
        try {
          hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.WRITE_ACCESS);
        } catch (NameNotFoundException e) {
          hostConfig = handler.createSubConfiguration(HttpConstants.HTTP_HOSTS);
          hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        hostConfig = hostConfig.createSubConfiguration(hostName);
        hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      Configuration webAliasConfig = null;
      try {
        webAliasConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.web_applications_key);
      } catch (InconsistentReadException e) {
        throw e;
      } catch (NameNotFoundException e) {
        webAliasConfig = hostConfig.createSubConfiguration(HostPropertiesImpl.web_applications_key);
        webAliasConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      appAliasToConfiguration(alias, enable, remove, webAliasConfig);
      handler.commit();
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000033", 
            "Cannot close cluster lock for storing virtual hosts settings.", e, null, null, null);
        }
      }
    }
  }
  
  /**
   * A version of modifyApplicationAliases(String alias, boolean enable, boolean remove) which modifies the config for all
   * web aliases of a given application in one transaction.
   * @param aliases
   * @param enable
   * @param remove
   * @throws ConfigurationException
   */
  public synchronized void modifyAllApplicationAliases(String[] aliases, boolean enable, boolean remove) throws ConfigurationException {
    ConfigurationHandler handler = factory.getConfigurationHandler();
    HttpLock httpLock = HttpServerFrame.getHttpLock();
    try {
      httpLock.enterLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000034", 
        "Cannot close cluster lock for storing virtual hosts settings.", e, null, null, null);
    }
    try {
      Configuration hostConfig = null;
      try {
        hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS + "/" + hostName, ConfigurationHandler.WRITE_ACCESS);
      } catch (NameNotFoundException ex) {
        try {
          hostConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.WRITE_ACCESS);
        } catch (NameNotFoundException e) {
          hostConfig = handler.createSubConfiguration(HttpConstants.HTTP_HOSTS);
          hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
        }
        hostConfig = hostConfig.createSubConfiguration(hostName);
        hostConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      Configuration webAliasConfig = null;
      try {
        webAliasConfig = hostConfig.getSubConfiguration(HostPropertiesImpl.web_applications_key);
      } catch (InconsistentReadException e) {
        throw e;
      } catch (NameNotFoundException e) {
        webAliasConfig = hostConfig.createSubConfiguration(HostPropertiesImpl.web_applications_key);
        webAliasConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
      }
      for (String alias : aliases) {
      	appAliasToConfiguration(alias, enable, remove, webAliasConfig);
      }
      handler.commit();
    } finally {
      try {
        handler.closeAllConfigurations();
      } finally {
        try {
          httpLock.leaveLockArea(HttpLock.HTTP_VIRTUAL_HOSTS_LOCK);
        } catch (Exception e) {
          Log.logWarning("ASJ.http.000035", 
            "Cannot close cluster lock for storing virtual hosts settings.", e, null, null, null);
        }
      }
    }
  }

  private void writeAllSingleProperties(Configuration hostConfiguration) throws ConfigurationException {
    hostConfiguration.modifyConfigEntry(HostPropertiesImpl.keep_alive_enabled_key, String.valueOf(hostProperties.isKeepAliveEnabled()), true);
    hostConfiguration.modifyConfigEntry(HostPropertiesImpl.dir_list_key, String.valueOf(hostProperties.isList()), true);
    hostConfiguration.modifyConfigEntry(HostPropertiesImpl.use_cache_key, String.valueOf(hostProperties.isUseCache()), true);
    hostConfiguration.modifyConfigEntry(HostPropertiesImpl.enable_log_key, String.valueOf(hostProperties.isLogEnabled()), true);
    if (LOCATION_HTTP.bePath()) {
			LOCATION_HTTP.pathT("ConfigurationWriter.writeAllSingleProperties(): Directory Listing value set to [" + String.valueOf(hostProperties.isList()) + 
					"] for host [" + hostName + "] and server [" + currentID + "].");
		}
  }

  private void writeRootDir(Configuration hostConfiguration) throws ConfigurationException {
    Configuration rootDirConfig = null;
    try {
      rootDirConfig = hostConfiguration.createSubConfiguration(HostPropertiesImpl.root_key);
      rootDirConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
    } catch (NameAlreadyExistsException e) {
      rootDirConfig = hostConfiguration.getSubConfiguration(HostPropertiesImpl.root_key);
    }
    rootDirConfig.modifyConfigEntry(currentID, hostProperties.getRootDirNoCanonical(), true);
    rootDirConfig.modifyConfigEntry(default_key, hostProperties.getRootDirNoCanonical(), true);
  }

  private void writeStartPage(Configuration hostConfiguration) throws ConfigurationException {
    Configuration startPageConfig = null;
    try {
      startPageConfig = hostConfiguration.createSubConfiguration(HostPropertiesImpl.start_page_key);
      startPageConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
    } catch (NameAlreadyExistsException e) {
      startPageConfig = hostConfiguration.getSubConfiguration(HostPropertiesImpl.start_page_key);
    }
    startPageConfig.modifyConfigEntry(currentID, hostProperties.getStartPage(), true);
    startPageConfig.modifyConfigEntry(default_key, hostProperties.getStartPage(), true);
  }

  private void writeHttpAliases(Configuration hostConfiguration) throws ConfigurationException {
    Configuration httpAliasesConfig = null;
    try {
      hostConfiguration = hostConfiguration.createSubConfiguration(HostPropertiesImpl.aliases_key);
      hostConfiguration.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
    } catch (NameAlreadyExistsException e) {
      hostConfiguration = hostConfiguration.getSubConfiguration(HostPropertiesImpl.aliases_key);
    }
    try {
      httpAliasesConfig = hostConfiguration.createSubConfiguration(currentID);
      httpAliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
    } catch (NameAlreadyExistsException e) {
      httpAliasesConfig = hostConfiguration.getSubConfiguration(currentID);
    }
    fillConfiguration(httpAliasesConfig, hostProperties.getHttpAliases());
    try {
      httpAliasesConfig = hostConfiguration.createSubConfiguration(default_key);
      httpAliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
    } catch (NameAlreadyExistsException e) {
      httpAliasesConfig = hostConfiguration.getSubConfiguration(default_key);
    }
    fillConfiguration(httpAliasesConfig, hostProperties.getHttpAliases());
  }

  private void writeApplicationAliases(Configuration hostConfiguration) throws ConfigurationException {
    Configuration appAliasesConfig = null;
    try {
      appAliasesConfig = hostConfiguration.createSubConfiguration(HostPropertiesImpl.web_applications_key);
      appAliasesConfig.setCacheMode(Configuration.READ_ALL + Configuration.CACHE_OFF);
    } catch (NameAlreadyExistsException e) {
      appAliasesConfig = hostConfiguration.getSubConfiguration(HostPropertiesImpl.web_applications_key);
    }
    fillConfiguration(appAliasesConfig, hostProperties.getApplications());
  }

  private void appAliasToConfiguration(String aliasName, boolean isEnabled, boolean willRemove, Configuration configuration) throws ConfigurationException {
    if (willRemove) {
      if (configuration.existsConfigEntry(aliasName)) {
        configuration.deleteConfigEntry(aliasName);
      }
    } else {
      if (isEnabled) {
        configuration.modifyConfigEntry(aliasName, "true", true);
      } else {
        configuration.modifyConfigEntry(aliasName, "false", true);
      }
    }
  }

  private void fillConfiguration(Configuration configuration, ConcurrentReadHashMap entries) throws ConfigurationException {
    configuration.deleteAllConfigEntries();
    Object[] entriesNames = entries.getAllKeys();
    for (int i = 0; entriesNames != null && i < entriesNames.length; i++) {
      configuration.addConfigEntry((String)entriesNames[i], entries.get(entriesNames[i]));
    }
  }
}
