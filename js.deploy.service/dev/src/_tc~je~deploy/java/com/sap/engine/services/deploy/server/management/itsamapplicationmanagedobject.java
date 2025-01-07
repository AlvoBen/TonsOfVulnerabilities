/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.management;

import static com.sap.engine.services.deploy.server.DeployConstants.RESOURCE_TYPE_APPLICATION;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeActionStatus;
import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplicationSettings;
import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Adapter;
import com.sap.engine.frame.ComponentNameUtils;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.op.util.FailOver;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * This class provides a MBean wrapper for any deployed application. Usually
 * these MBeans are used by NWA admin console. Every public method can be
 * invoked via MBean server methods invoke() or getAttribute().
 * 
 * @author Rumiana Angelova
 * @author Mariela Todorova
 */
public final class ITSAMApplicationManagedObject 
	extends SAP_ITSAMJ2eeApplication_Adapter {

	private static final Location location = 
		Location.getLocation(ITSAMApplicationManagedObject.class);
	
	private static final String CIM_CLASS = "cimclass";
	private static final String LIBRARY_COMPONENT = "SAP_ITSAMJ2eeLibraryComponent";
	private static final String LIBRARY_NAME = "SAP_ITSAMJ2eeLibraryComponent.Name";
	private static final String SERVICE_COMPONENT = "SAP_ITSAMJ2eeServiceComponent";
	private static final String SERVICE_NAME = "SAP_ITSAMJ2eeServiceComponent.Name";
	private static final String INTERFACE_COMPONENT = "SAP_ITSAMJ2eeInterfaceComponent";
	private static final String INTERFACE_NAME = "SAP_ITSAMJ2eeInterfaceComponent.Name";
	private static final String NO_DPL_DESCRIPTOR = "640 application. No deployment descriptor is saved in the application configuration.";

	// instance fields
	private final String appName;
	private final ManagementModelManager mmm;
	private final DSChangeLog changeLog;
	private final String internalName;
	private final ApplicationInstanceDelegate delegate;

	public ITSAMApplicationManagedObject(final String appName,
		final ManagementModelManager mmm, final DSChangeLog changeLog,
		final int instanceId) {
		this.appName = appName;
		this.mmm = mmm;
		this.changeLog = changeLog;

		internalName = "name=" + appName + ",j2eeType=J2EEApplication";
		delegate = new ApplicationInstanceDelegate(
			changeLog, appName, instanceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Impl#getName()
	 */
	public String getName() {
		return this.internalName;
	}

	/**
	 * Return registered modules for the corresponding application. It is
	 * containers responsibility to register the application modules.
	 * 
	 * @return array of module names.
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Adapter#getModules()
	 */
	public String[] getModules() {
		Set<ObjectName> objectNames = mmm.getManagementModelHelper()
				.getApplicationModuleNames(appName);
		String moduleNames[] = new String[objectNames.size()];
		int index = 0;
		for (ObjectName objName : objectNames) {
			moduleNames[index++] = objName.toString();
		}
		return moduleNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Impl#getDeploymentDescriptor()
	 */
	public String getDeploymentDescriptor() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		if (info != null) {
			final String dd = PropManager.getInstance().isDdReadable() ? info
					.getApplicationXML() : null;
			return dd != null ? dd : NO_DPL_DESCRIPTOR;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#isstateManageable()
	 */
	public boolean isstateManageable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#isstatisticsProvider()
	 */
	public boolean isstatisticsProvider() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#iseventProvider()
	 */
	public boolean iseventProvider() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#getServer()
	 */
	public String getServer() {
		return mmm.getManagementModelHelper().getJ2EEServer().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Impl#getCaption()
	 */
	public String getCaption() {
		return delegate.getCaption();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#getState()
	 */
	public int getState() {
		return delegate.getState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#getStartTime()
	 * 
	 * @deprecated to be deleted in a short time if not used
	 */
	public long getStartTime() {
		// This change is synchronized with Tsonyo
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#start()
	 */
	public void start() {
		delegate.asynchStart(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#startRecursive()
	 */
	public void startRecursive() {
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Starting managed object for application [{0}].",
						appName);
			}
			changeLog.startApplicationAndWait(appName,
					ApplicationInstanceDelegate.VIA_MBEAN);
		} catch (RemoteException rex) {// $JL-EXC$
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000293",
					"Could not start managed object for application {0}", rex,
					appName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#stop()
	 */
	public void stop() {
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Stopping managed object for application [{0}].",
						appName);
			}
			changeLog.stopApplicationAndWait(appName,
					ApplicationInstanceDelegate.VIA_MBEAN);
		} catch (RemoteException rex) {// $JL-EXC$
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000295",
					"Could not stop managed object for application [{0}]", rex,
					appName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Impl#getSettings()
	 */
	public SAP_ITSAMJ2eeApplicationSettings getSettings() {
		SAP_ITSAMJ2eeApplicationSettings settings = new SAP_ITSAMJ2eeApplicationSettings(
				delegate.getVendor(), getSoftwareType(), getRemoteSupport(),
				// This change is synchronized with Tsonyo
				-1, getCaption(), delegate.getAppName());
		settings.setApplicationFailover(getApplicationFailover());
		settings.setStartupMode(getStartupMode());
		return settings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Impl
	 * #ApplyChanges(com.sap.engine.admin.model.itsam
	 * .jsr77.application.SAP_ITSAMJ2eeApplicationSettings)
	 */
	public SAP_ITSAMJ2eeActionStatus ApplyChanges(
			SAP_ITSAMJ2eeApplicationSettings settings) {
		SAP_ITSAMJ2eeActionStatus status = new SAP_ITSAMJ2eeActionStatus();
		status.setCode(SAP_ITSAMJ2eeActionStatus.OK_CODE);
		if (settings == null) {
			status.setCode(SAP_ITSAMJ2eeActionStatus.ERROR_CODE);
			status.setMessageId("Composite data should not be null");
			DSLog.traceWarning(location, "ASJ.dpl_ds.002006",
					"Cannot set null composite data for application [{0}].",
					appName);
			return status;
		}

		String failover = settings.getApplicationFailover();

		if (failover != null && !failover.equals(getApplicationFailover())) { // check
			// if changed by user
			status = changeFailoverMode(failover);

			if (SAP_ITSAMJ2eeActionStatus.OK_CODE.equals(status.getCode())) {
				if (location.beDebug()) {
					DSLog
							.traceDebug(location,
									"Composite data for application [{0}] changed successfully",
									appName);
				}
			}
		}

		return status;

	}

	/**
	 * Asynchronous start of the application.
	 * 
	 * @param timeout
	 *            timeout in seconds.
	 * @param persist
	 *            flag to persist the STARTED status of the application.
	 * @return the corresponding error code or 0 by success. When the method
	 *         returns 0 this does not means that the application is started. To
	 *         check the application status the methods getStatus() and
	 *         getState() should be used.
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#Start(long,
	 *      boolean)
	 */
	public short Start(long timeout, boolean persist) {
		return delegate.asynchStart(timeout, persist);
	}

	/**
	 * Asynchronous stop of the application.
	 * 
	 * @param timeout
	 *            operation timeout in seconds.
	 * @param persist
	 *            flag to persist the STARTED state of the application.
	 * @return the error code or 0 by success. When the method returns 0 this
	 *         does not means that the application is started. To check the
	 *         application status the methods getStatus() and getState() should
	 *         be used.
	 * 
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#Stop(long,
	 *      boolean)
	 */
	public short Stop(long timeout, boolean persist) {
		return delegate.asynchStop(timeout, persist);
	}

	/**
	 * Restarts the application asynchronously.
	 * 
	 * @param timeout
	 *            not used.
	 * @param persist
	 *            not used.
	 * @return Always returns 0 for success (EC_OK) , but this does not means
	 *         that the restart was successful. To check the status of the
	 *         application, the methods getStatus() and getState() should be
	 *         used.
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#Restart(long,
	 *      boolean)
	 */
	public short Restart(long timeout, boolean persist) {
		return delegate.asynchRestart();
	}

	// J2EEApplicationOperations
	/**
	 * Starts the application asynchronously.
	 * 
	 * @param seconds
	 *            not used.
	 * @return operation status. The status code is OK if the starting thread
	 *         was successfully started. This doesn't means that the application
	 *         itself was started. To check the status of the application, the
	 *         methods getStatus() and getState() should be used.
	 */
	public SAP_ITSAMJ2eeActionStatus start(long seconds) {
		return delegate.asynchStart(seconds);
	}

	/**
	 * Stops the application asynchronously.
	 * 
	 * @param seconds
	 *            not used.
	 * @return operation status. The status code is OK if the stopping thread
	 *         was successfully started. This doesn't means that the application
	 *         itself was stopped. To check the status of the application, the
	 *         methods getStatus() and getState() should be used.
	 */
	public SAP_ITSAMJ2eeActionStatus stop(long seconds) {
		return delegate.asynchStop(seconds);
	}

	private String getApplicationFailover() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		String failover = "";

		if (info != null) {
			failover = info.getFailOver().getName();
		}
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Managed object for [{0}] with failover mode [{1}]",
					appName,
					failover);
		}
		return failover;
	}

	private SAP_ITSAMJ2eeActionStatus changeFailoverMode(String failover) {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		SAP_ITSAMJ2eeActionStatus status = new SAP_ITSAMJ2eeActionStatus();
		status.setCode(SAP_ITSAMJ2eeActionStatus.OK_CODE);

		if (info == null) {
			if (location.beInfo()) {
				DSLog
						.traceInfo(location,
								"ASJ.dpl_ds.003006",
								"Could not change failover mode because deployment info for application [{0}] is null.",
								appName);
			}
			status.setCode(SAP_ITSAMJ2eeActionStatus.ERROR_CODE);
			status.setMessageId("Could not change failover mode for {0}");
			status.setMessageParameters(new String[] { appName });
			return status;
		}

		if (failover != null && !failover.equals("")) {
			failover = failover.toLowerCase();

			if (failover.equals(DeploymentInfo.FAIL_OVER_DISABLE)
					|| failover.equals(DeploymentInfo.FAIL_OVER_ON_ATTRIBUTE)
					|| failover.equals(DeploymentInfo.FAIL_OVER_ON_REQUEST)) {

				try {
					AdditionalAppInfo addAppInfo = new AdditionalAppInfo();
					addAppInfo.setFailOver(FailOver.getFailOverByKey(failover));
					addAppInfo.setStartUpO(info.getStartUpO());
					addAppInfo.setJavaVersion(info.getJavaVersion(), info
							.isCustomJavaVersion());

					changeLog.setAdditionalAppInfo(appName, addAppInfo,
							ApplicationInstanceDelegate.VIA_MBEAN);
					if (location.beDebug()) {
						DSLog
								.traceDebug(location,
										"Failover mode for [{0}] changed successfully to [{1}]",
										appName,
										failover);
					}
				} catch (RemoteException re) {// $JL-EXC$
					DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000299",
							"Could not change failover mode for {0}", re,
							appName);
					status.setCode(SAP_ITSAMJ2eeActionStatus.ERROR_CODE);
					status
							.setMessageId("Could not change failover mode for {0}");
					status.setStackTrace(DUtils.getStackTrace(re));
					status.setMessageParameters(new String[] { appName });
					return status;
				}
			}
		}
		return status;
	}

	private String getSoftwareType() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		String type = "";

		if (info != null) {
			type = info.getSoftwareType();
		}
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Managed object for [{0}] of software type [{1}]",
					appName, type);
		}
		return type;
	}

	private String[] getRemoteSupport() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		String[] support = null;
		String[] remote = null;

		if (info == null) {
			return remote;
		}

		support = info.getRemoteSupport();

		if (support == null) {
			return remote;
		}

		remote = new String[support.length];

		for (int i = 0; i < support.length; i++) {
			remote[i] = support[i];
		}

		return remote;
	}

	@SuppressWarnings("deprecation")
	private String getStartupMode() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		String startup = DeploymentInfo
				.getStartUpString(DeploymentInfo.STARTUP_DEFAULT);

		if (info != null) {
			startup = DeploymentInfo.getStartUpString(info.getStartUp());
		}
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Managed object for [{0}] with startup mode [{1}]",
					appName,
					startup);
		}
		return startup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Adapter#moduleNames()
	 */
	public String[] moduleNames() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);

		if (info == null) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "No deployment info for [{0}]",
						appName);
			}
			return null;
		}

		String[] containers = info.getContainerNames();

		if (containers == null || containers.length == 0) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "No containers for [{0}]",
						appName);
			}
			return null;
		}

		String cont = null;
		Set<Resource> modules = null;
		ArrayList<String> result = new ArrayList<String>();
		ContainerInfo cInfo = null;
		String type = null;
		String name = null;

		for (int i = 0; i < containers.length; i++) {
			cont = containers[i];

			if (cont == null || cont.equals("")) {
				continue;
			}

			modules = info.getProvidedResources(cont);

			if (modules == null || modules.size() == 0) {
				continue;
			}

			cInfo = changeLog.getContainerInfo(cont);

			if (cInfo == null) {
				continue;
			}

			if (cInfo.isJ2EEContainer()) {
				type = cInfo.getJ2EEModuleName();
			} else { // non-J2EE container
				type = cInfo.getModuleName();
			}

			for (Resource mod : modules) {
				if (mod == null) {
					continue;
				}

				name = mod.getName();
				if (location.beDebug()) {
					DSLog.traceDebug(location, "Module [{0}] of type [{1}] for application [{2}]",
							name,
							type, appName);
				}
				result.add("container=" + cont + ",module=" + name + ",type="
						+ type);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Impl#getStartupTime()
	 */
	public Date getStartupTime() {
		try {
			return new Date(getStartTime());
		} catch (Exception e) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000306",
					"Cannot get Startup Time", e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Impl#getInstallDate()
	 * 
	 * @deprecated to be deleted in a short time if not used
	 */
	public Date getInstallDate() {
		// This change is synchronized with Tsonyo
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplication_Impl#getOperationalStatus()
	 */
	public short[] getOperationalStatus() {
		return delegate.getOperationalStatus();
	}

	/**
	 * The method will return the status of the application. If the application
	 * is in state INPLICIT_STOPPED or STOPPED_ON_ERROR will also return the
	 * error message of the exception.
	 * 
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#getStatusDescriptions()
	 */
	public String[] getStatusDescriptions() {
		return delegate.getStatusDescriptions();
	}

	/**
	 * @return Object names for all resources that belong to the application
	 *         (are deployed with the application).
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#getSAP_ITSAMJ2eeApplicationResourcesDependent()
	 */
	@SuppressWarnings("unchecked")
	public ObjectName[] getSAP_ITSAMJ2eeApplicationResourcesDependent() {
		try {
			Context ctx = new InitialContext();
			MBeanServer server = (MBeanServer) ctx.lookup("jmx");
			ArrayList<ObjectName> result = new ArrayList<ObjectName>();
			ObjectName[] queries = new ObjectName[3];
			queries[0] = new ObjectName(
					":*,cimclass=SAP_ITSAMJ2eeResourceAdapter,SAP_ITSAMJ2eeApplication.Name="
							+ appName);
			queries[1] = new ObjectName(
					":*,cimclass=SAP_ITSAMJ2eeJMSConnectionFactoryReference,SAP_ITSAMJ2eeApplication.Name="
							+ appName);
			queries[2] = new ObjectName(
					":*,cimclass=SAP_ITSAMJ2eeJMSDestinationReference,SAP_ITSAMJ2eeApplication.Name="
							+ appName);
			for (int i = 0; i < queries.length; i++) {
				result.addAll(server.queryNames(queries[i], null));
			}
			return result.toArray(new ObjectName[result.size()]);
		} catch (Exception e) {
			DSLog.logErrorThrowable(
									location, 
									"ASJ.dpl_ds.000309",
									"Cannot get application resource references", e);
			return null;
		}
	}

	/**
	 * @return Object names for all modules that belong to the application.
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#getSAP_ITSAMJ2eeApplicationModulesDependent()
	 */
	@SuppressWarnings("unchecked")
	public ObjectName[] getSAP_ITSAMJ2eeApplicationModulesDependent() {
		final Set<ObjectName> result = mmm.getManagementModelHelper()
				.getApplicationModuleNames(appName);
		final ObjectName names[] = new ObjectName[result.size()];
		return result.toArray(names);
	}

	/**
	 * @return Object names for all applications that have reference to the
	 *         existing one.
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#getSAP_ITSAMJ2eeApplicationReferenceToJ2eeApplicationDependent()
	 */
	public ObjectName[] getSAP_ITSAMJ2eeApplicationReferenceToJ2eeApplicationDependent() {
		// TODO has to be implemented.
		return null;
	}

	/**
	 * @return Object names for all applications that current one has reference
	 *         to.
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#getSAP_ITSAMJ2eeApplicationReferenceToJ2eeApplicationAntecedent()
	 */
	@SuppressWarnings("unchecked")
	public ObjectName[] getSAP_ITSAMJ2eeApplicationReferenceToJ2eeApplicationAntecedent() {
		try {
			DeploymentInfo info = changeLog.getApplicationInfo(appName);

			if (info == null) {
				if (location.beDebug()) {
					DSLog.traceDebug(location, "No deployment info for [{0}].",
							appName);
				}
				return null;
			}

			ReferenceObject[] refs = info.getReferences();
			ReferenceObject ref = null;
			ArrayList result = new ArrayList();

			if (refs == null || refs.length == 0) {
				if (location.beDebug()) {
					DSLog.traceDebug(location, "No referenced components for [{0}].",
							appName);
				}
				return null;
			}

			Context ctx = new InitialContext();
			MBeanServer server = (MBeanServer) ctx.lookup("jmx");

			for (int i = 0; i < refs.length; i++) {
				ref = refs[i];

				if (ref == null) {
					continue;
				}

				if (ref.getReferenceTargetType().equals(
						RESOURCE_TYPE_APPLICATION)) {
					Set appNames = server.queryNames(new ObjectName(
							":*,cimclass=SAP_ITSAMJ2eeApplication,name=sap.com/"
									+ ref.getReferenceTarget()), null);
					if ((appNames != null) && (!appNames.isEmpty())) {
						result.add(appNames.iterator().next());
					} else {
						appNames = server.queryNames(new ObjectName(
								":*,cimclass=SAP_ITSAMJ2eeApplication,name=engine.sap.com/"
										+ ref.getReferenceTarget()), null);
						if ((appNames != null) && (!appNames.isEmpty())) {
							result.add(appNames.iterator().next());
						} else {
							appNames = server.queryNames(new ObjectName(
									":*,cimclass=SAP_ITSAMJ2eeApplication,name="
											+ ref.getReferenceTarget()), null);
							if ((appNames != null) && (!appNames.isEmpty())) {
								result.add(appNames.iterator().next());
							}
						}
					}
				}
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location,
									"Referenced component for application [{0}] - [{1}].",
									appName,
									ref);
				}
			}
			return (ObjectName[]) result.toArray(new ObjectName[result.size()]);
		} catch (Exception e) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000313",
					"Cannot get application to application references", e);
			return null;
		}
	}

	private Set<ReferenceObject> obtainApplicationReferences()
		throws RemoteException {
		if (changeLog.getApplicationInfo(appName) == null) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "No deployment info for [{0}].",
						appName);
			}
			return null;
		}

		final Set<ReferenceObject> refsSet = changeLog
				.getApplicationReferences(appName);

		if (refsSet == null || refsSet.size() == 0) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "No referenced components for [{0}].",
						appName);
			}
			return null;
		}
		return refsSet;
	}

	private ObjectName[] obtainDeployComponentNames() throws NamingException,
			MBeanException, ReflectionException, InstanceNotFoundException,
			AttributeNotFoundException {
		final ObjectName clusterName = mmm.getManagementModelHelper()
				.getSAP_ITSAMJ2eeClusterObjectName();
		final Context ctx = new InitialContext();
		final MBeanServer server = (MBeanServer) ctx.lookup("jmx");

		return (ObjectName[]) server.getAttribute(clusterName,
				"SAP_ITSAMJ2eeClusterDevelopmentComponentPartComponent");
	}

	private String getDcName(final String runtimeName,
			final Map<String, String> mapRTtoDC) {
		return mapRTtoDC.containsKey(runtimeName) ? mapRTtoDC.get(runtimeName)
				: runtimeName;
	}

	private int findDCNameIndex(final ReferenceObject ref,
			final ObjectName[] dcNames, final Map<String, String> mapRTtoDC) {
		final String dcName = getDcName(ref.getName(), mapRTtoDC);
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Referenced component dc name : [{0}]",
					dcName);
		}

		for (int i = 0; i < dcNames.length; i++) {
			if (DeployConstants.RESOURCE_TYPE_LIBRARY.equals(ref
					.getReferenceTargetType())
					&& LIBRARY_COMPONENT.equals(dcNames[i]
							.getKeyProperty(CIM_CLASS))
					&& dcNames[i].getKeyProperty(LIBRARY_NAME).equals(dcName)) {
				return i;
			}
			if (DeployConstants.RESOURCE_TYPE_SERVICE.equals(ref
					.getReferenceTargetType())
					&& SERVICE_COMPONENT.equals(dcNames[i]
							.getKeyProperty(CIM_CLASS))
					&& dcNames[i].getKeyProperty(SERVICE_NAME).equals(dcName)) {
				return i;
			}
			if (DeployConstants.RESOURCE_TYPE_INTERFACE.equals(ref
					.getReferenceTargetType())
					&& INTERFACE_COMPONENT.equals(dcNames[i]
							.getKeyProperty(CIM_CLASS))
					&& dcNames[i].getKeyProperty(INTERFACE_NAME).equals(dcName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return Object names for all server components that current application
	 *         has reference to.
	 * @see com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplication_Impl#getSAP_ITSAMJ2eeApplicationReferenceToJ2eeEngineComponentAntecedent()
	 */
	public ObjectName[] getSAP_ITSAMJ2eeApplicationReferenceToJ2eeEngineComponentAntecedent() {
		try {
			final Set<ReferenceObject> appRefSet = obtainApplicationReferences();
			if (appRefSet == null) {
				return null;
			}

			// ComponentNameUtils.
			final ObjectName[] dcNames = obtainDeployComponentNames();

			final Iterator<ReferenceObject> refsIter = appRefSet.iterator();
			final List<ObjectName> result = new ArrayList<ObjectName>();
			@SuppressWarnings("deprecation")
			final HashMap<String, String> mapRTtoDC = ComponentNameUtils
					.getRTtoDCMapping(PropManager.getInstance()
							.getConfigurationHandlerFactory());

			while (refsIter.hasNext()) {
				// We suppose that there are not null elements in the set of
				// application references.
				final ReferenceObject ref = refsIter.next();
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location,
									"Referenced component for application [{0}] - [{1}]",
									appName,
									ref);
				}

				final int index = findDCNameIndex(ref, dcNames, mapRTtoDC);
				if (index == -1) {
					if (location.beDebug()) {
						DSLog.traceDebug(location, "No ObjectName is found for [", ref
								.getReferenceTarget(), "] with type [", ref
								.getReferenceTargetType(), "]");
					}
					continue;
				}
				result.add(dcNames[index]);
			}
			return result.toArray(new ObjectName[result.size()]);
		} catch (Exception e) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000319",
					"Cannot get application component references", e);
			return null;
		}
	}
}