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

import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.utils.filters.BatchFilter;
import com.sap.engine.services.dc.cm.validate.DeployValidationBatch;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public class DeployValidationBatchImpl implements DeployValidationBatch {
	private static final long serialVersionUID = 1L;
	private String[] deployItemPaths;
	private ComponentVersionHandlingRule componentVersionHandlingRule = ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY;
	private DeployWorkflowStrategy deployWorkflowStrategy = DeployWorkflowStrategy.NORMAL;
	private BatchFilter[] batchFilters;

	public DeployValidationBatchImpl(String[] deployItemPaths,
			BatchFilter[] batchFilters) {
		this.deployItemPaths = deployItemPaths;
		this.batchFilters = batchFilters;
	}

	public String[] getDeployItems() {
		return this.deployItemPaths;
	}

	public ComponentVersionHandlingRule getComponentVersionHandlingRule() {
		return this.componentVersionHandlingRule;
	}

	public BatchFilter[] getBatchFilters() {
		return this.batchFilters;
	}

	public DeployWorkflowStrategy getDeployWorkflowStrategy() {
		return this.deployWorkflowStrategy;
	}

	public void setComponentVersionHandlingRule(
			ComponentVersionHandlingRule componentVersionHandlingRule) {
		this.componentVersionHandlingRule = componentVersionHandlingRule;
	}

	public void setDeployWorkflowStrategy(
			DeployWorkflowStrategy deployWorkflowStrategy) {
		this.deployWorkflowStrategy = deployWorkflowStrategy;
	}
}
