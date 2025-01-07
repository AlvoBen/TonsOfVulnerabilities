package com.sap.engine.core.service630.container;

import com.sap.bc.proj.jstartup.sadm.ShmComponent;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.ProcessEnvironment;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceFrame;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.system.SystemEnvironment;
import com.sap.engine.system.ThreadWrapper;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tools.memory.trace.AllocationStatisticRegistry;

class ServiceStopper implements Runnable {

  private static boolean consecutiveStop= SystemProperties.getBoolean("consecutive");
  private static final Object stopLock = new Object();

  private MemoryContainer container;
  private ServiceWrapper service;

  private static final Category CATEGORY = Category.SYS_SERVER;
  private static final Location LOCATION = Location.getLocation(ServiceStopper.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Location timeStatistics = Location.getLocation("com.sap.engine.core.service630.container.TimeStatistics", Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Location memoryStatistics = Location.getLocation("com.sap.engine.core.service630.container.MemoryStatistics", Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  ServiceStopper(MemoryContainer container, ServiceWrapper service) {
    this.service = service;
    this.container = container;
    service.setInternalStatus(ServiceWrapper.INTERNAL_STATUS_STOPPING);
    //init ShmComponent to stopping
    try {
      ShmComponent shmc = ShmComponent.find(service.getComponentName(), ShmComponent.Type.SERVICE);
      shmc.setLocalStatus(ShmComponent.Status.STOPPING);
      //if the node is shutdown don't change the target status except for the last node in the instance
      if (container.getServiceContainer().getContainerState() != SystemMonitor.STATE_STOPPING || isLastInstranceNode()) {
        shmc.setTargetStatus(ShmComponent.Status.STOPPED);
      }
    } catch (ShmException e) {
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot set service " + service.getComponentName() + " status to STOPPING in ShmComponent", e);
    }
  }

  public void run() {
    try {
      ThreadWrapper.pushTask("Stop service [" + service.getComponentName() + "]", ThreadWrapper.TS_PROCESSING);
      stopService();
    } catch (OutOfMemoryError oom) {
      //$JL-EXC$
      ProcessEnvironment.handleOOM(oom);
    } catch (Throwable throwable) {
      //$JL-EXC$
      handleError(throwable);
    } finally {
      ThreadWrapper.popTask();
      container.serviceStopped(service);
    }
  }

  void stopSingleService() throws ServiceException {
    try {
      ThreadWrapper.pushTask("Stop service [" + service.getComponentName() + "]", ThreadWrapper.TS_PROCESSING);
      stopService();
    } catch (OutOfMemoryError oom) {
      //$JL-EXC$
      ProcessEnvironment.handleOOM(oom);
    } catch (Throwable throwable) {
      //$JL-EXC$
      handleError(throwable);
      if (throwable instanceof ServiceException) {
        //rethrow service exception
        throw (ServiceException) throwable;
      } else {
        //create new service exception
        throw new ServiceException(LOCATION, throwable);
      }
    } finally {
      ThreadWrapper.popTask();
    }
  }

  private void stopService() {
    Thread.currentThread().setContextClassLoader(service.getClassLoader());
    ContainerEvent event = new ContainerEvent();
    event.isBefore = true;
    event.method = ContainerEventListener.MASK_BEGIN_SERVICE_STOP;
    event.name = service.getComponentName();
    container.getServiceContainer().getContainerEventRegistry().addContainerEvent(event);
    ServiceFrame frame = service.getFrameClass();
    try {
      if (consecutiveStop) {
        synchronized (stopLock) {
          stopFrame(frame);
        }
      } else {
        stopFrame(frame);
      }
      service.setInternalStatus(ServiceWrapper.INTERNAL_STATUS_STOPPED);
    } finally {
      //set ShmComponent status from stopping to stopped
      try {
        ShmComponent shmc = ShmComponent.find(service.getComponentName(), ShmComponent.Type.SERVICE);
        shmc.setLocalStatus(ShmComponent.Status.STOPPED);
      } catch (ShmException e) {
        LOCATION.traceThrowableT(Severity.WARNING, "Cannot set service " + service.getComponentName() + " status to STOPPED in ShmComponent", e);
      }
      synchronized (ContainerEventRegistry.generalEventsLock) {
        service.setStatus(ComponentMonitor.STATUS_LOADED);
        event = new ContainerEvent();
        event.method = ContainerEventListener.MASK_SERVICE_STOPPED;
        event.name = service.getComponentName();
        container.getServiceContainer().getContainerEventRegistry().addContainerEvent(event);
      }
    }
  }

  private void stopFrame(ServiceFrame frame) {
    long startTime = System.currentTimeMillis();
    long cpuStartTime = SystemTime.currentCPUTimeUs();
    try {
      if (memoryStatistics.bePath() && AllocationStatisticRegistry.isEnabled()) {
        AllocationStatisticRegistry.setThreadTag("Service " + service.getComponentName() + " stop");
      }
      // unregister listener for properties change
      container.getPropertiesEventHandler().removeConfigurationChangedListener(service);
      // calls the stop method of the service
      frame.stop();
    } finally {
      if (memoryStatistics.bePath() && AllocationStatisticRegistry.isEnabled()) {
        AllocationStatisticRegistry.popThreadTag();
      }
    }
    long cpuEndTime = SystemTime.currentCPUTimeUs();
    long endTime = System.currentTimeMillis();
    long cpu = SystemTime.calculateTimeStampDeltaInMicros(cpuStartTime, cpuEndTime)/1000;
    long time = endTime - startTime;
    SystemEnvironment.STD_OUT.println("  Service [" + service.getComponentName() + "] stopped. [" + time + "] ms / [" + cpu + "] CPU ms");
    if (LOCATION.bePath()) {
      LOCATION.pathT("Service [" + service.getComponentName() + "] stopped. [" + time + "] ms / [" + cpu + "] CPU ms");
    }
    if (timeStatistics.bePath()) {
      ServiceContainerImpl.setTimeStatistic("Service " + service.getComponentName() + " stop", new long[] {time, cpu});
    }
  }

  private void handleError(Throwable throwable) {
    SystemEnvironment.STD_ERR.println("  Service [" + service.getComponentName() + "] stop ================= ERROR ================= ");
    SystemEnvironment.STD_ERR.println("  CSN Component [" + service.getCSNComponent() + "], DC Name [" + service.getDcName() + "]");
    throwable.printStackTrace(SystemEnvironment.STD_ERR);
    SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, service.getDcName(), service.getCSNComponent(),
        "ASJ.krn_srv.000060", "Service [{0}] error [{1}] during stop", service.getComponentName(), throwable.toString());
    SimpleLogger.trace(Severity.ERROR, LOCATION, service.getDcName(), service.getCSNComponent(),
        "ASJ.krn_srv.000066", "Service [{0}] error during stop", throwable, service.getComponentName());
    service.setInternalStatus(ServiceWrapper.INTERNAL_STATUS_STOP_FAIL);
    if (throwable instanceof ThreadDeath) {
      throw (ThreadDeath) throwable;
    }
  }

  private boolean isLastInstranceNode() {
    boolean result = true;
    ClusterManager clusterManager = (ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER);
    int instanceId = clusterManager.getClusterMonitor().getCurrentParticipant().getGroupId();
    //get all cluster participants excluding the current one
    ClusterElement[] elements = clusterManager.getClusterMonitor().getParticipants();
    for (ClusterElement element : elements) {
      if (element.getType() == ClusterElement.SERVER) {
        if (element.getGroupId() == instanceId) {
          result = false;
          break;
        }
      }
    }
    return result;
  }

}