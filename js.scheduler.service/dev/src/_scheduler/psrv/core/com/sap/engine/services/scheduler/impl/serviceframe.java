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
package com.sap.engine.services.scheduler.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.exceptions.NameNotFoundException;
import com.sap.engine.services.scheduler.util.LTF;
import com.sap.engine.services.timeout.TimeoutListener;
import com.sap.engine.services.timeout.TimeoutManager;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventConsumer;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class ServiceFrame implements ApplicationServiceFrame {
	static final String singletonJNDIName = "SingletonScheduler";
	static final String singletonAdministratorJNDIName = "SingletonSchedulerAdministrator";
	static final String administratorJNDIName = "SchedulerAdministrator";

	private final static Location location = Location
			.getLocation(ServiceFrame.class);

	private final HashSet exportedObjectsRMI = new HashSet();

	private ServiceInterfaceDelegate delegate;
	private AdministrativeInterfaceDelegate admDelegate;

	private MutexObtainer mutexObtainer;
	
	
	private Scheduler theScheduler = null;
	private FunctionalSingleton theFunctionalSingleton;
	private AdministrativeSingleton theAdministrativeSingleton;
    
    private SingletonEnvironment m_singletonEnv = null;
	
	private ApplicationServiceContext serviceContext = null;
    
    private boolean m_isSafeModeEnabled = false; 
    
    private EventConsumer m_eventConsumer = null;
    
    private Properties m_serviceProperties = null;
    
	
	public ServiceFrame() { }

	public ApplicationServiceContext getServiceContext() {
		return serviceContext;
	}

	/**
	 * @see com.sap.engine.frame.ApplicationServiceFrame#start
	 */
	public void start(ApplicationServiceContext serviceContext)
			throws ServiceException {

        try {
            this.delegate = new ServiceInterfaceDelegate(this);
            this.admDelegate = new AdministrativeInterfaceDelegate(this);
        } catch (NamingException ne) {
            final String msgErr = "Unable to obtain InitialContext for replicated JNDI";
            if (location.beDebug())
                location.traceThrowableT(Severity.ERROR, msgErr, ne);
            throw new ServiceException(location, new LTF(msgErr), ne);
        }
        //Creates an instance of MutexObtainer. This class encapsulates the logics of locking the singleton lock
        this.mutexObtainer = new MutexObtainer();
        
        m_isSafeModeEnabled = (CoreMonitor.RUNTIME_MODE_SAFE == serviceContext.getCoreContext().getCoreMonitor().getRuntimeMode());
        
        // do not initialize the Scheduler service if there's a scheduler instance or if the Safe-Mode is enabled
		if (!toRun(serviceContext)) {
			location.infoT("Scheduler service won't be active, because there is a singleton instance.");
			return;
		}
        if (m_isSafeModeEnabled) {
            location.infoT("Scheduler service won't be active, because the Safe-Mode is enabled.");
            return;
        }
        
        if (location.beInfo()) {
  		    location.infoT("Scheduler service starting.");
        }
		this.serviceContext = serviceContext;
        
        // if the next properties are set, write a WARNING
        if (getServiceProperties().getProperty("enableJobExecution", "true").equals("false")) {
            Category.SYS_SERVER.logT(Severity.WARNING, location, "Property 'enableJobExecution' is set to false. No jobs will be executed!");
        }
        if (getServiceProperties().getProperty("removeTasksOnSingletonStart", "false").equals("true")) {
            Category.SYS_SERVER.logT(Severity.WARNING, location, "Property 'removeTasksOnSingletonStart' is set to true. All tasks will be removed at startup!");
        }
        
		//registers the service interface
		serviceContext.getContainerContext().getObjectRegistry().registerInterface(delegate);
		try {
			InitialContext notReplicatedCtx = new InitialContext();
			notReplicatedCtx.bind(administratorJNDIName, admDelegate);
		} catch (NamingException ne) {
			throw new ServiceException(location, new LTF("Unable to bind administrative interface in jndi"), ne);
		}
        
        mutexObtainer.tryToStartTheSingleton();
        
        if (!mutexObtainer.isSingleton()) {
    		//start trying to obtain the singleton mutex
    		mutexObtainer.startTrying();
        }
        
        if (location.beInfo()) {
 		    location.infoT("Scheduler service successfully initialized.");
        }
	}

	private static final String SCHEDULER_INSTANCE_TYPE = "scheduler";

	private static final String CURRENT_INSTANCE_ID = "current_instance";

	private boolean toRun(ApplicationServiceContext serviceContext) {
		CoreContext coreContext = serviceContext.getCoreContext();
		ConfigurationHandlerFactory config = coreContext
				.getConfigurationHandlerFactory();
		CommonClusterFactory factory = ClusterConfiguration
				.getClusterFactory(config);

		try {
			return (!hasSchedulerInstance(factory)
					|| isSchedulerInstance(factory) || m_isSafeModeEnabled);
		} catch (Exception e) {
			final String message = "An error occured while checking if the scheduler service should be started. Service will start.";
			Category.SYS_SERVER.logThrowableT(Severity.WARNING, location,
					 message, e);
			return true;
		}
	}

	/**
	 * Checks whether the current instance is a dedicated Scheduler instance.
	 *
	 * @param factory The factory object to access the cluster configuration
	 * @return <code>true</code> if the current instance is a dedicated SCHEDULER instance
	 * @throws ClusterConfigurationException
	 * @throws NameNotFoundException
	 */
	private boolean isSchedulerInstance(CommonClusterFactory factory)
			throws NameNotFoundException, ClusterConfigurationException {
		ConfigurationLevel instance = factory.openConfigurationLevel(
				CommonClusterFactory.LEVEL_INSTANCE, CURRENT_INSTANCE_ID);
		return (SCHEDULER_INSTANCE_TYPE.equals(instance.getInstanceType()));
	}

	/**
	 * Checks whether there is a Scheduler instance configured in the cluster.
	 *
	 * @param factory The factory object to access the cluster configuration
	 * @return <code>true</code> if there is a SCHEDULER instance configured in the cluster
	 * @throws ClusterConfigurationException
	 */
	private boolean hasSchedulerInstance(CommonClusterFactory factory)
			throws ClusterConfigurationException {
		boolean hasSchedulerInstance = false;
		String[] instances = factory
				.listIdentifiers(CommonClusterFactory.LEVEL_INSTANCE);
		for (int i = 0; i < instances.length; i++) {
			String instanceID = instances[i];
			if (CURRENT_INSTANCE_ID.equals(instanceID)) {
				continue;
			}
			ConfigurationLevel instance = factory.openConfigurationLevel(
					CommonClusterFactory.LEVEL_INSTANCE, instanceID);
			if (SCHEDULER_INSTANCE_TYPE.equals(instance.getInstanceType())) {
				hasSchedulerInstance = true;
				break;
			}
		}
		return hasSchedulerInstance;
	}

	/**
	 * Export an object to underlaying RMI layer. This method calls PortableRemoteObject.exportObject.
	 * Besides exporting the object it also adds it to the list of all RMI objects exported by the service.
	 * When stopping the service unexports all objects currently found in this list.
	 *
	 * @param toExport - object to export in RMI
	 * @throws RemoteException - thrown if PortableRemoteObject threw RemoteException
	 * @see PortableRemoteObject#exportObject(java.rmi.Remote)
	 */
	private void exportObjectRMI(Remote toExport) throws RemoteException {
		PortableRemoteObject.exportObject(toExport);
		exportedObjectsRMI.add(toExport);
	}

	/**
	 * Stops the service. This method unexports all objects currently found in the list of all exported objects.
	 * Then if this node is the singleton it unbinds the singleton from replicated JNDI.
	 *
	 * @see com.sap.engine.frame.ServiceFrame#stop()
	 */
	public void stop() throws ServiceRuntimeException {        
		//    	stop trying to obtain the lock.
		mutexObtainer.stopTrying();
		//		unexport all remote objects from RMI
		unexportAllObjectsRMI();
		//		if this node is the singleton remove the singleton scheduler from replicated jndi.
		if (mutexObtainer.isSingleton()) {
            //      stop scheduling. no new tasksk will be execution from this moment on.
            theScheduler.shutDown();
			try {
				obtainReplicatedInitialContext().unbind(singletonJNDIName);
				obtainReplicatedInitialContext().unbind(singletonAdministratorJNDIName);
			} catch (NamingException ne) {
				final String errMsg = "Unable to remove the Singleton from replicated JNDI while stopping."
						+ " This is usualy not fatal as another node, which has obtained the singleton lock"
						+ " will try to rebind this location.";
				Category.SYS_SERVER.logThrowableT(Severity.ERROR, location,
						errMsg, ne);
			}

    		//		Release the lock so other nodes can become the singleton. This is actually necessary only
    		//		if this node is stopping without stopping the whole cluster.
    		try {
    			mutexObtainer.releaseLock();
    		} catch (TechnicalLockException tle) {
    			location.traceThrowableT(Severity.ERROR, "", tle);
    		}
    		
    		try {
				// unregister also the event subscriber
    			if (m_eventConsumer != null) {
    				theRuntime().unregisterEventSubscriber(m_eventConsumer);
    			}
			} catch (SQLException e) {
				location.traceThrowableT(Severity.ERROR, "Error while unregistering the EventSubscriber", e);
			}
    		
        }
        
		try {
			new InitialContext().unbind(administratorJNDIName);
		} catch (NamingException ne) {
			location.traceThrowableT(Severity.ERROR, "Unable to unbind the administrative interface for the scheduler service", ne);
		}
		this.serviceContext = null;
		location.infoT("Scheduler adapter service stopped.");
	}

	private synchronized void becomeTheSingleton() throws Exception {
        // initialize the SingletonEnvironment
        m_singletonEnv = new SingletonEnvironment();
        m_singletonEnv.setServiceFrame(this);
        
        // register the EventSubscriber to handle scheduled tasks which have an 
        // undeployed JobDefinition
        m_eventConsumer = new EventConsumerImpl(this);
        try {
            SchedulerDefinition builtinScheduler = theRuntime().getBuiltinScheduler();
            EventSubscriber sub = new EventSubscriber(builtinScheduler.getSubscriberId(),
                                                      new String[] { Event.EVENT_JOB_DEFINITION_UNDEPLOYED1, 
                                                                     Event.EVENT_JOB_FINISHED },
                                                      m_eventConsumer);              
            theRuntime().registerEventSubscriber(sub);  
            
            theScheduler = new Scheduler(m_singletonEnv, m_eventConsumer);
            m_singletonEnv.setScheduler(theScheduler);
            
            theFunctionalSingleton = new FunctionalSingletonImpl(m_singletonEnv);
            theAdministrativeSingleton = new AdministrativeInterfaceImpl(m_singletonEnv);            

        } catch (SQLException sqle) {
            throw new ServiceException(location, new LTF("Unable to access the Builtin Scheduler."), sqle);
        }
        
		final String errMsg = "Unable to export the singleton scheduler in underlying RMI infrastructure. " +
							  "This may not be fatal as objects get automatically exported when they are looked up from jndi for the first time.";
		// export the FunctionalSingleton
		try {
			//export the singleton in to RMI
			exportObjectRMI(theFunctionalSingleton);
		} catch (RemoteException re) {
			Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, errMsg, re);
		}
		// export the AdministrativeSingleton
		try {
			//export the singleton in to RMI
			exportObjectRMI(theAdministrativeSingleton);
		} catch (RemoteException re) {
			Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, errMsg, re);
		}
              
		//if any of this threw an error it would be fatal for the singleton. Thus exception is thrown up and is cought by the mutex obtainer
		//which logs the error and releases the lock so that other node can become the singleton.
        
        // we read here all tasks which are deployed via the activated ZeroAdmin-template
        // and store them into the DB, may throw an  InconsistentReadException
        // or ConfigurationException, in both cases an error has been logged
        // in the method and the singleton initialization will fail
        //
        SchedulerTask[] allZeroAdminTasks = theScheduler.readAllTasksFromZeroAdminTemplate();
        SchedulerTask[] allHoldZeroAdminTasksDB = theScheduler.getAllSchedulerTasks(new Short(SchedulerTask.TASK_SOURCE_ZERO_ADMIN), TaskStatus.hold);
        
        // update all new ZeroAdmin-tasks to the rules below if there's a 
        // ZeroAdmin-task in the DB set to HOLD:
        // 1.) which is in compare sense equal to a new one, set the new one also to HOLD
        // 2.) otherwise cancel the old one
        // 3.) which is not in the new ones, cancel the old one
        for (int i = 0; i < allHoldZeroAdminTasksDB.length; i++) {
            for (int j = 0; j < allZeroAdminTasks.length; j++) {
                if (allHoldZeroAdminTasksDB[i].compareSchedulerTask(allZeroAdminTasks[j])) {
                    // set the same task also to hold and old description value and commit
                    ((SchedulerTaskExtension)allZeroAdminTasks[j]).setTaskStatus(allHoldZeroAdminTasksDB[i].getTaskStatus()); 
                }
            }
        }
        
        // mark the old ZeroAdmin tasks as cancelled (may throw 
        // SchedulerRuntimeException which will cause the singleton 
        // initialization to fail) and persist the new ones
        theScheduler.markCancelledForZeroAdminTasks();
        
        for (int i = 0; i < allZeroAdminTasks.length; i++) {
            Connection con = null;
            try {
                con = theScheduler.obtainConnection();
                m_singletonEnv.getTaskPersistor().persist(allZeroAdminTasks[i], null, null, con);
            } catch (SQLException sqle) {
                theScheduler.createAndLogRuntimeException(allZeroAdminTasks[i].getTaskId(), sqle);
            } finally {
                theScheduler.closeConnection(con);
            }
        }     
        
        // ------------------------------------------------------------
        // Make sure there are no stale entries in the task table
        // ------------------------------------------------------------

        theScheduler.removeStaleTasks();
        
        
        // read the content again from DB
		theScheduler.recoverFromDB();
		
		InitialContext iCtx = obtainReplicatedInitialContext();
		iCtx.rebind(singletonJNDIName, theFunctionalSingleton);
		iCtx.rebind(singletonAdministratorJNDIName, theAdministrativeSingleton);
	}

	/**
	 * Obtains an InitialContext for replicated JNDI
	 *
	 * @return an InitialContext instance for replicated JNDI
	 * @throws NamingException - thrown if a problem obtaining the InitialContext has ocurred.
	 * @see InitialContext#InitialContext(java.util.Hashtable)
	 */
	InitialContext obtainReplicatedInitialContext()
			throws NamingException {
		Properties replicatedEnv = new Properties();
		replicatedEnv.put("Replicate", "true");
		replicatedEnv.put("domain", "true");
		InitialContext ctx = new InitialContext(replicatedEnv);
		// access the environment of the InitialContext to be sure that it is fully initialized
		ctx.getEnvironment();
		
		return ctx;
	}

	/**
	 * Unexports all objects, found in the list of currently exported objects, from RMI.
	 * If an error ocurres while unexporting any object it is traced(not logged) and the
	 * rest of the objects are processed.
	 */
	private void unexportAllObjectsRMI() {
		Iterator i = exportedObjectsRMI.iterator();
		while (i.hasNext()) {
			try {
				PortableRemoteObject.unexportObject((Remote) i.next());
			} catch (NoSuchObjectException nsoe) {
				if (location.beDebug())
					location
							.traceThrowableT(
									Severity.DEBUG,
									"A remote object was found in the list"
											+ " of all remote objects but not in the underlying RMI layer.",
									nsoe);
			}
		}
	}

	/**
	 * Obtains the service name of this service as determined by the service manager
	 *
	 * @return - the service name of this service as determined by the service manager.
	 */
	public String getServiceName() {
		return serviceContext.getServiceState().getServiceName();
	}
    
    
    /**
     * Obtains the service properties deployed with this service and caches it.
     * Thus no property of the scheduler service is flagged as online modifiable. 
     * If that will be requested in future we might also introduce a listener which 
     * reloads the changed Properties-instance.   
     *
     * @return - the service properties
     */
    public Properties getServiceProperties() {
    	if (m_serviceProperties == null) {
    		m_serviceProperties = serviceContext.getContainerContext().getSystemMonitor().getService(getServiceName()).getProperties();
    	}
    	return m_serviceProperties;
    }

	/**
	 * Obtains the timeout manager.
	 *
	 * @return obtains the timeout manager.
	 */
	private TimeoutManager obtainTimeoutManager() {
		//The scheduler has a hard reference to the timeout service, hence this code will
		//never return null.
		return (TimeoutManager) serviceContext.getContainerContext()
				.getObjectRegistry().getServiceInterface("timeout");
	}

	/**
	 * Determines whether this node is currently shutting down.
	 *
	 * @return True if this node is shutting down, false otherwise
	 */
	private boolean isShuttingDown() {
		return serviceContext == null || serviceContext.getClusterContext().getClusterMonitor()
				.getCurrentParticipant().getState() == ClusterElement.STOPPING;

	}

    public void executeTask(SchedulerTask toExecute) {
    	
    }
    
    public JobExecutionRuntime theRuntime() {
		return (JobExecutionRuntime)getServiceContext()
			.getContainerContext().getObjectRegistry()
			.getServiceInterface("scheduler~runtime");
	}
    
    
    protected AdministrativeSingleton getAdministrativeSingleton() {
        return theAdministrativeSingleton;
    }
    
    protected Scheduler getScheduler() {
        return theScheduler;
    }
    
	
	/**
	 * Encapsulates the logics of obtaining the the lock. This an instance of this class
	 * registers itself both as a timeout listener and as a cluster event listener. No more
	 * then one instance of this class in state started can exist becouse a service can have
	 * a single registered clustered event listener. An instance of this class is in state
	 * started if its <code>start()</code> method has been called. When an instance goes
	 * into the started state it registeres itself as a ClusterEventListener for this service
	 * and tries to obtain the mutex on every elementLoss event. The started instnace will
	 * also try to obtain the mutex at equally long intervals in the range [40 secs - 60]secs.
	 * Timeout notifications are delivered by the TimeoutManager thus no new threads are created.
	 * If an object succeeds in obtaining the singleton lock it will stop trying to obtain it.
	 *
	 * @author Hristo Sabev (i027642)
	 */
	private class MutexObtainer implements TimeoutListener,
			ClusterEventListener {
		private static final String lockingNamespace = "$scheduler~";

		private static final String lockName = lockingNamespace + "singleton";

		private static final String lockParameter = "synchronization";

		private final int obtainAttemptPeriod;

		private ServerInternalLocking singletonLock;

		private boolean isSingleton = false;

        private boolean activelyTrying = false;
        
		/**
		 * Constructs a new MutexObtainer instance
		 */
		public MutexObtainer() {
			//			Compute arbitrary time in the interval [40 secs - 60 secs].
			obtainAttemptPeriod = new Random().nextInt(20000) + 40000;
		}

		/**
		 * This method puts the current object in state started.
		 *
		 * @throws ServiceException - thown if there is a ClusterEventListener already registered for this service.
		 */
		public void startTrying() throws ServiceException {
            if (activelyTrying) {
                // don't need to register as we are are already trying to
                // acquire the singleton
                return;
            }
			TimeoutManager timeoutManager = obtainTimeoutManager();
			timeoutManager.registerTimeoutListener(this, obtainAttemptPeriod,
					obtainAttemptPeriod, false);
			try {
				serviceContext.getServiceState().registerClusterEventListener(
						mutexObtainer);
			} catch (ListenerAlreadyRegisteredException lare) {
				final String errMsg = "Service " + getServiceName()
						+ " will not start as ContainerEventListener"
						+ " for it has already been registered.";
				if (location.beDebug())
					location.traceThrowableT(Severity.FATAL, errMsg, lare);
				throw new ServiceException(location, new LTF(errMsg), lare);
			}
            activelyTrying = true;
		}

		/**
		 * Stops this instance from trying to obtain the lock any more.
		 *
		 */
		public void stopTrying() {
            if (!activelyTrying) {
                // we haven't been trying to acquire the singleton; nothing
                // to to
                return;
            }
			obtainTimeoutManager().unregisterTimeoutListener(this);
			serviceContext.getServiceState().unregisterClusterEventListener();
            activelyTrying = false;
		}

		/**
		 * Obtains a boolean value indicating whether this node is the singleton
		 *
		 * @return True if this node is currently the singleton, otherwise false
		 */
		private synchronized boolean isSingleton() {
			return isSingleton;
		}

		public void elementJoin(ClusterElement element) {
			if (location.beDebug())
				location.debugT("Cluster element " + element
						+ "Joined to cluster");
		}

		/**
		 * Called by the cluster manager when a cluster element has detached from the cluster. When invoked
		 * this method will sleep for a 1500 ms in order to allow the lock manager to remove the local lock
		 * and will try to obtain the singleton lock. If the node is currently shutting down no attempt to
		 * obtain the lock will be made. If this method succeeded in obtaining the lock then no further
		 * attempts to obtain the lock will be made.
		 */
		public void elementLoss(ClusterElement element) {
			final int enqueueChanceTime = 1500;
			if (location.beDebug())
				location.debugT("Cluster element " + element
						+ " has detached from the" + " cluster. Sleeping "
						+ enqueueChanceTime
						+ "ms in order to allow the enqueue server to"
						+ "free the lock.");
			try {
				Thread.sleep(enqueueChanceTime);
			} catch (InterruptedException ie) {
				location
						.traceThrowableT(
								Severity.WARNING,
								"Thead was interrupted while sleeping to give chance"
										+ " to the enqueue service to free the lock."
										+ (isShuttingDown() ? "Probably this has happened becouse the server is shutting down"
												: "")
										+ " Error is ignorred and lock obtaining sequence continues.",
								ie);
			}
			tryToStartTheSingleton();
		}

		public void elementStateChanged(ClusterElement element, byte oldState) {
			if (location.beDebug())
				location.debugT("Cluster element " + element
						+ " changed its state from " + oldState);
		}

		/**
		 * Called by the Timeout Manager to see whether the timeout event should be delivered to this listener. Returns
		 * true if the node is not shutting down. Otherwise returns false.
		 *
		 * @return True if the node is not shutting down. Otherwise returns false.
		 */
		public boolean check() {
			return !isShuttingDown(); //if the server is shuttind down don't remove the timeout listener
			//simply don't execute its timeout method. This is faster and easier then removing the listener.
		}

		/**
		 * Tries to obtain the singleton lock. This method is called by the Timeout Manager when it is time
		 * to try to obtain the singleton lock again. If this method succeeded in obtaining the lock then
		 * no further attempts to obtain the lock will be made.
		 */
		public void timeout() {
			if (location.beDebug())
				location
						.debugT("Trying to obtain the singleton lock after another "
								+ obtainAttemptPeriod + "ms.");
			tryToStartTheSingleton();
		}

		/**
		 * Releases the singleton lock. If this instnace is not the owner of the lock it passes silently.
		 *
		 * @throws TechnicalLockException - if error occurred while releasing the lock.
		 */
		public void releaseLock() throws TechnicalLockException {
			getLock().unlock(lockName, lockParameter, LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE);
		}

		/**
		 * Obtains a reference to the lock in the locking manager. This method creates the singletonLock
		 * lazily.
		 *
		 * @return a reference to the singleton lock
		 * @throws TechnicalLockException - if there is a problem obtaining a reference to the lock
		 */
		private ServerInternalLocking getLock() throws TechnicalLockException {
			return singletonLock == null ? (singletonLock = serviceContext
					.getCoreContext().getLockingContext()
					.createServerInternalLocking(lockingNamespace,
							"Scheduler Singleton Synchronization Lock"))
					: singletonLock;
		}

		/**
		 * Tries to lock the singleton lock. If succeeds this method forces the scheduler to recover by reading
		 * all tasks from the database. This method encapsulates a common logics for the timeout method and for
		 * the elementLoss method.
		 */
		private synchronized void tryToStartTheSingleton() {
			if (isShuttingDown()) {
				if (location.beDebug())
					location
							.debugT("This node is currently shutting down. No attempt"
									+ " to obtain the lock will be performed");
				return;
			}
            
            // check for second initialization
            //
            if (isSingleton) {
                // get stack trace
                //
                String stack = null;
                Exception e = new Exception();

                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                stack = writer.toString();

                Category.SYS_SERVER.errorT(location, "Attempt to initialize singleton for the second time. Request for initialization came from:" + stack);
                return;
            }

            try {
				getLock().lock(lockName, lockParameter,
						LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE);
				isSingleton = true;
				stopTrying();
				location.infoT("Successfully obtained the singleton lock.");
				//Call this on the ServiceFrame and leave it to deal with recovering the scheduler
			} catch (LockException le) {
				if (location.beDebug()) {
					location.traceThrowableT(Severity.DEBUG,
							"Unable to obtain the singleton lock. Waiting for another "
									+ obtainAttemptPeriod + "ms.", le);
                }
                return;
			} catch (TechnicalLockException tle) {
				Category.SYS_SERVER
						.logThrowableT(
								Severity.ERROR,
								location,
								"An error ocurred while trying to obtain the singleton lock.",
								tle);
                return;
			}
			try {
				becomeTheSingleton();
			} catch (Throwable e) {
				final String errMsg = "Error has ocurred while trying to start the singleton on this node. Releasing the lock" +
						"so that another node can become the singleton.";
				Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, errMsg, e);
				try {
					if (isSingleton()) {
						// stop the priority queue in case this service-instance obtained the singleton
						if (theScheduler != null)
							theScheduler.shutDown();
						// release the Enqueue lock
						releaseLock();
					}										
				} catch (TechnicalLockException tle) {
					final String errMsg1 = "Unable to release the lock. Probably no other node will be able to"
							+ " become the singleton.";
					Category.SYS_SERVER.logThrowableT(Severity.FATAL, location,
							errMsg1, tle);
				}
                isSingleton = false;
                try {
                    startTrying();
                } catch (ServiceException se) {
                    Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, "",se);
                }
			}
			
		}
	} // MutexObtainer
        
    
    /**
     * Returns the information about the current cluster layout. The returned map 
     * has the following structure:
     * 
     * Key:   String clusterId
     * Value: String[] {nodeName, status, hasSingleton, lockKey}
     *   
     * @return the Map with the cluster layout info
     */
    protected Map<String, String[]> getClusterLayout() throws SchedulerRuntimeException {        
        Map<String, String[]> map = new HashMap<String, String[]>();
        
        // info about the lock key (the node qwhich holds the scheduler singleton) 
        String nodeWithLock = null;
        String lockKey = null;
        try {
            // we access the LockingAdapter to provoke the exception which gives us
            // more info which node holds the lock for the Scheduler singleton.
            // Lock mode MODE_CHECK_EXCLUSIVE_NONCUMULATIVE is only a dummy method
            // which never locks/unlocks anything, it performs only a check if 
            // the lock is set.
            mutexObtainer.getLock().lock(MutexObtainer.lockName, MutexObtainer.lockParameter, LockingConstants.MODE_CHECK_EXCLUSIVE_NONCUMULATIVE);
            // no exception was thrown what means that there's no lock set for the singleton
        } catch (LockException le) {        
            // $JL-EXC$
            lockKey = le.getInternalCollisionUserId();
            int idx = lockKey.lastIndexOf('.');
            nodeWithLock = lockKey.substring(idx+1).trim();            
        } catch (TechnicalLockException tle ) {
            String errMsg = "Error while accessing the LockingAdapter to dump out the holder of the singleton-lock.";
            throw new SchedulerRuntimeException(errMsg, tle);
        }         
        
        // dump out all nodes registered in the cluster
        ClusterElement[] nodes = serviceContext.getClusterContext().getClusterMonitor().getParticipants();

        ArrayList<ClusterElement> nodesList = new ArrayList<ClusterElement>();
        for (int i = 0; i < nodes.length; i++) {
            nodesList.add(nodes[i]);
        }
        // add also the current one
        nodesList.add(serviceContext.getClusterContext().getClusterMonitor().getCurrentParticipant());        
                
        for (int i = 0; i < nodesList.size(); i++) {
            if (nodesList.get(i).getType() == ClusterElement.SERVER) {
                String stateStr = null;
                switch(nodesList.get(i).getState()) {                    
                    case ClusterElement.INITIAL: 
                        stateStr = "INITIAL";
                        break;
                    case ClusterElement.STARTING: 
                        stateStr = "STARTING";
                        break;
                    case ClusterElement.RUNNING: 
                        stateStr = "RUNNING";
                        break;
                    case ClusterElement.STOPPING: 
                        stateStr = "STOPPING";
                        break;
                    case ClusterElement.STOPPED: 
                        stateStr = "STOPPED";
                        break;
                    case ClusterElement.WAIT_START: 
                        stateStr = "WAIT_START";
                        break;
                    case ClusterElement.WAIT_STOP: 
                        stateStr = "WAIT_STOP";
                        break;
                    case ClusterElement.APPS_STARTING: 
                        stateStr = "APPS_STARTING";
                        break;
                    default:
                        stateStr = "UNDEFINED";
                }
                
                String[] strArr = new String[4];
                String clusterId = String.valueOf(nodesList.get(i).getClusterId());
                strArr[0] = nodesList.get(i).getName();
                strArr[1] = stateStr;
                if (clusterId.equals(nodeWithLock)) {
                    strArr[2] = String.valueOf(true);    
                } else {
                    strArr[2] = String.valueOf(false);
                }           
                strArr[3] = lockKey;
                map.put(clusterId, strArr);
            }         
        } // for        
       
        return map;
    } // getClusterLayout

}