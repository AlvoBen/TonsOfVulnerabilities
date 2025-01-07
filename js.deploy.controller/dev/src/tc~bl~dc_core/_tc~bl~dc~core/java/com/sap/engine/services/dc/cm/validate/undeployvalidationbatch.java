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

import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public interface UndeployValidationBatch extends ValidationBatch {
	public GenericUndeployItem[] getUndeployItems();

	public UndeploymentStrategy getUndeploymentStrategy();

	public UndeployWorkflowStrategy getUndeployWorkflowStrategy();

	public void setUndeploymentStrategy(
			UndeploymentStrategy undeploymentStrategy);

	public void setUndeployWorkflowStrategy(
			UndeployWorkflowStrategy undeployWorkflowStrategy);
}
