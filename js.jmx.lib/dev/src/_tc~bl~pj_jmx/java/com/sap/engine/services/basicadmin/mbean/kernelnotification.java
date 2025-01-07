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
public class KernelNotification extends Notification {

  public static final String RUNTIME_MANAGER_PROPERTIES_CHANGED = "runtime.manager.properties.changed";

  public static final String GLOBAL_CUSTOM_MANAGER_PROPERTIES_CHANGED = "global.custom.manager.properties.changed";
  public static final String GLOBAL_CUSTOM_MANAGER_PROPERTIES_DELETED = "global.custom.manager.properties.deleted";

  public static final String LOCAL_DEFAULT_MANAGER_PROPERTIES_CHANGED = "local.default.manager.properties.changed";
  public static final String LOCAL_CUSTOM_MANAGER_PROPERTIES_CHANGED = "local.custom.manager.properties.changed";
  public static final String LOCAL_CUSTOM_MANAGER_PROPERTIES_DELETED = "local.custom.manager.properties.deleted";

  private int clusterId;
  private String manager = null;

  private Properties runtimeManagerProperties;

  private Properties globalCustomProperties;
  private String[] globalCustomDeletedKeys;

  private Properties localDefaultManagerProperties;
  private Properties localCustomManagerProperties;
  private String[] localCustomDeletedKeys;

  public KernelNotification(String type, Object serviceObjectName, long sequenceNumber) {
    super(type, serviceObjectName, sequenceNumber);
    this.clusterId = Integer.parseInt(ObjectNameFactory.getClusterNode((ObjectName) serviceObjectName));
  }

  public String getManager() {
    return manager;
  }

  public void setManager(String manager) {
    this.manager = manager;
  }

  public int getClusterId() {
    return clusterId;
  }

  public Properties getRuntimeProperties() {
    return runtimeManagerProperties;
  }

  public void setRuntimeProperties(Properties runtimeManagerProperties) {
    this.runtimeManagerProperties = runtimeManagerProperties;
  }

  public String[] getGlobalCustomDeletedKeys() {
    return globalCustomDeletedKeys;
  }

  public void setGlobalCustomDeletedKeys(String[] globalCustomDeletedKeys) {
    this.globalCustomDeletedKeys = globalCustomDeletedKeys;
  }

  public Properties getLocalDefaultManagerProperties() {
    return localDefaultManagerProperties;
  }

  public void setLocalDefaultManagerProperties(Properties localDefaultManagerProperties) {
    this.localDefaultManagerProperties = localDefaultManagerProperties;
  }

  public Properties getLocalCustomManagerProperties() {
    return localCustomManagerProperties;
  }

  public void setLocalCustomManagerProperties(Properties localCustomManagerProperties) {
    this.localCustomManagerProperties = localCustomManagerProperties;
  }

  public String[] getLocalCustomDeletedKeys() {
    return localCustomDeletedKeys;
  }

  public void setLocalCustomDeletedKeys(String[] localCustomDeletedKeys) {
    this.localCustomDeletedKeys = localCustomDeletedKeys;
  }

  public Properties getGlobalCustomProperties() {
    return globalCustomProperties;
  }

  public void setGlobalCustomProperties(Properties globalCustomProperties) {
    this.globalCustomProperties = globalCustomProperties;
  }

}
