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

import java.util.ArrayList;

import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.services.deploy.container.ProgressEvent;

/**
 * Class providing implementation for DeployCallback interface. It is used for
 * processing events related to deploy service and provides methods for handling
 * events on applications, standalone modules, services, libraries, interfaces,
 * containers, references. It processes events of registering and unregistering
 * servers to the callback system. Deploy callback system is used as a manager
 * of all registered deploy listeners.
 * 
 * @author Rumiana Angelova
 */
public final class DeployCallbackImpl extends P4RemoteObject implements
		DeployCallback {

	private final ArrayList<DeployListener> listeners = new ArrayList<DeployListener>(
			1);

	// TODO:
	/* non-java doc see ... ->for all */
	public void processApplicationEvent(DeployEvent event) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).processApplicationEvent(event);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

	/**
	 * Used for processing deploy events related to libraries.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processLibraryEvent(DeployEvent event) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).processLibraryEvent(event);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

	/**
	 * Used for processing deploy events related to interfaces.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processInterfaceEvent(DeployEvent event) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).processInterfaceEvent(event);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

	/**
	 * Used for processing deploy events related to services.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processServiceEvent(DeployEvent event) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).processServiceEvent(event);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

	/**
	 * Used for processing deploy events related to containers.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processContainerEvent(ProgressEvent event) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).processContainerEvent(event);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

	/**
	 * Used for processing deploy events related to references.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processReferenceEvent(DeployEvent event) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).processReferenceEvent(event);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

	/**
	 * Used for processing deploy events related to standalone modules.
	 * 
	 * @param event
	 *            the event to be processed.
	 */
	public void processStandaloneModuleEvent(DeployEvent event) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).processStandaloneModuleEvent(event);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

	/**
	 * Adds deploy listener to the callback system.
	 * 
	 * @param listener
	 *            the listener to be managed by this callback system.
	 */
	public void addDeployListener(DeployListener listener) {
		if (listener == null) {
			return;
		}
		if (found(listener) != -1) {
			return;
		}

		listeners.add(listener);
	}

	private int found(DeployListener l) {
		for (int j = listeners.size(); j > 0; j--) {
			if (l == listeners.get(j - 1)) {
				return j - 1;
			}
		}
		return -1;
	}

	/**
	 * Removes deploy listener from the callback system.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeDeployListener(DeployListener listener) {
		int index = found(listener);
		if (index == -1) {
			return;
		}
		listeners.remove(listener);
	}

	/**
	 * Returns a list of deploy listeners managed by this callback system.
	 * 
	 * @return array of deploy listeners contained in the callback.
	 */
	public ArrayList<DeployListener> getDeployListeners() {
		return listeners;
	}

	/**
	 * Used for notification that a server has lost this callback.
	 * 
	 * @param serverName
	 *            the name of the server lost the callback.
	 */
	public void callbackLost(String serverName) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).callbackLost(serverName);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

	/**
	 * Used for notification that a server has been added to this callback.
	 * 
	 * @param serverName
	 *            the name of the server added to the callback.
	 */
	public void serverAdded(String serverName) {
		for (int j = listeners.size() - 1; j >= 0; j--) {
			try {
				listeners.get(j).serverAdded(serverName);
			} catch (Exception ex) {
				listeners.remove(j);
				// TODO: 1. no logging for client. 2.JLin exclude - to check
				// what is it for this case
			}
		}
	}

}
