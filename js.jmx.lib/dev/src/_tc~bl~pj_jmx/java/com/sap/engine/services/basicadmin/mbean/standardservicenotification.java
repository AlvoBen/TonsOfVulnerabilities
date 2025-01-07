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

package com.sap.engine.services.basicadmin.mbean;

import com.sap.jmx.ObjectNameFactory;

import javax.management.Notification;
import javax.management.ObjectName;
import java.util.Properties;

/**
 * @author Miroslav Petrov
 * @version 6.30
 */
public class StandardServiceNotification extends Notification {
  public static final String SERVICE_LOADED = "service.lodaded";
  public static final String SERVICE_UNLOADED = "service.unloaded";
  public static final String SERVICE_STARTED = "service.started";
  public static final String SERVICE_STOPPED = "service.stopped";
  public static final String SERVICE_STARTUP_MODE_CHANGED = "service.startup.mode.changed";

  public static final String RUNTIME_SERVICE_PROPERTIES_CHANGED = "runtime.service.properties.changed";

  public static final String GLOBAL_CUSTOM_SERVICE_PROPERTIES_CHANGED = "global.custom.service.properties.changed";
  public static final String GLOBAL_CUSTOM_SERVICE_PROPERTIES_DELETED = "global.custom.service.properties.deleted";

  public static final String LOCAL_DEFAULT_SERVICE_PROPERTIES_CHANGED = "local.default.service.properties.changed";
  public static final String LOCAL_CUSTOM_SERVICE_PROPERTIES_CHANGED = "local.custom.service.properties.changed";
  public static final String LOCAL_CUSTOM_SERVICE_PROPERTIES_DELETED = "local.custom.service.properties.deleted";

  private String serviceName = null;
  private int clusterId;
  private byte startupMode;

  private Properties runtimeProperties;

  private Properties globalCustomProperties;
  private String[] globalCustomDeletedKeys;

  private Properties localDefaultProperties;
  private Properties localCustomProperties;
  private String[] localCustomDeletedKeys;

  public StandardServiceNotification(String type, Object serviceObjectName, long sequenceNumber) {
    super(type, serviceObjectName, sequenceNumber);
    this.serviceName = ObjectNameFactory.getName((ObjectName) serviceObjectName);
    this.clusterId = Integer.parseInt(ObjectNameFactory.getClusterNode((ObjectName) serviceObjectName));
  }

  public Properties getLocalDefaultProperties() {
    return localDefaultProperties;
  }

  public void setLocalDefaultProperties(Properties localDefaultProperties) {
    this.localDefaultProperties = localDefaultProperties;
  }

  public Properties getLocalCustomProperties() {
    return localCustomProperties;
  }

  public void setLocalCustomProperties(Properties localCustomProperties) {
    this.localCustomProperties = localCustomProperties;
  }

  public String[] getLocalCustomDeletedKeys() {
    return localCustomDeletedKeys;
  }

  public void setLocalCustomDeletedKeys(String[] localCustomDeletedKeys) {
    this.localCustomDeletedKeys = localCustomDeletedKeys;
  }

  public String[] getGlobalCustomDeletedKeys() {
    return globalCustomDeletedKeys;
  }

  public void setGlobalCustomDeletedKeys(String[] globalCustomDeletedKeys) {
    this.globalCustomDeletedKeys = globalCustomDeletedKeys;
  }

  public byte getStartupMode() {
    return startupMode;
  }

  public void setStartupMode(byte startupMode) {
    this.startupMode = startupMode;
  }

  public int getClusterId() {
    return clusterId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public Properties getRuntimeProperties() {
    return runtimeProperties;
  }

  public void setRuntimeProperties(Properties runtimeProperties) {
    this.runtimeProperties = runtimeProperties;
  }

  public Properties getGlobalCustomProperties() {
    return globalCustomProperties;
  }

  public void setGlobalCustomProperties(Properties globalCustomProperties) {
    this.globalCustomProperties = globalCustomProperties;
  }

}

