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
package com.sap.engine.deployment.operations;

import java.util.Vector;
import java.util.Iterator;
import javax.enterprise.deploy.spi.status.*;
import javax.enterprise.deploy.spi.TargetModuleID;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.Logger;
import com.sap.engine.deployment.SAPTargetModuleID;
import com.sap.engine.deployment.exceptions.SAPOperationUnsupportedException;
import com.sap.engine.deployment.exceptions.ExceptionConstants;
import com.sap.engine.deployment.status.SAPDeploymentStatus;
import com.sap.engine.deployment.proxy.DeploymentProxy;

/**
 * @author Mariela Todorova
 */
public abstract class Operation implements ProgressObject, Runnable {
	private static final Location location = Location
			.getLocation(Operation.class);
	protected DeploymentProxy proxy = null;
	protected SAPDeploymentStatus status = null;
	protected SAPTargetModuleID[] targetModules = new SAPTargetModuleID[0];
	private Vector listeners = new Vector();
	private Vector events = new Vector();

	protected Operation(DeploymentProxy dProxy) {
		Logger.trace(location, Severity.PATH, "Initiating new operation");
		this.proxy = dProxy;
	}

	public DeploymentStatus getDeploymentStatus() {
		return this.status;
	}

	public TargetModuleID[] getResultTargetModuleIDs() {
		return this.targetModules;
	}

	public ClientConfiguration getClientConfiguration(
			TargetModuleID targetModuleID) {
		return null;
	}

	public boolean isCancelSupported() {
		return false;
	}

	public void cancel() throws SAPOperationUnsupportedException {
		throw new SAPOperationUnsupportedException(location,
				ExceptionConstants.CANCEL_NOT_SUPPORTED);
	}

	public boolean isStopSupported() {
		return false;
	}

	public void stop() throws SAPOperationUnsupportedException {
		throw new SAPOperationUnsupportedException(location,
				ExceptionConstants.STOP_NOT_SUPPORTED);
	}

	public void addProgressListener(ProgressListener pol) {
		synchronized (listeners) {
			Logger.trace(location, Severity.PATH, "Adding progress listener "
					+ pol);
			listeners.add(pol);

			if (events.size() > 0) {
				Logger.trace(location, Severity.DEBUG,
						"Delivering undelivered messages");

				for (Iterator i = events.iterator(); i.hasNext();) {
					pol.handleProgressEvent((ProgressEvent) i.next());
				}
			}
		}
	}

	public void removeProgressListener(ProgressListener progressListener) {
		synchronized (listeners) {
			Logger.trace(location, Severity.PATH, "Removing progress listener "
					+ progressListener);
			listeners.remove(progressListener);
		}
	}

	protected void fireProgressEvent(ProgressEvent progressEvent) {
		Vector currentListeners = null;

		synchronized (listeners) {
			currentListeners = (Vector) listeners.clone();
			events.add(progressEvent);
		}

		for (Iterator listenersItr = currentListeners.iterator(); listenersItr
				.hasNext();) {
			Logger.trace(location, Severity.PATH, "Handling progress event "
					+ progressEvent);
			((ProgressListener) listenersItr.next())
					.handleProgressEvent(progressEvent);
		}

		currentListeners = null;
	}

}