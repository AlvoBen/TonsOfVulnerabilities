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

import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Used to download application files and is executed only on one node in the
 * instance.
 *
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public class OncePerInstanceTransaction extends ApplicationTransaction {
	private static final Location location = 
		Location.getLocation(OncePerInstanceTransaction.class);
	private final boolean isDBChangedOrFSRolledBack;
	private final boolean downloadInGlobalPrepare;

	/**
	 * Global once per instance transaction.
	 * @param appName the application name.
	 * @param ctx Deploy service context.
	 * @param downloadInGlobalPrepare
	 * @param isDBChagedOrFSRolledBack
	 * @throws DeploymentException
	 */
	@SuppressWarnings("boxing")
	public OncePerInstanceTransaction(final String appName,
		final DeployServiceContext ctx, final boolean downloadInGlobalPrepare,
		final boolean isDBChangedOrFSRolledBack) throws DeploymentException {
		super(ctx);
		assert isDBChangedOrFSRolledBack || downloadInGlobalPrepare;
		setModuleID(appName);
		setTransactionType(DeployConstants.oncePerInstance);
		this.downloadInGlobalPrepare = downloadInGlobalPrepare;

		if (isDBChangedOrFSRolledBack) {
			this.isDBChangedOrFSRolledBack = isDBChangedOrFSRolledBack;
		} else if (downloadInGlobalPrepare) {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The [{0}] binaries will NOT be downloaded in [{1}] " +
					"on the remote instances, because " +
					" isDBChangedOrFSRolledBack is [{2}], " +
					"but will be downloaded on this one.",
					getModuleID(), getTransactionType(),
					isDBChangedOrFSRolledBack);
			}
			this.isDBChangedOrFSRolledBack = false;
		} else {
			// !isDBChangedOrFSRolledBack && !downloadInGlobalPrepare
			throw new IllegalArgumentException(
				"ASJ.dpl_ds.006018 The isDBChagedOrFSRolledBack is ["
				+ isDBChangedOrFSRolledBack
				+ "] and the downloadInGlobalPrepare is ["
				+ downloadInGlobalPrepare
				+ "], which is not supported.");
		}
	}

	/**
	 * Local once per instance transaction created as response to global
	 * transaction. 
	 * @param appName application name.
	 * @param ctx Deploy service context.
	 * @throws DeploymentException
	 */
	public OncePerInstanceTransaction(final String appName,
		final DeployServiceContext ctx) throws DeploymentException {
		super(ctx);
		downloadInGlobalPrepare = true;
		isDBChangedOrFSRolledBack = true;
		setModuleID(appName);
		setTransactionType(DeployConstants.oncePerInstance);
	}

	public void begin() throws DeploymentException,
		ComponentNotDeployedException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
	}

	public void beginLocal() throws DeploymentException,
		ComponentNotDeployedException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Begin local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	@SuppressWarnings("boxing")
	public void prepare() throws DeploymentException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare [{0}] of application [{1}]", getTransactionType(),
				getModuleID());
		}
		if (downloadInGlobalPrepare) {
			prepareCommon();
		} else {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"The [{0}] binaries will NOT be downloaded in [{1}], because downloadInGlobalPrepare is [{2}].",
					getModuleID(), getTransactionType(),
					downloadInGlobalPrepare);
			}
		}
	}

	public void prepareLocal() throws DeploymentException {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Prepare local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		prepareCommon();
	}

	public void prepareCommon() throws DeploymentException {
		bootstrapApplication(getModuleID());
	}

	public void commit() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit [{0}] of application [{1}]", 
				getTransactionType(), getModuleID());
		}
		commitCommon();
	}

	public void commitLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Commit local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		commitCommon();
	}

	public void commitCommon() {
		setSuccessfullyFinished(true);
	}

	public void rollback() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	public void rollbackLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	public void rollbackPrepare() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Rollback prepare local [{0}] of application [{1}]",
				getTransactionType(), getModuleID());
		}
		rollbackCommon();
	}

	public void rollbackCommon() {
		setSuccessfullyFinished(false);
	}

	@Override
	protected int[] getRemoteParticipants() {
		return isDBChangedOrFSRolledBack ?
			ctx.getClusterMonitorHelper()
				.findOneServerPerInstanceExceptCurrent() : new int[0];
	}
}