/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.server.application;

import java.io.File;
import java.io.IOException;
import java.security.Policy;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.ear.jar.EARReader;
import com.sap.engine.services.deploy.ear.jar.StandaloneModuleReader;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Transaction used to deploy an application to the server. If the application
 * is already deployed, it will be removed first in an nested transaction. The
 * status of newly deployed application is STOPPED.
 * 
 * @author Monika Kovachka, Rumiana Angelova
 */
public class DeploymentTransaction extends DeployUtilTransaction {
	private static final Location location = 
		Location.getLocation(DeploymentTransaction.class);
	/**
	 * Constructs a local deployment transaction as a reaction to already
	 * executed global deployment transaction on the global server node.
	 * 
	 * @param appName the name of the application which has to be deployed.
	 * @param ctx Deploy service context.
	 * @param containerProps hashtable with container properties.
	 * @param containerNames string array of container names.
	 * @throws DeploymentException if no container is specified or another 
	 * problem occurs.
	 */
	@SuppressWarnings("boxing")
	public DeploymentTransaction(final String appName,
	    final DeployServiceContext ctx,
	    final Dictionary<String, Properties> containerProps,
	    final String[] containerNames) throws DeploymentException {
		super(ctx);
		setModuleID(appName);
		setProperties(null);
		init(DeployConstants.APP_TYPE);

		if(containerNames == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_INFO_ABOUT_CONTS_RECEIVED,
				ctx.getClusterMonitorHelper().getCurrentServerId(),
				getTransactionType(), getModuleID());
			sde.setMessageID("ASJ.dpl_ds.005040");
			throw sde;
		}

		final TransactionCommunicator comm = ctx.getTxCommunicator();
		for(int i = 0; i < containerNames.length; i++) {
			final ContainerInterface cont = comm
			    .getContainer(containerNames[i]);
			if(cont != null) {
				addContainer(cont, containerProps.get(containerNames[i]));
			}
		}
	}

	/**
	 * Constructs a global deployment transaction.
	 * 
	 * @param earFilePath path to the ear file.
	 * @param remoteSupport string array of remote supports.
	 * @param props properties for deployment.
	 * @param ctx Deploy service context.
	 * @throws DeploymentException if ear file is incorrect or another problem 
	 * occurs.
	 */
	public DeploymentTransaction(final String earFilePath,
	    final String[] remoteSupport, final Properties props,
	    final DeployServiceContext ctx) throws DeploymentException {
		super(ctx);
		try {
			this.remoteSupport = remoteSupport;
			setProperties(props);
			init(DeployConstants.APP_TYPE);
			moduleFile = new File(earFilePath);

			try {
				reader = new EARReader(moduleFile.getAbsolutePath(), props);
				descr = reader.getDescriptor();
			} catch(IOException ioex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_READING_DESCR,
					new String[] {earFilePath}, ioex);
				sde.setMessageID("ASJ.dpl_ds.005041");
				throw sde;
			}
			setCompAndProviderNames();
		} catch(Throwable t) {
			throwableInConstructor(t);
		}
	}

	/**
	 * Constructs a global deployment transaction for a stand alone module.
	 * 
	 * @param moduleFilePath path to the archive file.
	 * @param containerName the name of the container for that module archive.
	 * @param remoteSupport string array of remote supports.
	 * @param props properties for deployment.
	 * @param ctx Deploy service context.
	 * @throws DeploymentException if module archive is not for the specified 
	 * container or another problem occurs.
	 */
	public DeploymentTransaction(final String moduleFilePath,
	    final String containerName, final String[] remoteSupport,
	    final Properties props, final DeployServiceContext ctx)
	    throws DeploymentException {
		super(ctx);
		try {
			this.remoteSupport = remoteSupport;
			setProperties(props);
			init(DeployConstants.MODULE_TYPE);
			moduleFile = new File(moduleFilePath);

			try {
				if(containerName != null
				    && ctx.getTxCommunicator().getContainer(containerName) != null) {
					reader = new StandaloneModuleReader(moduleFile,
					    containerName, props);
				} else {
					reader = new StandaloneModuleReader(moduleFile, props);
				}
				descr = reader.getDescriptor();
			} catch(IOException ioex) {
				ServerDeploymentException sde = new ServerDeploymentException(
				    ExceptionConstants.ERROR_IN_READING_DESCR,
				    new String[] {moduleFilePath}, ioex);
				sde.setMessageID("ASJ.dpl_ds.005041");
				throw sde;
			}
			setCompAndProviderNames();
		} catch(Throwable t) {
			throwableInConstructor(t);
		}
	}

	private void init(byte moduleType) {
		setModuleType(moduleType);
		setTransactionType(DeployConstants.deploy);
	}

	/**
	 * Starts the global deployment transaction.
	 * 
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 * @throws ComponentNotDeployedException
	 *             thrown if trying to remove a non existing application.
	 */
	public void begin() throws DeploymentException,
	    ComponentNotDeployedException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
			    "Begin [{0}] of application [{1}]", getTransactionType(),
			    getModuleID());
		}

		if(ctx.getTxCommunicator().getApplicationInfo(getModuleID()) != null) {
			// The application is already deployed. Remove it.
			makeNestedTransaction(new RemoveTransaction(getModuleID(), ctx));
		} else {
			// The application does not exist on the server.
			checkAndClear();
		}
		// Here the application is already removed.
		openHandler();
		config = createAppIndexedConfiguration(DeployConstants.ROOT_CFG_APPS,
		    true);
		deployConfig = createAppIndexedConfiguration(
		    DeployConstants.ROOT_CFG_DEPLOY, false);
		// false - because make references creates it.
		globalConfig = this.createOrGetGlobalApplicationConfiguration();
		commonBegin();
		deployment.setStatus(Status.STOPPED,
		    StatusDescriptionsEnum.INITIALY_STOPPED_AT_DEPLOY_BEGIN, null);
	}

	/**
	 * Prepares the application/ stand alone module deployment.
	 * 
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	public void prepare() throws DeploymentException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
			    "Prepare [{0}] of application [{1}]", getTransactionType(),
			    getModuleID());
		}

		if(containers == null || containers.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_AVAILABLE_CONTS_FOR_APP,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005036");
			throw sde;
		}
		for(int i = 0; i < containers.length; i++) {
			try {
				containers[i].prepareDeploy(getModuleID(), config);
			} catch(WarningException wex) {
				addWarnings(wex.getWarnings());
			} catch(DeploymentException de) {
				throw de;
			} catch(OutOfMemoryError oofmer) {
				throw oofmer;
			} catch(ThreadDeath td) {
				throw td;
			} catch(Throwable th) {
				ServerDeploymentException sde = new ServerDeploymentException(
				    ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				    new String[] {"preparing deploy of application "
				        + getModuleID()}, th);
				sde.setMessageID("ASJ.dpl_ds.005082");
				throw sde;
			}
		}
		updateContainerInCache(deployment);
		modifyDeploymentInfoInConfiguration();
		try {
			commitHandler();
		} catch(ConfigurationException ce) {
			setHandler(null);
			ServerDeploymentException sde = new ServerDeploymentException(
			    ExceptionConstants.CANNOT_COMMIT_HANDLER, new String[] {
			        getTransactionType(), getModuleID()}, ce);
			sde.setMessageID("ASJ.dpl_ds.005026");
			throw sde;
		}
	}

	/**
	 * Commits the application/ stand alone module deployment.
	 */
	public void commit() {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
			    "Commit [{0}] of application [{1}]", getTransactionType(),
			    getModuleID());
		}
		clearReader();

		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		communicator.addApplicationInfo(getModuleID(), deployment);
		communicator.registerApplicationManagedObject(getModuleID());
		for(int i = 0; i < containers.length; i++) {
			try {
				containers[i].commitDeploy(getModuleID());
			} catch(WarningException e) {
				addWarnings(e.getWarnings());
			} catch(OutOfMemoryError oofme) {
				throw oofme;
			} catch(ThreadDeath td) {
				throw td;
			} catch(Throwable th) {
				final ServerDeploymentException sdex = new ServerDeploymentException(
				    ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				    new String[] {"committing deployment of application "
				        + getModuleID()}, th);
				SimpleLogger.traceThrowable(
					Severity.ERROR, location, sdex.getLocalizedMessage(), th);
			}
		}
		communicator.unregisterReferences(getModuleID());
		Policy.getPolicy().refresh();
		this.setSuccessfullyFinished(true);
		setShmComponentStatusStopped();
		setShmComponentStartupMode();
	}

	/**
	 * Method used to rollback the deployment.
	 */
	public void rollback() {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	/**
	 * Method used to rollback the prepare deployment phase.
	 */
	public void rollbackPrepare() {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
			    "Rollback prepare [{0}] of application [{1}]",
			    getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	public void rollbackCommon() {
		if(containers != null) {
			for(int i = 0; i < containers.length; i++) {
				try {
					containers[i].rollbackDeploy(getModuleID());
				} catch(WarningException wex) {
					addWarnings(wex.getWarnings());
				} catch(OutOfMemoryError oofme) {
					throw oofme;
				} catch(ThreadDeath td) {
					throw td;
				} catch(Throwable th) {
					final ServerDeploymentException sdex = new ServerDeploymentException(
					    ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					    new String[] {"rolling back deployment of application "
					        + getModuleID()}, th);
					SimpleLogger.traceThrowable(Severity.ERROR, location, 
						sdex.getLocalizedMessage(), th);
				}
			}
		}
		ctx.getTxCommunicator().removeApplicationInfo(getModuleID());
		if(getHandler() != null) {
			try {
				rollbackHandler();
			} catch(ConfigurationException cex) {
				final ServerDeploymentException sdex = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] {"deployment rollback for application "
						+ getModuleID() + ".\nReason: "
						+ cex.toString()}, cex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					sdex.getLocalizedMessage(), sdex);
			}
		}
		try {
			openHandler();
		} catch(DeploymentException dex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location,
				dex.getLocalizedMessage(), dex);
		}
		if(getHandler() != null) {
			try {
				config = openApplicationConfiguration(
				    DeployConstants.ROOT_CFG_APPS,
				    ConfigurationHandler.WRITE_ACCESS);
				if(config != null) {
					config.deleteConfiguration();
				}
			} catch(ConfigurationException cex) {
				// $JL-EXC$
				if(location.beInfo()) {
					SimpleLogger.trace(Severity.INFO, location, 
						"ASJ.dpl_ds.000035",
					        "Cannot delete configuration <apps>. It might be missing.");
				}
			} catch(DeploymentException dex) {
				// $JL-EXC$
				if(location.beInfo()) {
					SimpleLogger.trace(Severity.INFO, location, 
						"ASJ.dpl_ds.000036",
					        "Cannot delete configuration <apps>. It might be missing.");
				}
			}

			try {
				deployConfig = openApplicationConfiguration(
				    DeployConstants.ROOT_CFG_DEPLOY,
				    ConfigurationHandler.WRITE_ACCESS);
				if(deployConfig != null) {
					deployConfig.deleteConfiguration();
				}
			} catch(ConfigurationException cex) {
				// $JL-EXC$
				if(location.beInfo()) {
					SimpleLogger.trace(Severity.INFO, location,
						"ASJ.dpl_ds.000037",
					        "Cannot delete configuration <deploy>. It might be missing.");
				}
			} catch(DeploymentException dex) {
				// $JL-EXC$
				if(location.beInfo()) {
					SimpleLogger.trace(Severity.INFO, location, 
						"ASJ.dpl_ds.000038",
					        "Cannot delete configuration <deploy>. It might be missing.");
				}
			}
			try {
				commitHandler();
			} catch(ConfigurationException cex) {
				final ServerDeploymentException sdex = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				    new String[] {"deployment rollback for application "
							+ getModuleID() + ".\nReason: "
							+ cex.toString() }, cex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					sdex.getLocalizedMessage(), sdex);
			}
		}
		if(PropManager.getInstance().isClearAfterFailure()) {
			DUtils.deleteDirectory(new File(PropManager.getInstance()
			    .getAppsWorkDir()
			    + getModuleID()));
			clearReader();
			cleanUpDeploymentFile();
		}
		shmCloseOnDeployFailed();
		// close shm component on deploy unsuccessful
	}

	/**
	 * Starts local deploying of application/ stand alone module.
	 * 
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	public void beginLocal() throws DeploymentException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		try {
			openHandler();
			config = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS, 
				ConfigurationHandler.READ_ACCESS);
			communicator.refreshDeploymentInfoFromDB(
				getModuleID(), config, getHandler());
		} finally {
			try {
				rollbackHandler();
			} catch(ConfigurationException ex) {
				final ServerDeploymentException sdex = new ServerDeploymentException(
				    ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				    new String[] {"local deploying of application ["
							+ getModuleID() + "].\nReason: " + ex.toString() },
				ex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					sdex.getLocalizedMessage(), sdex);
			}
		}
		if(containers == null || containers.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_AVAILABLE_CONTS_FOR_APP,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005036");
			throw sde;
		}
		for(int i = 0; i < containers.length; i++) {
			try {
				containers[i].notifyDeployedComponents(getModuleID(),
				    contProperties);
			} catch(WarningException wex) {
				addWarnings(wex.getWarnings());
			} catch(OutOfMemoryError oofme) {
				throw oofme;
			} catch(ThreadDeath td) {
				throw td;
			} catch(Throwable th) {
				ServerDeploymentException sde = new ServerDeploymentException(
				    ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				    new String[] {"notifying deployment of application "
				        + getModuleID()}, th);
				sde.setMessageID("ASJ.dpl_ds.005082");
				sde.setDcNameForObjectCaller(containers[i]);
				throw sde;
			}
		}
		communicator.registerApplicationManagedObject(getModuleID());
	}

	/**
	 * Prepares local deployment.
	 * 
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	public void prepareLocal() throws DeploymentException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
			    "Prepare local [{0}] of application [{1}]",
			    getTransactionType(), getModuleID());
		}
		updateContainerInCacheLocal();
	}

	/**
	 * Commits local deployment.
	 */
	public void commitLocal() {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
			    "Commit local [{0}] of application [{1}]",
			    getTransactionType(), getModuleID());
		}

		ctx.getTxCommunicator().unregisterReferences(getModuleID());
		this.setSuccessfullyFinished(true);

		setShmComponentStatusStopped();
		setShmComponentStartupMode();
	}

	/**
	 * Method used to rollback local deployment.
	 */
	public void rollbackLocal() {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
			    "Rollback local [{0}] of application [{1}]",
			    getTransactionType(), getModuleID());
		}
	}

	/**
	 * Method used to rollback prepare local deployment phase.
	 */
	public void rollbackPrepareLocal() {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
			    "Rollback prepare local [{0}] of application [{1}]",
			    getTransactionType(), getModuleID());
		}
	}

	@Override
	protected void makeComponents(File[] componentFiles,
	    ContainerDeploymentInfo containerInfo, Properties contProps,
	    ContainerInterface cont) throws DeploymentException {
		ApplicationDeployInfo tempInfo = null;
		if(cont == null) {
			return;
		}
		if(componentFiles == null) {
			return;
		}
		String contName = cont.getContainerInfo().getName();
		try {
			if(location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
				    "Start deploying on container [{0}]", contName);
			}
			tempInfo = cont.deploy(componentFiles, containerInfo, contProps);
		} catch(DeploymentException de) {
			addContainer(cont, contProps);
			throw de;
		} catch(Exception th) {
			addContainer(cont, contProps);
			ServerDeploymentException sde = new ServerDeploymentException(
			    ExceptionConstants.EXEPTION_DURING_CONTAINER_GENERATION,
			    new String[] {getModuleID(), contName}, th);
			sde.setMessageID("ASJ.dpl_ds.005089");
			throw sde;
		} catch(OutOfMemoryError oofmer) {
			addContainer(cont, contProps);
			throw oofmer;
		} catch(ThreadDeath td) {
			addContainer(cont, contProps);
			throw td;
		} catch(Error err) {
			addContainer(cont, contProps);
			ServerDeploymentException sde = new ServerDeploymentException(
			    ExceptionConstants.EXEPTION_DURING_CONTAINER_GENERATION,
			    new String[] {getModuleID(), contName}, err);
			sde.setMessageID("ASJ.dpl_ds.005089");
			throw sde;
		}
		this.processApplicationDeployInfo(tempInfo, componentFiles, cont, null);
	}

	private void checkAndClear() {
		File appRootDir = new File(PropManager.getInstance().getAppsWorkDir()
		    + getModuleID());
		DUtils.deleteDirectory(appRootDir);
	}

	@Override
	protected Hashtable<ContainerInterface, Properties> getConcernedContainers(
	    final Hashtable<String, File[]> allContFiles,
	    final ContainerDeploymentInfo containerInfo) {
		Hashtable<ContainerInterface, Properties> contsProps = 
			new Hashtable<ContainerInterface, Properties>();
		for(String contName : allContFiles.keySet()) {
			contsProps.put(ctx.getTxCommunicator().getContainer(contName),
			    (Properties) getProperties().clone());
		}
		return contsProps;
	}

	@Override
	protected void finalActions() throws WarningException, DeploymentException {
		if(location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
			    "Final actions in [{0}] for application [{1}].",
			    getTransactionType(), getModuleID());
		}
		// OncePerInstanceTransaction
		oncePerInstanceTransaction(isSuccessfullyFinished(), true);
	}
}