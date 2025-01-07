package com.sap.engine.core.service630.container;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

import com.sap.engine.core.Names;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.frame.container.event.AdminContainerEventListener;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.Reference;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This singleton class represents a container event registry. All events are register and distributes to registered
 * container event listeners
 *
 * @see com.sap.engine.frame.container.event.ContainerEventListener
 * @see com.sap.engine.core.service630.context.state.ServiceStateImpl
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ContainerEventRegistry {

  //general event lock (for registration & initial event generation)
  //used for setting component statuses (add remove interface impl in object registry
  //and start or stop service)
  public static final Object generalEventsLock = new Object();

  private ServiceContainerImpl serviceContainer;

  //holds container event listeners, package access to for setProperties
  HashMap<String, ContainerEventListenerWrapper> containerEvents;
  //timeout for before events
  private long beforeTimeout;

  //use for initial event generation
  private Set<String> serviceReferencesNameSet;
  //use for initial event generation
  private Set<String> interfaceReferencesNameSet;

  private static final Category CATEGORY = Category.SYS_SERVER;
  private static final Location LOCATION = Location.getLocation(ContainerEventRegistry.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  public ContainerEventRegistry(ServiceContainerImpl serviceContainer) {
    this.serviceContainer = serviceContainer;
    containerEvents = new HashMap<String, ContainerEventListenerWrapper>();
    try {
      beforeTimeout = Integer.parseInt(serviceContainer.getCurrentProperties().getProperty("EventTimeout", "10"));
    } catch (NumberFormatException e) {
      beforeTimeout = 10;
      if (SimpleLogger.isWritable(Severity.WARNING, CATEGORY)) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION,
            "ASJ.krn_srv.000001",
            "Error parsing property [EventTimeout], will use the value [10]");
      }
      if(SimpleLogger.isWritable(Severity.WARNING, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.WARNING, LOCATION,
            "Error parsing property [EventTimeout], will use the value [10]",
            e);
      }
    }
    beforeTimeout = beforeTimeout * 1000;
    serviceReferencesNameSet = new HashSet<String>();
    interfaceReferencesNameSet = new HashSet<String>();
  }

  //register listener
  public void registerContainerEventListener(ServiceWrapper service, ContainerEventListener eventListener, int mask, Set<String> names) {
    String serviceName = service.getComponentName();
    synchronized (generalEventsLock) {
      if (containerEvents.containsKey(serviceName)) {
        //log warning if listener already exist
        if (LOCATION.beWarning()) {
          LOCATION.warningT(ResourceUtils.formatString(ResourceUtils.CONTAINER_LISTENER_ALREADY_REGISTERED,
                  new Object[] {serviceName, eventListener}));
        }
      }
      ContainerEventListenerWrapper listenerWrapper;
      if (eventListener instanceof AdminContainerEventListener) {
        listenerWrapper = new AdminContainerEventListenerWrapper((AdminContainerEventListener) eventListener, service, mask, names);
      } else {
        listenerWrapper = new ContainerEventListenerWrapper(eventListener, service, mask, names);
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Service " + serviceName + " register container event listener " + eventListener + ", mask = " + mask + ", names = " + names + ".");
      }
      containerEvents.put(serviceName, listenerWrapper);
      generateInitialEvents(service, listenerWrapper);
    }
  }

  //unregister listener
  public void unregisterContainerEventListener(ServiceWrapper service) {
    String serviceName = service.getComponentName();
    synchronized (generalEventsLock) {
      if (!containerEvents.containsKey(serviceName)) {
        //log warning - attempt to unregister listener that is not registered!
        if (LOCATION.beWarning()) {
          LOCATION.warningT(ResourceUtils.formatString(ResourceUtils.LISTENER_DOESNT_EXIST, new Object[] {serviceName}));
        }
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Service " + serviceName + " unregister container event listener.");
      }
      containerEvents.remove(serviceName);
    }
  }

  //add event
  public void addContainerEvent(ContainerEvent event) {
    synchronized (generalEventsLock) {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Event " + ContainerEventListenerWrapper.getMethod(event) + " added.");
      }
      Collection<ContainerEventListenerWrapper> listeners = containerEvents.values();
      event.maxProcessorsCount = listeners.size();
      for (ContainerEventListenerWrapper listener : listeners) {
        if (listener.acceptEvent(event)) {
          if (event.isAdmin) {
            if (listener.isAdmin()) {
              listener.addEvent(event);
            }
          } else {
            listener.addEvent(event);
          }
        }
      }
    }
    //before events synchronization
    if (event.isBefore) {
      event.sendFinished(beforeTimeout);
    }
  }

  /**
   * This method generate a list of initial events that component interest to. This logic is
   * deprecated because contradicts with events concept, but all services rely on that at the moment.
   * The events that are generated are with masks MASK_SERVICE_STARTED, MASK_SERVICE_NOT_STARTED,
   * MASK_INTERFACE_AVAILABLE. All component statuses relative to this masks must be set
   * in synchronized block to avoid receiving of a event twice.
   * @deprecated
   */
  private void generateInitialEvents(ServiceWrapper service, ContainerEventListenerWrapper listenerWrapper) {
    //clear name sets - this sets are used for prevent generation of a event twice
    serviceReferencesNameSet.clear();
    interfaceReferencesNameSet.clear();
    //generate events based on references
    for (ReferenceImpl reference : service.getReferenceSet()) {
      if (reference.getReferentType() == Reference.REFER_SERVICE) {
        serviceReferencesNameSet.add(reference.getName());
        addServiceRelatedEvent(listenerWrapper, (ServiceWrapper) reference.getReferencedComponent(), service.getComponentName());
      } else if (reference.getReferentType() == Reference.REFER_INTERFACE) {
        interfaceReferencesNameSet.add(reference.getName());
        addInterfaceRelatedEvent(listenerWrapper, reference.getName(), service.getComponentName());
      }
    }
    //generate events based on names set
    if (listenerWrapper.getNames() != null) {
      for (String name : listenerWrapper.getNames()) {
        //try to find service:
        if (!serviceReferencesNameSet.contains(name)) {
          ServiceWrapper interestService = serviceContainer.getMemoryContainer().getServices().get(name);
          if (interestService != null) {
            addServiceRelatedEvent(listenerWrapper, interestService, service.getComponentName());
          }
        }
        //try to find interface:
        if (!interfaceReferencesNameSet.contains(name)) {
          InterfaceWrapper interestInterface = serviceContainer.getMemoryContainer().getInterfaces().get(name);
          if (interestInterface != null) {
            addInterfaceRelatedEvent(listenerWrapper, name, service.getComponentName());
          }
        }
      }
    }
  }

  private void addServiceRelatedEvent(ContainerEventListenerWrapper listenerWrapper, ServiceWrapper service, String forService) {
    ContainerEvent event;
    if (service.getStatus() == ComponentMonitor.STATUS_ACTIVE) {
      Object serviceInterface = serviceContainer.getContainerObjectRegistry().getServiceInterface(service.getComponentName());
      event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_SERVICE_STARTED;
      event.name = service.getComponentName();
      event.object = serviceInterface;
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Generate initial event " + ContainerEventListenerWrapper.getMethod(event) + " for service " + forService);
      }
      listenerWrapper.addEvent(event);
    } else if (service.getInternalStatus() == ServiceWrapper.INTERNAL_STATUS_START_FAIL) {
      event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_SERVICE_NOT_STARTED;
      event.name = service.getComponentName();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Generate initial event " + ContainerEventListenerWrapper.getMethod(event) + " for service " + forService);
      }
      listenerWrapper.addEvent(event);
    }
  }

  private void addInterfaceRelatedEvent(ContainerEventListenerWrapper listenerWrapper, String interfaceName, String forService) {
    ContainerEvent event;
    Object interfaceImpl = serviceContainer.getContainerObjectRegistry().getProvidedInterface(interfaceName);
    if (interfaceImpl != null) {
      interfaceImpl = (interfaceImpl.equals(ContainerObjectRegistry.EMPTY_INTERFACE)) ? null : interfaceImpl;
      event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_INTERFACE_AVAILABLE;
      event.name = interfaceName;
      event.object = interfaceImpl;
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Generate initial event " + ContainerEventListenerWrapper.getMethod(event) + " for service " + forService);
      }
      listenerWrapper.addEvent(event);
    }
  }

  /**
   * This method is invoked after the service container is started and fire container started event for a service
   * when service is starting runtime
   * @deprecated
   */
  void generateContainerStarted(String serviceName) {
    ContainerEvent event;
    ContainerEventListenerWrapper listenerWrapper = containerEvents.get(serviceName);
    if (listenerWrapper != null) {
      event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_CONTAINER_STARTED;
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Generate initial event " + ContainerEventListenerWrapper.getMethod(event) + " for service " + serviceName);
      }
      listenerWrapper.addEvent(event);
    }
  }

}