/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.sap.engine.services.deploy.server.utils.DSConstants.*;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;

/**
 * <p>Package private class used to hold information about the locks. Here we
 * implement a reentrant lock which can be obtained in exclusive or in shared
 * mode. If a given lock is exclusive, it can be owned only by threads from the
 * same family. Two threads belong to the same family if they are created by
 * the same parent thread. Threads from different families can share the same 
 * lock, provided that it is not exclusive. One thread can acquire the same 
 * lock many times. Also it is possible to upgrade a shared lock to exclusive,
 * if there is no owners from other families. The lock is released when it is 
 * released by all its owners.</p>
 * <p>We not need synchronization here, because it is done in LockManagerImpl.
 * </p>
 * 
 * @author Emil Dinchev
 */
final class NodeLock<N> {
	private final static int INITIAL_CAPACITY = 2;
	
	// If this field is set, then the current lock is exclusive.
	private LockOwner<N> exclusiveOwner;
	
	private final N node;

	// Maps thread ID of the owning thread to lock count.
	private final Map<LockOwner<N>, Integer> owners;

	NodeLock(N node) {
		this.node = node;
		owners = new HashMap<LockOwner<N>, Integer>(INITIAL_CAPACITY);
	}

	Set<LockOwner<N>> getOwningThreads() {
		return owners.keySet();
	}

	/**
	 * <p>Method to acquire the lock. The lock is cumulative and can be 
	 * acquired more than once. Also the current thread can obtain exclusive
	 * and shared locks simultaneously in a random order. If the lock was 
	 * acquired, it has to be released in the same thread.</p>
	 * <p>Also we take account of relations between threads allowing the child
	 * threads to acquire the same locks as their parent - i.e. threads from
	 * the same family.
	 * @param lockOwner the lock owner retrieved by the current deploy thread,
	 * which needs the lock.
	 * @param operation operation for which the lock is needed (the same as
	 * transaction type).
	 * @param exclusive whether this lock has to be exclusive, i.e. owned only 
	 * by the current thread family.
	 * @throws ConflictingOperationLockException if there is a conflict between
	 * the current operation and the given one. 
	 * @return <tt>true</tt> if the lock was acquired and <tt>false</tt> 
	 * otherwise.
	 */
	boolean lock(final LockOwner<N> lockOwner,
		final String operation, final boolean exclusive)
		throws ConflictingOperationLockException {

		// First we have to validate the lock request.
		if(!isLockable(lockOwner, operation, exclusive)) {
			return false;
		}

		final Integer lockCounter = owners.get(lockOwner);
		final int count = (lockCounter == null) ? 0 : lockCounter.intValue();
		owners.put(lockOwner, new Integer(count + 1));
		if(exclusive && exclusiveOwner == null) {
			exclusiveOwner = lockOwner;
		}
		return true;
	}

	/**
	 * Check whether this node can be locked by the given thread. 
	 * @param lockOwner the lock owner retrieved by the current thread context.
	 * @param operation current deploy operation.
	 * @param exclusive the needed lock is exclusive.
	 * @return <tt>true</tt> if the lock can be acquired.
	 * @throws ConflictingOperationLockException
	 */
	private boolean isLockable(final LockOwner<N> lockOwner,
		final String operation, final boolean exclusive) 
		throws ConflictingOperationLockException {
		
		if(owners.isEmpty()) {
			// Unlocked.
			return true;
		}
		if(exclusive) {
			if(isExclusive()) {
				if(exclusiveOwner.equals(lockOwner)) {
					// This lock is held exclusively by the same thread.
					return true;
				}
				if(exclusiveOwner.getCreator().equals(
					lockOwner.getCreator())) {
					// This lock is held exclusively by the same family.
					if(lockOwner.failFastOnLockAttempt()) {
						throw new ConflictingOperationLockException(
							"The thread " + lockOwner + " performing " + 
							operation + " cannot acquire exclusive lock for " +
							node + " because it is held exclusively by " + 
							exclusiveOwner + " and we have to fail fast.");
					}
					return true;
				}
				// The lock is held exclusively by another family.
				return false;
			}
			// The lock is held shared.
			final ThreadDescriptor creator = lockOwner.getCreator();
			for(LockOwner<N> curr : owners.keySet()) {
				if(curr.equals(lockOwner)) {
					// Held shared by the same thread.
					continue;
				}
				if(curr.getCreator().equals(creator)) {
					// Held shared by the same family.
					if(lockOwner.failFastOnLockAttempt()) {
						throw new ConflictingOperationLockException(
							"The thread " + lockOwner + " performing " + 
							operation + " cannot acquire exclusive lock for " +
							node + " because it is held shared by " + curr + 
							" and we have to fail fast.");
					}
				} else {
					// Held shared by another family.
					return false;
				}
			}
			// Shared by the same family.
			return true;
		}
		// Shared lock is requested.
		if(exclusiveOwner == null) {
			// No exclusive owner.
			return true;
		}
		if(exclusiveOwner.equals(lockOwner)) {
			// Owned exclusively by the same thread.
			return true;
		}
		if(exclusiveOwner.getCreator().equals(lockOwner.getCreator())) {
			// Owned exclusively by the same family.
			if(lockOwner.failFastOnLockAttempt()) {
				throw new ConflictingOperationLockException(
					"The thread " + lockOwner + " performing " + 
					operation + " cannot acquire shared lock for " + node +
					" because it is held exclusively by " 
					+ exclusiveOwner + " and we have to fail fast.");
			}
			return true;
		}
		// Owned exclusively by another family.
		return false;
	}

	/**
	 * Method to release the lock. Has to be called in the same thread as the
	 * corresponding <tt>lock(boolean)</tt>.
	 * @param lockOwner the owner of the lock.
	 * @return true if the lock was fully released, i.e. the lock has no more
	 * owners.
	 */
	boolean unlock(final LockOwner<N> lockOwner) {
		Integer lockCounter = owners.get(lockOwner);
		if(lockCounter == null) {
			throw new NullPointerException(
				"Exclusive lock is not owned by the current thread ["
				+ Thread.currentThread() + "] and cannot be unlocked by it.");
		}
		final int count = lockCounter.intValue() - 1;
		if (count == 0) {
			owners.remove(lockOwner);
			if(lockOwner.equals(exclusiveOwner)) {
				exclusiveOwner = null;
			}
			if (owners.size() == 0) {
				// The last owner is removed.
				return true;
			}
		} else {
			owners.put(lockOwner, new Integer(count));
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(node).append("],").append(EOL)
			.append("which is held ");
		if(exclusiveOwner != null) {
			sb.append("exclusively for [")
				.append(exclusiveOwner.getOperation())
				.append("] by ").append(exclusiveOwner)
				.append(EOL_TAB);
		} else {
			sb.append("shared by the following threads:").append(EOL_TAB);
			for(LockOwner<N> dplThread : owners.keySet()) {
				sb.append(dplThread.toString()).append(EOL);
			}
		}
		return sb.toString();
	}

	/**
	 * @return <tt>true</tt> if the node is locked exclusively by given thread
	 * family.
	 */
	boolean isExclusive() {
		return exclusiveOwner != null;
	}
	
	N getNode() {
		return node;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		final NodeLock<N> other = (NodeLock<N>)obj;
		return node.equals(other.node);
	}
	
	@Override
	public int hashCode() {
		return node.hashCode();
	}
}