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
package com.sap.engine.services.deploy.server;

import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * This class is used to trigger the initial start of the applications when all
 * services are started and the deploy controller is operational. There are two
 * possible scenarios:
 * <ul>
 *   <li>
 *     <tt>containerStarted</tt> event is received before 
 *     <tt>configurationChange</tt> event
 *	   <ul>
 * 		 <li>If the deploy controller is ready (i.e. the configuration does not 
 *           exists) we can trigger the initial start of the applications.</li>
 *       <li>If the deploy controller is still not ready (i.e. the 
 *           configuration exists) we have to wait until the configuration is 
 *           deleted.</li></ul></li>
 *   <li>
 *     <tt>configurationChange</tt> event is received before 
 * 	   <tt>containerStarted</tt> event 
 *     <p> We have to wait until the <tt>containerStarted</tt> event is 
 *         received.</p></li></ul>
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public class InitialStartTrigger implements ConfigurationChangedListener {
	
	private static final Location location = 
		Location.getLocation(InitialStartTrigger.class);
	
	private static final String DEPLOY_CONTROLLER_BUSY = "deploy_controller/lock/single_thread";
	private static final String CONTAINER_STARTED_EVENT_RECEIVED = "container event";
	private static final String CONFIGURATION_EVENT_RECEIVED = "configuration event";
	private static final int MAX_ATTEMPS_TO_READ_CONFIG = 3;

	private final ClusterChangeListener deploy;
	private final Object lock;
	// Flag to indicate that the containerStarted event is received. 
	private boolean isContainerStarted;
	// Flag to prevent second unregistration.
	private boolean isRegistered;
	// Flag to prevent second initial start triggering.
	private boolean isStartTriggered;

	public InitialStartTrigger(ClusterChangeListener deploy) {
		this.deploy = deploy;
		lock = new Object();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.frame.core.configuration.ConfigurationChangedListener#
	 * configurationChanged(com.sap.engine.frame.core.configuration.ChangeEvent)
	 */
	public void configurationChanged(ChangeEvent changeEvent) {
		if (filterChangeEvent(changeEvent)) {
			synchronized(lock) {
				// Deactivate the listener to spare resources.
				deactivate();
				if(isContainerStarted) {
					triggerInitialStart();				
				} else if (location.beDebug()) {
					DSLog.tracePath(location, "The cluster element is NOT ready in [{0}], because the container is NOT started.", CONFIGURATION_EVENT_RECEIVED);
				}
			}
		}
	}

	/**
	 * Here we are filtering change event to detect that the deploy controller
	 * is ready to operate. This will happen when the DEPLOY_CONTROLLER_BUSY
	 * configuration is deleted.
	 * @param changeEvent change event.
	 * @return true, if this is the change event we are interesting for.
	 */
	private boolean filterChangeEvent(ChangeEvent changeEvent) {
		final ChangeEvent[] detailedChangeEvents = 
			changeEvent.getDetailedChangeEvents();
		if (detailedChangeEvents == null || detailedChangeEvents.length == 0) {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Configuration event received without detailed changes.");
			}
			return false;
		}

		for (int i = 0; i < detailedChangeEvents.length; i++) {
			final String cfgPath = detailedChangeEvents[i].getPath();
			final int eventAction = detailedChangeEvents[i].getAction();
			if(DEPLOY_CONTROLLER_BUSY.equalsIgnoreCase(cfgPath) &&
				eventAction == ChangeEvent.ACTION_DELETED) {
				return true;
			} else if (location.beDebug()) {
				DSLog.traceDebug(location, "Received configuration event where [{0}] configuration is [{1}] changed.",
					cfgPath,
					eventAction);
			}
		}
		return false;
	}

	/**
	 * This method can be called only once by ClusterServiceAdapter, when all 
	 * AS Java services are started or timed out during the attempt to start.
	 */
	public void containerStarted() {
		synchronized(lock) {
			isContainerStarted = true;
			if(isDeployControllerReady()) {			
				deactivate();
				triggerInitialStart();
			} else {
				if(location.bePath()) {
					DSLog.tracePath(location, "The cluster element is NOT ready in [{0}], because the deploy controller is not operational.",
						CONTAINER_STARTED_EVENT_RECEIVED);
				}
			}
		}
	}

	private void triggerInitialStart() {
		if (isStartTriggered) {
			return;
		}
		isStartTriggered = true;
		if (location.bePath()) {
			DSLog.tracePath(location, "The cluster element is ready in [{0}], because the container is started and the deploy controller is operational.",
				CONTAINER_STARTED_EVENT_RECEIVED);
		}
		deploy.clusterElementReady();
	}

	/**
	 * Check whether the deploy controller is ready to operate.
	 * @return true if the controller is ready.
	 */
	private boolean isDeployControllerReady() {
		for (int attempt = 0; attempt < MAX_ATTEMPS_TO_READ_CONFIG; attempt++) {
			try {
				final ConfigurationHandler cfgHandler =	PropManager.getInstance()
					.getConfigurationHandlerFactory().getConfigurationHandler();
				try {
					cfgHandler.openConfiguration(DEPLOY_CONTROLLER_BUSY,
							ConfigurationHandler.READ_ACCESS);
				} catch (NameNotFoundException e) {
					return true;
				} finally {
					cfgHandler.closeAllConfigurations();
				}
			} catch (ConfigurationException e) {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000490",
						"The cluster element readiness check may fail.", e);
			}
		}
		return false;
	}

	/**
	 * Register the InitialStartTrigger as listener for configuration change
	 * events. Will be called only once by the deploy service.
	 * @throws ConfigurationException
	 */
	public void activate() throws ConfigurationException {
		final ConfigurationHandler cfgHandler = PropManager.getInstance()
			.getConfigurationHandlerFactory().getConfigurationHandler();
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Will register [{0}] as configuration changed listener.",
				this);
		}
		cfgHandler.addConfigurationChangedListener(this, DEPLOY_CONTROLLER_BUSY,
			ConfigurationChangedListener.MODE_ASYNCHRONOUS);
		isRegistered = true;
	}

	/**
	 * Unregister the InitialStartTrigger as listener for configuration change
	 * events.
	 */
	public void deactivate() {
		synchronized(lock) {
			if(!isRegistered) {
				return;
			}
			isRegistered = false;
			try {
				final ConfigurationHandler cfgHandler = PropManager
					.getInstance().getConfigurationHandlerFactory()
					.getConfigurationHandler();
				if (location.beDebug()) {
					DSLog.traceDebug(location, "Will UNregister [{0}] as configuration changed listener.",
						this);
				}
				cfgHandler.removeConfigurationChangedListener(
					this, DEPLOY_CONTROLLER_BUSY);
			} catch (ConfigurationException e) {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.000494",
						"The cluster element readiness check may fail.", e);
			}
		}
	}
}