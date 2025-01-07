/*
 * Copyright (C) 2000 - 2005 by SAP AG, Walldorf,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cm.validate.impl;

import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy;
import com.sap.engine.services.dc.cm.validate.UndeployValidationBatch;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public class UndeployValidationBatchImpl implements UndeployValidationBatch {
	private static final long serialVersionUID = 1L;
	private GenericUndeployItem[] undeployItems;
	private UndeploymentStrategy undeploymentStrategy = UndeploymentStrategy.IF_DEPENDING_STOP;
	private UndeployWorkflowStrategy workflowStrategy = UndeployWorkflowStrategy.NORMAL;

	public UndeployValidationBatchImpl(GenericUndeployItem[] undeployItems) {
		this.undeployItems = undeployItems;
	}

	public GenericUndeployItem[] getUndeployItems() {
		return this.undeployItems;
	}

	public UndeploymentStrategy getUndeploymentStrategy() {
		return this.undeploymentStrategy;
	}

	public UndeployWorkflowStrategy getUndeployWorkflowStrategy() {
		return this.workflowStrategy;
	}

	public void setUndeploymentStrategy(
			UndeploymentStrategy undeploymentStrategy) {
		this.undeploymentStrategy = undeploymentStrategy;
	}

	public void setUndeployWorkflowStrategy(
			UndeployWorkflowStrategy undeployWorkflowStrategy) {
		this.workflowStrategy = undeployWorkflowStrategy;
	}
}
