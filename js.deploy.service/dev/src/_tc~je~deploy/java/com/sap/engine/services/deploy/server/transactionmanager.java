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

package com.sap.engine.services.deploy.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.application.ApplicationTransaction;
import com.sap.engine.services.deploy.server.application.ParallelAdapter;
import com.sap.engine.services.deploy.server.application.StartInitiallyTransaction;
import com.sap.engine.services.deploy.server.event.impl.DeployEventSystem;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.engine.services.deploy.server.utils.LockUtils;
import com.sap.engine.services.deploy.server.utils.StringUtils;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.tc.logging.Location;

/**
 * Class to manage active transactions. Every transaction has to be registered
 * before its execution, and unregistered after that. During the registration
 * some local and cluster-wide locks are acquired. Registered transactions are
 * tracked to be possible to notify them about the operation result on the
 * remote nodes.
 * 
 * @author Anton Georgiev
 */
public final class TransactionManager {
	
	private static final Location location = 
		Location.getLocation(TransactionManager.class);

	private final static int CLUSTER_LOCK_TIMEOUT = 120000;
	private final static int MAX_ATTEMPS = 5;

	private final Map<String, DTransaction> txTracker;
	private final DeployEventSystem eventSystem;

	/**
	 * @param eventSystem
	 *            deploy remote system.
	 * @param remote
	 *            remote caller used to communicate with other nodes.
	 */
	public TransactionManager(final DeployEventSystem eventSystem) {
		this.eventSystem = eventSystem;
		txTracker = Collections.synchronizedMap(
			new HashMap<String, DTransaction>());
	}

	/**
	 * <p>Method to register active transactions. Every transaction has to be
	 * registered before its execution, and unregistered after that. After every
	 * successful call to registerTransaction() we have to call
	 * unregisterTransaction().</p>
	 * <p>During the registration some local and cluster-wide locks are 
	 * acquired and the transaction is registered in order to be notified for 
	 * the progress of operation on the remote nodes.</p>
	 * @param tx active transaction.
	 * @throws DeploymentException if the registration failed.
	 */
	public void registerTransaction(DTransaction tx)
		throws DeploymentException {
		if (location.beDebug()) {
			DSLog.traceDebug(
				location, 
				"Registering transaction [{0}] for application [{1}].",
				tx.getTransactionType(), tx.getModuleID());
		}
		lock(tx);
		trackTransaction(tx);
	}

	/**
	 * <p>
	 * Method to unregister executed transactions. Every successfully registered
	 * transaction has to be unregistered after its execution in order to
	 * release the acquired local and cluster-wide locks.
	 * 
	 * @param tx
	 *            executed transaction.
	 * @throws ServerDeploymentException
	 */
	public void unregisterTransaction(DTransaction tx)
		throws ServerDeploymentException {
		if (location.beDebug()) {
			DSLog.traceDebug(
				location, 
				"Unregistering transaction [{0}] for application [{1}].",
				tx.getTransactionType(),
				tx.getModuleID());
		}
		try {
			untrackTransaction(tx);
		} finally {
			// The transaction locks have to be released at last and always.
			tx.unlock();
		}
	}

	/**
	 * Locks application for the specified operation with the given lock type.
	 * 
	 * @param appName
	 *            the application to be locked.
	 * @param transactionType
	 *            transaction for some operation.
	 * @param lockType
	 *            lock type.
	 * @param retry
	 *            if <tt>true</tt> retries to lock the application in case of
	 *            failure, otherwise - throws an exception after the first
	 *            attempt.
	 * @throws DeploymentException
	 *             if application can not be locked.
	 */
	@SuppressWarnings("boxing")
	public void lockApplication(final String appName, final String txType,
		final char lockType, final boolean retry)
		throws DeploymentException {
		if (location.beDebug()) {
			DSLog.traceDebug(location, "Locking transaction [{0}] " +
				"for application [{1}] with type [{2}].",
				txType, appName, lockType);
		}
		for (int i = 0; i < MAX_ATTEMPS; i++) {
			try {
				LockUtils.lockAndWait(appName, lockType, CLUSTER_LOCK_TIMEOUT);
				return;
			} catch (LockException lex) {
				if (!retry || i == MAX_ATTEMPS - 1) {
					throw new ServerDeploymentException(
							ExceptionConstants.ALREADY_STARTED_OPERATION,
							new String[] { appName }, lex);
				}
			} catch (TechnicalLockException tlex) {
				throw new ServerDeploymentException(
					ExceptionConstants.CANNOT_LOCK_BECAUSE_OF_TECHNICAL_PROBLEMS,
					new String[] { appName, tlex.getMessage() }, tlex);
			}
			
		}
	}

	private void lock(DTransaction tx)
		throws ServerDeploymentException {
		DSLog.tracePath(location, "TransactionManager.lock({0}) called.", tx);
		try {
			tx.lock();
		} catch (LockSetNotAcquiredException ex) {
			throw new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "registering [" + tx.getTransactionType()
						+ "] of [" + tx.getModuleID() + "]."
						+ CAConstants.EOL + "Reason: " + ex.toString() },
				ex);
		} catch (ConflictingOperationLockException ex) {
			throw new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "registering [" + tx.getTransactionType()
					+ "] of [" + tx.getModuleID() + "]."
					+ CAConstants.EOL + "Reason: " + ex.toString() },
				ex);
		} catch (InterruptedException ex) {
			throw new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "registering [" + tx.getTransactionType()
					+ "] of [" + tx.getModuleID() + "]."
					+ CAConstants.EOL + "Reason: " + ex.toString() },
				ex);
		}
	}

	private String getTransactionId(final String appName, 
		final String transType) {
		final StringBuilder sb = new StringBuilder(appName);
		sb.append(":").append(transType);
		return sb.toString();
	}

	/**
	 * Registers transaction for tracking purposes.
	 * 
	 * @param tx the transaction to be tracked.
	 */
	private void trackTransaction(final DTransaction tx) {
		txTracker.put(getTransactionId(tx.getModuleID(), 
			tx.getTransactionType()), tx);
		dumpTrackingInfo(tx, "Register");
		tx.setBeginTime(System.currentTimeMillis());
		fireDeployEventForTransactionStart(tx);
	}

	private void fireDeployEventForTransactionStart(DTransaction tx) {
		if (tx.isEnqueueLockNeeded() &&
			tx.getLockType() == LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE) {
			eventSystem.fireDeployEvent(tx.getModuleID(), tx.getModuleType(),
				DeployEvent.ACTION_START, tx.getTransactionType(), "",
				null, null, tx.getSoftwareType());
		} else {
			eventSystem.fireDeployEvent(tx.getModuleID(), tx.getModuleType(),
				DeployEvent.LOCAL_ACTION_START, tx.getTransactionType(),
				"", null, null, tx.getSoftwareType());
		}
	}

	private void fireDeployEventForTransactionFinish(DTransaction tx) {
		final String[][] msg = DUtils.getWarningsAndErrors(tx);
		if (tx.isEnqueueLockNeeded() &&
			tx.getLockType() == LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE) {
			eventSystem.fireDeployEvent(tx.getModuleID(), tx.getModuleType(),
				DeployEvent.ACTION_FINISH, tx.getTransactionType(), "",
				msg[1], msg[0], tx.getSoftwareType());
		} else {
			eventSystem.fireDeployEvent(tx.getModuleID(), tx.getModuleType(),
				DeployEvent.LOCAL_ACTION_FINISH, tx.getTransactionType(),
				"", msg[1], msg[0], tx.getSoftwareType());
		}
	}

	/**
	 * Unregisters transaction registered for tracking purposes.
	 * 
	 * @param tx
	 *            the transaction.
	 * @throws ServerDeploymentException
	 */
	private void untrackTransaction(DTransaction tx)
		throws ServerDeploymentException {
		if (tx instanceof ApplicationTransaction) {
			ApplicationTransaction appTrans = (ApplicationTransaction) tx;
			handleNotNullableCfgHandler(appTrans);
		}
		txTracker.remove(
			getTransactionId(tx.getModuleID(), tx.getTransactionType()));
		dumpTrackingInfo(tx, "Unregister");
		printStatistics(tx);
		fireDeployEventForTransactionFinish(tx);
	}

	private String getDumpHeader(final String operation, final DTransaction tx) {
		StringBuilder sb = new StringBuilder(operation);
		sb.append(" (with");
		if (!tx.isEnqueueLockNeeded()) {
			sb.append("out");
		}
		sb.append(" lock ");
		if (tx.isEnqueueLockNeeded()) {
			sb.append(tx.getLockType());
		}
		sb.append(")");
		return StringUtils.intern(sb.toString());
	}

	private void handleNotNullableCfgHandler(ApplicationTransaction appTrans) {
		if (appTrans.getHandler() != null) {
			if (appTrans instanceof StartInitiallyTransaction) {
				DSLog.traceError(
					location, 
					"ASJ.dpl_ds.002036",
					"General error in [{0}] of [{1}]. Its configuration handler must be null.",
					appTrans.getTransactionType(), 
					appTrans.getModuleID());
				return;
			}
			if (location.beWarning()) {
				DSLog.traceWarning(
					location, 
					"ASJ.dpl_ds.003019",
					"The configuration handler of transaction [{0}] of [{1}] is not NULL, so it will be rolled back and all its configurations will be closed.",
					appTrans.getTransactionType(), appTrans.getModuleID());
			}
			try {
				appTrans.getHandler().rollback();
			} catch (ConfigurationException cex) {
				final ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "unregistering ["
						+ appTrans.getTransactionType() + "] of ["
						+ appTrans.getModuleID() + "] with lock."
						+ CAConstants.EOL + "Reason: " + cex.toString() },
						cex);
				DSLog.logErrorThrowable(location, sde);
			} finally {
				try {
					appTrans.getHandler().closeAllConfigurations();
				} catch (ConfigurationException cex) {
					final ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
							new String[] { "unregistering ["
									+ appTrans.getTransactionType() + "] of ["
									+ appTrans.getModuleID() + "] with lock."
									+ CAConstants.EOL + "Reason: "
									+ cex.toString() }, cex);
					DSLog.logErrorThrowable(location, sde);
				}
			}
		}
	}

	private void printStatistics(DTransaction transaction) {
		final TransactionStatistics[] stat = transaction.getStatistics();
		if (stat != null) {
			for (int i = 0; i < stat.length; i++) {
				if (stat[i] != null) {
					if (stat[i].getClusterID() == PropManager.getInstance()
							.getClElemID()) {
						if (transaction.isSuccessfullyFinished()) {
							logGlobal4Success(transaction, stat[i]);
						} else {
							logGlobal4NotSuccess(transaction, stat[i]);
						}
					} else {
						if (stat[i].isOkResult()) {
							logLocal4Success(transaction, stat[i]);
						} else {
							logLocal4NotSuccess(transaction, stat[i]);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("boxing")
	private void logGlobal4Success(DTransaction tx,
		TransactionStatistics stat) {
		final long time = System.currentTimeMillis() - tx.getBeginTime();
		final boolean isWarning = (stat.getWarnings() != null && stat
				.getWarnings().length > 0);
		if (!isWarning) {
			log4Success(
				"ASJ.dpl_ds.000554",
				"Global operation [{0}] over [{1}] [{2}] finished successfully on server [{3}].",
				tx, isWarning, tx.getTransactionType(),
				tx.getModuleTypeAsString(), tx.getModuleID(),
				stat.getClusterID());
		} else {
			log4Success(
					"ASJ.dpl_ds.0005540",
					"Global operation [{0}] over [{1}] [{2}] finished with warnings on server [{3}]. The warnings are not critical for the operation execution.",
					tx, isWarning, tx.getTransactionType(),
					tx.getModuleTypeAsString(), tx.getModuleID(),
					stat.getClusterID());
			DSLog.traceWarningWithFaultyDcName(
					location,
					tx.getModuleID(), "ASJ.dpl_ds.0005541",
					"Global operation [{0}] over [{1}] [{2}] finished with warnings for [{3}] ms on server [{4}]. The following warnings were collected: [{5}].",
					tx.getTransactionType(),
					tx.getModuleTypeAsString(), tx.getModuleID(),
					time, stat.getClusterID(), getWarnings(stat));
		}
	}

	@SuppressWarnings("boxing")
	private void logLocal4Success(DTransaction tx,
			TransactionStatistics stat) {
		final long time = System.currentTimeMillis() - tx.getBeginTime();
		final boolean isWarning = (stat.getWarnings() != null && stat
				.getWarnings().length > 0);
		if (!isWarning) {
			log4Success(
				"ASJ.dpl_ds.000555",
				"Local operation [{0}] over [{1}] [{2}] finished successfully on server [{3}].",
				tx, isWarning, tx.getTransactionType(),
				tx.getModuleTypeAsString(), tx.getModuleID(),
				stat.getClusterID());
		} else {
			log4Success(
					"ASJ.dpl_ds.0005550",
					"Local operation [{0}] over [{1}] [{2}] finished with warnings on server [{3}]. The warnings are not critical for the operation execution.",
					tx, isWarning, tx.getTransactionType(),
					tx.getModuleTypeAsString(), tx.getModuleID(),
					stat.getClusterID());
			DSLog.traceWarningWithFaultyDcName(
					location,
					tx.getModuleID(), "ASJ.dpl_ds.0005551",
					"Local operation [{0}] over [{1}] [{2}] finished with warnings for [{3}] ms on server [{4}]. The following warnings were collected: [{5}].", 
					 tx.getTransactionType(),
					 tx.getModuleTypeAsString(), tx.getModuleID(),
					time, stat.getClusterID(), getWarnings(stat));
		}
	}

	@SuppressWarnings("boxing")
	private void logGlobal4NotSuccess(DTransaction tx,
		TransactionStatistics stat) {
		final long time = System.currentTimeMillis() - tx.getBeginTime();
		log4NotSuccess(
			"ASJ.dpl_ds.002038",
			"Global operation [{0}] over [{1}] [{2}] failed with errors on server [{3}].",
			tx, tx.getTransactionType(),
			tx.getModuleTypeAsString(), tx.getModuleID(),
			stat.getClusterID());
		DSLog.traceErrorWithFaultyDcName(
				location,
				tx.getModuleID(), "ASJ.dpl_ds.0020380",
				"Global operation [{0}] over [{1}] [{2}] finished with errors for [{3}] ms on server [{4}] [{5}] [{6}].",
				tx.getTransactionType(),
				tx.getModuleTypeAsString(), tx.getModuleID(),
				time, stat.getClusterID(), getErrors(stat), getWarnings(stat));
	}

	@SuppressWarnings("boxing")
	private void logLocal4NotSuccess(DTransaction tx,
		TransactionStatistics stat) {
		final long time = System.currentTimeMillis() - tx.getBeginTime();
		log4NotSuccess(
			"ASJ.dpl_ds.002039",
			"Local operation [{0}] over [{1}] [{2}] failed with errors on server [{3}].",
			tx, tx.getTransactionType(),
			tx.getModuleTypeAsString(), tx.getModuleID(),
			stat.getClusterID());
		DSLog.traceErrorWithFaultyDcName(
				location,
				tx.getModuleID(), "ASJ.dpl_ds.0020390",
				"Local operation [{0}] over [{1}] [{2}] finished with errors for [{3}] ms on server [{4}] [{5}] [{6}].",
				tx.getTransactionType(),
				tx.getModuleTypeAsString(), tx.getModuleID(),
				time, stat.getClusterID(), getErrors(stat), getWarnings(stat));
	}

	private String getWarnings(TransactionStatistics stat) {
		return toString(stat.getWarnings(), "Warnings");
	}

	private String getErrors(TransactionStatistics stat) {
		return toString(stat.getErrors(), "Errors");
	}

	private static String toString(String[] source, String type) {
		if (source == null || source.length == 0) {
			return toStringByType("without " + type.toLowerCase());
		}
		final StringBuilder sb = new StringBuilder(toStringByType(type));
		for (int i = 0; i < source.length; i++) {
			sb.append(CAConstants.EOL).append(DSConstants.TAB).append(i + 1)
					.append("). ").append(source[i]);
		}
		return sb.toString();
	}

	private static String toStringByType(String type) {
		return CAConstants.EOL + " >>> " + type + " <<<";
	}

	private void log4Success(String messageID, String message,
			DTransaction transaction, boolean isWarning,
			Object... args) {
		// Deploy
		if (isWarning) {
			DSLog.logWarningWithFaultyDcName(location, transaction.getModuleID(),
				messageID, message, args);
		} else {
			if (DSLog.isPathTraceable()) {
				DSLog.tracePath(location, message, args);
			}
		}
	}

	private void log4NotSuccess(String messageID, String message,
		DTransaction transaction, Object... args) {
		// Deploy
		DSLog.logErrorWithFaultyDcName(
			location, messageID, message, transaction.getModuleID(), args);
	}

	/**
	 * Notifies that a cluster element disconnected from the cluster.
	 * 
	 * @param element
	 *            the disconnected element.
	 */
	public void elementLoss(ClusterElement element) {
		final int serverId = element.getClusterId();
		for (DTransaction tx : txTracker.values()) {
			if (tx instanceof ParallelAdapter) {
				((ParallelAdapter) tx).serverFinished(
					serverId, null, new String[] {
					"Cluster element " + serverId +	" was suddenly dropped."});
			}
		}
	}

	/**
	 * Returns the transaction registered for an application.
	 * 
	 * @param appName
	 *            application name.
	 * @param transType
	 *            the transaction type.
	 * @return transaction.
	 */
	public DTransaction getTransaction(String appName, String transType) {
		final String id = getTransactionId(appName, transType);
		DTransaction trans = txTracker.get(id);
		if (trans == null) {
			DSLog.traceError(
				location, 
				"ASJ.dpl_ds.002045",
				"Was not able to find trackable or UNtrackable transaction with application [{0}] and transaction type [{1}], will return NULL.",
				appName, transType);
		}
		return trans;
	}

	public void forcedUnregisterTransactionWithoutLock(String appName)
		throws ServerDeploymentException {
		DSLog.traceWarning(
			location, 
			"ASJ.dpl_ds.003020",
			"Will start forced unregistration of the transactions for [{0}] without lock.",
			appName);

		DTransaction untrTx = null;
		DTransaction trTx = null;
		boolean isOk = false;
		try {
			unregisterTransactions(appName);
			isOk = true;
		} finally {
			if (location.beDebug()) {
				DSLog.traceDebug(
					location, 
					"After executing forced unregistration of the transactions for [{0}] without locking, the [{1}] and [{2}] were unregistered [{3}].",
					appName,
					untrTx, trTx, (isOk ? "successfully"
										: "UNsuccessfully"));
			}
		}
	}

	private void dumpTrackingInfo(final DTransaction tx, final String operation) {
		if (location.beDebug()) {
			DSLog.traceDebug(
				location, 
				"[{0}] transaction [{1}] for application [{2}] {3}trackedTransactions: [{4}]",
				getDumpHeader(operation, tx),
				tx.getTransactionType(), tx.getModuleID(), 
				CAConstants.EOL, txTracker);
		}
	}

	private void unregisterTransactions(final String appName)
		throws ServerDeploymentException {
		final Collection<DTransaction> selected = new ArrayList<DTransaction>();
		for (String txId : txTracker.keySet()) {
			if (txId.startsWith(appName)) {
				selected.add(txTracker.get(txId));
			}
		}
		for (DTransaction tx : selected) {
			tx.setEnqueueLockNeeded(false);
			unregisterTransaction(tx);
		}
	}
}