/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.frame.core.monitor;

import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.frame.container.monitor.DescriptorContainer;
import com.sap.engine.frame.ServiceException;

import java.util.Properties;
import java.util.Set;

/**
 * General interface providing access to the kernel configuration
 *
 * @author Dimitar Kostadinov
 */
public interface CoreMonitor {

  /**
   * Normal runtime mode
   */
  public static final byte RUNTIME_MODE_NORMAL = 0;

  /**
   * Safe mode used for system update. Only one server node is running in this mode
   */
  public static final byte RUNTIME_MODE_SAFE = 1;

  /**
   * The default runtime action
   */
  public static final byte RUNTIME_ACTION_NONE = 0;

  /**
   * MIGRATE - the system is migrating
   */
  public static final byte RUNTIME_ACTION_MIGRATE = 1;

  /**
   * UPGRADE - the system is upgrading
   */
  public static final byte RUNTIME_ACTION_UPGRADE = 2;

  /**
   * CONVERT - the system is converting
   */
  public static final byte RUNTIME_ACTION_CONVERT = 3;

  /**
   * DEPLOY - components are deploying
   */
  public static final byte RUNTIME_ACTION_DEPLOY = 4;

  /**
   * APPLICATION_MIGRATE - applications are migrating
   */
  public static final byte RUNTIME_ACTION_APPLICATION_MIGRATE = 5;

  /**
   * EMERGENCY
   */
  public static final byte RUNTIME_ACTION_EMERGENCY = 6;

  /**
   * SWITCH
   */
  public static final byte RUNTIME_ACTION_SWITCH = 7;

  /**
   * Instance type J2EE. The default type with all services running on it.
   */
  public static final String INSTANCE_J2EE = "j2ee";

  /**
   * Instance type JMS. Used for jms singleton - only jms service is running on it.
   */
  public static final String INSTANCE_JMS = "jms";

  /**
   * Returns runtime mode. Possible values are RUNTIME_MODE_NORMAL and RUNTIME_MODE_SAFE.
   *
   * @return current runtime mode
   */
  public byte getRuntimeMode();

  /**
   * Returns current runtime action. The default action is NONE. During upgrade, migration, installation, etc. the
   * action can be MIGRATE, UPGRADE, CONVERT, DEPLOY, APPLICATION_MIGRATE, EMERGENCY and SWITCH.
   *
   * @return current action
   */
  public byte getRuntimeAction();

  /**
   * Returns core version.
   *
   * @see com.sap.engine.core.Framework#getVersion() 
   * @return core version
   */
  public String getCoreVersion();

  /**
   * Returns core major version.
   *
   * @see com.sap.engine.core.Framework#stringVersion
   * @return major version
   */
  public String getCoreMajorVersion();

  /**
   * Returns core minor version
   *
   * @see com.sap.engine.core.Framework#stringVersion
   * @return minor version
   */
  public String getCoreMinorVersion();

  /**
   * Returns core micro version.
   *
   * @see com.sap.engine.core.Framework#stringVersion
   * @return micro version
   */
  public String getCoreMicroVersion();

  /**
   * Returns core build version.
   *
   * @see com.sap.engine.core.Framework#stringVersion
   * @return build version
   */
  public String getCoreBuildVersion();

  /**
   * Returns all manager names.
   *
   * @return all manager names
   */
  public String[] getAllManagers();

  /**
   * Returns manager properties read from DB.
   *
   * @param manager - the name of the manager
   * @return manager properties
   *
   * @exception com.sap.engine.frame.ServiceRuntimeException if any error occurs while reading properties from DB
   * @throws IllegalArgumentException if manager name is not correct
   */
  public Properties getManagerProperties(String manager);

  /**
   * Returns manager property read from DB.
   *
   * @param manager - the name of the manager
   * @param key - the mane of the property
   * @return property value
   *
   * @exception com.sap.engine.frame.ServiceRuntimeException if any error occurs while reading properties from DB
   * @throws IllegalArgumentException if manager name is not correct
   */
  public String getManagerProperty(String manager, String key);

  /**
   * Updates manager runtime changeable properties.
   *
   * @param name - the name of the manager
   * @param properties - updated online modifiable properties
   * @throws IllegalArgumentException if manager name or properties are not correct
   * @see com.sap.engine.core.Manager#updateProperties(java.util.Properties)
   */
  public void updateManagerProperties(String name, Properties properties) throws IllegalArgumentException;

  /**
   * Shut down the current server node with corresponding exit code.
   *
   * @param exitCode - VM exit code
   */
  public void shutDown(int exitCode);

  /**
   * Returns instance type. Possible instance types are "j2ee" and "jms".
   *
   * @return current instance type
   */
  public String getInstanceType();

  /**
   * Returns ManagementInterface for <code>manager</code>. If interface is not registered <code>null</code> is returned
   *
   * @param   manager  The name of the manager whose ManagementInterface is being retrieved
   * @return   ManagementInterface that is used for runtime management and monitoring of the respective manager
   * or null if not exist
   *
   * @throws IllegalArgumentException if manager name is not correct
   */
  public ManagementInterface getManagementInterface(String manager);

  ///////////////////////////////////// DEPRECATED /////////////////////////////////////////////////////////////////////
  // Manager properties and configuration files must be accessed from configuration library ////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns runtime manager properties.
   *
   * @see com.sap.engine.core.Manager#getCurrentProperties()
   * @param manager - the name of the manager
   * @return manager properties
   *
   * @throws IllegalArgumentException if manager name is not correct
   * @deprecated use getManagerProperties(String manager) method instead
   */
  @Deprecated
  public Properties getManagerRuntimeProperties(String manager);

  /**
   * Returns runtime manager property.
   *
   * @see com.sap.engine.core.Manager#getCurrentProperty(String)
   * @param manager - the name of the manager
   * @param key - the mane of the property
   * @return property value
   *
   * @throws IllegalArgumentException if manager name is not correct
   * @deprecated use getManagerProperty(String manager, String key) method instead
   */
  @Deprecated
  public String getManagerRuntimeProperty(String manager, String key);

  /**
   * @deprecated - use shutDown(int exitCode)
   */
  public void shutDown();
  /**
   * @deprecated
   */
  public boolean setManagerProperty(String manager, String key, String value) throws IllegalArgumentException;
  /**
   * @deprecated
   */
  public boolean setManagerProperties(String manager, Properties properties) throws IllegalArgumentException;
  /**
   * @deprecated
   */
  public DescriptorContainer getDescriptorContainer();
  /**
   * @deprecated
   */
  public Properties getManagerGlobalDefaultProperties(String manager) throws ServiceException;
  /**
   * @deprecated
   */
  public Properties getManagerGlobalCustomProperties(String manager) throws ServiceException;
  /**
   * @deprecated
   */
  public Properties getManagerLocalDefaultProperties(String manager) throws ServiceException;
  /**
   * @deprecated
   */
  public Properties getManagerLocalCustomProperties(String manager) throws ServiceException;
  /**
   * @deprecated
   */
  public void setManagerGlobalDefaultProperties(String manager, Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public void setManagerGlobalCustomProperties(String manager, Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public void setManagerLocalDefaultProperties(String manager, Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public void setManagerLocalCustomProperties(String manager, Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public void setManagerGlobalDefaultProperties(String manager, Properties properties, Set secured) throws ServiceException;
  /**
   * @deprecated
   */
  public void setManagerGlobalCustomProperties(String manager, Properties properties, Set secured) throws ServiceException;
  /**
   * @deprecated
   */
  public void setManagerLocalDefaultProperties(String manager, Properties properties, Set secured) throws ServiceException;
  /**
   * @deprecated
   */
  public void setManagerLocalCustomProperties(String manager, Properties properties, Set secured) throws ServiceException;
  /**
   * @deprecated
   */
  public boolean notifyManagerPropertiesChange(String manager, Properties properties);
  /**
   * @deprecated
   */
  public void restoreGlobalProperties(String manager) throws ServiceException;
  /**
   * @deprecated
   */
  public void restoreGlobalProperty(String manager, String key) throws ServiceException;
  /**
   * @deprecated
   */
  public Set restoreLocalProperties(String manager) throws ServiceException;
  /**
   * @deprecated
   */
  public void restoreLocalProperty(String manager, String key) throws ServiceException;
  /**
   * @deprecated
   */
  public Set[] restoreGlobalProperties(String manager, String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public void restoreLocalProperties(String manager, String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public void removeLocalProperties(String manager, String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public void removeGlobalProperties(String manager, String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public Set getGlobalSecuredPropertiesKeys(String manager) throws ServiceException;
  /**
   * @deprecated
   */
  public Set getLocalSecuredPropertiesKeys(String manager) throws ServiceException;

}

