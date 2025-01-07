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
package com.sap.engine.services.httpserver.server.hosts.impl;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.services.httpserver.interfaces.exceptions.IllegalHostArgumentException;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.server.HttpLock;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.hosts.HostPropertiesModifier;

public class HostPropertiesModifierImpl implements HostPropertiesModifier {
  private HostPropertiesImpl hostProperties = null;
  private ConfigurationWriter configurationWriter = null;
  private HttpProperties httpProperties = null;
  private ConfigurationReader configurationReader = null;

  public HostPropertiesModifierImpl(HttpProperties httpProperties, HostPropertiesImpl hostProperties
    , ConfigurationWriter configurationWriter, ConfigurationReader configurationReader) {
    this.hostProperties = hostProperties;
    this.configurationWriter = configurationWriter;
    this.httpProperties = httpProperties;
    this.configurationReader = configurationReader;
  }

  public void setKeepAliveEnabled(boolean keepAliveEnabled) throws ConfigurationException {
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyProperty(HostPropertiesImpl.keep_alive_enabled_key, String.valueOf(keepAliveEnabled));
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000036", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.keepAliveEnabled = keepAliveEnabled;
  }

  public void setList(boolean list) throws ConfigurationException {
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyProperty(HostPropertiesImpl.dir_list_key, String.valueOf(list));
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000037",
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.list = list;
  }

  public void setLogEnabled(boolean enableLog) throws ConfigurationException {
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyProperty(HostPropertiesImpl.enable_log_key, String.valueOf(enableLog));
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000038", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.enableLog = enableLog;
  }

  public void setUseCache(boolean useCache) throws ConfigurationException {
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyProperty(HostPropertiesImpl.use_cache_key, String.valueOf(useCache));
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000039", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.useCache = useCache;
  }

  public void setStartPage(String startPage) throws ConfigurationException {
    if (startPage != null && startPage.trim().equals("")) {
      startPage = null;
    }
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyProperty(HostPropertiesImpl.start_page_key, startPage);
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000040", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.startPage = startPage;
  }

  public void setRootDir(String vDir) throws ConfigurationException {
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyProperty(HostPropertiesImpl.root_key, vDir);
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000041", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    try {
      hostProperties.initRootDir(vDir);
    } catch (Exception t) {      
      Log.logWarning("ASJ.http.000400", "Cannot set [{0}] as a [{1}] of the virtual host [{2}]", 
                 new Object[]{vDir, HostPropertiesImpl.root_key, hostProperties.getHostName()}, t, null, null, null);
    }
  }

  public void addHttpAlias(String alias, String value) throws ConfigurationException, IllegalHostArgumentException {
    alias = alias.replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);
    if (!alias.equals(ParseUtils.separator) && alias.startsWith(ParseUtils.separator)) {
      alias = alias.substring(1);
    }
    checkAlias(alias, true);
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyHttpAlias(alias, value, true);        
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000042", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.aliases.put(alias, ConfigurationReader.convertHttpAliasPath(value));
  } 
  
  public void removeHttpAlias(String alias) throws ConfigurationException {
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyHttpAlias(alias, null, false);
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000043", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.aliases.remove(alias);
  }

  public void changeHttpAlias(String alias, String value) throws ConfigurationException {
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.changeHttpAlias(alias, value);
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000323", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.aliases.put(alias, ConfigurationReader.convertHttpAliasPath(value));
  }

  /**
   * @deprecated use HostPropertiesModifier.enableApplicationAlias(String alias, boolean persistent)
   *             where you can specify to update or not the configuration.
   */  
  public void enableApplicationAlias(String alias) throws ConfigurationException {
    enableApplicationAlias(alias, true);
  }//end of enableApplicationAlias(String alias)

  public void enableApplicationAlias(String alias, boolean persistent) throws ConfigurationException {
    alias = alias.replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);
    if (persistent) {
      for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
        try {
          configurationWriter.modifyApplicationAliases(alias, true, false);
          break;
        } catch (InconsistentReadException e) {
          try {
            Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
          } catch (InterruptedException ie) {
            Log.logWarning("ASJ.http.000044", 
              "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
              "for HTTP Provider service for configuration access.", ie, null, null, null);
          }
          if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
            throw e;
          }
        }
      }
    }
    hostProperties.getApplications().put(alias, "true");
  }//end of enableApplicationAlias(String alias, boolean persistent)
  
  public void enableAllApplicationAliases(String appName, String[] aliasesCanonicalized, boolean persistent) throws ConfigurationException {
    if (persistent) {
      for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
        try {
          configurationWriter.modifyAllApplicationAliases(aliasesCanonicalized, true, false);
          break;
        } catch (InconsistentReadException e) {
          try {
            Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
          } catch (InterruptedException ie) {
            Log.logWarning("ASJ.http.000045", 
            	"Cannot obtain database read lock when trying to persist http hosts for web aliases " + 
            	"for application [{0}]. " +
            	"Possible reason: thread interrupted while waiting " +
            	"for cluster lock for HTTP Provider service for configuration access.", 
            	new Object[]{appName}, ie, null, null, null);
          }
          if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
            throw e;
          }
        }
      }
    }
    for (String alias : aliasesCanonicalized) {
    	hostProperties.getApplications().put(alias, "true");
    }
  }//end of enableAllApplicationAliases(String appName, String[] aliasesCanonicalized, boolean persistent)

  public void disableApplicationAlias(String alias) throws ConfigurationException {
    alias = alias.replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.modifyApplicationAliases(alias, false, false);
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logWarning("ASJ.http.000046", 
            "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
    hostProperties.getApplications().put(alias, "false");
  }

  public void removeApplicationAlias(String alias, boolean persistent) throws ConfigurationException {
    alias = alias.replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);

    if (persistent) {
      for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
        try {
          configurationWriter.modifyApplicationAliases(alias, false, true);
          break;
        } catch (InconsistentReadException e) {
          try {
            Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
          } catch (InterruptedException ie) {
            Log.logWarning("ASJ.http.000047", 
              "Cannot obtain database read lock. Possible reason: thread interrupted while waiting for cluster lock " +
              "for HTTP Provider service for configuration access.", ie, null, null, null);
          }
          if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
            throw e;
          }
        }
      }
    }

    hostProperties.getApplications().remove(alias);
  }

  /**
   * Checks if the alias is already added to the host. If checkDB is true,
   *  before checking syncs the data from the configuration
   *  
   * @param alias         aliasName
   * @param checkDB       if true syncs the host configuration from the DB
   * @throws IllegalHostArgumentException     if the alias already exists in the host
   */
  public void checkAlias(String alias, boolean checkDB) throws IllegalHostArgumentException {
    alias = alias.replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);
    //int zoneInd = -1;

    if (checkDB) {
      try {
        //Access the configuration if checkDB is true, which is false in the case for CSN message 2799735 2006
        configurationReader.readConfiguration(false); 
      } catch (ConfigurationException e) {
        Log.logWarning("ASJ.http.000048", "Cannot update cached properties.", e, null, null, null);
      }
    }

    //CSN messages 97492 2008, 481921 2008 
    //This checks for <alias>~<zone-name>
    //The problem is in the following: 
    //There is already deployed application "vily"
    //I want to deploy another application "vily~vily~vily"
    //With this check the second alias will not be registered 
    //because everything after first '~' will be removed and only "vily" will remain.
    //But such alias already exists on this host.

    //if (httpProperties.getZoneSeparator() != null) {
    //  zoneInd = alias.indexOf(httpProperties.getZoneSeparator()); //does not access the configuration
    //}
    //String aliasWithoutZone = null;
    //if (zoneInd > -1) {
    //  aliasWithoutZone = alias.substring(0, zoneInd);
    //}

    if (hostProperties.isApplicationAlias(alias) || hostProperties.getAliasValue(alias) != null) {
      //|| aliasWithoutZone != null && hostProperties.getAliasValue(aliasWithoutZone) != null) {
      throw new IllegalHostArgumentException(IllegalHostArgumentException.CANNOT_ADD_HTTP_ALIAS_ON_HOST_BECAUSE_APPLICATION_ALIAS_ALREADY_EXSITS, 
        new Object[]{alias, hostProperties.getHostName()});
    }
  }//end of checkAlias(String alias, boolean checkDB)
}
