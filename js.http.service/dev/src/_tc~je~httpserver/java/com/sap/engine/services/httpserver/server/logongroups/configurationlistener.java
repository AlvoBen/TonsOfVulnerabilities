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

import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.tc.logging.Location;

public class ConfigurationListener implements ConfigurationChangedListener {
  private static Location traceLocation = Location.getLocation(ConfigurationListener.class);
  private LogonGroupsManager logonGroupsManager;
  private ConfigurationReader configurationReader;

  public ConfigurationListener(LogonGroupsManager logonGroupsManager, ConfigurationReader configurationReader) {
    this.logonGroupsManager = logonGroupsManager;
    this.configurationReader = configurationReader;
  }

  public synchronized void configurationChanged(ChangeEvent e) {
    try {
      if (traceLocation.beDebug()) {
        String msg = "Received configuration changed event e =" + e;
        if (e.getDetailedChangeEvents() != null) {
          for (ChangeEvent de : e.getDetailedChangeEvents()) {
            msg += "; detailed event=[" + de + "] action=[" + de.getAction() + "]";
          }
        }
        traceLocation.debugT(msg);
      }
      String logonGroupName = e.getPath();
      ChangeEvent events[] = e.getDetailedChangeEvents();
      if (events == null || events.length == 0) {
        if (e.getAction() == 2) {
          logonGroupsManager.allLogonGroupsUnregistered();
        }
      } else {
        for (int i = 0; i < events.length; i++) {
          ChangeEvent event = events[i];
          if (event.getPath().length() <= "HttpZones/".length()) {
            continue;
          }
          logonGroupName = event.getPath().substring("HttpZones/".length());
          if (e.getAction() == 2) {
            logonGroupsManager.logonGroupUnregistered(logonGroupName);
          } else {
            configurationReader.logonGroupUpdated(logonGroupName);
          }
        }
      }
    } catch (ThreadDeath t) {
      throw t;
    } catch (OutOfMemoryError t) {
      throw t;
    } catch (Throwable t) {
      Log.logWarning("ASJ.http.000050", 
        "Error in processing ACTION_MODIFIED event for zones configuration.", t, null, null, null);
      return;
    }
  }
}