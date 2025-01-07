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

import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.op.util.Status;

/**
 * SingleNodeLockEvaluator is used to lock single nodes. The required lock is
 * always exclusive.
 * @author Emil Dinchev
 */
public final class SingleNodeLockEvaluator extends AbstractLockEvaluator {

	/**
	 * Constructor.
	 * @param operation operation name (transaction type). Not null.
	 * @param root the target status of the root node. Not null.
	 * @param enqueueLockType enqueue lock type.
	 * @param timeout timeout to acquire the needed locks.
	 */
	public SingleNodeLockEvaluator(final String operation, 
		final Component root, final char enqueueLockType, final long timeout) {
		super(operation, Status.UNKNOWN, root, enqueueLockType, timeout);
	}

	@Override
	public Collection<LockEntry<Component>> evaluate() {
		final Collection<LockEntry<Component>> entries =
			new ArrayList<LockEntry<Component>>(1);
		entries.add(new LockEntry<Component>(
			getRoot(), true));
		return entries;
	}
}