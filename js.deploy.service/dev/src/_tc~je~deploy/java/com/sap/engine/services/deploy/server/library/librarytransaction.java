/*
 * LibraryTransaction.java
 *
 * Created on April 9, 2002, 1:30 AM
 */
package com.sap.engine.services.deploy.server.library;

import java.util.List;

import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.server.DTransaction;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.TransactionStatistics;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSet;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.SingleNodeLockEvaluator;


/**
 * 
 * @author Radoslav Tsiklovski
 * @version
 */
public abstract class LibraryTransaction implements DTransaction {
	// TODO: to be replaced by enumeration
	public static final byte LIBRARY = 0;
	public static final byte INTERFACE = 1;
	public static final byte SERVICE = 2;

	private long beginTime = 0;

	protected final DeployServiceContext ctx;
	private final String moduleID;
	private boolean lockNeeded = false;
	protected String transactionType;
	protected int clusterElements = 0;
	private final TransactionStatistics currentStatistics;
	private boolean okFinished = false;
	private LockSet lockSet;
	protected final byte type;
	private char lockType = LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE;

	public LibraryTransaction(final DeployServiceContext ctx,
		final String transType, final byte type, final String libName) {
		this.ctx = ctx;
		currentStatistics = new TransactionStatistics(
			ctx.getClusterMonitorHelper().getCurrentServerId());
		this.transactionType = transType;
		this.type = type;
		moduleID = libName;
	}

	public Component getComponent() {
		final Component.Type componentType;
		switch (type) {
		case SERVICE:
			componentType = Component.Type.SERVICE;
			break;
		case INTERFACE:
			componentType = Component.Type.INTERFACE;
			break;
		default:
			componentType = Component.Type.LIBRARY;
			break;
		}
		return new Component(getModuleID(), componentType);
	}

	public void lock() throws LockSetNotAcquiredException, 
		InterruptedException, ConflictingOperationLockException {
		lockSet = ctx.getLockManager().lock(
			new SingleNodeLockEvaluator(getTransactionType(), getComponent(),
				isEnqueueLockNeeded() ? getLockType() : 0,
				PropManager.getInstance().getTimeout4LocalLock()));
	}

	public void unlock() {
		ctx.getLockManager().unlock(lockSet);
	}

	public String getModuleID() {
		return this.moduleID;
	}

	public String getSoftwareType() {
		return null;
	}

	public byte getModuleType() {
		return DeployConstants.LIB_TYPE;
	}

	public String getModuleTypeAsString() {
		return DeployConstants.RESOURCE_TYPE_LIBRARY;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void makeAllPhasesLocal() throws DeploymentException {
		// not implemented.
	}

	public void makeAllPhases() throws DeploymentException,
			ComponentNotDeployedException {
		try {
			this.begin();
		} catch (DeploymentException rex) {
			this.rollback();
			throw rex;
		} catch (ComponentNotDeployedException cnde) {
			this.rollback();
			throw cnde;
		}
		try {
			this.prepare();
		} catch (DeploymentException rex) {
			this.rollback();
			throw rex;
		}
		this.commit();
	}

	public void setEnqueueLockNeeded(boolean lockNeeded) {
		this.lockNeeded = lockNeeded;
	}

	public boolean isEnqueueLockNeeded() {
		return lockNeeded;
	}

	public void setBeginTime(long begin) {
		this.beginTime = begin;
	}

	public long getBeginTime() {
		return this.beginTime;
	}

	public TransactionStatistics[] getStatistics() {
		return new TransactionStatistics[] {
 			currentStatistics };
	}

	public TransactionStatistics getCurrentStatistics() {
		return this.currentStatistics;
	}

	public void beginLocal() throws DeploymentException,
		ComponentNotDeployedException {
		begin();
	}

	public void rollbackPrepare() {
		// Empty default method.
	}

	public void prepare() throws DeploymentException {
		// Empty default method.
	}

	public void prepareLocal() throws DeploymentException {
		// Empty default method.
	}

	public void commit() {
		// Empty default method.
	}

	public void commitLocal() {
		// Empty default method.
	}

	public void rollbackPrepareLocal() {
		// Empty default method.
	}

	public void rollback() {
		// Empty default method.
	}

	public void rollbackLocal() {
		// Empty default method.
	}

	public boolean isNeeded() {
		return true;
	}

	public void notNeeded() {
		// Empty default method.	
	}
	public boolean isSuccessfullyFinished() {
		return okFinished;
	}

	protected void setSuccessfullyFinished(boolean ok) {
		this.okFinished = ok;
	}

	public char getLockType() {
		return lockType;
	}

	public void setLockType(char lockType) {
		this.lockType = lockType;
	}

	public void addWarnings(List<String> warnings) {
		for(String warning : warnings) {
			currentStatistics.addWarning(warning);
		}
	}

	public boolean isTrackable() {
		return true;
	}
}
