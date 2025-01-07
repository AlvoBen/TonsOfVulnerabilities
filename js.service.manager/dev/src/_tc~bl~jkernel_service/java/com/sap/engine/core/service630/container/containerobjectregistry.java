package com.sap.engine.core.service630.container;

import com.sap.engine.core.Names;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.tc.logging.Location;
import com.sap.localization.LocalizableTextFormatter;

import java.util.Hashtable;

/**
 * This singleton class represents a object registry and holds service interfaces and provided interface implementations
 *
 * @see com.sap.engine.frame.container.registry.ObjectRegistry
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ContainerObjectRegistry {

  //holds service interfaces
  private Hashtable<String, Object> serviceObjectRegistry;
  //holds interface implementations
  private Hashtable<String, Object> interfaceObjectRegistry;
  //use for empty interfaces
  final static Object EMPTY_INTERFACE = new Object();

  private ServiceContainerImpl container;

  private static final Location location = Location.getLocation(ContainerObjectRegistry.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  ContainerObjectRegistry(ServiceContainerImpl container) {
    this.container = container;
    serviceObjectRegistry = new Hashtable<String, Object>();
    interfaceObjectRegistry = new Hashtable<String, Object>();
  }

  public void registerInterface(String serviceName, Object serviceInterface) {
    if (serviceObjectRegistry.containsKey(serviceName)) {
      if (location.beWarning()) {
        location.warningT(ResourceUtils.formatString(ResourceUtils.SERVICE_INTERFACE_IS_ALREADY_REGISTERED, new Object[] {serviceName}));
      }
    }
    if (location.beDebug()) {
      location.debugT("Register service interface " + serviceInterface + " for service " + serviceName);
    }
    serviceObjectRegistry.put(serviceName, serviceInterface);
  }

  public void unregisterInterface(String serviceName) {
    if (serviceObjectRegistry.containsKey(serviceName)) {
      if (location.beDebug()) {
        location.debugT("Unregister service interface for service " + serviceName);
      }
      serviceObjectRegistry.remove(serviceName);
    } else {
      if (location.beWarning()) {
        location.warningT(ResourceUtils.formatString(ResourceUtils.SERVICE_INTERFACE_NOT_REGISTERED, new Object[] {serviceName}));
      }
    }
  }

  public Object getServiceInterface(String serviceName) {
    Object result = serviceObjectRegistry.get(serviceName);
    if (location.beDebug()) {
      location.debugT("Return service interface " + result + " for service " + serviceName);
    }
    return result;
  }

  /////////////////////////////////////////// INTERFACES ///////////////////////////////////////////////////////////////

  public void registerInterfaceProvider(String interfaceName, Object interfaceImpl, ServiceWrapper provider) {
    interfaceName = InterfaceWrapper.transformINameApiToIName(interfaceName);
    if (interfaceObjectRegistry.contains(interfaceName)) {
      if (location.beWarning()) {
        location.warningT(ResourceUtils.formatString(ResourceUtils.INTERFACE_IS_ALREADY_REGISTERED_IN_REGISTRY,
                new Object[] {interfaceName, provider.getComponentName()}));
      }
    }
    InterfaceWrapper interfaceWrapper = container.getMemoryContainer().getInterfaces().get(interfaceName);
    if (interfaceWrapper != null) {
      if (provider.getComponentName().equals(interfaceWrapper.getProvidingServiceName())) {
        synchronized (ContainerEventRegistry.generalEventsLock) {
          if (interfaceImpl == null) {
            interfaceImpl = EMPTY_INTERFACE;
          }
          //the registration in HT should be the first step!
          if (location.beDebug()) {
            location.debugT("Register interface " + interfaceName + " implementation " + interfaceImpl + " from service " + provider.getComponentName());
          }
          interfaceObjectRegistry.put(interfaceName, interfaceImpl);
          ContainerEvent event = new ContainerEvent();
          event.method = ContainerEventListener.MASK_INTERFACE_AVAILABLE;
          event.name = interfaceName;
          event.object = interfaceImpl;
          container.getContainerEventRegistry().addContainerEvent(event);
        }
      } else {
        if (location.beWarning()) {
          location.warningT(ResourceUtils.formatString(ResourceUtils.INTERFACE_NOT_DECLARED,
                  new Object[] {interfaceName, provider.getComponentName()}));
        }
      }
    } else {
      throw new ServiceRuntimeException(location, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.INTERFACE_NOT_REGISTERED_AS_COMPONENT), new Object[] {interfaceName, provider.getComponentName()}));
    }
  }

  public void unregisterInterfaceProvider(String interfaceName, ServiceWrapper provider) {
    interfaceName = InterfaceWrapper.transformINameApiToIName(interfaceName);
    if (interfaceObjectRegistry.containsKey(interfaceName)) {
      InterfaceWrapper interfaceWrapper = container.getMemoryContainer().getInterfaces().get(interfaceName);
      if (interfaceWrapper != null) {
        if (provider.getComponentName().equals(interfaceWrapper.getProvidingServiceName())) {
          ContainerEvent event = new ContainerEvent();
          event.isBefore = true;
          event.method = ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE;
          event.name = interfaceName;
          container.getContainerEventRegistry().addContainerEvent(event);
          if (location.beDebug()) {
            location.debugT("Unregister interface " + interfaceName + " from service " + provider.getComponentName());
          }
          synchronized (ContainerEventRegistry.generalEventsLock) {//use synch block for initial events consistency
            //todo: if automatic cleanup is implemented it's possible to generate fake initial interface_available event!
            interfaceObjectRegistry.remove(interfaceName);
          }
        } else {
          if (location.beWarning()) {
            location.warningT(ResourceUtils.formatString(ResourceUtils.INTERFACE_NOT_DECLARED,
                    new Object[] {interfaceName, provider.getComponentName()}));
          }
        }
      } else {
        throw new ServiceRuntimeException(location, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                ResourceUtils.getKey(ResourceUtils.INTERFACE_NOT_REGISTERED_AS_COMPONENT), new Object[] {interfaceName, provider.getComponentName()}));
      }
    } else {
      if (location.beWarning()) {
        location.warningT(ResourceUtils.formatString(ResourceUtils.INTERFACE_NOT_REGISTERED_IN_REGISTRY,
                new Object[] {interfaceName, provider.getComponentName()}));
      }
    }
  }

  public Object getProvidedInterface(String interfaceName) {
    interfaceName = InterfaceWrapper.transformINameApiToIName(interfaceName);
    Object result = interfaceObjectRegistry.get(interfaceName);
    if (location.beDebug()) {
      location.debugT("Return interface implementation " + result + " for interface " + interfaceName);
    }
    return result;
  }

}