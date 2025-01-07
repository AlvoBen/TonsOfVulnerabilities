package com.sap.engine.services.deploy.server.utils;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;
import com.sap.transaction.TransactionTicket;
import com.sap.transaction.TxException;
import com.sap.transaction.TxManager;

/**
 * Helper class to support transactional operations. All configuration handlers
 * has to be obtained via <code>openHandler()</code> method of this class.
 * 
 * @author Emil Dinchev
 */
public final class TxOperationsHelper {
	
	private static final Location location = 
		Location.getLocation(TxOperationsHelper.class);
	
	private final boolean isTxOperationSupported;

	/**
	 * The transaction ticket is lazy initialized, during openHandler()
	 * invocation. Only one transaction ticket per transaction is obtained.
	 */
	private TransactionTicket txticket;

	public TxOperationsHelper(final boolean isTxOperationSupported) {
		this.isTxOperationSupported = isTxOperationSupported;
	}

	/**
	 * Open a configuration handler and start a transaction if needed. All
	 * configuration handlers have to be obtained via this method.
	 * 
	 * @return newly opened configuration handler. Cannot be null.
	 * @throws TxException
	 * @throws ConfigurationException
	 */
	public ConfigurationHandler openHandler() throws TxException,
			ConfigurationException {

		// Start a transactional operation, if needed. New JTA transaction has
		// to be started only once per deploy transaction. Note, that here we
		// use <code>TxManager.required()</code>, and if there is an already
		// started transaction, we will join to it. The method is private - it
		// is called internally by <code>openHanler()</code>.
		if (isTxOperationSupported && txticket == null) {
			txticket = TxManager.required();
			return PropManager.getInstance()
					.getDistributedConfigurationHandlerFactory()
					.getConfigurationHandler();
		} else {
			return PropManager.getInstance().getConfigurationHandlerFactory()
					.getConfigurationHandler();
		}
	}

	/**
	 * Commits a configuration handler, if the current transaction is not marked
	 * for roll back.
	 * 
	 * @param handler
	 *            configuration handler to be commit. The operation is silently
	 *            canceled, if it is null.
	 * @throws ConfigurationException
	 */
	public void commit(final ConfigurationHandler handler)
			throws ConfigurationException {

		if (handler == null) {
			return;
		}

		if (!isTxOperationSupported && !isRollbackOnly()) {
			try {
				handler.commit();
			} finally {
				try {
					// this method must not be called if transaction deploy is
					// enabled, because will roll back all done changes, which
					// are not committed
					handler.closeAllConfigurations();
				} catch (ConfigurationException cex) {
					DSLog.logErrorThrowable(
									location, 
									"ASJ.dpl_ds.006401",
									"Error closing configurations on transaction commit",
									cex);
				}
			}
		}
	}

	/**
	 * Rolls back a configuration handler.
	 * 
	 * @param handler
	 *            configuration handler to be rolled back. The operation is
	 *            silently canceled, if it is null.
	 * @throws ConfigurationException
	 *             if the handler's roll back failed.
	 */
	public void rollback(final ConfigurationHandler handler)
			throws ConfigurationException {

		if (handler == null) {
			return;
		}

		try {
			if (!isTxOperationSupported) {
				handler.rollback();
			}
		} finally {
			try {
				// this method won't throw exception even transaction deployment
				// to be enabled and must be called always in roll back to
				// ensure that configuration locks are removed
				handler.closeAllConfigurations();
			} catch (ConfigurationException cex) {
				DSLog.logErrorThrowable(
								location, 
								"ASJ.dpl_ds.006402",
								"Exception closing configurations on rollback transaction",
								cex);
			}
		}
	}

	/**
	 * Commit transactional operation. The methods
	 * <code>commitTxOperation()</code> or <code>rollbackTxOperation()</code>
	 * has to be called by every deployment transaction, before commit phase.
	 * 
	 * @throws TxException
	 *             if the transaction's commit failed.
	 */
	public void commitTxOperation() throws TxException {
		if (txticket == null) {
			return;
		}
		try {
			TxManager.commitLevel(txticket);
		} finally {
			TxManager.leaveLevel(txticket);
			txticket = null;
		}
	}

	/**
	 * Rollback transactional operation. The methods
	 * <code>commitTxOperation()</code> or <code>rollbackTxOperation()</code>
	 * has to be called by every deployment transaction, before commit phase.
	 * 
	 * @throws TxException
	 */
	public void rollbackTxOperation() throws TxException {
		if (txticket == null) {
			return;
		}
		TxManager.leaveLevel(txticket);
		txticket = null;
	}

	private boolean isRollbackOnly() {
		try {
			return TxManager.isTxMarkedRollback();
		} catch (TxException ex) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006402",
					"Error getting status from transaction manager", ex);
		}
		return false;
	}
}