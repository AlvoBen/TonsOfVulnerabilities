package com.sap.engine.core.service630.container;

import com.sap.bc.proj.jstartup.sadm.ShmComponent;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.core.service630.context.ApplicationServiceContextImpl;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ProcessEnvironment;
import com.sap.engine.frame.ServiceConfigurationException;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.system.SystemEnvironment;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tools.memory.trace.AllocationStatisticRegistry;

class ServiceRunner implements Runnable {

  private static boolean consecutiveStart = SystemProperties.getBoolean("consecutive");
  private static final Object startLock = new Object();

  private MemoryContainer container;
  private ServiceWrapper service;

  private boolean haltOnFailure = false;

  private static final Category CATEGORY = Category.SYS_SERVER;
  private static final Location LOCATION = Location.getLocation(ServiceRunner.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Location timeStatistics = Location.getLocation("com.sap.engine.core.service630.container.TimeStatistics", Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Location memoryStatistics = Location.getLocation("com.sap.engine.core.service630.container.MemoryStatistics", Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  
  private static final String CLASS_NOT_FOUND_MESSAGE = "Service's implementation class cannot be located";
  private static final String INSTANTIATION_MESSAGE = "Service's implementation class cannot be loaded";
  private static final String ACCESS_MESSAGE = "Service's implementation class is not accessible";
  private static final String SERVICE_ERROR_MESSAGE = "Error in execution of service's start method";

  ServiceRunner(MemoryContainer container, ServiceWrapper service, boolean haltOnFailure) {
    this.container = container;
    this.service = service;
    this.haltOnFailure = haltOnFailure;
    service.setInternalStatus(ServiceWrapper.INTERNAL_STATUS_STARTING);
    //init ShmComponent to starting
    try {
      ShmComponent shmc = ShmComponent.find(service.getComponentName(), ShmComponent.Type.SERVICE);
      shmc.setLocalStatus(ShmComponent.Status.STARTING);
      shmc.setTargetStatus(ShmComponent.Status.STARTED);
    } catch (ShmException e) {
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot set service " + service.getComponentName() + " status to STARTING in ShmComponent", e);
    }
  }

  public void run () {
    boolean success = false;
    try {
      ThreadWrapper.pushTask("Start service [" + service.getComponentName() + "]", ThreadWrapper.TS_PROCESSING);
      startService(false);
      success = true;
    } catch (OutOfMemoryError oom) {
      //$JL-EXC$
      ProcessEnvironment.handleOOM(oom);
    } catch (Throwable throwable) {
      //$JL-EXC$
      handleError(false, throwable);
    } finally {
      ThreadWrapper.popTask();
      //notify the container for this service
      container.serviceReady(service, success);
    }
  }

  public void startSingleService() throws ServiceException {
    try {
      ThreadWrapper.pushTask("Start service [" + service.getComponentName() + "]", ThreadWrapper.TS_PROCESSING);
      startService(true);
    } catch (OutOfMemoryError oom) {
      //$JL-EXC$
      ProcessEnvironment.handleOOM(oom);
    } catch (Throwable throwable) {
      //$JL-EXC$
      handleError(true, throwable);
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

  private void startService(boolean runtime) throws ServiceException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    Thread.currentThread().setContextClassLoader(service.getClassLoader());
    ApplicationServiceFrame frame;
    if (service.getFrameClass() == null) {
      String name = service.getApplicationFrameClassName();
      ClassLoader loader = service.getClassLoader();
      Class frameClass = Class.forName(name, true, loader);
      frame = (ApplicationServiceFrame) frameClass.newInstance();
      service.setFrameClass(frame);
    }
    frame = service.getFrameClass();
    if (consecutiveStart) {
      synchronized (startLock) {
        startFrame(frame);
      }
    } else {
      startFrame(frame);
    }
    if (runtime) {
      container.getServiceContainer().getContainerEventRegistry().generateContainerStarted(service.getComponentName());
    }
    //init ShmComponent to started
    try {
      ShmComponent shmc = ShmComponent.find(service.getComponentName(), ShmComponent.Type.SERVICE);
      shmc.setLocalStatus(ShmComponent.Status.STARTED);
    } catch (ShmException e) {
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot set service " + service.getComponentName() + " status to STARTED in ShmComponent", e);
    }
    synchronized (ContainerEventRegistry.generalEventsLock) {//change status and create event
      service.setStatus(ComponentMonitor.STATUS_ACTIVE);
      service.setInternalStatus(ServiceWrapper.INTERNAL_STATUS_STARTED);
      Object serviceInterface = container.getServiceContainer().getContainerObjectRegistry().getServiceInterface(service.getComponentName());
      ContainerEvent event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_SERVICE_STARTED;
      event.name = service.getComponentName();
      event.object = serviceInterface;
      container.getServiceContainer().getContainerEventRegistry().addContainerEvent(event);
    }
    if (service.getProvidedInterfaces() != null) {
      //check for providing interface registration
      for (String iName : service.getProvidedInterfaces()) {
        if (container.getServiceContainer().getContainerObjectRegistry().getProvidedInterface(iName) == null) {
          if (LOCATION.beWarning()) {
            SimpleLogger.trace(Severity.WARNING, LOCATION, Names.KERNEL_DC_NAME,
                               service.getCSNComponent(), "ASJ.krn_srv.000065",
                               "Service [{0}] declares that provides interface [{1}] but does not register interface implementation within its start method",
                               null, service.getComponentName(), iName);
          }
        }
      }
    }
  }

  private void startFrame(ApplicationServiceFrame frame) throws ServiceException {
    long startTime = System.currentTimeMillis();
    long cpuStartTime = SystemTime.currentCPUTimeUs();
    try {
      if (memoryStatistics.bePath() && AllocationStatisticRegistry.isEnabled()) {
        AllocationStatisticRegistry.setThreadTag("Service " + service.getComponentName() + " start");
      }
      //register listener for properties change
      container.getPropertiesEventHandler().addConfigurationChangedListener(service);
      // start the frame of the service
      frame.start(new ApplicationServiceContextImpl(container.getServiceContainer(), service));
    } finally {
      if (memoryStatistics.bePath() && AllocationStatisticRegistry.isEnabled()) {
        AllocationStatisticRegistry.popThreadTag();
      }
    }
    long cpuEndTime = SystemTime.currentCPUTimeUs();
    long endTime = System.currentTimeMillis();
    long cpu = SystemTime.calculateTimeStampDeltaInMicros(cpuStartTime, cpuEndTime)/1000;
    long time = endTime - startTime;
    SystemEnvironment.STD_OUT.println("  Service [" + service.getComponentName() + "] started. [" + time + "] ms / [" + cpu + "] CPU ms");
    if (LOCATION.bePath()) {
      LOCATION.pathT("Service [" + service.getComponentName() + "] started. [" + time + "] ms / [" + cpu + "] CPU ms");
    }
    if (timeStatistics.bePath()) {
      ServiceContainerImpl.setTimeStatistic("Service " + service.getComponentName() + " start", new long[] {time, cpu});
    }
  }

  private void handleError(boolean runtime, Throwable throwable) {
    SystemEnvironment.STD_ERR.println("  Service [" + service.getComponentName() + "] start ================= ERROR ================= ");
    SystemEnvironment.STD_ERR.println("  CSN Component [" + service.getCSNComponent() + "], DC Name [" + service.getDcName() + "]");
    throwable.printStackTrace(SystemEnvironment.STD_ERR);
    SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, service.getDcName(), service.getCSNComponent(),
            "ASJ.krn_srv.000059", "Service [{0}] failed to start; contact the component owner for resolving the problem",
            service.getComponentName());
    
//    SimpleLogger.trace(Severity.ERROR, LOCATION,
//            service.getDcName(), service.getCSNComponent(), "ASJ.krn_srv.000069",
//            "Service [{0}] failed to start; nested exception is: ",
//            throwable, new Object[]{service.getComponentName()});
    
    if(throwable instanceof ClassNotFoundException){
    	SimpleLogger.trace(Severity.ERROR, LOCATION,
    			service.getDcName(), service.getCSNComponent(), "ASJ.krn_srv.000071",
    			"Service [{0}] failed to start; Reason: [{1}]; nested exception is: ",
    			throwable, new Object[]{service.getComponentName(), CLASS_NOT_FOUND_MESSAGE});
    } else if(throwable instanceof InstantiationException){
        	SimpleLogger.trace(Severity.ERROR, LOCATION,
        			service.getDcName(), service.getCSNComponent(), "ASJ.krn_srv.000071",
        			"Service [{0}] failed to start; Reason: [{1}]; nested exception is: ",
        			throwable, new Object[]{service.getComponentName(), INSTANTIATION_MESSAGE});
    } else if(throwable instanceof IllegalAccessException){
    	SimpleLogger.trace(Severity.ERROR, LOCATION,
    			service.getDcName(), service.getCSNComponent(), "ASJ.krn_srv.000071",
    			"Service [{0}] failed to start; Reason: [{1}]; nested exception is: ",
    			throwable, new Object[]{service.getComponentName(), ACCESS_MESSAGE});
    } else if(throwable instanceof ServiceException){
    	SimpleLogger.trace(Severity.ERROR, LOCATION,
    			service.getDcName(), service.getCSNComponent(), "ASJ.krn_srv.000071",
    			"Service [{0}] failed to start; Reason: [{1}]; nested exception is: ",
    			throwable, new Object[]{service.getComponentName(), SERVICE_ERROR_MESSAGE});
    } else {
    	SimpleLogger.trace(Severity.ERROR, LOCATION,
                service.getDcName(), service.getCSNComponent(), "ASJ.krn_srv.000069",
                "Service [{0}] failed to start; nested exception is: ",
                throwable, new Object[]{service.getComponentName()});
    }
    
    //set ShmComponent status from starting to failed
    try {
      ShmComponent shmc = ShmComponent.find(service.getComponentName(), ShmComponent.Type.SERVICE);
      shmc.setLocalStatus(ShmComponent.Status.FAILED);
    } catch (ShmException e) {
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot set service " + service.getComponentName() + " status to FAILED in ShmComponent", e);
    }
    if (!runtime) {
      if (service.isCore()) {
        if (throwable instanceof ServiceConfigurationException) {
          Framework.criticalShutdown(MemoryContainer.CORE_SERVICE_CONFIGURATION_NOT_VALID, ResourceUtils.formatString(
                  ResourceUtils.CORE_SERVICE_CONFIGURATION_NOT_VALID, new Object[] {service.getComponentName(), throwable.getMessage()}));

        } else {
          Framework.criticalShutdown(MemoryContainer.CORE_SERVICE_FAILED_EXIT_CODE, ResourceUtils.formatString(
                  ResourceUtils.CORE_SERVICE_FAILED, new Object[] {service.getComponentName()}));
        }
      } else if (container.getServiceContainer().getContainerState() == SystemMonitor.STATE_STARTING && haltOnFailure) {
        Framework.criticalShutdown(MemoryContainer.ADDITIONAL_SERVICE_FAILED_EXIT_CODE, ResourceUtils.formatString(
                ResourceUtils.ADDITIONAL_SERVICE_FAILED, new Object[] {service.getComponentName()}));
      }
    }
    synchronized (ContainerEventRegistry.generalEventsLock) {//change status and create event
      service.setInternalStatus(ServiceWrapper.INTERNAL_STATUS_START_FAIL);
      ContainerEvent event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_SERVICE_NOT_STARTED;
      event.name = service.getComponentName();
      container.getServiceContainer().getContainerEventRegistry().addContainerEvent(event);
    }
    if (throwable instanceof ThreadDeath) {
      throw (ThreadDeath) throwable;
    }
  }

}