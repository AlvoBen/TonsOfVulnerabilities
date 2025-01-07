package com.sap.engine.services.deploy.server.utils.concurrent;

/**
 * Lock tracker can be used to track the lock state of a given node. It is
 * intended to be used by <tt>evaluate()</tt> methods of lock evaluators.
 *
 * @param <N> the node type.
 */
public interface LockTracker<N> {
	public enum LockState {
		UNLOCKED,
		EXCLUSIVE,
		SHARED
	}
	
	/**
	 * Checks the lock state of a given node.
	 * @param node the node to be checked.
	 * @return the lock state.
	 */
	LockState getLockState(N node);
}
