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
import com.sap.engine.services.deploy.server.utils.concurrent.LockTracker;
import com.sap.engine.services.deploy.server.utils.concurrent.LockTracker.LockState;

/**
 * Evaluator used to lock nodes for stop operation.
 * @author Emil Dinchev
 */
public final class StopLockEvaluator extends AbstractLockEvaluator {
	private final Graph<Component> graph;
	private final LockTracker<Component> lockTracker;
	private final boolean exlusiveLockRoot;

	/**
	 * Constructor.
	 * @param graph the used reference graph.
	 * @param lockTracker lock tracker.
	 * @param operation operation name (transaction type).
	 * @param exclusiveLockRoot we want to lock the root exclusively.
	 * @param root root node.
	 * @param enqueueLockType the enqueue lock type.
	 * @see com.sap.engine.frame.core.locking.LockingConstants
	 * @param timeout timeout to acquire the needed locks.
	 */
	public StopLockEvaluator(final Graph<Component> graph,
		final LockTracker<Component> lockTracker, 
		final String operation, final boolean exclusiveLockRoot, 
		final Component root, final char enqueueLockType, final long timeout) {
		super(operation, Status.STOPPED, root, enqueueLockType, timeout);
		this.graph = graph;
		this.lockTracker = lockTracker;
		this.exlusiveLockRoot = exclusiveLockRoot;
	}

	

	/**
	 * Get the successors of a given node. We cannot find the successors in
	 * in advance, because the graph is dynamic and it can be changed during 
	 * some other lock operations. Therefore we have to find the successors 
	 * every time, when we are trying to obtain the locks.
	 * @param node the given node.
	 * @return collection of nodes. Cannot be null.
	 */
	private Collection<Component> getSuccessors() {
		try {
			return graph.sortBackward(getRoot());
		} catch (CyclicReferencesException ex) {
			assert false;
			// this should not happens, because we make a check for cycles
			// during deploy.
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public Collection<LockEntry<Component>> evaluate() {
		final Collection<Component> components = getSuccessors();
		final Collection<LockEntry<Component>> entries =
			new ArrayList<LockEntry<Component>>();
		final Component root = getRoot();
		final boolean needExclusive = needExclusive(root);
		for(Component component : components) {
			if(exlusiveLockRoot && component.equals(root)) {
				entries.add(new LockEntry<Component>(component, true));
			} else {
				entries.add(new LockEntry<Component>(
					component, needExclusive && needExclusive(component)));
			}
		}
		return entries;
	}

	/**
	 * Checks whether we need exclusive lock for a given node.
	 * @param comp
	 * @return <tt>true</tt> if we need exclusive lock.
	 */
	@SuppressWarnings("deprecation")
	private boolean needExclusive(final Component comp) {
		final Status status = getStatus(comp);
		return LockState.EXCLUSIVE.equals(lockTracker.getLockState(comp)) ||
			!(status.equals(Status.IMPLICIT_STOPPED) ||
			 status.equals(Status.STOPPED));
	}
}