package com.sap.engine.frame.container.event;

import java.util.Properties;

/**
 * Empty implementation of ContainerEventListener interface. Use this adapter instead implementing the interface.
 *
 * @see com.sap.engine.frame.container.event.ContainerEventListener
 * @author Dimitar Kostadinov
 */
public class ContainerEventListenerAdapter implements ContainerEventListener {

  /**
   * Invoked when the container is started (all services are already started).
   */
  public void containerStarted() {
  }

  /**
   * Invoked before stopping the container.
   */
  public void beginContainerStop() {
  }

  /**
   * Invoked when a service is started.
   *
   * @param serviceName - the name of the started service
   * @param serviceInterface - service interface of the started service or <code>null</code> if not registered
   */
  public void serviceStarted(String serviceName, Object serviceInterface) {
  }

  /**
   * Invoked when a service start failed.
   *
   * @param serviceName - the name of the failed service
   */
  public void serviceNotStarted(String serviceName) {
  }

  /**
   * Invoked before stopping a service.
   *
   * @param serviceName - name of the stopping service
   */
  public void beginServiceStop(String serviceName) {
  }

  /**
   * Invoked when a service is stopped.
   *
   * @param serviceName - name of the stopped service
   */
  public void serviceStopped(String serviceName) {
  }

  /**
   * Invoked when interface is provided.
   *
   * @param interfaceName - interface name
   * @param interfaceImpl - interface implementation
   */
  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
  }

  /**
   * Invoked when interface implementation is going to be removed
   *
   * @param interfaceName - interface name
   */
  public void interfaceNotAvailable(String interfaceName) {
  }

  /**
   * @see com.sap.engine.frame.container.runtime.RuntimeConfiguration
   * @deprecated - use runtime configuration
   */
  public boolean setServiceProperty(String key, String value) {
    return false;
  }

  /**
   * @see com.sap.engine.frame.container.runtime.RuntimeConfiguration
   * @deprecated - use runtime configuration
   */
  public boolean setServiceProperties(Properties serviceProperties) {
    return false;
  }

}