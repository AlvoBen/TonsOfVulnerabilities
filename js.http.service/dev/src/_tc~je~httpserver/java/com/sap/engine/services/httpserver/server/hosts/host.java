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
package com.sap.engine.services.httpserver.server.hosts;

import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.services.httpserver.server.*;
import com.sap.engine.services.httpserver.server.hosts.impl.HostPropertiesImpl;
import com.sap.engine.services.httpserver.server.hosts.impl.HostPropertiesModifierImpl;
import com.sap.engine.services.httpserver.server.hosts.impl.ConfigurationReader;
import com.sap.engine.services.httpserver.server.hosts.impl.ConfigurationWriter;
import com.sap.engine.services.httpserver.chain.HostScope;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.ApplicationServiceContext;

public class Host implements HostScope {
  private byte[] version = null;
  private String hostName = null;
  private HostPropertiesModifierImpl hostPropertiesModifier = null;
  private HostPropertiesImpl hostProperties = null;
  private ConfigurationReader configurationReader = null;
  private ConfigurationWriter configurationWriter = null;
  private Date date = null;
  private HttpProperties httpProperties = null;
  private ConcurrentHashMapObjectObject translationTable = new ConcurrentHashMapObjectObject();

  public Host(String hostName, byte[] ver, HttpProperties httpProperties, HttpHosts httpHosts, Date date,
              ConfigurationHandlerFactory factory, ApplicationServiceContext sc) {
    this.hostName = hostName;
    this.version = ver;
    this.date = date;
    this.httpProperties = httpProperties;
    hostProperties = new HostPropertiesImpl(hostName, httpHosts);
    int serverId = sc.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();
    configurationReader = new ConfigurationReader(hostProperties, serverId, factory);
    configurationWriter = new ConfigurationWriter(hostProperties, serverId, factory);
    hostPropertiesModifier = new HostPropertiesModifierImpl(httpProperties, hostProperties, configurationWriter, configurationReader);
    translationTable = new ConcurrentHashMapObjectObject();
  }

  public String getHostName() {
    return hostName;
  }

  public HostPropertiesImpl getHostProperties() {
    return hostProperties;
  }

  public HostPropertiesModifierImpl getHostPropertiesModyfier() {
    return hostPropertiesModifier;
  }

  public ConfigurationReader getConfigurationReader() {
    return configurationReader;
  }

  public Date getDate() {
    return date;
  }

  public boolean rootExists() {
    return hostProperties.rootExists();
  }

  public byte[] getVersion() {
    return this.version;
  }

  public void store() throws ConfigurationException {
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        configurationWriter.writeToConfiguration();
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logError("ASJ.http.000107", 
            "A thread interrupted while waiting for cluster lock " +
            "for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw e;
        }
      }
    }
  }
  
  public ConcurrentHashMapObjectObject getTranslationTable() {
    return translationTable;
  }

}
