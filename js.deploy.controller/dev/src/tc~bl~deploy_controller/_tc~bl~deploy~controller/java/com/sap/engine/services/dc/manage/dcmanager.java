package com.sap.engine.services.dc.manage;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.services.dc.event.GlobalListenersList;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class DCManager {
	
	private Location location = getLocation(this.getClass());

	private static final DCManager INSTANCE = new DCManager();

	/**
	 * Initially we assume that the DC is initializing. This should mean that
	 * the DC is registered to the naming but is still not fully operational
	 */
	private DCState dcState = DCState.NOT_INITIALIZED;

	private DCManager() {
	}

	public static DCManager getInstance() {
		return INSTANCE;
	}

	public DCState getDCState() {
		return this.dcState;
	}

	public synchronized void setDCState(DCState deployControllerState) {

		if (location.beInfo()) {
			tracePath(location,
					"Changing DCState [{0}] --> [{1}]", new Object[] {
							this.dcState, deployControllerState });
		}

		this.dcState = deployControllerState;

		if (DCState.WORKING.equals(this.dcState)) {
			if (location.bePath()) {
				tracePath(location, 
						"As the Deploy Controller state has been set to [{0}] notify all waiting getXXXResult Threads.",
						new Object[] { this.dcState });
			}
			this.notifyAll();
			if (location.bePath()) {
				tracePath(location, 
						"Going to register the Deploy Controller Telnet commands.");
			}
			ServiceConfigurer.getInstance().registerTelnetOnNeed(null);

			// clear the event queues in the global listener list
			GlobalListenersList.getInstance().clearEventQueues();

		} else {
			if (location.bePath()) {
				tracePath(location, 
						"Deploy controller state has been set to [{0}]",
						new Object[] { this.dcState });
			}
		}
	}

	public boolean isInWorkingMode() {
		return DCState.WORKING.equals(getDCState());
	}

	/**
	 * Calling this method will cause the current thread to wait until the
	 * DCState becomes WORKING or the designated timeout expires. Please note
	 * that after the timeout the method will return event if the state is not
	 * WORKING yet.
	 * 
	 */
	public synchronized void waitUntilWorking(long timeout) {
		if (!DCState.WORKING.equals(this.dcState)) {
			try {
				this.wait(timeout);
			} catch (InterruptedException e) {
				if (isDebugLoggable()) {
					logDebugThrowable(
							location,
							null,
							"The thread was interrupted while waiting for the DCState to become working",
							e);
				}
			}
		}
	}
}
