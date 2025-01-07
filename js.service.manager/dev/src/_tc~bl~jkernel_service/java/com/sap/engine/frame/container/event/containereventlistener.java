package com.sap.engine.frame.container.event;

import java.util.Properties;

/**
 * Interface for container events notification. The implementation of this interface must be registered in service
 * state context. Use ContainerEventListenerAdapter instead implementing this interface.
 * All begin event (methods starts with "begin") + interfaceNotAvaileble are processed before starting
 * the corresponding operation.
 *
 * @see com.sap.engine.frame.state.ServiceState#registerContainerEventListener(int, java.util.Set, ContainerEventListener)
 * @author Dimitar Kostadinov
 */
public interface ContainerEventListener {

  /**
   * Event regarding component interface
   */
  public static final byte INTERFACE_TYPE = 0;

  /**
   * Event regarding component library
   */
  public static final byte LIBRARY_TYPE = 1;

  /**
   * Event regarding component service
   */
  public static final byte SERVICE_TYPE = 2;

  /**
   * Mask for containerStarted event
   */
  public static int MASK_CONTAINER_STARTED        = 0x00000001;

  /**
   * Mask for beginContainerStop event
   */
  public static int MASK_BEGIN_CONTAINER_STOP     = 0x00000002;

  /**
   * Mask for serviceStarted event
   */
  public static int MASK_SERVICE_STARTED          = 0x00000004;

  /**
   * Mask for serviceNotStarted event
   */
  public static int MASK_SERVICE_NOT_STARTED      = 0x00000008;

  /**
   * Mask for beginServiceStop event
   */
  public static int MASK_BEGIN_SERVICE_STOP       = 0x00000010;

  /**
   * Mask for serviceStopped event
   */
  public static int MASK_SERVICE_STOPPED          = 0x00000020;

  /**
   * Mask for interfaceAvailable event
   */
  public static int MASK_INTERFACE_AVAILABLE      = 0x00000040;

  /**
   * Mask for interfaceNotAvailable event
   */
  public static int MASK_INTERFACE_NOT_AVAILABLE  = 0x00000080;

  //////////////// ADMIN //////////////////////////////////////

  /**
   * Mask for componentRegistered event
   */
  public static int MASK_COMPONENT_REGISTERED     = 0x00000200;

  /**
   * Mask for componentResolved event
   */
  public static int MASK_COMPONENT_RESOLVED       = 0x00000400;

  /**
   * Mask for componentUnresolved event
   */
  public static int MASK_COMPONENT_NOT_RESOLVED   = 0x00000800;

  /**
   * Mask for componentLoaded event
   */
  public static int MASK_COMPONENT_LOADED         = 0x00001000;

  /**
   * Mask for componentNotLoaded event
   */
  public static int MASK_COMPONENT_NOT_LOADED     = 0x00002000;

  /**
   * Mask for beginComponentUnload event
   */
  public static int MASK_BEGIN_COMPONENT_UNLOAD   = 0x00004000;

  /**
   * Mask for componentUnloaded event
   */
  public static int MASK_COMPONENT_UNLOADED       = 0x00008000;

  /**
   * Mask for beginComponentUndeploy event
   */
  public static int MASK_BEGIN_COMPONENT_UNDEPLOY = 0x00010000;

  /**
   * Mask for componentUndeployed event
   */
  public static int MASK_COMPONENT_UNDEPLOYED     = 0x00020000;

  /**
   * Mask for managementInterfaceRegistered event
   */
  public static int MASK_MANAGEMENT_INTERFACE_REGISTERED   = 0x00040000;

  /**
   * Mask for managementInterfaceUnregistered event
   */
  public static int MASK_MANAGEMENT_INTERFACE_UNREGISTERED = 0x00080000;

  /**
   * Invoked when the container is started (all services are already started).
   */
  public void containerStarted();

  /**
   * Invoked before stopping the container.
   */
  public void beginContainerStop();

  /**
   * Invoked when a service is started.
   *
   * @param serviceName - the name of the started service
   * @param serviceInterface - service interface of the started service or <code>null</code> if not registered
   */
  public void serviceStarted(String serviceName, Object serviceInterface);

  /**
   * Invoked when a service start failed.
   *
   * @param serviceName - the name of the failed service
   */
  public void serviceNotStarted(String serviceName);

  /**
   * Invoked before stopping a service.
   *
   * @param serviceName - name of the stopping service
   */
  public void beginServiceStop(String serviceName);

  /**
   * Invoked when a service is stopped.
   *
   * @param serviceName - name of the stopped service
   */
  public void serviceStopped(String serviceName);

  /**
   * Invoked when interface is provided.
   *
   * @param interfaceName - interface name
   * @param interfaceImpl - interface implementation
   */
  public void interfaceAvailable(String interfaceName, Object interfaceImpl);

  /**
   * Invoked when interface implementation is going to be removed
   *
   * @param interfaceName - interface name
   */
  public void interfaceNotAvailable(String interfaceName);

  /**
   * @see com.sap.engine.frame.container.runtime.RuntimeConfiguration
   * @deprecated - use runtime configuration
   */
  public boolean setServiceProperty(String key, String value);

  /**
   * @see com.sap.engine.frame.container.runtime.RuntimeConfiguration
   * @deprecated - use runtime configuration
   */
  public boolean setServiceProperties(Properties serviceProperties);

}

