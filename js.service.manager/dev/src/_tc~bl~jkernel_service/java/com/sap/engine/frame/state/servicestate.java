/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame.state;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.cluster.event.ServiceEventListener;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;

import java.util.Properties;
import java.util.Set;
import java.util.Map;

/**
 * This interface provide access to service specific resources, registration and unregistration of container and
 * cluster listeners and management resources.
 *
 * @author Dimitar Kostadinov
 */
public interface ServiceState {

  /**
   * Service persistent container to get/set service specific content from DB
   *
   * @see  com.sap.engine.frame.state.PersistentContainer
   * @return persistent container
   */
  public PersistentContainer getPersistentContainer();

  /**
   * Returns service temp directory name
   *
   * @return service temp directory name
   */
  public String getWorkingDirectoryName();

  /**
   * Stop current service
   *
   * @throws ServiceException - if an error occurs
   */
  public void stopMe() throws ServiceException;

  /**
   * Returns service name for the service hold this context
   *
   * @return service name
   */
  public String getServiceName();

  /**
   * Returns service properties read from DB for the service hold this context
   *
   * @return service properties
   */
  public Properties getProperties();

  /**
   * Returns service property read from DB for this service
   *
   * @param key - property name
   * @return property value
   */
  public String getProperty(String key);

  /**
   * Returns service property read from DB for this service
   *
   * @param key - property name
   * @param defaultValue - return value if the property doesn't exists
   * @return property value
   */
  public String getProperty(String key, String defaultValue);

  /**
   * Register service management interface implementation
   *
   * @see com.sap.engine.frame.state.ManagementInterface
   * @param mi - interface implementation
   * @throws ServiceException - if an error occurs
   */
  public void registerManagementInterface(ManagementInterface mi) throws ServiceException;

  /**
   * Unregister management interface
   */
  public void unregisterManagementInterface();

  /**
   * Register container event listener to receive container events
   *
   * @see com.sap.engine.frame.container.event.ContainerEventListenerAdapter
   *
   * @param mask - use for event filtering, for example to receive interface available and service started events the
   * mask must be ContainerEventListener.MASK_INTERFACE_AVAILABLE | ContainerEventListener.MASK_SERVICE_STARTED
   * @param names - use for name filtering, for example to receive events for component <code>X</code> the set must
   * contains <code>X</code>
   * @param listener - listener implementation
   */
  public void registerContainerEventListener(int mask, Set names, ContainerEventListener listener);

  /**
   * Unregister container event listener
   */
  public void unregisterContainerEventListener();

  /**
   * Register cluster event listener to receive cluster events (element join, element loss, etc)
   *
   * @see com.sap.engine.frame.cluster.event.ClusterEventListener
   * @param listener - listener implementation
   * @throws ListenerAlreadyRegisteredException - if the listener is already registered
   */
  public void registerClusterEventListener(ClusterEventListener listener) throws ListenerAlreadyRegisteredException;

 /**
   * Unregister cluster event listener
   */
  public void unregisterClusterEventListener();

  /**
   * Register service event listener to receive cluster wide events for this service (service started/stopped)
   *
   * @see com.sap.engine.frame.cluster.event.ServiceEventListener
   * @param listener - listener implementation
   * @throws ListenerAlreadyRegisteredException - if the listener is already registered
   */
  public void registerServiceEventListener(ServiceEventListener listener) throws ListenerAlreadyRegisteredException;

 /**
   * Unregister service event listener
   */
  public void unregisterServiceEventListener();

  /**
   * Register service runtime configuration that provides service runtime modifiable properties update.
   *
   * @see com.sap.engine.frame.container.runtime.RuntimeConfiguration
   *
   * @param runtime - RuntimeConfiguration implementation
   */
  public void registerRuntimeConfiguration(RuntimeConfiguration runtime);

  /**
   * Unregister service runtime configuration
   */
  public void unregisterRuntimeConfiguration();

  /**
   * @deprecated - use PersistentContainer instead
   */
  public String getPersistentDirectoryName();

  /**
   * @deprecated - use registerContainerEventListener(int,java.util.Set,com.sap.engine.frame.container.event.ContainerEventListener)
   */
  public void registerContainerEventListener(ContainerEventListener listener);

}

