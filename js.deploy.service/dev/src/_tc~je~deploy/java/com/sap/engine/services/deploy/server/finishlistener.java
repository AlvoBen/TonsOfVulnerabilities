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
package com.sap.engine.services.deploy.server;

import java.util.ArrayList;
import java.util.List;

import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.lib.util.NotSupportedException;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.application.ParallelAdapter;
import com.sap.engine.services.deploy.server.application.StartTransaction;
import com.sap.engine.services.deploy.server.application.StopTransaction;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.event.impl.DeployEventSystem;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * Executes a specific operation with a set of applications.
 * 
 * @author Anton Georgiev
 * @since 6.30
 * @version 7.10
 */
public class FinishListener implements Runnable {
	
	private static final Location location = 
		Location.getLocation(FinishListener.class);
	private final String[] appNames;
	private final boolean wait;
	private final DeployEventSystem eventSystem;
	private final DeployServiceContext ctx;
	private final String whoCausedGroupOperation;
	private final String txType;

	private List<String> errors = null;
	private List<String> warnings = null;
	private DeployEvent dEvent = null;

	public FinishListener(final String[] appNames, final boolean wait,
		final DeployEventSystem eventSystem, final DeployServiceContext ctx,
		final String whoCausedGroupOperation, final String txType) {
		this.appNames = appNames;
		this.wait = wait;
		this.eventSystem = eventSystem;
		this.ctx = ctx;
		this.whoCausedGroupOperation = whoCausedGroupOperation;
		this.txType = txType;
		if (location.beDebug()) {
			DSLog.traceDebug(
				location, 
				"An instance of [{0}] was created, where appNames = [{1}], wait = [{2}], eventSystem = [{3}], tManager = [{4}], communicator = [{5}], whoCausedGroupOperation = [{6}] and deployConstant = [{7}].",
				getClass(),
				CAConvertor.toString(appNames, ""), new Boolean(wait), 
				eventSystem, ctx.getTxManager(), ctx.getTxCommunicator(), 
				whoCausedGroupOperation, txType);
		}
	}

	public void makeOperation() {
		if (wait) {
			run();
		} else {
			final String thName = getThreadName() + "(" + 
				Thread.currentThread().getId() + ")";
			PropManager.getInstance().getThreadSystem().startThread(this, null,
					thName, true, true); // could be start or stop
		}
	}

	public void run() {
		try {
			// init
			init();
			// fire start event
			notifyForStart();
			// prepare
			ParallelAdapter[] transactions = null;
			if (DeployConstants.startApp.equals(txType)) {
				transactions = getStartTransactions();
			} else if (DeployConstants.stopApp.equals(txType)) {
				transactions = getStopTransactions();
			} else {
				throw new NotSupportedException("[ERROR CODE DPL.DS.6204] The "
						+ txType + " is not supported.");
			}
			// execute
			execute(transactions);
		} finally {
			// fire finish event
			this.notifyForCompletition();
		}
	}

	private void execute(ParallelAdapter[] transactions) {
		if (transactions == null) {
			return;
		}
		final TransactionManager txManager = ctx.getTxManager();
		for (ParallelAdapter pa : transactions) {
			// These transactions are local.
			try {
				txManager.registerTransaction(pa);
				try {
					if(pa.isNeeded()) {
						pa.makeAllPhasesLocal();
					}
				} finally {
					txManager.unregisterTransaction(pa);
				}
				if (pa.getCurrentStatistics() != null) {
					this.addWarnings(pa.getCurrentStatistics().getWarnings());
				}
			} catch (OutOfMemoryError oofme) {
				throw oofme;
			} catch (ThreadDeath td) {
				throw td;
			} catch (DeploymentException dex) {
				if (!(ctx.isMarkedForShutdown())) {
					addError("Error during " + pa.getTransactionType()
							+ " application " + pa.getModuleID() + " : "
							+ dex.toString());
					DSLog.logErrorThrowable(location, dex);
				}
			} catch (Throwable th) {
				if (!ctx.isMarkedForShutdown()) {
					addError("Error during " + pa.getTransactionType()
							+ " application " + pa.getModuleID() + " : "
							+ th.toString());
					final ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION,
							new String[] { pa.getTransactionType(),
							"[" + pa.getModuleID() + "]" }, th);
					DSLog.logErrorThrowable(location, sde);
				}
			}
		}
	}

	private void notifyForStart() {
		if (dEvent != null) {
			eventSystem.fireDeployEvent(
				dEvent, DeployConstants.APP_TYPE, null);
		}
	}

	private void notifyForCompletition() {
		if (dEvent != null) {
			dEvent.setAction(DeployEvent.LOCAL_ACTION_FINISH);
			dEvent.setErrors(listToStringArray(errors));
			dEvent.setWarnings(listToStringArray(warnings));
			eventSystem.fireDeployEvent(
				dEvent, DeployConstants.APP_TYPE, null);
		}
	}

	void addError(String error) {
		if (error == null) {
			return;
		}
		if (this.errors == null) {
			this.errors = new ArrayList<String>();
		}
		this.errors.add(error);
	}

	void addWarnings(String[] warnings) {
		if (warnings == null) {
			return;
		}
		if (this.warnings == null) {
			this.warnings = new ArrayList<String>();
		}
		for (int i = 0; i < warnings.length; i++) {
			this.warnings.add(warnings[i]);
		}
	}

	private void init() {
		byte actionType = eventSystem.defineActionType(txType);
		if (actionType != -1) {
			this.dEvent = new DeployEvent(null, DeployEvent.LOCAL_ACTION_START,
					actionType, PropManager.getInstance().getClElemName());
			dEvent.setWhoCausedGroupOperation(this.whoCausedGroupOperation);
		}
	}

	private StartTransaction[] getStartTransactions() {
		List<StartTransaction> trans = new ArrayList<StartTransaction>();
		if (appNames != null) {
			StartTransaction start = null;
			DeploymentInfo info = null;
			final TransactionCommunicator communicator =
				ctx.getTxCommunicator();
			for (int i = 0; i < appNames.length; i++) {
				try {
					info = communicator.getApplicationInfo(appNames[i]);
					if (info == null || info.isSupportingLazyStart()
							|| Status.STARTED.equals(info.getStatus())) {
						// TODO - should be removed, because rejects explicit
						// request.
						continue;
					}
					start = new StartTransaction(appNames[i], ctx, false, null);
					start.setEnqueueLockNeeded(true);
					start.setLockType(LockingConstants.MODE_SHARED);
					trans.add(start);
				} catch (DeploymentException dEx) {
					DSLog.logErrorThrowable(location, dEx);
				}
			}
		}
		final StartTransaction[] sTrans = new StartTransaction[trans.size()];
		trans.toArray(sTrans);
		return sTrans;
	}

	private StopTransaction[] getStopTransactions() {
		List<StopTransaction> trans = new ArrayList<StopTransaction>();
		if (appNames != null) {
			final TransactionCommunicator communicator =
				ctx.getTxCommunicator();
			for (int i = 0; i < appNames.length; i++) {
				try {
					final DeploymentInfo info = communicator
							.getApplicationInfo(appNames[i]);
					if (info == null || Status.STOPPED.equals(info.getStatus())
							|| Status.IMPLICIT_STOPPED.equals(info.getStatus())) {
						continue;
					}
					// parameter is false because we expect when container is
					// stopped, its applications go to status stopped
					final StopTransaction stop = 
						new StopTransaction(appNames[i], null, ctx);
					stop.setEnqueueLockNeeded(true);
					stop.setLockType(LockingConstants.MODE_SHARED);
					trans.add(stop);
				} catch (DeploymentException rex) {
					DSLog.logErrorThrowable(location, rex);
				}
			}
		}
		final StopTransaction[] sTrans = new StopTransaction[trans.size()];
		trans.toArray(sTrans);
		return sTrans;
	}

	public String[] listToStringArray(List<String> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		String[] res = new String[list.size()];
		list.toArray(res);
		return res;
	}

	private String getThreadName() {
		if (DeployConstants.startApp.equals(txType)) {
			return DeployConstants.DEPLOY_START_APP_THREAD_NAME;
		} else if (DeployConstants.stopApp.equals(txType)) {
			return DeployConstants.DEPLOY_STOP_APP_THREAD_NAME;
		} else {
			return DeployConstants.DEPLOY_THREAD_NAME;
		}
	}
}