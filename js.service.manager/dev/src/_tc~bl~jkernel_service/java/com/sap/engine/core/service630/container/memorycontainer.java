package com.sap.engine.core.service630.container;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.sap.bc.proj.jstartup.sadm.ShmComponent;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.classload.ClassLoaderManager;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.core.thread.ThreadManager;
import com.sap.engine.frame.ProcessEnvironment;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.ServiceNotLoadedException;
import com.sap.engine.frame.container.deploy.ComponentNotDeployedException;
import com.sap.engine.frame.container.deploy.zdm.InstanceDescriptor;
import com.sap.engine.frame.container.deploy.zdm.RollingComponent;
import com.sap.engine.frame.container.deploy.zdm.RollingName;
import com.sap.engine.frame.container.deploy.zdm.RollingResult;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.Reference;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.system.SystemEnvironment;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Class managing runtime operations (start/stop/deploy/undeploy) in service container. This class is singleton.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class MemoryContainer {

  //core service list
  static final String[] CORE_SERVICES = new String[] {"telnet", "p4", "security", "tc~bl~deploy_controller", "adminadapter"};

  //exit codes
  static final int CORE_SERVICE_FAILED_EXIT_CODE       =  2150;
  static final int ADDITIONAL_SERVICE_FAILED_EXIT_CODE =  2151;
  static final int STARTING_SERVICE_TIMEOUT_EXIT_CODE  =  2152;
  //exit codes preventing restart
  static final int CLASSLOAD_CYCLE_DETECTED_EXIT_CODE  = -2100;
  static final int CANT_GET_MANAGERS_EXIT_CODE         = -2101;
  static final int ERROR_READING_FILTERS_EXIT_CODE     = -2102;
  static final int FILTERS_EMPTY_EXIT_CODE             = -2103;
  static final int HARD_REF_CYCLE_DETECTED_EXIT_CODE   = -2104;
  static final int CORE_COMPONENTS_MISSING_OR_NOT_LOADED_EXIT_CODE = -2105;
  static final int CORE_SERVICE_CONFIGURATION_NOT_VALID = -2106;

  //timeout actions
  private static final byte SERVICE_TIMEOUT_ACTION_WAIT = 0;
  private static final byte SERVICE_TIMEOUT_ACTION_HALT = 1;
  private static final byte SERVICE_TIMEOUT_ACTION_IGNORE = 2;
  //thread dump interval
  private static final long THREAD_DUMP_INTERVAL = 5 * 60 * 1000;
  //global lock for all runtime operations
  private static final Object runtimeOperationLock = new Object();
  //lock for lazy initialization of lock owner name
  private static final Object initLock = new Object();
  //use for start/stop set of services
  private static final Object operateSynchronizer = new Object();

  private ServiceContainerImpl serviceContainer;

  private ThreadManager threadManager;
  private LockingContext lockingManager;

  //components
  private Hashtable<String, InterfaceWrapper> interfaces;
  private Hashtable<String, LibraryWrapper> libraries;
  private Hashtable<String, ServiceWrapper> services;

  //use for read/write from DB
  private PersistentContainer persistentContainer;
  //use to resolve the references
  private ReferenceResolver referenceResolver;
  //create components loader
  private LoadContainer loadContainer;
  //use for coordination
  private OperationDistributor distributor;
  //use to handle properties changed events
  private PropertiesEventHandler propertiesEventHandler;

  //if true the engine will exit if additional service start failed
  private boolean haltOnAdditionalServiceFailure = false;
  //if true the engine will exit if classload cycle exists
  private boolean haltOnClassloadCycle = false;
  //current timeout action
  private byte serviceTimeoutAction;

  //core service set include CORE_SERVICES + all hard references
  private Set<ServiceWrapper> coreServiceSet;
  //current start service set according applyed filters
  private Set<ServiceWrapper> startServiceSet;
  //holds temporary set of service to be started or stopped
  private Set<ServiceWrapper> operateServicesSet;

  //timeout for services start
  private long serviceStartTimeout;
  //timeout for services stop
  private long serviceStopTimeout;

  //name of the lock owner
  private String lockOwnerName;

  private static final Category CATEGORY = Category.SYS_SERVER;
  /** Category used for logging critical engine messages for AGS. */
  private static final Category CATEGORY_SERVER_CRITICAL = Category.
    getCategory(Category.SYS_SERVER, "Critical");
  private static final Location LOCATION = Location.getLocation(MemoryContainer.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  MemoryContainer(ServiceContainerImpl serviceContainer) {
    this.serviceContainer = serviceContainer;
    threadManager = (ThreadManager) Framework.getManager(Names.THREAD_MANAGER);
    lockingManager = (LockingContext) Framework.getManager(Names.LOCKING_MANAGER);
    persistentContainer = new PersistentContainer(this);
    //init properties
    try {
      serviceStartTimeout = Integer.parseInt(serviceContainer.getCurrentProperties().getProperty("LoadTimeout", "2"));
    } catch (NumberFormatException e) {
      serviceStartTimeout = 2;
      if (SimpleLogger.isWritable(Severity.WARNING, CATEGORY)) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION,
            "ASJ.krn_srv.000009",
            "Error parsing property [LoadTimeout], will use the value [2]");
      }
      if(SimpleLogger.isWritable(Severity.WARNING, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.WARNING, LOCATION,
            "Error parsing property [LoadTimeout], will use the value [2]", e);
      }
    }
    serviceStartTimeout = (serviceStartTimeout <= 0) ? 2 * 60 * 1000 : serviceStartTimeout * 60 * 1000;
    try {
      serviceStopTimeout = Integer.parseInt(serviceContainer.getCurrentProperties().getProperty("ServiceStopTimeout", "20"));
    } catch (NumberFormatException e) {
      serviceStopTimeout = 20;
      if (SimpleLogger.isWritable(Severity.WARNING, CATEGORY)) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION,
            "ASJ.krn_srv.000010",
            "Error parsing property [ServiceStopTimeout], will use the value [20]");
      }
      if (SimpleLogger.isWritable(Severity.WARNING, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.WARNING, LOCATION,
            "Error parsing property [ServiceStopTimeout], will use the value [20]",
            e);
      }
    }
    serviceStopTimeout = (serviceStopTimeout < 0) ? 20 * 1000 : serviceStopTimeout * 1000;
    haltOnAdditionalServiceFailure = serviceContainer.getCurrentProperties().getProperty("HaltOnAdditionalServiceFailure", "false").equalsIgnoreCase("true");
    haltOnClassloadCycle = serviceContainer.getCurrentProperties().getProperty("HaltOnClassloaderCycles", "false").equalsIgnoreCase("true");
    String serviceTimeout = serviceContainer.getCurrentProperties().getProperty("ServiceTimeoutAction", "wait");
    if (serviceTimeout.equalsIgnoreCase("wait")) {
      serviceTimeoutAction = SERVICE_TIMEOUT_ACTION_WAIT;
    } else if (serviceTimeout.equalsIgnoreCase("halt")) {
      serviceTimeoutAction = SERVICE_TIMEOUT_ACTION_HALT;
    } else if (serviceTimeout.equalsIgnoreCase("ignore")) {
      serviceTimeoutAction = SERVICE_TIMEOUT_ACTION_IGNORE;
    } else {
      serviceTimeoutAction = SERVICE_TIMEOUT_ACTION_WAIT;
      if (SimpleLogger.isWritable(Severity.WARNING, CATEGORY)) {
        SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION,
            "ASJ.krn_srv.000011",
            "Error parsing property [ServiceTimeoutAction], will use the value [wait]");
      }
    }
  }

  /////////////////////////////////////// INTERNAL METHODS /////////////////////////////////////////////////////////////

  public PersistentContainer getPersistentContainer() {
    return persistentContainer;
  }

  Hashtable<String, InterfaceWrapper> getInterfaces() {
    return interfaces;
  }

  Hashtable<String, LibraryWrapper> getLibraries() {
    return libraries;
  }

  Hashtable<String, ServiceWrapper> getServices() {
    return services;
  }

  Set getCoreServiceSet() {
    return coreServiceSet;
  }

  Set getStartServiceSet() {
    return startServiceSet;
  }

  ServiceContainerImpl getServiceContainer() {
    return serviceContainer;
  }

  OperationDistributor getOperationDistributor() {
    return distributor;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

 /**
  * This method calsulate startup service set and start services in topologic order towards services hard
  * references - used for initial services start
  */
  boolean start()  {
    synchronized (runtimeOperationLock) {
      distributor = new OperationDistributor(this);
      long time = System.currentTimeMillis();
      //BEGIN init components
      interfaces = new Hashtable<String, InterfaceWrapper>();
      libraries = new Hashtable<String, LibraryWrapper>();
      services = new Hashtable<String, ServiceWrapper>();
      try {
        persistentContainer.createComponents(interfaces, libraries, services);
      } catch (Exception e) {
        SimpleLogger.log(Severity.FATAL, CATEGORY_SERVER_CRITICAL, LOCATION,
            "ASJ.krn_srv.000012",
            "Cannot initialize service container components");
        SimpleLogger.traceThrowable(Severity.FATAL, LOCATION,
            "Cannot initialize service container components", e);
        return false;
      }
      //add core library (parent of kernel loader)
      ClassLoaderManager classLoaderManager = (ClassLoaderManager) Framework.getManager(Names.CLASSLOADER_MANAGER);
      LibraryWrapper core = new LibraryWrapper(this, classLoaderManager.getResourceNames(getClass().getClassLoader().getParent()));
      libraries.put(core.getComponentName(), core);
      printMessage(" Initialize components for : " + (System.currentTimeMillis() - time) + " ms.");
      //END init components
      referenceResolver = new ReferenceResolver(this);
      loadContainer = new LoadContainer(this);
      propertiesEventHandler = new PropertiesEventHandler(this);
      //resolve and load components ->
      time = System.currentTimeMillis();
      referenceResolver.resolveComponentReferences();
      referenceResolver.checkForHardReferenceCycles(false);
      ArrayList<ComponentWrapper[]> loaders = referenceResolver.findCommonLoaderAreas();
      referenceResolver.checkForClassloadCycles(loaders, haltOnClassloadCycle);
      printMessage(" Resolve components for : " + (System.currentTimeMillis()- time) + " ms.");
      time = System.currentTimeMillis();
      loadContainer.createClassLoaders(loaders);
      printMessage(" Load components for " + (System.currentTimeMillis() - time) + " ms.");
      //start services ->
      printMessage(ResourceUtils.getString(ResourceUtils.LOADING_SERVICES));
      time = System.currentTimeMillis();
      //check whether all core components are loaded
      referenceResolver.checkCoreComponentsExisting();
      coreServiceSet = referenceResolver.getCoreServiceSet();
      startServiceSet = referenceResolver.getStartupServiceSet();
      startServiceSet.addAll(coreServiceSet);
      printMessage(" Calculate startup service set for " + (System.currentTimeMillis() - time) + " ms.");
      
      if (LOCATION.beDebug()) {
        //dump start & core services set if trace severity is debug
        LOCATION.debugT("Starting service set with all core services = " + startServiceSet);
        LOCATION.debugT("Core service set" + coreServiceSet);
      }
      //init shm startup modes & status failed for not resolved components
      for (ServiceWrapper sw : services.values()) {
        initServiceShm(sw);
      }
      operateServicesSet = new HashSet<ServiceWrapper>();
      operateServicesSet.addAll(startServiceSet);
      time = System.currentTimeMillis();
      synchronized (operateSynchronizer) {
        for (ServiceWrapper service : operateServicesSet) {
          //calculate service wait count -> service will start after all services/interfaces that service has hard reference to are started/provided
          service.calculateWaitCount(false);
          //start service if no hard references found!
          if (service.getWaitCount() == 0) {
            threadManager.startThread(new ServiceRunner(this, service, haltOnAdditionalServiceFailure), null, "Service Runner [" + service.getComponentName() + "]", true);
          }
        }
        try {
          operateSynchronizer.wait(serviceStartTimeout);
          if (!operateServicesSet.isEmpty()) {
            servicesTimeout(true);
          }
        } catch (InterruptedException e) {
          SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION,
              "ASJ.krn_srv.000013", "Wait interrupted");
          if (SimpleLogger.isWritable(Severity.INFO, LOCATION)) {
            SimpleLogger.traceThrowable(Severity.INFO, LOCATION,
                "Wait interrupted", e);
          }
        }
      }
      printMessage(" Start all services from startup set for : " + (System.currentTimeMillis() - time)  + " ms.");
      if (persistentContainer.getDefaultTemplateLevel().getLevelIdentifier().equals(persistentContainer.getTemplateLevel().getLevelIdentifier())) {
        printMessage(" Current Java instance is using default template [" + persistentContainer.getDefaultTemplateLevel().getLevelIdentifier() + "]");
      } else {
        printMessage(" Current Java instance is using default template [" + persistentContainer.getDefaultTemplateLevel().getLevelIdentifier() +
                      "] and custom template [" + persistentContainer.getDefaultTemplateLevel().getLevelIdentifier() + "]");
      }
      return true;
    }
  }

  private void initServiceShm(ServiceWrapper sw) {
    try {
      ShmComponent shmc = ShmComponent.find(sw.getComponentName(), ShmComponent.Type.SERVICE);
      if (sw.getStatus() == ServiceMonitor.STATUS_LOADED) {
        byte startupMode = sw.getStartupMode();
        switch (startupMode) {
          case ServiceMonitor.ALWAYS_START : {
            shmc.setStartMode(ShmComponent.StartMode.ALWAYS);
            shmc.setTargetStatus(ShmComponent.Status.STARTED);
          } break;
          case ServiceMonitor.MANUAL_START : {
            shmc.setStartMode(ShmComponent.StartMode.MANUAL);
            shmc.setTargetStatus(ShmComponent.Status.STOPPED);
          } break;
          case ServiceMonitor.DISABLED : {
            shmc.setStartMode(ShmComponent.StartMode.DISABLED);
            shmc.setTargetStatus(ShmComponent.Status.STOPPED);
          } break;
        }
      } else {
        //if component is not loaded -> cannot determine startup mode -> set it manual
        shmc.setStartMode(ShmComponent.StartMode.MANUAL);
        shmc.setLocalStatus(ShmComponent.Status.FAILED);
      }
    } catch (ShmException e) {
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot init service " + sw.getComponentName() + " ShmComponent", e);
    }
  }

  //stop all active services - used during shutdown ("runtime" topologic sort algorithm is used)
  void stop() {
    synchronized (runtimeOperationLock) {
      distributor.destroy();
      printMessage(ResourceUtils.getString(ResourceUtils.STOPPING_SERVICES));
      if (serviceStopTimeout == 0) {
        //if stop timeout == 0 halt
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Services stop timeout is 0 -> Runtime.halt()");
        }
        Runtime.getRuntime().halt(0);
      } else {
        long time = System.currentTimeMillis();
        synchronized (operateSynchronizer) {
          operateServicesSet.clear();
          //get active ones and calculate wait counts.
          for (ServiceWrapper service : services.values()) {
            if (service.getStatus() == ComponentMonitor.STATUS_ACTIVE) {
              operateServicesSet.add(service);
              service.calculateWaitCount(true);
            }
          }
          //1. stop services which are not hard referred from others
          for (ServiceWrapper service : operateServicesSet) {
            if (service.getWaitCount() == 0) {
              threadManager.startThread(new ServiceStopper(this, service), null, "Service Stopper [" + service.getComponentName() + "]", true);
            }
          }
          try {
            operateSynchronizer.wait(serviceStopTimeout);
            if (!operateServicesSet.isEmpty()) {
              servicesTimeout(false);
            }
          } catch (InterruptedException e) {
            SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION,
                "ASJ.krn_srv.000014", "Wait interrupted");
            if (SimpleLogger.isWritable(Severity.INFO, LOCATION)) {
              SimpleLogger.traceThrowable(Severity.INFO, LOCATION,
                  "Wait interrupted", e);
            }
          }
        }
        printMessage(" Stop all started services for : " + (System.currentTimeMillis() - time)  + " ms.");
        propertiesEventHandler.destroy();
      }
    }
  }

  //invoked from service runner when a service is started
  void serviceReady(ServiceWrapper service, boolean success) {
    //if all services are started -> notify
    synchronized (operateSynchronizer) {
      operateServicesSet.remove(service);
      if (operateServicesSet.isEmpty()) {
        operateSynchronizer.notify();
        return;
      }
    }
    for (ReferenceImpl ref : service.getReverseReferenceSet()) {
      if (ref.getType() == Reference.TYPE_HARD) {
        if (ref.getReferentType() == Reference.REFER_SERVICE) {
          ServiceWrapper sw = (ServiceWrapper) ref.getReferencedComponent();
          if (success) {
            if (sw.decreaseWaitCount() == 0) {
              threadManager.startThread(new ServiceRunner(this, sw, haltOnAdditionalServiceFailure), null, "Service Runner [" + sw.getComponentName() + "]", true);
            }
          } else {
            printErrorTree(sw, service.getComponentName(), null);
          }
        }
      }
    }
    if (service.getProvidedInterfaces() != null) {
      for (String interfaceName : service.getProvidedInterfaces()) {
        InterfaceWrapper iw = interfaces.get(interfaceName);//interface exists because component is resolved
        for (ReferenceImpl ref : iw.getReverseReferenceSet()) {
          if (ref.getType() == Reference.TYPE_HARD && ref.getReferentType() == Reference.REFER_SERVICE) {
            ServiceWrapper sw = (ServiceWrapper) ref.getReferencedComponent();
            if (success) {
              if (sw.decreaseWaitCount() == 0) {
                threadManager.startThread(new ServiceRunner(this, sw, haltOnAdditionalServiceFailure), null, "Service Runner [" + sw.getComponentName() + "]", true);
              }
            } else {
              printErrorTree(sw, iw.getComponentName(), service.getComponentName());
            }
          }
        }
      }
    }
  }

  private void printErrorTree(ServiceWrapper service, String initiatorName, String interfaceProvider) {
    if (interfaceProvider != null) {
      SystemEnvironment.STD_ERR.println("  Service [" +
          service.getComponentName() +
          "] cannot be started because of hard reference to interface [" +
          initiatorName + "] with failed to start provider service [" +
          interfaceProvider + "]");
      if(SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000015",
            "Service [{0}] cannot be started because of hard reference to interface [{1}] with failed to start provider service [{2}]",
            new Object[] {service.getComponentName(), initiatorName,
              interfaceProvider});
      }
    } else {
      SystemEnvironment.STD_ERR.println("  Service [" +
          service.getComponentName() +
          "] cannot be started because of hard reference to failed to start service [" +
          initiatorName + "]");
      if(SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000016",
            "Service [{0}] cannot be started because of hard reference to failed to start service [{1}]",
            new Object[] {service.getComponentName(), initiatorName});
      }
    }
    service.setNegativeWaitCount();
    //set ShmComponent local status to stopped
    try {
      ShmComponent shmc = ShmComponent.find(service.getComponentName(), ShmComponent.Type.SERVICE);
      shmc.setLocalStatus(ShmComponent.Status.STOPPED);
    } catch (ShmException e) {
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot set service " + service.getComponentName() + " status to STOPPED in ShmComponent", e);
    }
    serviceReady(service, false);
  }

  //invoked from service stopper when a service is stopped
  void serviceStopped(ServiceWrapper service) {
    synchronized (operateSynchronizer) {
      operateServicesSet.remove(service);
      if (operateServicesSet.isEmpty()) {
        operateSynchronizer.notify();
        return;
      }
    }
    //notify all direct hard ref (S --h--> I and S --h--> S)
    for (ReferenceImpl ref : service.getReferenceSet()) {
      if (ref.getType() == Reference.TYPE_HARD) {
        if (ref.getReferentType() == Reference.REFER_SERVICE) {
          ServiceWrapper sw = (ServiceWrapper) ref.getReferencedComponent();
          if (sw.decreaseWaitCount() == 0) {//decrease wait count with 1
            threadManager.startThread(new ServiceStopper(this, sw), null, "Service Stopper [" + sw.getComponentName() + "]", true);
          }
        } else if (ref.getReferentType() == Reference.REFER_INTERFACE) {
          InterfaceWrapper iw = (InterfaceWrapper) ref.getReferencedComponent();
          ServiceWrapper sw = iw.getProvider();
          //<sw> must not be null because <service> was started
          //i.e if service was started it means that HR to interfaces are resolved against interface providers
          if (sw.decreaseWaitCount() == 0) {//decrease wait count with 1
            threadManager.startThread(new ServiceStopper(this, sw), null, "Service Stopper [" + sw.getComponentName() + "]", true);
          }
        }
      }
    }
    //provided interfaces from the stopped service have no relation to the stop logic.
  }

  private void servicesTimeout(boolean isStartup) {
    boolean coreTimeout = false; //indicate if a core service exists in the set
    boolean found = false; //if remain false -> all services are meanwhile started|stopped and timeout is ignored
    for (ServiceWrapper service : operateServicesSet) {
      String reason;
      int current = service.getInternalStatus();
      // timeout reason
      if (isStartup) {
        if (current == ServiceWrapper.INTERNAL_STATUS_STARTED) {//if service is meanwhile started
          continue;
        } else if (current == ServiceWrapper.INTERNAL_STATUS_STARTING) {//if service is stating
          reason = ResourceUtils.getString(ResourceUtils.TIMEOUT_REASON_STARTING);
        } else {//wait for other components to be started.
          Object[] param = new Object[] {service.getWaitCount() + " : " + getFrowardWaitList(service)};
          reason = ResourceUtils.formatString(ResourceUtils.TIMEOUT_REASON_WAIT_COMPONENTS_TO_START, param);
        }
      } else {
        if (current == ServiceWrapper.INTERNAL_STATUS_STOPPED) {//if service is meanwhile stopped
          continue;
        } else if (current == ServiceWrapper.INTERNAL_STATUS_STOPPING) {//if service is stopping
          reason = ResourceUtils.getString(ResourceUtils.TIMEOUT_REASON_STOPPING);
        } else {//wait for other components to be stopped.
          Object[] param = new Object[] {service.getWaitCount() + " : " + getBackwardWaitList(service)};
          reason = ResourceUtils.formatString(ResourceUtils.TIMEOUT_REASON_WAIT_COMPONENTS_TO_STOP, param);
        }
      }
      if (!found) {
        found = true;
        SystemEnvironment.STD_ERR.println("Timed out services");
        SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.krn_srv.000017", "Timed out services");
      }
      coreTimeout |= service.isCore();
      SystemEnvironment.STD_ERR.println("Service [" + service.getComponentName() + "] > [" + reason + "]");
      SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.krn_srv.000018", "Service [{0}] > [{1}]", service.getComponentName(), reason);
    }
    if (found) {
      ProcessEnvironment.getThreadDump("Timed out services");
      if (serviceTimeoutAction == SERVICE_TIMEOUT_ACTION_HALT) {
        if (isStartup) {
          Framework.criticalShutdown(STARTING_SERVICE_TIMEOUT_EXIT_CODE, ResourceUtils.formatString(ResourceUtils.SERVICES_TIMED_OUT, new Object[] {operateServicesSet.toString()}));
        }
      } else if (serviceTimeoutAction == SERVICE_TIMEOUT_ACTION_WAIT) {
        //take thread dumps on regular intervals until all services are started/stopped!
        int count = 0;
        while (!operateServicesSet.isEmpty()) {
          try {
            //$JL-WAIT$ //this method is invoked within synchronized (operateSynchronizer) block.
            operateSynchronizer.wait(THREAD_DUMP_INTERVAL);
            if (!operateServicesSet.isEmpty()) {//take thread dump if not all services are started
              ProcessEnvironment.getThreadDump(ResourceUtils.formatString(ResourceUtils.THREAD_DUMP_NUMBER, new Object[] {operateServicesSet.toString(), "" + ++count}));
            }
          } catch (InterruptedException e) {
            //$JL-EXC$ if occurs -> continue on condition
          }
        }
      } else {//SERVICE_TIMEOUT_ACTION_IGNORE - old behaviour
        if (isStartup)  {//if core service timeout or haltOnAdditionalServiceFailure flag is ON -> critical shutdown
          if (coreTimeout || haltOnAdditionalServiceFailure) {
            Framework.criticalShutdown(STARTING_SERVICE_TIMEOUT_EXIT_CODE, ResourceUtils.formatString(ResourceUtils.SERVICES_TIMED_OUT, new Object[] {operateServicesSet.toString()}));
          }
        }
      }
    }
  }

  private ArrayList<String> getFrowardWaitList(ServiceWrapper service) {
    ArrayList<String> result = new ArrayList<String>();
    for (ReferenceImpl reference : service.getReferenceSet()) {
      if (reference.getType() == Reference.TYPE_HARD) {
        ServiceWrapper referenceService = null;
        if (reference.getReferentType() == Reference.REFER_SERVICE) {
          referenceService = (ServiceWrapper) reference.getReferencedComponent();
        } else if (reference.getReferentType() == Reference.REFER_INTERFACE) {
          referenceService = ((InterfaceWrapper) reference.getReferencedComponent()).getProvider();
        }
        //service is null if type is library - there is no sense to set hard ref from service to library.
        if (referenceService != null) {
          //if ref service is not started
          if (referenceService.getInternalStatus() != ServiceWrapper.INTERNAL_STATUS_STARTED) {
            result.add(referenceService.getComponentName());
          }
        }
      }
    }
    return result;
  }

  private ArrayList<String> getBackwardWaitList(ServiceWrapper service) {
    ArrayList<String> result = new ArrayList<String>();
    for (ReferenceImpl reference : service.getReverseReferenceSet()) {
      //check reverse hard service refs
      if (reference.getType() == Reference.TYPE_HARD && reference.getReferentType() == Reference.REFER_SERVICE) {
        ServiceWrapper referenceService = (ServiceWrapper) reference.getReferencedComponent();
        //if reverse ref service is not stoped.
        byte status = referenceService.getInternalStatus();
        if (status != ServiceWrapper.INTERNAL_STATUS_STOPPED && status != ServiceWrapper.INTERNAL_STATUS_STOP_FAIL) {
          result.add(referenceService.getComponentName());
        }
      }
    }
    if (service.getProvidedInterfaces() != null) {
      for (String interfaceName : service.getProvidedInterfaces()) {
        InterfaceWrapper interfaceWrapper = interfaces.get(interfaceName);//interface exists because component is resolved
        for (ReferenceImpl reference : interfaceWrapper.getReverseReferenceSet()) {
          if (reference.getType() == Reference.TYPE_HARD && reference.getReferentType() == Reference.REFER_SERVICE) {
            ServiceWrapper referenceService = (ServiceWrapper) reference.getReferencedComponent();
            //if reverse ref service is not stoped.
            byte status = referenceService.getInternalStatus();
            if (status != ServiceWrapper.INTERNAL_STATUS_STOPPED && status != ServiceWrapper.INTERNAL_STATUS_STOP_FAIL) {
              result.add(referenceService.getComponentName());
            }
          }
        }
      }
    }
    return result;
  }

  private void printMessage(String message) {
    SystemEnvironment.STD_OUT.println(message);
    if (LOCATION.beDebug()) {
      LOCATION.debugT(message);
    }
  }

  //-------------------------------------- runtime ---------------------------------------------------------------------
  
  //used only for debug
  private String getServiceStatusAsString(ServiceWrapper service) {
      byte statusCode = service.getStatus();
      String status = "";
      switch(statusCode) {
      case ComponentMonitor.STATUS_LOADED:
          status = "loaded";
          break;
      case ComponentMonitor.STATUS_ACTIVE:
          status = "active";
          break;
      default: 
          status+= statusCode;
      }
      return status;
  }

  //used only for debug
  private String getServiceInternalStatusAsString(ServiceWrapper service) {
      byte statusCode = service.getInternalStatus();
      String status = "";
      switch(statusCode) {
      case ServiceMonitor.INTERNAL_STATUS_START_FAIL:
          status = "start failed";
          break;
      case ServiceMonitor.INTERNAL_STATUS_STARTING:
          status = "starting";
          break;
      case ServiceMonitor.INTERNAL_STATUS_STARTED:
          status = "started";
          break;
      case ServiceMonitor.INTERNAL_STATUS_STOP_FAIL:
          status = "stop failed";
          break;
      case ServiceMonitor.INTERNAL_STATUS_STOPPING:
          status = "stopping";
          break;
      case ServiceMonitor.INTERNAL_STATUS_STOPPED:
          status = "stopped";
          break;
      default: 
          status+= statusCode;
      }
      return status;
  }
  
  public void stopServiceRuntime(ServiceWrapper service) throws ServiceException {
    synchronized (runtimeOperationLock) {
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Intending to stop service " + service.getComponentName() +
                "; Service status: " + getServiceStatusAsString(service) + "; Internal status: " + getServiceInternalStatusAsString(service),
                new Exception());
      }
      if (service.getStatus() == ComponentMonitor.STATUS_ACTIVE) {
        if (SystemThreadProcessor.isSystem()) {
          //thread is system -> process
          stopServiceRuntime(service, null, false);
        } else {
          //thread is application -> start system thread
          SystemThreadProcessor processor = new SystemThreadProcessor(this, service, false);
          processor.process();
        }
      } else {
        SystemEnvironment.STD_OUT.println("Attempt to stop service [" + service.getComponentName() + "] but it is not started");
        if (SimpleLogger.isWritable(Severity.WARNING, CATEGORY)) {
          SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.krn_srv.000019", "Attempt to stop service [{0}] but it is not started", service.getComponentName());
        }
      }
    }
  }

  void stopServiceRuntime(ServiceWrapper service, ComponentWrapper initiator, boolean isUnload) throws ServiceException {
    //core service check
    if (service.isCore()) {
      SystemEnvironment.STD_OUT.println("Cannot stop core service [" + service + "]!");
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_STOP_CORE_SERVICE), new Object[] {service.getComponentName()}));
    }
    //print initiator info if initiator is service, otherwise the initiator is component to be unloaded.
    if (initiator != null) {
      if (isUnload) {
        printMessage("Unload of " + initiator + " initiate service " + service + " stop.");
      } else {
        printMessage("Stop of service " + initiator + " initiate service " + service + " stop due to hard reference.");
      }
    }
    //1. stop components that have hard reference to the stopping service
    for (ReferenceImpl reference : service.getReverseReferenceSet()) {
      if (reference.getType() == Reference.TYPE_HARD && reference.getReferentType() == Reference.REFER_SERVICE) {
        ServiceWrapper reverseRefService = (ServiceWrapper) reference.getReferencedComponent();
        if (reverseRefService.getStatus() == ComponentMonitor.STATUS_ACTIVE) {
          stopServiceRuntime(reverseRefService, service, false);
        }
      }
    }
    //2. stop services that have hard reference to provided interfaces
    if (service.getProvidedInterfaces() != null) {
      for (String interfaceName : service.getProvidedInterfaces()) {
        InterfaceWrapper interfaceWrapper = interfaces.get(interfaceName); //interface exist because the service is loaded
        for (ReferenceImpl reference : interfaceWrapper.getReverseReferenceSet()) {
          if (reference.getType() == Reference.TYPE_HARD && reference.getReferentType() == Reference.REFER_SERVICE) {
            ServiceWrapper reverseRefService = (ServiceWrapper) reference.getReferencedComponent();
            if (reverseRefService.getStatus() == ComponentMonitor.STATUS_ACTIVE) {
              stopServiceRuntime(reverseRefService, service, false);
            }
          }
        }
      }
    }
    //3. stop the service
    ServiceStopper serviceStopper = new ServiceStopper(this, service);
    serviceStopper.stopSingleService();
    if (initiator != null) {
      service.setImplicit();
    }
  }

  public void startServiceRuntime(ServiceWrapper service) throws ServiceException {
    synchronized (runtimeOperationLock) {
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Intending to start service " + service.getComponentName() +
                "; Service status: " + getServiceStatusAsString(service) + "; Internal status: " + getServiceInternalStatusAsString(service),
                new Exception());
      }
      if (service.getStatus() == ComponentMonitor.STATUS_ACTIVE) {
        SystemEnvironment.STD_OUT.println("Attempt to start service [" + service.getComponentName() + "] but it is already started");
        if (SimpleLogger.isWritable(Severity.WARNING, CATEGORY)) {
          SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.krn_srv.000020", "Attempt to start service [{0}] but it is already started", service.getComponentName());
        }
      } else {
        if (service.getStatus() != ComponentMonitor.STATUS_LOADED) {
          throw new ServiceNotLoadedException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                  ResourceUtils.getKey(ResourceUtils.SERVICE_NOT_LOADED), new Object[] {service.getComponentName()}));
        } else if (service.isDisabled()) {
          SystemEnvironment.STD_OUT.println("Service [" + service.getComponentName() + "] is disabled");
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.krn_srv.000021", "Service [{0}] is disabled", service.getComponentName());
          throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                  ResourceUtils.getKey(ResourceUtils.CANNOT_START_DISABLED_SERVICE), new Object[] {service.getComponentName()}));
        }
        if (SystemThreadProcessor.isSystem()) {
          //thread is system -> process
          startServiceRuntime(service, null, false, false);
        } else {
          //thread is application -> start system thread
          SystemThreadProcessor processor = new SystemThreadProcessor(this, service, true);
          processor.process();
        }
      }
    }
  }

  //initiator - the component that initiates this start, if it is not null the invocation is because of hard references
  //isImplicitStart - if true the initiator is implicit
  //isLoad - if true the reason message is 'load' instead of 'start'
  void startServiceRuntime(ServiceWrapper service, ComponentWrapper initiator, boolean isImplicitStart, boolean isLoad) throws ServiceException {
    //print initiator and implicit info
    if (initiator != null) {
      String reason = (isImplicitStart) ? "implicit stop state." : "hard reference.";
      printMessage(((isLoad) ? "Load" : "Start") + " of " + initiator + " initiate service " + service + " start due to " + reason);
    }
    //1. star components that starting service has hard reference to them
    for (ReferenceImpl reference : service.getReferenceSet()) {
      if (reference.getType() == Reference.TYPE_HARD) {
        if (reference.getReferentType() == Reference.REFER_SERVICE) {
          ServiceWrapper refService = (ServiceWrapper) reference.getReferencedComponent();
          if (refService.getStatus() != ComponentMonitor.STATUS_ACTIVE) {
            startServiceRuntime(refService, service, false, false);
          }
        } else if (reference.getReferentType() == Reference.REFER_INTERFACE) {
          InterfaceWrapper interfaceWrapper = (InterfaceWrapper) reference.getReferencedComponent();
          ServiceWrapper provider = interfaceWrapper.getProvider();
          if (provider != null && provider.getStatus() >= ComponentMonitor.STATUS_LOADED) {
            if (provider.getStatus() != ComponentMonitor.STATUS_ACTIVE) {
              startServiceRuntime(provider, service, false, false);
            }
          } else {//interface provider can be <null> or not loaded in both cases throw service exception
            if (provider == null) {
              throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                      ResourceUtils.getKey(ResourceUtils.CANT_START_SERVICE_IPROVIDER_NOT_DEPLOYED),
                      new Object[] {service.getComponentName(), interfaceWrapper.getComponentName()}));
            } else {
              throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                      ResourceUtils.getKey(ResourceUtils.CANT_START_SERVICE_IPROVIDER_NOT_LOADED),
                      new Object[] {service.getComponentName(), interfaceWrapper.getComponentName(), provider.getComponentName()}));
            }
          }
        }
      }
    }
    //2. start the service
    ServiceRunner serviceRunner = new ServiceRunner(this, service, haltOnAdditionalServiceFailure);
    serviceRunner.startSingleService();
    if (initiator != null) {
      service.setImplicit();
    }
    //3. start services that have hard reference to the service (or provided interfaces) and are in implicit stop
    for (ReferenceImpl reference : service.getReverseReferenceSet()) {
      if (reference.getType() == Reference.TYPE_HARD && reference.getReferentType() == Reference.REFER_SERVICE) {
        ServiceWrapper reverseRefService = (ServiceWrapper) reference.getReferencedComponent();
        if (reverseRefService.isImplicit() && reverseRefService.getInternalStatus() == ServiceWrapper.INTERNAL_STATUS_STOPPED && reverseRefService.isDirectStartPossible()) {
          startServiceRuntime(reverseRefService, service, true, false);
        }
      }
    }
    if (service.getProvidedInterfaces() != null) {
      for (String interfaceName : service.getProvidedInterfaces()) {
        InterfaceWrapper interfaceWrapper = interfaces.get(interfaceName); //interface exist because the service is loaded
        for (ReferenceImpl reference : interfaceWrapper.getReverseReferenceSet()) {
          if (reference.getType() == Reference.TYPE_HARD && reference.getReferentType() == Reference.REFER_SERVICE) {
            ServiceWrapper reverseRefService = (ServiceWrapper) reference.getReferencedComponent();
            if (reverseRefService.isImplicit() && reverseRefService.getInternalStatus() == ServiceWrapper.INTERNAL_STATUS_STOPPED && reverseRefService.isDirectStartPossible()) {
              startServiceRuntime(reverseRefService, service, true, false);
            }
          }
        }
      }
    }
  }

/////////////////////////////////////////// DEPLOY / UNDEPLOY //////////////////////////////////////////////////////////

  public String deployInterface(File jar, String initiator) throws ServiceException {
    return deployComponent(jar, initiator, ComponentWrapper.TYPE_INTERFACE);
  }

  public String deployLibrary(File jar, String initiator) throws ServiceException {
    return deployComponent(jar, initiator, ComponentWrapper.TYPE_LIBRARY);
  }

  public String deployService(File jar, String initiator) throws ServiceException {
    return deployComponent(jar, initiator, ComponentWrapper.TYPE_SERVICE);
  }

  private String deployComponent(File jar, String initiator, byte componentType) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Online deploy for archive " + jar + " is triggered from service " + initiator, new Exception());
    }
    long begin = System.currentTimeMillis();
    long t = begin;
    lock();
    if (LOCATION.beDebug()) {
      LOCATION.debugT(" > Time for lock = " + (System.currentTimeMillis() - t) + " ms.");
    }
    try {
      //1. DC replace is deprecated -> removed as runtime operation
      //2. If provider xml is not defined the deploy will failed
      //3. Take jar name as component name is not an option: componentName = jar.getName();
      //deploy component using offline deploy:
      t = System.currentTimeMillis();
      String componentName = persistentContainer.deployComponentInDB(jar, componentType);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for deploy component " + componentName + " in DB = " + (System.currentTimeMillis() - t) + " ms.");
      }
      t = System.currentTimeMillis();
      distributor.sendCreate(componentName, componentType);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for creating component " + componentName + " = " + (System.currentTimeMillis() - t) + " ms.");
      }
      return componentName;
    } finally {
      unlock();
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Total time for deploy = " + (System.currentTimeMillis() - begin) + " ms.");
      }
    }
  }

  public void removeInterface(String providerName, String interfaceName, String initiator) throws ServiceException, ComponentNotDeployedException {
    removeComponent(providerName, interfaceName, initiator, ComponentWrapper.TYPE_INTERFACE);
  }

  public void removeLibrary(String providerName, String libraryName, String initiator) throws ServiceException, ComponentNotDeployedException {
    removeComponent(providerName, libraryName, initiator, ComponentWrapper.TYPE_LIBRARY);
  }

  public void removeService(String providerName, String serviceName, String initiator) throws ServiceException, ComponentNotDeployedException {
    removeComponent(providerName, serviceName, initiator, ComponentWrapper.TYPE_SERVICE);
  }

  public void removeComponent(String providerName, String name, String initiator, byte componentType) throws ServiceException, ComponentNotDeployedException {
    if (LOCATION.beDebug()) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Online remove for " + providerName + "~" + name + " is triggered from service " + initiator, new Exception());
    }
    long begin = System.currentTimeMillis();
    long t = begin;
    lock();
    if (LOCATION.beDebug()) {
      LOCATION.debugT(" > Time for lock = " + (System.currentTimeMillis() - t) + " ms.");
    }
    try {
      //remove from DB
      t = System.currentTimeMillis();
      String componentName = persistentContainer.removeComponentFromDB(providerName, name, componentType);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for removing component " + componentName + "from DB = " + (System.currentTimeMillis() - t) + " ms.");
      }
      //remove from all service containers in the cluster
      t = System.currentTimeMillis();
      distributor.sendRemove(componentName, componentType);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for removing component " + componentName + " = " + (System.currentTimeMillis() - t) + " ms.");
      }
    } finally {
      unlock();
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Total time for remove = " + (System.currentTimeMillis() - begin) + " ms.");
      }
    }
  }

  void startCreate(String name, byte type) throws ServiceException {
    synchronized (runtimeOperationLock) {
      //1. if exists -> unload and remove component to release the jars
      if (existComponent(name, type)) {
        removeComponentFromServiceContainer(name, type);
      }
      //2. one node from an instance download binaries
      synchronizeBinaries();
      //3. register component in service container
      long t = System.currentTimeMillis();
      ComponentWrapper component = persistentContainer.createComponent(name, type);
      getComponentTable(type).put(component.getComponentName(), component);
      //4. fire register event
      ContainerEvent event = new ContainerEvent();
      event.isAdmin = true;
      event.method = ContainerEventListener.MASK_COMPONENT_REGISTERED;
      event.name = component.getComponentName();
      event.type = component.getByteType();
      serviceContainer.getContainerEventRegistry().addContainerEvent(event);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for register component " + name + " = " + (System.currentTimeMillis() - t) + " ms.");
      }
      t = System.currentTimeMillis();
      Set<ComponentWrapper> newlyResolved = referenceResolver.resolveComponent(component);
      if (!newlyResolved.isEmpty()) {
        ArrayList<ComponentWrapper[]> list = referenceResolver.findCommonLoaderAreas();
        //check for hard references and classload cycle
        referenceResolver.checkForHardReferenceCycles(true);
        referenceResolver.checkForClassloadCycles(list, false);
        //in one of the upper checks fails it leads to potential engine crash after restart!
        //therefore deploy time cycle check is implemented  
        loadContainer.createClassLoaders(list);
        Set<ServiceWrapper> servicesToBeStarted = referenceResolver.applyFilters(newlyResolved);
        startServiceSet.addAll(servicesToBeStarted);
        if (type == ComponentWrapper.TYPE_SERVICE) {//init shm component
          ServiceWrapper service = (ServiceWrapper) component;
          initServiceShm(service);
          if (service.getStatus() == ServiceMonitor.STATUS_LOADED && service.isDirectStartPossible()) {
            //start service only if direct start is possible!
            startServiceRuntime(service, null, false, false);
          }
        }
        //start implicitly stopped services
        startImplicitlyStoppedServices(component);
        //start services to be started
        startServicesToBeStarted(servicesToBeStarted, component);
      } else {
        if (type == ComponentWrapper.TYPE_SERVICE) {//init shm component to FAILED
          initServiceShm((ServiceWrapper) component);
        }
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for resolve and load component " + name + " = " + (System.currentTimeMillis() - t) + " ms.");
      }
    }
  }

  void startImplicitlyStoppedServices(ComponentWrapper component) throws ServiceException {
    Set<ComponentWrapper> set = component.getLoaderParticipant();
    if (set != null) {//currently if component is loaded
      //for all components participate in this loader check for service refs
      for (ComponentWrapper cw : set) {
        // 1. if <cw> is servcie try to start it.
        if (cw.getByteType() == ComponentWrapper.TYPE_SERVICE) {
          ServiceWrapper sw = (ServiceWrapper) cw;
          if (sw.isImplicit() && sw.getInternalStatus() == ServiceWrapper.INTERNAL_STATUS_STOPPED && sw.isDirectStartPossible()) {
            startServiceRuntime(sw, component, true, true);
          }
        }
        // 2. check component reverse references.
        for (ReferenceImpl reference : cw.getReverseReferenceSet()) {
          ComponentWrapper revecrceWrapper = reference.getReferencedComponent();
          if (!set.contains(revecrceWrapper)) {
            startImplicitlyStoppedServices(revecrceWrapper);
          }
        }
      }
    }
  }

  void startServicesToBeStarted(Set<ServiceWrapper> servicesToBeStarted, ComponentWrapper initiator) throws ServiceException {
    boolean update;
    do {
      update = false;
      for (ServiceWrapper sw : servicesToBeStarted) {
        if (sw.getStatus() == ServiceMonitor.STATUS_LOADED && sw.isDirectStartPossible()) {
          startServiceRuntime(sw, initiator, false, true);
          update = true;
        }
      }
    } while (update);
  }

  void startRemove(String name, byte type) throws ServiceException {
    synchronized(runtimeOperationLock) {
      //1. unload and undegister component from service container
      removeComponentFromServiceContainer(name, type);
      //2. one node per instance synchronizes binaries
      synchronizeBinaries();
    }
  }

  private void synchronizeBinaries() throws ServiceException {
    long t = System.currentTimeMillis();
    boolean lockObtained = false;
    try {
      //try to get instance lock
      lockInstance();
      lockObtained = true;
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for get instance lock = " + (System.currentTimeMillis() - t) + " ms.");
      }
      boolean downloadSuccessful = false;
      try {
        t = System.currentTimeMillis();
        distributor.instanceLockObtained();
        if (LOCATION.beDebug()) {
          LOCATION.debugT(" > Time for send instance lock obtained = " + (System.currentTimeMillis() - t) + " ms.");
        }
        //download new jars using bootstrap
        t = System.currentTimeMillis();
        persistentContainer.synchBinaries();
        downloadSuccessful = true;
        if (LOCATION.beDebug()) {
          LOCATION.debugT(" > Time for synchronizing binaries = " + (System.currentTimeMillis() - t) + " ms.");
        }
      } finally {
        //send ready message
        distributor.binariesSynchronized(downloadSuccessful);
      }
    } catch (LockException e) {
      //instance is locked wait for synchronization
      t = System.currentTimeMillis();
      if (!distributor.isBinariesDownloaded()) {
        throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor, ResourceUtils.getKey(ResourceUtils.BINARY_SYNCHRONIZATION_ERROR)));
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time wait for synchronizing binaries = " + (System.currentTimeMillis() - t) + " ms.");
      }
    } finally {
      if (lockObtained) {
        unlockInstance();
      }
    }
  }

  private void removeComponentFromServiceContainer(String name, byte type) throws ServiceException {
    long t = System.currentTimeMillis();
    ComponentWrapper component = (ComponentWrapper) getComponentTable(type).get(name);
    if (component != null) {
      //fire begin undeploy event
      ContainerEvent event = new ContainerEvent();
      event.isBefore = true;
      event.isAdmin = true;
      event.method = ContainerEventListener.MASK_BEGIN_COMPONENT_UNDEPLOY;
      event.name = component.getComponentName();
      event.type = component.getByteType();
      serviceContainer.getContainerEventRegistry().addContainerEvent(event);
      //unload component & remove from reverse sets and registry
      unloadComponentRuntime(component, null);
      referenceResolver.removeFromReverseReferences(component);
      getComponentTable(type).remove(name);
      if (type == ComponentWrapper.TYPE_SERVICE) {//try to remove it from startup set & from shm registry
        ServiceWrapper service = (ServiceWrapper) component;
        startServiceSet.remove(service);
        try {
          ShmComponent shmc = ShmComponent.find(component.getComponentName(), ShmComponent.Type.SERVICE);
          shmc.close();
        } catch (ShmException e) {
          LOCATION.traceThrowableT(Severity.WARNING, "Cannot remove service " + component.getComponentName() + " ShmComponent", e);
        }
      }
      //fire component undeployed
      event = new ContainerEvent();
      event.isAdmin = true;
      event.method = ContainerEventListener.MASK_COMPONENT_UNDEPLOYED;
      event.name = component.getComponentName();
      event.type = component.getByteType();
      serviceContainer.getContainerEventRegistry().addContainerEvent(event);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for remove component " + name + " from service container = " + (System.currentTimeMillis() - t) + " ms.");
      }
    } else {
      if (LOCATION.beWarning()) {
        LOCATION.warningT(ResourceUtils.formatString(ResourceUtils.COMPONENT_NOT_EXISTS_IN_REGISTRY, new Object[] {name}));
      }
    }
  }

  private Hashtable getComponentTable(byte type) {
    switch (type) {
      case ComponentWrapper.TYPE_INTERFACE : return interfaces;
      case ComponentWrapper.TYPE_LIBRARY : return libraries;
      case ComponentWrapper.TYPE_SERVICE : return services;
      default : return null;
    }
  }

  private boolean existComponent(String name, byte type) {
    switch (type) {
      case ComponentWrapper.TYPE_INTERFACE : return interfaces.containsKey(InterfaceWrapper.transformINameApiToIName(name));
      case ComponentWrapper.TYPE_LIBRARY : return libraries.containsKey(name);
      case ComponentWrapper.TYPE_SERVICE: return services.containsKey(name);
      default : return false;
    }
  }

  private void unloadComponentRuntime(ComponentWrapper component, String initiator) throws ServiceException {
    if (component.getStatus() == ComponentMonitor.STATUS_DEPLOYED || component.getStatus() == ComponentMonitor.STATUS_RESOLVED) {
      return; //return for deployed and resolved statuses
    }
    long t = System.currentTimeMillis();
    ClassLoaderManager clm = (ClassLoaderManager) Framework.getManager(Names.CLASSLOADER_MANAGER);
    ClassLoader loader = component.getClassLoader();
    Set<ComponentWrapper> set = component.getLoaderParticipant();
    for (ComponentWrapper cw : set) {
      //if service stop =>
      if (cw.getByteType() == ComponentWrapper.TYPE_SERVICE && cw.getStatus() == ComponentMonitor.STATUS_ACTIVE) {
        try {
          stopServiceRuntime((ServiceWrapper) cw, component, true);
        } catch (ServiceException e) {
          //$JL-EXC$ ServiceStopper will log the error
          //error during stop should not break the unload operation!
        }
      }
      //first unload components that has reference to this component
      Set<ReferenceImpl> reverseReferences = cw.getReverseReferenceSet();
      for (ReferenceImpl reference : reverseReferences) {
        ComponentWrapper revecrceWrapper = reference.getReferencedComponent();
        if (!set.contains(revecrceWrapper)) {
          unloadComponentRuntime(revecrceWrapper, component.toString());
        }
      }
      //second fire before unload event
      ContainerEvent event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_BEGIN_COMPONENT_UNLOAD;
      event.isBefore = true;
      event.isAdmin = true;
      event.name = cw.getComponentName();
      event.type = cw.getByteType();
      serviceContainer.getContainerEventRegistry().addContainerEvent(event);
    }
    try {
      clm.unregister(loader);
    } catch (Exception e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_UNREGISTER_COMPONENT_LOADER), new Object[] {loader.toString()}), e);
    }
    for (ComponentWrapper cw : set) {
      //set component loader null && set status to deployed
      cw.setClassLoader(null);
      cw.setStatus(ComponentMonitor.STATUS_DEPLOYED);
      //fire unloaded event
      ContainerEvent event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_COMPONENT_UNLOADED;
      event.isAdmin = true;
      event.name = cw.getComponentName();
      event.type = cw.getByteType();
      serviceContainer.getContainerEventRegistry().addContainerEvent(event);
    }
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Unload component " + set + ((initiator == null) ? "" : " because of [" + initiator + "] unloading"));
      LOCATION.debugT(" > Time for unloading component " + set + " = " + (System.currentTimeMillis() - t) + " ms.");
    }
  }

  private void lock() throws ServiceException {
    initializeLockOwnerName();
    //try to take the lock - (6000 * 100) = 10 min
    int tries = 6000;
    boolean success = false;
    while (!success) {
      try {
        lockingManager.getAdministrativeLocking().lock(lockOwnerName, "service_manager_lock_area", "component_deploy", LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE);
        success = true;
      } catch (LockException e) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException ie) {
          //$JL-EXC$ if occurs -> continue on condition
        }
        if (tries-- == 0) {
          throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                  ResourceUtils.getKey(ResourceUtils.CANT_LOCK_SERVICEMANAGER_LOCK_AREA), new Object[] {"component_deploy", lockOwnerName}), e);
        }
      } catch (TechnicalLockException e) {
        throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                ResourceUtils.getKey(ResourceUtils.CANT_LOCK_SERVICEMANAGER_LOCK_AREA), new Object[] {"component_deploy", lockOwnerName}), e);
      }
    }
  }

  private void unlock() throws ServiceException {
    try {
      lockingManager.getAdministrativeLocking().unlock(lockOwnerName, "service_manager_lock_area", "component_deploy", LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE, false);
    } catch (TechnicalLockException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_UNLOCK_SERVICEMANAGER_LOCK_AREA), new Object[] {"component_deploy", lockOwnerName}), e);
    }
  }

  private void lockInstance() throws ServiceException, LockException {
    initializeLockOwnerName();
    try {
      lockingManager.getAdministrativeLocking().lock(lockOwnerName, "service_manager_lock_area", "synchronize_binaries_" + distributor.getInstanceID(), LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE);
    } catch (TechnicalLockException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_LOCK_SERVICEMANAGER_LOCK_AREA), new Object[] {"synchronize_binaries_" + distributor.getInstanceID(), lockOwnerName}), e);
    }
  }

  private void unlockInstance() throws ServiceException {
    try {
      lockingManager.getAdministrativeLocking().unlock(lockOwnerName, "service_manager_lock_area", "synchronize_binaries_" + distributor.getInstanceID(), LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE, false);
    } catch (TechnicalLockException e) {
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CANT_UNLOCK_SERVICEMANAGER_LOCK_AREA), new Object[] {"synchronize_binaries_" + distributor.getInstanceID(), lockOwnerName}), e);
    }
  }

  private void initializeLockOwnerName() throws ServiceException {
    synchronized (initLock) {
      if (lockOwnerName == null) {
        try {
          lockOwnerName = lockingManager.getAdministrativeLocking().createUniqueOwner();
        } catch (TechnicalLockException e) {
          throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                  ResourceUtils.getKey(ResourceUtils.CANT_CREATE_LOCK_OWNER)), e);
        }
      }
    }
  }

  //---------------------------------------- ROLLING -------------------------------------------------------------------

  public RollingResult updateInstanceAndDB(RollingComponent rollingComponent, String initiator) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Online rolling update for component " + rollingComponent + " is triggered from service " + initiator, new Exception());
    }
    long begin = System.currentTimeMillis();
    long t = begin;
    lock();
    if (LOCATION.beDebug()) {
      LOCATION.debugT(" > Time for lock = " + (System.currentTimeMillis() - t) + " ms.");
    }
    try {
      //deploy component using offline deploy:
      t = System.currentTimeMillis();
      byte componentType = rollingComponent.getComponentType();
      String componentName = persistentContainer.deployComponentInDB(new File(rollingComponent.getFilePath()), componentType);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for deploy component " + componentName + " in DB = " + (System.currentTimeMillis() - t) + " ms.");
      }
      t = System.currentTimeMillis();
      //init rolling runtime name
      RollingName rollingName = new RollingName(componentName, componentType);
      InstanceDescriptor descriptor = distributor.sendRolling(componentName, rollingComponent.getComponentType());
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Time for creating component " + componentName + " on instance " + distributor.getInstanceID() + " = " + (System.currentTimeMillis() - t) + " ms.");
      }
      return new RollingResult(rollingName, descriptor);
    } finally {
      unlock();
      if (LOCATION.beDebug()) {
        LOCATION.debugT(" > Total time for rolling update = " + (System.currentTimeMillis() - begin) + " ms.");
      }
    }
  }

  /**
   * Do not take the deploy lock to allow parallel sync.
   */
  public RollingResult syncInstanceWithDB(RollingName rollingName, String initiator) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.traceThrowableT(Severity.DEBUG, "Online rolling sync for component " + rollingName + " is triggered from service " + initiator, new Exception());
    }
    long t = System.currentTimeMillis();
    InstanceDescriptor descriptor = distributor.sendRolling(rollingName.getName(), rollingName.getComponentType());
    if (LOCATION.beDebug()) {
      LOCATION.debugT(" > Total time for rolling sync on instance " + distributor.getInstanceID() + " = " + (System.currentTimeMillis() - t) + " ms.");
    }
    return new RollingResult(rollingName, descriptor);
  }

  /**
   * Returns the properties event handler
   *
   * @return Returns the configuration listener responsible for updating the properties of components
   */
  public PropertiesEventHandler getPropertiesEventHandler() {
    return propertiesEventHandler;
  }

}