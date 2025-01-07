package com.sap.engine.services.deploy.server.management;

import java.rmi.RemoteException;
import java.util.List;

import com.sap.engine.admin.model.itsam.jsr77.application.SAP_ITSAMJ2eeActionStatus;
import com.sap.engine.admin.model.jsr77.StateManageable;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.exceptions.NameNotFoundException;
import com.sap.engine.lib.config.api.filters.ComponentFilter;
import com.sap.engine.lib.config.api.filters.FilterHandler;
import com.sap.engine.services.deploy.container.ExceptionInfo;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.AuthorizationChecker;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * Class which contains the common logic for ITSAMApplicationManagedObject and
 * ITSAMApplicationInstanceManagedObject.
 * 
 * @author Emil Dinchev
 */
public class ApplicationInstanceDelegate {
	
	private static final Location location = 
		Location.getLocation(ApplicationInstanceDelegate.class);
	
	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated Please use
	 *             com.sap.engine.services.deploy.container.dpl_info.module
	 *             .Status.STOPPED.getName()
	 */
	@Deprecated
	public static final String STOPPED = Status.STOPPED.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated Please use
	 *             com.sap.engine.services.deploy.container.dpl_info.module
	 *             .Status.STOPPING.getName()
	 */
	@Deprecated
	public static final String STOPPING = Status.STOPPING.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated Please use
	 *             com.sap.engine.services.deploy.container.dpl_info.module
	 *             .Status.STARTED.getName()
	 */
	@Deprecated
	public static final String STARTED = Status.STARTED.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated Please use
	 *             com.sap.engine.services.deploy.container.dpl_info.module
	 *             .Status.STARTING.getName()
	 */
	@Deprecated
	public static final String STARTING = Status.STARTING.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated Please use
	 *             com.sap.engine.services.deploy.container.dpl_info.module
	 *             .Status.IMPLICIT_STOPPED.getName()
	 */
	@Deprecated
	public static final String IMPLICIT_STOPPED = Status.IMPLICIT_STOPPED
			.getName();

	/**
	 * A status key, used for checking the application status.
	 * 
	 * @deprecated Please use
	 *             com.sap.engine.services.deploy.container.dpl_info.module
	 *             .Status.UNKNOWN.getName()
	 */
	@Deprecated
	public static final String UNKNOWN = Status.UNKNOWN.getName();

	// Error codes returned by Start and Stop methods
	public static final int EC_OK = 0;
	// General errors 1 .. 10
	public static final int EC_CANNOT_START_APP = 1;
	public static final int EC_CANNOT_STOP_APP = 2;
	// Authorization errors 11 .. 20
	public static final int EC_NO_AUTHORIZATION = 11;
	// Persistence errors 21 .. 30
	public static final int EC_CANNOT_PERSIST = 21;
	public static final int EC_CANNOT_OBTAIN_HANDLER = 22;

	// Operational status codes for method getOperationalStatus
	public static final int OSC_STARTED = 2;
	public static final int OSC_IMPLICIT_STOPPED = 5;
	public static final int OSC_STOPPED_ON_ERROR = 6;
	public static final int OSC_STARTING = 8;
	public static final int OSC_STOPPING = 9;
	public static final int OSC_STOPPED = 10;

	// Change Log
	static final String VIA_MBEAN = "MBean";

	private static final String AUTH_TO_PERSIST_STATE = "persist state";

	private final DSChangeLog changeLog;
	private final String appName;
	private final int instanceId;
	
	ApplicationInstanceDelegate(
		final DSChangeLog deploy, final String appName, final int instanceId) {
		this.changeLog = deploy;
		this.appName = appName;
		this.instanceId = instanceId;
	}

	public String getStatus() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);

		if (info != null) {
			Status status = info.getStatus();
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"Managed object for [{0}] with status [{1}]",
								appName, 
								status);
			}
			if (Status.STOPPED.equals(status)) {
				return STOPPED;
			} else if (Status.STOPPING.equals(status)) {
				return STOPPING;
			} else if (Status.STARTED.equals(status)) {
				return STARTED;
			} else if (Status.STARTING.equals(status)) {
				return STARTING;
			} else if (Status.IMPLICIT_STOPPED.equals(status)) {
				return IMPLICIT_STOPPED;
			} else if (Status.UNKNOWN.equals(status)) {
				return UNKNOWN;
			}
		}
		return UNKNOWN;
	}

	public int getState() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		if (info != null) {
			Status status = info.getStatus();
			if (Status.STOPPED.equals(status)) {
				return StateManageable.STOPPED;
			} else if (Status.STOPPING.equals(status)) {
				return StateManageable.STOPPING;
			} else if (Status.STARTED.equals(status)) {
				return StateManageable.RUNNING;
			} else if (Status.STARTING.equals(status)) {
				return StateManageable.STARTING;
			} else if (Status.IMPLICIT_STOPPED.equals(status)) {
				return StateManageable.FAILED;
			} else if (Status.UNKNOWN.equals(status)) {
				return StateManageable.FAILED;
			}
		}
		return StateManageable.FAILED;
	}

	public String getCaption() {
		return "Managed object for application " + appName;
	}

	public String getAppName() {
		final String name = appName.substring(appName.indexOf('/') + 1);
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Managed object for [{0}] with name [{1}]",
					appName, name);
		}
		return name;
	}

	/**
	 * This methods gets the status of an application and returns a number as a
	 * result, that represent its state. stopped - OSC_STOPPED (10) started -
	 * OSC_STARTED (2) stopping - OSC_STOPPING (9) starting - OSC_STARTING (8)
	 * Implicit_stopped OSC_STOPPED (10), OSC_IMPLICIT_STOPPED (5) stopped on
	 * error - OSC_STOPPED (10), OSC_STOPPED_ON_ERROR (6) unknown - OSC_STOPPED
	 * (10), OSC_STOPPED_ON_ERROR(6)
	 */
	public short[] getOperationalStatus() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		ExceptionInfo exceptionInfo = info.getExceptionInfo();
		if (info != null) {
			Status status = info.getStatus();
			if (Status.STOPPED.equals(status)) {
				if (exceptionInfo == null) {
					return new short[] { OSC_STOPPED };
				} else
					return new short[] { OSC_STOPPED, OSC_STOPPED_ON_ERROR };
			} else if (Status.STOPPING.equals(status)) {
				return new short[] { OSC_STOPPING };
			} else if (Status.STARTED.equals(status)) {
				return new short[] { OSC_STARTED };
			} else if (Status.STARTING.equals(status)) {
				return new short[] { OSC_STARTING };
			} else if (Status.IMPLICIT_STOPPED.equals(status)) {
				return new short[] { OSC_STOPPED, OSC_IMPLICIT_STOPPED };
			} else if (Status.UNKNOWN.equals(status)) {
				return new short[] { OSC_STOPPED, OSC_STOPPED_ON_ERROR };
			}
		}
		return new short[] { OSC_STOPPED, OSC_STOPPED_ON_ERROR };
	}

	/**
	 * The method will return the status of the application. If the application
	 * is in state Implicit_stopped or stopped on error will also return the
	 * error message of the exception
	 */
	public String[] getStatusDescriptions() {
		final String status = getStatus();
		final String exceptionStatus = getStatusDescription();
		return exceptionStatus == null ? new String[] { status }
				: new String[] { status, exceptionStatus };
	}

	private String getStatusDescription() {
		DeploymentInfo info = changeLog.getApplicationInfo(appName);
		if (info == null) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "No deployment info for [{0}]",
						appName);
			}
			return null;
		}
		ExceptionInfo exceptionInfo = info.getExceptionInfo();
		if (exceptionInfo == null) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "No exception info for [{0}]",
						appName);
			}
			return null;
		} else
			return exceptionInfo.getMessage();
	}

	/*
	 * Restart the application asynchronously in a new thread.
	 */
	public short asynchRestart() {
		final ThreadSystem threadCtx = PropManager.getInstance()
				.getThreadSystem();

		final Runnable run = new Runnable() {
			public void run() {
				try {
					changeLog.stopApplicationAndWait(appName, VIA_MBEAN);
					if (location.beDebug()) {
						DSLog.traceDebug(location, "Has stopped application [{0}]",
								appName);
					}
				} catch (RemoteException rex) {// $JL-EXC$
					DSLog.logErrorThrowable( 
									location,
									"ASJ.dpl_ds.000277",
									"Could not stop managed object for application {0}",
									rex, appName);
				}
				try {
					changeLog.startApplicationAndWait(appName, VIA_MBEAN);
					if (location.beDebug()) {
						DSLog.traceDebug(location, "Has started  application [{0}]",
							appName);
					}
				} catch (RemoteException rex) {// $JL-EXC$
					DSLog.logErrorThrowable(
											location,
											"ASJ.dpl_ds.000279",
											"Could not start managed object for application {0}",
											rex, appName);
				}
			}
		};
		threadCtx.startThread(run, null,
			DeployConstants.DEPLOY_RESTART_APP_THREAD_NAME, true, true);
		return EC_OK;
	}

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
	public SAP_ITSAMJ2eeActionStatus asynchStart(long seconds) {// wait &
		// synchronize
		SAP_ITSAMJ2eeActionStatus status = new SAP_ITSAMJ2eeActionStatus();
		status.setCode(SAP_ITSAMJ2eeActionStatus.OK_CODE);

		try {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Starting managed object for application [{0}]",
						appName);
			}
			// start in a new thread - actual status not returned
			changeLog.startApplication(appName, VIA_MBEAN);
		} catch (RemoteException rex) {// $JL-EXC$
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000281",
				"Could not start managed object for application [{0}]",
				rex, appName);
			status.setCode(SAP_ITSAMJ2eeActionStatus.ERROR_CODE);
			status.setMessageId("Could not start application [{0}]");
			status.setStackTrace(rex.toString());
			status.setMessageParameters(new String[] { appName });
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
	public short asynchStart(long timeout, boolean persist) {
		if (persist) {
			final short result = persistAppState(ComponentFilter.ACTION_START,
				Status.STARTED);
			if (result != ApplicationInstanceDelegate.EC_OK) {
				return result;
			}
		}
		return (short) (asynchStart(timeout).getCode().equals(
				SAP_ITSAMJ2eeActionStatus.OK_CODE) ? EC_OK
				: EC_CANNOT_START_APP);
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
	public SAP_ITSAMJ2eeActionStatus asynchStop(long seconds) {// wait &
		// synchronize
		SAP_ITSAMJ2eeActionStatus status = new SAP_ITSAMJ2eeActionStatus();
		status.setCode(SAP_ITSAMJ2eeActionStatus.OK_CODE);
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Stopping managed object for application [{0}].",
					appName);
			}
			// stop in a new thread - actual status not returned
			changeLog.stopApplication(appName, VIA_MBEAN);
		} catch (RemoteException rex) {// $JL-EXC$
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000283",
					"Could not stop managed object for application [{0}]", rex,
					appName);
			status.setCode(SAP_ITSAMJ2eeActionStatus.ERROR_CODE);
			status.setMessageId("Could not stop application [{0}]");
			status.setStackTrace(rex.toString());
			status.setMessageParameters(new String[] { appName });
		}
		return status;
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
	public short asynchStop(long timeout, boolean persist) {
		if (persist) {
			final short errCode = persistAppState(ComponentFilter.ACTION_STOP,
					Status.STOPPED);
			if (errCode != EC_OK) {
				return errCode;
			}
		}
		return (short) (asynchStop(timeout).getCode().equals(
				SAP_ITSAMJ2eeActionStatus.OK_CODE) ? EC_OK : EC_CANNOT_STOP_APP);
	}

	public String getVendor() {
		final String vendor = (appName.indexOf('/') > 0) ? appName.substring(0,
				appName.indexOf('/')) : "";
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Managed object for [{0}] with vendor [{1}]",
					appName, vendor);
		}
		return vendor;
	}

	private short persistAppState(int action, Status status) {
		short errCode;
		try {
			new AuthorizationChecker()
					.checkAuthorization(AUTH_TO_PERSIST_STATE);
			final CommonClusterFactory configFactory = ClusterConfiguration
					.getClusterFactory(PropManager.getInstance()
							.getConfigurationHandlerFactory());
			final ConfigurationLevel levelTemplate = configFactory
					.openConfigurationLevel(
							CommonClusterFactory.LEVEL_INSTANCE,
							"ID" + instanceId).getParent();
			final FilterHandler filterHandler = levelTemplate.getFilters(false);
			// create a new start rule for the component
			final ComponentFilter filterRule = filterHandler.createRule(action,
					ComponentFilter.COMPONENT_APPLICATION, getVendor(),
					getAppName());
			// get all filter rules on this level (template)
			final List<ComponentFilter> filter = filterHandler.getFilter();
			// add the new rule; the rule is added at the end of the list
			filter.add(filterRule);
			// set back the rules; this also immediately saves the filter
			filterHandler.setFilter(filter);
			errCode = EC_OK;
		} catch (NameNotFoundException nnfe) {
			DSLog.logErrorThrowable(location, 
				"ASJ.dpl_ds.000285",
				"Could not obtain a configuration level handler for the current instance {0}",
				nnfe, instanceId);
			errCode = EC_CANNOT_OBTAIN_HANDLER;
		} catch (ClusterConfigurationException cce) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000286",
				"Could not persist {0} state of application {1}", cce,
				status.getName(), appName);
			errCode = EC_CANNOT_PERSIST;
		} catch (RemoteException ex) {
			DSLog.logErrorThrowable(location, 
				"ASJ.dpl_ds.000287",
				"The current user has not permissions to persist {0} state of application {1}",
				ex, status.getName(), appName);
			errCode = EC_NO_AUTHORIZATION;
		}
		return errCode;
	}
}
