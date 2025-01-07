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

import java.util.Collection;

import com.sap.engine.services.deploy.server.utils.concurrent.EnqueueLocker.EnqueueLock;

/**
 * <p>This interface is used to evaluate the need for locks for nodes which
 * have complex relations between selves. These relations usually are presented
 * by reference graph. The nodes in our case present server components 
 * (applications, libraries, services and interfaces).</p>
 * <p>In order to evaluate the need for given lock, we have to check the state
 * of the component. So we assume that the state of an application will be changed
 * only if this application is exclusively locked. The calls to <tt>evaluate</tt>
 * are synchronized by the lock manager.</p>
 * </p>Also it is supposed that the lock evaluators have access to the graph in
 * order to create the lock entries for the needed locks. We will have 
 * different implementations of lock evaluators.</p>
 * <p>This interface has to be implemented by the client of the locking 
 * utils.</p>
 * 
 * @author Emil Dinchev
 * 
 * @param <N> node type.
 */
public interface LockEvaluator<N> {

	/**
	 * Lock entry presents a node which has to be locked exclusively or not.
	 * @param <N> node type
	 */
	public final class LockEntry<N> {
		private final N node;
		private final boolean needExclusive;
		
		/**
		 * Creates new lock entry.
		 * @param node 
		 * @param needExclusive
		 */
		public LockEntry(final N node, final boolean needExclusive) {
			this.node = node;
			this.needExclusive = needExclusive;
		}
		
		public N getNode() {
			return node;
		}
		
		public boolean isNeedExclusive() {
			return needExclusive;
		}	
	}
	
	/**
	 * Evaluate the need for locks.
	 * @return collection of lock entries, describing the needed locks.
	 */
	Collection<LockEntry<N>> evaluate();
	
	/**
	 * Returns the root node for which we need to perform locking.
	 * @return the root node.
	 */
	N getRoot();
	
	/**
	 * @return the timeout in milliseconds inside which the locks has to be 
	 * acquired.
	 */
	long getTimeout();

	/**
	 * @return the operation for which we need the lock.
	 */
	String getOperation();
	
	EnqueueLock getEnqueueLock();
}