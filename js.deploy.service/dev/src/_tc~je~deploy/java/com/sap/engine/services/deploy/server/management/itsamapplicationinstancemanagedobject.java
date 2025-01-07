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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.management.ObjectName;
import javax.naming.NamingException;

import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeActionStatus;
import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeApplicationInstance_Adapter;
import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeConfigurationProperty;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.properties.PropManager;

/**
 * @author Rumiana Angelova
 * @author Mariela Todorova
 */
public class ITSAMApplicationInstanceManagedObject extends
		SAP_ITSAMJ2eeApplicationInstance_Adapter {

	private final String internalName;
	private final String appName;
	private final ApplicationInstanceDelegate delegate;

	public ITSAMApplicationInstanceManagedObject(final String appName,
		final DSChangeLog changeLog, final int instanceId) {
		this.internalName = "instanceApplication=" + appName;
		this.appName = appName;
		delegate = new ApplicationInstanceDelegate(
			changeLog, appName, instanceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl#getName()
	 */
	@Override
	public String getName() {
		return this.internalName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl#getCaption()
	 */
	@Override
	public String getCaption() {
		return delegate.getCaption();
	}

	/*
	 * The method starts the applications asynchronous Starts the application in
	 * a new thread, and the actual result from the start is not return The
	 * return code 0, does not give you information, whether the start was
	 * successful To check the status of the application, the methods
	 * getStatus() and getState() should be used.
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl#Start(long, boolean)
	 */
	@Override
	public short Start(long timeout, boolean persist) {
		return delegate.asynchStart(timeout, persist);
	}

	/*
	 * The method stops the applications asynchronous Stops the application in a
	 * new thread, and the actual result from the start is not return The return
	 * code 0, does not give you information, whether the stop was successful To
	 * check the status of the application, the methods getStatus() and
	 * getState() should be used.
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl#Stop(long, boolean)
	 */
	@Override
	public short Stop(long timeout, boolean persist) {
		return delegate.asynchStop(timeout, persist);
	}

	/*
	 * The method restarts the applications synchronous The return code 0, does
	 * not give you information, whether the restart was successful To check the
	 * status of the application, the methods getStatus() and getState() should
	 * be used.
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl#Restart(long, boolean)
	 */
	@Override
	public short Restart(long timeout, boolean persist) {
		return delegate.asynchRestart();
	}

	/*
	 * This methods gets the status of an application and returns a number as a
	 * result, that represent its state.
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl#getOperationalStatus()
	 */
	@Override
	public short[] getOperationalStatus() {
		return delegate.getOperationalStatus();
	}

	/*
	 * The method will return the status of the application. If the application
	 * is in state Implicit_stopped or stopped on error will also return the
	 * error message of the exception
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl#getStatusDescriptions()
	 */
	@Override
	public String[] getStatusDescriptions() {
		return delegate.getStatusDescriptions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl
	 * #getSAP_ITSAMJ2eeApplicationInstanceTemplatePropertiesAntecedent()
	 */
	@Override
	public ObjectName getSAP_ITSAMJ2eeApplicationInstanceTemplatePropertiesAntecedent() {
		// TODO: not implemented
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl
	 * #getSAP_ITSAMJ2eeApplicationInstancePropertiesAntecedent()
	 */
	@Override
	public ObjectName getSAP_ITSAMJ2eeApplicationInstancePropertiesAntecedent() {
		// TODO: not implemented
		return null;
	}

	// Workaround for 7.20
	private Object getInstanceAdmin() throws NamingException {
		final ClassLoader clCurrent = Thread.currentThread()
				.getContextClassLoader();
		try {
			// TODO - Delete in 7.30 when CSN 0004444032 2008 is fixed
			final ClassLoader clBasicAdmin = PropManager.getInstance()
					.getAppServiceCtx().getCoreContext().getLoadContext()
					.getClassLoader("service:" + DeployConstants.SERVICE_BASICADMIN);
			Thread.currentThread().setContextClassLoader(clBasicAdmin);
			// TODO - Delete in 7.30 when CSN 0004444032 2008 is fixed

			return PropManager.getInstance().getInitialContext().lookup(
					DeployConstants.REMOTE_INSTANCE_ADMIN);
		} finally {
			Thread.currentThread().setContextClassLoader(clCurrent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl
	 * #UpdateProperties(com.sap.engine.admin
	 * .model.itsam.jsr77.application.SAP_ITSAMJ2eeConfigurationProperty[],
	 * java.lang.String[])
	 */
	@Override
	public SAP_ITSAMJ2eeActionStatus UpdateProperties(
		SAP_ITSAMJ2eeConfigurationProperty[] PropertiesToUpdate,
		String[] propertiesToRestore) throws SecurityException {
		String code = null;
		String description = null;
		String stackTrace = null;
		try {
			Object instanceAdmin = getInstanceAdmin();

			if (instanceAdmin != null) {
				Properties propsToUpdate = J2eeConfigurationPropertyArray2Properties(PropertiesToUpdate);
				Class[] arguments = new Class[] { Integer.TYPE, String.class,
						String.class, propsToUpdate.getClass(),
						propertiesToRestore.getClass(), Long.TYPE, Boolean.TYPE };
				String vendor = appName.substring(0, appName.indexOf('/'));
				String shortAppName = appName
						.substring(appName.indexOf('/') + 1);
				Method m = instanceAdmin.getClass().getMethod(
						"updateInstanceProperties", arguments);
				if (m != null) {
					m.invoke(instanceAdmin,
							new Object[] { new Integer(1), vendor,
									shortAppName, propsToUpdate,
									propertiesToRestore, new Long(0),
									new Boolean(true) });
				} else {
					code = SAP_ITSAMJ2eeActionStatus.ERROR_CODE;
				}
			} else {
				code = SAP_ITSAMJ2eeActionStatus.ERROR_CODE;
			}
			code = SAP_ITSAMJ2eeActionStatus.OK_CODE;
		} catch (InvocationTargetException invex) {
			code = SAP_ITSAMJ2eeActionStatus.ERROR_CODE;
			Throwable cause = invex.getCause();
			if (cause instanceof RemoteException) {
				RemoteException remoteException = (RemoteException) cause;
				Throwable cme = remoteException.getCause();
				if (cme != null) {
					Method m;
					try {
						m = cme.getClass().getMethod("getId", null);
						if (m != null) {
							description = (String) m.invoke(cme, null);
						}
					} catch (InvocationTargetException e) {
						throw new RuntimeException("ASJ.dpl_ds.006300",
								e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException("ASJ.dpl_ds.006301",
								e);

					} catch (NoSuchMethodException e) {
						throw new RuntimeException("ASJ.dpl_ds.006302",
								e);
					}
				}
			}
		} catch (Exception e) {
			code = SAP_ITSAMJ2eeActionStatus.ERROR_CODE;
			description = e.getMessage();
			stackTrace = DUtils.getStackTrace(e);
		}
		SAP_ITSAMJ2eeActionStatus status = new SAP_ITSAMJ2eeActionStatus(null,
				description, null);
		status.setCode(code);
		status.setStackTrace(stackTrace);
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.admin.model.itsam.jsr77.application.
	 * SAP_ITSAMJ2eeApplicationInstance_Impl
	 * #UpdateTemplateProperties(com.sap.engine
	 * .admin.model.itsam.jsr77.application
	 * .SAP_ITSAMJ2eeConfigurationProperty[], java.lang.String[])
	 */
	@Override
	public SAP_ITSAMJ2eeActionStatus UpdateTemplateProperties(
		SAP_ITSAMJ2eeConfigurationProperty[] PropertiesToUpdate,
		String[] propertiesToRestore) {

		String code = null;
		String description = null;
		String stackTrace = null;
		try {
			Object instanceAdmin = getInstanceAdmin();

			if (instanceAdmin != null) {
				Properties propsToUpdate = J2eeConfigurationPropertyArray2Properties(PropertiesToUpdate);
				Class[] arguments = new Class[] { Integer.TYPE, String.class,
						String.class, propsToUpdate.getClass(),
						propertiesToRestore.getClass(), Long.TYPE, Boolean.TYPE };
				String vendor = appName.substring(0, appName.indexOf('/'));
				String shortAppName = appName
						.substring(appName.indexOf('/') + 1);
				Method m = instanceAdmin.getClass().getMethod(
						"updateTemplateProperties", arguments);
				if (m != null) {
					m.invoke(instanceAdmin,
							new Object[] { new Integer(1), vendor,
									shortAppName, propsToUpdate,
									propertiesToRestore, new Long(0),
									new Boolean(true) });
				} else {
					code = SAP_ITSAMJ2eeActionStatus.ERROR_CODE;
				}
			} else {
				code = SAP_ITSAMJ2eeActionStatus.ERROR_CODE;
			}
			code = SAP_ITSAMJ2eeActionStatus.OK_CODE;
		} catch (InvocationTargetException invex) {
			code = SAP_ITSAMJ2eeActionStatus.ERROR_CODE;
			Throwable cause = invex.getCause();
			if (cause instanceof RemoteException) {
				RemoteException remoteException = (RemoteException) cause;
				Throwable cme = remoteException.getCause();
				if (cme != null) {
					Method m;
					try {
						m = cme.getClass().getMethod("getId", null);
						if (m != null) {
							description = (String) m.invoke(cme, null);
						}
					} catch (InvocationTargetException e) {
						throw new RuntimeException("ASJ.dpl_ds.006303",
								e);

					} catch (IllegalAccessException e) {
						throw new RuntimeException("ASJ.dpl_ds.006304",
								e);

					} catch (NoSuchMethodException e) {
						throw new RuntimeException("ASJ.dpl_ds.006305",
								e);

					}
				}
			}
		} catch (Exception e) {
			code = SAP_ITSAMJ2eeActionStatus.ERROR_CODE;
			description = e.getMessage();
			stackTrace = DUtils.getStackTrace(e);
		}
		SAP_ITSAMJ2eeActionStatus status = new SAP_ITSAMJ2eeActionStatus(null,
				description, null);
		status.setCode(code);
		status.setStackTrace(stackTrace);
		return status;
	}

	private Properties J2eeConfigurationPropertyArray2Properties(
			SAP_ITSAMJ2eeConfigurationProperty props[]) {
		Properties result = new Properties();
		if (props != null && props.length > 0) {
			int size = props.length;
			for (int i = 0; i < size; i++) {
				result.put(props[i].getInstanceID(), props[i].getValue());
			}
		}
		return result;
	}
}