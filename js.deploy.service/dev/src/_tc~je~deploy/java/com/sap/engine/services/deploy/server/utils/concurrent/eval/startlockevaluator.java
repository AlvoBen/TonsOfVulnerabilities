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
package com.sap.engine.services.deploy.server.utils.concurrent.eval;

import java.util.ArrayList;
import java.util.Collection;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.utils.concurrent.LockTracker;
import com.sap.engine.services.deploy.server.utils.concurrent.LockTracker.LockState;

/**
 * Evaluator used to lock nodes for start operation. 
 * @author Emil Dinchev 
 */
public final class StartLockEvaluator extends AbstractLockEvaluator {
	
	private final Graph<Component> graph;
	
	private final LockTracker<Component> lockTracker;
	
	/**
	 * Constructor.
	 * @param graph the used reference graph.
	 * @param lockTracker lock tracker.
	 * @param root root node.
	 * @param enqueueLockType the enqueue lock type.
	 * @see com.sap.engine.frame.core.locking.LockingConstants
	 * @param timeout timeout to acquire the needed locks.
	 */
	public StartLockEvaluator(final Graph<Component> graph,
		final LockTracker<Component> lockTracker, final Component root, 
		final char enqueueLockType, final long timeout) {
		super(DeployConstants.startApp, Status.STARTED, 
			root, enqueueLockType, timeout);
		this.graph = graph;
		this.lockTracker = lockTracker;
	}

	/**
	 * Gets the predecessors of a given node in the graph. We cannot find the
	 * predecessors in advance, because the graph is dynamic and it can be 
	 * changed during some other lock operations. Therefore we have to find the
	 * predecessors every time, when we are trying to obtain the locks.
	 * @param node the given node.
	 * @return collection of nodes. Cannot be null.
	 */
	protected Collection<Component> getPredecessors() {
		try {
			return graph.sort(getRoot());
		} catch (CyclicReferencesException ex) {
			assert false;
			// this should not happens, because we make a check for cycles
			// during deploy.
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Collection<LockEntry<Component>> evaluate() {
		final Collection<Component> components = getPredecessors();
		final Collection<LockEntry<Component>> entries =
			new ArrayList<LockEntry<Component>>();
		final Component root = getRoot();
		final boolean needExclusive = 
			lockTracker.getLockState(root) == LockState.EXCLUSIVE ||
			isStatusDifferent(root);
		for(Component component : components) {
			if(lockTracker.getLockState(component) == LockState.EXCLUSIVE) {
				// This node is locked exclusive which means that
				// someone wants to change its status.
				entries.add(new LockEntry<Component>(component, true));
			} else {
				// The lock is not locked or is locked with shared lock,
				// which means that no one wants to change its status now.
				entries.add(new LockEntry<Component>(component, 
					needExclusive && isStatusDifferent(component)));
			}
		}
		return entries;
	}
}