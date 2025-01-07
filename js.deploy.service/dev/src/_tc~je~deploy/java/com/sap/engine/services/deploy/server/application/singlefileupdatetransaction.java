/*
 * Copyright (c) 2000 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.deploy.server.application;

import java.io.File;
import java.io.IOException;
import java.security.Policy;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
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
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class is intended only for internal use by deploy service.
 * 
 * @author Rumiana Angelova
 * @version 6.30
 */
public class SingleFileUpdateTransaction extends ApplicationTransaction {
	private static final Location location = 
		Location.getLocation(SingleFileUpdateTransaction.class);

	private Configuration config = null;
	private FileUpdateInfo[] updateInfoes = null;
	private boolean needStartAfterFinish = false,
			needOncePerInstanceAfterFinish = true;
	private Status oldStatus = Status.UNKNOWN;
	private Properties props = new Properties();

	/**
	 * Create global single file update transaction.
	 * @param ctx
	 * @param appName
	 * @param updateInfoes
	 * @param props
	 * @throws DeploymentException
	 */
	public SingleFileUpdateTransaction(final DeployServiceContext ctx,
		final String appName, final FileUpdateInfo[] updateInfoes,
		final Properties props) throws DeploymentException {
		super(ctx);
		setModuleID(appName);
		setModuleType(DeployConstants.APP_TYPE);
		setTransactionType(DeployConstants.singleFileUpdate);
		this.updateInfoes = updateInfoes;
		// precaution - properties may be null;
		// containers do not normally include checks for null properties
		this.props = props != null ? props : new Properties();
		checkFileUpdateInfoes(updateInfoes);
	}

	private void checkFileUpdateInfoes(FileUpdateInfo[] updateInfoes)
			throws DeploymentException {
		if (updateInfoes == null || updateInfoes.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.MISSING_PARAMETERS,
				getTransactionType(), "");
			sde.setMessageID("ASJ.dpl_ds.005024");
			throw sde;
		}
		for (int i = 0; i < updateInfoes.length; i++) {
			if (updateInfoes[i] == null) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_PARAMETERS,
					getTransactionType(), getModuleID(), "");
				sde.setMessageID("ASJ.dpl_ds.005053");
				throw sde;
			} else if (updateInfoes[i].getArchiveEntryName() == null) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_PARAMETERS,
					getTransactionType(), getModuleID(),
					"Not specified archive entry name in FileUpdateInfo");
				sde.setMessageID("ASJ.dpl_ds.005053");
				throw sde;
			} else if (updateInfoes[i].getContainerName() == null) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_PARAMETERS,
					getTransactionType(), getModuleID(),
					"Not specified container name in FileUpdateInfo");
				sde.setMessageID("ASJ.dpl_ds.005053");
				throw sde;
			} else if (updateInfoes[i].getFileName() == null ||
				!new File(updateInfoes[i].getFileName()).exists()) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_PARAMETERS,
						getTransactionType(), getModuleID(),
						"File "	+ updateInfoes[i].getFileName() +
						" which is specified in FileUpdateInfo doesn't exist.");
				sde.setMessageID("ASJ.dpl_ds.005053");
				throw sde;
			}
		}
	}

	/**
	 * Create local single file update transaction.
	 * @param ctx
	 * @param appName
	 * @param containerNames
	 * @throws DeploymentException
	 */
	@SuppressWarnings("boxing")
	public SingleFileUpdateTransaction(final DeployServiceContext ctx,
		final String appName, final String[] containerNames)
		throws DeploymentException {
		super(ctx);
		setModuleID(appName);
		setModuleType(DeployConstants.APP_TYPE);
		setTransactionType(DeployConstants.singleFileUpdate);
		ContainerInterface ci = null;
		if (containerNames == null || containerNames.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_INFO_ABOUT_CONTS_RECEIVED,
				ctx.getClusterMonitorHelper().getCurrentServerId(),
				getTransactionType(), getModuleID());
			sde.setMessageID("ASJ.dpl_ds.005040");
			throw sde;
		}
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		DeploymentInfo info = communicator.getApplicationInfo(getModuleID());
		if (info == null) {
			info = refreshDeploymentInfoFromDB();
		}
		for (int i = 0; i < containerNames.length; i++) {
			ci = communicator.getContainer(containerNames[i]);
			if (ci == null) {
				//if (!info.isOptionalContainer(containerNames[i]))  - check deleted because of no real use cases	
					ServerDeploymentException sde = 
						new ServerDeploymentException(
							ExceptionConstants.NOT_AVAILABLE_CONTAINER,
							containerNames[i], getTransactionType(), 
							getModuleID());
					sde.setMessageID("ASJ.dpl_ds.005006");
					throw sde;
			} else if (!ci.getContainerInfo().isSupportingSingleFileUpdate()) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_SUPPORTED_OPERATION,
					containerNames[i], getTransactionType());
				sde.setMessageID("ASJ.dpl_ds.005054");
				throw sde;
			} else {
				addContainer(ci, null);
			}
		}
		if (this.containers == null || this.containers.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_AVAILABLE_CONTS_FOR_APP,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005036");
			throw sde;
		}

	}

	private ContainerDeploymentInfo getContainerDeployInfo() {
		return new ContainerDeploymentInfo(getModuleID(), config,
			new AppConfigurationHandlerImpl(getHandler()), null);
	}

	public void begin() throws DeploymentException {
		openHandler();
		config = openApplicationConfiguration(
			DeployConstants.ROOT_CFG_APPS,
			ConfigurationHandler.WRITE_ACCESS);
		final Hashtable<String, FileUpdateInfo[]> contFUInfoes = getDistribution();
		final Enumeration<String> conts = contFUInfoes.keys();
		final ContainerInterface[] cis = new ContainerInterface[contFUInfoes
				.size()];
		final TransactionCommunicator communicator = ctx.getTxCommunicator();
		DeploymentInfo info = communicator.getApplicationInfo(getModuleID());
		if (info == null) {
			info = refreshDeploymentInfoFromDB();
		}
		oldStatus = info.getStatus();

		int k = 0;
		while (conts.hasMoreElements()) {
			final String containerName = conts.nextElement();
			final ContainerInterface ci = communicator
					.getContainer(containerName);
			if (ci == null) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_AVAILABLE_CONTAINER,
					containerName, getTransactionType(), getModuleID());
				sde.setMessageID("ASJ.dpl_ds.005006");
				throw sde;
			}
			cis[k++] = ci;
		}
		ContainerDeploymentInfo containerInfo = getContainerDeployInfo();
		needStopApplicationPhase(info, cis, contFUInfoes, containerInfo);

		final File tmpDir = createTempDir();
		copyUpdatedFiles(tmpDir.getAbsolutePath());
		try {
			// The temp loader
			containerInfo.setLoader(null);
			for (int i = 0; i < cis.length; i++) {
				final ContainerInterface ci = cis[i];
				addContainer(ci, null);
				try {
					ci.makeSingleFileUpdate(contFUInfoes.get(ci
							.getContainerInfo().getName()), containerInfo,
							props);
				} catch (DeploymentException de) {
					throw de;
				} catch (OutOfMemoryError oofmer) {
					throw oofmer;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "making single file update of application "
							+ getModuleID() }, th);
					sde.setMessageID("ASJ.dpl_ds.005082");
					throw sde;
				}
			}

		} finally {
			DUtils.deleteDirectory(tmpDir);
		}

		if (this.containers == null || this.containers.length == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NO_AVAILABLE_CONTS_FOR_APP,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005036");
			throw sde;
		}
	}

	private void copyUpdatedFiles(final String dir) throws DeploymentException {
		for (int i = 0; i < updateInfoes.length; i++) {
			if (updateInfoes[i].getFileEntryName() != null
					&& updateInfoes[i].getFileName() != null
					&& new File(updateInfoes[i].getFileName()).exists()) {
				final File file = new File(dir
						+ File.separator
						+ updateInfoes[i].getFileEntryName().replace('/',
								File.separatorChar));
				if (!file.isDirectory()) {
					try {
						FileUtils.copyFile(new File(updateInfoes[i]
								.getFileName()), file);
					} catch (IOException ioex) {
						ServerDeploymentException sde = new ServerDeploymentException(
								ExceptionConstants.CANNOT_COPY_FILE,
								new Object[] {
										new File(updateInfoes[i].getFileName())
												.getPath(), file.getPath() });
						sde.setMessageID("ASJ.dpl_ds.005133");
						throw sde;
					}
				}
			}
		}
	}

	private File createTempDir() {
		final StringBuilder sb = new StringBuilder(PropManager.getInstance()
				.getAppServiceCtx().getServiceState().getWorkingDirectoryName())
				.append(File.separator).append(getModuleID().replace('/', '~'));

		final File root = new File(sb.toString());
		root.mkdirs();
		return root;
	}

	private Hashtable<String, FileUpdateInfo[]> getDistribution() {
		final Hashtable<String, FileUpdateInfo[]> res = new Hashtable<String, FileUpdateInfo[]>();

		for (int i = 0; i < this.updateInfoes.length; i++) {
			final String contName = this.updateInfoes[i].getContainerName();
			if (contName != null) {
				res.put(contName, addUpdateInfo(res.get(contName),
						updateInfoes[i]));
			}
		}
		return res;
	}

	private FileUpdateInfo[] addUpdateInfo(FileUpdateInfo[] all,
			FileUpdateInfo info) {
		if (all == null) {
			return new FileUpdateInfo[] { info };
		}
		FileUpdateInfo[] res = new FileUpdateInfo[all.length + 1];
		System.arraycopy(all, 0, res, 0, all.length);
		res[all.length] = info;
		return res;
	}

	public void beginLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Begin local phase of operation [{0}]",
					getTransactionType());
		}
		openHandler();
		try {
			config = openApplicationConfiguration(
				DeployConstants.ROOT_CFG_APPS,
				ConfigurationHandler.READ_ACCESS);
			ctx.getTxCommunicator().refreshDeploymentInfoFromDB(getModuleID(),
					config, getHandler());

			for (int i = 0; i < containers.length; i++) {
				try {
					containers[i].notifySingleFileUpdate(getModuleID(), config,
							null);
				} catch (WarningException wex) {
					addWarnings(wex.getWarnings());
				} catch (OutOfMemoryError oofme) {
					throw oofme;
				} catch (ThreadDeath td) {
					throw td;
				} catch (Throwable th) {
					final ServerDeploymentException sdex = new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "notifying single file update of application ["
									+ getModuleID() + "]" }, th);
					SimpleLogger.traceThrowable(Severity.ERROR, location, sdex
							.getLocalizedMessage(), th);
				}
			}
		} finally {
			try {
				rollbackHandler();
			} catch (ConfigurationException ce) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.CANNOT_COMMIT_HANDLER, new String[] {
								getTransactionType(), getModuleID() }, ce);
				sde.setMessageID("ASJ.dpl_ds.005026");
				throw sde;
			}
		}
	}

	public void prepare() throws DeploymentException {
		for (int i = 0; i < containers.length; i++) {
			try {
				containers[i].prepareSingleFileUpdate(getModuleID());
			} catch (WarningException wex) {
				addWarnings(wex.getWarnings());
			} catch (DeploymentException de) {
				throw de;
			} catch (OutOfMemoryError oofmer) {
				throw oofmer;
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable th) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "preparing single file update of application "
								+ getModuleID() }, th);
				sde.setMessageID("ASJ.dpl_ds.005082");
				throw sde;
			}
		}
	}

	public void prepareLocal() throws DeploymentException {
	}

	public void commit() {
		ApplicationDeployInfo info = null;
		final DeploymentInfo deployment = ctx.getTxCommunicator()
				.getApplicationInfo(getModuleID());
		String cName;
		boolean isDInfoChanged = false;
		for (int i = 0; i < containers.length; i++) {
			cName = containers[i].getContainerInfo().getName();
			try {
				info = containers[i].commitSingleFileUpdate(getModuleID());
			} catch (WarningException wex) {
				addWarnings(wex.getWarnings());
			} catch (OutOfMemoryError oofme) {
				throw oofme;
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable th) {
				final ServerDeploymentException sdex = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "committing single file update of application ["
								+ getModuleID() + "]" }, th);
				SimpleLogger.traceThrowable(Severity.ERROR, location, sdex
						.getLocalizedMessage(), th);
			}
			if (info != null) {
				isDInfoChanged = true;
				deployment.addContName_FilesForCL(cName, info
						.getFilesForClassloader());
			}
		}
		if (isDInfoChanged) {
			try {
				try {
					final DIWriter diWriter = EditorFactory.getInstance()
							.getDIWriter(deployment.getVersion());
					final Configuration deployConfig = 
						openApplicationConfiguration(
							DeployConstants.ROOT_CFG_DEPLOY, 
							ConfigurationHandler.WRITE_ACCESS);
					diWriter.modifyDeploymentInfo(config, deployConfig,
							deployment);
					commitHandler();
				} catch (DeploymentException dex) {
					SimpleLogger.traceThrowable(Severity.ERROR, location, dex
							.getLocalizedMessage(), dex);
				} finally {
					rollbackHandler();
				}
			} catch (ConfigurationException cex) {
				final ServerDeploymentException sdex = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "commit phase of operation ["
								+ getTransactionType() + "] for application ["
								+ getModuleID() + "].\nReason: "
								+ cex.toString() }, cex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, sdex
						.getLocalizedMessage(), cex);
			}
		}

		Policy.getPolicy().refresh();
		this.setSuccessfullyFinished(true);
	}

	public void commitLocal() {
		this.setSuccessfullyFinished(true);
	}

	public void rollback() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Rollback in [{0}] for application [{1}].",
					getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	public void rollbackLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Rollback local in [{0}] for application [{1}].",
					getTransactionType(), getModuleID());
		}
	}

	public void rollbackPrepare() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Rollback prepare in [{0}] for application [{1}].",
					getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	private void rollbackCommon() {
		try {
			if (getHandler() != null) {
				try {
					rollbackHandler();
				} catch (ConfigurationException cex) {
					final ServerDeploymentException sdex = new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "rolling back single file update of application ["
									+ getModuleID()
									+ "].\nReason: "
									+ cex.toString() }, cex);
					SimpleLogger.traceThrowable(Severity.ERROR, location, sdex
							.getLocalizedMessage(), cex);
				}
			}

			openHandler();
			config = openApplicationConfiguration(
					DeployConstants.ROOT_CFG_APPS,
					ConfigurationHandler.READ_ACCESS);
			if (containers != null) {
				for (int i = 0; i < containers.length; i++) {
					try {
						containers[i].rollbackSingleFileUpdate(getModuleID(),
								config);
					} catch (WarningException wex) {
						addWarnings(wex.getWarnings());
					} catch (OutOfMemoryError oofme) {
						throw oofme;
					} catch (ThreadDeath td) {
						throw td;
					} catch (Throwable th) {
						final ServerDeploymentException sdex = new ServerDeploymentException(
								ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
								new String[] { "rolling back single file update of application ["
										+ getModuleID() + "]" }, th);
						SimpleLogger.traceThrowable(Severity.ERROR, location,
								sdex.getLocalizedMessage(), th);
					}
				}
			}
		} catch (DeploymentException dex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, dex
					.getLocalizedMessage(), dex);
		} finally {
			try {
				rollbackHandler();
			} catch (ConfigurationException cex) {
				final ServerDeploymentException sdex = new ServerDeploymentException(
						ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
						new String[] { "rolling back single file update of application ["
								+ getModuleID()
								+ "].\nReason: "
								+ cex.toString() }, cex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, sdex
						.getLocalizedMessage(), cex);
			}
		}
	}

	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
					"Rollback prepare local in [{0}] for application [{1}].",
					getTransactionType(), getModuleID());
		}
	}

	private void needStopApplicationPhase(final DeploymentInfo info,
			final ContainerInterface[] cis,
			final Hashtable<String, FileUpdateInfo[]> contFUInfoes,
			ContainerDeploymentInfo containerInfo) throws DeploymentException {
		if (Status.STARTED.equals(info.getStatus())) {
			for (int i = 0; i < cis.length; i++) {
				final ContainerInterface ci = cis[i];
				try {
					if (ci.needStopOnSingleFileUpdate(contFUInfoes.get(ci
							.getContainerInfo().getName()), containerInfo,
							props)) {
						// set it before starting the transaction, to be able to
						// set
						// the correct status in the rollBack part.
						this.needStartAfterFinish = true;
						makeNestedParallelTransaction(new StopTransaction(
							getModuleID(), ctx, 
							ctx.getClusterMonitorHelper().findServers()));
						return;
					}
				} catch (WarningException wex) {
					addWarnings(wex.getWarnings());
				}
			}
			// the application will not be stopped, so its binaries should not
			// be downloaded.
			needOncePerInstanceAfterFinish = false;
		}
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
			oncePerInstanceTransaction(isSuccessfullyFinished(), true);
		} else {
			if (location.bePath()) {
				SimpleLogger.trace(
					Severity.PATH, location, null,
					"The needOncePerInstanceAfterFinish is [{0}] and it won't be invoked.",
					needOncePerInstanceAfterFinish);
			}
		}
		// StartTransaction
		if ((needStartAfterFinish && Status.STARTED.equals(oldStatus))
				|| Status.IMPLICIT_STOPPED.equals(oldStatus)) {
			try {
				makeNestedParallelTransaction(new StartTransaction(
					getModuleID(), ctx, 
					ctx.getClusterMonitorHelper().findServers()));
			} catch (DeploymentException dex) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, dex
						.getLocalizedMessage(), dex);
			}
		}
	}
}