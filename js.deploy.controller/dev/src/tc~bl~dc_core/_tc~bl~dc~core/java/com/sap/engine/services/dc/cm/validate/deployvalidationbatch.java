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
package com.sap.engine.services.dc.cm.validate;

import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.utils.filters.BatchFilter;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public interface DeployValidationBatch extends ValidationBatch {
	public String[] getDeployItems();

	public ComponentVersionHandlingRule getComponentVersionHandlingRule();

	public void setComponentVersionHandlingRule(
			ComponentVersionHandlingRule componentVersionHandlingRule);

	public DeployWorkflowStrategy getDeployWorkflowStrategy();

	public void setDeployWorkflowStrategy(
			DeployWorkflowStrategy deployWorkflowStrategy);

	public BatchFilter[] getBatchFilters();
}
