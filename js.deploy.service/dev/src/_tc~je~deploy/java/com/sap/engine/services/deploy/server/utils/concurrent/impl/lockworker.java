package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import static com.sap.engine.services.deploy.container.util.CAConstants.EOL;
import static com.sap.engine.services.deploy.server.utils.DSConstants.EOL_TAB_TAB;
import static com.sap.engine.services.deploy.server.utils.DSConstants.TAB;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator;
import com.sap.engine.services.deploy.server.utils.concurrent.LockTracker;

/**
 * This class is responsible for the real work with the locks and holds the
 * mapping between nodes and locks.We use a single instance of the class, which
 * is created by the LockManagerImpl. All method invocations are synchronized 
 * outside by LockManagerImpl.
 *  
 * @author Emil Dinchev
 * 
 * @param <N> node type.
 */
final class LockWorker<N> implements LockTracker<N> {
	/**
	 * Map between nodes and locks. The access to this map is synchronized
	 * outside.
	 */
	private final Map<N, NodeLock<N>> locksMap;
	
	/**
	 * Set of nodes which need to be locked, but currently it is not possible.
	 */
	private final Set<N> neededLocks;
	
	/**
	 * Creates lock worker. We use a single instance of the class, which is
	 * created by the LockManagerImpl.
	 */
	LockWorker() {
		locksMap = new LinkedHashMap<N, NodeLock<N>>();
		neededLocks = new HashSet<N>();
	}

	/**
	 * Try to acquire locks for a collection of nodes. This is atomic operation:
	 * when the method returns, all locks are acquired, or no locks are acquired
	 * at all. No need to synchronize this method, because it is synchronized 
	 * by the global lock of LockManagerImpl.
	 * 
	 * @param evaluator the evaluator to decide which locks we need.
	 * @param owner the candidate owner of the locks. 
	 * @return <tt>LockSetImpl</tt> object, which <tt>isAcquired()</tt> method
	 * returns <tt>true</tt> when we have acquired all needed locks or 
	 * <tt>false</tt> if not. Cannot be null.
	 * @throws ConflictingOperationLockException 
	 */
	LockSetImpl<N> doLock(final LockEvaluator<N> evaluator,
		final LockOwner<N> owner) throws ConflictingOperationLockException {
		// Nodes to be locked.
		final Collection<LockEvaluator.LockEntry<N>> entries = 
			evaluator.evaluate();
		final Set<N> currNodes = new HashSet<N>();
		for(LockEvaluator.LockEntry<N> entry : entries) {
			final NodeLock<N> nLock = obtainLockFor(entry.getNode());
			try {
				if(nLock.lock(owner, 
					evaluator.getOperation(), entry.isNeedExclusive())) {
					currNodes.add(entry.getNode());
				} else {
					// Release all currently locked nodes.
					unlockNodes(owner, currNodes);
					// The node cannot be locked and is added to needed locks
					neededLocks.add(entry.getNode());
					return LockSetImpl.createEmptySet(nLock, entry.isNeedExclusive());
				}
			} catch(ConflictingOperationLockException ex) {
				// This is not the same as finally!
				unlockNodes(owner, currNodes);
				throw ex;
			}
		}
		// All required nodes are locked successfully.		
		return LockSetImpl.create(entries);
	}

	/**
	 * Gets the lock for a given node or create a new one, registering it in the
	 * locks map. No need to synchronize this method, because the access to
	 * locks map is protected by the global lock of LockManagerImpl.
	 * 
	 * @param node
	 *            the node for which we need a lock.
	 * @return the registered lock.
	 */
	NodeLock<N> obtainLockFor(final N node) {
		NodeLock<N> nLock = locksMap.get(node);
		if (nLock == null) {
			nLock = new NodeLock<N>(node);
			locksMap.put(node, nLock);
		}
		return nLock;
	}

	/**
	 * Unlock a collection of nodes. No need to synchronize this method, because
	 * the access to locks map is protected by the global lock of
	 * LockManagerImpl.
	 * 
	 * @param nodes
	 *            collection of nodes for which we have to unlock acquired
	 *            locks.
	 * @return <tt>true</tt> if some of needed locks are unlocked.
	 */
	boolean unlockNodes(final LockOwner<N> consumer,
		final Collection<N> nodes) {
		boolean neededLockUnlocked = false;
		for(N node : nodes) {
			if(unlockSingleNode(consumer, node)) {
				neededLockUnlocked = true;
			}
		}
		
		return neededLockUnlocked;
	}

	/**
	 * Check whether the given node is exclusively locked. No need to
	 * synchronize this method, because the access to locks map is protected by
	 * the global lock of LockManagerImpl.
	 * 
	 * @param node
	 *            the given node
	 * @return <tt>true</tt> if the node is exclusively locked.
	 */
	String dumpLockState() {
		final Map<LockOwner<N>, Set<NodeLock<N>>> threads =
			new HashMap<LockOwner<N>, Set<NodeLock<N>>>();
		
		for(NodeLock<N> nodeLock : locksMap.values()) {
			for(LockOwner<N> th : nodeLock.getOwningThreads()) {
				Set<NodeLock<N>> nodes = threads.get(th);
				if(nodes == null) {
					nodes = new HashSet<NodeLock<N>>();
					threads.put(th, nodes);
				}
				nodes.add(nodeLock);
			}
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(EOL).append("Active threads:").append(EOL);
		for(LockOwner<N> th : threads.keySet()) {
			sb.append("Thread:").append(th.toString())
				.append(" owns the following locks:");
			for(NodeLock<N> nodeLock : threads.get(th)) {
				sb.append(EOL_TAB_TAB).append(TAB).append(nodeLock.getNode())
					.append(" - ").append(
						nodeLock.isExclusive() ? "exclusive" : "shared");
			}
			sb.append(EOL);
		}
		sb.append(EOL).append("Needed locks:").append(DSConstants.EOL_TAB)
			.append(neededLocks).append(CAConstants.EOL);
		return sb.toString();
	}

	
	/**
	 * Unlocks a single node.
	 * 
	 * @param node
	 *            the given node.
	 * @return <tt>true</tt> if the unlocked node was among the nodes needed to
	 *         be locked.
	 */
	private boolean unlockSingleNode(final LockOwner<N> consumer, 
		final N node) {
		final NodeLock<N> nodeLock = locksMap.get(node);
		assert nodeLock != null : "Locks map does not contains locks for node " + node;
		if (nodeLock.unlock(consumer)) {
			locksMap.remove(node);
			return neededLocks.remove(node);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.utils.concurrent.LockTracker#getLockState(java.lang.Object)
	 */
	public LockState getLockState(final N node) {
		final NodeLock<N> nodeLock = locksMap.get(node);
		return nodeLock == null ?
			LockState.UNLOCKED : nodeLock.isExclusive() ?
				LockState.EXCLUSIVE : LockState.SHARED;
	}
}