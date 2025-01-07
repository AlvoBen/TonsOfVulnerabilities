/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.manage;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;

import com.sap.engine.boot.soft.CriticalOperationNotAlowedException;
import com.sap.engine.boot.soft.CriticalOperationStore;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.container.deploy.zdm.RollingPatch;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.thread.execution.Executor;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.deploy.DeployParallelismStrategy;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.server.spi.ServerMode;
import com.sap.engine.services.dc.cm.undeploy.UndeployParallelismStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.cmd.CMDRegisterException;
import com.sap.engine.services.dc.cmd.telnet.TelnetCommandsManager;
import com.sap.engine.services.dc.frame.DeployControllerFrame;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.ValidatorUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.DeployServiceExt;
import com.sap.engine.services.deploy.zdm.DSRollingPatch;
import com.sap.tc.logging.Location;

/**
 * Wraps all cluster information.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class ServiceConfigurer {
	
	private static Location location = DCLog.getLocation(ServiceConfigurer.class);

	private static final ServiceConfigurer INSTANCE = new ServiceConfigurer();

	public static final String SERVER_INTERNAL_LOCKING_ID = "$services.dc";
	private static final String SERVER_INTERNAL_LOCKING_DESCR = "This locking is used from the deploy controller.";
	private static boolean modeSet = false;
	private static ServerMode mode;

	private ApplicationServiceContext appServiceCtx = null;
	private String crcCheckMode = null;
	private boolean isCrcCheckSwitchedOn = false;
	private boolean dsWarningSuppressed = true;
	private SecurityContext securityContext = null;
	private DeployWorkflowStrategy deplWorkflowStrategy = null;
	private UndeployWorkflowStrategy undeplWorkflowStrategy = null;
	private DeployParallelismStrategy deplParallelismStrategy = null;
	private UndeployParallelismStrategy undeplParallelismStrategy = null;
	private ServerInternalLocking serverInternalLocking = null;
	private int genSessionId4Secs = 30;
	private int genSessionIdsAtOnes = 10;
	private long offlineResultTimeout = 3600000L;
	private ShellInterface shellInterface = null;
	private String wdServerInfo = null;
	private Properties instId2InstPfl = null;
	private long minFreeBytesToDeploy = 0;
	private int deployThreadsNumber = 0;
	private String osUser = null;
	private String osPass = null;
	private int lockingRetries;
	private int lockingInterval;
	private Executor eventExecutor;
	private Executor parallelDeployExecutor;
	private Executor singleThreadDeployExecutor;
	private Map<String, Integer> operationId2criticalOperation = new HashMap<String, Integer>(5);

	private ServiceConfigurer() {
	}

	public static ServiceConfigurer getInstance() {
		return INSTANCE;
	}

	/**
	 * Sets the <code>ApplicationServiceContext</code>.
	 * 
	 * @param _appServiceCtx
	 *            <code>ApplicationServiceContext</code>
	 * @throws IllegalArgumentException
	 * @throws TechnicalLockException
	 */
	public void setApplicationServiceContext(
			ApplicationServiceContext _appServiceCtx)
			throws TechnicalLockException, IllegalArgumentException {
		this.appServiceCtx = _appServiceCtx;
		ValidatorUtils.validate(getApplicationServiceContext());
		this.serverInternalLocking = getApplicationServiceContext()
				.getCoreContext().getLockingContext()
				.createServerInternalLocking(SERVER_INTERNAL_LOCKING_ID,
						SERVER_INTERNAL_LOCKING_DESCR);
	}

	/**
	 * Gets the <code>ApplicationServiceContext</code>.
	 * 
	 * @return <code>ApplicationServiceContext</code>
	 */
	public ApplicationServiceContext getApplicationServiceContext() {
		return this.appServiceCtx;
	}

	/**
	 * Gets the <code>ConfigurationHandler</code> from the
	 * <code>ApplicationServiceContext</code>.
	 * 
	 * @return <code>ConfigurationHandler</code>
	 * @throws ConfigurationException
	 *             if cannot get handler.
	 */
	public ConfigurationHandler getConfigurationHandler()
			throws ConfigurationException {
		ValidatorUtils.validate(getApplicationServiceContext());
		return getApplicationServiceContext().getCoreContext()
				.getConfigurationHandlerFactory().getConfigurationHandler();
	}

	/**
	 * Gets the <code>ConfigurationHandlerFactory</code> from the
	 * <code>ApplicationServiceContext</code>.
	 * 
	 * @return <code>ConfigurationHandlerFactory</code>
	 * @throws NullPointerException
	 *             if the <code>ApplicationServiceContext</code> is null.
	 */
	public ConfigurationHandlerFactory getConfigurationHandlerFactory()
			throws NullPointerException {
		ValidatorUtils.validate(getApplicationServiceContext());
		return getApplicationServiceContext().getCoreContext()
				.getConfigurationHandlerFactory();
	}

	/**
	 * Gets the <code>AdministrativeLocking</code> from the
	 * <code>ApplicationServiceContext</code>.
	 * 
	 * @return <code>AdministrativeLocking</code>
	 * @throws NullPointerException
	 *             if the <code>ApplicationServiceContext</code> is null.
	 * @deprecated
	 */
	public AdministrativeLocking getAdministrativeLocking()
			throws NullPointerException {
		ValidatorUtils.validate(getApplicationServiceContext());
		return getApplicationServiceContext().getCoreContext()
				.getLockingContext().getAdministrativeLocking();
	}

	/**
	 * Gets the <code>ServerInternalLocking</code> from the
	 * <code>ApplicationServiceContext</code>.
	 * 
	 * @return <code>ServerInternalLocking</code>
	 * @throws NullPointerException
	 *             if the <code>ServerInternalLocking</code> is null.
	 */
	public ServerInternalLocking getServerInternalLocking()
			throws NullPointerException {
		ValidatorUtils.validateNull(this.serverInternalLocking,
				"ServerInternalLocking");
		return this.serverInternalLocking;
	}

	/**
	 * Gets the <code>ClusterMonitor</code> from the
	 * <code>ApplicationServiceContext</code>.
	 * 
	 * @return <code>ClusterMonitor</code>
	 * @throws NullPointerException
	 *             if the <code>ApplicationServiceContext</code> is null.
	 */
	public ClusterMonitor getClusterMonitor() throws NullPointerException {
		ValidatorUtils.validate(getApplicationServiceContext());
		return getApplicationServiceContext().getClusterContext()
				.getClusterMonitor();
	}

	/**
	 * Gets the <code>CoreMonitor</code> from the
	 * <code>ApplicationServiceContext</code>.
	 * 
	 * @return <code>CoreMonitor</code>
	 * @throws NullPointerException
	 *             if the <code>ApplicationServiceContext</code> is null.
	 */
	public CoreMonitor getCoreMonitor() throws NullPointerException {
		ValidatorUtils.validate(getApplicationServiceContext());
		return getApplicationServiceContext().getCoreContext().getCoreMonitor();
	}

	public synchronized void clear() {
		if (this.eventExecutor != null) {
			this.appServiceCtx.getCoreContext().getThreadSystem()
					.destroyExecutor(this.eventExecutor);
		}
		if (this.parallelDeployExecutor != null) {
			this.appServiceCtx.getCoreContext().getThreadSystem()
					.destroyExecutor(this.parallelDeployExecutor);
		}
		if (this.singleThreadDeployExecutor != null) {
			this.appServiceCtx.getCoreContext().getThreadSystem()
					.destroyExecutor(this.singleThreadDeployExecutor);
		}
		/*
		 *  commented in order to avoid the NPE reported in CSN 0002318090 2009
		 *  This is a temporary solution. The real solution is:
		 *  When stop of the server is triggered:
		 *  	1. No new RMI call requests are accepted
		 *  	2. All existing requests are awaited until finished (or until a given timeout)
		 *  	3. Only then stop of the services takes place
		 */
		//		this.appServiceCtx = null;
	}

	public boolean isCrcCheckSwitchedOn() {
		return this.isCrcCheckSwitchedOn;
	}

	public String getCrcCheckMode() {
		return this.crcCheckMode;
	}

	public void setCrcCheckMode(boolean isCrcCheckSwitchedOn) {
		this.isCrcCheckSwitchedOn = isCrcCheckSwitchedOn;
	}

	public SecurityContext getSecurityContext() {
		return this.securityContext;
	}

	public void setSecurityContext(SecurityContext securityContext) {
		if (location.beInfo()) {
			tracePath(location,
					"Setting the security context in the service configurer to [{0}]",
					new Object[] { securityContext });
		}
		this.securityContext = securityContext;
	}

	public void setDeployWorkflowStrategy(
			DeployWorkflowStrategy deplWorkflowStrategy) {
		this.deplWorkflowStrategy = deplWorkflowStrategy;
	}

	public DeployWorkflowStrategy getDeployWorkflowStrategy() {
		return this.deplWorkflowStrategy;
	}

	public void setUndeployWorkflowStrategy(
			UndeployWorkflowStrategy undeplWorkflowStrategy) {
		this.undeplWorkflowStrategy = undeplWorkflowStrategy;
	}

	public UndeployWorkflowStrategy getUndeployWorkflowStrategy() {
		return this.undeplWorkflowStrategy;
	}

	public DeployParallelismStrategy getDeployParallelismStrategy() {
		return this.deplParallelismStrategy;
	}

	public void setDeployParallelismStrategy(
			DeployParallelismStrategy _deplParallelismStrategy) {
		if (location.beInfo()) {
			tracePath(location, 
					"ServiceConfigurer->setDeployParallelismStrategy = [{0}]",
					new Object[] { _deplParallelismStrategy });
		}
		this.deplParallelismStrategy = _deplParallelismStrategy;
	}

	public UndeployParallelismStrategy getUndeployParallelismStrategy() {
		return this.undeplParallelismStrategy;
	}

	public void setUndeployParallelismStrategy(
			UndeployParallelismStrategy undeplParallelismStrategy) {
		if (location.beInfo()) {
			tracePath(location, 
					"ServiceConfigurer->setUndeployParallelismStrategy= [{0}]",
					new Object[] { undeplParallelismStrategy });
		}
		this.undeplParallelismStrategy = undeplParallelismStrategy;
	}

	public DeployService getDeployService() throws NamingException {
		return (DeployService) getFromRegistryOrNaming(Constants.DEPLOY_SERVICE_NAME);
	}

	public DeployServiceExt getDeployServiceExt() throws NamingException {
		return (DeployServiceExt) getFromRegistryOrNaming(Constants.DEPLOY_SERVICE_NAME);
	}

	public DSRollingPatch getDSRollingPatch() throws NamingException {
		return (DSRollingPatch) getFromRegistryOrNaming(Constants.DEPLOY_SERVICE_NAME);
	}

	public RollingPatch getRollingPatch() throws NamingException {
		return getApplicationServiceContext().getContainerContext()
				.getDeployContext().getRollingPatch();
	}

	public CM getComponentManager() throws NamingException {
		return (CM) getFromRegistryOrNaming(CM.SERVICE_NAME);
	}

	public Object getFromRegistryOrNaming(String bindName)
			throws NamingException {
		Object reference = null;
		try {
			if (this.appServiceCtx != null) {
				reference = this.appServiceCtx.getContainerContext()
						.getObjectRegistry().getServiceInterface(bindName);
			}
		} catch (Exception e) {
			String msg = DCLog
					.buildExceptionMessage(
							"",
							"Cannot get service interface [{0}] from the component registry.",
							new String[] { bindName });
			DCLog.logErrorThrowable(location, null, msg, e);
		}
		if (reference == null) {
			reference = getFromNaming(bindName);
		}
		return reference;
	}

	public Object getFromNaming(String bindName) throws NamingException {
		return new javax.naming.InitialContext().lookup(bindName);
	}

	public int getGenSessionId4Secs() {
		return this.genSessionId4Secs;
	}

	public void setGenSessionId4Secs(int genSessionId4Secs) {
		this.genSessionId4Secs = genSessionId4Secs;
	}

	public int getGenSessionIdsAtOnes() {
		return this.genSessionIdsAtOnes;
	}

	public void setGenSessionIdsAtOnes(int genSessionIdsAtOnes) {
		int minValue = 1;
		if (genSessionIdsAtOnes < minValue) {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003334 The 'genSessionIdsAtOnes' should be ["
							+ minValue + ".." + Integer.MAX_VALUE + "]");
		}
		this.genSessionIdsAtOnes = genSessionIdsAtOnes;
	}

	public void setOfflineResultTimeout(long timeout) {
		this.offlineResultTimeout = timeout;
	}

	public long getOfflineResultTimeout() {
		return this.offlineResultTimeout;
	}

	private static boolean registerTelnetCommand(ShellInterface _shellInterface) {
		if (_shellInterface != null) {
			try {
				TelnetCommandsManager telnetCommandsManager = TelnetCommandsManager
						.getInstance();
				if (telnetCommandsManager != null) {
					telnetCommandsManager
							.registerTelnetCommands(_shellInterface);
					return true;
				} else {
					DCLog
							.logWarning(
									location, 
									"ASJ.dpl_dc.005513",
									"Deploy Controler Telnet cannot be registered because TelnetCommandsManager instance is 'null'.");
				}
			} catch (CMDRegisterException e) {
				DCLog.logErrorThrowable(location, e);
			}
		} else {
			if (location.beInfo()) {
				tracePath(location, 
						"Telnet commands of Deploy Controller will not be registered, because [shell] is [Null].");
			}
		}
		return false;
	}

	public void registerTelnetOnNeed(ShellInterface _shellInterface) {
		if (location.beDebug()) {
			traceDebug(
					location,
					"ServiceConfigurer::registerTelnetOnNeed with argument [{0}] while the Deploy Controller is in state [{1}]",
					new Object[] { _shellInterface,
							DCManager.getInstance().getDCState() });
		}
		if (DCManager.getInstance().isInWorkingMode()) {
			ShellInterface notNullShellInterface = null;
			notNullShellInterface = (_shellInterface != null) ? _shellInterface
					: this.shellInterface;
			if (registerTelnetCommand(notNullShellInterface)) {
				this.shellInterface = null;
			}
		} else if (_shellInterface != null) {
			this.shellInterface = _shellInterface;
		}
	}

	public InetSocketAddress getInstanceHttpAccessPoint(int instanceId) {
		return getClusterMonitor().getAccessPoint(instanceId, "j2ee");
	}

	public InetSocketAddress getInstanceP4AccessPoint(int instanceId) {
		return getClusterMonitor().getAccessPoint(instanceId, "p4");
	}

	public String getInstPfl(int instanceID) {
		return (String) instId2InstPfl.get(instanceID + "");
	}

	public Properties getInstId2InstPfl() {
		return instId2InstPfl;
	}

	public void setInstId2InstPfl(Properties instId2InstPfl) {
		this.instId2InstPfl = instId2InstPfl;
		if (location.beDebug()) {
			traceDebug(location, "Setting [{0}] = [{1}]",
					new Object[] { DeployControllerFrame.INST_ID_2_INST_PFL,
							getInstId2InstPfl() });
		}
	}

	public String getOsPass() {
		return osPass;
	}

	public void setOsPass(String osPass) {
		this.osPass = osPass;
		if (location.beDebug()) {
			traceDebug(location, "Setting [{0}] = [{1}]",
					new Object[] { DeployControllerFrame.OS_PASS, "*****" });
		}
	}

	public String getOsUser() {
		return osUser;
	}

	public void setOsUser(String osUser) {
		this.osUser = osUser;
		if (location.beDebug()) {
			traceDebug(location, "Setting [{0}] = [{1}]",
					new Object[] { DeployControllerFrame.OS_USER, getOsUser() });
		}
	}

	public void setDeployThreadsNumber(final Integer deployThreadsNumber) {
		this.deployThreadsNumber = deployThreadsNumber;
	}

	public Integer getDeployThreadsNumber() {
		return this.deployThreadsNumber;
	}

	public String getWdServerInfo() {
		return wdServerInfo;
	}

	public void setWdServerInfo(String wdServerInfo) {
		this.wdServerInfo = wdServerInfo;
		if (location.beDebug()) {
			traceDebug(location, "Setting [{0}] = [{1}]",
					new Object[] { DeployControllerFrame.WEB_DISP_SERVER_INFO,
							getWdServerInfo() });
		}
	}

	public synchronized ServerMode getServerMode() {
		if (!ServiceConfigurer.modeSet) {
			throw new IllegalStateException("ServerMode not set yet");
		}
		return ServiceConfigurer.mode;
	}

	public synchronized void setServerMode(ServerMode mode) {
		if (!modeSet) {
			ServiceConfigurer.mode = mode;
			ServiceConfigurer.modeSet = true;
		} else {
			throw new IllegalStateException("Cannot set server mode twice");
		}
	}

	public long getMinFreeBytesToDeploy() {
		return this.minFreeBytesToDeploy;
	}

	public void setMinFreeBytesToDeploy(final long minFreeBytesToDeploy) {
		if (location.beInfo()) {
			tracePath(location, 
					"ServiceConfigurer->setMinFreeBytesToDeploy= [{0}]",
					new Object[] { minFreeBytesToDeploy });
		}
		this.minFreeBytesToDeploy = minFreeBytesToDeploy;
	}

	public synchronized Executor getExecutor() {

		if (this.eventExecutor == null) {
			this.eventExecutor = this.appServiceCtx.getCoreContext()
					.getThreadSystem().createExecutor(
							"DeployControllerEventExecutor", 10, 10);
		}

		return this.eventExecutor;
	}

	public synchronized void setExecutor(final Executor eventExecutor) {
		this.eventExecutor = eventExecutor;
	}

	public synchronized Executor getParallelDeployExecutor() {
		if (this.parallelDeployExecutor == null) {
			this.parallelDeployExecutor = this.appServiceCtx.getCoreContext()
					.getThreadSystem().createExecutor(
							"DeployControllerParallelDeployExecutor",
							getDeployThreadsNumber(), getDeployThreadsNumber(),
							Executor.WAIT_TO_QUEUE_POLICY);
		}
		return this.parallelDeployExecutor;
	}

	// TODO fix to be single thread in current thread
	public synchronized Executor getSingleThreadDeployExecutor() {
		if (this.singleThreadDeployExecutor == null) {
			this.singleThreadDeployExecutor = this.appServiceCtx
					.getCoreContext().getThreadSystem().createExecutor(
							"DeployControllerSingleThreadDeployExecutor", 1, 0,
							Executor.WAIT_TO_QUEUE_POLICY);
		}
		return this.singleThreadDeployExecutor;
	}

	public synchronized void setDeployExecutor(final Executor executor) {
		this.parallelDeployExecutor = executor;
	}

	public void setLockingRetries(int retries) {

		this.lockingRetries = retries;

	}

	public void setLockingInterval(int interval) {

		this.lockingInterval = interval;

	}

	public synchronized int getLockingRetries() {
		return lockingRetries;
	}

	public synchronized int getLockingInterval() {
		return lockingInterval;
	}
	
	/**
	 * Soft shutdown integration. This method attempts to enter a critical operation section
	 * for the given operation ID. An operation ID is just a string that has to be unique for the
	 * operation. 
	 * 
	 * @param operationId
	 * @throws CriticalOperationNotAlowedException if the server is already in shutdown state
	 * @throws IllegalStateException if this method has already been called with the given
	 * sesionId
	 */
	public void enterCriticalOperation(String operationId) throws CriticalOperationNotAlowedException {
		
		synchronized (this.operationId2criticalOperation){
		
			if(this.operationId2criticalOperation.containsKey(operationId)){
				throw new IllegalStateException("A critical operation for operationId [" + operationId + "] has already been started" );
			}
			
			// get an operation ID and associate it with the session ID 
			int id = CriticalOperationStore.registerCriticalComponent(CM.SERVICE_NAME);			
			this.operationId2criticalOperation.put(operationId, id);
			
			CriticalOperationStore.enterCriticalOperation(id);
		}
		
	}

	/**
	 * Soft shutdown integration. This method attempts to end a critical operation for
	 * the given operation ID. If the operation has already been ended the 
	 * method just returns
	 * 
	 * @param operationId
	 */
	public void exitCriticalOperation(String operationId){
		
		synchronized (this.operationId2criticalOperation){
			
			// get the associated operation ID
			Integer id = this.operationId2criticalOperation.get(operationId);
			if(id == null){
				return;
			}

			// exit the critical operation and clear the mapping
			CriticalOperationStore.exitCriticalOperation(id);
			this.operationId2criticalOperation.remove(operationId);
			
			Iterator<String> iter = this.operationId2criticalOperation.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				if(this.operationId2criticalOperation.get(key) == id){
					iter.remove();
				}
			}
			
		}

		
	}
	
	public void createAliasForCriticalOperatioId(String source, String dest){
		
		
		synchronized (this.operationId2criticalOperation){
			
			
			// get the associated operation ID
			Integer id = this.operationId2criticalOperation.get(source);
			if(id == null){
				throw new IllegalStateException("A critical operation for operationId [" + source + "] has never been started" );
			}


			this.operationId2criticalOperation.put(dest, id);
			
		}
		
	}
	
	/**
	 * @return if the warning from the Deploy Service are ignored for forming the deploy status; if <tt>false</tt>,
	 * the status of items for which Deploy Service throws warnings is changed from "Success" to "Warning"
	 */
	public boolean isDSWarningSuppressed() {
		return dsWarningSuppressed;
	}
	
	/**
	 * Set the flag for suppressing the warnings from Deploy Service. 
	 * @param dsWarningSuppressed if <tt>true</tt>, the warnings from Deploy Service are ignored;
	 * if <tt>false</tt>, the status of items for which Deploy Service throws warnings is changed from "Success" to "Warning"
	 */
	public void setDSWarningSuppressed(boolean dsWarningSuppressed) {
		this.dsWarningSuppressed = dsWarningSuppressed;
	}

}