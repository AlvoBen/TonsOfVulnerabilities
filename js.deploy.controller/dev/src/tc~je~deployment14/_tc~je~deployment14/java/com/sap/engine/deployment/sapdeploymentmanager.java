package com.sap.engine.deployment;

import java.io.File;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Locale;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.naming.NamingException;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.exception.standard.SAPUnsupportedOperationException;
import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.deployment.configuration.SAPDeploymentConfiguration;
import com.sap.engine.deployment.operations.DistributeOperation;
import com.sap.engine.deployment.operations.OperationExecutor;
import com.sap.engine.deployment.operations.RedeployOperation;
import com.sap.engine.deployment.operations.StartOperation;
import com.sap.engine.deployment.operations.StopOperation;
import com.sap.engine.deployment.operations.UndeployOperation;
import com.sap.engine.deployment.proxy.DeploymentProxy;
import com.sap.engine.deployment.proxy.DeploymentProxyImpl;
import com.sap.engine.deployment.proxy.LoginInfo;
import com.sap.engine.deployment.exceptions.SAPDConfigBeanVersionUnsupportedException;
import com.sap.engine.deployment.exceptions.SAPDeploymentManagerCreationException;
import com.sap.engine.deployment.exceptions.SAPIllegalStateException;
import com.sap.engine.deployment.exceptions.SAPInvalidModuleException;
import com.sap.engine.deployment.exceptions.SAPTargetException;
import com.sap.engine.deployment.exceptions.SAPRemoteException;
import com.sap.engine.deployment.exceptions.SAPIllegalArgumentsException;
import com.sap.engine.deployment.exceptions.ExceptionConstants;

/**
 * The DeploymentManager object provides the core set of functions a J2EE
 * platform must provide for J2EE application deployment. It provides server
 * related information, such as, a list of deployment targets, and vendor unique
 * runtime configuration information.
 * 
 * @author Mariela Todorova
 */
public class SAPDeploymentManager implements DeploymentManager {
	private static final Location location = Location
			.getLocation(SAPDeploymentManager.class);
	private Locale defaultLocale = Locale.ENGLISH;
	private Locale currentLocale = defaultLocale;
	private DeploymentProxy proxy = null;
	private LoginInfo login = null;

	public SAPDeploymentManager() {
	}

	public SAPDeploymentManager(LoginInfo info)
			throws SAPDeploymentManagerCreationException {
		this.login = info;
		this.proxy = new DeploymentProxyImpl(info);
	}

	/**
	 * Retrieve the list of deployment targets supported by this
	 * DeploymentManager.
	 * 
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @return A list of deployment Target designators the user may select for
	 *         application deployment or 'null' if there are none.
	 */
	public Target[] getTargets() throws SAPIllegalStateException {
		Logger.trace(location, Severity.PATH, "Getting targets");
		checkConnection(true);
		try {
			return proxy.getTargets();
		} catch (SAPRemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve the list of J2EE application modules distributed to the
	 * identified targets and that are currently running on the associated
	 * server or servers.
	 * 
	 * @param moduleType
	 *            A predefined designator for a J2EE module type.
	 * 
	 * @param targetList
	 *            A list of deployment Target designators the user wants checked
	 *            for module run status.
	 * 
	 * @return An array of TargetModuleID objects representing the running
	 *         modules or 'null' if there are none.
	 * 
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @throws TargetException
	 *             An invalid Target designator encountered.
	 */
	public TargetModuleID[] getRunningModules(ModuleType moduleType,
			Target[] targetList) throws SAPTargetException,
			SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG,
				"Getting running modules of type " + moduleType);

		checkConnection(false);

		if (moduleType == null || moduleType.equals("")) {
			return null;
		}

		if (targetList == null || targetList.length == 0) {
			throw new SAPTargetException(location,
					ExceptionConstants.NO_TARGETS);
		}

		try {
			return proxy.getRunningModules(moduleType,
					convertTargets(targetList));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve the list of J2EE application modules distributed to the
	 * identified targets and that are currently not running on the associated
	 * server or servers.
	 * 
	 * @param moduleType
	 *            A predefined designator for a J2EE module type.
	 * 
	 * @param targetList
	 *            A list of deployment Target designators the user wants checked
	 *            for module not running status.
	 * 
	 * @return An array of TargetModuleID objects representing the non-running
	 *         modules or 'null' if there are none.
	 * 
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @throws TargetException
	 *             An invalid Target designator encountered.
	 */
	public TargetModuleID[] getNonRunningModules(ModuleType moduleType,
			Target[] targetList) throws SAPTargetException,
			SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG,
				"Getting non-running modules of type " + moduleType);
		checkConnection(false);

		if (moduleType == null || moduleType.equals("")) {
			return null;
		}

		if (targetList == null || targetList.length == 0) {
			throw new SAPTargetException(location,
					ExceptionConstants.NO_TARGETS);
		}

		try {
			return proxy.getNonRunningModules(moduleType,
					convertTargets(targetList));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve the list of all J2EE application modules running or not running
	 * on the identified targets.
	 * 
	 * @param moduleType
	 *            A predefined designator for a J2EE module type.
	 * 
	 * @param targetList
	 *            A list of deployment Target designators the user wants checked
	 *            for module not running status.
	 * 
	 * @return An array of TargetModuleID objects representing all deployed
	 *         modules running or not or 'null' if there are no deployed
	 *         modules.
	 * 
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @throws TargetException
	 *             An invalid Target designator encountered.
	 */
	public TargetModuleID[] getAvailableModules(ModuleType moduleType,
			Target[] targetList) throws SAPTargetException,
			SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG,
				"Getting available modules of type " + moduleType);
		checkConnection(false);

		if (moduleType == null || moduleType.equals("")) {
			return null;
		}

		if (targetList == null || targetList.length == 0) {
			throw new SAPTargetException(location,
					ExceptionConstants.NO_TARGETS);
		}

		try {
			return proxy.getAvailableModules(moduleType,
					convertTargets(targetList));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve the object that provides server-specific deployment
	 * configuration information for the J2EE deployable component.
	 * 
	 * @param dObj
	 *            An object representing a J2EE deployable component.
	 * @throws InvalidModuleException
	 *             The DeployableObject is an unknown or unsupport component for
	 *             this configuration tool.
	 */
	public DeploymentConfiguration createConfiguration(DeployableObject dObj)
			throws SAPInvalidModuleException {
		Logger.trace(location, Severity.DEBUG,
				"Creating configuration for deployable object " + dObj);
		return new SAPDeploymentConfiguration(dObj, this);
	}

	/**
	 * The distribute method performs three tasks; it validates the deployment
	 * configuration data, generates all container specific classes and
	 * interfaces, and moves the fully baked archive to the designated
	 * deployment targets.
	 * 
	 * @param targetList
	 *            A list of server targets the user is specifying this
	 *            application be deployed to.
	 * @param moduleArchive
	 *            The file name of the application archive to be disrtibuted.
	 * @param deploymentPlan
	 *            The XML file containing the runtime configuration information
	 *            associated with this application archive.
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @return ProgressObject an object that tracks and reports the status of
	 *         the distribution process.
	 */
	public ProgressObject distribute(Target[] targetList, File moduleArchive,
			File deploymentPlan) throws SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG, "Distributing archive "
				+ moduleArchive + " with deployment plan " + deploymentPlan);
		checkConnection(false);

		if (targetList == null || targetList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGETS);
		}

		DistributeOperation operation = new DistributeOperation(proxy,
				convertTargets(targetList), moduleArchive, deploymentPlan);
		OperationExecutor.execute(operation);

		return operation;
	}

	/**
	 * The distribute method performs three tasks; it validates the deployment
	 * configuration data, generates all container specific classes and
	 * interfaces, and moves the fully baked archive to the designated
	 * deployment targets.
	 * 
	 * @param targetList
	 *            A list of server targets the user is specifying this
	 *            application be deployed to.
	 * @param moduleArchive
	 *            The input stream containing the application archive to be
	 *            disrtibuted.
	 * @param deploymentPlan
	 *            The input stream containing the deployment configuration
	 *            information associated with this application archive.
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @return ProgressObject an object that tracks and reports the status of
	 *         the distribution process.
	 * 
	 * @deprecated as of Java EE 5, replaced with
	 *             {@link #distribute(Target[], ModuleType, InputStream, InputStream)}
	 */
	public ProgressObject distribute(Target[] targetList,
			InputStream moduleArchive, InputStream deploymentPlan)
			throws SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG, "Distributing stream archive "
				+ moduleArchive + " with stream deployment plan "
				+ deploymentPlan);
		checkConnection(false);

		if (targetList == null || targetList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGETS);
		}

		DistributeOperation operation = new DistributeOperation(proxy,
				convertTargets(targetList), moduleArchive, deploymentPlan);
		OperationExecutor.execute(operation);

		return operation;
	}

	/**
	 * The distribute method performs three tasks; it validates the deployment
	 * configuration data, generates all container specific classes and
	 * interfaces, and moves the fully baked archive to the designated
	 * deployment targets.
	 * 
	 * @param targetList
	 *            A list of server targets the user is specifying this
	 *            application be deployed to.
	 * @param moduleType
	 *            The module type of this application archive.
	 * @param moduleArchive
	 *            The input stream containing the application archive to be
	 *            disrtibuted.
	 * @param deploymentPlan
	 *            The input stream containing the deployment configuration
	 *            information associated with this application archive.
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @return ProgressObject an object that tracks and reports the status of
	 *         the distribution process.
	 * 
	 */

	public ProgressObject distribute(Target[] targetList, ModuleType type,
			InputStream moduleArchive, InputStream deploymentPlan)
			throws IllegalStateException {
		Logger.trace(location, Severity.DEBUG, "Distributing " + type
				+ " stream archive " + moduleArchive
				+ " with stream deployment plan " + deploymentPlan);
		checkConnection(false);

		if (targetList == null || targetList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGETS);
		}

		DistributeOperation operation = new DistributeOperation(proxy,
				convertTargets(targetList), type, moduleArchive, deploymentPlan);
		OperationExecutor.execute(operation);

		return operation;
	}

	/**
	 * Start the application running.
	 * 
	 * <p>
	 * Only the TargetModuleIDs which represent a root module are valid for
	 * being started. A root TargetModuleID has no parent. A TargetModuleID with
	 * a parent can not be individually started. A root TargetModuleID module
	 * and all its child modules will be started.
	 * 
	 * @param moduleIDList
	 *            A array of TargetModuleID objects representing the modules to
	 *            be started.
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @return ProgressObject an object that tracks and reports the status of
	 *         the start operation.
	 */
	public ProgressObject start(TargetModuleID[] moduleIDList)
			throws SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG, "Starting target modules "
				+ moduleIDList);
		checkConnection(true);

		if (moduleIDList == null || moduleIDList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGET_MODULES);
		}

		StartOperation operation = new StartOperation(proxy,
				convertTargetModuleIDs(moduleIDList));
		OperationExecutor.execute(operation);

		return operation;
	}

	/**
	 * Stop the application running.
	 * 
	 * <p>
	 * Only the TargetModuleIDs which represent a root module are valid for
	 * being stopped. A root TargetModuleID has no parent. A TargetModuleID with
	 * a parent can not be individually stopped. A root TargetModuleID module
	 * and all its child modules will be stopped.
	 * 
	 * @param moduleIDList
	 *            A array of TargetModuleID objects representing the modules to
	 *            be stopped.
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @return ProgressObject an object that tracks and reports the status of
	 *         the stop operation.
	 */
	public ProgressObject stop(TargetModuleID[] moduleIDList)
			throws SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG, "Stopping target modules "
				+ moduleIDList);
		checkConnection(true);

		if (moduleIDList == null || moduleIDList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGET_MODULES);
		}

		StopOperation operation = new StopOperation(proxy,
				convertTargetModuleIDs(moduleIDList));
		OperationExecutor.execute(operation);

		return operation;
	}

	/**
	 * Remove the application from the target server.
	 * 
	 * <p>
	 * Only the TargetModuleIDs which represent a root module are valid for
	 * undeployment. A root TargetModuleID has no parent. A TargetModuleID with
	 * a parent can not be undeployed. A root TargetModuleID module and all its
	 * child modules will be undeployed. The root TargetModuleID module and all
	 * its child modules must stopped before they can be undeployed.
	 * 
	 * @param moduleIDList
	 *            An array of TargetModuleID objects representing the root
	 *            modules to be stopped.
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @return ProgressObject an object that tracks and reports the status of
	 *         the stop operation.
	 */
	public ProgressObject undeploy(TargetModuleID[] moduleIDList)
			throws SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG, "Undeploying target modules "
				+ moduleIDList);
		checkConnection(false);

		if (moduleIDList == null || moduleIDList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGET_MODULES);
		}

		UndeployOperation operation = new UndeployOperation(proxy,
				convertTargetModuleIDs(moduleIDList));
		OperationExecutor.execute(operation);

		return operation;
	}

	/**
	 * This method designates whether this platform vendor provides application
	 * redeployment functionality. A value of true means it is supported. False
	 * means it is not.
	 * 
	 * @return A value of true means redeployment is supported by this vendor's
	 *         DeploymentManager. False means it is not.
	 */
	public boolean isRedeploySupported() {
		return true;
	}

	/**
	 * (optional) The redeploy method provides a means for updating currently
	 * deployed J2EE applications. This is an optional method for vendor
	 * implementation.
	 * 
	 * Redeploy replaces a currently deployed application with an updated
	 * version. The runtime configuration information for the updated
	 * application must remain identical to the application it is updating.
	 * 
	 * When an application update is redeployed, all existing client connections
	 * to the original running application must not be disrupted; new clients
	 * will connect to the application update.
	 * 
	 * This operation is valid for TargetModuleIDs that represent a root module.
	 * A root TargetModuleID has no parent. A root TargetModuleID module and all
	 * its child modules will be redeployed. A child TargetModuleID module
	 * cannot be individually redeployed. The redeploy operation is complete
	 * only when this action for all the modules has completed.
	 * 
	 * @param moduleIDList
	 *            An array of designators of the applications to be updated.
	 * @param moduleArchive
	 *            The file name of the application archive to be disrtibuted.
	 * @param deploymentPlan
	 *            The deployment configuration information associated with this
	 *            application archive.
	 * @return ProgressObject an object that tracks and reports the status of
	 *         the redeploy operation.
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @throws java.lang.UnsupportedOperationException
	 *             this optional command is not supported by this
	 *             implementation.
	 */
	public ProgressObject redeploy(TargetModuleID[] moduleIDList,
			File moduleArchive, File deploymentPlan)
			throws SAPUnsupportedOperationException, SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG, "Redeploying target modules "
				+ moduleIDList + "; new archive: " + moduleArchive
				+ " with deployment plan: " + deploymentPlan);
		checkConnection(false);

		if (moduleIDList == null || moduleIDList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGET_MODULES);
		}

		RedeployOperation operation = new RedeployOperation(proxy,
				convertTargetModuleIDs(moduleIDList), moduleArchive,
				deploymentPlan);
		OperationExecutor.execute(operation);

		return operation;
	}

	/**
	 * (optional) The redeploy method provides a means for updating currently
	 * deployed J2EE applications. This is an optional method for vendor
	 * implementation.
	 * 
	 * Redeploy replaces a currently deployed application with an updated
	 * version. The runtime configuration information for the updated
	 * application must remain identical to the application it is updating.
	 * 
	 * When an application update is redeployed, all existing client connections
	 * to the original running application must not be disrupted; new clients
	 * will connect to the application update.
	 * 
	 * This operation is valid for TargetModuleIDs that represent a root module.
	 * A root TargetModuleID has no parent. A root TargetModuleID module and all
	 * its child modules will be redeployed. A child TargetModuleID module
	 * cannot be individually redeployed. The redeploy operation is complete
	 * only when this action for all the modules has completed.
	 * 
	 * @param moduleIDList
	 *            An array of designators of the applications to be updated.
	 * @param moduleArchive
	 *            The input stream containing the application archive to be
	 *            disrtibuted.
	 * @param deploymentPlan
	 *            The input stream containing the runtime configuration
	 *            information associated with this application archive.
	 * @return ProgressObject an object that tracks and reports the status of
	 *         the redeploy operation.
	 * @throws IllegalStateException
	 *             is thrown when the method is called when running in
	 *             disconnected mode.
	 * @throws java.lang.UnsupportedOperationException
	 *             this optional command is not supported by this
	 *             implementation.
	 */
	public ProgressObject redeploy(TargetModuleID[] moduleIDList,
			InputStream moduleArchive, InputStream deploymentPlan)
			throws SAPUnsupportedOperationException, SAPIllegalStateException {
		Logger.trace(location, Severity.DEBUG, "Redeploying target modules "
				+ moduleIDList + "; new stream archive: " + moduleArchive
				+ " with stream deployment plan: " + deploymentPlan);
		checkConnection(false);

		if (moduleIDList == null || moduleIDList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGET_MODULES);
		}

		RedeployOperation operation = new RedeployOperation(proxy,
				convertTargetModuleIDs(moduleIDList), moduleArchive,
				deploymentPlan);
		OperationExecutor.execute(operation);

		return operation;
	}

	/**
	 * The release method is the mechanism by which the tool signals to the
	 * DeploymentManager that the tool does not need it to continue running
	 * connected to the platform.
	 * 
	 * The tool may be signaling it wants to run in a disconnected mode or it is
	 * planning to shutdown.
	 * 
	 * When release is called the DeploymentManager may close any J2EE resource
	 * connections it had for deployment configuration and perform other related
	 * resource cleanup. It should not accept any new operation requests (i.e.,
	 * distribute, start stop, undeploy, redeploy. It should finish any
	 * operations that are currently in process. Each ProgressObject associated
	 * with a running operation should be marked as released (see the
	 * ProgressObject).
	 * 
	 */
	public void release() {
		if (proxy == null) {
			return;
		}

		// check for not finished operations!
		// log not finished operations

		try {
			proxy.disconnect();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
		proxy = null;
	}

	/**
	 * Returns the default locale supported by this implementation of
	 * javax.enterprise.deploy.spi subpackages.
	 * 
	 * @return Locale the default locale for this implementation.
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * Returns the active locale this implementation of
	 * javax.enterprise.deploy.spi subpackages is running.
	 * 
	 * @return Locale the active locale of this implementation.
	 */
	public Locale getCurrentLocale() {
		return currentLocale;
	}

	/**
	 * Set the active locale for this implementation of
	 * javax.enterprise.deploy.spi subpackages to run.
	 * 
	 * @throws java.lang.UnsupportedOperationException
	 *             the provide locale is not supported.
	 */
	public void setLocale(Locale locale)
			throws SAPUnsupportedOperationException {
		throw new SAPUnsupportedOperationException(location,
				SAPUnsupportedOperationException.UNSUPPORTED_OPERATION_1,
				new String[] { "Change active locale" });
	}

	/**
	 * Returns an array of supported locales for this implementation.
	 * 
	 * @return Locale[] the list of supported locales.
	 */
	public Locale[] getSupportedLocales() {
		return new Locale[] { defaultLocale };
	}

	/**
	 * Reports if this implementation supports the designated locale.
	 * 
	 * @return A value of 'true' means it is support and 'false' it is not.
	 */
	public boolean isLocaleSupported(Locale locale) {
		if (defaultLocale.equals(locale)) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the J2EE platform version number for which the configuration
	 * beans are provided. The beans must have been compiled with the J2SE
	 * version required by the J2EE platform.
	 * 
	 * @return a DConfigBeanVersionType object representing the platform version
	 *         number for which these beans are provided.
	 */
	public DConfigBeanVersionType getDConfigBeanVersion() {
		return DConfigBeanVersionType.V5;
	}

	/**
	 * Returns 'true' if the configuration beans support the J2EE platform
	 * version specified. It returns 'false' if the version is not supported.
	 * 
	 * @param version
	 *            a DConfigBeanVersionType object representing the J2EE platform
	 *            version for which support is requested.
	 * @return 'true' if the version is supported and 'false if not.
	 */
	public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType version) {
		return DConfigBeanVersionType.V5.equals(version);
	}

	/**
	 * Set the configuration beans to be used to the J2EE platform version
	 * specificed.
	 * 
	 * @param version
	 *            a DConfigBeanVersionType object representing the J2EE platform
	 *            version for which support is requested.
	 * @throws DConfigBeanVersionUnsupportedException
	 *             when the requested bean version is not supported.
	 */
	public void setDConfigBeanVersion(DConfigBeanVersionType version)
			throws SAPDConfigBeanVersionUnsupportedException {
		if (!DConfigBeanVersionType.V5.equals(version)) {
			throw new SAPDConfigBeanVersionUnsupportedException(location,
					ExceptionConstants.VERSION_NOT_SUPPORTED,
					new String[] { version == null ? "null" : version
							.toString() });
		}
	}

	private void checkConnection(boolean reconnect)
			throws SAPIllegalStateException {
		if (this.proxy == null) {
			throw new SAPIllegalStateException(location,
					ExceptionConstants.DISCONNECTED);
		}

		if (reconnect) {
			Logger.trace(location, Severity.DEBUG, "Reconnecting");
			try {
				this.proxy.disconnect();
			} catch (NamingException e) {
				throw new SAPIllegalStateException(location,
						ExceptionConstants.COULD_NOT_RECONNECT, e);
			} catch (ConnectionException e) {
				throw new SAPIllegalStateException(location,
						ExceptionConstants.COULD_NOT_RECONNECT, e);
			}

			try {
				proxy = new DeploymentProxyImpl(login);
			} catch (SAPDeploymentManagerCreationException e) {
				throw new SAPIllegalStateException(location,
						ExceptionConstants.COULD_NOT_RECONNECT, e);
			}
		}
	}

	private SAPTarget[] convertTargets(Target[] targetList) {
		Logger.trace(location, Severity.PATH,
				"Converting targets to SAP targets");
		SAPTarget[] targets = null;

		if (targetList != null) {
			targets = new SAPTarget[targetList.length];

			for (int i = 0; i < targetList.length; i++) {
				if (targetList[i] instanceof SAPTarget) {
					targets[i] = (SAPTarget) targetList[i];
				}
			}
		}

		return targets;
	}

	private SAPTargetModuleID[] convertTargetModuleIDs(
			TargetModuleID[] moduleIDList) {
		Logger.trace(location, Severity.PATH,
				"Converting target modules to SAP target modules");
		SAPTargetModuleID[] sapModuleIDs = null;

		if (moduleIDList != null) {
			sapModuleIDs = new SAPTargetModuleID[moduleIDList.length];

			for (int i = 0; i < moduleIDList.length; i++) {
				if (moduleIDList[i] instanceof SAPTargetModuleID) {
					sapModuleIDs[i] = (SAPTargetModuleID) moduleIDList[i];
				}
			}
		}

		return sapModuleIDs;
	}

	public SerializableFile getClientJar(TargetModuleID[] moduleIDList)
			throws SAPRemoteException {
		Logger.trace(location, Severity.DEBUG,
				"Getting client jar for target modules " + moduleIDList);
		checkConnection(false);

		if (moduleIDList == null || moduleIDList.length == 0) {
			throw new SAPIllegalArgumentsException(location,
					ExceptionConstants.NO_TARGET_MODULES);
		}

		return proxy.getClientJar(convertTargetModuleIDs(moduleIDList));
	}

}
