package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSet;
import com.sap.engine.services.deploy.server.utils.concurrent.EnqueueLocker.EnqueueLock;

/**
 *  Successful call means that we have received lock set for which 
 *  <tt>isAcquired()</tt> method returns <tt>true</tt>.
 * @author Emil Dinchev
 * 
 * @param <N> the type of the nodes.
 */
final class LockSetImpl<N> implements LockSet {
	private final Collection<N> nodes;
	private final NodeLock<N> neededLock;
	private final boolean neededExclusively;

	private EnqueueLock enqueueLock;

	/*
	 * Constructor for successfully acquired lock set.
	 * @param nodes locked nodes.
	 */
	private LockSetImpl(final Collection<N> nodes) {
		assert nodes != null;
		this.nodes = nodes;
		this.neededLock = null;
		this.neededExclusively = false;
	}

	void setEnqueueLock(final EnqueueLock enqueueLock) {
		this.enqueueLock = enqueueLock;
	}
	
	EnqueueLock getEnqueueLock() {
		return enqueueLock;
	}

	/*
	 * Constructor for failed try to acquire the locks.
	 * @param neededLock the node lock that cannot be acquired now.
	 */
	private LockSetImpl(final NodeLock<N> neededLock, 
		final boolean neededExclusively) {
		nodes = null;
		this.neededLock = neededLock;
		this.neededExclusively = neededExclusively;
	}

	/**
	 * Method to create successfully acquired lock set.
	 * @param <N> the type of the nodes.
	 * @param nodes locked nodes.
	 * @return successfully acquired lock set. 
	 */
	static <N> LockSetImpl<N> create(
		final Collection<LockEvaluator.LockEntry<N>> entries) {
		final List<N> nodes = new ArrayList<N>(entries.size());
		for(LockEvaluator.LockEntry<N> entry : entries) {
			nodes.add(entry.getNode());
		}
		return new LockSetImpl<N>(nodes);
	}
	
	/**
	 * This method is called to create an empty lock set, indicating that the
	 * lock request was unsuccessful.
	 * @param <N> the type of the nodes.
	 * @param neededLock the lock which is held by other threads.
	 * @param exclusive whether we have tried to lock the node exclusively.
	 * @return an empty lock set, which holds the reason for the failed lock.
	 */
	static <N> LockSetImpl<N> createEmptySet(
		final NodeLock<N> neededLock, final boolean needsExclusively) {
		return new LockSetImpl<N>(neededLock, needsExclusively);
	}

	Collection<N> getNodes() {
		return nodes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (nodes != null) {
			sb.append("LockSet {").append(DSConstants.EOL_TAB)
				.append(nodes).append("}").append(CAConstants.EOL);
		} else {
			sb.append("LockSet is empty.");
		}
		return sb.toString();
	}

	/**
	 * Checks whether all needed locks are acquired.
	 * @return <tt>true</tt> if all needed locks are acquired.
	 */
	boolean isAcquired() {
		return neededLock == null;
	}
	
	/**
	 * @return the needed lock, which is the reason for the failed lock 
	 * acquisition. 
	 */
	NodeLock<N> getNeededLock() {
		assert !isAcquired();
		return neededLock;	
	}
	
	boolean isNeededExclusively() {
		assert !isAcquired();
		return neededExclusively;
	}
}