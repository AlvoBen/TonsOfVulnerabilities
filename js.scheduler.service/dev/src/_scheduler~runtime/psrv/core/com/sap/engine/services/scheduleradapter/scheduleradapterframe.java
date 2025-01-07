/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduleradapter;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ejb.CreateException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.deploy.DeployCallbackImpl;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.JobExecutionRuntimeImpl;
import com.sap.engine.services.scheduler.runtime.RuntimeIntfAccessAsserter;
import com.sap.engine.services.scheduler.runtime.cluster.ClusterCommunication;
import com.sap.engine.services.scheduler.runtime.db.DBHandler;
import com.sap.engine.services.scheduler.runtime.db.EventPersistor;
import com.sap.engine.services.scheduler.runtime.db.JobDefinitionHandler;
import com.sap.engine.services.scheduler.runtime.db.JobQueryHandler;
import com.sap.engine.services.scheduler.runtime.db.LogHandler;
import com.sap.engine.services.scheduler.runtime.db.SchedulerCache;
import com.sap.engine.services.scheduler.runtime.db.SchedulerManagementHandler;
import com.sap.engine.services.scheduler.runtime.event.EventManager;
import com.sap.engine.services.scheduler.runtime.jxbp.SchedulerManager;
import com.sap.engine.services.scheduler.runtime.logging.JobLoggingManager;
import com.sap.engine.services.scheduler.runtime.mbean.SAP_ITSAMJavaSchedulerWrapper;
import com.sap.engine.services.scheduler.runtime.mbean.SAP_ITSAMJavaScheduler_Impl2;
import com.sap.engine.services.scheduler.runtime.mdb.MDBJobExecutor;
import com.sap.engine.services.scheduler.runtime.mdb.MDBJobRuntimeManager;
import com.sap.engine.services.scheduleradapter.command.SchedulerCheckCommand;
import com.sap.engine.services.scheduleradapter.command.SchedulerInfoCommand;
import com.sap.engine.services.scheduleradapter.command.SchedulerJMSCommand;
import com.sap.engine.services.scheduleradapter.command.SchedulerJobCommand;
import com.sap.engine.services.scheduleradapter.command.SchedulerTaskCommand;
import com.sap.engine.services.scheduleradapter.jobdeploy.ConfigurationParser;
import com.sap.engine.services.scheduleradapter.jobdeploy.SchedulerContainerImpl;
import com.sap.engine.services.scheduleradapter.repository.JobRepository;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.scheduler.api.SchedulerAdministrator;
import com.sap.scheduler.runtime.JobDefinitionType;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.ShellInterface;


/**
 * This class is used by the service framework in order to 
 * start the scheduler adapter service. 
 * 
 * @author Dirk Marwinski
 */
public class SchedulerAdapterFrame extends ContainerEventListenerAdapter implements ApplicationServiceFrame, ClusterEventListener {

	/**
	 * Initialization of the location for SAP logging.
	 */  
	private final static Location location = Location.getLocation(SchedulerAdapterFrame.class);

	/**
	 * Initialization of the category for SAP logging.
	 */  
    private final static Category category = LoggingHelper.SYS_SERVER;

    public static final String SERVICE_NAME = "scheduler~runtime";

	/**
	 * The service context which was used to initialize
	 * this service
	 */
    private ApplicationServiceContext mServiceContext = null;
    
	/**
	 * Environment holding all relevant information for this
	 * service. Will be initialized while start of thi service.
	 */
    private Environment mEnvironment = null;
    
	/**
	 * Deploy callback for receiving deploy events
	 */
	private DeployCallbackImpl mDeployCallback = null;

    private ContainerManagement mContainerManagement = null;
    
    private DeployCommunicator mDeployCommunicator;
    
    private MDBJobRuntimeManager mMDBRuntime;
    
    private ObjectName mMBeanName = null;
    /** 
     * Used for shell-commands (given in interfaceAvailable()) 
     */
    private ShellInterface m_shellInterface = null;  
    
    /** 
     * Used for shell-commands (created in interfaceAvailable()) 
     */
    private int m_commandId;
    
	/**
	 * @see com.sap.engine.frame.ApplicationServiceFrame#start
	 */
    public void start(ApplicationServiceContext serviceContext) throws ServiceException {
        mEnvironment = new Environment();
        mEnvironment.setSchedulerCache(new SchedulerCache());
        
        // check if the system is running in safe mode and store it into the Environment
        boolean safeMode = (CoreMonitor.RUNTIME_MODE_SAFE == serviceContext.getCoreContext().getCoreMonitor().getRuntimeMode());
        mEnvironment.setSafeMode(safeMode);
        
        // ------------------------------------------------------------------
        // General initialization
        // ------------------------------------------------------------------

        mServiceContext = serviceContext;
        mEnvironment.setServiceContext(serviceContext);

        // retrieve cluster information
        //
        int clusterId = serviceContext.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();
        mEnvironment.setClusterId(clusterId);
        String nodeName = serviceContext.getClusterContext().getClusterMonitor().getCurrentParticipant().getName();
        mEnvironment.setNodeName(nodeName);
        String clusterName = serviceContext.getClusterContext().getClusterMonitor().getClusterName();
        mEnvironment.setClusterName(clusterName);
        
        // read configuration for this service (may throw a service exception
        // when misconfigured)
        //
        readConfiguration(serviceContext);

        // ------------------------------------------------------------------
        // Initialize scheduler runtime (on all nodes)
        // ------------------------------------------------------------------
        
        initializeSchedulerRuntime();
                
        // ------------------------------------------------------------------
        // Initialize scheduler runtime container
        // ------------------------------------------------------------------
        
        int mask = ContainerEventListener.MASK_SERVICE_STARTED | ContainerEventListener.MASK_INTERFACE_AVAILABLE | ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE;

        Set<String> names = new HashSet<String>();
        names.add("container");
        names.add("ejbmonitor");
        names.add("deploy");
        names.add("shell");
        names.add("scheduler"); // register also for the singleton, which registers the SchedulerAdministrator to register the MBean
        serviceContext.getServiceState().registerContainerEventListener(mask, names, this);
    }

    /**
     * Read service configuration for this service
     */
    private void readConfiguration(ApplicationServiceContext ctx) 
                                             throws ServiceException {
        
        ServiceMonitor mon = ctx.getContainerContext().getSystemMonitor().getService(SERVICE_NAME);
        Properties props = mon.getProperties();
                
        if (location.beDebug()) {
            location.debugT("Read the following properties from configuration: " + props.toString());
        }
    }
    
    public void stop() throws ServiceRuntimeException {

        stopSchedulerRuntime();
        
		location.infoT("Scheduler runtime service stopped.");
    }
    
    private void initializeSchedulerRuntime() 
                              throws ServiceException
    {

        // initialize job repository
        //
        JobRepository repos = new JobRepository(mEnvironment);
        mEnvironment.setJobRepository(repos);

        // initialize database logger
        //
        initializeDB();
        
        // make sure default values are deployed in database
        //
        initializeTables();
        
        // initialize logging manager for jobs
        //
        JobLoggingManager mgr = new JobLoggingManager(mEnvironment);
        mEnvironment.setJobLoggingManager(mgr);

        // event handler
        //
        EventManager eventManager = new EventManager(mEnvironment);
        mEnvironment.setEventManager(eventManager);

        // job execution runtime
        //
        JobExecutionRuntime rt = new RuntimeIntfAccessAsserter(new JobExecutionRuntimeImpl(mEnvironment));
        mEnvironment.setJobExecutionRuntime(rt);

        // scheduler manager
        //
        SchedulerManager smgr = new SchedulerManager(mEnvironment);
        mEnvironment.setSchedulerManager(smgr);
        
        // initialize MDB Job runtime
        //        
        initializeMDBJobRuntime();
        
        // create configuration parser
        //
        ConfigurationParser parser = new ConfigurationParser(mEnvironment);
        mEnvironment.setConfigurationParser(parser);
        
        // register in JNDI, service startup fails if this fails
        try {
            registerSchedulerRuntimeAPI();
        } catch (NamingException ne) {
            category.logThrowableT(Severity.ERROR, location, SERVICE_NAME + " is unable to register scheduler API in JNDI.", ne);
            // cleanup
            unregisterSchedulerRuntimeAPI();
            throw new ServiceException(location, new LocalizableTextFormatter(SchedulerAdapterResourceAccessor.getResourceAccessor(),
                                                                    SchedulerAdapterResourceAccessor.UNABLE_TO_REGISTER_IN_JNDI, 
                                                                    new Object[] {SERVICE_NAME}), ne);
        }
        //register service interface in ojbect regestry. Once registered in the object regestry this interface will be
        //accessible from JNDI for applications that declared service-component reference to this service.
        mEnvironment.getServiceContext().getContainerContext()
                .getObjectRegistry().registerInterface(mEnvironment.getJobExecutionRuntime());

        // register cluster communication module for this service
        //
        ClusterCommunication comm = new ClusterCommunication();
        comm.init(mEnvironment, mEnvironment.getServiceContext().getClusterContext().getMessageContext());
        mEnvironment.setClusterCommunication(comm);
        
        // clean up dangling jobs
        //
        DBHandler db = mEnvironment.getDBHandler();
        try {
            db.cleanup(mEnvironment.getNodeName());
        } catch (SQLException sql) {
            category.logThrowableT(Severity.ERROR, location, "Unable to check for \"unknown\" jobs.", sql);
        }
    }
    
    private void initializeMDBJobRuntime()
                                  throws ServiceException {
     
        // Initialize MDB job runtime
        //
        mMDBRuntime = new MDBJobRuntimeManager(mEnvironment, mEnvironment.getJobExecutionRuntime());

        // initialize MDB job trigger
        //
        MDBJobExecutor trigger = new MDBJobExecutor(mEnvironment);
        mEnvironment.addJobExecutor(trigger, JobDefinitionType.MDB_JOB_DEFINITION);        
    }
    
    private void stopSchedulerRuntime() {

        // unregister from container
        //
        mContainerManagement.unregisterContainer(SchedulerContainerImpl.CONTAINER_NAME);
        
        // unregister management inteface
        mEnvironment.getServiceContext().getServiceState().unregisterManagementInterface();
        
        //remove service interface
        mEnvironment.getServiceContext().getContainerContext()
                .getObjectRegistry().unregisterInterface();

        // unregister references in JNDI
        //
        unregisterSchedulerRuntimeAPI();
        
        // unregister cluster event listener
        //
        mEnvironment.getClusterCommuniction().close();
        
        // unregister MBean
        unregisterMBeanFromMBeanServer();
        // unregister Shell
        unsetShellInterface();
    }
    
                              

    /**
     * This method registers the scheduler API with the naming service.
     */
    private void registerSchedulerRuntimeAPI() 
                                 throws NamingException {
        
        
        Context rootCtx = new InitialContext();
        Context schedulerCtx;
        try {
            schedulerCtx = rootCtx.createSubcontext(Environment.SCHEDULER_CONTEXT_JNDI_NAME);
        } catch (NameAlreadyBoundException nab) {
            // that exception is ok, another node has created the subcontext 
            // already, just get a reference to it now
            schedulerCtx = (Context)rootCtx.lookup(Environment.SCHEDULER_CONTEXT_JNDI_NAME);
        }
        schedulerCtx.bind(Environment.SCHEDULER_BINDING_JOB_EXECUTION_RUNTIME, mEnvironment.getJobExecutionRuntime());
        
    }

	/**
	 * This method unregisters the scheduler API with the naming service.
	 *
	 */
	private void unregisterSchedulerRuntimeAPI() {
      
        try {
    		Context rootCtx = new InitialContext();
    		Context schedulerCtx = (Context)rootCtx.lookup(Environment.SCHEDULER_CONTEXT_JNDI_NAME);

    		schedulerCtx.unbind(Environment.SCHEDULER_BINDING_JXBP_NAME);
    		schedulerCtx.unbind(Environment.SCHEDULER_BINDING_JOB_EXECUTION_RUNTIME);
            schedulerCtx.close();
    		rootCtx.destroySubcontext(Environment.SCHEDULER_CONTEXT_JNDI_NAME);
        } catch(NamingException ne) {
            location.traceThrowableT(Severity.WARNING, "Unable to unregister scheduler runtime API from JNDI.", ne);
        }
	}

	private void initializeDB() throws ServiceException {
        LogHandler lh = new LogHandler(mEnvironment);
        mEnvironment.setLogHandler(lh);
		
		DBHandler dbhd = new DBHandler(mEnvironment);
		mEnvironment.setDBHandler(dbhd);
        
        JobDefinitionHandler jdh = new JobDefinitionHandler(mEnvironment);
        mEnvironment.setJobDefinitionHandler(jdh);
        
        JobQueryHandler qh = new JobQueryHandler(mEnvironment);
        mEnvironment.setJobQueryHandler(qh);
        
        SchedulerManagementHandler sm = new SchedulerManagementHandler(mEnvironment);
        mEnvironment.setSchedulerManagementHandler(sm);
        
        EventPersistor ep = new EventPersistor(mEnvironment);
        mEnvironment.setEventPersistor(ep);
	}

    
    private void initializeTables() 
                        throws ServiceException {
        
        DBHandler db = mEnvironment.getDBHandler();
        try {
            db.initializeTables();
        } catch (SQLException sql) {
            throw new ServiceException(location, new LocalizableTextFormatter(SchedulerAdapterResourceAccessor.getResourceAccessor(),
                    SchedulerAdapterResourceAccessor.UNABLE_TO_INIT_TABLE_DEFAULT), 
                    sql);
        }
    }
    
	public void interfaceAvailable(String interfaceName, 
                                   Object interfaceImpl) {
    
        location.debugT("--- Interface available: " + interfaceName);
        if ("container".equals(interfaceName)) {
            if (location.beDebug()) {
                location.debugT("container interface is available. Registering scheduler container.");
            }
            mContainerManagement = (ContainerManagement)interfaceImpl;
            try {
                SchedulerContainerImpl ct = new SchedulerContainerImpl(mServiceContext, mEnvironment);
                mDeployCommunicator = mContainerManagement.registerContainer(SchedulerContainerImpl.CONTAINER_NAME, ct);
            } catch (ServiceException e) {
                category.logThrowableT(Severity.FATAL, location, "Unable to start service " + SERVICE_NAME, e);
            }
        } else if ("shell".equals(interfaceName)) {
            setShellInterface((ShellInterface) interfaceImpl);
        }            
	}
    
    public void interfaceNotAvailable(String interfaceName) {
      if ("shell".equals(interfaceName)) {
        unsetShellInterface();
      }
    }

    
    public void serviceStarted(String serviceName, Object serviceInterface) {
        if ("scheduler".equals(serviceName)) {
            if ( !mEnvironment.isSafeModeEnabled() ) {
                // register MBean when the singleton service is started
                registerMBeanInMBeanServer();
            } else {
                location.infoT("Instance is running in Safe-Mode, thus the Scheduler-MBean will not be registered into the MBeanServer");
            }
        }
    }


	public void containerStarted() {

		location.logT(Severity.ERROR, "Container now started.");

	}

	/**
	 * This method is invoked when a new cluster node attaches to the cluster.
	 *
	 * @param element the element that attaches to the cluster.
	 */
	public void elementJoin(ClusterElement element) {
	}

	/**
	 * This method is invoked when a new cluster node detaches the cluster.
	 *
	 * @param element the element that detaches the cluster.
	 */
	public void elementLoss(ClusterElement element) {
    
	}

	/**
	 * This method is invoked when a cluster participant changes its current
	 * running state.
	 *
	 * @param element  the element that changes its current running state (this
	 *                 instance of <code>ClusterElement</code> contains the new
	 *                 state).
	 * @param oldState the old state of the cluster node.
	 */
	public void elementStateChanged(ClusterElement element, byte oldState) {
	}

    
    /**
     * Sets the Shell interface
     * 
     * @param shellInterface the Shell intreface to register
     */
    private void setShellInterface(ShellInterface shellInterface) {
      m_shellInterface = shellInterface;
      Command shellCommands[] = new Command[] {
          new SchedulerInfoCommand(),
          new SchedulerTaskCommand(mEnvironment),
          new SchedulerJobCommand(mEnvironment),
          new SchedulerJMSCommand(),
          new SchedulerCheckCommand(mEnvironment)
      };
      m_commandId = m_shellInterface.registerCommands(shellCommands);
    }
    
    /**
     * Unsets the Shell interface
     */
    private void unsetShellInterface() {
      if (m_shellInterface != null) {
        m_shellInterface.unregisterCommands(m_commandId);
      }
      m_shellInterface = null;
    }
    
    
    /**
     * Register MBean into the MBeanServer 
     */ 
    private void registerMBeanInMBeanServer() {
      String METHOD = "registerMBeanInMBeanServer()";
      try {
          MBeanServer mbs = (MBeanServer) mServiceContext.getContainerContext().getObjectRegistry().getServiceInterface("jmx");
          ObjectName javaSchedulerObjName = getObjectNameForMBeanServer();

          if (!mbs.isRegistered(javaSchedulerObjName)) {
              JobExecutionRuntime jert = mEnvironment.getJobExecutionRuntime();
              
              final String schedulerAdministratorJNDIName = "SchedulerAdministrator";
              SchedulerAdministrator administrator = null;
              try {
                  InitialContext ictx = new InitialContext();
                  administrator = (SchedulerAdministrator)ictx.lookup(schedulerAdministratorJNDIName);
              } catch (NamingException ne) {
                  throw new CreateException("Unable to obtain SchedulerAdministrator interface from jndi location: " + schedulerAdministratorJNDIName);
              }          
              
              
              // Note, that we register the subclass SAP_ITSAMJavaScheduler_Impl2
              SAP_ITSAMJavaSchedulerWrapper javaSchedulerWrapper = new SAP_ITSAMJavaSchedulerWrapper(new SAP_ITSAMJavaScheduler_Impl2(jert, administrator, mEnvironment));
              mbs.registerMBean(javaSchedulerWrapper, javaSchedulerObjName);

              location.pathT(METHOD, "MBean registered successfully into MBeanServer");
          } else {
              location.pathT(METHOD, "MBean already registered in MBeanServer");
          }
      } catch (Exception e) {
          category.logThrowableT(Severity.ERROR, location, "Error while registering MBean in MBeanServer", e);
      } 
    }

      
    /**
     * Unregister MBean from MBeanServer 
     */ 
    private void unregisterMBeanFromMBeanServer() {
        String METHOD = "unregisterMBeanFromMBeanServer()";
        try {
            MBeanServer mbs = (MBeanServer)mServiceContext.getContainerContext().getObjectRegistry().getServiceInterface("jmx");
            mbs.unregisterMBean(getObjectNameForMBeanServer());
            location.pathT(METHOD, "MBean unregistered successfully from MBeanServer");
        } catch (Exception e) { 
            // $JL-EXC$
            category.logT(Severity.WARNING, location, "Error while unregistering MBean in MBeanServer: "+e.getMessage());
        } 
    }
      
      
    private ObjectName getObjectNameForMBeanServer() throws Exception {
        if (mMBeanName == null) {
            Properties sysProperties = System.getProperties();
            String sid = sysProperties.getProperty("SAPSYSTEMNAME");
            String dbhost = sysProperties.getProperty("j2ee.dbhost");
            String clusterName = sid + ".SystemHome." + dbhost;
    
            mMBeanName = new ObjectName("com.sap.default:version=1.0,"+
                                         "cimclass=SAP_ITSAMJavaScheduler,"+
                                         "type=SAP_ITSAMJ2eeCluster.SAP_ITSAMJavaScheduler,"+
                                         "SAP_ITSAMJavaScheduler.Name=JavaScheduler,"+
                                         "SAP_ITSAMJavaScheduler.CreationClassName=SAP_ITSAMJavaScheduler,"+
                                         "SAP_ITSAMJavaScheduler.SystemName=" + clusterName + ","+
                                         "SAP_ITSAMJavaScheduler.SystemCreationClassName=SAP_ITSAMJ2eeCluster,"+
                                         "SAP_ITSAMJ2eeCluster.CreationClassName=SAP_ITSAMJ2eeCluster,"+
                                         "SAP_ITSAMJ2eeCluster.Name=" + clusterName);   
        }
        return mMBeanName;
    }
    
}
