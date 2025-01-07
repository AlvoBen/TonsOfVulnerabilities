/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy;

import com.sap.engine.services.deploy.container.ProgressEvent;

/* This class belongs to the public API of the DeployService project. */
/**
 * Interface for processing events related to Deploy service. It provides
 * methods for handling events on applications, standalone modules, services,
 * libraries, interfaces, containers, references. DeployListener processes
 * events of registering and unregistering servers to the callback system.
 * 
 * @author Rumiana Angelova
 */
public interface DeployListener {

	/**
	 * Used for processing deploy events related to applications.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processApplicationEvent(DeployEvent event);

	/**
	 * Used for processing deploy events related to services.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processServiceEvent(DeployEvent event);

	/**
	 * Used for processing deploy events related to libraries.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processLibraryEvent(DeployEvent event);

	/**
	 * Used for processing deploy events related to interfaces.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processInterfaceEvent(DeployEvent event);

	/**
	 * Used for processing deploy events related to containers.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processContainerEvent(ProgressEvent event);

	/**
	 * Used for processing deploy events related to references.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processReferenceEvent(DeployEvent event);

	/**
	 * Used for processing deploy events related to standalone modules.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processStandaloneModuleEvent(DeployEvent event);

	/**
	 * Used for notification that a server has lost the callback of this
	 * listener.
	 * 
	 * @param serverName
	 *            the name of the server lost the callback.
	 */
	public void callbackLost(String serverName);

	/**
	 * Used for notification that a server has been added to the callback of
	 * this listener.
	 * 
	 * @param serverName
	 *            the name of the server added to the callback.
	 */
	public void serverAdded(String serverName);

}
