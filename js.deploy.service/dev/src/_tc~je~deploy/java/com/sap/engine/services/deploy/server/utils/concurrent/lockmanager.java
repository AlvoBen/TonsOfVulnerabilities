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
package com.sap.engine.services.deploy.server.utils.concurrent;

/**
 * <p>Interface used to manage local node locks (VM locks). Usually the nodes 
 * have complex relations between selves and when we need to make an operation 
 * over a given node, we have to lock not only the node itself, but also some
 * of the related nodes. The selection of the related nodes is done by the 
 * lock evaluators during the call to their <tt>evaluate</tt> method. This call
 * will be synchronized by the lock manager. 
 * 
 * @param <N> The node type.
 * @author Emil Dinchev
 */
public interface LockManager<N> {
	
	/**
	 * Method to activate the lock manager. 
	 */
	void activate();
	
	/**
	 * Method to deactivate the lock manager. 
	 */
	void deactivate();
	
	/**
	 * Method to acquire locks for nodes requested by a lock evaluator. This is
	 * an atomic operation - all nodes are locked with the desired lock type or
	 * none are locked at all. Every successful call to this method must be 
	 * followed by a call to the unlock method in order to release the acquired
	 * locks.
	 * @param evaluator used lock evaluator. The call to its <tt>evaluate</tt>
	 * method will be synchronized by the lock manager so, that it will not
	 * allow simultaneous modifications of the graph.</p>
	 * @return the lock set for this operation. Guaranteed that it is not null
	 * if there are no exceptions.
	 * @throws ConflictingOperationLockException
	 * @throws LockSetNotAcquiredException
	 * @throws InterruptedException
	 */
	LockSet lock(LockEvaluator<N> evaluator)
		throws ConflictingOperationLockException, LockSetNotAcquiredException,
		InterruptedException;

	/**
	 * Method to unlock a given lock set. Has to be called only if the lock set
	 * was successfully acquired.
	 * @param lockSet the lock set to be unlocked. Must not be null.
	 */
	void unlock(LockSet lockSet);
	
	/**
	 * @return the lock tracker.
	 */
	LockTracker<N> getLockTracker();
	
	/**
	 * Dumps all currently acquired locks and their owners.
	 */
	String dumpLocks();
	
	/**
	 * This method dumps the current thread.
	 * @return dump of the operation stack for the current thread or null if
	 * this stack is empty.
	 */
	String dumpCurrentThread();
	
}