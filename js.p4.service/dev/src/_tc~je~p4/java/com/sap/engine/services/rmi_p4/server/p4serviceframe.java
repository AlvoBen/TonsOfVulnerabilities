package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceConfigurationException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.ResourceAccessor;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.cross.CrossObjectBroker;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.rmi_p4.exception.P4ResourceAccessor;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.monitor.P4RuntimeControl;
import com.sap.engine.services.rmi_p4.monitor.P4RuntimeControlInterface;
import com.sap.engine.services.rmi_p4.server.command.*;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.services.rmi_p4.jmx.model.*;
import com.sap.engine.services.rmi_p4.lite.P4Lite;
import com.sap.engine.services.rmi_p4.lite.P4LiteImpl;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sap.bc.proj.jstartup.sadm.ShmAccessPoint;
import com.sap.bc.proj.jstartup.sadm.ShmException;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class P4ServiceFrame extends P4ContainerEventListenerImpl implements ApplicationServiceFrame, ContainerEventListener {

  public static final String INSTANTIATE_LOCAL_STUBS = "INSTANTIATE_LOCAL_STUBS";
  public static final String INSTANTIATE_GENERATED_LOCAL_STUBS = "INSTANTIATE_GENERATED_LOCAL_STUBS";
  public static final String SKIP_CLASS_WRAPPERS ="SKIP_CLASS_WRAPPERS";
  public static final String GENERATE_STUBS = "generateStubs";
  public static final String BROKER_ID = "brokerId";
  public static final String USE_STREAM_HOOKS = "useStreamHooks";
  public static final String P4_START_TRIES = "p4StartTries";
  public static final String P4_LITE = "_P4Lite_";
  public static final String WORKER_THREADS = "parallelRequests";
  public static final String REQUEST_QUEUE_SIZE = "requestQueueSize";
  public static final String ENABLE_P4_ACCOUNTING = "ENABLE_P4_ACCOUNTING";
  public static final String USE_LOCAL_CALL_OPTIMIZATION = "USE_LOCAL_CALL_OPTIMIZATION";
  

  public static P4ServiceFrame frame = null;

  public static Location location = null;
  public static Category category = null;

  /**
   * **** PROPERTIES***************
   */
  protected static int repeatTime = -1;
  protected static boolean instantiateLocalStubs = true;
  protected boolean generateStubs = true;
  protected static int threads = 10;
  protected static int requestQueueSize = 100;
  protected static CrossInterface crossInterface = null;
  protected static int retries = 240;

  /* P4 Lite - Used for P4 Lite Client*/
  private P4Lite p4LiteObject = null;
  private P4RemoteObject p4RemoteObj = null;

  public P4SessionProcessor sessionProcessor;

  private ApplicationServiceContext applicationServiceContext = null;
  private static P4RuntimeControl managementInterface = null;


  private MBeanServer mbs;
  private ObjectName p4ITSAMObjectName;
  private ObjectName p4ITSAMServerMBean;

  /** 
   * Start P4 service
   */
  public void start(ApplicationServiceContext _baseContext) throws ServiceException {
    frame = this;
    
    /* Create location and category for p4 service*/
    if (P4ResourceAccessor.location == null) {
      location = Location.getLocation(P4ResourceAccessor.LOCATION_PATH);
      P4ResourceAccessor.location = location;
    }
    if (P4ResourceAccessor.category == null) {
      category = Category.getCategory(P4ResourceAccessor.CATEGORY);
      P4ResourceAccessor.category = category;
    }
    if (category == null) {
      System.out.println("ERROR:P4 Service - Cannot instantiate Category and p4 service cannot log"); //$JL-SYS_OUT_ERR$
    }
    if (location == null) {
      System.out.println("ERROR:P4 Service - Cannot instantiate Location and p4 service cannot trace"); //$JL-SYS_OUT_ERR$
    }
    
    Properties properties = _baseContext.getServiceState().getProperties();
    String retriesProperty = System.getProperty(P4_START_TRIES, properties.getProperty(P4_START_TRIES));
    try {
      if (retriesProperty != null) {
        retries = Integer.valueOf(retriesProperty);
        if (retries < 20) retries = 20; //Minimum 10 seconds in retry connection to the ICM.
      }
    }catch (NumberFormatException nfe){//$JL-EXC$
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.start()", "Value of property \"{0}\" is not a numeric value. Using the default value: {1}. Check the configuration of this property", "ASJ.rmip4.cf1021", new Object[] {P4_START_TRIES,retries} );
       }
    }
    
    /* Get access points (port) from ICM */
    ThreadWrapper.pushTask("P4 service start", ThreadWrapper.TS_PROCESSING);
    ThreadWrapper.pushSubtask("Waiting for ICM access points", ThreadWrapper.TS_PROCESSING);
    int counter = 0; // for backward compatibility
  try { // For assuring pop sub task. I aim minimal differences in code.
    boolean profilesAvailabe = false;
    while (!profilesAvailabe && counter < retries) {
        int delay = 500;//delay for the next try in case the access point are not available. [ms]
        try {
          ShmAccessPoint[] pid_p4 = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_P4);
          if (pid_p4 != null && pid_p4.length > 0) {
            break; // we have some profiles available
          }
          // maybe we have only ssl port configured ...
          ShmAccessPoint[] pid_p4ssl = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_P4S);
          if (pid_p4ssl != null && pid_p4ssl.length > 0) {
            break; // we have some profiles available
          }
          if (counter<=10){ // this is one of the first 10 loops in this 'while'
            P4Logger.trace(P4Logger.WARNING, "P4ServiceFrame.start()", "RMI-P4 startup was delayed; could not get access point from ICM. Possible reasons are: \r\n" +
            		"\t1. The ICM has not started yet \r\n" +
            		"\t2. Default slot for RMI-P4 was replaced by another configuration, and there is not correct RMI-P4 port configuration in system profile \r\n" +
            		" RMI-P4 core service could not start without opened access point in ICM. RMI-P4 will try {0} times again to get access point from ICM in the next {1} seconds", "ASJ.rmip4.rt1039", new Object[]{retries-counter, ((retries-counter)*delay/1000)});                
          } else { // other loops
            SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, P4Logger.getLocation(), "ASJ.rmip4.cf0001", "RMI-P4 startup was delayed; could not get access point from ICM. Possible reasons are: \r\n" +
            		"\t1. The ICM has not started yet \r\n" +
            		"\t2. Default slot for RMI-P4 was replaced by another configuration, and there is not correct RMI-P4 port configuration in system profile \r\n" +
            		" RMI-P4 core service could not start without opened access point in ICM. RMI-P4 will try {0} times again to get access point from ICM in the next {1} seconds", new Object[]{retries-counter, ((retries-counter)*delay/1000)});
          }

          ++counter; //could not get the port yet another time
          Thread.sleep(delay);
        } catch (ShmException shmex) {
          if (P4Logger.getLocation().beError()) {
           P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.start()", "Check if RMI-P4 port is configured correctly in the system profile. Failed to get the ICM profiles: {0}", "ASJ.rmip4.cf1022", new Object[]{P4Logger.exceptionTrace(shmex) });            	  
          }
        } catch (InterruptedException ie) {
          throw new ServiceException("Thread interrupted while waiting for the access points information to become available");
        }
    }
  } finally {
    ThreadWrapper.popSubtask();
    if (counter >= retries) {//10 seconds by default
      SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, P4Logger.getLocation(), "ASJ.rmip4.cf0008", "RMI-P4 port is not configured in system profile or configuration is wrong; RMI-P4 core service could not start without opened access point in ICM. Check port configuration and check if ICM is started");
      if (P4Logger.getLocation().beError()) {
//        StringBuilder message = new StringBuilder("P4 core service could not start without opened access point in ICM");
//        message.append("\r\n").append("P4 port is not configured in system profile or configuration is wrong; Check ICM port configuration for P4 protocol");
//        message.append("\r\n").append("You should not configure non P4 protocol at slot 1 in system profile. Configuration in system profile like: ");
//        message.append("\r\n").append("icm/server_port_1 = PROT=<not_P4_protocol>, PORT=<port>, TIMEOUT=<timeout>");
//        message.append("\r\n").append("Will remove default p4 port configuration. In case of such configuration it have to configure ports for other protocols additionally");
//        message.append("\r\n").append("Server will be halted because - P4 core service could not start because of wrong port configuration");
        //P4Logger.getLocation().errorT("P4ServiceFrame.start()", message.toString());
        P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.start()", "RMI-P4 core service failed to start. No access point to ICM is configured or ICM is not started yet. Check ICM port configuration for RMI-P4 protocol", "ASJ.rmip4.cf1023");            	  
      }
      //This pop is just in case of wrong configuration. For normal startup, popTask is later.
      ThreadWrapper.popTask();

      String exitMessageKey = "p4_0031";//"P4 core service could not start because of missing or wrong port configuration";
      ResourceAccessor resourceAccessor = P4ResourceAccessor.getResourceAccessor();
      LocalizableTextFormatter localizableText = new LocalizableTextFormatter(resourceAccessor, exitMessageKey);
      throw new ServiceConfigurationException(P4Logger.getLocation(), localizableText);
    }
  }
    /* Initialize P4 objects */
  try { // For assuring pop task
    P4ObjectBrokerServerImpl broker = new P4ObjectBrokerServerImpl(_baseContext);
    sessionProcessor = new P4SessionProcessor(_baseContext);
    broker.sessionProcessor = sessionProcessor;
    
    /* Read and initialize P4 service properties */
    initProperties(properties);

    try {
      /* Register ContainerEventListener for services: 
       * "shell" - for telnet and 
       * "basicadmin" - for administration;
       */ 
      this.applicationServiceContext = _baseContext;
      int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE | ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE | ContainerEventListener.MASK_SERVICE_STARTED | ContainerEventListener.MASK_BEGIN_SERVICE_STOP;
      Set<String> names = new HashSet<String>(2);
      names.add("basicadmin");
      names.add("shell");
      applicationServiceContext.getServiceState().registerContainerEventListener(mask, names, this);
      crossInterface = (CrossInterface) applicationServiceContext.getContainerContext().getObjectRegistry().getProvidedInterface(CROSS_INTERFACE_NAME);
      
      //String enableNAT = properties.getProperty(P4ObjectBroker.ENABLE_NAT);
      //if (enableNAT != null && enableNAT.equals("false")) 
      P4ObjectBroker.enableNAT = false;
      p4Provider = new P4Provider(sessionProcessor, generateStubs);
      crossInterface.registerProtocolProvider(p4Provider);
      
      managementInterface = new P4RuntimeControl();
      applicationServiceContext.getServiceState().registerManagementInterface(managementInterface);
      applicationServiceContext.getContainerContext().getObjectRegistry().registerInterface(managementInterface);

      /* P4 Lite - Used for P4 Lite Client*/
      if (P4ObjectBroker.runP4Lite) {
        p4LiteObject = new P4LiteImpl();
        p4RemoteObj = P4ObjectBroker.init().loadObject(p4LiteObject);
        P4ObjectBroker.init().setInitialObject(P4_LITE, p4RemoteObj);
      }
    } catch (Exception ex) {
      throw new ServiceException("Unexpected exception while p4 service is trying to start", ex);
    }
  } finally {
    ThreadWrapper.popTask();
  }
  }

  /** 
   * Stop P4 service
   */
  public void stop() {
    sessionProcessor.stop();
    applicationServiceContext.getServiceState().unregisterContainerEventListener();
    CrossObjectBroker.unregisterP4ProtocolProvider();
    frame = null;
    crossInterface = null;
    instantiateLocalStubs = true;
    P4ObjectBroker.isP4Stopped = true;
  }

  /** 
   * Read and initialize P4 service properties 
   */
  private void initProperties(Properties properties) {
    String trueValue = "true";
    String falseValue = "false";
    generateStubs = properties.getProperty(GENERATE_STUBS, trueValue).equalsIgnoreCase(trueValue);
    instantiateLocalStubs = properties.getProperty(INSTANTIATE_LOCAL_STUBS, trueValue).equalsIgnoreCase(trueValue);
    P4ObjectBrokerServerImpl.setEnabledStreamHooks(properties.getProperty(USE_STREAM_HOOKS, falseValue).equalsIgnoreCase(trueValue));
    /* P4 Lite - Used for P4 Lite Client*/
    P4ObjectBroker.runP4Lite = properties.getProperty(P4ObjectBroker.P4_LITE_PROPS, falseValue).equalsIgnoreCase(trueValue);
    try {
      threads = Integer.parseInt(properties.getProperty(WORKER_THREADS));
      if(threads <= 1) { 
        // 1 is also not valid value. If there is only one worker thread it will put all incoming messages to queue until it is full
        threads = 10;
        if (P4Logger.getLocation().beError()) {
          P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.initProperties(Properties)", "Invalid value of property \"{0}\". Default value: {1} will be used", "ASJ.rmip4.cf1024", new Object []{WORKER_THREADS, threads});            	  
        }
      }
    } catch (NumberFormatException e) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.initProperties(Properties)", "Invalid or missing value of property \"{0}\". Default value: {1} will be used. Exception: {2}", "ASJ.rmip4.cf1025", new Object []{WORKER_THREADS, threads, e.toString()});            	  
      }
    }

    try{
      requestQueueSize = Integer.parseInt(properties.getProperty(REQUEST_QUEUE_SIZE));
      if (requestQueueSize < 1) {
        requestQueueSize = 100;
        if (P4Logger.getLocation().beError()) {
          P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.initProperties(Properties)", "Invalid value of property \"{0}\". Default value: {1} will be used" , "ASJ.rmip4.cf1026", new Object []{REQUEST_QUEUE_SIZE, requestQueueSize});
        }
      }
    } catch (NumberFormatException e) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.initProperties(Properties)", "Invalid or missing value of property \"{0}\" Default value: {1} will be used. Exception: {2}", "ASJ.rmip4.cf1027", new Object []{REQUEST_QUEUE_SIZE, requestQueueSize, e.toString()});
      }
    }

    System.setProperty("SKIP_CLASS_WRAPPERS", properties.getProperty("SKIP_CLASS_WRAPPERS", falseValue));
    
    //Check for accounting service integration switch
    if (properties.getProperty(ENABLE_P4_ACCOUNTING, falseValue).equalsIgnoreCase(trueValue)) {
      P4ObjectBroker.setAccountingFlag(true);
    }
    
    //Check for cache for generated and dynamic skeletons, not to search for generated skeletons every time.  
    if (properties.getProperty(P4ObjectBroker.CACHE_GENERATED_SKEL, falseValue).equalsIgnoreCase(trueValue)) {
      P4ObjectBroker.setSkeletonClassCacheFlag(true);
    }
    
    //Check for service property that forbids searching of generated local stubs and always works with proxies  
    if (properties.getProperty(INSTANTIATE_GENERATED_LOCAL_STUBS, trueValue).equalsIgnoreCase(falseValue)) {
      P4ObjectBrokerServerImpl.setInstantiateGeneratedLocalStubs(false);
    }
    
    //Local invocation without serialization disabled
    if (properties.getProperty(USE_LOCAL_CALL_OPTIMIZATION, trueValue).equalsIgnoreCase(falseValue)) {
        P4ObjectBroker.setLocalCallOptimization(false);
     }
  }

  /**
   * Set Service Property
   * @param key - key for service property. One of: "INSTANTIATE_LOCAL_STUBS" or "generateStubs"
   * @param value - value for this service property.
   * @return true if the property was successfully set; 
   *         false if the property was not set.
   */
  public boolean setServiceProperty(String key, String value) {
    String trueValue = "true";
    if ((key != null) && (value != null)) {
      if (key.equalsIgnoreCase(INSTANTIATE_LOCAL_STUBS)) {
        instantiateLocalStubs = value.equalsIgnoreCase(trueValue);
        return true;
      } 
      if (key.equals(GENERATE_STUBS)) {
        this.generateStubs = value.equalsIgnoreCase(trueValue);
        p4Provider.generateStubs = this.generateStubs;
        return true;
      } //Ignore also key = BROKER_ID
      return false;
    } else {
      return false;
    }
  }

  /**
   * Re-initialize P4 service properties, and set "Generate Stubs" value to P4 provider.
   */
  public boolean setServiceProperties(Properties props) {
    initProperties(props);
    p4Provider.generateStubs = this.generateStubs;
    return true;
  }

  /**
   * Implementation of method from interface ContainerEventListener
   * Register MBean monitoring when "basicadmin" service is started.
   */
  public void serviceStarted(String serviceName, Object serviceInterface) {
    if (serviceName.equals("basicadmin")) {
      registerITSAMMBean();
    }
  }

  /**
   * Implementation of method from interface ContainerEventListener
   * Register P4 telnet commands in shell when shell is available. 
   */
  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    if (interfaceName.equals("shell")) {
      Command cmds[] = {new ListCallsInfo(), new ListP4Objects(), new ListProfiles(sessionProcessor.organizer), new P4Information(), new Connect(), new AccountingServiceManager()};
      ((ShellInterface) interfaceImpl).registerCommands(cmds);
    }
  }

  /**
   * Implementation of method from interface ContainerEventListener
   * Unregister MBean monitoring when "basicadmin" service is stopping.
   */
  public void beginServiceStop(String s) {
    if (s.equals("basicadmin")) {
      unregisterITSAMMBean();
    }
  }
  public static boolean  localStubsAllowed(){
    return instantiateLocalStubs;
  }

  public static int getThreadCount(){
    return threads;
  }

  public static int getRequestQueueSize(){
    return requestQueueSize;
  }

  /*********************************************************/
  /*******************     private methods     **************/
  /*********************************************************/
  private void registerITSAMMBean() {
    try {
    	mbs = (MBeanServer) applicationServiceContext.getContainerContext().getObjectRegistry().getServiceInterface("jmx");

    	p4ITSAMServerMBean = getObjectNameForServerMBean();
    	P4ManagementMBean p4MBean = new P4Management(sessionProcessor);
    	mbs.registerMBean(p4MBean, p4ITSAMServerMBean);

    	p4ITSAMObjectName = getObjectNameForITSAM();
    	SAP_ITSAMP4ManagementService mBean = new SAP_ITSAMP4ManagementService_Impl(mbs);
		SAP_ITSAMP4ManagementServiceWrapper wrapper = new SAP_ITSAMP4ManagementServiceWrapper(mBean);
		mbs.registerMBean(wrapper, p4ITSAMObjectName);
    } catch (Exception ex) {
    	if (P4Logger.getLocation().beError()) {
    	  P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.registerITSAMMBean()", "ITSAMMBean registration failed. MbeanServer or p4ITSAMObjectName are invalid. Exception: {0}", "ASJ.rmip4.rt1028", new Object []{ex.toString()});
      }
    }
  }

  private void unregisterITSAMMBean() {
    try {
    	mbs.unregisterMBean(p4ITSAMObjectName);
    	mbs.unregisterMBean(p4ITSAMServerMBean);
    } catch (Exception ex) {
    	if (P4Logger.getLocation().beError()) {
    	  P4Logger.trace(P4Logger.ERROR, "P4ServiceFrame.unregisterITSAMMBean()", "ITSAMMBean unregistration failed. MbeanServer or p4ITSAMObjectName are invalid. Exception: {0}", "ASJ.rmip4.rt1029", new Object []{ex.toString()});
    	}
    }
  }

  private ObjectName getObjectNameForITSAM() throws Exception{
  	String simClass = "SAP_ITSAMP4ManagementService";
    String simParentClass = "SAP_ITSAMJ2eeCluster";
    String clusterNameKey = simParentClass + ".Name";
    String clusterCreationClassKey = simParentClass + ".CreationClassName";

    ObjectName patternON = new ObjectName(":type=SAP_ITSAMJ2eeCluster,cimclass=SAP_ITSAMJ2eeCluster,*");
    Set names =  mbs.queryNames(patternON, null);

    String clusterNameValue = null;
    String clusterCreationClassValue = null;
    if (names.size() > 0) {
    	ObjectName j2eeClusterON = (ObjectName) names.iterator().next();
      clusterNameValue = j2eeClusterON.getKeyProperty(clusterNameKey);
      clusterCreationClassValue = j2eeClusterON.getKeyProperty(clusterCreationClassKey);
    }



    String pattern = ":cimclass=" + simClass + "," +
	                 "version=1.0," +
                     "type=" + simParentClass + "." + simClass + "," +
                     clusterNameKey + "=" + clusterNameValue + "," +
                     clusterCreationClassKey + "=" + clusterCreationClassValue + "," +
                     simClass + ".ElementName=" + simClass;

    return (new ObjectName(pattern));
  }

  private ObjectName getObjectNameForServerMBean() throws Exception {
	  String simClass = "SAP_ITSAMP4ManagementServicePerNode";
	  String simClusterClass = "SAP_ITSAMJ2eeCluster";
	  String simInstanceClass = "SAP_ITSAMJ2eeInstance";
	  String simNodeClass = "SAP_ITSAMJ2eeNode";

	  String clusterNameKey = simClusterClass + ".Name";
	  String clusterCreationClassKey = simClusterClass + ".CreationClassName";

	  String instanceNameKey = simInstanceClass + ".Name";
	  String instanceCreationClassKey = simInstanceClass + ".CreationClassName";
	  String instanceIDKey = simInstanceClass + ".J2eeInstanceID";

	  String nodeNameKey = simNodeClass + ".Name";
	  String nodeCreationClassKey = simNodeClass + ".CreationClassName";

	  String j2eeClusterNode = "SAP_J2EEClusterNode";

	  //get values for cluster part
	  ObjectName patternON = new ObjectName(":type=SAP_ITSAMJ2eeCluster,cimclass=SAP_ITSAMJ2eeCluster,*");
	  Set result =  mbs.queryNames(patternON, null);
    String clusterNameValue = null;
    if (result.size() > 0) {
	   	ObjectName j2eeClusterON = (ObjectName) result.iterator().next();
      clusterNameValue = j2eeClusterON.getKeyProperty(clusterNameKey);
    }

	  //String clusterCreationClassValue = j2eeClusterON.getKeyProperty(clusterCreationClassKey);

//	 find local J2eeNode MBean
	  String instanceIDValue = null;
	  String instanceNameValue = null;
	  String instanceCreationClassValue = null;
	  String nodeNameValue = null;
	  String nodeCreationClassValue = null;


		ObjectName localJ2eeNode = null;
		ObjectName localJ2eeNodePattern = new ObjectName("*:type=SAP_ITSAMJ2eeCluster.SAP_ITSAMJ2eeInstance.SAP_ITSAMJ2eeNode,SAP_J2EEClusterNode=\"\",*");
		result = mbs.queryNames(localJ2eeNodePattern, null);
		if (result.size()>0) {
			localJ2eeNode = (ObjectName) result.iterator().next();
			nodeNameValue = localJ2eeNode.getKeyProperty("SAP_ITSAMJ2eeNode.Name");
			nodeCreationClassValue = localJ2eeNode.getKeyProperty("SAP_ITSAMJ2eeNode.CreationClassName");

			// get local J2eeNode parent - local J2eeInstance MBean
			ObjectName j2eeInstances[] = (ObjectName[]) mbs.getAttribute(localJ2eeNode, "SAP_ITSAMJ2eeInstanceJ2eeNodeGroupComponent");
			if (j2eeInstances!=null && j2eeInstances.length>0) {
				ObjectName localJ2eeInstance = j2eeInstances[0];
				instanceNameValue = localJ2eeInstance.getKeyProperty("SAP_ITSAMJ2eeInstance.Name");
				instanceIDValue = localJ2eeInstance.getKeyProperty("SAP_ITSAMJ2eeInstance.J2eeInstanceID");
				instanceCreationClassValue = localJ2eeInstance.getKeyProperty("SAP_ITSAMJ2eeInstance.CreationClassName");
			}
		}

	  String pattern = ":cimclass=" + simClass + "," +
	  				   "version=1.0," +
	  				 //type=SAP_ITSAMJ2eeCluster.SAP_ITSAMJ2eeInstance.SAP_ITSAMJ2eeNode.SAP_ITSAMP4ManagementServicePerNode
	  				   "type=" + simClusterClass + "." + simInstanceClass + "." + simNodeClass + ".SAP_ITSAMP4ManagementServicePerNode," +

	  				   clusterNameKey + "=" + clusterNameValue + "," +
	  				   clusterCreationClassKey + "=" + simClass + "," +

	  				   instanceNameKey + "=" + instanceNameValue + "," +
	  				   instanceIDKey + "=" + instanceIDValue + "," +
	  				   instanceCreationClassKey + "=" + instanceCreationClassValue + "," +

	  				   nodeNameKey + "=" + nodeNameValue + "," +
	  				   nodeCreationClassKey + "=" + nodeCreationClassValue + "," +

	  				   simClass + ".ElementName=" + simClass + "," +

	  				   j2eeClusterNode + "=" + nodeNameValue;


	  return (new ObjectName(pattern));
   }

   public static P4RuntimeControlInterface getP4RuntimeControl() {
       return managementInterface;
   }
}

