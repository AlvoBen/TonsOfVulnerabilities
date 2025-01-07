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
package com.sap.engine.services.deploy.server.application;

import java.util.Dictionary;
import java.util.Properties;

import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated - rolling
 */
@Deprecated
public class UpdateWithSyncTransaction extends UpdateTransaction {
	private static final Location location = 
		Location.getLocation(UpdateWithSyncTransaction.class);

	private final boolean needStop = true;
	// TODO - to be sent from the instance,
	// where RollingPatch.updateInstanceAndDB had been invoked.

	/**
	 * Global update with synchronization transaction.
	 * @param appName
	 * @param ctx
	 * @param containerProps
	 * @param containerNames
	 * @param isGlobal not used.
	 * @throws DeploymentException
	 */
	public UpdateWithSyncTransaction(final String appName,
		final DeployServiceContext ctx, 
		final Dictionary<String, Properties> containerProps,
		final String[] containerNames, final boolean isGlobal)
		throws DeploymentException {
		this(appName, ctx, containerProps, containerNames);
	}

	/**
	 * Local update with synchronization transaction.
	 * @param appName
	 * @param ctx
	 * @param containerProps
	 * @param containerNames
	 * @throws DeploymentException
	 */
	public UpdateWithSyncTransaction(final String appName,
		final DeployServiceContext ctx, 
		final Dictionary<String, Properties> containerProps,
		final String[] containerNames) throws DeploymentException {
		super(appName, ctx, containerProps, containerNames);
		setLockType(LockingConstants.MODE_SHARED);
		setTransactionType(DeployConstants.updateWithSync);
	}

	@Override
	public void begin() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		oldStatus = oldDeployment.getStatus();
		oldStatusDesc = oldDeployment.getStatusDescription();
		needStopApplicationPhase();
		super.l_beginLocal();
	}

	@Override
	protected int[] getRemoteParticipants() {
		return ctx.getClusterMonitorHelper()
			.findOtherServersInCurrentInstance();
	}

	private void needStopApplicationPhase() throws DeploymentException {
		if (needStop) {
			// set it before starting the transaction, to be able to set
			// the correct status in the rollBack part.
			needStartAfterFinish = true;
			makeNestedParallelTransaction(new StopTransaction(
				getModuleID(), ctx, 
				ctx.getClusterMonitorHelper().findServers()));
			return;
		}
		needOncePerInstanceAfterFinish = false;
		// the application will be started during the update.
	}

	@Override
	public void prepare() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare [{0}] for application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	@Override
	public void commit() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		super.l_commitLocal();
	}

	@Override
	protected void oncePerInstanceTransaction(boolean isSuccessfullyFinished,
		boolean downloadInGlobalPrepare) throws DeploymentException {
		// Note: false - sync in instance only
		super.oncePerInstanceTransaction(false, downloadInGlobalPrepare);
	}

}
