/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */

package com.sap.engine.services.httpserver.server.hosts.impl;

import com.sap.engine.frame.core.configuration.*;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.HttpHosts;
import com.sap.engine.services.httpserver.server.hosts.Host;
import com.sap.engine.services.httpserver.exceptions.IllegalHostArgumentsException;
import com.sap.engine.services.httpserver.lib.util.HttpConstants;

/**
 *
 * @author Violeta Uzunova
 * @version 6.30
 */
public class HttpHostListener implements ConfigurationChangedListener {
  private ConfigurationReader configurationReader = null;
  private HttpHosts httpHosts = null;
  private ConfigurationHandlerFactory factory = null;
  private boolean isGlobal = false;

  public HttpHostListener(ConfigurationReader configurationReader) {
    this.configurationReader = configurationReader;
  }

  public HttpHostListener(HttpHosts httpHosts, ConfigurationHandlerFactory factory) {
    this.httpHosts = httpHosts;
    this.factory = factory;
    isGlobal = true; // listening when a new host is created
  }

  public void configurationChanged(ChangeEvent e) {
    if (e.getAction() != ChangeEvent.ACTION_MODIFIED) {
      return;
    }
    try {
      if (isGlobal) {
        checkChange();
      } else {
        configurationReader.readConfiguration(true);
      }
    } catch (OutOfMemoryError t) {
      throw t;
    } catch (ThreadDeath t) {
      throw t;
    } catch (Throwable t) {
      Log.logWarning("ASJ.http.000049", 
        "Cannot synchronize local host properties with configuration. " +
        "Error in processing ACTION_MODIFIED event.", t, null, null, null);
      return;
    }
  }

  private void checkChange() throws ConfigurationException, IllegalHostArgumentsException {
    Host local[] = httpHosts.getAllHosts();
    String localHosts[] = new String[local.length];
    for (int i = 0; i < local.length; i++) {
      localHosts[i] = local[i].getHostName();
    }
    String dbHosts[] = null;
    ConfigurationHandler handler = null;
    try {
      handler = factory.getConfigurationHandler();
      Configuration hostsConfig = handler.openConfiguration(HttpConstants.HTTP_HOSTS, ConfigurationHandler.READ_ACCESS);
      dbHosts = hostsConfig.getAllSubConfigurationNames();
      handler.commit();
    } finally {
      handler.closeAllConfigurations();
    }
    for (int i = 0; i < localHosts.length; i++) {
      if (!findIn(dbHosts, localHosts[i])) {
        httpHosts.removeHost(localHosts[i], true);
        return;
      }
    }
    for (int i = 0; i < dbHosts.length; i++) {
      if (!findIn(localHosts, dbHosts[i])) {
        httpHosts.createHost(dbHosts[i], false);
        return;
      }
    }
  }

  private boolean findIn(String all[], String host) {
    for (int i = 0; i < all.length; i++) {
      if (all[i].equals(host)) {
        return true;
      }
    }
    return false;
  }
}
