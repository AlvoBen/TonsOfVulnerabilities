/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/LockingApplicationFrame.java#60 $ SAP*/
package com.sap.engine.services.locking;

 
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.CommunicationServiceContext;
import com.sap.engine.frame.CommunicationServiceFrame;
import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.SAPLockingIllegalArgumentException;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.locking.Util;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.locking.command.DisableServerLoggingCommand;
import com.sap.engine.services.locking.command.DisableTimeStatisticsCommand;
import com.sap.engine.services.locking.command.DisplayTimeStatisticsCommand;
import com.sap.engine.services.locking.command.DisplayUniqueNumberCommand;
import com.sap.engine.services.locking.command.EnableServerLoggingCommand;
import com.sap.engine.services.locking.command.EnableTimeStatisticsCommand;
import com.sap.engine.services.locking.command.GetLocksCommand;
import com.sap.engine.services.locking.command.LockCommand;
import com.sap.engine.services.locking.command.ResetTimeStatisticsCommand;
import com.sap.engine.services.locking.command.RunTestsCommand;
import com.sap.engine.services.locking.command.UnlockAllCommand;
import com.sap.engine.services.locking.command.UnlockCommand;
import com.sap.engine.services.locking.command.UnlockCumulativeCommand;
import com.sap.engine.services.locking.itsam.SAP_ITSAMJ2eeEnqueueServerWrapper;
import com.sap.engine.services.locking.itsam.SAP_ITSAMJ2eeEnqueueServer_Impl;
import com.sap.engine.services.locking.test.AllTests;
import com.sap.engine.services.locking.test.ILockingContextTest;
import com.sap.engine.services.locking.test.TestResult;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * A simple service, which is just used to configure the LockingManager with a nice GUI.
 * The problem is, that currently it is not supported to configure managers directly.
 */
public class LockingApplicationFrame implements CommunicationServiceFrame, ApplicationServiceFrame, ContainerEventListener
{ 
  private static final Location LOCATION = Location.getLocation(LockingApplicationFrame.class);
  private static final Category CATEGORY = LoggingHelper.SYS_SERVER;
  
  private static final String JNDI_SERVICE_NAME = "naming";
  private static final String OWN_NAME = "Locking service";
  private static final String PROPERTY_TESTLEVEL  = "testlevel";
  private static final Util UTIL = new Util();
  
    
  /** The ServiceContext (given in the start-method) */
  private ServiceContext _serviceContext;

  /** The LockingContext (created in the start-method) */
  LockingContext _lockingContext;
  
  /** The ObjectRegistry (created in the start-method) */
  private ObjectRegistry _objectRegistry;
  
  /** The ThreadSystem (created in the start-method) */
  private ThreadSystem _threadSystem;

  /** The LockingRuntimeInterface (created in the start-method) */
  private LockingRuntimeInterface _lockingRuntimeInterface;
    
  /** Used for shell-commands (given in interfaceAvailable()) */  
  private ShellInterface _shellInterface;
  
  /** Used for shell-commands (created in interfaceAvailable()) */  
  private int _commandId;
  
  /** one of the own properties */  
  private int _testlevel;
    
  /** object name for itsam cim mbean */
  private ObjectName _objNameITSAM;
    
  public LockingRuntimeInterface getRuntimeInterface()
  {
    return _lockingRuntimeInterface;
  }
  
  public ThreadSystem getThreadSystem()
  {
    return _threadSystem;
  }
  
  
  // ================== interface ApplicationServiceFrame ======================
  
  
  /**
   *  Service start method of dispatcher.
   */
  public void start(CommunicationServiceContext serviceContext) throws ServiceException
  {
    String METHOD = "start(communicationServiceContext)";
    
    _serviceContext = serviceContext;

    try
    {
      _objectRegistry = serviceContext.getContainerContext().getObjectRegistry();
      _threadSystem = serviceContext.getCoreContext().getThreadSystem();
      // create runtime
      _lockingContext = serviceContext.getCoreContext().getLockingContext();
      _lockingRuntimeInterface = new LockingRuntimeInterfaceImpl(this, _lockingContext);
      startIndependent();   
    }
    catch (Exception e)
    {
      CATEGORY.fatalT(LOCATION, METHOD, Messages.$1_CAN_NOT_START, new Object[] { OWN_NAME });
      LoggingHelper.logThrowable(Severity.ERROR, CATEGORY, LOCATION, METHOD, e);
      try {
         stop(); 
      } catch (Exception x) {
        LoggingHelper.logThrowable(Severity.DEBUG, CATEGORY, LOCATION, METHOD, x);
      } // cleanup, if partly initialized
      throw new ServiceException(ServiceException.SERVICE_NOT_STARTED, new Object[] { OWN_NAME }, null);
    }
  }
  
  /**
   *  Service start method of server.
   */
  public void start(ApplicationServiceContext serviceContext) throws ServiceException
  {
    String METHOD = "start(applicationServiceContext)";
    
    _serviceContext = serviceContext;

    try
    {
      _objectRegistry = serviceContext.getContainerContext().getObjectRegistry();
      _threadSystem = serviceContext.getCoreContext().getThreadSystem();
      // create runtime
      _lockingContext = serviceContext.getCoreContext().getLockingContext();
      _lockingRuntimeInterface = new LockingRuntimeInterfaceImpl(this, _lockingContext);
      startIndependent();   
    }
    catch (Exception e)
    {
      CATEGORY.fatalT(LOCATION, METHOD, Messages.$1_CAN_NOT_START, new Object[] { OWN_NAME });
      LoggingHelper.logThrowable(Severity.ERROR, CATEGORY, LOCATION, METHOD, e);
      try { 
        stop();
      } catch (Exception x) { 
        LoggingHelper.logThrowable(Severity.DEBUG, CATEGORY, LOCATION, METHOD, x);
      } // cleanup, if partly initialized
      throw new ServiceException(ServiceException.SERVICE_NOT_STARTED, new Object[] { OWN_NAME }, null);
    }
  }
 
  /**
   * Service stop method.
   */
  public void stop() throws ServiceRuntimeException
  {
    String METHOD = "stop()";

    try
    {
      LOCATION.pathT(METHOD, "begin");
      _objectRegistry.unregisterInterface();
      _serviceContext.getServiceState().unregisterContainerEventListener();
      unsetShellInterface();
      LOCATION.pathT(METHOD, "success");
    }
    catch (Exception e)
    {
      CATEGORY.errorT(LOCATION, METHOD, Messages.$1_CAN_NOT_CLEANUP, new Object[] { OWN_NAME });
      LoggingHelper.logThrowable(Severity.ERROR, CATEGORY, LOCATION, METHOD, e);
      throw new ServiceRuntimeException(ServiceRuntimeException.PROBLEMS_WHEN_STOPPING_SERVICE, new Object[] { OWN_NAME }, null);
    }
    //Unregister the CIM Model MBean used by ITSAM.
    finally{
    	removeFromITSAM();
    }
  }

  
  // ================== interface ContainerEventListener =======================

  
  public void containerStarted() { }
  public void beginContainerStop() { }
  public void serviceNotStarted(String serviceName) { }
  public void serviceStopped(String serviceName) { }
  
  public boolean setServiceProperty(String key, String value) 
  { 
    Properties currentProperties = _serviceContext.getServiceState().getProperties();
    Properties properties = (Properties) currentProperties.clone();
    properties.put(key, value);
    return parseProperties(properties);
  }
  
  public boolean setServiceProperties(Properties serviceProperties) 
  { 
    return parseProperties(serviceProperties); 
  }


  public void interfaceAvailable(String interfaceName, Object interfaceImpl) 
  {
    String METHOD = "interfaceAvailable(interfaceName, interfaceImpl)";
    LOCATION.pathT(METHOD, "interfaceName={0}", new Object[] { interfaceName });
    if (interfaceName.equals("shell")) 
      setShellInterface((ShellInterface) interfaceImpl); 
  }

  public void interfaceNotAvailable(String interfaceName) 
  {
    String METHOD = "interfaceNotAvailable(interfaceName)";
    LOCATION.pathT(METHOD, "interfaceName={0}", new Object[] { interfaceName });
    if (interfaceName.equals("shell")) 
      unsetShellInterface(); 
  }

  public void markForShutdown(long timeout) {
  }

  // ======================== private helper-methods ===========================


  private void setShellInterface(ShellInterface shellInterface)
  {
    String METHOD = "setShellInterface(shellInterface)";
    LOCATION.pathT(METHOD, "begin");
    _shellInterface = shellInterface;
    Command shellcommands[] = new Command[] {
                new LockCommand(this),
                new UnlockCommand(this),
                new UnlockCumulativeCommand(this),
                new UnlockAllCommand(this),
                new GetLocksCommand(this),
                new DisplayTimeStatisticsCommand(this),
                new ResetTimeStatisticsCommand(this),
                new RunTestsCommand(this),
                new DisplayUniqueNumberCommand(this),
                new EnableServerLoggingCommand(this),
                new DisableServerLoggingCommand(this),
                new EnableTimeStatisticsCommand(this),
                new DisableTimeStatisticsCommand(this),
              }; 
    _commandId = _shellInterface.registerCommands(shellcommands);
    LOCATION.pathT(METHOD, "success");
 }
  
  private void unsetShellInterface()
  {
    String METHOD = "unsetShellInterface()";
    LOCATION.pathT(METHOD, "begin");
    if (_shellInterface != null)
      _shellInterface.unregisterCommands(_commandId);
    _shellInterface = null;
    LOCATION.pathT(METHOD, "success");
  }
  
  private void runTests(int testlevel) throws Exception
  {
    String METHOD = "runTests(testlevel)";
    LOCATION.pathT(METHOD, "testlevel={0}", new Object[] { new Integer(testlevel) });
    
    if (testlevel <= 0)
      return;
          
    long start = System.currentTimeMillis();
     
        
    if (testlevel >= 2)
      runLockingContextTests(AllTests.getAllLockingContextTests());
    else
      runLockingContextTests(AllTests.getFunctionalLockingContextTests());
    
    long end = System.currentTimeMillis();  
    CATEGORY.infoT(LOCATION, METHOD, Messages.TESTS_SUCCESSFUL_$1_MS, new Object[] { new Long(end - start) });
  }
  
  private void runLockingContextTests(ILockingContextTest test[]) throws Exception
  {
    String METHOD = "runLockingContextTests(test)";
    
    CATEGORY.infoT(LOCATION, METHOD, Messages.START_$1_TESTS, new Object[] { new Integer(test.length) });
    AdministrativeLocking administrativeLocking = _lockingRuntimeInterface.getAdministrativeLocking();
    for (int i = 0; i < test.length; i++)
    {
      LOCATION.pathT(METHOD, "start test {0}", new Object[] { test[i] });
      TestResult result = test[i].start(_threadSystem, administrativeLocking, null);
      if (result.getException() != null)
      {
        CATEGORY.errorT(LOCATION, METHOD, result.getLog());
        throw new TechnicalLockException(TechnicalLockException.TEST_FAILED, new Object[] { new Integer(i), test[i].getName() }, result.getException());
      }
      else 
      {
        LOCATION.pathT(METHOD, Messages.TEST_$1_SUCCESSFUL, new Object[] { test[i].getName() });
        LOCATION.pathT(METHOD, result.getLog());
      }
    }
  }
    
  private boolean parseProperties(Properties properties) throws IllegalArgumentException
  {
    String METHOD = "changeProperties(properties)";
    
    try
    {
      boolean propertiesActiveWithoutRestart = true;
      LOCATION.debugT(METHOD, "properties={0}", new Object[] { properties });
      LOCATION.pathT(METHOD, "begin");
      
      _testlevel = UTIL.getIntegerProperty(properties, PROPERTY_TESTLEVEL, 0, Integer.MAX_VALUE, false).intValue();
      
      return propertiesActiveWithoutRestart;
    }
    catch (Exception e)
    {
      IllegalArgumentException wrappedException = new SAPLockingIllegalArgumentException(SAPLockingIllegalArgumentException.CAN_NOT_SET_PROPERTIES, new Object[] { OWN_NAME, e.toString() });
      LoggingHelper.logThrowable(Severity.ERROR, CATEGORY, LOCATION, METHOD, wrappedException);
      throw wrappedException;
    }
  }

  private void startIndependent() throws Exception
  {
    String METHOD = "startIndependent()";

    // parse own properties (=> tracing becomes available)
    Properties properties = _serviceContext.getServiceState().getProperties();
    parseProperties(properties);
    LOCATION.pathT(METHOD, "begin");
    // register runtime
    // Listener for basicadmin and jmx services is added
    int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE | ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE | //shell
    			ContainerEventListener.MASK_SERVICE_STARTED | ContainerEventListener.MASK_BEGIN_SERVICE_STOP; //basicadmin and jmx services
    Set names = new HashSet(3);
    names.add("shell");
    names.add("basicadmin");
    names.add("jmx");
    _serviceContext.getServiceState().registerContainerEventListener(mask, names, this);
    _objectRegistry.registerInterface(_lockingRuntimeInterface);
    _serviceContext.getServiceState().registerManagementInterface(_lockingRuntimeInterface);
    LOCATION.pathT(METHOD, "registered in server");
  }  
  
  // Register CIM Model MBean (SAP_ITSAMJ2eeEnqueueServerWrapper), 
  // used by ITSAM. True for Rio or latest engine's versions.
  // All related ITSAM classes are generated automatically by java code generator plug-in.
  // All methods declared in this MBean has the same signature as methods declared in LockingRuntimeInterface.
  // We are passing previously created LockingRuntimeInterface as a parameter.
  // Each method of this MBean calls corresponding method from the runtime interface,
  private void addToITSAM(){
  	
  	String METHOD = "addToITSAM()";
    try
    {
    	LOCATION.pathT(METHOD, "Begin");
    	
    	MBeanServer mbs = (MBeanServer)_objectRegistry.getServiceInterface("jmx");
    	
    	ObjectName objName = getObjectNameForITSAM();
    	
    	if(!mbs.isRegistered(objName)){
	        // Create an instance by using runtime interface as a paremeter
	        SAP_ITSAMJ2eeEnqueueServer_Impl mbeanImpl = new SAP_ITSAMJ2eeEnqueueServer_Impl(_lockingRuntimeInterface);
	        
	        // Now we register the ITSAM MBean, that resides on each node.           
	        mbs.registerMBean(new SAP_ITSAMJ2eeEnqueueServerWrapper(mbeanImpl), objName);
	        
	        LOCATION.pathT(METHOD, "Registered in MBean Server");
    	}
    	else{
    		LOCATION.pathT(METHOD, "Already registered in MBean Server");
    	}
    } 
    catch (Exception ex){
    	LoggingHelper.logThrowable(Severity.ERROR, CATEGORY, LOCATION, METHOD, ex);
    }    
    finally{
    	LOCATION.pathT(METHOD, "End");
    }
  }
  
  //Unregister MBean
  private void removeFromITSAM(){
  	String METHOD = "removeFromITSAM()";

    LOCATION.pathT(METHOD, "Begin");
    	
    try{
    	//Due to naming could be already down, use service framework, instead of naming service
    	//MBeanServer mbs = (MBeanServer) new InitialContext().lookup("jmx");
    	MBeanServer mbs = (MBeanServer)_objectRegistry.getServiceInterface("jmx");
    	mbs.unregisterMBean(getObjectNameForITSAM());
    	LOCATION.pathT(METHOD, "Unregistered from MBean Server");
    }
    catch(Exception ex){
    	LOCATION.debugT("Unregistering MBean: SAP_ITSAMJ2eeEnqueueServer failed, due to: " + ex.getMessage());
    }
    finally{
    	LOCATION.pathT(METHOD, "End");
    }
  }
  
  //Create Object name.
  private ObjectName getObjectNameForITSAM() throws Exception{  	
  	if(_objNameITSAM==null){
  		
	  	String simClass = "SAP_ITSAMJ2eeEnqueueServer"; 
	  	String simParentClass = "SAP_ITSAMJ2eeCluster";
	  	
	  	String clusterNameKey = simParentClass + ".Name";
	  	String clusterCreationClassKey = simParentClass + ".CreationClassName";
	  	
	  	ManagementModelManager mmm = (ManagementModelManager)_objectRegistry.getServiceInterface("basicadmin");
	  	ObjectName onCluster = mmm.getManagementModelHelper().getSAP_ITSAMJ2eeClusterObjectName();
	  	
	  	String clusterNameValue = onCluster.getKeyProperty(clusterNameKey);
	  	String clusterCreationClassValue = onCluster.getKeyProperty(clusterCreationClassKey);
	  		  	
	  	String pattern = ":cimclass=" + simClass + "," + 
	  			"version=1.0," + 
	  			"type=" + simParentClass + "." + simClass + "," + 
	  			clusterNameKey + "=" + clusterNameValue + "," + 
	  			clusterCreationClassKey + "=" + clusterCreationClassValue + "," + 
	  			simClass + ".ElementName=" + simClass;
	  	
	  	_objNameITSAM = new ObjectName(pattern);
  	}
  	return _objNameITSAM;
  }
  
  public void serviceStarted(String serviceName, Object serviceInterface) {
  	String METHOD = "serviceStarted(serviceName, serviceInterface)";
    LOCATION.pathT(METHOD, "serviceName={0}", new Object[] { serviceName });
    if (serviceName.equals("basicadmin")){ 
      addToITSAM();
    }
  }
  
  public void beginServiceStop(String serviceName) {
  	String METHOD = "beginServiceStop(serviceName)";
    LOCATION.pathT(METHOD, "serviceName={0}", new Object[] { serviceName });
    if (serviceName.equals("jmx")){ 
      removeFromITSAM(); 
    }
  }
}
