package com.sap.engine.core.service630.container;

import com.sap.engine.frame.container.event.AdminContainerEventListener;
import com.sap.engine.frame.container.event.ContainerEventListener;

import java.util.Set;

/**
 * This class extends ContainerEventListenerWrapper.
 *
 * @see com.sap.engine.frame.container.event.AdminContainerEventListener
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class AdminContainerEventListenerWrapper extends ContainerEventListenerWrapper {

  private AdminContainerEventListener listener;

  AdminContainerEventListenerWrapper(AdminContainerEventListener listener, ServiceWrapper service, int mask, Set<String> names) {
    super(listener, service, mask, names);
    this.listener = listener;
  }

  protected void processEvent(ContainerEvent event) {
    super.processEvent(event);
    switch (event.method) {
      case ContainerEventListener.MASK_COMPONENT_REGISTERED: {
        listener.componentRegistered(event.name, event.type);
        if (event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.componentRegistered(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_COMPONENT_RESOLVED: {
        listener.componentResolved(event.name, event.type);
        if (event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.componentResolved(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_COMPONENT_NOT_RESOLVED: {
        listener.componentUnresolved(event.name, event.type);
        if (event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.componentUnresolved(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_COMPONENT_LOADED: {
        listener.componentLoaded(event.name, event.type);
        if (event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.componentLoaded(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_COMPONENT_NOT_LOADED: {
        listener.componentNotLoaded(event.name, event.type);
        if (event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.componentNotLoaded(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_BEGIN_COMPONENT_UNLOAD: {
        listener.beginComponentUnload(event.name, event.type);
        if (event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.beginComponentUnload(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_COMPONENT_UNLOADED: {
        listener.componentUnloaded(event.name, event.type);
        if (event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.componentUnloaded(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_BEGIN_COMPONENT_UNDEPLOY: {
        listener.beginComponentUndeploy(event.name, event.type);
        if(event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.beginComponentUndeploy(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_COMPONENT_UNDEPLOYED: {
        listener.componentUndeployed(event.name, event.type);
        if(event.type == ContainerEventListener.INTERFACE_TYPE && InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.componentUndeployed(InterfaceWrapper.transformINameToINameApi(event.name), event.type);
        }
        break;
      }
      case ContainerEventListener.MASK_MANAGEMENT_INTERFACE_REGISTERED: {
        listener.managementInterfaceRegistered(event.name, event.managementInterface);
        break;
      }
      case ContainerEventListener.MASK_MANAGEMENT_INTERFACE_UNREGISTERED: {
        listener.managementInterfaceUnregistered(event.name);
        break;
      }
    }
  }

  protected boolean isAdmin() {
    return true;
  }

}