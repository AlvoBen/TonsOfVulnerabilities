package com.sap.engine.services.deploy.server.application;

import static com.sap.engine.services.deploy.container.util.CAConstants.EOL;
import static com.sap.engine.services.deploy.logging.DSLog.logThrowableAlwaysSucceeds;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.lib.security.domain.ProtectionDomainFactory;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.accounting.APredefinedComponent;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.ShmComponentUtils;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.engine.services.deploy.timestat.DeployOperationTimeStat;
import com.sap.engine.services.deploy.timestat.ITimeStatConstants;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Transaction used to remove a deployed application. It is always successful.
 * 
 * @author Rumiana Angelova
 * @version 6.25
 */
public class RemoveTransaction extends ApplicationTransaction {
	private static final Location location = 
		Location.getLocation(RemoveTransaction.class);
	private Configuration config = null;
	// The result of the execution of a global remove transaction.
	private final RemoveResult result;
	
	private final class RemoveResult {

		private final Map<String, Throwable> errors;
		private final Map<String, WarningException> warnings;
		
		public RemoveResult() {
			errors = new HashMap<String, Throwable>();
			warnings = new HashMap<String, WarningException>();
		}
		
		public void addError(final String containerName, 
			final Throwable ex) {
			errors.put(containerName, ex);
		}
		
		public void addWarning(final String containerName, 
			final WarningException ex) {
			warnings.put(containerName, ex);
		}
		
		public RemoteException getException() {
			final RemoteException ex;
			if(errors.size() > 0) {
				ex = new RemoteException(getExceptionMessage());
			} else if(warnings.size() > 0) {
				ex = new WarningException(getExceptionMessage());
			} else {
				ex = null;
			}
			return ex;
		}

		private String getExceptionMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append("Remove operation of application [")
				.append(RemoveTransaction.this.getModuleID())
				.append("] finished.").append(EOL);
			if(errors.size() > 0) {
				sb.append("Errors:").append(EOL);
				for(Map.Entry<String, Throwable> entry : errors.entrySet()) {
					sb.append("Container [").append(entry.getKey()).append("]: ")
						.append(entry.getValue().getMessage())
						.append(EOL);
				}
			}
			if(warnings.size() > 0) {
				sb.append("Warnings:").append(EOL);
				for(Map.Entry<String, WarningException> entry 
					: warnings.entrySet()) {
					sb.append("Container [").append(entry.getKey()).append("] ")
						.append(entry.getValue().getMessage())
						.append(EOL);
				}
			}
			return sb.toString();
        }
	}

	/**
	 * Creates a local remove transaction, when the corresponding command is 
	 * received, as a part of global remove transaction. 
	 * @param appName
	 * @param ctx
	 * @param onlyFlag used only to distinguish between both constructors.
	 * @throws DeploymentException
	 * @throws ComponentNotDeployedException
	 */
	public RemoveTransaction(final String appName, final String[] containers,
		final DeployServiceContext ctx, final boolean onlyFlag)
		throws DeploymentException, ComponentNotDeployedException {
		super(ctx, PropManager.getInstance().isTxOperationSupported());
		result = null;
		init(appName);
		
		final TransactionCommunicator comm = ctx.getTxCommunicator();
		final DeploymentInfo info = comm.getApplicationInfo(getModuleID());
		if (info == null) {
			throw new ComponentNotDeployedException(
				ExceptionConstants.NOT_DEPLOYED, new String[] {
				getModuleID(), getTransactionType() });
		}
		try {
			if (Status.STARTED.equals(info.getStatus())) {
				makeNestedParallelTransaction(
					new StopTransaction(getModuleID(), null, ctx));
			}
		} catch (OutOfMemoryError oom) {
			throw oom;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			logThrowableAlwaysSucceeds(this, "stop transaction", th);
		}

		ContainerInterface cont = null;
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		for (int i = 0; i < containers.length; i++) {
			cont = communicator.getContainer(containers[i]);
			if (cont == null) {
				final ServerDeploymentException sdex = 
					new ServerDeploymentException(
						ExceptionConstants.NOT_AVAILABLE_CONTAINER,
						containers[i], getTransactionType(), getModuleID());
				SimpleLogger.traceThrowable(Severity.ERROR, location,
					sdex.getLocalizedMessage(), sdex);
			} else {
				addContainer(cont, null);
			}
		}
	}

	/**
	 * Global remove transaction.
	 * @param moduleName
	 * @param ctx
	 * @throws DeploymentException
	 * @throws ComponentNotDeployedException
	 */
	public RemoveTransaction(final String moduleName,
		final DeployServiceContext ctx) throws DeploymentException,
		ComponentNotDeployedException {
		super(ctx, PropManager.getInstance().isTxOperationSupported());
		result = new RemoveResult();
		init(moduleName);
		if (ctx.getTxCommunicator().getApplicationInfo(getModuleID()) == null) {
			throw new ComponentNotDeployedException(
				ExceptionConstants.NOT_DEPLOYED, new String[] {
					getModuleID(), getTransactionType() });
		}
	}

	private void init(String moduleName) {
		setModuleID(moduleName);
		setModuleType(DeployConstants.APP_TYPE);
		setTransactionType(DeployConstants.removeApp);
	}

	public void begin() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		DeploymentInfo deployment = null;
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		deployment = communicator.getApplicationInfo(getModuleID());
		String[] contNames = deployment.getContainerNames();
		if (contNames != null) {
			for (int i = 0; i < contNames.length; i++) {
				final ContainerInterface cont = communicator
						.getContainer(contNames[i]);
				if (cont == null) {
					final ServerDeploymentException sdex = 
						new ServerDeploymentException(
							ExceptionConstants.NOT_AVAILABLE_CONTAINER,
							contNames[i], getTransactionType(),	getModuleID());
					SimpleLogger.traceThrowable(Severity.ERROR, location, 
						sdex.getLocalizedMessage(), sdex);
				} else {
					addContainer(cont, null);
				}
			}
		}

		if (containers == null) {
			containers = new ContainerInterface[0];
		}

		if (Status.STARTED.equals(deployment.getStatus())) {
			try {
				makeNestedParallelTransaction(
					new StopTransaction(getModuleID(), ctx,
						ctx.getClusterMonitorHelper().findServers()));
			} catch (OutOfMemoryError oom) {
				throw oom;
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable th) {
				logThrowableAlwaysSucceeds(this, "stop transaction", th);
			}
		}

	}

	public void beginLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		if (containers != null) {
			for (int i = 0; i < containers.length; i++) {
				this.notifyRemoveApplication(getModuleID(), containers[i]);
			}
		}

		ShmComponentUtils.close(getModuleID());
		deleteAppDir();
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		communicator.removeApplicationInfo(getModuleID());
		communicator.unregisterApplicationManagedObject(getModuleID());
	}

	private void deleteAppDir() {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		try {
			Accounting.beginMeasure(ITimeStatConstants.IOs_Delete, this
					.getClass());
			DUtils.deleteDirectory(new File(PropManager.getInstance()
					.getAppsWorkDir()
					+ getModuleID()));
		} finally {
			Accounting.endMeasure(ITimeStatConstants.IOs_Delete);
			long end = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addIOsOperation(new DeployOperationTimeStat(
					ITimeStatConstants.IOs_Delete, start, end,
					cpuStartTime, cpuEndTime));
		}
	}

	public void prepare() throws DeploymentException {
		final String appID = getModuleID();
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare [{0}] of application [{1}]",
				getTransactionType(), appID);
		}
		openHandler();
		try {
			config = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS,
				ConfigurationHandler.WRITE_ACCESS);
		} catch (DeploymentException dex) {
			logThrowableAlwaysSucceeds(this, "prepare", dex);
		}
		
		for (int i = 0; i < containers.length; i++) {
			removeApplication(appID, containers[i], getHandler(), config);
		}
		if(result.errors.size() > 0) {
			markForRemoval(appID);
		} else {
			ShmComponentUtils.close(getModuleID());
			// removing protection domains for application.
			updateProtDomFactory();
			removeContainerFromCacheIfAny();
			deleteAllConfigs();
		}
	}

	private void markForRemoval(final String appID)
        throws DeploymentException, ServerDeploymentException {
	    final Configuration deployConfig = openApplicationConfiguration(
	    	DeployConstants.ROOT_CFG_DEPLOY,
	    	ConfigurationHandler.WRITE_ACCESS);
	    final DeploymentInfo dInfo = 
			ctx.getTxCommunicator().getApplicationInfo(appID);
	    containers = removeFailedContainers(dInfo);
	    dInfo.setStatus(Status.MARKED_FOR_REMOVAL, null);
	    final DIWriter diWriter = EditorFactory.getInstance()
	    	.getDIWriter(dInfo.getVersion());
	    diWriter.modifyDeploymentInfo(config, deployConfig, dInfo);
	}

	private ContainerInterface[] removeFailedContainers(
		final DeploymentInfo dInfo) {
	    final List<ContainerInterface> cntList = 
	    	new ArrayList<ContainerInterface>();
	    for(ContainerInterface container : containers) {
	    	final String cntName = container.getContainerInfo().getName();
	    	if(!result.errors.keySet().contains(cntName)) {
	    		cntList.add(container);
	    	} else {
	    		dInfo.removeContainerData(cntName);
	    	}
	    }
	    return cntList.toArray(new ContainerInterface[cntList.size()]);
	}

	private void updateProtDomFactory() {
		final DeploymentInfo info = ctx.getTxCommunicator().getApplicationInfo(
				getModuleID());
		final String[] jars = info.getApplicationLoaderFiles();
		final ProtectionDomainFactory factory = getProtectionDomainFactory();
		removeStoredPropDoms(factory, jars);
	}

	private ProtectionDomainFactory getProtectionDomainFactory() {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		ProtectionDomainFactory protectionDomainFactory = null;
		try {
			Accounting.beginMeasure(
				ITimeStatConstants.PROT_DOM_GET_FACTORY,
				ProtectionDomainFactory.class);
			protectionDomainFactory = ProtectionDomainFactory.getFactory();
			return protectionDomainFactory;
		} finally {
			Accounting.endMeasure(ITimeStatConstants.PROT_DOM_GET_FACTORY);
			long end = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addProtDomOperation(
				new DeployOperationTimeStat(
					ITimeStatConstants.PROT_DOM_GET_FACTORY,
					start, end, cpuStartTime, cpuEndTime));
		}
	}

	private void removeStoredPropDoms(final ProtectionDomainFactory factory,
		final String[] jars) {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		try {
			Accounting.beginMeasure(
				ITimeStatConstants.PROT_DOM_REMOVE_STORED_PROT_DOMS,
				ProtectionDomainFactory.class);
			String path = null;
			if (jars != null) {
				for (int i = 0; i < jars.length; i++) {
					try {
						path = new File(jars[i]).getCanonicalPath();
					} catch (IOException e) {
						path = null;
					}
					if (path != null) {
						factory.removeStoredProtectionDomain(jars[i]);
					}
				}
			}
			factory.removeStoredProtectionDomain(getModuleID());
		} finally {
			Accounting.endMeasure(ITimeStatConstants.PROT_DOM_REMOVE_STORED_PROT_DOMS);
			long end = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addProtDomOperation(
				new DeployOperationTimeStat(
					ITimeStatConstants.PROT_DOM_REMOVE_STORED_PROT_DOMS,
					start, end, cpuStartTime, cpuEndTime));
		}
	}

	public void prepareLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		removeContainerFromCacheIfAny();
	}

	public void commit() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}

		for (int i = 0; i < containers.length; i++) {
			commitRemove(containers[i], getModuleID());
		}

		deleteAppDir();
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		communicator.removeApplicationInfo(getModuleID());

		communicator.unregisterApplicationManagedObject(getModuleID());
		this.setSuccessfullyFinished(true);
	}
	
	private void deleteAllConfigs() {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		try {
			Accounting.beginMeasure(ITimeStatConstants.CFG_MNG_DELETE_CFG,
				APredefinedComponent.ConfigurationManager);
			if (getHandler() != null && config != null) {
				try {
					config.deleteConfiguration();
				} catch (ConfigurationException cEx) {
					logThrowableAlwaysSucceeds(this, "commit", cEx);
				}
				deleteAppConfiguration(DeployConstants.ROOT_CFG_DEPLOY, true);
				// it's normal not to have this configuration for old deployed
				// applications.
				deleteAppConfiguration(DeployConstants.CURRENT_INSTANCE_CONFIG,
						false);
				// it's normal not to have this configuration for old deployed
				// applications.
				deleteAppConfiguration(DeployConstants.GLOBAL_CONFIG, false);
				// this configuration references the global above, but explicit
				// delete is made as precaution
				deleteAppConfiguration(DeployConstants.CUSTOM_GLOBAL_CONFIG,
						false);
				// this configuration references the global above , but explicit
				// delete is made as precaution
				deleteAppConfiguration(DeployConstants.PREFS_ROOT, false);
			}
		} finally {
			Accounting.endMeasure(ITimeStatConstants.CFG_MNG_DELETE_CFG);
			long end = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addCfgMngOperation(new DeployOperationTimeStat(
				ITimeStatConstants.CFG_MNG_DELETE_CFG, start, end,
				cpuStartTime, cpuEndTime));

			try {
				if (getHandler() != null) {
					commitHandler();
				}
			} catch (ConfigurationException ce1) {
				logThrowableAlwaysSucceeds(this, "commit", ce1);
			}
		}		
	}

	private void deleteAppConfiguration(String root,
			boolean ifErrorWillItBeLogged) {
		try {
			final Configuration deployConfig = openApplicationConfiguration(
				root, ConfigurationHandler.WRITE_ACCESS);
			if (deployConfig != null) {
				deployConfig.deleteConfiguration();
			}
		} catch (ConfigurationException cEx) {
			// $JL-EXC$
			if (ifErrorWillItBeLogged) {
				logThrowableAlwaysSucceeds(this, "commit", cEx);
			}
		} catch (DeploymentException dEx) {
			// $JL-EXC$
			if (ifErrorWillItBeLogged) {
				logThrowableAlwaysSucceeds(this, "commit", dEx);
			}
		}
	}

	public void commitLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		this.setSuccessfullyFinished(true);
	}

	public void rollback() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		try {
			if (getHandler() != null) {
				rollbackHandler();
			}
		} catch (ConfigurationException ce1) {
			logThrowableAlwaysSucceeds(this, "rollback", ce1);
		}
	}

	public void rollbackLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	public void rollbackPrepare() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		try {
			if (getHandler() != null) {
				rollbackHandler();
			}
		} catch (ConfigurationException ce1) {
			logThrowableAlwaysSucceeds(this, "rollback prepare", ce1);
		}
	}

	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	private void notifyRemoveApplication(String applicationName,
		ContainerInterface container) {
		try {
			if (location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"Start removing of application [{0}] from container [{1}]",
					getModuleID(), container.getContainerInfo().getName());
			}
			// this should always be ContainerWrapper
			((ContainerWrapper) container)
					.notifyRemoveInternal(applicationName);
			if (location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"Finished removing of application [{0}] from container [{1}]",
					getModuleID(), container.getContainerInfo().getName());
			}
		} catch (OutOfMemoryError oom) {
			throw oom;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			logThrowableAlwaysSucceeds(this, 
				container.getContainerInfo().getName() + " container", th);
		}
	}

	@Override
	protected void clearExceptionInfoAndNotifyFailed2Start() {
		ctx.getTxCommunicator().getManagementListenerUtils()
			.notify4Remove(getModuleID());
	}


	private void removeApplication(final String applicationName,
		final ContainerInterface container, final ConfigurationHandler handler,
		final Configuration appConfig) {

		try {
			// This should always be ContainerWrapper.
			((ContainerWrapper) container).remove(
				applicationName, handler, appConfig);
		} catch (OutOfMemoryError oom) {
			throw oom;
		} catch (ThreadDeath td) {
			throw td;
		} catch (WarningException ex) {
			result.addWarning(container.getContainerInfo().getName(), ex);
		} catch (Throwable th) {
			result.addError(container.getContainerInfo().getName(), th);
		}
	}

	private void commitRemove(ContainerInterface container,
			String applicationName) {
		try {
			if (location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"Start commitRemove of application [{0}] from container [{1}]",
					getModuleID(), container.getContainerInfo().getName());
			}
			// this should always be ContainerWrapper
			((ContainerWrapper) container).commitRemove(applicationName);
			if (location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"Finished commitRemove of application [{0}] from container [{1}]",
					getModuleID(), container.getContainerInfo().getName());
			}
		} catch (OutOfMemoryError oom) {
			throw oom;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			logThrowableAlwaysSucceeds(this, container.getContainerInfo()
					.getName()
					+ " container", th);
		}
	}
	
	public RemoteException getResultException() {
		return result.getException();
	}
}