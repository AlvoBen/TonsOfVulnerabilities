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

import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.ValidationResult;
import com.sap.engine.services.dc.cm.deploy.ValidationStatus;
import com.sap.engine.services.dc.cm.deploy.impl.ValidationResultImpl;
import com.sap.engine.services.dc.cm.validate.DeployValidationBatchResult;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public class DeployValidationBatchResultImpl extends ValidationResultImpl
		implements DeployValidationBatchResult {
	private static final long serialVersionUID = 1L;

    private final String description;

	DeployValidationBatchResultImpl(ValidationStatus validationStatus,
        boolean offlinePhaseScheduled, Collection sortedDeploymentBatchItems,
        Collection deploymentBatchItems, String description)
    {
		super(validationStatus, offlinePhaseScheduled,
				sortedDeploymentBatchItems, deploymentBatchItems);
        this.description = description == null ? "" : description; 
    }

    DeployValidationBatchResultImpl(ValidationResult validationResult, String description)
    {
        this(validationResult.getValidationStatus(),
            validationResult.isOfflinePhaseScheduled(),
            validationResult.getSortedDeploymentBatchItems(),
            validationResult.getDeploymentBatchItems(), description);
	}

	public String getDescription() {
		return this.description;
	}
}
