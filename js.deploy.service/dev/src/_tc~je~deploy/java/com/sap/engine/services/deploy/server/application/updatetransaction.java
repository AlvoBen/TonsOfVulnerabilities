package com.sap.engine.services.deploy.server.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.jar.EARReader;
import com.sap.engine.services.deploy.ear.jar.StandaloneModuleReader;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.utils.ShmComponentUtils;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.StopLockEvaluator;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author Rumiana Angelova
 * @version 6.40
 */
public class UpdateTransaction extends DeployUtilTransaction {
	private static final Location location = 
		Location.getLocation(UpdateTransaction.class);

	protected DeploymentInfo oldDeployment;
	protected boolean needStartAfterFinish = false;
	protected boolean needOncePerInstanceAfterFinish = true;
	protected Status oldStatus = Status.UNKNOWN;
	protected StatusDescription oldStatusDesc;
	private boolean isRolling;
	
	/**
	 * Create initiated local transaction as a result of received update 
	 * command.
	 * @param appName the name of the application which has to be updated.
	 * @param ctx the deploy service context.
	 * @param containerProps container properties.
	 * @param containerNames container names.
	 * @throws DeploymentException
	 */
	public UpdateTransaction(final String appName,
		final DeployServiceContext ctx,
		final Dictionary<String, Properties> containerProps,
		final String[] containerNames) throws DeploymentException {
		super(ctx);
		SimpleLogger.trace(Severity.PATH, location, null,
			"New local update transaction is initiated");
		setModuleID(appName);
		setProperties(null);
		init(DeployConstants.APP_TYPE, false);
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		oldDeployment = communicator.getApplicationInfo(getModuleID());
		if (containerNames == null || containerNames.length == 0) {
			return;
		}

		for (int i = 0; i < containerNames.length; i++) {
			final ContainerInterface cont = 
				communicator.getContainer(containerNames[i]);
			if (cont == null) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_AVAILABLE_CONTAINER,
					containerNames[i], getTransactionType(), getModuleID());
				sde.setMessageID("ASJ.dpl_ds.005006");
				throw sde;
			}
			addContainer(cont, containerProps.get(containerNames[i]));
		}
	}

	/**
	 * Create global update transaction.
	 * @param earFilePath
	 * @param props
	 * @param ctx
	 * @throws DeploymentException
	 */
	public UpdateTransaction(final String earFilePath, final Properties props,
		final DeployServiceContext ctx) throws DeploymentException {
		this(earFilePath, props, ctx, false);
		SimpleLogger.trace(Severity.PATH, location, null,
			"New global update transaction is created");
	}

	/**
	 * Create global rolling transaction.
	 * @param earFilePath
	 * @param props
	 * @param ctx
	 * @param isRolling
	 * @throws DeploymentException
	 */
	@Deprecated
	public UpdateTransaction(final String earFilePath, final Properties props,
		final DeployServiceContext ctx, final boolean isRolling)
		throws DeploymentException {
		super(ctx);
		try {
			setProperties(props);
			init(DeployConstants.APP_TYPE, isRolling);
			moduleFile = new File(earFilePath);

			try {
				reader = new EARReader(moduleFile.getAbsolutePath(), props);
				descr = reader.getDescriptor();
			} catch (IOException ioex) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.ERROR_IN_READING_DESCR,
						new String[] { earFilePath }, ioex);
				sde.setMessageID("ASJ.dpl_ds.005041");
				throw sde;
			}
			setCompAndProviderNames();

			oldDeployment = ctx.getTxCommunicator().getApplicationInfo(
					getModuleID());
			if (oldDeployment == null) {
				SimpleLogger.trace(Severity.WARNING, location,
					"ASJ.dpl_ds.000182",
					"The application [{0}] is not deployed and cannot be " +
					"updated. Will clear its reader, which will be " +
					"reinitialized in our deploy during update logic.",
					getModuleID());
				reader.clear();
				return;
			}
			remoteSupport = oldDeployment.getRemoteSupport();
		} catch (Throwable e) {
			throwableInConstructor(e);
		}
	}

	/**
	 * Create a global update transaction for a stand alone module.
	 * @param moduleFilePath path to the archive file.
	 * @param containerName the name of the container for that module archive.
	 * @param remoteSupport supported remote protocols.
	 * @param props properties for deployment.
	 * @param ctx deploy service context.
	 * @throws DeploymentException if module archive is not for the specified 
	 * container or another problem occurs.
	 */
	public UpdateTransaction(final String moduleFilePath,
		final String containerName, final String[] remoteSupport,
		final Properties props, final DeployServiceContext ctx)
		throws DeploymentException {
		this(moduleFilePath, containerName, remoteSupport, props, ctx, false);
	}

	/**
	 * Create global rolling update transaction for a standalone module.
	 * @param containerName the name of the container for that module archive.
	 * @param remoteSupport supported remote protocols.
	 * @param props properties for deployment.
	 * @param ctx deploy service context.
	 * @param isRolling whether this is a rolling update.
	 * @throws DeploymentException if module archive is not for the specified 
	 * container or another problem occurs.
	 */
	@Deprecated
	public UpdateTransaction(final String moduleFilePath,
		final String containerName, String[] remoteSupport,
		final Properties props, final DeployServiceContext ctx,
		final boolean isRolling) throws DeploymentException {
		super(ctx);
		try {
			this.remoteSupport = remoteSupport;
			setProperties(props);
			init(DeployConstants.MODULE_TYPE, isRolling);
			moduleFile = new File(moduleFilePath);
			final TransactionCommunicator communicator = 
				ctx.getTxCommunicator();
			try {
				if (containerName != null
						&& communicator.getContainer(containerName) != null) {
					reader = new StandaloneModuleReader(moduleFile,
							containerName, props);
				} else {
					reader = new StandaloneModuleReader(moduleFile, props);
				}
				descr = reader.getDescriptor();
			} catch (IOException ioex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_READING_DESCR,
					new String[] { moduleFilePath }, ioex);
				sde.setMessageID("ASJ.dpl_ds.005041");
				throw sde;
			}
			setCompAndProviderNames();

			oldDeployment = communicator.getApplicationInfo(getModuleID());
			if (oldDeployment == null) {
				return;
			}
		} catch (Throwable t) {
			throwableInConstructor(t);
		}
	}

	private void init(final byte moduleType, final boolean isRolling) {
		this.isRolling = isRolling;
		setModuleType(moduleType);
		setTransactionType(DeployConstants.update);
	}

	public void begin() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}

		oldStatus = oldDeployment.getStatus();
		oldStatusDesc = oldDeployment.getStatusDescription();
		openHandler();
		config = openApplicationConfiguration(
			DeployConstants.ROOT_CFG_APPS, ConfigurationHandler.READ_ACCESS);
		commonBegin();
		deployment.setStatus(
			Status.UPGRADING, StatusDescriptionsEnum.UPDATING, null);
	}

	public void beginLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Begin local [{0}] of application [{1}]",
					getTransactionType(), getModuleID());
		}
		oldStatus = oldDeployment.getStatus();
		oldStatusDesc = oldDeployment.getStatusDescription();
		l_beginLocal();
	}

	public void l_beginLocal() throws DeploymentException {
		try {
			openHandler();
			config = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS, 
				ConfigurationHandler.READ_ACCESS);
			{// The DeploymentInfo will be downloaded from DB and updated
				// locally.
				final TransactionCommunicator communicator = ctx
						.getTxCommunicator();
				communicator.refreshDeploymentInfoFromDB(getModuleID(), config,
						getHandler());
				if (!needStartAfterFinish) {
					communicator.getApplicationInfo(getModuleID()).setStatus(
							oldStatus, oldStatusDesc);
				}
			}
			if (containers != null) {
				for (int i = 0; i < containers.length; i++) {
					try {
						Properties containerProperties = 
							(Properties) contProperties.get(
								containers[i].getContainerInfo().getName());
						containerProperties = 
							containerProperties == null ? 
								new Properties() : containerProperties;
						containers[i].notifyUpdatedComponents(getModuleID(),
								config, containerProperties);
					} catch (WarningException wex) {
						wex.printStackTrace();
						addWarnings(wex.getWarnings());
					}
				}
			}
		} finally {
			try {
				rollbackHandler();
			} catch (ConfigurationException e) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_COMMIT_HANDLER, new String[] {
						getTransactionType(), getModuleID() }, e);
				sde.setMessageID("ASJ.dpl_ds.005026");
				throw sde;
			}
		}
	}

	public void prepare() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}

		if (containers != null && containers.length > 0) {
			for (int i = 0; i < containers.length; i++) {
				try {
					containers[i].prepareUpdate(getModuleID());
				} catch (WarningException wex) {
					addWarnings(wex.getWarnings());
				}
			}
		}
		updateContainerInCache(deployment);

		modifyDeploymentInfoInConfiguration();
		try {
			commitHandler();
		} catch (ConfigurationException ce) {
			setHandler(null);
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_COMMIT_HANDLER, new String[] {
							getTransactionType(), getModuleID() }, ce);
			sde.setMessageID("ASJ.dpl_ds.005026");
			throw sde;
		}
	}

	public void prepareLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Prepare local [{0}] of application [{1}]",
					getTransactionType(), getModuleID());
		}
		updateContainerInCacheLocal();

	}

	public void commit() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Commit [{0}] of application [{1}]", getTransactionType(),
					getModuleID());
		}
		clearReader();

		ApplicationDeployInfo appInfo = null;
		ctx.getTxCommunicator().addApplicationInfo(getModuleID(), deployment);
		if (containers != null) {
			for (int i = 0; i < containers.length; i++) {
				try {
					appInfo = containers[i].commitUpdate(getModuleID());
					if (appInfo != null) {
						if (appInfo.getWarnings() != null
								&& appInfo.getWarnings().size() > 0) {
							String[] w = new String[appInfo.getWarnings()
									.size()];
							appInfo.getWarnings().toArray(w);
							addWarnings(w);
						}
					}
				} catch (WarningException wex) {
					addWarnings(wex.getWarnings());
				} catch (OutOfMemoryError oofme) {
					throw oofme;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					final ServerDeploymentException sde = 
						new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "committing update of application ["
								+ getModuleID()
								+ "] on container ["
								+ containers[i].getContainerInfo()
											.getName() + "]" }, th);
					SimpleLogger.traceThrowable(Severity.ERROR, location, 
						sde.getLocalizedMessage(), th);
				}
			}
		}
		if (this.needStartAfterFinish) {
			deployment.setStatus(Status.STOPPED,
				StatusDescriptionsEnum.STOPPED_AFTER_UPDATE_AS_NEEDS_START,
				null);
		} else {
			deployment.setStatus(oldStatus,
				StatusDescriptionsEnum.INITIAL_STATUS_AFTER_UPDATE_SUCCESS,
				null);
		}

		Policy.getPolicy().refresh();
		this.setSuccessfullyFinished(true);
		ShmComponentUtils.close(getModuleID());
		setShmComponentStatusStopped();
		setShmComponentStartupMode();
	}

	public void commitLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		l_commitLocal();
	}

	public void l_commitLocal() {
		this.setSuccessfullyFinished(true);
		ShmComponentUtils.close(getModuleID());
		setShmComponentStatusStopped();
		setShmComponentStartupMode();
	}

	public void rollback() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
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
		rollbackContainerInCacheIfAny(deployment);
		rollbackCommon();
	}

	// to ensure that app binaries will be downloaded after update failure
	private void deleteVersionBin(DeploymentInfo dInfo) {
		final File version_bin = dInfo.getVersionBin();
		if (version_bin.exists()) {
			version_bin.delete();
		}
		dInfo.setIndexFS(null);
	}

	private void rollbackCommon() {
		deleteVersionBin(oldDeployment);
		ctx.getTxCommunicator()
				.addApplicationInfo(getModuleID(), oldDeployment);
		try {
			if (getHandler() != null) {
				try {
					rollbackHandler();
				} catch (ConfigurationException cex) {
					final ServerDeploymentException sdex = 
						new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "deployment rollback for application ["
							+ getModuleID()
							+ "].\nReason: "
							+ cex.toString() }, cex);
					SimpleLogger.traceThrowable(Severity.ERROR, location, 
						sdex.getLocalizedMessage(), cex);
				}
			}

			openHandler();
			config = openApplicationConfiguration(
					DeployConstants.ROOT_CFG_APPS,
					ConfigurationHandler.READ_ACCESS);
			try {
				if (containers != null) {
					for (int i = 0; i < containers.length; i++) {
						try {
							containers[i].rollbackUpdate(getModuleID(), config,
									contProperties);
						} catch (WarningException wex) {
							addWarnings(wex.getWarnings());
						} catch (OutOfMemoryError oofme) {
							throw oofme;
						} catch (ThreadDeath td) {
							throw td;
						} catch (Throwable th) {
							final ServerDeploymentException sde = new ServerDeploymentException(
									ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
									new String[] { "committing update of application ["
											+ getModuleID()
											+ "] on container ["
											+ containers[i].getContainerInfo()
													.getName() + "]" }, th);
							SimpleLogger.traceThrowable(Severity.ERROR,
									location, sde.getLocalizedMessage(), th);
						}
					}
				}
			} finally {
				clearReader();
				cleanUpDeploymentFile();
				if (needStartAfterFinish) {
					oldDeployment.setStatus(Status.STOPPED,
						StatusDescriptionsEnum.STOPPED_AFTER_UPDATE_AS_UPDATE_FAILED_AND_NEEDS_START,
						null);
				} else {
					oldDeployment.setStatus(oldStatus,
						StatusDescriptionsEnum.INITIAL_STATUS_AFTER_UPDATE_ROLLBACK,
						null);
				}
			}
		} catch (DeploymentException dex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, dex
					.getLocalizedMessage(), dex);
		} finally {
			try {
				rollbackHandler();
			} catch (ConfigurationException cex) {
				final ServerDeploymentException sdex = 
					new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "deployment rollback for application ["
								+ getModuleID() + "].\nReason: "
								+ cex.toString() }, cex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, sdex
						.getLocalizedMessage(), cex);
			}
		}
	}

	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackContainerInCacheIfAny(deployment);
	}

	@Override
	protected void makeComponents(File[] componentFiles,
		ContainerDeploymentInfo containerInfo, Properties contProps,
		ContainerInterface cont) throws DeploymentException {
		ApplicationDeployInfo tempInfo = null;
		if (cont == null) {
			return;
		}
		if (componentFiles == null) {
			componentFiles = new File[0];
		}
		String contName = cont.getContainerInfo().getName();
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
					"Start updating on container [{0}].", contName);
		}
		try {
			tempInfo = cont
					.makeUpdate(componentFiles, containerInfo, contProps);
		} catch (DeploymentException de) {
			addContainer(cont, contProps);
			throw de;
		} catch (Exception th) {
			addContainer(cont, contProps);
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.EXEPTION_DURING_CONTAINER_GENERATION,
					new String[] { getModuleID(), contName }, th);
			sde.setMessageID("ASJ.dpl_ds.005089");
			throw sde;
		} catch (OutOfMemoryError oofmer) {
			addContainer(cont, contProps);
			throw oofmer;
		} catch (ThreadDeath td) {
			addContainer(cont, contProps);
			throw td;
		} catch (Error err) {
			addContainer(cont, contProps);			
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.EXEPTION_DURING_CONTAINER_GENERATION,
				new String[] { getModuleID(), contName }, err);
			sde.setMessageID("ASJ.dpl_ds.005089");
			throw sde;
		}
		this.processApplicationDeployInfo(tempInfo, componentFiles, cont,
				oldDeployment);
	}

	private void addChangesInWebProps(Properties webProps) {
		Properties props = deployment.getProperties();
		List<String> newAliases = new ArrayList<String>();
		List<String> removedAliases = new ArrayList<String>();
		if (props != null) {
			Enumeration keys = props.keys();
			String key = null;
			while (keys.hasMoreElements()) {
				key = (String) keys.nextElement();
				webProps.put(key, props.getProperty(key));
				newAliases.add(props.getProperty(key));
			}
		}
		Properties oldProps = oldDeployment.getProperties();
		if (oldProps != null) {
			Enumeration oldAliases = oldProps.elements();
			String oldAlias = null;
			while (oldAliases.hasMoreElements()) {
				oldAlias = (String) oldAliases.nextElement();
				if (!newAliases.contains(oldAlias)) {
					removedAliases.add(oldAlias);
				} else {
					newAliases.remove(oldAlias);
				}
			}
		}
		String[] newTemp = new String[newAliases.size()];
		String[] removedTemp = new String[removedAliases.size()];
		newAliases.toArray(newTemp);
		removedAliases.toArray(removedTemp);
		webProps.put("added_aliases", newTemp);
		webProps.put("removed_aliases", removedTemp);
	}

	@Override
	protected Hashtable<ContainerInterface, Properties> getConcernedContainers(
		final Hashtable<String, File[]> allContFiles,
		final ContainerDeploymentInfo containerInfo) 
		throws DeploymentException {
		
		final List<ContainerInterface> cies = new ArrayList<ContainerInterface>();
		ContainerInterface ci = null;
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		for(String name : allContFiles.keySet()) {
			ci = communicator.getContainer(name);
			if (ci != null) {
				cies.add(ci);
			} else {
				throw new ServerDeploymentException(
						ExceptionConstants.NOT_AVAILABLE_CONTAINER,
					new Object [] {name, getTransactionType(),	getModuleID()});
			}
		}
		List<String> stoppedOptionalContainers = new ArrayList<String>();
		String[] oldConts = oldDeployment.getContainerNames();
		if (oldConts != null) {
			for (int i = 0; i < oldConts.length; i++) {
				if (allContFiles.get(oldConts[i]) == null) {
					ci = communicator.getContainer(oldConts[i]);
					if (ci != null) {
						cies.add(ci);
					} else {
						if (oldDeployment.isOptionalContainer(oldConts[i])) {
							stoppedOptionalContainers.add(oldConts[i]);
						} else {
							throw new ServerDeploymentException(
								ExceptionConstants.NOT_AVAILABLE_CONTAINER,
								oldConts[i], getTransactionType(), 
								getModuleID());
						}
					}
				}
			}
		}
		ContainerInterface[] res = new ContainerInterface[cies.size()];
		cies.toArray(res);
		String[] stoppedOptionalContainerNames = new String[stoppedOptionalContainers
				.size()];
		stoppedOptionalContainers.toArray(stoppedOptionalContainerNames);
		Hashtable<ContainerInterface, Properties> result = 
			getContainersWhichNeedUpdate(res,
				stoppedOptionalContainerNames, allContFiles, containerInfo);
		return result;
	}

	@Override
	public void lock() throws LockSetNotAcquiredException,
		InterruptedException, ConflictingOperationLockException {
		lockSet = ctx.getLockManager().lock(
			new StopLockEvaluator(Applications.getReferenceGraph(), 
				ctx.getLockManager().getLockTracker(),
				getTransactionType(), true, getComponent(),
				isEnqueueLockNeeded() ? getLockType() : 0,
				PropManager.getInstance().getTimeout4LocalLock()));
	}

	private Hashtable<ContainerInterface, Properties> 
		getContainersWhichNeedUpdate(
		final ContainerInterface[] concernedContainers,
		final String[] stoppedOptionalContainerNames, 
		final Hashtable<String, File[]> allContFiles,
		final ContainerDeploymentInfo containerInfo) 
		throws DeploymentException {

		deployment.setVersion(oldDeployment.getVersion());
		final Properties[] cProps = new Properties[concernedContainers.length];
		for (int i = 0; i < concernedContainers.length; i++) {
			cProps[i] = (Properties) getProperties().clone();
		}
		Hashtable<ContainerInterface, Properties> contInterface2Properties = 
			new Hashtable<ContainerInterface, Properties>();
		String contName = null;
		File[] files = null;
		if (stoppedOptionalContainerNames != null) {
			for (int i = 0; i < stoppedOptionalContainerNames.length; i++) {
				copyInfoForUnchangedContainers(stoppedOptionalContainerNames[i]);
			}
		}
		for (int i = 0; i < concernedContainers.length; i++) {
			if (concernedContainers[i].getContainerInfo().isJ2EEContainer()
					&& concernedContainers[i].getContainerInfo()
						.getJ2EEModuleName().equals(
							J2EEModule.Type.web.name())) {
				addChangesInWebProps(cProps[i]);
			}
			contName = concernedContainers[i].getContainerInfo().getName();
			try {
				files = allContFiles.get(contName);
				if (files == null) {
					files = new File[0];
				}
				if (concernedContainers[i].needUpdate(files, containerInfo,
					cProps[i])) {
					contInterface2Properties.put(concernedContainers[i],
						cProps[i]);
				} else if (deployment.getVersion().getId().compareTo(
					Version.getNewestVersion().getId()) < 0) {
					if (location.beInfo()) {
						SimpleLogger.trace(Severity.INFO, location,
							"ASJ.dpl_ds.000198",
							"The container [{0}] will participate in the [{1}] of application + [{2}] , because its [{3}] has to be migrated from version [{4}] to [{5}].",
							contName, getTransactionType(),
							getModuleID(), deployment.getClass().getName(), 
							deployment.getVersion().getName(),
							Version.getNewestVersion().getName());
					}
					contInterface2Properties.put(concernedContainers[i],
							cProps[i]);
				} else if (oldDeployment.getAllProvidedResources().size() > 0) {
					this.copyInfoForUnchangedContainers(concernedContainers[i]
							.getContainerInfo().getName());
				}
			} catch (WarningException wex) {
				addWarnings(wex.getWarnings());
			}
		}

		needStopApplicationPhase(contInterface2Properties, allContFiles,
				containerInfo);
		try {
			this.config.close();
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
					"update", getModuleID() }, cex);
			sde.setMessageID("ASJ.dpl_ds.005029");
			throw sde;
		} finally {
			this.config = null;
		}

		return contInterface2Properties;
	}

	private void copyInfoForUnchangedContainers(String contName) {
		if (!oldDeployment.isContainerData(contName)) {
			return;
		}
		final ContainerData oldCData = oldDeployment
			.getOrCreateContainerData(contName);
		if (oldCData != null) {
			final Collection<Resource> dComps = oldCData.getProvidedResources();
			if (dComps != null && dComps.size() > 0) {
				deployment.setContainerData(oldCData);
			}
		}
	}

	private void needStopApplicationPhase(
		final Hashtable<ContainerInterface, Properties> contInterface2Properties,
		final Hashtable<String, File[]> allContFiles, 
		final ContainerDeploymentInfo containerInfo)
			throws DeploymentException {

		Properties contProperties = null;
		File[] files = null;
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		for(final ContainerInterface contInterface : contInterface2Properties.keySet()) {
			contProperties = contInterface2Properties.get(contInterface);
			try {
				files = allContFiles.get(contInterface.getContainerInfo().getName());
				if (files == null) {
					files = new File[0];
				}
				if (contInterface.needStopOnUpdate(
					files, containerInfo, contProperties)) {
					communicator.addApplicationInfo(
						getModuleID(), oldDeployment);
					// set it before starting the transaction,
					// to be able to set the correct status in the rollBack
					// part.
					needStartAfterFinish = true;
					makeNestedParallelTransaction(new StopTransaction(
						getModuleID(), ctx, 
						ctx.getClusterMonitorHelper().findServers()));
					return;
				}
			} catch (WarningException wex) {
				addWarnings(wex.getWarnings());
			}
		}
		// the application will not be stopped, so its binaries should not be
		// downloaded.
		needOncePerInstanceAfterFinish = false;
	}

	@Override
	protected void finalActions() throws WarningException, DeploymentException {
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Final actions in [{0}] for application [{1}].",
				getTransactionType(), getModuleID());
		}
		// OncePerInstanceTransaction
		if (needOncePerInstanceAfterFinish) {
			final boolean isFinishedOK = isRolling ?  
				false : isSuccessfullyFinished();
			oncePerInstanceTransaction(isFinishedOK, true);
		} else {
			if (location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"The needOncePerInstanceAfterFinish is [{0}] and it won't be invoked.",
					needOncePerInstanceAfterFinish);
			}

		}
	}

	/**
	 * Restarts the application after
	 */
	public void restart() {
		if ((needStartAfterFinish && Status.STARTED.equals(oldStatus)) ||
			Status.IMPLICIT_STOPPED.equals(oldStatus)) {
			try {
				makeNestedParallelTransaction(new StartTransaction(
					getModuleID(), ctx, 
					ctx.getClusterMonitorHelper().findServers()));
			} catch (DeploymentException dex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					dex.getLocalizedMessage(), dex);
			}
		}
	}

	protected void rollbackContainerInCacheIfAny(DeploymentInfo deployment) {
		String cInfoXML = deployment.getContainerInfoXML();
		if (cInfoXML != null) {
			InputStream is = new StringBufferInputStream(cInfoXML);
			String componentName = deployment.getApplicationName();
			Containers.getInstance().addContainers(is, 
				new Component(componentName, Component.Type.APPLICATION));
		}
	}

	@Override
	protected int[] getRemoteParticipants() {
		final ClusterMonitorHelper cmHelper = ctx.getClusterMonitorHelper();
		if(!isRolling) {
			return cmHelper.findEligibleReceivers();
		}
		return cmHelper.findOtherServersInCurrentInstance();
	}
}