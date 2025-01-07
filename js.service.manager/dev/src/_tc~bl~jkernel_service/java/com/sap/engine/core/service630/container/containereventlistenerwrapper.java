package com.sap.engine.core.service630.container;

import com.sap.engine.core.thread.ThreadManager;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.*;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.lib.util.Queue;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tools.memory.trace.AllocationStatisticRegistry;

import java.util.Properties;
import java.util.Set;

/**
 * This class represents a ContainerEventListener. It has provides event filtering and event queue.
 * All events that are relevant for the listener are added in the queue and are executed consecutively.
 *
 * @see com.sap.engine.frame.container.event.ContainerEventListener
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ContainerEventListenerWrapper implements Runnable {

  //wrapped listener
  private ContainerEventListener listener;

  private ThreadManager threadManager;
  private ServiceWrapper service;

  //event queue
  private Queue queue;
  //if true runnting work thread exist
  private boolean working;
  //method & name filter
  private int mask;
  private Set<String> names;

  private static final Location location = Location.getLocation(ContainerEventListenerWrapper.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Location timeStatistics = Location.getLocation("com.sap.engine.core.service630.container.TimeStatistics", Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Location memoryStatistics = Location.getLocation("com.sap.engine.core.service630.container.MemoryStatistics", Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  ContainerEventListenerWrapper(ContainerEventListener listener, ServiceWrapper service, int mask, Set<String> names) {
    this.listener = listener;
    this.service = service;
    this.mask = mask;
    this.names = names;
    this.queue = new Queue();
    this.threadManager = (ThreadManager) Framework.getManager(Names.THREAD_MANAGER);
    this.working = false;
  }

  //event filter
  boolean acceptEvent(ContainerEvent event) {
    boolean result = false;
    if (event.method != ContainerEvent.CONTAINER_SYNCHRONIZATION) { //internal event is always accepted
      if ((mask & event.method) != 0) {
        if (event.name == null || names == null) {
          result = true;
        } else {
          result = names.contains(event.name) || names.contains(InterfaceWrapper.transformINameToINameApi(event.name));
        }
      }
    } else {
      result = true;
    }
    return result;
  }

  //add event in queue and start a thread if needed
  synchronized void addEvent(ContainerEvent event) {
    if (queue.isEmpty() && !working) {
      threadManager.startThread(this, null, "Event Processor [" + service.getComponentName() + "]", true);
      working = true;
    }
    if (event.isBefore) {
      event.startProcess(service.getComponentName(), service.getCSNComponent());
    }
    queue.enqueue(event);
    if (location.beDebug()) {
      location.debugT("Event " + ContainerEventListenerWrapper.getMethod(event) + " added in service " + service.getComponentName() + " event queue.");
    }
  }

  //return event from event queue or null if queue is empty
  private synchronized ContainerEvent getEvent() {
    if (queue.isEmpty()) {
      working = false;
      return null;
    }
    return (ContainerEvent) queue.dequeue();
  }

  //run logic
  public void run() {
    ContainerEvent event;
    while ((event = getEvent()) != null) {
      try {
        ThreadWrapper.pushTask("Event [" + getMethod(event) + "]", ThreadWrapper.TS_PROCESSING);
        //execute event with service context class loader
        Thread.currentThread().setContextClassLoader(service.getClassLoader());
        long startTime = System.currentTimeMillis();
        long cpuStartTime = SystemTime.currentCPUTimeUs();
        try {
          if (memoryStatistics.bePath() && AllocationStatisticRegistry.isEnabled()) {
            AllocationStatisticRegistry.setThreadTag("Service " + service.getComponentName() + " process event " + getMethod(event));
          }
          processEvent(event);
        } finally {
          if (memoryStatistics.bePath() && AllocationStatisticRegistry.isEnabled()) {
            AllocationStatisticRegistry.popThreadTag();
          }
        }
        long cpuEndTime = SystemTime.currentCPUTimeUs();
        long endTime = System.currentTimeMillis();
        long cpu = SystemTime.calculateTimeStampDeltaInMicros(cpuStartTime, cpuEndTime)/1000;
        long time = endTime - startTime;
        if (location.beDebug()) {
          location.debugT("Service " + service.getComponentName() + " process event " + getMethod(event) + ". (" + time + " ms / " + cpu + " CPU ms)");
        }
        if (timeStatistics.bePath()) {
          ServiceContainerImpl.setTimeStatistic("Service " + service.getComponentName() + " process event " + getMethod(event), new long[] {time, cpu});
        }
      } catch (OutOfMemoryError outOfMemoryError) {
        //$JL-EXC$
        ProcessEnvironment.handleOOM(outOfMemoryError);
      } catch (ThreadDeath threadDeath) {
        //$JL-EXC$
        location.throwing(threadDeath);
        throw threadDeath;
      } catch (Throwable throwable) {
        //$JL-EXC$
        SimpleLogger.trace(Severity.WARNING, location,
                           service.getDcName(), service.getCSNComponent(), "ASJ.krn_srv.000063",
                           "Error [{0}] occurred while service [{1}] processed [{2}] event",
                           throwable);
      } finally {
        ThreadWrapper.popTask();
        if (event.isBefore) {
          event.finishProcess(service.getComponentName());
        }
      }
    }
  }

  //invoke listener method
  protected void processEvent(ContainerEvent event) {
    switch (event.method) {
      case ContainerEventListener.MASK_CONTAINER_STARTED : {
        listener.containerStarted();
        break;
      }
      case ContainerEventListener.MASK_BEGIN_CONTAINER_STOP : {
        listener.beginContainerStop();
        break;
      }
      case ContainerEventListener.MASK_SERVICE_STARTED : {
        listener.serviceStarted(event.name, event.object);
        break;
      }
      case ContainerEventListener.MASK_SERVICE_NOT_STARTED : {
        listener.serviceNotStarted(event.name);
        break;
      }
      case ContainerEventListener.MASK_BEGIN_SERVICE_STOP : {
        listener.beginServiceStop(event.name);
        break;
      }
      case ContainerEventListener.MASK_SERVICE_STOPPED : {
        listener.serviceStopped(event.name);
        break;
      }
      case ContainerEventListener.MASK_INTERFACE_AVAILABLE : {
        listener.interfaceAvailable(event.name, event.object);
        if (InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.interfaceAvailable(InterfaceWrapper.transformINameToINameApi(event.name), event.object);
        }
        break;
      }
      case ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE : {
        listener.interfaceNotAvailable(event.name);
        if(InterfaceWrapper.existInInterfaceApiList(event.name)) {
          listener.interfaceNotAvailable(InterfaceWrapper.transformINameToINameApi(event.name));
        }
        break;
      }
    }
  }

  boolean setServiceProperty(String key, String value) {
    if (location.beDebug()) {
      location.debugT("Invoke setServiceProperty: key = " + key + ", value = " + value + "; for service " + service.getComponentName());
    }
    return listener.setServiceProperty(key, value);
  }

  boolean setServiceProperties(Properties serviceProperties) {
    if (location.beDebug()) {
      location.debugT("Invoke setServiceProperties: properties = " + serviceProperties + "; for service " + service.getComponentName());
    }
    return listener.setServiceProperties(serviceProperties);
  }

  protected boolean isAdmin() {
    return false;
  }

  Set<String> getNames() {
    return names;
  }

  //get event method as String
  public static String getMethod(ContainerEvent event) {
    switch (event.method) {
      case ContainerEventListener.MASK_CONTAINER_STARTED : {
        return "containerStarted()";
      }
      case ContainerEventListener.MASK_BEGIN_CONTAINER_STOP : {
        return "beginContainerStop()";
      }
      case ContainerEventListener.MASK_SERVICE_STARTED : {
        return "serviceStarted(" + event.name + ", " + event.object + ")";
      }
      case ContainerEventListener.MASK_SERVICE_NOT_STARTED : {
        return "serviceNotStarted(" + event.name + ")";
      }
      case ContainerEventListener.MASK_BEGIN_SERVICE_STOP : {
        return "beginServiceStop(" + event.name + ")";
      }
      case ContainerEventListener.MASK_SERVICE_STOPPED : {
        return "serviceStopped(" + event.name + ")";
      }
      case ContainerEventListener.MASK_INTERFACE_AVAILABLE : {
        return "interfaceAvailable(" + event.name + ", " + event.object + ")";
      }
      case ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE : {
        return "interfaceNotAvailable(" + event.name + ")";
      }
      case ContainerEventListener.MASK_COMPONENT_REGISTERED : {
        return "componentRegistered(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_COMPONENT_RESOLVED : {
        return "componentResolved(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_COMPONENT_NOT_RESOLVED : {
        return "componentUnresolved(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_COMPONENT_LOADED : {
        return "componentLoaded(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_COMPONENT_NOT_LOADED : {
        return "componentNotLoaded(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_BEGIN_COMPONENT_UNLOAD : {
        return "beginComponentUnload(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_COMPONENT_UNLOADED : {
        return "componentUnloaded(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_BEGIN_COMPONENT_UNDEPLOY : {
        return "beginComponentUndeploy(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_COMPONENT_UNDEPLOYED : {
        return "componentUndeployed(" + event.name + ", " + event.type + ")";
      }
      case ContainerEventListener.MASK_MANAGEMENT_INTERFACE_REGISTERED : {
        return "managementInterfaceRegistered(" + event.name + ", " + event.managementInterface + ")";
      }
      case ContainerEventListener.MASK_MANAGEMENT_INTERFACE_UNREGISTERED : {
        return "managementInterfaceUnregistered(" + event.name + ")";
      }
      case ContainerEvent.CONTAINER_SYNCHRONIZATION : {
        return "internalSynchronizer()";
      }
      default: {
        return "";
      }
    }
  }

}