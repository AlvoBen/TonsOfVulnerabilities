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
package com.sap.engine.services.dc.manage.handle.impl;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.services.dc.frame.ServerContainerEventListener;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.manage.handle.HandleManager;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class HandleManagerImpl extends HandleManager {
	
	private Location location = DCLog.getLocation(this.getClass());

	// private final ShellEventHandler shellEventHandler =
	// ShellEventHandler.getInstance();
	private final MessageHandler messageHandler = MessageHandler.getInstance();
	private final ServerContainerEventListener containerEventListener = new ServerContainerEventListener();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.manage.handle.HandleManager#registerHandlers()
	 */
	public void registerHandlers(final ApplicationServiceContext appServiceCtx)
			throws HandleManagerException {
		int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE
				| ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE
				| ContainerEventListener.MASK_SERVICE_STARTED
				| ContainerEventListener.MASK_SERVICE_STOPPED
				| ContainerEventListener.MASK_CONTAINER_STARTED;
		// | ContainerEventListener.MASK_BEGIN_CONTAINER_STOP;
		// A short term solution in order to resolve CSN 2783951/2006
		// In one particular case this event can't be processed correctly and
		// leads to a deadlock with 30 min. timeout
		// The current implementation just does some debug level logging that
		// could be skipped without significant impact on functionality.
		// The problematic workflow can be described as follows:
		// 1. When the container is restarted for the first time, during
		// deployment with workflow=safety, in response to the CONTAINER_STARTED
		// event
		// the ServerContainerEventListener will call some logic in order to
		// deploy the online items and
		// then will try to restart the server once again. To do that it will
		// call JStart and tell it to restart.
		//    
		// 2. JStart will trigger the restart and the BEGIN_CONTAINER_STOP event
		// will be dispatched to all the services that have subscribed for it.
		// However the processing of the previous event CONTAINER_STARTED is
		// still not done because of the ensureServerDown method,
		// which waits for the server to stop (it has a timeout of 30 minutes).
		// Thus the new BEGIN_CONTAINER_STOP event will stay in the event queue
		// of the DCService, preventing the server from stoping for 30 min.

		// 3. The BEGIN_CONTAINER_STOP event does not have the usual 20 sec
		// timeout because sometimes stopping of the applications takes a lot of
		// time
		// Because of that the server will not be stopped until the loop in the
		// ensureServerDown times out ( 30 min)
		// See CSN 2783951/2006

		Set names = new HashSet(3);
		names.add(ServerContainerEventListener.SECURITY_SERVICE_NAME);
		names.add(ServerContainerEventListener.LOG_INTERFACE_NAME);
		names.add(ServerContainerEventListener.SHELL_INTERFACE_NAME);
		appServiceCtx.getServiceState().registerContainerEventListener(mask,
				names, this.containerEventListener);

		try {
			appServiceCtx.getClusterContext().getMessageContext()
					.registerListener(this.messageHandler);
		} catch (ListenerAlreadyRegisteredException lare) {
			final String errMsg = "ASJ.dpl_dc.003327 An error occurred while registering the Deploy Controller's "
					+ "MessageHandler.";
			DCLog.logErrorThrowable(location, null, errMsg, lare);
			throw new HandleManagerException(errMsg, lare);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.manage.handle.HandleManager#unregisterHandlers
	 * ()
	 */
	public void unregisterHandlers() {
		final ApplicationServiceContext appServiceCtx = ServiceConfigurer
				.getInstance().getApplicationServiceContext();

		appServiceCtx.getClusterContext().getMessageContext()
				.unregisterListener();
		appServiceCtx.getServiceState().unregisterContainerEventListener();
	}
}
