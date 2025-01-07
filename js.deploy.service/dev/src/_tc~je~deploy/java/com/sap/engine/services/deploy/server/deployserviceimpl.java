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

package com.sap.engine.services.deploy.server;

import static com.sap.engine.services.deploy.server.DeployConstants.REFERENCE_TYPE_NAME_WEAK;
import static com.sap.engine.services.deploy.server.DeployConstants.RESOURCE_TYPE_APPLICATION;
import static com.sap.engine.services.deploy.server.DeployConstants.RESOURCE_TYPE_LIBRARY;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.rmi.PortableRemoteObject;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.LibraryMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.state.ServiceState;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.cross.ProtocolProvider;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.ApplicationInformation;
import com.sap.engine.services.deploy.DeployCallback;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.DeployServiceExt;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.command.AppReferencesGraph;
import com.sap.engine.services.deploy.command.ChangeReference;
import com.sap.engine.services.deploy.command.ContainerInfoCommand;
import com.sap.engine.services.deploy.command.DeploymentInfoCommand;
import com.sap.engine.services.deploy.command.GetStartUpModeCommand;
import com.sap.engine.services.deploy.command.GetStatusCommand;
import com.sap.engine.services.deploy.command.JavaVersionCommand;
import com.sap.engine.services.deploy.command.ListAppResources;
import com.sap.engine.services.deploy.command.ListApplicationsCommand;
import com.sap.engine.services.deploy.command.ListElementsCommand;
import com.sap.engine.services.deploy.command.ListRefsCommand;
import com.sap.engine.services.deploy.command.MigrationStatisticCommand;
import com.sap.engine.services.deploy.command.ReferenceGraphFindPath;
import com.sap.engine.services.deploy.command.StartApplicationCommand;
import com.sap.engine.services.deploy.command.StopApplicationCommand;
import com.sap.engine.services.deploy.command.UnlockApplicationCommand;
import com.sap.engine.services.deploy.command.UpdateFilesCommand;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.migration.CMigrationInterface;
import com.sap.engine.services.deploy.container.migration.exceptions.CMigrationException;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatistic;
import com.sap.engine.services.deploy.container.op.IOpConstants;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.application.AddAppInfoChangeTransaction;
import com.sap.engine.services.deploy.server.application.DeploymentTransaction;
import com.sap.engine.services.deploy.server.application.OncePerInstanceTransaction;
import com.sap.engine.services.deploy.server.application.RemoveTransaction;
import com.sap.engine.services.deploy.server.application.RuntimeTransaction;
import com.sap.engine.services.deploy.server.application.SingleFileUpdateTransaction;
import com.sap.engine.services.deploy.server.application.StartInitiallyTransaction;
import com.sap.engine.services.deploy.server.application.StartTransaction;
import com.sap.engine.services.deploy.server.application.StopTransaction;
import com.sap.engine.services.deploy.server.application.UpdateTransaction;
import com.sap.engine.services.deploy.server.application.UpdateWithSyncTransaction;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.library.DeployLibTransaction;
import com.sap.engine.services.deploy.server.library.LibraryTransaction;
import com.sap.engine.services.deploy.server.library.MakeReferencesTransaction;
import com.sap.engine.services.deploy.server.library.ReferencesTransaction;
import com.sap.engine.services.deploy.server.library.RemoveLibTransaction;
import com.sap.engine.services.deploy.server.library.RemoveReferencesTransaction;
import com.sap.engine.services.deploy.server.management.AppManagedObjectManager;
import com.sap.engine.services.deploy.server.prl.ParallelOperator;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.ServerState;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.remote.MessageResponse;
import com.sap.engine.services.deploy.server.remote.RemoteCaller;
import com.sap.engine.services.deploy.server.remote.RemoteCommandFactory;
import com.sap.engine.services.deploy.server.remote.RemoteCommandFactory.RemoteCommand;
import com.sap.engine.services.deploy.server.utils.ApplicationInitializer;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.DSRemoteException;
import com.sap.engine.services.deploy.server.utils.LoadContextUtils;
import com.sap.engine.services.deploy.server.utils.LockUtils;
import com.sap.engine.services.deploy.server.utils.ManagementListenerUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.engine.services.deploy.timestat.DeployOperationTimeStat;
import com.sap.engine.services.deploy.timestat.ITimeStatConstants;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.engine.services.deploy.zdm.DSRollingException;
import com.sap.engine.services.deploy.zdm.DSRollingPatch;
import com.sap.engine.services.deploy.zdm.DSRollingResult;
import com.sap.engine.services.deploy.zdm.utils.ApplicationComponent;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.tc.logging.Location;

/**
 * Class implementing com.sap.engine.services.deploy.DeployService interface.
 * This class is extended by {@link DeployServiceImplTimeStatWrapper}, which
 * adds some logging functionality. There is always only one instance of this
 * class in the corresponding JVM, created by <code>DeployServiceFrame</code>
 * via the <code>DeployServiceFactory</code>. This class is intended only for
 * internal use by deploy service. <p/>
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Monika Kovachka, Rumiana Angelova
 */
public class DeployServiceImpl extends PortableRemoteObject 
	implements DeployServiceExt, LocalDeployment, TransactionCommunicator,
		ClusterChangeListener, DSRollingPatch {
	
	private static final Location location = 
		Location.getLocation(DeployServiceImpl.class);

	// constants
	private static final String deployed_in_unavailable_container = "deployed in unavailable container ";
	private static final String[] defaultRemoteSupports = new String[] { "p4" };

	// final members

	private final Hashtable<String, ReferenceObject[]> applicationReferences;
	private final Containers mContainers;
	private final ClusterServicesAdapter clusterAdapter;
	private final AuthorizationChecker authChecker;
	private final ManagementListenerUtils mlUtils;
	private final DSChangeLog changeLog;

	private DeployBootstrapper dBootstraper;
	private LoadContextUtils clFactory;
	private Command[] cmds;
	private AppManagedObjectManager appMBeanManager;

	// Internal state field - the initial start is done. This flag is set
	// after the initial start of the applications.
	private boolean isInitialStartDone;
	private InitialStartTrigger isTrigger;
	private DeployServiceContext ctx;

	private static class RemoveReferenceException extends Exception {
		private static final long serialVersionUID = 1L;
		private final String[] warnings;

		RemoveReferenceException(String[] warnings) {
			this.warnings = warnings;
		}

		public String[] getWarnings() {
			return warnings;
		}
	}

	/**
	 * Called by DeployServiceFactory, when the deploy service frame is started.
	 * There is only one instance of DeployServiceImpl in the JVM.
	 * 
	 * @see DeployServiceFrame, DeployServiceFactory
	 * @throws RemoteException
	 * @throws ListenerAlreadyRegisteredException
	 * @throws ConfigurationException
	 */
	public DeployServiceImpl() throws RemoteException {
		applicationReferences = new Hashtable<String, ReferenceObject[]>();
		mContainers = Containers.getInstance();
		isTrigger = new InitialStartTrigger(this);
		clusterAdapter = new ClusterServicesAdapter(this, isTrigger);
		changeLog = new DSChangeLog(this);
		authChecker = new AuthorizationChecker();
		mlUtils = new ManagementListenerUtils();
	}

	private AuthorizationChecker getAuthorizationChecker() {
		return authChecker;
	}

	public DeployServiceContext getDeployServiceContext() {
		return ctx;
	}

	/**
	 * Called by {@link DeployServiceFrame} when the service is started.
	 * DeployServiceFrame is in the same package and by this reason needs only
	 * default access level.
	 * 
	 * @throws DeploymentException
	 */
	final void activate(final DeployServiceContext ctx)
		throws DeploymentException, ConfigurationException,
			ListenerAlreadyRegisteredException {

		this.ctx = ctx;
		cmds = registerCommands(ctx);
		appMBeanManager = new AppManagedObjectManager(
			changeLog, ctx.getClusterMonitorHelper().getCurrentInstanceId());
		final ReferenceResolver resolver = ctx.getReferenceResolver();
		dBootstraper = new DeployBootstrapper(
			this, ctx.getClusterMonitorHelper());
		final PropManager pm = PropManager.getInstance();
		clFactory = new LoadContextUtils(resolver,
			pm.getServerState().isValid4ContainerMigration());
		clusterAdapter.activate(resolver);
		isTrigger.activate();
		// registerServiceEventListener
		pm.getAppServiceCtx().getServiceState()
			.registerServiceEventListener(clusterAdapter);

		// registerContainerEventListener
		final int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE
				| ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE
				| ContainerEventListener.MASK_SERVICE_STARTED
				| ContainerEventListener.MASK_BEGIN_SERVICE_STOP
				| ContainerEventListener.MASK_COMPONENT_LOADED
				| ContainerEventListener.MASK_BEGIN_COMPONENT_UNLOAD
				| ContainerEventListener.MASK_CONTAINER_STARTED
				| ContainerEventListener.MASK_BEGIN_CONTAINER_STOP;
		pm.getAppServiceCtx().getServiceState()
			.registerContainerEventListener(mask, null, clusterAdapter);
		initializeApplications();
	}

	private void initializeApplications() throws DeploymentException {
		final ConfigurationHandler cfgHandler = ConfigUtils
				.getConfigurationHandler(PropManager.getInstance()
						.getConfigurationHandlerFactory(),
						"initializing applications");

		ApplicationInitializer.initializeApplications(cfgHandler,
				applicationReferences, DeployConstants.CURRENT_INSTANCE_CONFIG);

		final Iterator<String> apps = Applications.getNames().iterator();
		while (apps.hasNext()) {
			final String cApp = apps.next();
			addApplicationInfo(cApp, getApplicationInfo(cApp));
		}

		dBootstraper.prepareServer(cfgHandler);
	}

	/**
	 * Called by {@link DeployServiceFrame} to unregister listeners during the
	 * service stop. DeployServiceFrame is in the same package and by this
	 * reason needs only default access level.
	 */
	private void unregister() {
		final ServiceState sState = PropManager.getInstance()
				.getAppServiceCtx().getServiceState();
		sState.unregisterContainerEventListener();
		sState.unregisterClusterEventListener();
		sState.unregisterServiceEventListener();
	}

	final void deactivate() {
		if (clusterAdapter != null) {
			clusterAdapter.deactivate();
		}
		if (isTrigger != null) {
			isTrigger.deactivate();
		}
		unregister();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#deploy(java.lang.String,
	 * java.lang.String[], java.util.Properties)
	 */
	public String[] deploy(String earFile, String[] remoteSupport,
		Properties props) throws RemoteException {

		final String tagName = "Deploy";
		try {
			Accounting.beginMeasure(tagName, this.getClass());

			getAuthorizationChecker()
					.checkAuthorization(DeployConstants.deploy);
			if (props == null) {
				props = new Properties();
			}

			try {
				shutdownCheck();
				List<String> warnings = new ArrayList<String>();
				DeploymentTransaction deployTransaction = new DeploymentTransaction(
					earFile, checkSupport(remoteSupport, warnings), props, ctx);
				return deploy(deployTransaction, warnings);
			} catch (DeploymentException dex) {
				return (String[]) catchDeploymentExceptionWithDSRem(dex,
						"deploying ear file " + earFile);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#deploy(java.lang.String,
	 * java.lang.String, java.lang.String[], java.util.Properties)
	 */
	public String[] deploy(String archiveFile, String containerName,
		String[] remoteSupport, Properties props) throws RemoteException {
		final String tagName = "Deploy";
		try {
			Accounting.beginMeasure(tagName, this.getClass());

			getAuthorizationChecker()
					.checkAuthorization(DeployConstants.deploy);
			nullVerifier(props, "deploy application with Properties");

			try {
				shutdownCheck();
				List<String> warnings = new ArrayList<String>();
				DeploymentTransaction deployTransaction = 
					new DeploymentTransaction(archiveFile, containerName, 
						checkSupport(remoteSupport, warnings), props, ctx);
				return deploy(deployTransaction, warnings);
			} catch (DeploymentException dex) {
				return (String[]) catchDeploymentExceptionWithDSRem(dex,
						"deploying stand-alone module file " + archiveFile);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	private String[] deploy(DeploymentTransaction deployTransaction,
		List<String> warnings) throws DeploymentException, RemoteException {
		makeGlobalTransaction(deployTransaction);
		deployTransaction.addWarnings(warnings);
		try {
			DUtils.processWarningsAndErrors(deployTransaction);
		} catch (WarningException wex) {
			wex.setResult(deployTransaction.getResult());
			throw wex;
		}
		return deployTransaction.getResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#update(java.lang.String,
	 * java.util.Properties)
	 */
	public String[] update(String earFile, Properties props)
		throws RemoteException {
		final String tagName = "Update";
		try {
			Accounting.beginMeasure(tagName, this.getClass());
			getAuthorizationChecker()
					.checkAuthorization(DeployConstants.update);

			DSLog.tracePath(
				location, 
				"Starting update with [{0}] ear file.", earFile);
			try {
				shutdownCheck();
				UpdateTransaction updateTransaction = 
					new UpdateTransaction(earFile, props, ctx);
				if (getApplicationInfo(updateTransaction.getModuleID()) == null) {
					return deployApplicationDuringUpdate(earFile,
						defaultRemoteSupports, props, false, null);
				}
				return update(updateTransaction, null, true);
			} catch (DeploymentException dex) {
				return (String[]) catchDeploymentExceptionWithDSRem(dex,
					"updating ear file " + earFile);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	private String[] deployApplicationDuringUpdate(String file,
		String[] remoteSupport, Properties props, boolean isStandAlone,
		String container) throws RemoteException, WarningException {
		String[] res = null;
		String appName = null;
		String[] warningsArr = null;
		try {
			if (isStandAlone) {
				res = deploy(file, container, remoteSupport, props);
			} else {
				res = deploy(file, remoteSupport, props);
			}
			if (res != null && res.length > 0
					&& res[0].startsWith("Application : ")) {
				appName = res[0].substring("Application : ".length());
			}
		} catch (WarningException wex) {
			res = wex.getResult();
			warningsArr = wex.getWarnings();
			if (res != null && res.length > 0
					&& res[0].startsWith("Application : ")) {
				appName = res[0].substring("Application : ".length());
			}
		}
		if (appName != null &&
			!ServerState.SAFE_UPGRADE.equals(
				PropManager.getInstance().getServerState())) {
			try {
				startApplicationAndWait(appName);
			} catch (WarningException wex) {
				warningsArr = DUtils.concatArrays(
					warningsArr, wex.getWarnings());
			}
		}
		if (warningsArr != null) {
			WarningException wex = new WarningException();
			wex.setWarning(warningsArr);
			wex.setResult(res);
			throw wex;
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#update(java.lang.String,
	 * java.lang.String, java.lang.String[], java.util.Properties)
	 */
	public String[] update(String archiveFile, String containerName,
		String[] remoteSupport, Properties props) throws RemoteException {
		final String tagName = "Update";
		try {
			Accounting.beginMeasure(tagName, this.getClass());
			getAuthorizationChecker()
				.checkAuthorization(DeployConstants.update);
			nullVerifier(props, "update stand-alone with Properties");

			DSLog.tracePath(location, "Starting update with [{0}] stand-alone file.",
				archiveFile);
			try {
				shutdownCheck();
				List<String> warnings = new ArrayList<String>();
				UpdateTransaction updateTransaction = new UpdateTransaction(
					archiveFile, containerName, 
					checkSupport(remoteSupport, warnings), props, ctx);
				if (Applications.get(updateTransaction.getModuleID()) == null) {
					return deployApplicationDuringUpdate(archiveFile,
						remoteSupport, props, true, containerName);
				}
				return update(updateTransaction, warnings, true);
			} catch (DeploymentException dex) {
				return (String[]) catchDeploymentExceptionWithDSRem(dex,
					"updating stand-alone module file " + archiveFile);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	private String[] update(final UpdateTransaction updateTransaction,
		final List<String> warnings, final boolean throwWarnigException)
		throws DeploymentException, RemoteException {
		try {
			makeGlobalTransaction(updateTransaction);
		} finally {
			updateTransaction.restart();
		}
		updateTransaction.addWarnings(warnings);
		if (throwWarnigException) {
			try {
				DUtils.processWarningsAndErrors(updateTransaction);
			} catch (WarningException wex) {
				wex.setResult(updateTransaction.getResult());
				throw wex;
			}
		}
		return updateTransaction.getResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.zdm.DSRollingPatch#updateInstanceAndDB
	 * (com.sap.engine.services.deploy.zdm.utils.ApplicationComponent)
	 */
	public DSRollingResult updateInstanceAndDB(
		ApplicationComponent applicationComponent)
		throws DSRollingException {
		try {
			getAuthorizationChecker().checkAuthorization(
				DeployConstants.updateWithSync);
		} catch (RemoteException re) {
			throw new DSRollingException(
				"ASJ.dpl_ds.006140 " + re.getMessage(),
				(re.getCause() != null ? re.getCause() : re));
		}
		DSLog.tracePath(
			location, 
			"Starting rolling update of this instance and data base, where [{0}]",
			applicationComponent);
		try {
			shutdownCheck();
			List<String> warnings = new ArrayList<String>();

			final UpdateTransaction updateTransaction;
			final int instanceId = ctx.getClusterMonitorHelper()
				.getCurrentInstanceId();
			if (applicationComponent.isStandalone()) {
				updateTransaction = new UpdateTransaction(
					applicationComponent.getFilePath(), null, 
					defaultRemoteSupports, applicationComponent.getProperties(), 
					ctx, true);
			} else {
				updateTransaction = new UpdateTransaction(
					applicationComponent.getFilePath(), 
					applicationComponent.getProperties(), ctx, true);
			}
			if (Applications.get(updateTransaction.getModuleID()) == null) {
				throw new DSRollingException("ASJ.dpl_ds.006141 The "
					+ updateTransaction.getModuleID() + " is not deployed.");
			}
			update(updateTransaction, warnings, false);
			return DSRollingResult.createDSRollingResult(
				updateTransaction, instanceId, 
				ctx.getClusterMonitorHelper().getCurrentServerId());
		} catch (Exception ex) {
			return (DSRollingResult) catchDeploymentExceptionWithDSRol(ex,
				"rolling update of this instance and data base with "
				+ applicationComponent.getFilePath() + " file.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.zdm.DSRollingPatch#syncInstanceWithDB(
	 * com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName)
	 */
	@SuppressWarnings("deprecation")
	public DSRollingResult syncInstanceWithDB(ApplicationName applicationName)
		throws DSRollingException {
		try {
			getAuthorizationChecker().checkAuthorization(
				DeployConstants.updateWithSync);
		} catch (RemoteException re) {
			throw new DSRollingException(
				"ASJ.dpl_ds.006142 " + re.getMessage(),
				(re.getCause() != null ? re.getCause() : re));
		}
		DSLog.tracePath(
			location, 
			"Starting rolling synchronization of this instance with data base of [{0}] application.",
			applicationName);

		final DeploymentInfo dInfo = Applications.get(
			applicationName.getApplicationName());
		if (dInfo == null) {
			throw new DSRollingException("ASJ.dpl_ds.006143 The "
				+ applicationName.getApplicationName()
				+ " is not deployed.");
		}

		try {
			shutdownCheck();
			List<String> warnings = new ArrayList<String>();

			final int instanceId = ctx.getClusterMonitorHelper()
				.getCurrentInstanceId();

			final String contNames[] = dInfo.getContainerNames();
			// TODO - provide as parameters
			final Dictionary<String, Properties> props = 
				new Hashtable<String, Properties>();
			// TODO - provide as parameters

			final UpdateWithSyncTransaction updateWithSyncTransaction = 
				new UpdateWithSyncTransaction(
					applicationName.getApplicationName(), ctx, props,
					contNames, true);

			update(updateWithSyncTransaction, warnings, false);
			return DSRollingResult.createDSRollingResult(
				updateWithSyncTransaction, instanceId,
					ctx.getClusterMonitorHelper().getCurrentServerId());
		} catch (Exception ex) {
			return (DSRollingResult) catchDeploymentExceptionWithDSRol(ex,
				"rolling sync of this instance  with " + applicationName
				+ " application.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#singleFileUpdate(com.sap
	 * .engine.services.deploy.container.FileUpdateInfo[], java.lang.String,
	 * java.util.Properties)
	 */
	public void singleFileUpdate(FileUpdateInfo[] files, String appName,
		Properties props) throws java.rmi.RemoteException {
		final String tagName = "Single File Update";
		try {
			Accounting.beginMeasure(tagName, this.getClass());
			getAuthorizationChecker().checkAuthorization(
				DeployConstants.singleFileUpdate);
			compNameVerifier(appName, "single-file-update",
				RESOURCE_TYPE_APPLICATION);

			SingleFileUpdateTransaction sfuTrans = null;
			try {
				shutdownCheck();
				sfuTrans = new SingleFileUpdateTransaction(
					ctx, appName, files, props);
				makeGlobalTransaction(sfuTrans);
				DUtils.processWarningsAndErrors(sfuTrans);
			} catch (DeploymentException dex) {
				catchDeploymentExceptionWithDSRem(dex,
					"single file update of application " + appName);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#remove(java.lang.String)
	 */
	public void remove(String appID) throws RemoteException {
		final String tagName = "Remove";
		Accounting.beginMeasure(tagName, this.getClass());
		try {
			getAuthorizationChecker().checkAuthorization(
				DeployConstants.removeApp);
			compNameVerifier(appID, "remove", RESOURCE_TYPE_APPLICATION);

			try {
				shutdownCheck();
				RemoveTransaction tx = new RemoveTransaction(appID, ctx);
				makeGlobalTransaction(tx);
				RemoteException ex = tx.getResultException();
				if(ex != null) {
					throw ex;
				}
			} catch (DeploymentException dex) {
				catchDeploymentExceptionWithDSRem(
					dex, "removing application " + appID);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#listContainers(java.lang
	 * .String[])
	 */
	public String[] listContainers(final String[] serverNames)
		throws java.rmi.RemoteException {
		try {
			shutdownCheck();
			final ClusterMonitorHelper cmHelper =
				ctx.getClusterMonitorHelper();
			int[] serverIDs = cmHelper.getServerIDs(serverNames);
			int localIndex = cmHelper.findIndexOfCurrentServerId(serverIDs);
			String[] res = null;
			if (localIndex != -1) {
				res = listContainers();
				int[] temp = new int[serverIDs.length - 1];
				System.arraycopy(serverIDs, 0, temp, 0, localIndex);
				System.arraycopy(serverIDs, localIndex + 1, temp, 
					localIndex, temp.length - localIndex);
				serverIDs = temp;
			}
			final String[] remoteContainers = ctx.getRemoteCaller()
				.listContainersRemotely(serverIDs);
			return DUtils.concatArrays(res, remoteContainers);
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
			throw new DSRemoteException(
				"ASJ.dpl_ds.006144 Error occurred while listing containers on "
				+ Arrays.toString(serverNames) + " servers.", dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.LocalDeployment#listContainers()
	 */
	public String[] listContainers() {
		String[] containers = new String[mContainers.size()];
		int i = 0;
		for (Iterator<ContainerWrapper> it = mContainers.getAll().iterator(); 
			it.hasNext();) {
			containers[i] = it.next().getContainerInfo().getName();
			i++;
		}
		return containers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#listApplications(java.lang
	 * .String, java.lang.String[])
	 */
	public String[] listApplications(String containerName, String[] serverNames)
		throws RemoteException {
		return listGivenApplications(containerName, serverNames, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#listJ2EEApplications(java
	 * .lang.String, java.lang.String[])
	 */
	public String[] listJ2EEApplications(String containerName,
		String[] serverNames) throws java.rmi.RemoteException {
		return listGivenApplications(containerName, serverNames, true);
	}

	private String[] listGivenApplications(String containerName,
		String[] serverNames, boolean onlyJ2ee) throws RemoteException {
		try {
			String[] res = null;
			final ClusterMonitorHelper cmHelper = ctx.getClusterMonitorHelper();
			int[] serverIDs = cmHelper.getServerIDs(serverNames);
			int localIndex = cmHelper.findIndexOfCurrentServerId(serverIDs);
			if (localIndex != -1) {
				if (containerName == null) {
					if (onlyJ2ee) {
						res = listJ2EEApplications();
					} else {
						res = Applications.list();
					}
				} else {
					if (onlyJ2ee) {
						res = listJ2EEApplications(containerName);
					} else {
						res = listApplications(containerName);
					}
					int[] temp = new int[serverIDs.length - 1];
					System.arraycopy(serverIDs, 0, temp, 0, localIndex);
					System.arraycopy(serverIDs, localIndex + 1, temp, localIndex,
							temp.length - localIndex);
					serverIDs = temp;
				}
			}
			final String[] apps = ctx.getRemoteCaller().listAppsRemotely(
				containerName, serverIDs, onlyJ2ee);
			res = DUtils.concatArrays(res, apps);
			if (res == null) {
				res = new String[0];
			}
			return res;
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
			throw new DSRemoteException(
				"ASJ.dpl_ds.006145 Error occurred while listing the applications deployed in "
				+ containerName + " container on "
				+ Arrays.toString(serverNames) + " servers.", dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#listElements(java.lang.String
	 * , java.lang.String, java.lang.String[])
	 */
	public String[] listElements(String containerName, String appName,
		String[] serverNames) throws java.rmi.RemoteException {
		try {
			shutdownCheck();
			String[] res = null;
			final String moduleId = DUtils.getApplicationID(appName);
			final ClusterMonitorHelper cmHelper = ctx.getClusterMonitorHelper();
			int[] serverIDs = cmHelper.getServerIDs(serverNames);
			final int localIndex = cmHelper.findIndexOfCurrentServerId(serverIDs);
			if (localIndex != -1) {
				res = listElements(containerName, moduleId);
				int[] temp = new int[serverIDs.length - 1];
				System.arraycopy(serverIDs, 0, temp, 0, localIndex);
				System.arraycopy(serverIDs, localIndex + 1, temp, localIndex,
					temp.length - localIndex);
				serverIDs = temp;
			}
			String[] elements = ctx.getRemoteCaller()
				.listElementsRemotely(containerName, moduleId, serverIDs);
			res = DUtils.concatArrays(res, elements);

			if (res == null) {
				res = new String[0];
			}
			return DUtils.processListElement(res,
				deployed_in_unavailable_container);
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
			throw new DSRemoteException(
				"ASJ.dpl_ds.006146 Error occurred while listing the elements on "
				+ appName + " deployed in " + containerName
				+ " container on " + Arrays.toString(serverNames)
				+ " servers.", dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getClientJar(java.lang.String
	 * )
	 */
	public SerializableFile getClientJar(String applicationName)
		throws RemoteException {
		getAuthorizationChecker().checkAuthorization(
			DeployConstants.getClientJar);
		compNameVerifier(applicationName, "get-client-jar of",
			RESOURCE_TYPE_APPLICATION);

		try {
			shutdownCheck();
			SerializableFile[] res = null;
			File[] files = null;
			files = listClientJars(null, applicationName);

			if (files != null && files.length > 0) {
				res = new SerializableFile[files.length];

				for (int i = 0; i < files.length; i++) {
					res[i] = new SerializableFile(files[i]);
				}
			}

			return DUtils.mergeClientJars(res, applicationName, 
				PropManager.getInstance().getServiceWorkDir());
		} catch (DeploymentException dex) {
			return (SerializableFile) catchDeploymentExceptionWithDSRem(dex,
				"getting client jar of application " + applicationName);
		}
	}

	private File[] listClientJars(String containerName, String applicationName)
		throws DeploymentException {
		List<File> allComponents = new ArrayList<File>();
		File[] serverAppl = null;

		if (containerName == null) {
			Iterator<ContainerWrapper> containers = 
				mContainers.getAll().iterator();
			while (containers.hasNext()) {
				try {
					serverAppl = containers.next()
						.getClientJar(applicationName);
					if (serverAppl != null) {
						DUtils.addArrayToList(serverAppl, allComponents);
					}
				} catch (Exception e) {
					ServerDeploymentException sde = 
						new ServerDeploymentException(
							ExceptionConstants.ERROR_IN_GETTING_CLIENT_JAR,
							new String[] { applicationName }, e);
					sde.setMessageID("ASJ.dpl_ds.005063");
					throw sde;
				}
			}
		} else {
			ContainerInterface cont = mContainers.getContainer(containerName);
			if (cont != null) {
				serverAppl = cont.getClientJar(applicationName);
			}
			return serverAppl;
		}
		if (allComponents.size() > 0) {
			serverAppl = new File[allComponents.size()];
			allComponents.toArray(serverAppl);
		} else {
			serverAppl = null;
		}
		return serverAppl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#stopApplication(java.lang
	 * .String)
	 */
	public void stopApplication(String applicationName) throws RemoteException {
		// The authorization checking and name verification
		// have to be done only once.
		stopApplication(applicationName, (String[]) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#stopApplication(java.lang
	 * .String, java.lang.String[])
	 */
	public void stopApplication(String applicationName, String[] serverNames)
		throws RemoteException {
		startOrStopApplicationOnInstanceAuth(
			applicationName, false, serverNames);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#startApplication(java.lang
	 * .String)
	 */
	public void startApplication(String applicationName) throws RemoteException {
		// The authorization checking and name verification
		// have to be done only once.
		startOrStopApplicationOnInstanceAuth(
			applicationName, true, (String[]) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#startApplication(java.lang
	 * .String, java.lang.String[])
	 */
	public void startApplication(String applicationName, String[] serverNames)
		throws RemoteException {
		startOrStopApplicationOnInstanceAuth(
			applicationName, true, serverNames);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getApplicationStatus(java
	 * .lang.String)
	 */
	public String getApplicationStatus(final String applicationName)
		throws RemoteException {
		compNameVerifier(applicationName, "get-application-status of",
			RESOURCE_TYPE_APPLICATION);

		try {
			final String moduleId = DUtils.getApplicationID(applicationName);
			final DeploymentInfo info = Applications.get(moduleId);
			if (info != null) {
				return info.getStatus().getName();
			}
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_DEPLOYED,
				applicationName, "get its status");
			sde.setMessageID("ASJ.dpl_ds.005005");
			throw sde;
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
			throw new DSRemoteException("ASJ.dpl_ds.006155 "
				+ "Error in getting status of application "
				+ applicationName + ".", dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getApplicationStatus(java
	 * .lang.String, java.lang.String)
	 */
	public String getApplicationStatus(String applicationName, String serverName)
		throws RemoteException {
		compNameVerifier(applicationName, "get-application-status of",
			RESOURCE_TYPE_APPLICATION);

		if (PropManager.getInstance().getClElemName().equals(serverName)) {
			return getApplicationStatus(applicationName);
		}
		try {
			int[] serverIDs = ctx.getClusterMonitorHelper()
				.getServerIDs(new String[] { serverName });
			final String status = ctx.getRemoteCaller()
				.getApplicationStatusRemotely(applicationName, serverIDs[0]);
			if (status == null) {
				throw new DSRemoteException(
					"ASJ.dpl_ds.006156 Missing result from server " +
					serverName + " about status of application " +
					applicationName);
			}
			return status;
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
			throw new DSRemoteException(
				"ASJ.dpl_ds.006158 Error occurred while getting status of " +
				"application " + applicationName + ".", dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getApplicationStatusDescription
	 * (java.lang.String)
	 */
	public StatusDescription getApplicationStatusDescription(
		String applicationName) throws RemoteException {
		compNameVerifier(applicationName,
			"get-application-status-description of",
			RESOURCE_TYPE_APPLICATION);

		try {
			applicationName = DUtils.getApplicationID(applicationName);
			DeploymentInfo info = Applications.get(applicationName);
			if (info != null) {
				return info.getStatusDescription();
			} 
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_DEPLOYED,
					applicationName, "get its status");
				sde.setMessageID("ASJ.dpl_ds.005005");
				throw sde;
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
			throw new DSRemoteException(
				"ASJ.dpl_ds.006159 Error in getting status description " +
				"of application " + applicationName + ".", dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getApplicationStatusDescription
	 * (java.lang.String, java.lang.String)
	 */
	public StatusDescription getApplicationStatusDescription(
		String applicationName, String serverName) throws RemoteException {
		compNameVerifier(applicationName,
			"get-application-status-description of",
			RESOURCE_TYPE_APPLICATION);

		if (PropManager.getInstance().getClElemName().equals(serverName)) {
			return getApplicationStatusDescription(applicationName);
		}
		StatusDescription statDesc = null;
		try {
			final ClusterMonitorHelper cmHelper = ctx.getClusterMonitorHelper();
			final int[] serverIDs = cmHelper.getServerIDs(
				new String[] { serverName });
			final int localIndex = cmHelper.findIndexOfCurrentServerId(serverIDs);
			if (localIndex != -1) {
				statDesc = getApplicationStatusDescription(applicationName);
				if (statDesc != null) {
					if (location.beInfo()) {
						DSLog.traceInfo(location, "ASJ.dpl_ds.002024", 
							"Received from server [{0}] " +
							"status description [{1}] of application [{2}]",
							serverName, statDesc, applicationName);
					}
					return statDesc;
				}
				throw new DSRemoteException(
					"ASJ.dpl_ds.006160 Missing result from server "
					+ serverName
					+ " about status descrition of application "
					+ applicationName);
			}
			for (int i = 0; i < serverIDs.length; i++) {
				if (i != localIndex) {
					statDesc = ctx.getRemoteCaller()
						.getApplicationStatusDescriptionRemotely(
							applicationName, serverIDs[i]);
					if (statDesc != null) {
						if (location.beInfo()) {
							DSLog.traceInfo(location, "ASJ.dpl_ds.000467", 
								"Received from server [{0}] " +
								"status description [{1}] of application [{2}]",
								serverName, statDesc, applicationName);
						}
						return statDesc;
					}
					throw new DSRemoteException(
 						"ASJ.dpl_ds.006161 Missing result from server "
						+ serverName 
						+ " about status descrition of application "
						+ applicationName);
				}
			}
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
			throw new DSRemoteException(
				"ASJ.dpl_ds.006162 Error occurred " +
				"while getting status description of application "
				+ applicationName + ".", dex);
		}
		return statDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getApplicationStatusDescription
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public StatusDescription getApplicationStatusDescription(
		String providerName, String applicationName, String serverName)
		throws RemoteException {
		compNameVerifier(applicationName, "get-application-status of",
			RESOURCE_TYPE_APPLICATION);
		return getApplicationStatusDescription(
			getInternalAppID(providerName, applicationName), serverName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#deployLibrary(java.lang.
	 * String)
	 */
	public void deployLibrary(String jar) throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.deployLib);
		deployServerComponent(jar, null, LibraryTransaction.LIBRARY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#removeLibrary(java.lang.
	 * String)
	 */
	public void removeLibrary(String libName) throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.removeLib);
		compNameVerifier(libName, "remove", RESOURCE_TYPE_LIBRARY);
		removeServerComponent(DeployConstants.DEFAULT_PROVIDER_4_APPS_SAP_COM,
			libName, LibraryTransaction.LIBRARY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#removeLibrary(java.lang.
	 * String, java.lang.String)
	 */
	public void removeLibrary(String providerName, String libName)
		throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.removeLib);
		compNameVerifier(libName, "remove", RESOURCE_TYPE_LIBRARY);
		removeServerComponent(providerName, libName, LibraryTransaction.LIBRARY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployServiceExt#deployService(java.lang
	 * .String)
	 */
	public void deployService(String sda) throws RemoteException {
		getAuthorizationChecker().checkAuthorization(
			DeployConstants.deployService);
		DeployLibTransaction transaction = deployServerComponent(
			sda, null, LibraryTransaction.SERVICE);
		if (transaction != null && transaction.isSuccessfullyFinished()) {
			final ServiceMonitor sm = PropManager.getInstance()
				.getAppServiceCtx().getContainerContext()
					.getSystemMonitor().getService(
						transaction.getComponentRuntimeName());
			// sm cannot be null
			mContainers.addContainers(sm);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployServiceExt#removeService(java.lang
	 * .String, java.lang.String)
	 */
	public void removeService(String vendor, String name)
		throws RemoteException {
		getAuthorizationChecker().checkAuthorization(
			DeployConstants.removeService);
		compNameVerifier(name, "remove", DeployConstants.RESOURCE_TYPE_SERVICE);

		final ServiceMonitor sm = PropManager.getInstance().getAppServiceCtx()
			.getContainerContext().getSystemMonitor().getService(name);
		if (sm != null) {// sm can be null, if this service is not deployed
			mContainers.removeContainers(sm);
		}
		removeServerComponent(vendor, name, LibraryTransaction.SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployServiceExt#deployInterface(java.
	 * lang.String)
	 */
	public void deployInterface(String sda) throws RemoteException {
		getAuthorizationChecker().checkAuthorization(
			DeployConstants.deployInterface);
		deployServerComponent(sda, null, LibraryTransaction.INTERFACE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployServiceExt#removeInterface(java.
	 * lang.String, java.lang.String)
	 */
	public void removeInterface(String vendor, String name)
		throws RemoteException {
		getAuthorizationChecker().checkAuthorization(
			DeployConstants.removeInterface);
		compNameVerifier(name, "remove",
			DeployConstants.RESOURCE_TYPE_INTERFACE);
		removeServerComponent(vendor, name, LibraryTransaction.INTERFACE);
	}

	private DeployLibTransaction deployServerComponent(final String jar,
		final String name, final byte type) throws RemoteException {
		DeployLibTransaction transaction = null;
		try {
			shutdownCheck();
			transaction = new DeployLibTransaction(jar, name, type, ctx);
			transaction.setEnqueueLockNeeded(true);
			final TransactionManager txManager = ctx.getTxManager();
			txManager.registerTransaction(transaction);
			try {
				transaction.makeAllPhases();
			} finally {
				txManager.unregisterTransaction(transaction);
			}
		} catch (DeploymentException dex) {
			catchDeploymentExceptionWithDSRem(dex,
				"deploying server component " + name);
		}
		return transaction;
	}

	private void removeServerComponent(final String providerName,
		final String name, final byte type) throws RemoteException {
		try {
			List<String> warnings = new ArrayList<String>();
			shutdownCheck();
			String[] remoteSupport = null;
			remoteSupport = checkSupport(remoteSupport, warnings);
			RemoveLibTransaction transaction = new RemoveLibTransaction(
				providerName, name, type, ctx);
			transaction.setEnqueueLockNeeded(true);
			final TransactionManager txManager = ctx.getTxManager();
			txManager.registerTransaction(transaction);
			try {
				transaction.makeAllPhases();
				transaction.addWarnings(warnings);
			} finally {
				txManager.unregisterTransaction(transaction);
			}
			DUtils.processWarningsAndErrors(transaction);
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
			throw new DSRemoteException("ASJ.dpl_ds.006165 Error occurred " +
				"while removing server component " + name + ".", dex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#makeReferences(java.lang
	 * .String, java.lang.String[])
	 */
	public void makeReferences(String fromApplication, String[] refs)
		throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.makeRefs);
		compNameVerifier(fromApplication, "make reference(s) from",
			RESOURCE_TYPE_APPLICATION);

		if (refs == null || refs.length == 0) {
			return;
		}
		ReferenceObject[] references = new ReferenceObject[refs.length];
		for (int i = 0; i < references.length; i++) {
			references[i] = new ReferenceObject(refs[i],
				ReferenceObjectIntf.REF_TYPE_WEAK);
		}
		makeReferences(fromApplication, references);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#makeReferences(java.lang
	 * .String, java.util.Properties)
	 */
	public void makeReferences(String fromApplication, Properties toLibraries)
		throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.makeRefs);
		compNameVerifier(fromApplication, "make reference(s) from",
			RESOURCE_TYPE_APPLICATION);

		if (toLibraries == null || toLibraries.size() == 0) {
			return;
		}
		ReferenceObject[] references = new ReferenceObject[toLibraries.size()];
		int i = 0;
		Enumeration refs = toLibraries.keys();
		String ref = null;
		while (refs.hasMoreElements()) {
			ref = (String) refs.nextElement();
			references[i] = new ReferenceObject(
				ref, toLibraries.getProperty(ref));
		}
		makeReferences(fromApplication, references);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#removeReferences(java.lang
	 * .String, java.lang.String[])
	 */
	public void removeReferences(String fromApplication, String[] toLibraries)
		throws RemoteException {
		getAuthorizationChecker()
			.checkAuthorization(DeployConstants.removeRefs);
		compNameVerifier(fromApplication, "remove reference(s) from",
			RESOURCE_TYPE_APPLICATION);

		try {
			shutdownCheck();
			if (fromApplication.startsWith("service:")) {
				throw new DSRemoteException(
					"ASJ.dpl_ds.006166 It is not allowed to change " +
					"references of services after the service is deployed.");
			}
			if (fromApplication.startsWith("library:")) {
				throw new DSRemoteException(
					"ASJ.dpl_ds.006167 It is not allowed to change " +
					"references of libraries after the library is deployed.");
			}
			if (fromApplication.startsWith("interface:")) {
				throw new DSRemoteException(
					"ASJ.dpl_ds.006168 It is not allowed to change " +
					"references of interfaces after the interface is deployed.");
			}
			if (toLibraries == null || toLibraries.length == 0) {
				return;
			}
			fromApplication = DUtils.getApplicationID(fromApplication);
			DeploymentInfo info = Applications.get(fromApplication);
			if (info == null) {
				ReferenceObject[] refs = 
					applicationReferences.get(fromApplication);
				String warnings[] = null;
				if (refs != null) {
					warnings = evaluateReferences(
						fromApplication, refs, toLibraries);
					final ConfigurationHandler handler = ConfigUtils
						.getConfigurationHandler(PropManager.getInstance()
							.getConfigurationHandlerFactory(),
								"removing application references");
					try {
						final Configuration deployCfg = ConfigUtils
							.getSubConfiguration(handler,
								DeployConstants.ROOT_CFG_DEPLOY,
								fromApplication.substring(0,
									fromApplication.indexOf("/")),
								fromApplication.substring(
									fromApplication.indexOf("/") + 1));
						final DIWriter diWriter = EditorFactory.getInstance()
							.getDIWriter(deployCfg);
						diWriter.modifyReferences(deployCfg, refs);
						ConfigUtils.commitHandler(handler,
							"removing application references");
					} catch (ConfigurationException ce) {
						ServerDeploymentException sde = 
							new ServerDeploymentException(
								ExceptionConstants.CANNOT_STORE_REFERENCES_IN_DB,
								new String[] { "removing references from not deployed application "
									+ fromApplication }, ce);
						sde.setMessageID("ASJ.dpl_ds.005094");
						sde.setDcNameForObjectCaller(handler);
						throw sde;
					} finally {
						ConfigUtils.rollbackHandler(handler,
							"removing application references");
					}
				} else {
					warnings = new String[toLibraries.length];
					for (int i = 0; i < toLibraries.length; i++) {
						warnings[i] = "Reference from " + fromApplication
							+ " to " + toLibraries[i] + " doesn't exist.";
					}
				}
				final RemoteCommand cmd = createRemoveRefsTxCmd(
					fromApplication, toLibraries);
				final RemoteCaller remote = ctx.getRemoteCaller();
				final MessageResponse[] responses = 
					remote.sendAndWait(cmd, 
						ctx.getClusterMonitorHelper().findServers());
				if (PropManager.getInstance().isStrictJ2eeChecks()) {
					WarningException wex = null;
					if (warnings != null && warnings.length > 0) {
						wex = new WarningException();
						wex.setWarning(DUtils.generateResultMessage(warnings,
							"Warning", ctx.getClusterMonitorHelper()
								.getCurrentServerId(), 
							DeployConstants.removeRefs, fromApplication));
					}
					if (responses != null) {
						String transWarnings[] = null;
						for (int i = 0; i < responses.length; i++) {
							transWarnings = DUtils.generateResultMessage(
								responses[i].getWarnings(), "Warning", 
								responses[i].getClusterID(),
									DeployConstants.removeRefs,
									fromApplication);
							if (transWarnings != null) {
								for (int j = 0; j < transWarnings.length; j++) {
									if (wex == null) {
										wex = new WarningException();
									}
									wex.addWarning(transWarnings[j]);
								}
							}
						}
					}
					if (wex != null) {
						throw wex;
					}
				}
			} else {
				RemoveReferencesTransaction transaction = 
					new RemoveReferencesTransaction(
						fromApplication, toLibraries, ctx);
				transaction.setEnqueueLockNeeded(true);
				final TransactionManager txManager = ctx.getTxManager();
				txManager.registerTransaction(transaction);
				try {
					transaction.makeAllPhases();
				} finally {
					txManager.unregisterTransaction(transaction);
				}
				DUtils.processWarningsAndErrors(transaction);
			}
		} catch (DeploymentException dex) {
			catchDeploymentExceptionWithDSRem(dex,
				"removing references from application " + fromApplication);
		} catch (WarningException wex) {
			if (PropManager.getInstance().isStrictJ2eeChecks()) {
				throw wex;
			}
		}
	}

	private RemoteCommand createRemoveRefsTxCmd(
		final String fromApplication, final String[] toLibraries) {
		Hashtable<String, Object> table = new Hashtable<String, Object>();
		table.put("command", DeployConstants.removeRefs);
		table.put("application_name", fromApplication);
		table.put("ref_objects", toLibraries);
		return RemoteCommandFactory.createMakeTransactionCmd(
			table, PropManager.getInstance().getClElemID());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.DeployService#getSupports()
	 */
	public String[] getSupports() throws java.rmi.RemoteException {
		String[] names = null;
		ProtocolProvider[] monitors = ((CrossInterface) 
			PropManager.getInstance().getAppServiceCtx().getContainerContext()
				.getObjectRegistry().getProvidedInterface(
					DeployConstants.INTERFACE_CROSS)).getProviders();
		if (monitors != null) {
			names = new String[monitors.length];
			for (int i = 0; i < monitors.length; i++) {
				names[i] = monitors[i].getName();
			}
		}

		if (names == null || names.length == 0 || names[0] == null) {
			names = new String[] { "p4" };
		}
		return names;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.LocalDeployment#listApplications()
	 */
	public String[] listApplications() {
		return Applications.getNames().toArray(new String[Applications.size()]);
	}

	private String[] listJ2EEApplications() {
		return getJ2EEApplications().keySet().toArray(new String[0]);
	}

	/**
	 * Locks application for the specified operation. Called by
	 * DeployCommunicatorImpl which is in the same package and therefore needs
	 * only default access level.
	 * 
	 * @param appName
	 *            the application to be locked.
	 * @param transactionType
	 *            transaction for some operation.
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	final void lockApplication(String appName, String transactionType)
		throws DeploymentException {
		ctx.getTxManager().lockApplication(appName, transactionType,
			LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator
	 * #getContainers()
	 */
	public Enumeration getContainers() {
		final Iterator internal = mContainers.getAll().iterator();
		return new Enumeration() {
			public Object nextElement() {
				return internal.next();
			}

			public boolean hasMoreElements() {
				return internal.hasNext();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.TransactionCommunicator#getContainer
	 * (java.lang.String)
	 */
	public ContainerInterface getContainer(String contName) {
		return (contName != null) ? mContainers.getContainer(contName) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.TransactionCommunicator#bindLoader
	 * (java.lang.String,
	 * com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo)
	 */
	public ClassLoader bindLoader(DeploymentInfo deployment)
		throws DeploymentException {
		if (deployment == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.D_INFO_NULL, "UNKNOWN",
				"bind application loader");
			sde.setMessageID("ASJ.dpl_ds.005000");
			throw sde;
		}
		if (!deployment.isJ2EEApplication()) {
			// The FS and DB stuff don't need loader.
			// WARNING: do NOT let the FS deployable to have loader changed from
			// DS,
			// because this will brake the Portal.
			return null;
		}
		{// check the application status
			final byte status = getStatus(deployment.getApplicationName());
			if (Status.STOPPED.getId().byteValue() == status || 
				// STOPPED in case of migration
				Status.STARTING.getId().byteValue() == status) { 
				// STARTING in case of regular application start continue
			} else {
				throw new IllegalStateException(
					"ASJ.dpl_ds.006169 Cannot create a class loader for "
					+ deployment.getApplicationName() + " application, because its status is "
					+ Status.getStatusByID(status) + ".");
			}
		}
		if (PropManager.getInstance().isProductiveMode()) {
			// workaround due to ByD escalation for leaking application loaders
			removeApplicationLoader(deployment.getApplicationName(), true);
		}
		return clFactory.defineSharedApplicationLoader(deployment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * removeApplicationLoader(java.lang.String)
	 */
	public void removeApplicationLoader(String appName)
		throws DeploymentException {
		removeApplicationLoader(appName, false);
	}

	private void removeApplicationLoader(String appName, boolean isError2Exist)
		throws DeploymentException {
		LoadContextUtils.unregisterLoader(appName, isError2Exist);
		LoadContextUtils.unregisterLoader(appName
			+ DeployConstants.LIBRARY_LOADER_SUFFIX, isError2Exist);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.ClusterChangeListener#
	 * clusterElementReady()
	 */
	public void clusterElementReady() {
		long _start = System.currentTimeMillis();
		final ClassLoader threadLoader = Thread.currentThread()
			.getContextClassLoader();
		final ServerState sState = PropManager.getInstance().getServerState();

		try {
			shutdownCheck();
			Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
			try {
				// Bootstrap must be performed always independently of the
				// service state and mode.
				dBootstraper.start();
			} catch (DeploymentException de) {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006359",
					"Exception on starting deploy bootstrapper", de);
			}

			if (sState.isValid4ContainerMigration()) {
				CMigrationInvoker.getInstance().start(ctx);
			} else if (ServerState.SAFE_DEPLOY.equals(sState)) {
				// do nothing in safe/deploy
			} else if (ServerState.SAFE_UPGRADE.equals(sState)) {
				// do nothing in safe/upgrade
			} else {
				parallelStartApplicationInitially();
			}
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, dex);
		} finally {
			long _est = System.currentTimeMillis() - _start;
			Thread.currentThread().setContextClassLoader(threadLoader);
			PropManager.getInstance().getAppServiceCtx().getContainerContext()
				.getDeployContext().applicationsStarted();

			if (sState.isValid4ContainerMigration()) {
				try {
					if (location.beInfo()) {
						DSLog.traceInfo(location, "ASJ.dpl_ds.000469", "\n{0}", 
							CAConvertor.toString(
								getCMigrationStatistic(), "     "));
					}
				} catch (RemoteException re) {
					DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000470",
						"Cannot retrieve the migration statistic.", re);
				}
			} else if (ServerState.SAFE_DEPLOY.equals(sState)) {
				if (location.beInfo()) {
					DSLog.traceInfo(location, "ASJ.dpl_ds.000471",
						"The applications will NOT be started initially, " +
						"because the server state is [{0}]", sState.getName());
				}
				setStatusDescToAllApps(
					StatusDescriptionsEnum.INITIALLY_NOT_STARTED_BECAUSE_OF_SERVER_STATE,
					new Object[] { sState.getName() });
			} else if (ServerState.SAFE_UPGRADE.equals(sState)) {
				if (location.beInfo()) {
					DSLog.traceInfo(location, "ASJ.dpl_ds.000472",
						"The applications will NOT be started initially, " +
						"because the server state is [{0}]", sState.getName());
				}
				setStatusDescToAllApps(
					StatusDescriptionsEnum.INITIALLY_NOT_STARTED_BECAUSE_OF_SERVER_STATE,
					new Object[] { sState.getName() });
			} else {
				if (location.beInfo()) {
					DSLog.traceInfo(location, "ASJ.dpl_ds.000473",
						"The initial application start up finished for [{0}] ms." +
						" The server state is [{1}]", _est, sState.getName());
				}
			}
		}
		initialStartDone();
	}

	private void initialStartDone() {
		DSLog.traceDebug(location, "The initial start of the applications is done. "
			+ "Active locks are: {0}", ctx.getLockManager().dumpLocks());
		isInitialStartDone = true;
		isTrigger = null;
	}

	/**
	 * Method for parallel start of applications.
	 */
	private void parallelStartApplicationInitially()
		throws ServerDeploymentException {
		final DeployEvent event = createInitialStartApplications();
		final Set<Component> applicationNames = 
			new ApplicationStatusResolver(ctx.getClusterMonitorHelper()
				.getCurrentInstanceId())
				.getApplicationNamesWhichHasToBeStarted();
		fireInitialStartApplications(event);
		try {
			new ParallelOperator(this)
				.initialParallelApplicationStart(applicationNames);
		} finally {
			event.setAction(DeployEvent.LOCAL_ACTION_FINISH);
			fireInitialStartApplications(event);
		}
	}

	private DeployEvent createInitialStartApplications() {
		final DeployEvent event = new DeployEvent(null,
			DeployEvent.LOCAL_ACTION_START, ctx.getEventSystem()
				.defineActionType(DeployConstants.initialStartApplications),
			PropManager.getInstance().getClElemName());
		event.setWhoCausedGroupOperation("deploy");
		return event;
	}

	private void fireInitialStartApplications(DeployEvent event) {
		try {
			ctx.getEventSystem().fireDeployEvent(
				event, DeployConstants.APP_TYPE, null);
		} catch (OutOfMemoryError oofmer) {
			throw oofmer;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION, new String[] {
					"initial starting", "" }, th);
			sde.setMessageID("ASJ.dpl_ds.005029");
			DSLog.logErrorThrowable(location, sde);
		}
	}

	/**
	 * Helper method for setting a status description to all available
	 * applications.
	 * 
	 * @param statDescEnum
	 * @param oParams
	 */
	private void setStatusDescToAllApps(StatusDescriptionsEnum statDescEnum,
		Object[] oParams) {
		DeploymentInfo[] allDplInfos = getAllDeploymentInfoes();
		for (int i = 0; i < allDplInfos.length; i++) {
			allDplInfos[i].setStatusDescription(statDescEnum, oParams);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.ClusterChangeListener#elementJoin
	 * (com.sap.engine.frame.cluster.ClusterElement)
	 */
	public void elementJoin(ClusterElement element) {
		ctx.getEventSystem().serverAdded(element.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.ClusterChangeListener#elementLoss
	 * (com.sap.engine.frame.cluster.ClusterElement)
	 */
	public void elementLoss(ClusterElement element) {
		ctx.getTxManager().elementLoss(element);
	}

	// TODO we must see if this cannot be replaced/combined with
	// isInternalAppStatusTriggered property
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.ClusterChangeListener#markForShutdown
	 * ()
	 */
	public void markForShutdown() {
		ctx.markForShutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.ClusterChangeListener#getCommands()
	 */
	public Command[] getCommands() {
		return cmds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.LocalDeployment#beginLocalTransaction
	 * (java.util.Hashtable, int)
	 */
	public MessageResponse beginLocalTransaction(
		final Map<String, Object> commandTable,
		final int fromClusterId) throws DeploymentException,
		ComponentNotDeployedException {
		try {
			DTransaction trans = createLocalTransaction(
				commandTable, fromClusterId);
			if (trans != null) {
				final TransactionManager txManager = ctx.getTxManager();
				TransactionStatistics stat = trans.getCurrentStatistics();
				// It is very important to set this flag to false,
				// in order to avoid deadlock.
				trans.setEnqueueLockNeeded(false);
				txManager.registerTransaction(trans);
				try {
					if (trans.isNeeded()) {
						trans.makeAllPhasesLocal();
					} else {
						trans.notNeeded();
					}
				} finally {
					txManager.unregisterTransaction(trans);
				}
				return new MessageResponse(
					ctx.getClusterMonitorHelper().getCurrentServerId(), 
					stat.getWarnings(),	stat.getErrors(), null);
			}
			return null;
		} catch (RemoveReferenceException ex) {
			return new MessageResponse(PropManager.getInstance().getClElemID(),
				ex.getWarnings(), null, null);
		}
	}

	@SuppressWarnings("boxing")
    private DTransaction createLocalTransaction(
		final Map<String, Object> commandTable,
		final int fromClusterId) throws DeploymentException,
		ComponentNotDeployedException, ServerDeploymentException,
		RemoveReferenceException {
		String appName = (String) commandTable.get(RemoteCaller.APP_NAME);
		String transactionType = (String) commandTable.get(RemoteCaller.COMMAND);
		String[] containers = (String[]) commandTable.get("containers");
		Dictionary<String, Properties> containerProps = 
			new Hashtable<String, Properties>();
		Properties props = null;

		if (containers != null) {
			for (int i = 0; i < containers.length; i++) {
				props = (Properties) commandTable.get("properties:"
					+ containers[i]);
				if (props != null) {
					containerProps.put(containers[i], props);
				}
			}
		}

		Object obj = commandTable.get("object");
		Date last_change = (Date) commandTable.get("last_change");

		DTransaction trans = null;
		final RemoteCaller remote = ctx.getRemoteCaller();
		if (transactionType.equals(DeployConstants.deploy)) {
			trans = new DeploymentTransaction(
				appName, ctx, containerProps, containers);
		} else if (transactionType.equals(DeployConstants.update)) {
			trans = new UpdateTransaction(
				appName, ctx, containerProps, containers);
		} else if (transactionType.equals(DeployConstants.updateWithSync)) {
			trans = new UpdateWithSyncTransaction(
				appName, ctx, containerProps, containers);
		} else if (transactionType.equals(DeployConstants.startInitiallyApp)) {
			final int dlSrvs[] = (int[]) commandTable.get(
				DeployConstants.DOWNLOAD);
			final boolean isDownloadNeeded = ctx.getClusterMonitorHelper()
				.findIndexOfCurrentServerId(dlSrvs) != -1;
			trans = new StartInitiallyTransaction(appName, ctx,
					isDownloadNeeded);
		} else if (transactionType.equals(DeployConstants.stopApp)) {
			try {
				String phase = (String) commandTable.get(DeployConstants.phase);
				if (phase != null && phase.equals(DeployConstants.force)) {
					trans = new StopTransaction(appName, null, ctx);
				} else {
					trans = new StopTransaction(
						appName, ctx, fromClusterId, containers);
				}
			} catch (DeploymentException dex) {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006360",
					"Exception on trying to create stop transaction for remote node",
					dex);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				dex.printStackTrace(new PrintStream(baos));
				remote.sendRespond(appName, transactionType, fromClusterId,
					null, new String[] { baos.toString() });
				return null;
			}
		} else if (transactionType.equals(DeployConstants.startApp)) {
			String phase = (String) commandTable.get(DeployConstants.phase);
			boolean readDIFromDB = Boolean.parseBoolean(
				(String) commandTable.get(DeployConstants.READ_DI_FROM_DB));
			try {
				if (phase != null && phase.equals(DeployConstants.force)) {
					trans = new StartTransaction(
						appName, ctx, readDIFromDB, null);
				} else {
					trans = new StartTransaction(
						appName, ctx, fromClusterId, containers, readDIFromDB);
				}
			} catch (DeploymentException dex) {
				DSLog.logErrorThrowable(location, dex);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				dex.printStackTrace(new PrintStream(baos));
				remote.sendRespond(appName, transactionType, fromClusterId,
					null, new String[] { baos.toString() });
				return null;
			}
		} else if (transactionType.equals(DeployConstants.oncePerInstance)) {
			trans = new OncePerInstanceTransaction(appName, ctx);
		} else if (transactionType.equals(DeployConstants.removeApp)) {
			trans = new RemoveTransaction(appName, 
				(String[])commandTable.get("containers"),
				ctx, true);
		} else if (transactionType.equals(DeployConstants.deployLib)) {
			// trans = new DeployLibTransaction((String)
			// commandTable.get("library_files"), (String)
			// commandTable.get("library_name"), ((Byte)
			// commandTable.get("type")).byteValue(), this);
		} else if (transactionType.equals(DeployConstants.removeLib)) {
			// trans = new RemoveLibTransaction((String)
			// commandTable.get("library_name"), ((Byte)
			// commandTable.get("type")).byteValue(), this);
		} else if (transactionType.equals(DeployConstants.makeRefs)) {
			DeploymentInfo info = Applications.get(appName);
			if (info == null) {
				applicationReferences.put(appName,
					(ReferenceObject[]) commandTable.get("ref_objects"));
			} else {
				trans = new MakeReferencesTransaction(appName,
					(ReferenceObject[]) commandTable.get("ref_objects"),
					ctx);
				((ReferencesTransaction) trans)
					.setTimeOfLastChange(last_change);
			}
		} else if (transactionType.equals(DeployConstants.removeRefs)) {
			DeploymentInfo info = Applications.get(appName);
			if (info == null) {
				String warnings[] = null;
				String[] forRemove = (String[]) commandTable.get("ref_objects");
				ReferenceObject[] existing = applicationReferences.get(appName);
				if (existing != null) {
					warnings = evaluateReferences(appName, existing, forRemove);
				} else {
					warnings = new String[forRemove.length];
					for (int i = 0; i < forRemove.length; i++) {
						warnings[i] = "Reference from " + appName + " to "
							+ forRemove[i] + " doesn't exist.";
					}
				}
				if (warnings != null && warnings.length > 0) {
					throw new RemoveReferenceException(warnings);
				}
			} else {
				trans = new RemoveReferencesTransaction(appName,
					(String[]) commandTable.get("ref_objects"), ctx);
				((ReferencesTransaction) trans)
					.setTimeOfLastChange(last_change);
			}
		} else if (transactionType.equals(DeployConstants.runtimeChanges)) {
			if (containers == null || containers.length == 0) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NO_INFO_ABOUT_CONTAINER_RECEIVED,
					DeployConstants.runtimeChanges,
					String.valueOf(fromClusterId), appName,
					ctx.getClusterMonitorHelper().getCurrentServerId());
				sde.setMessageID("ASJ.dpl_ds.005001");
				throw sde;
			}
			trans = new RuntimeTransaction(appName, ctx, containers[0]);
		} else if (transactionType.equals(DeployConstants.singleFileUpdate)) {
			if (containers == null) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NO_INFO_ABOUT_CONTAINER_RECEIVED,
					appName, String.valueOf(fromClusterId),
					ctx.getClusterMonitorHelper().getCurrentServerId(),
					"single file update");
				sde.setMessageID("ASJ.dpl_ds.005001");
				throw sde;
			}
			trans = new SingleFileUpdateTransaction(ctx, appName, containers);
		} else if (transactionType.equals(DeployConstants.appInfoChange)) {
			trans = new AddAppInfoChangeTransaction(appName, ctx, containers,
				obj instanceof AdditionalAppInfo ? 
					(AdditionalAppInfo) obj	: null);
		}
		return trans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.LocalDeployment#getTransaction(
	 * java.lang.String, java.lang.String)
	 */
	public DTransaction getTransaction(String appName, String transactionType) {
		return ctx.getTxManager().getTransaction(appName, transactionType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.LocalDeployment#listApplications
	 * (java.lang.String)
	 */
	public String[] listApplications(String containerName) {
		return listGivenApplications(
			containerName, Applications.getApplicationsMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.LocalDeployment#listJ2EEApplications
	 * (java.lang.String)
	 */
	public String[] listJ2EEApplications(String containerName) {
		return listGivenApplications(containerName, getJ2EEApplications());
	}

	private String[] listGivenApplications(final String containerName,
		final Map<String, DeploymentInfo> appName2DplInfo) {
		String[] allApps = null;
		if (containerName == null) {
			return appName2DplInfo.keySet().toArray(
				new String[appName2DplInfo.size()]);
		} 
		List<String> res = new ArrayList<String>();
		Iterator<DeploymentInfo> infoes = appName2DplInfo.values().iterator();
		DeploymentInfo info = null;
		while (infoes.hasNext()) {
			info = infoes.next();
			if (info.getProvidedResources(containerName).size() > 0) {
				res.add(info.getApplicationName());
			}
		}
		allApps = new String[res.size()];
		res.toArray(allApps);
		return allApps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.LocalDeployment
	 * 		#listElements(java.lang.String, java.lang.String)
	 */
	public String[] listElements(final String containerName,
		final String applicationName) {
		ArrayList<String> allComponents = new ArrayList<String>();
		DeploymentInfo info = null;
		if (applicationName == null) {
			Iterator<DeploymentInfo> iterator = 
				Applications.getAll().iterator();
			while (iterator.hasNext()) {
				info = iterator.next();
				collectDeployedComponents(allComponents, containerName, info);
			}
		} else {
			info = Applications.get(applicationName);
			collectDeployedComponents(allComponents, containerName, info);
		}
		return allComponents.toArray(new String[allComponents.size()]);
	}

	private void shutdownCheck() throws DeploymentException {
		if (ctx.isMarkedForShutdown()) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.IN_SHUTDOWN);
			sde.setMessageID("ASJ.dpl_ds.005068");
			throw sde;
		}
	}

	private void makeGlobalTransaction(DTransaction tx)
		throws DeploymentException, ComponentNotDeployedException {
		tx.setEnqueueLockNeeded(true);
		final TransactionManager txManager = ctx.getTxManager();
		txManager.registerTransaction(tx);
		try {
			if (tx.isNeeded()) {
				tx.makeAllPhases();
			}
		} finally {
			txManager.unregisterTransaction(tx);
		}
	}

	private String[] checkSupport(String[] remoteSupport, List<String> warnings)
		throws RemoteException {
		String[] support = getSupports();
		if (remoteSupport == null || remoteSupport.length == 0) {
			return new String[] { "p4" };
		}
		boolean isOne = false;
		boolean isFound = false;
		boolean addMessage = false;
		String names = "";
		String some = "";
		for (int i = 0; i < remoteSupport.length; i++) {
			isFound = false;
			some += remoteSupport[i] + ", ";
			for (int j = 0; j < support.length; j++) {
				if (i == 0) {
					names += support[j] + ", ";
				}
				if (remoteSupport[i] != null
						&& remoteSupport[i].equals(support[j])) {
					isFound = true;
					if (!isOne) {
						isOne = true;
					}
					break;
				}
			}
			if (!isFound) {
				addMessage = true;
				warnings.add("The following remote support is not defined : "
					+ remoteSupport[i]);
			}
		}
		if (addMessage) {
			warnings.add("The only available remote support protocols are : "
				+ names + "please remove the ones that are wrong.");
		}
		if (!isOne) {
			warnings.add("ID5008 The given remote supports "
				+ some + "are not available."
				+ " The following are currently accessible : "
				+ names
				+ ".The application will be deployed with default one - p4 support.");
		}
		return remoteSupport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.DeployService#getServerLibraries()
	 */
	public Hashtable<String, String[]> getServerLibraries() {
		LibraryMonitor[] lms = PropManager.getInstance().getAppServiceCtx()
			.getContainerContext().getSystemMonitor().getLibraries();
		Hashtable<String, String[]> result = new Hashtable<String, String[]>();
		String libName = null;
		String[] libJars = null;
		for (int i = 0; i < lms.length; i++) {
			libName = lms[i].getComponentName();
			libJars = lms[i].getJars();
			result.put(libName, libJars);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.DeployService#getServerReferences()
	 */
	public String[] getServerReferences() {
		List<String> result = new ArrayList<String>();
		LibraryMonitor[] lms = PropManager.getInstance().getAppServiceCtx()
			.getContainerContext().getSystemMonitor().getLibraries();
		com.sap.engine.frame.container.monitor.Reference[] refs = null;
		int j = 0;
		String libName = null;
		String newRef = null;
		for (int i = 0; i < lms.length; i++) {
			libName = lms[i].getComponentName();
			refs = lms[i].getReferences();
			for (j = 0; j < refs.length; j++) {
				newRef = libName + ";"
					+ DUtils.parseReferencesInternal(refs[j].getName());
				if (!result.contains(newRef)) {
					result.add(newRef);
				}
			}
		}

		Iterator<String> apps = Applications.getNames().iterator();
		String appName = null;
		ReferenceObject[] appRefs = null;
		DeploymentInfo dInfo = null;
		while (apps.hasNext()) {
			appName = apps.next();
			dInfo = Applications.get(appName);
			appRefs = dInfo.getReferences();
			if (appRefs != null) {
				for (int i = 0; i < appRefs.length; i++) {
					newRef = appName + ";"
						+ DUtils.parseReferencesInternal(
							appRefs[i].toString());
					if (!result.contains(newRef)) {
						result.add(newRef);
					}
				}
			}
		}

		Iterator<String> allRefs = applicationReferences.keySet().iterator();
		appName = null;
		appRefs = null;
		while (allRefs.hasNext()) {
			appName = allRefs.next();
			appRefs = applicationReferences.get(appName);
			if (appRefs != null) {
				for (int i = 0; i < appRefs.length; i++) {
					newRef = appName + ";"
						+ DUtils.parseReferencesInternal(
							appRefs[i].toString());
					if (!result.contains(newRef)) {
						result.add(newRef);
					}
				}
			}
		}

		String[] r = new String[result.size()];
		result.toArray(r);
		return r;
	}

	/**
	 * Called by DeployCommunicatorImpl, which is in the same package. Returns
	 * deployment info for all deployed applications on server.
	 * 
	 * @return array of DeploymentInfo objects.
	 */
	final DeploymentInfo[] getAllDeploymentInfoes() {
		return Applications.getAll().toArray(
			new DeploymentInfo[Applications.size()]);
	}

	private Command[] registerCommands(DeployServiceContext ctx) {
		int count = 17;
		Command[] cmds = new Command[count];
		cmds[--count] = new ListApplicationsCommand(
			changeLog, ctx.getClusterMonitorHelper());
		cmds[--count] = new ListElementsCommand(changeLog);
		cmds[--count] = new StopApplicationCommand(changeLog);
		cmds[--count] = new StartApplicationCommand(changeLog);
		cmds[--count] = new GetStatusCommand(changeLog);
		cmds[--count] = new ChangeReference(changeLog);
		cmds[--count] = new UnlockApplicationCommand(changeLog);
		cmds[--count] = new ListRefsCommand(changeLog);
		cmds[--count] = new ContainerInfoCommand(changeLog);
		cmds[--count] = new UpdateFilesCommand(changeLog);
		cmds[--count] = new GetStartUpModeCommand(changeLog);
		cmds[--count] = new DeploymentInfoCommand(changeLog);
		cmds[--count] = new ListAppResources(changeLog);
		cmds[--count] = new AppReferencesGraph();
		cmds[--count] = new ReferenceGraphFindPath();
		cmds[--count] = new MigrationStatisticCommand(changeLog);
		cmds[--count] = new JavaVersionCommand(changeLog);
		return cmds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.LocalDeployment#getApplicationInfo
	 * (java.lang.String)
	 */
	public DeploymentInfo getApplicationInfo(String appName) {
		if (appName != null) {
			// In case application with name containing some of the characters
			// ; , = & % [ ] #
			// has previously been deployed on an older engine
			// this application should be displayed as it is,
			// though the specified characters are not allowed now.
			// The application status in Telnet is NOT DEPLOYED
			// and no operation can be performed on this application.
			// It is recommended that the application information is deleted
			// from
			// configuration.
			// In offline config tool, remove corresponding to the application
			// configurations:
			// apps/<provider_name>/<application_name>
			// deploy/<provider_name>/<application_name>
			return Applications.get(appName);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * refreshDeploymentInfoFromDB(java.lang.String,
	 * com.sap.engine.frame.core.configuration.Configuration,
	 * com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public DeploymentInfo refreshDeploymentInfoFromDB(final String appName,
		final Configuration appConfig, final ConfigurationHandler handler)
		throws ServerDeploymentException {

		Configuration customAppConfig = null;
		final String action = "reading the application " + appName
			+ " from DB.";

		final Configuration deployAppConfig = ConfigUtils.openConfiguration(
			handler, DeployConstants.ROOT_CFG_DEPLOY + "/" + appName,
			ConfigurationHandler.READ_ACCESS,
			"download application info from DB");
		final String ccAppsCfg = DeployConstants.CUSTOM_GLOBAL_CONFIG + "/"
			+ appName;
		try {
			customAppConfig = handler.openConfiguration(
				ccAppsCfg, ConfigurationHandler.READ_ACCESS);
		} catch (NameNotFoundException nnfe) {
			// $JL-EXC$ - might not exist
		} catch (ConfigurationException cEx) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION_ON_PRINCIPLE,
				new String[] { ccAppsCfg + " in " + action, "read" }, cEx);
			sde.setDcNameForObjectCaller(handler);
			sde.setMessageID("ASJ.dpl_ds.005062");
			throw sde;
		}
		return refreshDeploymentInfoFromDB(
			appName, appConfig, deployAppConfig, customAppConfig);
	}

	private DeploymentInfo refreshDeploymentInfoFromDB(final String appName,
		final Configuration appConfig, final Configuration deployAppConfig,
		final Configuration customAppConfig)
		throws ServerDeploymentException {

		ValidateUtils.nullValidator(appName, "application name");
		ValidateUtils.nullValidator(appConfig, "appsCfg");
		ValidateUtils.nullValidator(deployAppConfig, "deployCfg");

		final DIReader diReader = 
			EditorFactory.getInstance().getDIReader(appConfig);
		final DeploymentInfo dInfo = diReader.readDI(
			appName, appConfig,	deployAppConfig, customAppConfig);
		// Save the old application status
		final DeploymentInfo oldDInfo = Applications.get(appName);
		if (oldDInfo != null) {
			dInfo.setStatus(
				oldDInfo.getStatus(), oldDInfo.getStatusDescription());
			dInfo.setExceptionInfo(oldDInfo.getExceptionInfo());
		}
		// Store the new deployment info in the cache.
		addApplicationInfo(dInfo.getApplicationName(), dInfo);
		
		return dInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * getContainerWorkDir(java.lang.String, java.lang.String)
	 */
	public String getContainerWorkDir(String containerName,
		String applicationName) {
		final StringBuilder sb = new StringBuilder(
			PropManager.getInstance().getAppsWorkDir())
			.append(applicationName.replace('/', File.separatorChar))
			.append(File.separator).append(containerName);
		final String path = sb.toString();
		final File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * getAdditionalReferences(java.lang.String)
	 */
	public ReferenceObject[] getAdditionalReferences(String applicationName) {
		return applicationReferences.get(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * setLocalApplicationStatus(java.lang.String,
	 * com.sap.engine.services.deploy.container.op.util.Status,
	 * com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum,
	 * java.lang.Object[])
	 */
	public void setLocalApplicationStatus(String appName, Status status,
		StatusDescriptionsEnum statDescId, Object[] oParams) {
		DeploymentInfo info = Applications.get(appName);
		if (info != null) {
			DSLog.traceDebug(
				location, 
				"Set status [{0}] of application [{1}]", status, appName);
			info.setStatus(status, statDescId, oParams);
			String[] conts = info.getContainerNames();
			if (conts != null) {
				ContainerInterface cont = null;
				for (int i = 0; i < conts.length; i++) {
					cont = getContainer(conts[i]);
					if (cont != null) {
						cont.applicationStatusChanged(
							appName, status.getId().byteValue());
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.TransactionCommunicator#getStatus
	 * (java.lang.String)
	 */
	public byte getStatus(final String appName) throws DeploymentException {
		final DeploymentInfo info = 
			Applications.get(DUtils.getApplicationID(appName));
		if (info == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_DEPLOYED, 
				appName, "get its status");
			sde.setMessageID("ASJ.dpl_ds.005005");
			throw sde;
		}
		return info.getStatus().getId().byteValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * removeApplicationInfo(java.lang.String)
	 */
	public void removeApplicationInfo(String appName) {
		if (appName == null) {
			return;
		}
		final DeploymentInfo dInfo = getApplicationInfo(appName);
		if (dInfo != null) {
			Applications.remove(dInfo.getApplicationName());
		}
	}

	/**
	 * Called by ListRefsCommand and ITSAMApplicationManagedObject Returns
	 * application references for an application.
	 * 
	 * @param appName
	 *            application name.
	 * @return <code>Set<ReferenceObject><code>
	 * @throws RemoteException
	 *             if a problem occurs during the process.
	 */
	public Set<ReferenceObject> getApplicationReferences(final String appName)
		throws RemoteException {
		final DeploymentInfo info = 
			Applications.get(DUtils.getApplicationID(appName));
		final Set<ReferenceObject> res = new TreeSet<ReferenceObject>();
		ReferenceObject[] temp = null;
		if (info == null) {
			temp = applicationReferences.get(appName);
			if (temp != null) {
				for (int i = 0; i < temp.length; i++) {
					res.add(temp[i]);
				}
			}
		} else {
			final List<Component> comps = 
				PropManager.getInstance().getStandardAppRefs();
			ReferenceObject ref = null;
			for (Component comp : comps) {
				ref = new ReferenceObject(comp.toString(),
					REFERENCE_TYPE_NAME_WEAK);
				ref.setReferenceProviderName(
					DeployConstants.DEFAULT_PROVIDER_4_CORE_COMPS_SAP_COM);
				res.add(ref);
			}
			temp = info.getReferences();
			if (temp != null) {
				for (int i = 0; i < temp.length; i++) {
					res.add(temp[i]);
				}
			}
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * registerReferences(java.lang.String,
	 * com.sap.engine.services.deploy.ReferenceObject[])
	 */
	public void registerReferences(String appName, ReferenceObject[] refs) {
		if (appName != null && refs != null) {
			this.applicationReferences.put(appName, refs);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * unregisterReferences(java.lang.String)
	 */
	public void unregisterReferences(String appName) {
		if (appName != null) {
			this.applicationReferences.remove(appName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * addApplicationInfo(java.lang.String,
	 * com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo, boolean)
	 */
	public void addApplicationInfo(String appName, DeploymentInfo info) {
		if (appName == null || info == null) {
			return;
		}
		Applications.add(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.ClusterChangeListener
	 * 		#getDeployService()
	 */
	public DeployService getDeployService() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.DeployService
	 * 		#getContainerInfo(java.lang.String, java.lang.String)
	 */
	public ContainerInfo getContainerInfo(String containerName,
		String serverName) throws RemoteException {
		if (containerName == null) {
			throw new DSRemoteException("ASJ.dpl_ds.006170 " +
				"The given container name is NULL, which is wrong.");
		}
		return getContainerInfo(containerName);
	}

	/**
	 * Called by ContainerInfoCommand Obtains information about specified
	 * container from specified servers.
	 * 
	 * @param containerName
	 *            container name.
	 * @param serverNames
	 *            String array of server names.
	 * @return ContainerInfo object, which contains information about the
	 *         specified container.
	 * @throws RemoteException
	 *             if container name is null or the specified container is not
	 *             started on the specified servers.
	 */
	public ContainerInfo getContainerInfo(String containerName,
		String[] serverNames) throws RemoteException {
		if (containerName == null) {
			throw new DSRemoteException("ASJ.dpl_ds.006171 " +
				"The given container name is NULL, which is wrong.");
		}

		ContainerInfo info = getContainerInfo(containerName);
		if (info != null) {
			return info;
		}

		throw new DSRemoteException("ASJ.dpl_ds.006172 " +
			"The container " + containerName + " is not registered on "
			+ Arrays.toString(serverNames) + " servers.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.LocalDeployment
	 * 		#getContainerInfo(java.lang.String)
	 */
	public ContainerInfo getContainerInfo(String containerName) {
		ContainerInterface cont = getContainer(containerName);
		if (cont != null) {
			return cont.getContainerInfo();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.DeployService#registerDeployCallback(
	 * 		com.sap.engine.services.deploy.DeployCallback, java.lang.String[])
	 */
	public void registerDeployCallback(DeployCallback callback,
		String[] serverNames) throws RemoteException {
		try {
			final ClusterMonitorHelper cmHelper = ctx.getClusterMonitorHelper();
			final int[] serverIDs = cmHelper.getServerIDs(serverNames);
			final int localIndex = cmHelper.findIndexOfCurrentServerId(serverIDs);
			if (localIndex != -1) {
				ctx.getEventSystem().addDeployCallback(callback);
			}
			DeployService remoteDeploy = null;
			P4ObjectBroker broker = P4ObjectBroker.init();
			ClusterElement cEment = null;
			for (int i = 0; i < serverIDs.length; i++) {
				if (i != localIndex) {
					remoteDeploy = (DeployService) broker.narrow(
						broker.stringToObject(
							"clusteraloc:::" + serverIDs[i]	+ "::/deploy"),
						com.sap.engine.services.deploy.DeployService.class);
					if (remoteDeploy != null) {
						cEment = PropManager.getInstance().getAppServiceCtx()
							.getClusterContext().getClusterMonitor()
								.getParticipant(serverIDs[i]);
						if (cEment != null) {
							remoteDeploy.registerDeployCallback(callback,
								new String[] { cEment.getName() });
						} else {
							throw new DSRemoteException("ASJ.dpl_ds.006173 " +
								"Error while registering deploy callback " +
								"on cluster id " + serverIDs[i] +
								", because it doesn't participate in the cluster.");
						}
					} else {
						throw new DSRemoteException("ASJ.dpl_ds.006174 " +
							"P4Object broker return null while trying to " +
							"access the Deploy Service on cluster id "
							+ serverIDs[i]);
					}
				}
			}
		} catch (DeploymentException dex) {
			catchDeploymentExceptionWithDSRem(dex,
				"registering deploy callback " + callback);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#unregisterDeployCallback
	 * (com.sap.engine.services.deploy.DeployCallback, java.lang.String[])
	 */
	public void unregisterDeployCallback(DeployCallback callback,
		String[] serverNames) throws RemoteException {
		try {
			final ClusterMonitorHelper cmHelper = ctx.getClusterMonitorHelper();
			int[] serverIDs = cmHelper.getServerIDs(serverNames);
			int localIndex = cmHelper.findIndexOfCurrentServerId(serverIDs);
			if (localIndex != -1) {
				ctx.getEventSystem().removeDeployCallback(callback);
			}
			DeployService remoteDeploy = null;
			P4ObjectBroker broker = P4ObjectBroker.init();
			ClusterElement cEment = null;
			for (int i = 0; i < serverIDs.length; i++) {
				if (i != localIndex) {
					remoteDeploy = (DeployService) broker.narrow(
						broker.stringToObject(
							"clusteraloc:::" + serverIDs[i] + "::/deploy"),
						com.sap.engine.services.deploy.DeployService.class);
					cEment = PropManager.getInstance().getAppServiceCtx()
						.getClusterContext().getClusterMonitor()
							.getParticipant(serverIDs[i]);
					if (cEment != null) {
						remoteDeploy.unregisterDeployCallback(
							callback, new String[] { cEment.getName() });
					}
				}
			}
		} catch (DeploymentException dex) {
			catchDeploymentExceptionWithDSRem(dex,
				"unregistering deploy callback " + callback);
		}
	}

	/**
	 * Called by DeployCommunicatorImpl. Returns applications for a container.
	 * 
	 * @param contName
	 *            container name.
	 * @return String array of application names.
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	final String[] getApplicationsForContainer(String contName)
		throws DeploymentException {
		if (getContainer(contName) == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_AVAILABLE_CONTAINER, contName,
				"get applications that have components, deployed on it");
			sde.setMessageID("ASJ.dpl_ds.005006");
			throw sde;
		}
		String[] res = new String[Applications.size()];
		int c = 0;
		DeploymentInfo info = null;
		Iterator<DeploymentInfo> iterator = Applications.getAll().iterator();
		while (iterator.hasNext()) {
			info = iterator.next();
			if (info.getProvidedResources(contName).size() > 0) {
				res[c++] = info.getApplicationName();
			}
		}
		String result[] = new String[c];
		System.arraycopy(res, 0, result, 0, c);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.LocalDeployment#
	 * startApplicationLocalAndWait(java.lang.String)
	 */
	public void startApplicationLocalAndWait(final String appName,
		final Component cause)
		throws DeploymentException {
		final String tagName = "Start Application Local And Wait";
		try {
			Accounting.beginMeasure(tagName, this.getClass());
			shutdownCheck();
			StartTransaction trans = 
				new StartTransaction(appName, ctx, false, cause);
			trans.setEnqueueLockNeeded(true);
			trans.setLockType(LockingConstants.MODE_SHARED);
			ctx.getTxManager().registerTransaction(trans);
			try {
				if (trans.isNeeded()) {
					trans.makeAllPhasesLocal();
				}
			} finally {
				ctx.getTxManager().unregisterTransaction(trans);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.LocalDeployment#
	 * stopApplicationLocalAndWait(java.lang.String, boolean)
	 */
	public void stopApplicationLocalAndWait(final String appName,
		final Component cause) throws DeploymentException {
		StopTransaction trans = new StopTransaction(appName, cause, ctx);
		trans.setEnqueueLockNeeded(true);
		trans.setLockType(LockingConstants.MODE_SHARED);
		final TransactionManager txManager = ctx.getTxManager();
		txManager.registerTransaction(trans);
		try {
			if (trans.isNeeded()) {
				trans.makeAllPhasesLocal();
			}
		} finally {
			txManager.unregisterTransaction(trans);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#startApplicationAndWait(
	 * java.lang.String)
	 */
	public void startApplicationAndWait(String appName) throws RemoteException {
		startApplicationOnInstanceAndWaitAuth(
			appName, ctx.getClusterMonitorHelper().findServers());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#startApplicationAndWait(
	 * java.lang.String, java.lang.String[])
	 */
	public void startApplicationAndWait(String appName, String[] serverNames)
		throws RemoteException {
		startApplicationOnInstanceAndWaitAuth(appName,
			ctx.getClusterMonitorHelper().expandToWholeInstances(serverNames));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#stopApplicationAndWait(java
	 * .lang.String)
	 */
	public void stopApplicationAndWait(String appName) throws RemoteException {
		stopApplicationOnInstanceAndWaitAuth(appName,
			ctx.getClusterMonitorHelper().findServers());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#stopApplicationAndWait(java
	 * .lang.String, java.lang.String[])
	 */
	public void stopApplicationAndWait(String appName, String[] serverNames)
		throws RemoteException {
		stopApplicationOnInstanceAndWaitAuth(appName,
			ctx.getClusterMonitorHelper().expandToWholeInstances(serverNames));
	}

	/**
	 * Called by DeployCommunicatorImpl to start an application. Do not check
	 * for authorization here, because this method can be called in any thread.
	 * 
	 * @param appName
	 *            application name.
	 * @param lock
	 *            show the way of application starting.
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 * @throws WarningException
	 *             com.sap.engine.services.deploy.container.WarningException If
	 *             successful stop of the application causes any possible
	 *             problems, with other applications, for example. This
	 *             exception extends RemoteException.
	 */
	public void startApplicationAndWait(String appName, String[] serverNames,
		boolean lock) throws RemoteException, WarningException {
		startApplicationOnInstanceWait(appName, lock,
			ctx.getClusterMonitorHelper().expandToWholeInstances(serverNames));
	}

	private String[][] startApplicationAndWait(String appName, int[] serverIDs,
		boolean lock) throws DeploymentException {
		assert serverIDs != null;
		shutdownCheck();
		StartTransaction trans = new StartTransaction(appName, ctx, serverIDs);
		trans.setEnqueueLockNeeded(lock);
		final TransactionManager txManager = ctx.getTxManager();
		txManager.registerTransaction(trans);
		try {
			if (trans.isNeeded()) {
				trans.makeAllPhases();
			}
		} finally {
			txManager.unregisterTransaction(trans);
		}
		return DUtils.getWarningsAndErrors(trans);
		// warnings and errors
	}

	/**
	 * Called by DeployCommunicatorImpl to stop an application.
	 * 
	 * @param appName
	 *            application name.
	 * @param lock
	 *            shows the way of application stopping.
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 * @throws WarningException
	 *             com.sap.engine.services.deploy.container.WarningException If
	 *             successful stop of the application causes any possible
	 *             problems, with other applications, for example. This
	 *             exception extends RemoteException.
	 */
	public void stopApplicationAndWait(String appName, String[] serverNames,
		boolean lock) throws RemoteException, WarningException {
		stopApplicationOnInstanceWait(appName,
			ctx.getClusterMonitorHelper().expandToWholeInstances(serverNames));
	}

	private void stopApplicationAndWait(String appName, int[] serverIDs,
		boolean lock) throws DeploymentException, WarningException {
		shutdownCheck();
		StopTransaction trans = 
			new StopTransaction(appName, ctx, serverIDs);
		trans.setEnqueueLockNeeded(lock);
		final TransactionManager txManager = ctx.getTxManager();
		txManager.registerTransaction(trans);
		try {
			if (trans.isNeeded()) {
				trans.makeAllPhases();
			}
		} finally {
			txManager.unregisterTransaction(trans);
		}
		DUtils.processWarningsAndErrors(trans);
	}

	/**
	 * Called by StartApplicationCommand and DeployCommunicatorImpl to start an
	 * application locally.
	 * 
	 * @param appName
	 *            application name.
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	public void startApplicationLocal(String appName)
		throws DeploymentException {
		shutdownCheck();
		StartTransaction trans = new StartTransaction(
			appName, ctx, false, null);
		trans.setEnqueueLockNeeded(true);
		trans.setLockType(LockingConstants.MODE_SHARED);
		final TransactionManager txManager = ctx.getTxManager();
		txManager.registerTransaction(trans);
		try {
			if (trans.isNeeded()) {
				trans.makeAllPhasesLocal();
			}
		} finally {
			txManager.unregisterTransaction(trans);
		}
	}

	/**
	 * Called by StopApplicationCommand and DeployCommunicatorImpl to stop an
	 * application locally.
	 * 
	 * @param appName
	 *            application name.
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	public void stopApplicationLocal(String appName) 
		throws DeploymentException {
		shutdownCheck();
		StopTransaction tx = new StopTransaction(appName, null, ctx);
		tx.setEnqueueLockNeeded(true);
		tx.setLockType(LockingConstants.MODE_SHARED);
		final TransactionManager txManager = ctx.getTxManager();
		txManager.registerTransaction(tx);
		try {
			if (tx.isNeeded()) {
				tx.makeAllPhasesLocal();
			}
		} finally {
			txManager.unregisterTransaction(tx);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator
	 * #getDeployProperty(java.lang.String)
	 */
	public String getDeployProperty(String key) {
		final Properties serviceProps = PropManager.getInstance()
			.getServiceProperties();
		if (serviceProps != null && key != null) {
			return serviceProps.getProperty(key);
		}
		return null;
	}

	/**
	 * Called only by DeployRuntimeControlImpl Returns additional application
	 * info for an application.
	 * 
	 * @param appName
	 *            application name.
	 * @return additional application info.
	 * @throws RemoteException
	 *             if a problem occurs during the process.
	 */
	public AdditionalAppInfo getAdditionalAppInfo(String appName)
		throws RemoteException {
		AdditionalAppInfo info = Applications.get(appName);
		if (info == null) {
			throw new DSRemoteException("ASJ.dpl_ds.006175 Application "
				+ appName + " is not deployed on server "
				+ PropManager.getInstance().getClElemName());
		}
		AdditionalAppInfo addAppInfo = new AdditionalAppInfo();
		addAppInfo.setFailOver(info.getFailOver());
		return addAppInfo;
	}

	/**
	 * Called by ITSAMApplicationManagedObject, DeployCommunicatorImpl,
	 * DeployRuntimeControlImpl and setJavaVersion() method. Sets additional
	 * application info for an application.
	 * 
	 * @param appName
	 *            application name.
	 * @param info
	 *            additional application info.
	 * @throws RemoteException
	 *             if a problem occurs during the process.
	 */
	public void setAdditionalAppInfo(String appName, AdditionalAppInfo info)
		throws RemoteException {
		try {
			shutdownCheck();
			AddAppInfoChangeTransaction changeTransaction;
			changeTransaction = new AddAppInfoChangeTransaction(
				appName, ctx, info);
			makeGlobalTransaction(changeTransaction);
		} catch (DeploymentException dex) {
			catchDeploymentExceptionWithDSRem(dex,
				"setting additional application info of application " + 
				appName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#makeReferences(java.lang
	 * .String, com.sap.engine.services.deploy.ReferenceObject[])
	 */
	public void makeReferences(String fromApplication,
		ReferenceObject[] references) throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.makeRefs);
		compNameVerifier(fromApplication, "make reference(s) from",
			RESOURCE_TYPE_APPLICATION);

		if (references == null || references.length == 0) {
			return;
		}
		try {
			shutdownCheck();
			if (fromApplication.startsWith("service:")) {
				throw new DSRemoteException("ASJ.dpl_ds.006176 " +
					"It is not allowed to change references of services " +
					"after the service is deployed.");
			}
			if (fromApplication.startsWith("library:")) {
				throw new DSRemoteException("ASJ.dpl_ds.006177 " +
					"It is not allowed to change references of libraries " +
					"after the library is deployed.");
			}
			if (fromApplication.startsWith("interface:")) {
				throw new DSRemoteException("ASJ.dpl_ds.006178 " +
					"It is not allowed to change references of interfaces " +
					"after the interface is deployed.");
			}
			fromApplication = DUtils.getApplicationID(fromApplication);
			DeploymentInfo info = Applications.get(fromApplication);
			if (info == null) {
				ReferenceObject[] previousRefs = 
					applicationReferences.get(fromApplication);
				previousRefs = DUtils.concatReferences(
					previousRefs, references);
				applicationReferences.put(fromApplication, previousRefs);
				final ConfigurationHandler handler = ConfigUtils
					.getConfigurationHandler(PropManager.getInstance()
						.getConfigurationHandlerFactory(),
							"making application references");
				if (handler == null) {
					ServerDeploymentException sde = 
						new ServerDeploymentException(
							ExceptionConstants.NOT_AVAILABLE_CONFIG_MANAGER_ON_PRINCIPLE,
							"saving application references");
					sde.setMessageID("ASJ.dpl_ds.005057");
					throw sde;
				} 
					try {
						final Configuration deployCfg = ConfigUtils
							.getSubConfiguration(handler,
								DeployConstants.ROOT_CFG_DEPLOY,
								fromApplication.substring(0,
									fromApplication.indexOf("/")),
								fromApplication.substring(
									fromApplication.indexOf("/") + 1));
						final DIWriter diWriter = 
							EditorFactory.getInstance().getDIWriter(deployCfg);
						diWriter.modifyReferences(deployCfg, previousRefs);
						ConfigUtils.commitHandler(handler,
							"making application references");
					} catch (ConfigurationException ce) {
						DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006361",
							"Error in getting subconfiguration", ce);
						ServerDeploymentException sde = 
							new ServerDeploymentException(
								ExceptionConstants.CANNOT_STORE_REFERENCES_IN_DB,
								new String[] { "making references from not deployed application" },
								ce);
						sde.setMessageID("ASJ.dpl_ds.005094");
						sde.setDcNameForObjectCaller(handler);
						throw sde;
					} finally {
						ConfigUtils.rollbackHandler(handler,
								"making application references");
					}
				final RemoteCommand cmd = createMakeRefsTransactionCmd(
					fromApplication, previousRefs);
				final RemoteCaller remote = ctx.getRemoteCaller();
				remote.sendAndWait(cmd, 
					ctx.getClusterMonitorHelper().findServers());
			} else {
				MakeReferencesTransaction tx = new MakeReferencesTransaction(
					fromApplication, references, ctx);
				tx.setEnqueueLockNeeded(true);
				final TransactionManager txManager = ctx.getTxManager();
				txManager.registerTransaction(tx);
				try {
					tx.makeAllPhases();
				} finally {
					txManager.unregisterTransaction(tx);
				}
			}
		} catch (DeploymentException dex) {
			catchDeploymentExceptionWithDSRem(dex,
				"making references from application " + fromApplication);
		}
	}

	private RemoteCommand createMakeRefsTransactionCmd(
		final String fromApplication, final ReferenceObject[] previousRefs) {
		final Map<String, Object> cmdMap = new HashMap<String, Object>();
		cmdMap.put("command", DeployConstants.makeRefs);
		cmdMap.put("application_name", fromApplication);
		cmdMap.put("ref_objects", previousRefs);
		return RemoteCommandFactory.createMakeTransactionCmd(
			cmdMap, PropManager.getInstance().getClElemID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#removeReferences(java.lang
	 * .String, com.sap.engine.services.deploy.ReferenceObject[])
	 */
	public void removeReferences(String fromApplication,
		ReferenceObject[] references) throws RemoteException {
		getAuthorizationChecker()
			.checkAuthorization(DeployConstants.removeRefs);
		compNameVerifier(fromApplication, "remove reference(s) from",
			RESOURCE_TYPE_APPLICATION);

		String[] toLibraries = new String[references.length];
		for (int i = 0; i < references.length; i++) {
			toLibraries[i] = references[i].toString();
		}
		this.removeReferences(fromApplication, toLibraries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#makeReferences(java.lang
	 * .String, java.lang.String,
	 * com.sap.engine.services.deploy.ReferenceObject[])
	 */
	public void makeReferences(String providerName, String fromApplication,
		ReferenceObject[] references) throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.makeRefs);
		compNameVerifier(fromApplication, "make reference(s) from",
			RESOURCE_TYPE_APPLICATION);

		makeReferences(
			getInternalAppID(providerName, fromApplication), references);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#removeReferences(java.lang
	 * .String, java.lang.String,
	 * com.sap.engine.services.deploy.ReferenceObject[])
	 */
	public void removeReferences(String providerName, String fromApplication,
		ReferenceObject[] references) throws RemoteException {
		getAuthorizationChecker()
			.checkAuthorization(DeployConstants.removeRefs);
		compNameVerifier(fromApplication, "remove reference(s) from",
			RESOURCE_TYPE_APPLICATION);

		removeReferences(
			getInternalAppID(providerName, fromApplication), references);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getApplicationStatus(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	public String getApplicationStatus(String providerName,
		String applicationName, String serverName) throws RemoteException {
		compNameVerifier(applicationName, "get-application-status of",
			RESOURCE_TYPE_APPLICATION);
		return getApplicationStatus(
			getInternalAppID(providerName, applicationName), serverName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getClientJar(java.lang.String
	 * , java.lang.String)
	 */
	public SerializableFile getClientJar(String providerName,
		String applicationName) throws java.rmi.RemoteException {
		getAuthorizationChecker().checkAuthorization(
			DeployConstants.getClientJar);
		compNameVerifier(applicationName, "get-client-jar of",
			RESOURCE_TYPE_APPLICATION);

		return getClientJar(getInternalAppID(providerName, applicationName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#listElements(java.lang.String
	 * , java.lang.String, java.lang.String, java.lang.String[])
	 */
	public String[] listElements(String containerName, String providerName,
		String applicationName, String[] serverNames) throws RemoteException {
		return listElements(containerName, 
			getInternalAppID(providerName, applicationName), serverNames);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#remove(java.lang.String,
	 * java.lang.String)
	 */
	public void remove(String providerName, String applicationName)
		throws RemoteException {
		final String tagName = "Remove";
		try {
			Accounting.beginMeasure(tagName, this.getClass());
			getAuthorizationChecker().checkAuthorization(
				DeployConstants.removeApp);
			compNameVerifier(applicationName, "remove",
				RESOURCE_TYPE_APPLICATION);

			remove(getInternalAppID(providerName, applicationName));
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#singleFileUpdate(com.sap
	 * .engine.services.deploy.container.FileUpdateInfo[], java.lang.String,
	 * java.lang.String, java.util.Properties)
	 */
	public void singleFileUpdate(FileUpdateInfo[] files, String providerName,
		String appName, Properties props) throws RemoteException {
		final String tagName = "Single File Update";
		try {
			Accounting.beginMeasure(tagName, this.getClass());
			getAuthorizationChecker().checkAuthorization(
				DeployConstants.singleFileUpdate);
			compNameVerifier(appName, "single-file-update",
				RESOURCE_TYPE_APPLICATION);

			singleFileUpdate(files, getInternalAppID(providerName, appName),
				props);
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/**
	 * Tries to start an application on all server nodes that are part of the
	 * current instance.
	 * 
	 * If the start fails on any of the server nodes stop operation will be
	 * performed in order to try to bring the server instance in consistent
	 * state.
	 * 
	 * Note: Everyone that calls it must take care of the authorization
	 * 
	 * @param appName
	 *            name of the application that will be started
	 * @param needLock
	 *            true if enqueue lock is needed
	 * 
	 * @throws RemoteException
	 *             thrown if a remote problem during getting process occurs
	 */
	private void startApplicationOnInstanceWait(String appName,
		boolean needLock, int[] serverIds) 
		throws RemoteException, WarningException {
		final String tagName = "Start Application On Instance";
		try {
			Accounting.beginMeasure(tagName, this.getClass());
			String[][] warningsAndErrors = null;
			String[] errors = null;
			String[] warnings = null;
			try {

				DSLog.tracePath(
					location, 
					"Starting application [{0}] and wait.", appName);
				// if initial start of the applications during start of the
				// server is not done only local start operation
				if (isInitialStartDone) {
					warningsAndErrors = startApplicationAndWait(
						appName, serverIds, needLock);
					warnings = warningsAndErrors[0];
					errors = warningsAndErrors[1];
					if (location.beDebug()) {
						DSLog.traceDebug(
							location, 
							"Application [{0}] started without exception, " +
							"errors from other nodes: {1}, warnings: {2}",
							appName, CAConvertor.toString(errors, ""), 
							CAConvertor.toString(warnings, ""));
					}
				} else {
					startApplicationLocalAndWait(appName, null);
				}
			} catch (DeploymentException dex) {
				if (location.beDebug()) {
					DSLog.traceDebug(
						location, 
						"Application [{0}] failed to start, exception: {1}",
						appName, dex.getMessage());
				}
				final StringBuffer exceptionMessage = 
					stopOnInstanceAfterFailedStartAppendMessage(
						appName, serverIds);
				throw new DSRemoteException(exceptionMessage.toString(), dex);
			}

			if (errors != null && errors.length > 0) {
				final StringBuffer exceptionMessage = 
					stopOnInstanceAfterFailedStartAppendMessage(
						appName, serverIds);
				for (int i = 0; i < errors.length; i++) {
					exceptionMessage.append(errors[i].toString());
				}
				throw new DSRemoteException(exceptionMessage.toString());
			}

			if (!PropManager.getInstance().isStrictJ2eeChecks()) {
				return;
			}
			if ((warnings != null && warnings.length != 0)) {
				WarningException wex = new WarningException();
				wex.setWarning(warnings);
				throw wex;
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/*
	 * Private method that stops application on all server nodes if a start
	 * operation failed on any node.
	 */
	@SuppressWarnings("boxing")
	private StringBuffer stopOnInstanceAfterFailedStartAppendMessage(
		String appName, int[] serverIds) {
		final StringBuffer message = new StringBuffer();
		try {
			message.append(
				"ASJ.dpl_ds.006153 Error occurred while starting application ["
				+ appName + "]: It is not started "
				+ "successfully on server nodes "
				+ CAConvertor.toString(serverIds, "")
				+ ". In order to bring the server in consistent state "
				+ "stop operation will be performed."
				+ CAConstants.EOL);
			String localStatus = getApplicationStatus(appName);
			int currentId = PropManager.getInstance().getClElemID();		
			for (int id : serverIds) {
				if (id != currentId) {
					final String remoteStatus = ctx.getRemoteCaller()
						.getApplicationStatusRemotely(appName, id);
					if (remoteStatus != null) {
						if (location.beInfo()) {
							DSLog.traceInfo(location, "ASJ.dpl_ds.000571",
								"Received from server [{0}], status [{1}] of application [{2}].",
								id, remoteStatus, appName);
						}
						if (!remoteStatus.equals(localStatus)) {
							DSLog.traceDebug(
								location, 
								"Application [{0}} has different statuses"
								+ " [{1}] and [{2}] on two of the server nodes and will be stopped.",
								appName, localStatus, remoteStatus);
							// if at least one application has different status
							// we must stop all of them
							stopApplicationOnInstanceWait(appName, serverIds);
							return message;
						}
					} else {
						message.append("Errors occured during the stop of application ["
							+ appName
							+ "]: No result received from server "
							+ id
							+ " about status of application ["
							+ appName
							+ "] Note that the server might be in inconsistent state");
						return message;
					}
				}
			}
		} catch (RemoteException de) {
			// Stopping application because of failed start - exception from
			// stop must not be rethrown
			// Exception from the stop is logged in stopApplicationAndWait
			// method
			message.append("Errors occured during the stop of application ["
				+ appName + "]: " + de.getMessage()
				+ " Note that the server might be in inconsistent state.");
		} catch (DeploymentException dex) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006363",
				"Exception on getting application status on stop", dex);							// logged
			message.append("Errors occured during the stop of application ["
				+ appName + "], while trying to get its status: "
				+ dex.getMessage());
		}
		return message;
	}

	/*
	 * Stops application on all server nodes from the current instance.
	 * 
	 * This method is private, because only Deploy Service should decide who can
	 * start an application without an authorization check.
	 */
	private void stopApplicationOnInstanceWait(String appName, int[] serverIds)
		throws RemoteException {
		final String tagName = "Stop Application On Instance And Wait";
		try {
			Accounting.beginMeasure(tagName, this.getClass());
			if (isInitialStartDone) {
				stopApplicationAndWait(appName, serverIds, true);
			} else {
				stopApplicationLocalAndWait(appName, null);
			}
		} catch (DeploymentException dex) {
			catchDeploymentExceptionWithDSRem(dex, "stopping application "
				+ appName + " and wait");
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	/*
	 * Starts application on all server nodes from the current instance. All
	 * methods from DeployService interface must check for authorization.
	 */
	public void startApplicationOnInstanceAndWaitAuth(String appName,
		int[] serverIds) throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.startApp);
		compNameVerifier(appName, "start", RESOURCE_TYPE_APPLICATION);

		startApplicationOnInstanceWait(appName, true, serverIds);
	}

	/*
	 * Stops application on all server nodes from the current instance. All
	 * methods from DeployService interface must check for authorization.
	 */
	public void stopApplicationOnInstanceAndWaitAuth(String appName,
		int[] serverIds) throws RemoteException {
		getAuthorizationChecker().checkAuthorization(DeployConstants.stopApp);
		compNameVerifier(appName, "stop", RESOURCE_TYPE_APPLICATION);

		stopApplicationOnInstanceWait(appName, serverIds);
	}

	/*
	 * Starts or stops application on all server nodes from the current instance
	 * in a new thread. If the operation fails the error message is logged. If
	 * the start fails on any of the nodes, the application will be stopped on
	 * the whole instance.
	 */
	private void startOrStopApplicationOnInstanceAuth(final String appName,
		final boolean start, final String[] serverNames)
		throws RemoteException {

		getAuthorizationChecker().checkAuthorization(
			start ? DeployConstants.startApp : DeployConstants.stopApp);
		compNameVerifier(appName, start ? "start" : "stop",
			RESOURCE_TYPE_APPLICATION);

		ThreadSystem threadS = PropManager.getInstance().getThreadSystem();
		Runnable run = new Runnable() {
			public void run() {
				try {
					if (start) {
						startApplicationOnInstanceWait(appName, true,
							ctx.getClusterMonitorHelper()
								.expandToWholeInstances(serverNames));
					} else {
						stopApplicationOnInstanceWait(appName,
							ctx.getClusterMonitorHelper()
								.expandToWholeInstances(serverNames));
					}
				} catch (RemoteException re) {
					DSLog.logErrorThrowable(
						location, 
						"ASJ.dpl_ds.006362",
						"Exception in application start or stop on remote node",
						re);
				}
			}
		};

		threadS.startThread(run, null,
			start ? DeployConstants.DEPLOY_START_APP_THREAD_NAME
				: DeployConstants.DEPLOY_STOP_APP_THREAD_NAME, 
			false, true);
	}

	private String getInternalAppID(final String providerName,
		final String applicationName) {
		final StringBuilder sb = new StringBuilder();
		sb.append(providerName == null ? "sap.com" : providerName).append("/")
			.append(DUtils.replaceForbiddenSymbols(applicationName));
		return sb.toString();
	}

	private void collectDeployedComponents(List<String> toList,
		String containerName, DeploymentInfo info) {
		ContainerInfo contInfo = null;
		if (info == null) {
			return;
		}
		if (containerName == null) {
			String[] containerNames = info.getContainerNames();
			if (containerNames != null) {
				for (int i = 0; i < containerNames.length; i++) {
					contInfo = getContainerInfo(containerNames[i]);
					collectResourcesNames(toList, 
						info.getProvidedResources(containerNames[i]), 
						contInfo);
				}
			}

		} else {
			contInfo = getContainerInfo(containerName);
			collectResourcesNames(
				toList, info.getProvidedResources(containerName), contInfo);
		}
	}

	private void collectResourcesNames(List<String> toList,
		Set<com.sap.engine.services.deploy.server.dpl_info.module.Resource> resources,
		ContainerInfo contInfo) {
		if (resources == null)
			return;
		for(com.sap.engine.services.deploy.server.dpl_info.module.Resource rs : resources) {
			if (contInfo != null) {
				toList.add(rs.getName()
					+ "- " + (contInfo.isJ2EEContainer() ? 
						contInfo.getJ2EEModuleName() : 
						contInfo.getModuleName()));
			} else {
				toList.add(rs.getName() + "- "
					+ deployed_in_unavailable_container + " on "
					+ PropManager.getInstance().getClElemName());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * 		removeReferencesInternally(java.lang.String, java.lang.String[])
	 */
	public void removeReferencesInternally(String fromApplicationName,
		String[] refs) {
		DeploymentInfo info = getApplicationInfo(fromApplicationName);
		if (refs == null) {
			return;
		}
		if (info != null) {
			info.removeReferences(refs);
			Applications.add(info);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.DeployService#getApplicationInformation
	 * (java.lang.String)
	 */
	public ApplicationInformation getApplicationInformation(
		String applicationName) throws RemoteException {
		compNameVerifier(applicationName, "get-application-information of",
			RESOURCE_TYPE_APPLICATION);
		DeploymentInfo info = getApplicationInfo(applicationName);
		if (info == null) {
			throw new DSRemoteException(
				"ASJ.dpl_ds.006179 Application with name "
				+ applicationName + " is not deployed in the cluster.");
		}
		return (new ApplicationInformation(info));
	}

	/**
	 * Called only by DeployCommunicatorImpl. Returns all the applications which
	 * have reference to the specified component.
	 * 
	 * @param component
	 *            the referenced component.
	 * @return a String array of application names.
	 */
	final String[] getAppsReferencedComponent(String component) {
		List<String> list = new ArrayList<String>();
		Iterator<DeploymentInfo> apps = Applications.getAll().iterator();
		DeploymentInfo info = null;
		ReferenceObject[] refs = null;
		while (apps.hasNext()) {
			info = apps.next();
			refs = info.getReferences();
			if (refs != null) {
				for (int i = 0; i < refs.length; i++) {
					if (refs[i].toString().equals(component)) {
						list.add(info.getApplicationName());
						break;
					}
				}
			}
		}
		String[] res = new String[list.size()];
		list.toArray(res);
		return res;
	}

	/**
	 * Called only by DeployCommunicatorImpl. Used for starting a process of
	 * modification over an application.
	 * 
	 * @param app
	 *            the name of the application.
	 * @throws LockException
	 *             if the application is already locked by another operation.
	 */
	final void startAppModification(String app) throws LockException {
		if (app != null) {
			try {
				LockUtils.lockAndWait(app,
					LockingConstants.MODE_EXCLUSIVE_CUMULATIVE, 120000);
			} catch (TechnicalLockException tlex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "making modification of application "
						+ app }, tlex);
				sde.setMessageID("ASJ.dpl_ds.005082");
				DSLog.logErrorThrowable(location, sde);
			} catch (IllegalArgumentException iaex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "making modification of application "
						+ app }, iaex);
				sde.setMessageID("ASJ.dpl_ds.005082");
				DSLog.logErrorThrowable(location, sde);
			}
		}
	}

	/**
	 * Called only by DeployCommunicatorImpl. Used when the process of
	 * modification over an application has finished.
	 * 
	 * @param app
	 *            the name of the application.
	 */
	final void appModificationDone(String app) {
		if (app != null) {
			try {
				LockUtils.unlock(app,
					LockingConstants.MODE_EXCLUSIVE_CUMULATIVE);
			} catch (TechnicalLockException tlex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "finishing modification of application ["
						+ app + "]" }, tlex);
				sde.setMessageID("ASJ.dpl_ds.005082");
				DSLog.logErrorThrowable(location, sde);
			} catch (IllegalArgumentException iaex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "finishing modification of application ["
						+ app + "]" }, iaex);
				sde.setMessageID("ASJ.dpl_ds.005082");
				DSLog.logErrorThrowable(location, sde);
			}
		}
	}

	private void compNameVerifier(String compName, String operation,
		String componentType) throws RemoteException {
		if (compName == null) {
			String temp = ((componentType != null && (componentType
				.startsWith("a") || componentType.startsWith("i"))) ? " an "
				: " a ");
			throw new DSRemoteException("ASJ.dpl_ds.006184 You are trying to "
				+ operation + temp + componentType + " with name "
				+ compName + ", which is not legal.");
		}
	}

	private void nullVerifier(Object obj, String action) throws RemoteException {
		if (obj == null) {
			throw new DSRemoteException("ASJ.dpl_ds.006185 You are trying to "
					+ action + " null, which is not legal.");
		}
	}

	/**
	 * Called only by ListApplicationsCommand.
	 * 
	 * @param containerName
	 * @param clusterIDs Not null but can be empty.
	 * @param onlyJ2ee
	 * @param withStatusDescription
	 * @return
	 * @throws RemoteException
	 */
	public MessageResponse[] listApplicationAndStatusesInCluster(
		String containerName, int[] clusterIDs, boolean onlyJ2ee,
		boolean withStatusDescription) throws RemoteException {

		final int index = ctx.getClusterMonitorHelper()
			.findIndexOfCurrentServerId(clusterIDs);
		// process current node
		MessageResponse current = null;
		if (index != -1) {
			Map<String, Object> res = new HashMap<String, Object>();
			String[] apps = null;
			if (onlyJ2ee) {
				apps = listJ2EEApplications(containerName);
			} else {
				apps = listApplications(containerName);
			}
			if (apps != null) {
				String status = null;
				StatusDescription statusDescription = null;
				for (int i = 0; i < apps.length; i++) {
					try {
						status = getApplicationStatus(apps[i]);
					} catch (RemoteException rex) {
						status = "NOT DEPLOYED";
					}
					if (withStatusDescription) {
						try {
							statusDescription = 
								getApplicationStatusDescription(apps[i]);
						} catch (RemoteException rex) {
							statusDescription = null;
						}
						res.put(apps[i], new Object[] { status,
								statusDescription });
					} else {
						res.put(apps[i], status);
					}
				}
			}
			current = new MessageResponse(
				PropManager.getInstance().getClElemID(), null, null, res);
		}
		// process the rest
		MessageResponse[] resStatistics = null;
		try {
			int restClusterIDs[] = DUtils.removeElement(clusterIDs, 
				ctx.getClusterMonitorHelper().getCurrentServerId());
			if (restClusterIDs != null && restClusterIDs.length != 0) {
				MessageResponse[] statistics = 
					ctx.getRemoteCaller()
						.listAppsAndStatusesRemotely(containerName,
							restClusterIDs, onlyJ2ee, withStatusDescription);
				if (statistics != null) {
					if (current != null) {
						resStatistics = new MessageResponse[statistics.length + 1];
						System.arraycopy(statistics, 0, resStatistics, 0,
							statistics.length);
						resStatistics[statistics.length] = current;
					} else {
						resStatistics = statistics;
					}
				} else if (current != null) {
					resStatistics = new MessageResponse[] { current };
				}
			} else if (current != null) {
				resStatistics = new MessageResponse[] { current };
			}
		} catch (DeploymentException dex) {
			throw new DSRemoteException("ASJ.dpl_ds.006186 " +
				"Error occurred while listing all applications" +
				"and their statuses in cluster.", dex);
		}
		return resStatistics;
	}

	private String[] evaluateReferences(String fromApp,
		ReferenceObject existing[], String forRemove[]) {
		String res[] = null;
		List<ReferenceObject> list = new ArrayList<ReferenceObject>();
		boolean isFound[] = new boolean[forRemove.length];
		for (int i = 0; i < isFound.length; i++) {
			isFound[i] = false;
		}
		boolean found = false;
		for (int i = 0; i < existing.length; i++) {
			found = false;
			for (int j = 0; j < forRemove.length; j++) {
				if (existing[i].toString().equals(forRemove[j])) {
					isFound[j] = true;
					found = true;
					break;
				}
			}
			if (!found) {
				list.add(existing[i]);
			}
		}
		List<String> warnings = new ArrayList<String>();
		for (int i = 0; i < isFound.length; i++) {
			if (!isFound[i]) {
				warnings.add("Reference from " + fromApp + " to "
					+ forRemove[i] + " doesn't exist.");
			}
		}
		existing = new ReferenceObject[list.size()];
		list.toArray(existing);

		if (existing.length == 0) {
			applicationReferences.remove(fromApp);
		} else {
			applicationReferences.put(fromApp, existing);
		}

		res = new String[warnings.size()];
		warnings.toArray(res);
		return res;
	}

	/**
	 * Called only by DeployRuntimeControlImpl.
	 * 
	 * @param applicationName
	 * @param container
	 * @param module
	 * @return
	 * @throws RemoteException
	 */
	final String[] getModuleEntries(String applicationName, String container,
		String module) throws RemoteException {
		getAuthorizationChecker().checkAuthorization("get module entries");
		compNameVerifier(applicationName, "get module entries of",
			RESOURCE_TYPE_APPLICATION);

		if (container == null || container.trim().equals("")) {
			throw new DSRemoteException(
				"ASJ.dpl_ds.006187 Please specify correct container name.");
		}
		if (module == null || module.trim().equals("")) {
			throw new DSRemoteException(
				"ASJ.dpl_ds.006188 Please specify correct module name.");
		}
		applicationName = DUtils.getApplicationID(applicationName);

		ConfigurationHandler handler = null;
		try {
			ConfigurationHandlerFactory factory = PropManager.getInstance()
				.getConfigurationHandlerFactory();
			if (factory != null) {
				try {
					handler = factory.getConfigurationHandler();
				} catch (ConfigurationException ce) {
					throw new DSRemoteException("ASJ.dpl_ds.006189 " +
						"Cannot get configuration handler " +
						"while getting module entries.", ce);
				}
			}
			if (handler != null) {
				Configuration appConfig = null;
				String configPath = "apps/" + applicationName;
				try {
					appConfig = handler.openConfiguration(
						configPath, ConfigurationHandler.READ_ACCESS);
				} catch (ConfigurationException ce) {
					throw new DSRemoteException(
						"ASJ.dpl_ds.006190 Cannot open configuration "
						+ configPath + " for READ access.", ce);
				}
				if (appConfig != null) {
					try {
						return getModuleEntries(appConfig, module);
					} catch (ConfigurationException ce) {
						throw new DSRemoteException("ASJ.dpl_ds.006191 " +
							"Cannot search the configuration " +
							appConfig.getPath()	+ " for module with name " + 
							module + ".", ce);
					}
				}
			}
		} finally {
			try {
				if (handler != null) {
					handler.commit();
					handler.closeAllConfigurations();
				}
			} catch (ConfigurationException ce) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "getting module entries." }, ce);
				sde.setMessageID("ASJ.dpl_ds.005082");
				sde.setDcNameForObjectCaller(handler);
				DSLog.logErrorThrowable(location, sde);
			}
		}
		return null;
	}

	private String[] getModuleEntries(Configuration config, String module)
		throws ConfigurationException {
		String fileNames[] = config.getAllFileEntryNames();
		int index = DUtils.findElement(module, fileNames);
		if (index != -1) {
			List<String> list = new ArrayList<String>();
			ZipInputStream zis = null;
			try {
				zis = new ZipInputStream(config.getFile(fileNames[index]));
				if (zis != null) {
					ZipEntry entry = null;
					while (true) {
						entry = zis.getNextEntry();
						if (entry == null || entry.getName() == null) {
							break;
						}
						if (entry.getName().endsWith("/")
								|| entry.getName().endsWith("\\")) {
							continue;
						}
						list.add(entry.getName());
					}
				}
			} catch (IOException ioe) {
				zis = null;
			} finally {
				if (zis != null) {
					try {
						zis.close();
					} catch (IOException ioe) {
						zis = null;
					}
				}
			}
			String entries[] = new String[list.size()];
			list.toArray(entries);
			return entries;
		}
		String allSubConfigs[] = config.getAllSubConfigurationNames();
		String result[] = null;
		for (int i = 0; i < allSubConfigs.length; i++) {
			result = getModuleEntries(
				config.getSubConfiguration(allSubConfigs[i]), module);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.ClusterChangeListener#
	 * 		registerAllManagedObjects()
	 */
	public void registerAllManagedObjects() {
		appMBeanManager.registerAllManagedObjects();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.ClusterChangeListener#
	 * 		unregisterAllManagedObjects()
	 */
	public void unregisterAllManagedObjects() {
		appMBeanManager.unregisterAllManagedObjects();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * 		registerApplicationManagedObject(java.lang.String)
	 */
	public void registerApplicationManagedObject(String appName) {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		try {
			appMBeanManager.registerApplicationManagedObject(appName);
		} finally {
			long end = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addAppMngObjOperation(
				new DeployOperationTimeStat(
					DeployOperationTimeStat.APP_MNG_OBJ_REG, start,
					end, cpuStartTime, cpuEndTime));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * unregisterApplicationManagedObject(java.lang.String)
	 */
	public void unregisterApplicationManagedObject(String appName) {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		try {
			appMBeanManager.unregisterApplicationManagedObject(appName);
		} finally {
			long end = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat.addAppMngObjOperation(
				new DeployOperationTimeStat(
					ITimeStatConstants.APP_MNG_OBJ_UNREG, 
					start, end, cpuStartTime, cpuEndTime));
		}
	}

	/**
	 * Called only by DeployCommunicatorImpl.
	 * 
	 * @param migratorName
	 * @param migrator
	 * @throws CMigrationException
	 */
	final void registerMigrator(String migratorName,
		CMigrationInterface migrator) throws CMigrationException {
		CMigrationInvoker.getInstance().registerMigrator(
			migratorName, migrator);
	}

	private Hashtable<String, DeploymentInfo> getJ2EEApplications() {
		final Hashtable<String, DeploymentInfo> j2eeApps = 
			new Hashtable<String, DeploymentInfo>();
		final Iterator<String> dcNames = Applications.getNames().iterator();
		String dcName = null;
		DeploymentInfo dInfo = null;
		while (dcNames.hasNext()) {
			dcName = dcNames.next();
			dInfo = Applications.get(dcName);
			if (dInfo.isJ2EEApplication()) {
				j2eeApps.put(dInfo.getApplicationName(), dInfo);
			}
		}
		return j2eeApps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.DeployService
	 * 		#getCMigrationStatistic()
	 */
	public CMigrationStatistic getCMigrationStatistic() throws RemoteException {
		return CMigrationInvoker.getInstance().getCMigrationStatistic(this);
	}

	/**
	 * Called only by JavaVersionCommand to set the java version mode of an
	 * application.
	 * 
	 * @param appName
	 *            the name of the application.
	 * @param version
	 *            the java version to set
	 * 
	 * @throws RemoteException
	 *             if a problem occurs during the process.
	 */
	public void setJavaVersion(String appName, String sVersion)
		throws RemoteException {
		final DeploymentInfo dInfo = getApplicationInfo(appName);
		if (dInfo == null) {
			throw new RemoteException("ASJ.dpl_ds.006192 Application "
				+ appName
				+ " doesn't exist. Please use fully qualified name.");
		}
		if (sVersion != null && sVersion.equals(dInfo.getJavaVersion()) &&
			dInfo.isCustomJavaVersion()) {
			DSLog.logWarning(location, "ASJ.dpl_ds.002027",
				"Java version [{0}] is the same as current version. " +
				"Set operation aborted!", sVersion);
			return;
		}
		AdditionalAppInfo addAppInfo = new AdditionalAppInfo();
		addAppInfo.setFailOver(dInfo.getFailOver());
		addAppInfo.setStartUpO(dInfo.getStartUpO());
		// this set command is always considered custom java version edit
		int niValidationResult = addAppInfo.setJavaVersion(sVersion, true);
		// precaution - log erroneous custom values
		// although value should normally have been checked at this point
		switch (niValidationResult) {
		// abort if unsupported
		case IOpConstants.UNSUPPORTED:
			DSLog.logWarning(location, "ASJ.dpl_ds.002028",
				"Unsupported java version [{0}] was detected. " +
				"Set operation aborted!", sVersion);
			return;
			// abort if missing
		case IOpConstants.MISSING:
			DSLog.logWarning(location, "ASJ.dpl_ds.002029",
				"The java version is missing. Set operation aborted!");
			return;
			// correct if sub version
		case IOpConstants.SUB_VERSION:
			DSLog.logWarning(location, "ASJ.dpl_ds.002030",
				"A sub java version [{0}] was detected " +
				"in the application-j2ee-engine.xml. " +
				"Sub versions are not allowed! A version [{1}] will be used.",
				sVersion, addAppInfo.getJavaVersion());
			break;
		}
		setAdditionalAppInfo(appName, addAppInfo);
	}

	/**
	 * WARNING: This method has to be used only from UnlockApplicationCommand.
	 * 
	 * @param appName
	 * @throws ServerDeploymentException
	 */
	public void forcedUnregisterTransactionWithoutLock(String appName)
		throws ServerDeploymentException {
		ctx.getTxManager().forcedUnregisterTransactionWithoutLock(appName);
	}

	public Object catchDeploymentExceptionWithDSRem(DeploymentException dex,
		String action) throws RemoteException {
		DSLog.logErrorThrowable(location, dex);
		throw new DSRemoteException("ASJ.dpl_ds.006193 " +
			"Error while " + action, dex);
	}

	private Object catchDeploymentExceptionWithDSRol(Exception dex,
		String action) throws DSRollingException {
		DSLog.logErrorThrowable(location, dex);
		throw new DSRollingException("ASJ.dpl_ds.006196 Error while " + action,
				dex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.deploy.server.TransactionCommunicator#
	 * getManagementListenerUtils()
	 */
	public ManagementListenerUtils getManagementListenerUtils() {
		return mlUtils;
	}
}