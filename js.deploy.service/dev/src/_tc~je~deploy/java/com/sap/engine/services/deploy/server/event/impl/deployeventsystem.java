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
package com.sap.engine.services.deploy.server.event.impl;

import com.sap.engine.services.deploy.DeployCallback;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ProgressEvent;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.tc.logging.Location;

/**
 * @author Rumiana Angelova
 * @version 6.30
 */
public final class DeployEventSystem implements ProgressListener {
	
	private static final Location location = 
		Location.getLocation(DeployEventSystem.class);
	
	private final String clusterElementName;
	private final boolean isDebug;

	private DeployCallback[] callbacks;
	private DeployServiceContext ctx;

	public DeployEventSystem(final String clusterElementName,
		final boolean isDebug) {
		this.clusterElementName = PropManager.getInstance().getClElemName();
		this.isDebug = PropManager.getInstance().isAdditionalDebugInfo();

		callbacks = new DeployCallback[0];
	}

	public void activate(DeployServiceContext ctx) {
		this.ctx = ctx;
	}

	public synchronized void addDeployCallback(DeployCallback callback) {
		if (callback == null) {
			return;
		}
		if (found(callback) != -1) {
			return;
		}

		if(isDebug) {
			final Exception traceEx = new Exception(
				"Tracing the stack trace in order to see the owner of "
					+ callback + " instance.");
			DSLog.traceDebugThrowable(location, null, traceEx, "ASJ.dpl_ds.000235", 
				"{0}", traceEx.getMessage());
		}

		DeployCallback[] temp = new DeployCallback[callbacks.length + 1];
		System.arraycopy(callbacks, 0, temp, 0, callbacks.length);
		temp[callbacks.length] = callback;
		callbacks = temp;
	}

	private int found(DeployCallback l) {
		for (int j = callbacks.length - 1; j >= 0; j--) {
			if (l.equals(callbacks[j])) {
				return j;
			}
		}
		return -1;
	}

	public synchronized void removeDeployCallback(DeployCallback callback) {
		int index = found(callback);
		if (index == -1) {
			return;
		}
		DeployCallback[] temp = new DeployCallback[callbacks.length - 1];
		System.arraycopy(callbacks, 0, temp, 0, index);
		if (index != (callbacks.length - 1)) {
			System.arraycopy(callbacks, index + 1, temp, index,
					callbacks.length - index - 1);
		}
		callbacks = temp;
	}

	public void fireDeployEvent(String compName, byte componentType,
		byte phase, String action, String msg, String errors[],
		String warnings[], String softwareType) {
		if (filterEvents(action)) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, 
					"The deploy event with action [{0}] will be filtered and will not be sent.",
					action);
			}
			return;
		}
		byte actionType = this.defineActionType(action);
		DeployEvent event = 
			new DeployEvent(compName, phase, actionType, clusterElementName);
		event.setMessage(msg);
		event.setErrors(errors);
		event.setWarnings(warnings);
		this.fireDeployEvent(event, componentType, softwareType);
	}

	private boolean filterEvents(String action) {
		if (DeployConstants.oncePerInstance.equals(action)) {
			return true;
		}
		return false;
	}

	public byte defineActionType(String action) {
		byte actionType = -1;
		if (action == null) {
			return actionType;
		}
		if (action.equals(DeployConstants.deploy)) {
			actionType = DeployEvent.DEPLOY_APP;
		} else if (action.equals(DeployConstants.deployLib)) {
			actionType = DeployEvent.DEPLOY_LIB;
		} else if (action.equals(DeployConstants.removeLib)) {
			actionType = DeployEvent.REMOVE_LIB;
		} else if (action.equals(DeployConstants.makeRefs)) {
			actionType = DeployEvent.MAKE_REFS;
		} else if (action.equals(DeployConstants.removeRefs)) {
			actionType = DeployEvent.REMOVE_REFS;
		} else if (action.equals(DeployConstants.update)) {
			actionType = DeployEvent.UPDATE_APP;
		} else if (action.equals(DeployConstants.removeApp)) {
			actionType = DeployEvent.REMOVE_APP;
		} else if (action.equals(DeployConstants.stopApp)) {
			actionType = DeployEvent.STOP_APP;
		} else if (action.equals(DeployConstants.startApp)) {
			actionType = DeployEvent.START_APP;
		} else if (action.equals(DeployConstants.runtimeChanges)) {
			actionType = DeployEvent.RUNTIME_CHANGES;
		} else if (action.equals(DeployConstants.singleFileUpdate)) {
			actionType = DeployEvent.SINGLE_FILE_UPDATE;
		} else if (action.equals(DeployConstants.appInfoChange)) {
			actionType = DeployEvent.ADD_APP_INFO_CHANGE;
		} else if (action.equals(DeployConstants.initialStartApplications)) {
			actionType = DeployEvent.INITIAL_START_APPLICATIONS;
		} else if (action.equals(DeployConstants.startInitiallyApp)) {
			actionType = DeployEvent.START_INITIALLY;
		}
		return actionType;
	}

	private void setAppRelatedData(DeployEvent dEvent) {
		byte status = DeployEvent.UNKNOWN_STATUS;
		if (dEvent.getComponentName() != null) {
			DeploymentInfo info = ctx.getLocalDeployment().getApplicationInfo(
				dEvent.getComponentName());
			if (info == null) {
				status = DeployEvent.REMOVED_STATUS;
			} else {
				status = convertAppStatus(info.getStatus());
			}
			dEvent.setStatus(status);
		}
	}

	public void fireDeployEvent(final DeployEvent event, 
		final byte componentType, final String softwareType) {
		if (event == null) {
			return;
		}
		final long startGlobal = System.currentTimeMillis();

		switch (componentType) {
		case (DeployConstants.APP_TYPE): {
			setAppRelatedData(event);
			break;
		}
		case (DeployConstants.MODULE_TYPE): {
			setAppRelatedData(event);
			break;
		}
		case (DeployConstants.LIB_TYPE): {
			break;
		}
		case (DeployConstants.SERVICE_TYPE): {
			break;
		}
		case (DeployConstants.INTERFACE_TYPE): {
			break;
		}
		}
		boolean isTraced = false;
		long startLocal = 0;
		StringBuilder deTimes = null;
		
		DeployCallback [] callbacks = this.callbacks;
		
		for (int i = callbacks.length - 1; i >= 0; i--) {
			startLocal = System.currentTimeMillis();
			try {
				switch (componentType) {
				case (DeployConstants.APP_TYPE): {
					if (softwareType != null) {
						if (!isTraced) {
							if (location.beDebug()) {
								DSLog.traceDebug(
												location, 
									"The following event will not be sent, because its software type is [{0}].\n[{1}]",
									softwareType,
									event.toString());
							}
							isTraced = true;
						}
					} else {
						callbacks[i].processApplicationEvent(event);
					}
					break;
				}
				case (DeployConstants.LIB_TYPE): {
					callbacks[i].processLibraryEvent(event);
					break;
				}
				case (DeployConstants.SERVICE_TYPE): {
					callbacks[i].processServiceEvent(event);
					break;
				}
				case (DeployConstants.INTERFACE_TYPE): {
					callbacks[i].processInterfaceEvent(event);
					break;
				}
				case (DeployConstants.REF_TYPE): {
					callbacks[i].processReferenceEvent(event);
					break;
				}
				case (DeployConstants.MODULE_TYPE): {
					if (softwareType != null) {
						if (location.beDebug()) {
							DSLog.traceDebug(
											location,
								"The following event will not be sent, because its software type is [{0}].\n[{1}]",
								softwareType,
								event.toString());
						}
					} else {
						callbacks[i].processStandaloneModuleEvent(event);
					}
					break;
				}
				}
			} catch (P4ConnectionException p4cex) {
				if (!ctx.isMarkedForShutdown()) {
					ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.CALLBACK_THROWS_EXCEPTION,
							new String[] { event.toString() }, p4cex);
					sde.setMessageID("ASJ.dpl_ds.006308");
					DSLog.logErrorThrowable(location, sde);
				}
				removeDeployCallback(callbacks[i]);
			} catch (Exception ex) {
				if (!ctx.isMarkedForShutdown()) {
					final DeploymentException dex;
					if (ex instanceof DeploymentException) {
						dex = (DeploymentException) ex;
					} else {
						dex = new ServerDeploymentException(
							ExceptionConstants.CALLBACK_THROWS_EXCEPTION,
							new String[] { event.toString() }, ex);
						dex.setMessageID("ASJ.dpl_ds.006308");
					}

					DSLog.traceDebugThrowable(
						location,
						callbacks[i], dex,
						"ASJ.dpl_ds.000239",
						"The [{0}] threw [{1}], while processing the deploy event sent to it. For more details, check DEBUG traces of deploy service.",
						callbacks[i], ex.getClass());
				}
			} catch (OutOfMemoryError oofme) {
				throw oofme;
			} catch (Throwable th) {
				if (!ctx.isMarkedForShutdown()) {
					final ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.CALLBACK_THROWS_EXCEPTION,
						new String[] { event.toString() }, th);
					sde.setMessageID("ASJ.dpl_ds.006308");
					DSLog.traceDebugThrowable(
						location,
						callbacks[i], sde,
						"ASJ.dpl_ds.000240",
						"The [{0}] threw [{1}], while processing the deploy event sent to it. For more details, check DEBUG traces of deploy service.",
						callbacks[i], th.getClass());
				}
			} finally {
				if (PropManager.getInstance().isAdditionalDebugInfo()) {
					if (deTimes == null) {
						deTimes = new StringBuilder();
					}
					deTimes.append(callbacks[i] + "="
						+ (System.currentTimeMillis() - startLocal)
						+ "ms; ");
				}
			}
		}

		if(isDebug) {
			DSLog.traceDebug(
							location,
				"The following event was sent for [{0}] ms [{1}]. [{2}]",
				new Long(System.currentTimeMillis() - startGlobal),
				deTimes,
				CAConvertor.toString(event, ""));
		}
	}

	public void deactivate() {
		for (int j = callbacks.length - 1; j >= 0; j--) {
			callbacks[j].callbackLost(
				PropManager.getInstance().getClElemName());
		}
	}

	public void serverAdded(String serverName) {
		for (int j = callbacks.length - 1; j >= 0; j--) {
			callbacks[j].serverAdded(serverName);
		}
	}

	public void handleProgressEvent(ProgressEvent event) {
		for (int i = callbacks.length - 1; i >= 0; i--) {
			try {
				callbacks[i].processContainerEvent(event);
			} catch (Exception ex) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.CALLBACK_THROWS_EXCEPTION,
						new String[] { event.toString() }, ex);
				sde.setMessageID("ASJ.dpl_ds.006308");
				DSLog.logErrorThrowable(location, sde);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public byte convertAppStatus(Status status) {
		if (Status.STOPPED.equals(status)) {
			return DeployEvent.STOPPED_STATUS;
		} else if (Status.STOPPING.equals(status)) {
			return DeployEvent.STOPPING_STATUS;
		} else if (Status.STARTED.equals(status)) {
			return DeployEvent.STARTED_STATUS;
		} else if (Status.STARTING.equals(status)) {
			return DeployEvent.STARTING_STATUS;
		} else if (Status.IMPLICIT_STOPPED.equals(status)) {
			return DeployEvent.IMPLICIT_STOPPED_STATUS;
		} else if (Status.UPGRADING.equals(status)) {
			return DeployEvent.UPGRADING_STATUS;
		} else {
			return DeployEvent.UNKNOWN_STATUS;
		}
	}
}
