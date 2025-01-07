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
package com.sap.engine.services.dc.frame;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.Properties;

import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.utils.EngineThreadUtil;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.CfgUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * @author
 * @author Boris Savov(i030791)
 * @version 7.0
 */
public class ServerContainerEventListener extends ContainerEventListenerAdapter {
	
	private Location location = DCLog.getLocation(this.getClass());

	public static final String SECURITY_SERVICE_NAME = "security";
	public static final String LOG_INTERFACE_NAME = "log";
	public static final String SHELL_INTERFACE_NAME = "shell";

	public ServerContainerEventListener() {
	}

	public void serviceStarted(String serviceName, Object serviceInterface) {
		if (location.bePath()) {
			tracePath(location, 
					"The service [{0}] was notified that [{1}] service was started and its interface is [{2}]",
					new Object[] { CM.SERVICE_NAME, serviceName,
							serviceInterface });
		}
		if (serviceName.equals(SECURITY_SERVICE_NAME)) {
			ServiceConfigurer.getInstance().setSecurityContext(
					(SecurityContext) serviceInterface);
		}
	}

	public void serviceStopped(String serviceName) {
		if (location.bePath()) {
			tracePath(location, 
					"The service [{0}] was notified that [{1}] service was stopped.",
					new Object[] { CM.SERVICE_NAME, serviceName });
		}
		if (serviceName.equals(SECURITY_SERVICE_NAME)) {
			ServiceConfigurer.getInstance().setSecurityContext(null);
		}
	}

	public boolean setServiceProperty(String key, String value) {
		return false;
	}

	public boolean setServiceProperties(Properties serviceProperties) {
		return false;
	}

	// //////////////////////////////////////////////// former
	// AdminContainerEventHandler stuff

	public void containerStarted() {

		if (location.bePath()) {
			tracePath(location, 
					"ServerContainerEventListener.containerStarted() ...");
		}

		// this logic shouldn't be executed in the event dispatching thread
		// because it
		// has a timeout of several minutes
		final Runnable runnable = new Runnable() {
			public void run() {
				try {
					final ServiceStateProcessor stateProcessor = ServiceStateProcessor
							.getInstance();
					try {
						stateProcessor.notifyMainRollingNode();
					} catch (ServiceStateProcessingException spe) {
						DCLog.logErrorThrowable(location, spe);
					}
					try {
						stateProcessor.containerStarted();
					} catch (ServiceStateProcessingException spe) {
						DCLog.logErrorThrowable(location, spe);
					}
				} catch (OutOfMemoryError e) { // OOM, ThreadDeath and Internal
					// error are not consumed
					throw e;
				} catch (ThreadDeath e) {
					throw e;
				} catch (InternalError e) {
					throw e;
				} catch (RuntimeException e) {
					// During safety workflow the server will be restarted after
					// the deployment is done and when the
					// thread is interrupted a RuntimeException will be thrown
					// in order to release the thread. If the
					// DCManager is in state RESTRARTING this RuntimeException
					// should not be treated as an error.
					if (!DCManager.getInstance().getDCState().equals(
							DCState.RESTARTING)) {
						DCLog
								.logErrorThrowable(location, 
										null,
										"Unexpected problem occured while processign the container started event",
										e);
					} else {
						if (isDebugLoggable()) {
							final String message = "Caught a runtime exception while in state RESTARTING. Assuming a clean exit of the thread.";
							logDebugThrowable(location, null, message, e);
						}
					}

				} catch (Throwable t) {
					DCLog
							.logErrorThrowable(location, 
									null,
									"Unexpected problem occured while processign the container started event",
									t);
				}
			}
		};

		final String taskName = "[Deploy Controller] - handle event 'container started' ...";
		final String threadName = "'Container Started' Event Handler Thread";

		EngineThreadUtil.executeThreadAsync(runnable, taskName, threadName,
				Boolean.FALSE);

	}

	public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
		if (LOG_INTERFACE_NAME.equals(interfaceName)) {
			DCLog.initLogging(ServiceConfigurer.getInstance()
					.getClusterMonitor().getCurrentParticipant().getName());
		} else if (SHELL_INTERFACE_NAME.equals(interfaceName)) {
			if (interfaceImpl instanceof ShellInterface) {
				ServiceConfigurer.getInstance().registerTelnetOnNeed(
						(ShellInterface) interfaceImpl);
			}
		}
	}

}
