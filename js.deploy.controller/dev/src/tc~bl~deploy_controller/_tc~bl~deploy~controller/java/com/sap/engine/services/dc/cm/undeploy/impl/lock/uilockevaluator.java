/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cm.undeploy.impl.lock;

import java.util.Collection;

import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployParallelismStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.undeploy.ValidationException;
import com.sap.engine.services.dc.util.lock.LockData;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 1.0
 * @since 7.1
 */
public interface UILockEvaluator {

	/**
	 * Evaluates the <code>LockData</code>.
	 * 
	 * @param sortedUndeployItems
	 *            <code>Collection</code>
	 * @param sessionID
	 *            <code>String</code>
	 * @param undeployParallelismStrategy
	 *            <code>UndeployParallelismStrategy</code>
	 * @param undeployWorkflowStrategy
	 *            <code>UndeployWorkflowStrategy</code>>
	 * @return <code>LockData</code> with information what and how to be locked
	 *         or null to lock all.
	 * @throws ValidationException
	 *             if any item is not valid.
	 */
	public LockData evaluateLockData(
			Collection<GenericUndeployItem> sortedUndeployItems, String sessionID,
			UndeployParallelismStrategy undeployParallelismStrategy,
			UndeployWorkflowStrategy undeployWorkflowStrategy)
			throws ValidationException;

}
