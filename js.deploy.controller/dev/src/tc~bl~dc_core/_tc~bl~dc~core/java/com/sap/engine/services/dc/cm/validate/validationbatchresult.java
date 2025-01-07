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

import java.io.Serializable;

import com.sap.engine.services.dc.cm.deploy.ValidationStatus;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public interface ValidationBatchResult extends Serializable {
	public ValidationStatus getValidationStatus();

	public boolean isOfflinePhaseScheduled();
    
	public String getDescription();
}
