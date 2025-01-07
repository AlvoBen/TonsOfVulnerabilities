package com.sap.engine.services.deploy.server;

import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;

/**
 * Common interface for all deployment transactions. Every deployment
 * transaction is executed in the thread which creates it except start and stop
 * transactions, which sometimes are executed in separated thread. No new
 * threads are created during the transaction execution.
 * 
 * @author Rumiana Angelova
 * @version 6.25
 */
public interface DTransaction {

	public String getModuleID();

	/**
	 * Returns the component affected by the transaction.
	 */
	public Component getComponent();

	public String getSoftwareType();

	public byte getModuleType();

	public String getModuleTypeAsString();

	public String getTransactionType();

	/**
	 * This method is called internally by makeAllPhases() to mark the start of
	 * transaction. If the method fails the transaction will be rolled back via
	 * call to rollback() method.
	 * 
	 * @throws DeploymentException
	 * @throws ComponentNotDeployedException
	 */
	public void begin() throws DeploymentException,
			ComponentNotDeployedException;

	/**
	 * This method is called internally by makeAllPhasesLocal() to mark the
	 * start of transaction. If the method fails the transaction will be rolled
	 * back via call to rollbackLocal() method.
	 * 
	 * @throws DeploymentException
	 * @throws ComponentNotDeployedException
	 */
	public void beginLocal() throws DeploymentException,
			ComponentNotDeployedException;

	/**
	 * This method is called internally by makeAllPhases() to prepare the
	 * transaction. If the method fails the transaction will be rolled back via
	 * call to rollbackPrepare() method. All open handlers have to be committed
	 * at the end of this method.
	 */
	public void prepare() throws DeploymentException;

	/**
	 * This method is called internally by makeAllPhasesLocal() to prepare the
	 * transaction. If the method failed the transaction will be rolled back via
	 * call to rollbackPrepareLocal() method. All open handlers have to be
	 * committed at the end of this method.
	 */
	public void prepareLocal() throws DeploymentException;

	/**
	 * This method is called internally by makeAllPhases() to commit the
	 * transaction. The commit of all handlers has to be already done. It is not
	 * allowed to open new handlers or to commit handlers here.
	 */
	public void commit();

	/**
	 * This method is called internally by makeAllPhasesLocal() to commit the
	 * transaction. The commit of all handlers has to be already done. It is not
	 * allowed to open new handlers or to commit handlers here.
	 */
	public void commitLocal();

	/**
	 * This method is called internally by makeAllPhases() to rollback the
	 * transaction if there is an exception in begin phase. All open handlers
	 * have to be rolled back here.
	 */
	public void rollback();

	/**
	 * This method is called internally by makeAllPhasesLocal() to rollback the
	 * transaction if there is an exception in beginLocal phase. All open
	 * handlers have to be rolled back here.
	 */
	public void rollbackLocal();

	/**
	 * This method is called internally by makeAllPhases() to rollback the
	 * transaction if there is an exception in prepare phase. All open handlers
	 * have to be rolled back here.
	 */
	public void rollbackPrepare();

	/**
	 * This method is called internally by makeAllPhasesLocal() to rollback the
	 * transaction if there is an exception in prepareLocal phase. All open
	 * handlers have to be rolled back here.
	 */
	public void rollbackPrepareLocal();

	/**
	 * Sets the flag which indicates that this transaction needs an enqueue
	 * lock in order to avoid the conflicts with other transactions.
	 * 
	 * @param lockNeeded flag to indicate the need for enqueue lock.
	 */
	public void setEnqueueLockNeeded(boolean lockNeeded);
	
	/**
	 * Checks the flag which indicates the need for enqueue lock.
	 * @return the flag value.
	 */
	public boolean isEnqueueLockNeeded();

	/**
	 * @return the type of needed enqueue lock for the current transaction.
	 * @see com.sap.engine.frame.core.locking.LockingConstants
	 */
	public char getLockType();
	
	/**
	 * This method is called for global transactions (i.e. transactions 
	 * triggered directly by the client, via remote DeployService interface).
	 * @throws DeploymentException
	 * @throws ComponentNotDeployedException
	 */
	public void makeAllPhases() throws DeploymentException,
			ComponentNotDeployedException;

	/**
	 * This method is called for local transactions (nested transactions or 
	 * transactions created when we have been notified by a remote server that
	 * a global transaction has been successfully executed). So, here we are
	 * at replication server node. The notification is done via MessageContext.
	 * 
	 * @throws DeploymentException
	 * @throws ComponentNotDeployedException
	 */
	public void makeAllPhasesLocal() throws DeploymentException,
			ComponentNotDeployedException;

	/**
	 * Acquires needed local locks and the enqueue lock for the current
	 * transaction.
	 * @throws InterruptedException
	 * @throws ConflictingOperationLockException
	 * @throws LockSetNotAcquiredException
	 */
	public void lock() throws ConflictingOperationLockException,
		LockSetNotAcquiredException, InterruptedException;

	/**
	 * Releases all currently held local locks and the enqueue lock.
	 */ 
	public void unlock();

	public void setBeginTime(long begin);

	public long getBeginTime();

	/**
	 * @return transaction statistics for the current and remote participants.
	 */
	public TransactionStatistics[] getStatistics();

	/**
	 * @return transaction statistics which cannot be null.
	 */
	public TransactionStatistics getCurrentStatistics();

	/**
	 * Check whether we need to perform the given transaction. Has to be called
	 * only when the needed resources for the transaction are already locked.
	 * @return <tt>true</tt> if we need to perform the transaction.
	 */
	public boolean isNeeded();
	
	/**
	 * This method will be called when there is no need for the current 
	 * local transaction (i.e. <tt>isNeeded()</tt> returns <tt>false</tt>).
	 */
	public void notNeeded();

	public boolean isSuccessfullyFinished();

	public boolean isTrackable();
}
