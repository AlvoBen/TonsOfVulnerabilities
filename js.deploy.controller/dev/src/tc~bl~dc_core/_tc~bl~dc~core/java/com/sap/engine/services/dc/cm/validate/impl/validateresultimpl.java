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

import com.sap.engine.services.dc.cm.deploy.ValidationStatus;
import com.sap.engine.services.dc.cm.validate.ValidateResult;
import com.sap.engine.services.dc.cm.validate.ValidationBatchResult;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public class ValidateResultImpl implements ValidateResult {
	private static final long serialVersionUID = 1L;
	private final ValidationBatchResult[] batchResults;
	private ValidationStatus validationStatus;

	ValidateResultImpl(ValidationBatchResult[] batchResults) {
		this.batchResults = batchResults;
		this.validationStatus = ValidationStatus.SUCCESS;
		for (int i = 0; i < batchResults.length; ++i) {
			if (batchResults[i].getValidationStatus().equals(
					ValidationStatus.ERROR)) {
				this.validationStatus = ValidationStatus.ERROR;
				break;
			}
		}
	}

	public ValidationStatus getValidationStatus() {
		return validationStatus;
	}

	public ValidationBatchResult[] getBatchResults() {
		return batchResults;
	}

	public String getDescription() {
		return "";
	}
}
