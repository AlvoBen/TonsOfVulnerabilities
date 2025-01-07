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
package com.sap.engine.services.deploy.server.management;

import java.util.Hashtable;
import java.util.Iterator;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplicationInstanceWrapperAdapter;
import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplicationWrapperAdapter;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.utils.Convertor;
import com.sap.tc.logging.Location;

/**
 * This class is used exclusively by deploy service implementation to register
 * and unregister MBeans for every deployed/undeployed application.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public final class AppManagedObjectManager {
	
	private static final Location location = 
		Location.getLocation(AppManagedObjectManager.class);
	
	private final Hashtable<String, ObjectName> managedObjects;
	private final DSChangeLog changeLog;
	private final int instanceId;
	/**
	 * Only one instance of this class exists in the current JVM. This
	 * constructor has to be called only by <code>DeployServiceImpl</code>.
	 * 
	 * @param deploy
	 */
	public AppManagedObjectManager(final DSChangeLog changeLog,
		final int instanceId) {
		managedObjects = new Hashtable<String, ObjectName>();
		this.changeLog = changeLog;
		this.instanceId = instanceId;
	}

	/**
	 * Register application MBean. Called by DeployServiceImpl.
	 * 
	 * @param appName
	 *            the name of the deployed application.
	 */
	public void registerApplicationManagedObject(String appName) {
		try {
			final ManagementModelManager mmm = getManagementModelManager();
			final String tagName = "Register application MBean:" + appName;
			try {
				Accounting.beginMeasure(tagName, ManagementModelManager.class);
				registerApplicationManagedObject(appName, mmm);
			} finally {
				Accounting.endMeasure(tagName);
			}
		} catch (OutOfMemoryError oofme) {
			throw oofme;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "registration of a managed object for application ["
					+ appName + "]" }, th);
			sde.setMessageID("ASJ.dpl_ds.005082");
			DSLog.logErrorThrowable(location, sde);
		}
	}

	private void registerApplicationManagedObject(String appName,
		ManagementModelManager mmm) throws InstanceAlreadyExistsException,
		MBeanRegistrationException, NotCompliantMBeanException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Will register application managed object for [{0}].",
				appName);
		}
		SAP_ITSAMJ2eeApplicationWrapperAdapter mmo = new SAP_ITSAMJ2eeApplicationWrapperAdapter(
			new ITSAMApplicationManagedObject(
				appName, mmm, changeLog, instanceId));
		ObjectName objName = mmm.registerITSAMManagedObject(mmo,
			"SAP_ITSAMJ2eeApplication", null);
		managedObjects.put(appName, objName);
		SAP_ITSAMJ2eeApplicationInstanceWrapperAdapter mmmo = 
			new SAP_ITSAMJ2eeApplicationInstanceWrapperAdapter(
				new ITSAMApplicationInstanceManagedObject(
					appName, changeLog, instanceId));
		objName = mmm.registerITSAMManagedObject(mmmo,
			"SAP_ITSAMJ2eeApplicationInstance", null);
		managedObjects.put("InstanceMBean:" + appName, objName);
	}

	/**
	 * Unregister application MBean. Called by DeployServiceImpl.
	 * 
	 * @param appName
	 *            the name of the deployed application.
	 */
	public void unregisterApplicationManagedObject(String appName) {
		try {
			final ManagementModelManager mmm = getManagementModelManager();
			final String tagName = "Register application MBean:" + appName;
			try {
				Accounting.beginMeasure(tagName, ManagementModelManager.class);
				unregisterApplicationManagedObject(appName, mmm);
			} finally {
				Accounting.endMeasure(tagName);
			}
		} catch (OutOfMemoryError oofme) {
			throw oofme;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Throwable th) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "unregistration of a managed object for application ["
					+ appName + "]" }, th);
			sde.setMessageID("ASJ.dpl_ds.005082");
			DSLog.logErrorThrowable(location, sde);
		}
	}

	private void unregisterApplicationManagedObject(String appName,
			ManagementModelManager mmm) throws InstanceNotFoundException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Will UNregister application managed object for [{0}].",
					appName);
		}
		ObjectName mmo = managedObjects.get(appName);
		if (mmo != null) {
			mmm.unregisterManagedObject(mmo);
		}
		mmo = managedObjects.get("InstanceMBean:" + appName);
		if (mmo != null) {
			mmm.unregisterManagedObject(mmo);
		}
	}

	/**
	 * Called by DeployServiceImpl to register MBeans for all deployed
	 * applications.
	 */
	public void registerAllManagedObjects() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Will register all managed objects for [{0}]",
					Convertor
							.toString(Applications.getNames(), ""));
		}
		ManagementModelManager mmm = null;
		try {
			mmm = getManagementModelManager();
		} catch (NamingException e) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "registration of all managed objects" }, e);
			sde.setMessageID("ASJ.dpl_ds.005082");
			DSLog.logErrorThrowable(location, sde);
		}
		if (mmm == null) {
			DSLog.traceError(location, 
							"ASJ.dpl_ds.002004",
							"The application managed objects won't be registered, because the ManagementModelManager is null.");
			return;
		}

		final Iterator<String> keys = Applications.getNames().iterator();
		while (keys.hasNext()) {
			final String key = keys.next();
			DeploymentInfo dInfo = changeLog.getApplicationInfo(key);
			if (dInfo != null && dInfo.isJ2EEApplication()) {
				try {
					this.registerApplicationManagedObject(key, mmm);
				} catch (Throwable th) {
					ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "registration of a managed object for application ["
									+ key + "]" }, th);
					sde.setMessageID("ASJ.dpl_ds.005082");
					DSLog
							.logErrorThrowable(location, sde);
				}
			}
		}
	}

	/**
	 * Called by DeployServiceImpl to unregister MBeans for all deployed
	 * applications.
	 */
	public void unregisterAllManagedObjects() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Will UNregister all managed objects for [{0}]",
				Convertor.toString(Applications.getNames(), ""));
		}
		ManagementModelManager mmm = null;
		try {
			mmm = getManagementModelManager();
		} catch (NamingException e) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "unregistration of all managed objects" },
				e);
			sde.setMessageID("ASJ.dpl_ds.005082");
			DSLog.logErrorThrowable(location, sde);
		}
		if (mmm == null) {
			DSLog.traceError(location, 
				"ASJ.dpl_ds.002005",
				"The application managed objects won't be unregistered, because the ManagementModelManager is null.");
			return;
		}
		final Iterator<String> keys = Applications.getNames().iterator();
		while (keys.hasNext()) {
			final String key = keys.next();
			try {
				this.unregisterApplicationManagedObject(key, mmm);
			} catch (OutOfMemoryError oofme) {
				throw oofme;
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable th) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "unregistration of a managed object for application ["
						+ key + "]" }, th);
				sde.setMessageID("ASJ.dpl_ds.005082");
				DSLog.logErrorThrowable(location, sde);
			}
		}
	}

	private ManagementModelManager getManagementModelManager()
			throws NamingException {
		final Context ctx = new InitialContext();
		return (ManagementModelManager) ctx
				.lookup(DeployConstants.SERVICE_BASICADMIN);
	}
}