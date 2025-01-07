/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MultipleAnswer;
import com.sap.engine.frame.container.ApplicationContainerContext;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.InterfaceMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.interfaces.connector.ComponentExecutionContext;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.cross.CrossObjectFactory;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.lib.util.ArrayInt;
import com.sap.engine.services.jndi.cache.CacheCommunicatorImpl;
import com.sap.engine.services.jndi.cache.ReplicationCounter;
import com.sap.engine.services.jndi.cluster.SecurityBase;
import com.sap.engine.services.jndi.persistent.JNDILogger;
import com.sap.engine.services.jndi.persistent.JNDIResourceAccessor;
import com.sap.engine.services.jndi.persistent.RemoteSerializator;
import com.sap.engine.services.jndi.persistent.SerializatorFactory;
import com.sap.engine.services.jndi.persistent.exceptions.RuntimeExceptionConstants;
import com.sap.engine.services.jndi.providerimpl.DefaultResolver;
import com.sap.engine.services.jndi.shellcmd.ServCLUtils;
import com.sap.engine.system.naming.provider.ResolverManager;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;


/**
 * JNDI Service Frame
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class JNDIFrame implements ApplicationServiceFrame {

    private static ComponentExecutionContext appContext = null;
    /**
     * Cache communicator to use
     */
    private static CacheCommunicatorImpl cacheCommunicator;
    /**
     * Naming manager to use
     */
    private static NamingManager naming = null;
    /**
     * Name used for setting initial object
     */
    private static final String NAMING_SERVICE_NAME = "naming";
    /**
     * Protocol used .... -> this to beread as property ?
     */
    public static String PROTOCOL_USED = "p4";
    /**
     * Service Context instance
     */
    private static ApplicationServiceContext sc = null;
    /**
     * Service's ContainerContext instance
     */
    public static ApplicationContainerContext containerContext = null;
    /**
     * Service's ThreadSystem instance
     */
    public static ThreadSystem threadSystem = null;
    /**
     * CrossInterface instance
     */
    public static CrossInterface crossInterface = null;
    /**
     * CoreContext instance
     */
    public static CoreContext coreContext = null;

    public static AuthenticationContext loginContext = null;

    public static SecurityContext namingSecurityContext = null;

    /**
     * Location instance
     */
    private final static Location LOG_LOCATION = Location.getLocation(JNDIFrame.class);
    public static Location location = Location.getLocation(JNDIResourceAccessor.LOCATION_PATH);
    public static Location debugLocation = Location.getLocation(JNDIFrame.class);
    /**
     * Category instance
     */
    public static Category category = Category.SYS_SERVER;
    /**
     * JNDILogger instance
     */
    public static JNDILogger log = new JNDILogger(location, category);
    /**
     * JNDIContainerEventListener instance
     */
    private JNDIContainerEventListener containerEventListener = null;
    /**
     * Constant used in synchronization of replication proccess
     */
    final static int CLUSTER_LOCK = 13;
    final static int CLUSTER_UNLOCK = 14;

    private String debugString = null;

    private DefaultResolver resolver = null;

    /**
     * When the server is started or by will of an administrator, The ServiceManager
     * call's this method to start the Service. Also the state of service is set
     * to STARTING and stay, until the thread running the method is released.
     *
     * @param _sc ServiceContext passed by ServiceManager
     */
    public void start(ApplicationServiceContext _sc) throws ServiceException {
        this.sc = _sc;

        //ClientContext clientContext = null;
        JNDIResourceAccessor.init(category, location);
        cacheCommunicator = new CacheCommunicatorImpl(sc);
        try {
            if (RemoteSerializator.serializatorFactory == null) {
                Class factory = Class.forName("com.sap.engine.services.jndi.persistent.ServerSerializatorFactory");
                RemoteSerializator.serializatorFactory = (SerializatorFactory) factory.newInstance();
            }

            // obtain containerContext
            containerContext = _sc.getContainerContext();
            containerEventListener = new JNDIContainerEventListener(containerContext.getSystemMonitor().getService("naming").getProperties());
            int mask = ContainerEventListener.MASK_BEGIN_SERVICE_STOP |
                    ContainerEventListener.MASK_SERVICE_STARTED |
                    ContainerEventListener.MASK_INTERFACE_AVAILABLE |
                    ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE;
            Set names = new HashSet();
            names.add(JNDIContainerEventListener.SHELL_INTERFACE);
            names.add(JNDIContainerEventListener.SECURITY_INTERFACE);
            names.add(JNDIContainerEventListener.APPCONTEXT_INTERFACE);
            names.add(JNDIContainerEventListener.JMX_SERVICE);
            names.add(JNDIContainerEventListener.BASICADMIN_SERVICE);

            _sc.getServiceState().registerContainerEventListener(mask, names, containerEventListener);
            //clientContext = containerContext.getClientContext();
            coreContext = _sc.getCoreContext();
            threadSystem = coreContext.getThreadSystem();
            try {
                Constants.lockTrials = Integer.parseInt(containerContext.getSystemMonitor().getService("naming").getProperty("LockTrials"));

                if (Constants.lockTrials < 1) {
                    Constants.lockTrials = 1;
                }
            } catch (Exception e) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
                Constants.lockTrials = 3;
            }

            // trying to find a property for the max size of the replication messages, this property is not set by default
            String temp = null;
            try {
                temp = containerContext.getSystemMonitor().getService("naming").getProperty("ReplicationMessageSize");

                if (temp != null) {
                    Constants.REPLICATION_MESSAGE_SIZE = Integer.parseInt(temp);
                }
            } catch (Exception e) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
                temp = null;
            }

            // trying to find a property for enablind/disabling the default root lookup from applications
            temp = null;
            try {
                temp = containerContext.getSystemMonitor().getService("naming").getProperty("ApplicationRootLookupEnabled");
                if (temp != null) {
                    Constants.APPLICATION_ROOT_LOOKUP_ENABLED = (temp.equalsIgnoreCase("false") ? false : true);
                }
            } catch (Exception e) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
            }

//    trying to find a property for enablind/disabling the old behaviour of rebind operation over non serializable objects
            temp = null;
            try {
                temp = containerContext.getSystemMonitor().getService("naming").getProperty("NonSerializableRebindOldBehaviour");
                if (temp != null) {
                    Constants.NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR = (temp.equalsIgnoreCase("false") ? false : true);
                }
            } catch (Exception e) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
            }

//    trying to find a property for enablind/disabling the old behaviour of destroyContext operation over context which contains non serializable objects
            temp = null;
            try {
                temp = containerContext.getSystemMonitor().getService("naming").getProperty("DestroyContextOldBehaviour");
                if (temp != null) {
                    Constants.DESTROY_CONTEXT_OLD_BEHAVIOUR = (temp.equalsIgnoreCase("false") ? false : true);
                }
            } catch (Exception e) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
            }

            JNDIProxyImpl proxy;
            // call init of JNDIManager to initialize the static field manager in NamingManager
            JNDIManager.init();
            naming = NamingManager.getNamingManager();
            if (debugLocation.beDebug()) {
                debugString = "Got NamingManager[" + naming + "] hashcode[" + naming.hashCode() + "] classloader[" + naming.getClass().getClassLoader() + "] ";
            }
            // set Service Manager in JNDIManager
            ((JNDIManager) naming).setContainerContext(containerContext);
            ((JNDIManager) naming).setCommunicator((CacheCommunicatorImpl) cacheCommunicator);
            // initialize Naming Structure, do the running job
            // Also Initialize Offset Naming Table
            proxy = (JNDIProxyImpl) naming.start();
            if (debugLocation.beDebug()) {
                debugString += "Got JNDIProxy[" + proxy + "] hashcode[" + proxy.hashCode() + "] classloader[" + proxy.getClass().getClassLoader() + "] ";
                debugLocation.debugT(debugString);
            }
            // set Initial Naming proxy
            crossInterface = (CrossInterface) containerContext.getObjectRegistry().getProvidedInterface("cross");
            crossInterface.setInitialObject(NAMING_SERVICE_NAME, proxy);
            // register CrossObjectFactory instance
            CrossObjectFactory objectFactoryImpl = ((JNDIManager) naming).getObjectFactory();
            crossInterface.registerObjectFactory(Constants.OBJECT_FACTORY_REGISTRATION_NAME, objectFactoryImpl);

            // ...
            initializeFactoryBuidler();
            //      testJNDI(); // call this for fast jndi tests when some changes are made
            //      Thread jnditest = new TestJNDI(((Security)(containerContext.getClientContext().getServiceInterface("security"))));
            //      jnditest.start();
            // Binding service references
            // ...
            bindReferences();
            // register JNDI's runtime interface - PermissionAdministrator
            //sc.getOwnContext().registerRuntimeMonitor(getRuntimeInterface());
            ManagementInterface mi = getManagementInterface();
            if (mi == null) {
                if (LOG_LOCATION.beWarning()) {
                  SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000045", "Cannot register ManagementInterface for the JNDI Registry Service");
                }
            }
            _sc.getServiceState().registerManagementInterface(mi);
            // register JNDI ProviderCommunicationContext
            //      CommunicationContext cctx = sc.getCommunicationContext();
            //      cctx.registerProvider(new ProviderCommunicationContextImpl(sc.getClusterContext()));
            //clientContext.registerInterface(this);
            //logContext = sc.getContainerContext().getLogContext();
            //logId = logContext.register("jndi", null);
            performClusterLock();
            //registerReferenceLoaders(_sc.getCoreContext().getLoadContext());
            _sc.getContainerContext().getObjectRegistry().registerInterfaceProvider("naming", null);
        } catch (javax.naming.NamingException ne) { // Fail to start Naming
          SimpleLogger.trace(Severity.ERROR,LOG_LOCATION, "ASJ.jndi.000034", "JNDI System Exception * Failed to start the JNDI Registry Service. Reason: [{0}]",  new Object[] { ne.toString()});
            if (LOG_LOCATION.bePath()) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "JNDI System Exception * Failed to start the JNDI Registry Service.", ne);
            }
            LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), RuntimeExceptionConstants.FAIL_TO_START_NAMING, null);
            ServiceException se = new ServiceException(formater, ne);
            if (LOG_LOCATION.bePath()) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "JNDI System Exception * Failed to start the JNDI Registry Service.", ne);
            }
            throw se;
        } catch (Exception e) {
          SimpleLogger.trace(Severity.ERROR,LOG_LOCATION, "ASJ.jndi.000035", "JNDI System Exception * Failed to start the JNDI Registry Service. Reason: [{0}]",  new Object[] { e.toString()});
            if (LOG_LOCATION.bePath()) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "JNDI System Exception * Failed to start the JNDI Registry Service.", e);
            }
            LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), RuntimeExceptionConstants.FAIL_TO_START_NAMING, null);
            ServiceException se = new ServiceException(formater, e);
            if (LOG_LOCATION.bePath()) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "JNDI System Exception * Failed to start the JNDI Registry Service.", se);
            }
            throw se;
        }
    }

    /**
     * When the server is shutting down or by will of an Administrrator, this method is
     * called by the ServiceManager.The service must free all resources. When the thread
     * running the method is released from the method, the service is removed from memory
     * and it's classloader is destroyed.
     */
    public synchronized void stop() {
        try {
            sc.getContainerContext().getObjectRegistry().unregisterInterfaceProvider("naming");
            this.naming.stop();
            naming = null;
            SecurityBase.getServerInfo().close();
            JNDIProxyImpl.db = null;
            JNDIProxyImpl.rootObject = null;
            JNDIProxyImpl.jndiRootContainer = null;
            ServCLUtils.ctx = null;
            sc.getServiceState().unregisterContainerEventListener();
            sc.getClusterContext().getMessageContext().unregisterListener();
            sc.getServiceState().unregisterManagementInterface();
            containerEventListener.unregisterResources();
            sc.getServiceState().unregisterServiceEventListener();
            crossInterface.removeInitialObject(NAMING_SERVICE_NAME);
            if(LOG_LOCATION.beInfo()) {
            	LOG_LOCATION.infoT("JNDI Registry service was successfully stopped.");
            }

            try {
                ResolverManager resolverManager = (ResolverManager) ResolverManager.getInstance();
                resolverManager.unregisterResolver(resolver);
                resolverManager.disableLogging();
            } catch (Exception e) {
                if (LOG_LOCATION.bePath()) {
                	LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
                }
            }
        } catch (Exception e) {
            if (LOG_LOCATION.beInfo()) {
            	LOG_LOCATION.traceThrowableT(Severity.INFO, "Failed to stop the JNDI Registry Service.", e);
            }
        }
    }

    /**
     * Used to get a new instance of JNDI's management interface
     */
    public ManagementInterface getManagementInterface() {
        try {
            return new PermissionAdministrator();
        } catch (RemoteException e) {
            if (LOG_LOCATION.bePath()) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "Cannot instantiate PermissionAdministrator.", e);
            }
            return null;
        }
    }

    /**
     * Static method for obtaining jndi service CoreContext
     *
     * @return CoreContext
     */
    protected static ApplicationContainerContext getContainerContext() {
        return containerContext;
    }

    /**
     * Change properties method implementation
     *
     * @param properties Properties
     * @return boolean
     * @throws IllegalArgumentException
     */
    public boolean changeProperties(Properties properties) throws IllegalArgumentException {
        // Jndi has not runtime changeable properties
        return false;
    }

    /**
     * Bind references to services
     */
    private void bindReferences() {
        try {
            Context jndi = new InitialContext();
            ServiceMonitor[] names = null;
            InterfaceMonitor[] interfaceNames = null;

            while (names == null || interfaceNames == null) {
                names = containerContext.getSystemMonitor().getServices();
                interfaceNames = containerContext.getSystemMonitor().getInterfaces();
                Thread.currentThread().yield();
            }

            ServiceReferenceImpl[] refs = new ServiceReferenceImpl[names.length];
            ServiceReferenceImpl[] interfaceRefs = new ServiceReferenceImpl[interfaceNames.length];
            try {
                for (int i = 0; i < refs.length; i++) {
                    refs[i] = new ServiceReferenceImpl(names[i].getComponentName());
                }
                for (int i = 0; i < interfaceRefs.length; i++) {
                    interfaceRefs[i] = new ServiceReferenceImpl(interfaceNames[i].getComponentName(), false);
                }
            } catch (java.rmi.RemoteException re) {
                if (LOG_LOCATION.beWarning()) {
                  SimpleLogger.traceThrowable(Severity.WARNING,LOG_LOCATION,re, "ASJ.jndi.000025", "RemoteException while binding service references. Exception: [{0}]. Result: some or all services will not be available for lookup in the naming system",  new Object[] { re.toString()});
                }
            }

            String serviceName = "";
            for (int i = 0; i < refs.length; i++) {
                try {
                    serviceName = refs[i].getServiceName();
                    jndi.rebind(serviceName, refs[i]);
                } catch (Exception e) {
                    if (LOG_LOCATION.beWarning()) {
                      SimpleLogger.traceThrowable(Severity.WARNING,LOG_LOCATION,e, "ASJ.jndi.000026", "Failed to bind service reference for service [{0}]. Exception: [{1}]. Result: this service will not be available for lookup in the naming system",  new Object[] { serviceName,e.toString()});
                    }
                }
            }
            jndi = jndi.createSubcontext("interfaces");
            for (int i = 0; i < interfaceRefs.length; i++) {
                String name = null;
                try {
                    name = interfaceRefs[i].getServiceName();
                    jndi.rebind(name, interfaceRefs[i]);
                    for (int k = 0; k < InterfaceMonitor.INTERFACE_NAMES.length; k++) {
                        if (InterfaceMonitor.INTERFACE_NAMES[k].equals(name)) {
                            jndi.rebind(InterfaceMonitor.INTERFACE_NAMES_API[k], interfaceRefs[i]);
                            break;
                        }
                    }
                } catch (Exception e) {
                    if (LOG_LOCATION.beWarning()) {
                      SimpleLogger.traceThrowable(Severity.WARNING,LOG_LOCATION,e, "ASJ.jndi.000027", "Failed to bind interface reference for interface [{0}]. Exception: [{1}]. Result: this interface will not be available for lookup in the naming system",  new Object[] { name,e.toString()});
                    }
                }
            }
        } catch (javax.naming.NamingException e) {
            if (LOG_LOCATION.beWarning()) {
              SimpleLogger.traceThrowable(Severity.WARNING,LOG_LOCATION,e, "ASJ.jndi.000028", "RemoteException while binding service references. Exception: [{0}]. Result: some or all services will not be available for lookup in the naming system",  new Object[] { e.toString()});
            }
        }
    }

    public static ClassLoader getClassLoader(String name) {
        return coreContext.getLoadContext().getClassLoader(name);
    }

    /**
     * Gets the name of a specified loader.
     *
     * @param loader Class loader instance, which name has to be found
     */
    public static String getLoaderName(ClassLoader loader) {
        String loaderName = null;
        if (coreContext != null) {
            loaderName = coreContext.getLoadContext().getName(loader);
        }
        return loaderName;
    }


    // ... move to Service Container
    public void initializeFactoryBuidler() {
        if (!javax.naming.spi.NamingManager.hasInitialContextFactoryBuilder()) {
            // set it for the first time
            try {
                ResolverManager.init();
            } catch (Exception e) {
                if (LOG_LOCATION.beWarning()) {
                  SimpleLogger.traceThrowable(Severity.WARNING,LOG_LOCATION,e, "ASJ.jndi.000029", "Error while configuring the JNDI proxy");
                }
            }
        }
        // Setting InitialContextFactory
        resolver = new DefaultResolver();
        resolver.addInitialContextFactoryImpl(new InitialContextFactoryImpl());
        resolver.addInitialContextFactoryImpl(new InitialReplicatingContextFactoryImpl());
        resolver.addObjectFactoryImpl(new AppclientObjectFactory());
        resolver.addObjectFactoryImpl(new ComponentObjectFactory());
        // register resolver
        try {
            ResolverManager resolverManager = (ResolverManager) ResolverManager.getInstance();
            try {
                resolverManager.setLogger(log);
            } catch (Exception e) {
               	LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
            }
            resolverManager.registerResolver(resolver);
        } catch (javax.naming.NamingException e) {
            if (LOG_LOCATION.bePath()) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "Error while configuring the JNDI proxy.", e);
            }
        }
    }

    protected static boolean isAppContextAvailable() {
        return (appContext != null);
    }

    protected static void setAppContextProvider(ComponentExecutionContext appContext) {
        JNDIFrame.appContext = appContext;
    }

    protected static ComponentExecutionContext getAppContextProvider() {
        return appContext;
    }

    static void initializeSecurity() throws ServiceException {
        SecurityContext newSecurityContext = (SecurityContext) sc.getContainerContext().getObjectRegistry().getServiceInterface("security");
//    if (!SecurityBase.WITHOUT_SECURITY) {
        boolean success = false;
        Exception exception = null;

        for (int i = 0; i < Constants.lockTrials; i++) {
            try {
                namingSecurityContext = newSecurityContext.getPolicyConfigurationContext("service.naming");
                if (namingSecurityContext != null) {
                    success = true;
                    break;
                }

                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException ie) {
                    // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here
                    // Please do not remove this comment!
                    success = false;
                }

            } catch (Exception e) {
            	LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
                exception = e;
            }
        }

        if (!success) {
            if (exception == null) {
                if (LOG_LOCATION.beWarning()) {
                  SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000043", "JNDI System Exception * Failed to get policy configuration from SecurityContext, the naming policy configuration is null. Result: the JNDI Registry Service will not support security checks for this server process");
                }
                LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), RuntimeExceptionConstants.POLICY_CONFIGURATION_IS_NULL, null);
                ServiceException se = new ServiceException(formater);
                if(LOG_LOCATION.bePath()) {
                	LOG_LOCATION.traceThrowableT(Severity.PATH, "JNDI System Exception * Failed to get policy configuration from SecurityContext, the naming policy configuration is null. Result: the JNDI Registry Service will not support security checks for this server node.", se);
                }
                loginContext = null;
                SecurityBase.WITHOUT_SECURITY = true;
                throw se;
            } else {
                if (LOG_LOCATION.bePath()) {
                  SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000044", "JNDI System Exception * Failed to get policy configuration from SecurityContext. Result: the JNDI Registry Service will not support security checks for this server process");
                }
                LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), RuntimeExceptionConstants.FAIL_TO_GET_POLICY_CONFIGURATION, null);
                ServiceException se = new ServiceException(formater, exception);
                if(LOG_LOCATION.bePath()) {
                	LOG_LOCATION.traceThrowableT(Severity.PATH, "", se);
                }
                loginContext = null;
                SecurityBase.WITHOUT_SECURITY = true;
                throw se;
            }
        }

        loginContext = newSecurityContext.getAuthenticationContext();
        try {
            SecurityBase.WITHOUT_SECURITY = false;
            new SecurityBase(sc);
            if (LOG_LOCATION.bePath()) {
            	LOG_LOCATION.pathT("SecurityContext and LoginContext are initialized successfully. Security checks in the JNDI Registry Service are enabled.");
            }
        } catch (Exception e) {
            // did not succeed - continuing w/o security
            loginContext = null;
            SecurityBase.WITHOUT_SECURITY = true;
            if (LOG_LOCATION.beWarning()) {
              SimpleLogger.traceThrowable(Severity.WARNING,LOG_LOCATION,e, "ASJ.jndi.000031", "Failed to enable security checks for the JNDI Registry Service. Result: there will be no authentication and authorization checks for any client for this server process");
            }
        }
//    }
    }

    private void performClusterLock() {
        MultipleAnswer ma = null;
        boolean lockPerformedOk = false;
        do {

            ma = ((CacheCommunicatorImpl) cacheCommunicator).sendToAllAndWaitForAnswer(new byte[0], JNDIFrame.CLUSTER_LOCK, 0);

            if (ma != null) {
                ArrayInt servers = checkAnswers(ma);
                if (servers == null) {  //have to unlock
                    ((CacheCommunicatorImpl) cacheCommunicator).sendToAllAndWaitForAnswer(new byte[0], JNDIFrame.CLUSTER_UNLOCK, 0);

                } else if (servers.size() != 0) {
                    MessageAnswer answer = null;
                    byte[] count;
                    int clusterId = -1;
                    do {
                        clusterId = servers.lastElement();
                        servers.removeLastElement();
                        answer = ((CacheCommunicatorImpl) cacheCommunicator).sendToServerAndWaitForAnswer(clusterId, new byte[0], 11, 0); //to replicate jndi db
                    } while (answer == null && servers.size() != 0);

                    if (answer != null) {
                        count = answer.getMessage();

                        int replicationPassCount = Convert.byteArrToInt(count, 0);
                        if (replicationPassCount > 0) {
                            ReplicationCounter repCount = ((CacheCommunicatorImpl) cacheCommunicator).replicationCounter;
                            synchronized (repCount) {
                                while (repCount.getValue() < replicationPassCount) {
                                    try {
                                        repCount.wait();
                                    } catch (InterruptedException e) {
                                        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
                                    }
                                }
                            }
                        }
                    } // this server or the server we are sending request to is in DEBUGGING mode
//          ((JNDIMemoryImpl)JNDIManager.db).initIDGenerator();
                    ((CacheCommunicatorImpl) cacheCommunicator).setReplicationCompleted(true);
                    ((CacheCommunicatorImpl) cacheCommunicator).sendToAllAndWaitForAnswer(new byte[0], JNDIFrame.CLUSTER_UNLOCK, 0);
                    lockPerformedOk = true;
                } else {
                    ((CacheCommunicatorImpl) cacheCommunicator).setReplicationCompleted(true);
                    ((CacheCommunicatorImpl) cacheCommunicator).sendToAllAndWaitForAnswer(new byte[0], JNDIFrame.CLUSTER_UNLOCK, 0);
                    lockPerformedOk = true;
                }
            } else {
                lockPerformedOk = true;
            }
        } while (!lockPerformedOk);
    }

    private ArrayInt checkAnswers(MultipleAnswer ma) {
        int thisClusterId = sc.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();
        int localIdPrefix = thisClusterId / 100;
        ArrayInt localServers = new ArrayInt();
        int[] servers = ma.participants();
        ArrayInt readyServers = new ArrayInt();
        for (int i = 0; i < servers.length; i++) {
            try {
                byte[] answer = ma.getAnswer(servers[i]).getMessage();
                int result = Convert.byteArrToInt(answer, 0);
                if (result == 0) {
                    // check if there are any local servers to replicate from
                    if (localIdPrefix == (servers[i] / 100)) {
                        localServers.add(servers[i]);
                    } else {
                        readyServers.add(servers[i]);
                    }
                } else if (result == 1) {
                    return null;
                }
            } catch (ClusterException e) {
                LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
            }
        }
        readyServers.addAll(localServers);
        return readyServers;
    }

//  private int checkForStillUnlockedServers(MultipleAnswer ma) {
//    int count = 0;
//    int[] servers = ma.participants();
//    try {
//      for(int i = 0; i < servers.length; i++) {
//        byte[] answer = ma.getAnswer(servers[i]).getMessage();
//        int result = Convert.byteArrToInt(answer, 0);
//        if (result == 1) {
//          count++;
//        }
//      }
//    } catch (ClusterException e) {
//      count++;
//    }
//    return count;
//  }
}

