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
package com.sap.engine.services.deploy.server.utils.container;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ContainerInterfaceExtension;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.ProgressEvent;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.start.ApplicationStartInfo;
import com.sap.engine.services.deploy.container.op.start.ContainerStartInfo;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetectorExt;
import com.sap.engine.services.deploy.ear.jar.moduledetect.ModuleDetectorWrapper;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.logging.DSLogConstants;
import com.sap.engine.services.deploy.server.DeployServiceFactory;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.application.AppConfigurationHandlerImpl;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.engine.services.deploy.server.utils.ServiceUtils;
import com.sap.engine.services.deploy.timestat.ContainerOperationTimeStat;
import com.sap.engine.services.deploy.timestat.ITimeStatConstants;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;
import com.sap.tools.memory.trace.AllocationStatisticRegistry;
import com.sap.engine.services.deploy.server.ReferenceResolver;

/**
 * This class is used to wrap the ContainerIntrfaceExtension.
 * 
 * @author Anton Georgiev
 * @version 6.40
 */
public class ContainerWrapper implements ContainerInterfaceExtension {
	
	private static final Location location = 
		Location.getLocation(ContainerWrapper.class);
	
	protected ContainerInterface realContainer;
	protected String contName;

	private static final Object codbMonitor = new Object();
	private static Map<String, Map<String, Long>> cName2OpDurBlob = null;
	private static boolean boostPerformance = PropManager.getInstance()
			.isBoostPerformance();

	private ContainerInfoWrapper containerInfo;
	private ModuleDetectorWrapper detectorWrapper;

	public ContainerWrapper(ContainerInterface realContainer) {
		contName = realContainer.getContainerInfo().getName();
		internalSetContainer(realContainer);
	}

	/**
	 * Create fake wrapper
	 * 
	 * @param contInfoXML
	 *            - containerInfo created from xml file.
	 * @param _comunicator
	 */
	public ContainerWrapper(ContainerInfoWrapper contInfoXML) {
		this.realContainer = null;
		this.containerInfo = contInfoXML;
		contName = containerInfo.getName();
	}

	/**
	 * Set _cont interface and make this "fake" wrapper real, with real _cont
	 * interface. Used by registerContainer() and unregisterContainer of
	 * ContainerManagementImpl.
	 * 
	 * @param realContainer
	 * @param _comunicator
	 */
	public void setContainer(ContainerInterface realContainer) {
		if (this.containerInfo == null) {
			throw new ServiceRuntimeException(DSLog.getLocalizedMessage(
					ExceptionConstants.ILLEGAL_CONTAINER_PASSED, new Object[0]));
		}
		if (realContainer == null) {
			this.realContainer = null;
			this.containerInfo.setModuleDetector(null);
			this.containerInfo.setGenerator(null);
		} else {
			String compatibility = this.containerInfo
					.checkCompatibility(realContainer.getContainerInfo());
			if (compatibility != null) {
				if (this.containerInfo.isForceServiceStart()) {
					throw new ServiceRuntimeException(
							DSLog
									.getLocalizedMessage(
											DSLogConstants.INCOMPATIBLE_CONTAINER_INFO,
											new Object[] { this.contName,
													compatibility }));
				} else {
					DSLog
							.traceWarningWithFaultyComponentCSN(
									location,
									realContainer,
									"ASJ.dpl_ds.002011",
									"The container info object, created from containers-info.xml and the one returned from getContainerInfo() method are not the same. Container name is: [{0}]. Different properties are: [{1}]",
									this.contName, compatibility);
				}
			}
			internalSetContainer(realContainer);

			ModuleDetector detector = realContainer.getContainerInfo()
					.getModuleDetector();
			if (detector != null) {
				detectorWrapper = new ModuleDetectorWrapper(detector,
						detector instanceof ModuleDetectorExt);
			}
			this.containerInfo.setModuleDetector(detector);
			this.containerInfo.setGenerator(realContainer.getContainerInfo()
					.getGenerator());
			ServiceUtils.notifyAllStopWaiting(containerInfo.getServiceName());
		}
	}

	private void internalSetContainer(ContainerInterface realContainer) {
		this.realContainer = realContainer;
		// ts
		// progress listener for handling sub operations time statistics
		realContainer.addProgressListener(new ProgressListener() {
			public void handleProgressEvent(ProgressEvent evt) {
				TransactionTimeStat.handleProgressEvent(evt);
			}
		});
	}

	private void checkAndStartComponent() throws ServerDeploymentException,
			ServiceException {		
		if (realContainer == null) {
			String checkAvailability = componentEnabledCheck(containerInfo.getComponent());
			if(checkAvailability.equals("")) {		
				ServiceUtils.startComponentAndWait(containerInfo.getComponent(),
					this.contName);
			} else {
			// throw an exception that says container is not started because its component 
			// was not eligible to be started, i.e. service was stopped manually - in this case we 
			// do not try to start it
				throw new IllegalStateException("Container [" + containerInfo.getName() +
					"] was not started.\n The reason is: " +
					checkAvailability);
			}
		}
	}

	private String componentEnabledCheck(Component comp) {
		String errorMessage = "";
		if (comp.getType().equals(Component.Type.APPLICATION)){
			DeploymentInfo dInfo = Applications.get(comp.getName());
			if (dInfo == null) {
				errorMessage = "The application that provides this container is not deployed";
			} else if(dInfo.getStartUpO().equals(StartUp.DISABLED)) {
				errorMessage = "The application that provides this container is DISABLED";
			} 
		} else { //we are in case 'service'
			boolean isExplicitlyDisabled = DeployServiceFactory.getDeployService()
											.getDeployServiceContext().getReferenceResolver()
											.getComponentsRepository().isExplicitlyDisabled(comp);
			if (isExplicitlyDisabled) {
				errorMessage = "The service that provides this container was " +
						"not eligible to be started, i.e. service was stopped manually";
				return errorMessage;
			}			
			final ServiceMonitor sMonitor = PropManager.getInstance()
			.getAppServiceCtx().getContainerContext().getSystemMonitor()
			.getService(comp.getName());
			if (sMonitor != null) {	
				if (sMonitor.getStartupMode() == ServiceMonitor.DISABLED) {
					errorMessage = "The service that provides this container is DISABLED";
				} else if (sMonitor.getStatus() == ComponentMonitor.STATUS_ACTIVE ) {			
						errorMessage = "The service that provides this container is started,"+
										" but container was not registered; contact the container owner";
				} else if (sMonitor.getStatus()!= ComponentMonitor.STATUS_LOADED) {
							errorMessage = "The service that provides this container was " +
											"not eligible to be started, i.e. service was stopped manually";
				}
			} else { //monitor == null
				errorMessage = "The service that provides this container is not deployed.";
			}
		}		
		return errorMessage;
	}
	
	private void checkAndStartComponentDE() throws DeploymentException {
		try {
			checkAndStartComponent();
		} catch (ServiceException e) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_START_SERVICE, new Object[] {
							containerInfo.getServiceName(), this.contName }, e);
			sde.setMessageID("ASJ.dpl_ds.005116");
			throw sde;
		}
	}

	private void checkAndStartComponentWE() throws WarningException {
		try {
			checkAndStartComponent();
		} catch (ServiceException e) {
			throw new WarningException(ExceptionConstants.CANNOT_START_SERVICE,
					new Object[] { containerInfo.getServiceName(),
							this.contName }, e);
		} catch (ServerDeploymentException e) {
			throw new WarningException(ExceptionConstants.CANNOT_START_SERVICE,
					new Object[] { containerInfo.getServiceName(),
							this.contName }, e);
		}
	}

	private void checkAndStartComponentRE() {
		try {
			checkAndStartComponent();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to start service ["
					+ containerInfo.getServiceName()
					+ "], which provides container [" + this.contName + "]", e);
		}
	}

	/**
	 * Override ContainerInfo method and check if the container service have to
	 * be started explicitly.
	 */
	public ContainerInfo getContainerInfo() {
		if (this.realContainer == null) {
			if (containerInfo.isForceServiceStart()) {
				ServiceUtils.startComponentAndWaitRE(containerInfo
						.getComponent(), containerInfo.getName());
			} else {
				return this.containerInfo;
			}
		}
		return realContainer.getContainerInfo();
	}

	/**
	 * Gets <code>ContainerInfo</code> when we do not want to start container
	 * provider, i.e. on unregister container.
	 * 
	 * @return
	 */

	public ContainerInfo getContainerInfoWithoutStart() {
		if (this.realContainer == null) {
			return this.containerInfo;
		} else {
			return realContainer.getContainerInfo();
		}
	}

	public String getApplicationName(File standaloneFile)
			throws DeploymentException {
		checkAndStartComponentDE();
		return realContainer.getApplicationName(standaloneFile);
	}

	private static final String deploy = "deploy";

	public ApplicationDeployInfo deploy(File[] archiveFiles,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(deploy, dInfo.getApplicationName());
			return realContainer.deploy(archiveFiles, dInfo, props);
		} finally {
			stop(deploy, dInfo.getApplicationName(), msAndCpu);
		}
	}

	private static final String notifyDeployedComponents = "notifyDeployedComponents";

	public void notifyDeployedComponents(String appName, Properties props)
			throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(notifyDeployedComponents, appName);
			realContainer.notifyDeployedComponents(appName, props);
		} finally {
			stop(notifyDeployedComponents, appName, msAndCpu);
		}
	}

	private static final String prepareDeploy = "prepareDeploy";

	public void prepareDeploy(String appName, Configuration appConfig)
			throws DeploymentException, WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(prepareDeploy, appName);
			realContainer.prepareDeploy(appName, appConfig);
		} finally {
			stop(prepareDeploy, appName, msAndCpu);
		}
	}

	private static final String commitDeploy = "commitDeploy";

	public void commitDeploy(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(commitDeploy, appName);
			realContainer.commitDeploy(appName);
		} finally {
			stop(commitDeploy, appName, msAndCpu);
		}
	}

	private static final String rollbackDeploy = "rollbackDeploy";

	public void rollbackDeploy(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(rollbackDeploy, appName);
			realContainer.rollbackDeploy(appName);
		} finally {
			stop(rollbackDeploy, appName, msAndCpu);
		}
	}

	private static final String needUpdate = "needUpdate";

	public boolean needUpdate(File[] archiveFiles,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException, WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(needUpdate, dInfo.getApplicationName());
			return realContainer.needUpdate(archiveFiles, dInfo, props);
		} finally {
			stop(needUpdate, dInfo.getApplicationName(), msAndCpu);
		}
	}

	private static final String needStopOnUpdate = "needStopOnUpdate";

	public boolean needStopOnUpdate(File[] archiveFiles,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException, WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(needStopOnUpdate, dInfo.getApplicationName());
			return realContainer.needStopOnUpdate(archiveFiles, dInfo, props);
		} finally {
			stop(needStopOnUpdate, dInfo.getApplicationName(), msAndCpu);
		}
	}

	private static final String makeUpdate = "makeUpdate";

	public ApplicationDeployInfo makeUpdate(File[] archiveFiles,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(makeUpdate, dInfo.getApplicationName());
			return realContainer.makeUpdate(archiveFiles, dInfo, props);
		} finally {
			stop(makeUpdate, dInfo.getApplicationName(), msAndCpu);
		}
	}

	private static final String notifyUpdatedComponents = "notifyUpdatedComponents";

	public void notifyUpdatedComponents(String appName,
			Configuration applicationConfig, Properties props)
			throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(notifyUpdatedComponents, appName);
			realContainer.notifyUpdatedComponents(appName, applicationConfig, props);
		} finally {
			stop(notifyUpdatedComponents, appName, msAndCpu);
		}
	}

	private static final String prepareUpdate = "prepareUpdate";

	public void prepareUpdate(String appName) throws DeploymentException,
			WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(prepareUpdate, appName);
			realContainer.prepareUpdate(appName);
		} finally {
			stop(prepareUpdate, appName, msAndCpu);
		}
	}

	private static final String commitUpdate = "commitUpdate";

	public ApplicationDeployInfo commitUpdate(String appName)
			throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(commitUpdate, appName);
			return realContainer.commitUpdate(appName);
		} finally {
			stop(commitUpdate, appName, msAndCpu);
		}
	}

	private static final String rollbackUpdate = "rollbackUpdate";

	public void rollbackUpdate(String appName, Configuration applicationConfig,
			Properties props) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(rollbackUpdate, appName);
			realContainer.rollbackUpdate(appName, applicationConfig, props);
		} finally {
			stop(rollbackUpdate, appName, msAndCpu);
		}
	}

	private static final String remove = "remove";

	public void remove(String appName) throws DeploymentException,
			WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(remove, appName);
			realContainer.remove(appName);
		} finally {
			stop(remove, appName, msAndCpu);
		}
	}

	private static final String downloadApplicationFiles = "downloadApplicationFiles";

	public void downloadApplicationFiles(String appName,
			Configuration applicationConfig) throws DeploymentException,
			WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(downloadApplicationFiles, appName);
			realContainer.downloadApplicationFiles(appName, applicationConfig);
		} finally {
			stop(downloadApplicationFiles, appName, msAndCpu);
		}
	}

	private static final String prepareStart = "prepareStart";

	public void prepareStart(String appName, Configuration applicationConfig)
			throws DeploymentException, WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(prepareStart, appName);
			realContainer.prepareStart(appName, applicationConfig);
		} finally {
			stop(prepareStart, appName, msAndCpu);
		}
	}

	private static final String commitStart = "commitStart";

	public void commitStart(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(commitStart, appName);
			realContainer.commitStart(appName);
		} finally {
			stop(commitStart, appName, msAndCpu);
		}
	}

	private static final String rollbackStart = "rollbackStart";

	public void rollbackStart(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(rollbackStart, appName);
			realContainer.rollbackStart(appName);
		} finally {
			stop(rollbackStart, appName, msAndCpu);
		}
	}

	private static final String prepareStop = "prepareStop";

	public void prepareStop(String appName, Configuration applicationConfig)
			throws DeploymentException, WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(prepareStop, appName);
			realContainer.prepareStop(appName, applicationConfig);
		} finally {
			stop(prepareStop, appName, msAndCpu);
		}
	}

	private static final String commitStop = "commitStop";

	public void commitStop(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(commitStop, appName);
			realContainer.commitStop(appName);
		} finally {
			stop(commitStop, appName, msAndCpu);
		}
	}

	private static final String rollbackStop = "rollbackStop";

	public void rollbackStop(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(rollbackStop, appName);
			realContainer.rollbackStop(appName);
		} finally {
			stop(rollbackStop, appName, msAndCpu);
		}
	}

	private static final String notifyRuntimeChanges = "notifyRuntimeChanges";

	public void notifyRuntimeChanges(String appName, Configuration appConfig)
			throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(notifyRuntimeChanges, appName);
			realContainer.notifyRuntimeChanges(appName, appConfig);
		} finally {
			stop(notifyRuntimeChanges, appName, msAndCpu);
		}
	}

	private static final String prepareRuntimeChanges = "prepareRuntimeChanges";

	public void prepareRuntimeChanges(String appName)
			throws DeploymentException, WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(prepareRuntimeChanges, appName);
			realContainer.prepareRuntimeChanges(appName);
		} finally {
			stop(prepareRuntimeChanges, appName, msAndCpu);
		}
	}

	private static final String commitRuntimeChanges = "commitRuntimeChanges";

	public ApplicationDeployInfo commitRuntimeChanges(String appName)
			throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(commitRuntimeChanges, appName);
			return realContainer.commitRuntimeChanges(appName);
		} finally {
			stop(commitRuntimeChanges, appName, msAndCpu);
		}
	}

	private static final String rollbackRuntimeChanges = "rollbackRuntimeChanges";

	public void rollbackRuntimeChanges(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(rollbackRuntimeChanges, appName);
			realContainer.rollbackRuntimeChanges(appName);
		} finally {
			stop(rollbackRuntimeChanges, appName, msAndCpu);
		}
	}

	private static final String getClientJar = "getClientJar";

	public File[] getClientJar(String appName) {
		long[] msAndCpu = null;
		checkAndStartComponentRE();
		try {
			msAndCpu = start(getClientJar, appName);
			return realContainer.getClientJar(appName);
		} finally {
			stop(getClientJar, appName, msAndCpu);
		}
	}

	private static final String addProgressListener = "addProgressListener";

	public void addProgressListener(ProgressListener listener) {
		long[] msAndCpu = null;
		checkAndStartComponentRE();
		try {
			msAndCpu = start(addProgressListener + "(" + listener + ")", null);
			realContainer.addProgressListener(listener);
		} finally {
			stop(addProgressListener + "(" + listener + ")", null, msAndCpu);
		}
	}

	private static final String removeProgressListener = "removeProgressListener";

	/**
	 * This method removes progress listener, but does not start component that
	 * provides container, because the method is called on container
	 * unregistration.
	 */
	public void removeProgressListener(ProgressListener listener) {
		long[] msAndCpu = null;
		try {
			msAndCpu = start(removeProgressListener + "(" + listener + ")",
					null);
			realContainer.removeProgressListener(listener);
		} finally {
			stop(removeProgressListener + "(" + listener + ")", null, msAndCpu);
		}
	}

	private static final String needStopOnSingleFileUpdate = "needStopOnSingleFileUpdate";

	public boolean needStopOnSingleFileUpdate(FileUpdateInfo[] files,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException, WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(needStopOnSingleFileUpdate, dInfo
					.getApplicationName());
			return realContainer.needStopOnSingleFileUpdate(files, dInfo, props);
		} finally {
			stop(needStopOnSingleFileUpdate, dInfo.getApplicationName(),
					msAndCpu);
		}
	}

	private static final String makeSingleFileUpdate = "makeSingleFileUpdate";

	public ApplicationDeployInfo makeSingleFileUpdate(FileUpdateInfo[] files,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(makeSingleFileUpdate, dInfo.getApplicationName());
			return realContainer.makeSingleFileUpdate(files, dInfo, props);
		} finally {
			stop(makeSingleFileUpdate, dInfo.getApplicationName(), msAndCpu);
		}
	}

	private static final String notifySingleFileUpdate = "notifySingleFileUpdate";

	public void notifySingleFileUpdate(String appName, Configuration config,
			Properties props) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(notifySingleFileUpdate, appName);
			realContainer.notifySingleFileUpdate(appName, config, props);
		} finally {
			stop(notifySingleFileUpdate, appName, msAndCpu);
		}
	}

	private static final String prepareSingleFileUpdate = "prepareSingleFileUpdate";

	public void prepareSingleFileUpdate(String appName)
			throws DeploymentException, WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(prepareSingleFileUpdate, appName);
			realContainer.prepareSingleFileUpdate(appName);
		} finally {
			stop(prepareSingleFileUpdate, appName, msAndCpu);
		}
	}

	private static final String commitSingleFileUpdate = "commitSingleFileUpdate";

	public ApplicationDeployInfo commitSingleFileUpdate(String appName)
			throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(commitSingleFileUpdate, appName);
			return realContainer.commitSingleFileUpdate(appName);
		} finally {
			stop(commitSingleFileUpdate, appName, msAndCpu);
		}
	}

	private static final String rollbackSingleFileUpdate = "rollbackSingleFileUpdate";

	public void rollbackSingleFileUpdate(String appName, Configuration config)
			throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(rollbackSingleFileUpdate, appName);
			realContainer.rollbackSingleFileUpdate(appName, config);
		} finally {
			stop(rollbackSingleFileUpdate, appName, msAndCpu);
		}
	}

	private static final String applicationStatusChanged = "applicationStatusChanged";

	public void applicationStatusChanged(String appName, byte status) {
		long[] msAndCpu = null;
		checkAndStartComponentRE();
		try {
			msAndCpu = start(applicationStatusChanged, appName);

			realContainer.applicationStatusChanged(appName, status);
		} finally {
			stop(applicationStatusChanged, appName, msAndCpu);
		}
	}

	private static final String getResourcesForTempLoader = "getResourcesForTempLoader";

	public String[] getResourcesForTempLoader(String appName)
			throws DeploymentException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(getResourcesForTempLoader, appName);
			return realContainer.getResourcesForTempLoader(appName);
		} finally {
			stop(getResourcesForTempLoader, appName, msAndCpu);
		}
	}

	private static final String acceptedAppInfoChange = "acceptedAppInfoChange";

	public boolean acceptedAppInfoChange(String appName,
			AdditionalAppInfo addAppInfo) throws DeploymentException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(acceptedAppInfoChange, appName);
			return realContainer.acceptedAppInfoChange(appName, addAppInfo);
		} finally {
			stop(acceptedAppInfoChange, appName, msAndCpu);
		}
	}

	private static final String needStopOnAppInfoChanged = "needStopOnAppInfoChanged";

	public boolean needStopOnAppInfoChanged(String appName,
			AdditionalAppInfo addAppInfo) {
		long[] msAndCpu = null;
		checkAndStartComponentRE();
		try {
			msAndCpu = start(needStopOnAppInfoChanged, appName);
			return realContainer.needStopOnAppInfoChanged(appName, addAppInfo);
		} finally {
			stop(needStopOnAppInfoChanged, appName, msAndCpu);
		}
	}

	private static final String makeAppInfoChange = "makeAppInfoChange";

	public void makeAppInfoChange(String appName, AdditionalAppInfo addAppInfo,
			Configuration configuration) throws WarningException,
			DeploymentException {
		long[] msAndCpu = null;
		checkAndStartComponentDE();
		try {
			msAndCpu = start(makeAppInfoChange, appName);
			realContainer.makeAppInfoChange(appName, addAppInfo, configuration);
		} finally {
			stop(makeAppInfoChange, appName, msAndCpu);
		}
	}

	private static final String appInfoChangedCommit = "appInfoChangedCommit";

	public void appInfoChangedCommit(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(appInfoChangedCommit, appName);
			realContainer.appInfoChangedCommit(appName);
		} finally {
			stop(appInfoChangedCommit, appName, msAndCpu);
		}
	}

	private static final String appInfoChangedRollback = "appInfoChangedRollback";

	public void appInfoChangedRollback(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(appInfoChangedRollback, appName);
			realContainer.appInfoChangedRollback(appName);
		} finally {
			stop(appInfoChangedRollback, appName, msAndCpu);
		}
	}

	private static final String notifyAppInfoChanged = "notifyAppInfoChanged";

	public void notifyAppInfoChanged(String appName) throws WarningException {
		long[] msAndCpu = null;
		checkAndStartComponentWE();
		try {
			msAndCpu = start(notifyAppInfoChanged, appName);
			realContainer.notifyAppInfoChanged(appName);
		} finally {
			stop(notifyAppInfoChanged, appName, msAndCpu);
		}
	}

	protected long[] start(String subOperation, String applicationName) {
		Accounting.beginMeasure(subOperation, this.realContainer.getClass());
		long[] res = null;
		if (PropManager.getInstance().isAdditionalDebugInfo()) {
			AllocationStatisticRegistry.pushThreadTag(
					ITimeStatConstants.DEPLOY_SERVICE + ":" + applicationName
							+ ":" + getContainerInfo().getName() + ":"
							+ subOperation, true);
			res = new long[] { System.currentTimeMillis(),
					SystemTime.currentCPUTimeUs() };
		}
		if (!boostPerformance) {
			ThreadWrapper.pushTask("Service [deploy] [" + subOperation
					+ "] on container [" + getContainerInfo().getName()
					+ "] for component [" + applicationName + "]",
					ThreadWrapper.TS_PROCESSING);
		}
		return res;
	}

	protected void stop(String subOperation, String applicationName,
			long[] msAndCpu) {
		Accounting.endMeasure(subOperation);
		if (PropManager.getInstance().isAdditionalDebugInfo()) {
			AllocationStatisticRegistry.popThreadTag();
			long endTime = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			ContainerOperationTimeStat contOperation = new ContainerOperationTimeStat(
					subOperation, applicationName, msAndCpu[0], endTime,
					msAndCpu[1], cpuEndTime);
			TransactionTimeStat.addContainerOp(contName, contOperation);
			calcCName2OpDurBlob(subOperation, endTime - msAndCpu[0]);
		}
		if (!boostPerformance) {
			ThreadWrapper.popTask();
		}
	}

	private void calcCName2OpDurBlob(String subOperation, long endTime) {
		final String cName = getContainerInfo().getName();
		synchronized (codbMonitor) {
			if (cName2OpDurBlob == null) {
				cName2OpDurBlob = new HashMap<String, Map<String, Long>>();
			}
			Map<String, Long> opDurBlob = cName2OpDurBlob.get(cName);
			if (opDurBlob == null) {
				opDurBlob = new HashMap<String, Long>();
			}
			Long duration = opDurBlob.get(subOperation);
			if (duration == null) {
				duration = new Long(0);
			}
			duration = new Long(duration.longValue() + endTime);
			opDurBlob.put(subOperation, duration);
			cName2OpDurBlob.put(cName, opDurBlob);
		}
	}

	public static void printCName2OpDurBlob() {
		synchronized (codbMonitor) {
			if (cName2OpDurBlob == null) {
				if (location.beDebug()) {
					DSLog.traceDebug(
									location,
									"The blob with the duration of "
									+ "the container operations is empty, because of the service properties.");
				}
				return;
			}
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
						"Will trace the blob with the duration of the container operations.");
			}
			final Iterator<String> cnIters = cName2OpDurBlob.keySet()
					.iterator();
			final StringBuilder result = new StringBuilder(
					"Current blob with the duration of the container operations follows ...");
			result.append(DSConstants.EOL);
			Iterator<String> opsIter = null;
			String cName = null, opName = null;
			Long duration = null;
			Map<String, Long> op2Dur = null;
			while (cnIters.hasNext()) {
				cName = cnIters.next();
				op2Dur = cName2OpDurBlob.get(cName);
				opsIter = op2Dur.keySet().iterator();
				while (opsIter.hasNext()) {
					opName = opsIter.next();
					duration = op2Dur.get(opName);
					result.append(cName.replace(" ", "_"));
					result.append(".");
					result.append(opName);
					result.append("\t");
					result.append(duration);
					result.append(DSConstants.EOL);
				}
			}
			if (location.beDebug()) {
				DSLog.traceDebug(location, "{0}", result);
			}
		}
	}

	/**
	 * Returns the name of the Container Interface implementation class as
	 * String
	 * 
	 * @return String
	 */

	public String getRealContainerName() {
		return realContainer != null ? realContainer.getClass().getName() : null;
	}

	public boolean hasContainerInfoWrapper() {
		return this.containerInfo != null;
	}

	/**
	 * We get the real container interface instead the container wrapper in
	 * order to call method getContainerInfo() on real container interface and
	 * not the container wrapper.
	 * 
	 * @return ContainerInterface
	 */
	public ContainerInterface getRealContainerInterface() {
		return this.realContainer;
	}

	/**
	 * Returns the module detector wrapper, which is used to call the
	 * corresponding method of the container's module detector depending on the
	 * implemented interface.
	 * 
	 * @return module detector wrapper
	 */
	public ModuleDetectorWrapper getDetectorWrapper() {
		if (this.hasContainerInfoWrapper() && detectorWrapper == null) {
			ModuleDetector detector = containerInfo.getModuleDetector();
			if (detector != null) {
				detectorWrapper = new ModuleDetectorWrapper(detector,
						detector instanceof ModuleDetectorExt);
			}
		}
		return detectorWrapper;
	}

	/**
	 * Sets module detector wrapper.
	 * 
	 * @param detectorWrapper
	 */
	public void setDetectorWrapper(ModuleDetectorWrapper detectorWrapper) {
		this.detectorWrapper = detectorWrapper;
	}

	private static final String notifyRemove = "notifyRemove";

	private static final String commitRemove = "commitRemove";

	private static final String makeStartInitially = "makeStartInitially";

	public ApplicationStartInfo makeStartInitially(ContainerStartInfo csInfo)
			throws DeploymentException {
		checkAndStartComponentDE();
		long[] msAndCpu = null;
		try {
			msAndCpu = start(makeStartInitially, csInfo.getApplicationName());
			if (realContainer instanceof ContainerInterfaceExtension) {
				return ((ContainerInterfaceExtension) realContainer)
						.makeStartInitially(csInfo);
			} else {
				return null;
			}
		} finally {
			stop(makeStartInitially, csInfo.getApplicationName(), msAndCpu);
		}
	}

	public void commitRemove(String appName) throws WarningException {
		checkAndStartComponentWE();
		long[] msAndCpu = null;
		try {
			msAndCpu = start(commitRemove, appName);
			if (realContainer instanceof ContainerInterfaceExtension) {
				((ContainerInterfaceExtension) realContainer).commitRemove(appName);
			}
		} finally {
			stop(commitRemove, appName, msAndCpu);
		}
	}

	public void remove(String applicationName, ConfigurationHandler handler,
			Configuration appConfig) throws WarningException,
			DeploymentException {
		checkAndStartComponentDE();
		long[] msAndCpu = null;
		try {
			msAndCpu = start(remove, applicationName);
			if (realContainer instanceof ContainerInterfaceExtension) {
				((ContainerInterfaceExtension) realContainer).remove(applicationName,
						new AppConfigurationHandlerImpl(handler), appConfig);
			} else {
				realContainer.remove(applicationName);
			}
		} finally {
			stop(remove, applicationName, msAndCpu);
		}
	}

	public void notifyRemove(String applicationName) throws WarningException {
		if (true) {
			throw new IllegalStateException(
					"Do not call this one, but call notifyRemoveInternal instead!");
		}
	}

	public void notifyRemoveInternal(String applicationName)
			throws WarningException, DeploymentException {
		checkAndStartComponentDE();
		long[] msAndCpu = null;
		try {
			msAndCpu = start(notifyRemove, applicationName);
			if (realContainer instanceof ContainerInterfaceExtension) {
				((ContainerInterfaceExtension) realContainer)
						.notifyRemove(applicationName);
			} else {
				realContainer.remove(applicationName);
			}
		} finally {
			stop(notifyRemove, applicationName, msAndCpu);
		}
	}

	/**
	 * Checks if the real container implements ContainerInterfaceExtension
	 * 
	 * @return true if cont is instance of ContainerInterfaceExtension
	 */
	public boolean isCIExtension() {
		return (realContainer != null && realContainer instanceof ContainerInterfaceExtension);
	}
}
