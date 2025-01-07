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
package com.sap.engine.services.dc.cm.deploy.impl.lock;

import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.DeployParallelismStrategy;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.util.lock.LockData;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 1.0
 * @since 7.1
 */
public interface DBILockEvaluator {

	public static final String CAN_BE_EXECUTED_IN_PARALLEL = "canBeExecutedInParallel";

	/**
	 * Evaluates the <code>LockData</code>.
	 * 
	 * @param sortedAdmittedDeploymentBatchItems
	 *            <code>Collection</code> with the sorted admitted
	 *            <code>DeploymentBatchItem</code> objects.
	 * @param sessionID
	 *            <code>String</code>
	 * @param deployParallelismStrategy
	 *            <code>DeployParallelismStrategy</code>
	 * @param deployWorkflowStrategy
	 *            <code>DeployWorkflowStrategy</code>
	 * @return <code>LockData</code> with information what and how to be locked
	 *         or null to lock all.
	 * @throws ValidationException
	 *             if any item is not valid.
	 */

	public LockData evaluateLockData(
			Collection<DeploymentBatchItem> sortedAdmittedDeploymentBatchItems,
			DeployParallelismStrategy deployParallelismStrategy,
			DeployWorkflowStrategy deployWorkflowStrategy)
			throws ValidationException;

}
