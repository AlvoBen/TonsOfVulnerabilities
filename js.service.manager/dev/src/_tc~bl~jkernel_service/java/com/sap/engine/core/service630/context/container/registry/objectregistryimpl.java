package com.sap.engine.core.service630.context.container.registry;

import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.core.service630.container.ServiceContainerImpl;
import com.sap.engine.core.service630.container.ServiceWrapper;

/**
 * Object registry proxy.
 *
 * @see com.sap.engine.frame.container.registry.ObjectRegistry
 * @see com.sap.engine.core.service630.container.ContainerObjectRegistry
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ObjectRegistryImpl implements ObjectRegistry {

  private ServiceContainerImpl serviceContainer;
  private ServiceWrapper service;

  public ObjectRegistryImpl(ServiceContainerImpl serviceContainer, ServiceWrapper service) {
    this.serviceContainer = serviceContainer;
    this.service = service;
  }

  public void registerInterface(Object serviceInterface) {
    serviceContainer.getContainerObjectRegistry().registerInterface(service.getComponentName(), serviceInterface);
  }

  public void unregisterInterface() {
    serviceContainer.getContainerObjectRegistry().unregisterInterface(service.getComponentName());
  }

  public Object getServiceInterface(String name) {
    return serviceContainer.getContainerObjectRegistry().getServiceInterface(name);
  }

  public void registerInterfaceProvider(String interfaceName, Object interfaceImpl) {
    serviceContainer.getContainerObjectRegistry().registerInterfaceProvider(interfaceName, interfaceImpl, service);
  }

  public void unregisterInterfaceProvider(String interfaceName) {
    serviceContainer.getContainerObjectRegistry().unregisterInterfaceProvider(interfaceName, service);
  }

  public Object getProvidedInterface(String interfaceName) {
    return serviceContainer.getContainerObjectRegistry().getProvidedInterface(interfaceName);
  }

}