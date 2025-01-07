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

import static com.sap.engine.services.deploy.container.util.CAConstants.EOL;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.EnqueueLocker;
import com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator;
import com.sap.engine.services.deploy.server.utils.concurrent.LockManager;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSet;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockTracker;
import com.sap.engine.services.deploy.server.utils.concurrent.EnqueueLocker.EnqueueLock;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * An implementation of <tt>LockManager</tt> interface.
 * @author Emil Dinchev
 * @param <N> node types.
 */
public final class LockManagerImpl<N> implements LockManager<N> {
	private static final int WAIT_BEFORE_NEW_ENQUEUE_LOCK_ATTEMPT = 10000;
	private static final Location location = 
		Location.getLocation(LockManagerImpl.class);
	
	private final LockOwnerRetriever<N> lockOwnerRetriever;
	private final EnqueueLocker enqueueLocker;
	private final Set<LockOwner<N>> waitingThreads;
	/**
	 * The low level lock operations are delegated to the lock worker.
	 */
	final LockWorker<N> worker;

	/**
	 * Fair policy reentrant lock to synchronize the calls to lock worker. This
	 * lock is used to ensure atomic concurrent locking operations with scope
	 * VM.
	 */
	private final Lock managerLock;

	/**
	 * Condition signaled when some needed locks are released.
	 */
	private final Condition lockIsReleased;

	/**
	 * The constructor.
	 * @param graph the graph, describing the relations of the nodes.
	 */
	public LockManagerImpl(final ThreadSystem threadSystem,
		final EnqueueLocker enqueueLocker,
		final boolean failFastOnLockAttempt) {
		lockOwnerRetriever = new LockOwnerRetriever<N>(
			threadSystem, failFastOnLockAttempt);
		this.enqueueLocker = enqueueLocker;
		worker = new LockWorker<N>();
		// We use a global lock with fair policy.
		managerLock = new ReentrantLock(true);
		// Condition to indicate that a node lock is released.
		lockIsReleased = managerLock.newCondition();
		waitingThreads = new HashSet<LockOwner<N>>();
		
	}

	// (non-Javadoc)
	/**
	 * <p>The locking require obtaining of local locks and one optional enqueue
	 * lock. This is atomic operation and if it fails, all acquired locks will 
	 * be released.</p>
	 * <p>The order of lock acquisition is very important. If we acquire first
	 * the enqueue locks than a deadlock will occur in the following situation:
	 * <pre>
	 *   a  b  c                     successors
	 *    \/ \/
	 *    d  e
	 *   / \/ \
	 *  f  g   h                     predecessors
	 *  
	 * (g) is stopped and (c) is updated in parallel.</pre> 
	 * <p>Lets imagine that (g, d, e, a, b, c) are locked exclusively for stop.
	 * (c) is locked only in the enqueue for update and is waiting for local 
	 * locks. Stopping thread will start to creep over the successors, and will 
	 * reach (c). There it will try to lock it in the enqueue, but this will be 
	 * not possible, because the update thread has locked it. So stopping 
	 * thread will wait for enqueue lock of (c) and updating thread will wait 
	 * for local locks of (e, g, h).</p>
	 */
	@SuppressWarnings("boxing")
	public LockSet lock(final LockEvaluator<N> evaluator) 
		throws InterruptedException, ConflictingOperationLockException,
		LockSetNotAcquiredException {
		if(location.beDebug()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"LockManager.doLock() called for [{0} of {1}].", 
				evaluator.getOperation(), evaluator.getRoot());
		}
		LockSetImpl<N> lockSet;
		// Manager lock can be obtained very fast.
		managerLock.lock();
		final LockOwner<N> owner = lockOwnerRetriever.retrieve();
		try {
			// The current thread holds the manager lock now.
			final Timeout timeout = new Timeout(evaluator.getTimeout());
			boolean retry;
			do {
				// The current thread holds the manager lock now.
				// We have to try to acquire the lock set.
				lockSet = worker.doLock(evaluator, owner);
				if (lockSet.isAcquired()) {
					if(!obtainEnqueueLock(evaluator.getEnqueueLock())) {
						if(worker.unlockNodes(owner, lockSet.getNodes())) {
							lockIsReleased.signalAll();
						}
						lockIsReleased.await(
							WAIT_BEFORE_NEW_ENQUEUE_LOCK_ATTEMPT, 
							TimeUnit.MILLISECONDS);
						retry = !timeout.isElapsed();
						continue;
					}
					lockSet.setEnqueueLock(evaluator.getEnqueueLock());
					owner.enterOperation(
						evaluator.getOperation(), evaluator.getRoot());
					if(location.beDebug()) {
						SimpleLogger.trace(Severity.DEBUG, location, null,
							"The thread {0}({1})" + EOL +
							"succesfully acquired the needed locks." + EOL +
							"{2}", 
							owner.getCurrent().getName(),
							owner.getCurrent().getId(), worker.dumpLockState());
					}
					return lockSet;
				}
				// There is a locked node for which unlocking we have to wait.
				waitingThreads.add(owner);
				if(location.beInfo()) {
					SimpleLogger.trace(Severity.INFO, location, null,
						"The thread {0}" + EOL +
						"performing operation [{1} {2}]" + EOL +
						"have to wait for {3} lock of {4}." + EOL +
						"{5}",
						owner, evaluator.getOperation(), evaluator.getRoot(),
						lockSet.isNeededExclusively() ? "exclusive" : "shared",
						lockSet.getNeededLock(), worker.dumpLockState());
				}
				// The manager lock is released during the call to await().
				retry = !timeout.isElapsed() && lockIsReleased.await(
					timeout.getRemainingTime(),	TimeUnit.MILLISECONDS);
			} while(retry);

			// Time out.
			final String reasonForFailure = getReasonForFailure(
				owner, lockSet, evaluator);
				
			if(location.beDebug()) {
				// The severity is debug, to log it only once 
				// - will be logged by the client.
				SimpleLogger.trace(Severity.DEBUG, location, null,
					reasonForFailure);
			}
			throw new LockSetNotAcquiredException(reasonForFailure);
		} finally {
			// We have finished our job. Give a chance to another thread.
			waitingThreads.remove(owner);			
			managerLock.unlock();
		}
	}

	private boolean obtainEnqueueLock(final EnqueueLock enqueueLock) {
		managerLock.unlock();
		try {
			return enqueueLocker.lock(enqueueLock);
		} finally {
			managerLock.lock();
		}
	}

	private String dumpWaitingThreads() {
		final StringBuilder sb = new StringBuilder();
		for(LockOwner<N> thread : waitingThreads) {
			sb.append(thread).append(EOL);
		}
		return sb.toString();
	}

	// (non-Javadoc)
	@SuppressWarnings("unchecked")
	public void unlock(LockSet lockSet) {
		final LockSetImpl<N> impl = (LockSetImpl<N>)lockSet;
		// Unlock the enqueue lock first.
		enqueueLocker.unlock(impl.getEnqueueLock());
		
		// The current thread has to hold the manager lock.
		managerLock.lock();
		final LockOwner<N> lockOwner = lockOwnerRetriever.retrieve();
		try {
			if (worker.unlockNodes(lockOwner, impl.getNodes())) {
				lockIsReleased.signalAll();
			}
			
		} finally {
			lockOwner.leaveOperation();
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"{0} is released." + EOL + "{1}", 
					lockSet.toString(), worker.dumpLockState());
			}
			managerLock.unlock();
		}
	}

	// (non-Javadoc)
	public String dumpLocks() {
		managerLock.lock();
		try {
			return worker.dumpLockState();
		} finally {
			managerLock.unlock();
		}
	}

	// (non-Javadoc)
	public void activate() {
		lockOwnerRetriever.activate();
	}

	// (non-Javadoc)
	public void deactivate() {
		lockOwnerRetriever.deactivate();
	}

	// (non-Javadoc)
	public LockTracker<N> getLockTracker() {
		return worker;
	}
	
	private String getReasonForFailure(final LockOwner<N> owner,
		final LockSetImpl<N> lockSet, final LockEvaluator<N> evaluator) {
		final StringBuilder sb = new StringBuilder();
		if(lockSet.getEnqueueLock() == null) {
			sb.append("Cannot obtain the enqueue lock of type [")
				.append(evaluator.getEnqueueLock().getType())
				.append("] for ").append(evaluator.getOperation())
				.append(" of application ").append(evaluator.getRoot());
			return sb.toString();
		} 
		sb.append("The thread ").append(owner).append(EOL)
			.append("timed out while trying to get the locks for operation [")
			.append(evaluator.getOperation()).append(" : ")
			.append(evaluator.getRoot()).append("]").append(EOL)
			.append("Has been waiting for ")
			.append(lockSet.isNeededExclusively() ? "exclusive" : "shared") 
			.append(" lock for the node ")
			.append(lockSet.getNeededLock()).append(EOL)
			.append(worker.dumpLockState()).append(EOL)
			.append("Waiting threads: ").append(dumpWaitingThreads());
		final Set<LockOwner<N>> threads = lockSet.getNeededLock().getOwningThreads();
		final Map<Thread, StackTraceElement[]> callStacks = Thread.getAllStackTraces();
		sb.append(EOL).append("Call stacks:").append(EOL);
		for(Thread th : callStacks.keySet()) {
			for(LockOwner<N> dplThread : threads) {
				if(th.getId() == dplThread.getCurrent().getId()) {
					sb.append(dplThread).append(EOL);
					for(StackTraceElement stElement : callStacks.get(th)) {
						sb.append(stElement).append(EOL);
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Package-private method used by CleanRunnable to obtain the lock owner 
	 * retriever.
	 * @return the lock owner retriever.
	 */
	LockOwnerRetriever<N> getLockOwnerRetriever() {
		return lockOwnerRetriever;
	}
	
	/* (non-Javadoc)
	 */
	public String dumpCurrentThread() {
		return lockOwnerRetriever.retrieve().toString();
	}
}