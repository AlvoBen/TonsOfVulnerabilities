package com.sap.engine.frame.container.monitor;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.engine.frame.state.ManagementInterface;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Set;

/**
 * General interface for services monitoring
 *
 * @see com.sap.engine.frame.container.monitor.ComponentMonitor
 * @author Dimitar Kostadinov
 */
public interface ServiceMonitor extends ComponentMonitor {

  /**
   * @deprecated
   */
  public static final byte STARTUP_STATE_STOPPED = 10;

  /**
   * @deprecated
   */
  public static final byte STARTUP_STATE_STARTED = 11;

  /**
   * The service is not started during engine start up
   */
  public static final byte MANUAL_START = 0;
  /**
   * @deprecated
   */
  public static final byte AUTOMATIC_START = 1;

  /**
   * The service is starting on engine start up
   */
  public static final byte ALWAYS_START = 2;

  /**
   * The component is disabled
   */
  public static final byte DISABLED = 3;

  /**
   * Service is stopped
   */
  static final byte INTERNAL_STATUS_STOPPED    = 80;

  /**
   * Service is started
   */
  static final byte INTERNAL_STATUS_STARTED    = 81;

  /**
   * Service is starting
   */
  static final byte INTERNAL_STATUS_STARTING   = 82;

  /**
   * Service fails to start (last call to start() method exits with exception)
   */
  static final byte INTERNAL_STATUS_START_FAIL = 83;

  /**
   * Service is stopping
   */
  static final byte INTERNAL_STATUS_STOPPING   = 84;

  /**
   * Service fails to stop (last call to stop() method exits with exception)
   */
  static final byte INTERNAL_STATUS_STOP_FAIL  = 85;

  /**
   * Returns service internal status. The status can be INTERNAL_STATUS_STOPPED, INTERNAL_STATUS_STARTED, INTERNAL_STATUS_STARTING,
   * INTERNAL_STATUS_START_FAIL, INTERNAL_STATUS_STOPPING, INTERNAL_STATUS_STOP_FAIL
   *
   * @return current service internal status
   */
  public byte getInternalStatus();

  /**
   * Returns service frame class name
   *
   * @return frame name
   */
  public String getApplicationFrameClassName();

  /**
   * Returns service runtime control class name
   *
   * @return runtime control name
   */
  public String getRuntimeControlClassName();

  /**
   * Start this service
   *
   * @throws ServiceException - if an error occurs
   */
  public void start() throws ServiceException;

  /**
   * Stop this service
   *
   * @throws ServiceException - if an error occurs
   */
  public void stop() throws ServiceException;

  /**
   * Returns true if the service is core. Core services are: {telnet, p4, security, tc~bl~deploy_controller,
   * adminadapter} and all required by this set recursively.
   *
   * @return true if the service is core
   */
  public boolean isCore();

  /**
   * Current startup mode. The mode can be DISABLED, ALWAYS_START or MANUAL_START depending on current filter settings
   *
   * @return startup mode
   */
  public byte getStartupMode();

  /**
   * Current startup state. The state can be STARTUP_STATE_STARTED if the service is started or
   * STARTUP_STATE_STOPPED otherwise.
   *
   * @return startup state
   */
  public byte getStartupState();

  /**
   * Returns service runtime configuration that provides service runtime modifiable properties update.
   *
   * @see com.sap.engine.frame.container.runtime.RuntimeConfiguration
   *
   * @return - runtime configuration or <code>null</code> if runtime is not registered
   */
  public RuntimeConfiguration getRuntimeConfiguration();

  /**
   * Read service properties from DB
   *
   * @return current service properties
   */
  public Properties getProperties();

  /**
   * Read service property from DB
   *
   * @return service property value
   */
  public String getProperty(String key);

  /**
   * Returns service management interface. This interface can be registered during service start and must be unregistered
   * in the service stop() method.
   *
   * @see com.sap.engine.frame.state.ServiceState#registerManagementInterface(com.sap.engine.frame.state.ManagementInterface)
   * @see com.sap.engine.frame.state.ServiceState#unregisterManagementInterface()
   *
   * @return management interface or null if not registered
   */
  public ManagementInterface getManagementInterface();

  //////////////////////////////////////////// DEPRECATED //////////////////////////////////////////////////////////////
  /**
   * @deprecated
   */
  public String getCommunicationFrameClassName();
  /**
   * @deprecated
   */  
  public void setStartupMode(byte startupMode) throws ServiceException;
  /**
   * @deprecated
   */
  public Properties getGlobalDefaultProperties();
  /**
   * @deprecated
   */
  public Properties getGlobalCustomProperties();
  /**
   * @deprecated
   */
  public Properties getLocalDefaultProperties();
  /**
   * @deprecated
   */
  public Properties getLocalCustomProperties();
  /**
   * @deprecated
   */
  public boolean setProperties(Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public boolean setProperty(String key, String value) throws ServiceException;
  /**
   * @deprecated
   */
  public void removeProperty(String key) throws ServiceException;
  /**
   * @deprecated
   */
  public void removeProperties(String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public void setGlobalDefaultProperties(Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public void setGlobalCustomProperties(Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public void setLocalDefaultProperties(Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public void setLocalCustomProperties(Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public boolean notifyPropertiesChange(Properties properties) throws ServiceException;
  /**
   * @deprecated
   */
  public void restoreGlobalProperties() throws ServiceException;
  /**
   * @deprecated
   */
  public void restoreGlobalProperty(String key) throws ServiceException;
  /**
   * @deprecated
   */
  public Set restoreLocalProperties() throws ServiceException;
  /**
   * @deprecated
   */
  public void restoreLocalProperty(String key) throws ServiceException;
  /**
   * @deprecated
   */
  public void removeLocalProperties(String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public void removeGlobalProperties(String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public Set[] restoreGlobalProperties(String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public void restoreLocalProperties(String[] keys) throws ServiceException;
  /**
   * @deprecated
   */
  public Set getGlobalSecuredPropertiesKeys();
  /**
   * @deprecated
   */
  public Set getLocalSecuredPropertiesKeys();
  /**
   * @deprecated
   */
  public void setGlobalDefaultProperties(Properties properties, Set secured) throws ServiceException;
  /**
   * @deprecated
   */
  public void setGlobalCustomProperties(Properties properties, Set secured) throws ServiceException;
  /**
   * @deprecated
   */
  public void setLocalDefaultProperties(Properties properties, Set secured) throws ServiceException;
  /**
   * @deprecated
   */
  public void setLocalCustomProperties(Properties properties, Set secured) throws ServiceException;

}

