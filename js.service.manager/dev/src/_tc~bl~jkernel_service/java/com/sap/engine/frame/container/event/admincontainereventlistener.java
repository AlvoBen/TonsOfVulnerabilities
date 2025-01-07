package com.sap.engine.frame.container.event;

import com.sap.engine.frame.state.ManagementInterface;

/**
 * ContainerEventListener extension for admin purposes. Use AdminContainerEventListenerAdapter instead
 * implementing this interface.
 *
 * @see com.sap.engine.frame.container.event.ContainerEventListener
 * @author Dimitar Kostadinov
 */
public interface AdminContainerEventListener extends ContainerEventListener {

  /**
   * Invoked when new component (only runtime library is not deprecated) is deployed
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.LIBRARY_TYPE)
   */
  public void componentRegistered(String componentName, byte componentType);

  /**
   * Invoked when a component is resolved.
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.INTERFACE_TYPE, ContainerEventListener.LIBRARY_TYPE or
   * ContainerEventListener.SERVICE_TYPE)
   */
  public void componentResolved(String componentName, byte componentType);

  /**
   * Invoked when a component is not resolved and there are references to missing components.
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.INTERFACE_TYPE, ContainerEventListener.LIBRARY_TYPE or
   * ContainerEventListener.SERVICE_TYPE)
   */
  public void componentUnresolved(String componentName, byte componentType);

  /**
   * Invoked when a component is loaded.
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.INTERFACE_TYPE, ContainerEventListener.LIBRARY_TYPE or
   * ContainerEventListener.SERVICE_TYPE)
   */
  public void componentLoaded(String componentName, byte componentType);

  /**
   * Invoked when a component loading is failed.
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.INTERFACE_TYPE, ContainerEventListener.LIBRARY_TYPE or
   * ContainerEventListener.SERVICE_TYPE)
   */
  public void componentNotLoaded(String componentName, byte componentType);

  /**
   * Invoked when a component is going to be unload.
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.INTERFACE_TYPE, ContainerEventListener.LIBRARY_TYPE or
   * ContainerEventListener.SERVICE_TYPE)
   */
  public void beginComponentUnload(String componentName, byte componentType);

  /**
   * Invoked when a component is unloaded.
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.INTERFACE_TYPE, ContainerEventListener.LIBRARY_TYPE or
   * ContainerEventListener.SERVICE_TYPE)
   */
  public void componentUnloaded(String componentName, byte componentType);

  /**
   * Invoked when a component is going to be removed. Only libraries can be undeployed runtime.
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.LIBRARY_TYPE)
   */
  public void beginComponentUndeploy(String componentName, byte componentType);

  /**
   * Invoked when a component is removed. Only libraries can be undeployed runtime.
   *
   * @param componentName the name of the component
   * @param componentType component type (ContainerEventListener.LIBRARY_TYPE)
   */
  public void componentUndeployed(String componentName, byte componentType);

  /**
   * Invoked when service register its management interface.
   *
   * @param serviceName name of the service
   * @param managementInterface interface implementation
   */
  public void managementInterfaceRegistered(String serviceName, ManagementInterface managementInterface);

  /**
   * Invoked when service unregistered its management interface.
   *
   * @param serviceName name of the service
   */
  public void managementInterfaceUnregistered(String serviceName);

}

