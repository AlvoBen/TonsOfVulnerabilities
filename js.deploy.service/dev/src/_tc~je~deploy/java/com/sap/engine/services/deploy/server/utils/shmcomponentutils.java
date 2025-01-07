package com.sap.engine.services.deploy.server.utils;

import com.sap.bc.proj.jstartup.sadm.ShmComponent;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * @author I046522
 * 
 */

public class ShmComponentUtils {
	
	private static final Location location = 
		Location.getLocation(ShmComponentUtils.class);

	private static boolean boostPerformance = PropManager.getInstance()
			.isBoostPerformance();

	public static void setLocalStatus(Status status, String appName) {
		if (boostPerformance) {
			return;
		}

		try {
			final ShmComponent shmc = getShmComponent(appName);
			if (status.equals(Status.STARTING)) {
				setLocalStatus(shmc, ShmComponent.Status.STARTING);
			} else if (status.equals(Status.STARTED)) {
				setLocalStatus(shmc, ShmComponent.Status.STARTED);
			} else if (status.equals(Status.STOPPING)) {
				setLocalStatus(shmc, ShmComponent.Status.STOPPING);
			} else if (status.equals(Status.STOPPED)) {
				setLocalStatus(shmc, ShmComponent.Status.STOPPED);
			} else if (status.equals(Status.IMPLICIT_STOPPED)) {
				setLocalStatus(shmc, ShmComponent.Status.STOPPED);
			}
		} catch (ShmException e) {
			DSLog.logErrorThrowable(location, e);
		}
	}

	public static void setExpectedStatus(Status status, String appName) {
		if (boostPerformance) {
			return;
		}
		try {
			final ShmComponent shmc = getShmComponent(appName);
			if (status.equals(Status.STARTING)) {
				DSLog.traceError(
								location,
								"ASJ.dpl_ds.000401",
								"Shared memory reporting: The expected application's status can be only STARTED or STOPPED, not [{0}]. ",
								status);
			} else if (status.equals(Status.STARTED)) {
				setExpectedStatus(shmc, ShmComponent.Status.STARTED);
			} else if (status.equals(Status.STOPPING)) {
				DSLog.traceError(
								location, 
								"ASJ.dpl_ds.000402",
								"Shared memory reporting: The expected application's status can be only STARTED or STOPPED, not [{0}]. ",
								status);
			} else if (status.equals(Status.STOPPED)) {
				setExpectedStatus(shmc, ShmComponent.Status.STOPPED);
			} else if (status.equals(Status.IMPLICIT_STOPPED)) {
				setExpectedStatus(shmc, ShmComponent.Status.STOPPED);
			}
		} catch (ShmException e) {
			DSLog.logErrorThrowable(
							location, 
							"ASJ.dpl_ds.006397",
							"Error on trying to set expected status of shared memory component",
							e);
		}
	}

	public static void setStartupModeLazy(String appName) {
		setStartupMode(appName, ShmComponent.StartMode.LAZY);
	}

	public static void setStartupModeAlways(String appName) {
		setStartupMode(appName, ShmComponent.StartMode.ALWAYS);
	}

	public static void setStartupModeManual(String appName) {
		setStartupMode(appName, ShmComponent.StartMode.MANUAL);
	}

	public static void setStartupModeDisabled(String appName) {
		setStartupMode(appName, ShmComponent.StartMode.DISABLED);
	}

	private static void setStartupMode(String appName,
			ShmComponent.StartMode shmStartMode) {
		if (boostPerformance) {
			return;
		}
		try {
			final ShmComponent shmc = getShmComponent(appName);
			setStartupMode(shmc, shmStartMode);
		} catch (ShmException e) {
			DSLog.logErrorThrowable(
							location,
							"ASJ.dpl_ds.006399",
							"Error trying to set startup mode of shared memory component",
							e);
		}
	}

	private static void setStartupMode(ShmComponent shmc,
			ShmComponent.StartMode startup) throws ShmException {
		if (!startup.equals(shmc.getStartMode())) {
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"Will set startup mode [{0}] for [{1}] in shared memory, because its current value is [{2}]",
								startup, shmc.getName(), shmc.getStartMode());
			}
			shmc.setStartMode(startup);
		}
	}

	public static void setStartupModeManualWithCheck(String appName) {
		try {
			final ShmComponent shmc = getShmComponent(appName);
			setStartupMode(shmc, ShmComponent.StartMode.MANUAL);
		} catch (ShmException e) {
			DSLog.logErrorThrowable(
							location,
							"ASJ.dpl_ds.006400",
							"Error in trying to set startup mode of shared memory component to manual", e);
		}
	}

	public static void setLocalStatusFailed(String appName) {
		if (boostPerformance) {
			return;
		}
		try {
			final ShmComponent shmc = getShmComponent(appName);
			setLocalStatus(shmc, ShmComponent.Status.FAILED);
		} catch (ShmException e) {
			DSLog.logErrorThrowable(
							location,
							"ASJ.dpl_ds.006398",
							"Error on trying to set local status of shared memory component",
							e);
		}
	}

	public static void close(String appName) {
		if (boostPerformance) {
			return;
		}
		try {
			final ShmComponent shmc = getShmComponent(appName);
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Will close [{0}] in shared memory.", shmc
						.getName());
			}
			shmc.close(true);
		} catch (ShmException e) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006396",
					"Exception on closing shared memory component", e);
		}
	}

	private static ShmComponent getShmComponent(String appName)
			throws ShmException {
		return ShmComponent.find(appName, ShmComponent.Type.APPLICATION);
	}

	private static void setLocalStatus(ShmComponent shmc,
			ShmComponent.Status status) throws ShmException {
		if (!status.equals(shmc.getLocalStatus())) {
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"Will set local status [{0}] for [{1}] in shared memory.",
								status, shmc.getName());
			}
			shmc.setLocalStatus(status);
		}
	}

	private static void setExpectedStatus(ShmComponent shmc,
			ShmComponent.Status status) throws ShmException {
		if (!status.equals(shmc.getExpectedStatus())) {
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"Will set expected status [{0}] for [{1}] in shared memory.",
								status, shmc.getName());
			}
			shmc.setTargetStatus(status); // TODO - to use setTargetStatus(...)
			// instead of setExpectedStatus(...)
		}
	}

}
