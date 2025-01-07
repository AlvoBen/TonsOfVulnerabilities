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
package com.sap.engine.services.dc.api.validate;

import com.sap.engine.services.dc.api.deploy.ValidationStatus;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Keeps result of the validate transaction.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2007</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2007-11-13</DD>
 * </DL>
 * 
 * @author Todor Atanasov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.validate.ValidateProcessor#validate(Batch[]
 *      batchList)
 */
public interface ValidationResultSet {
	/**
	 * The operation returns the overall status of validation
	 * 
	 * @return validation status.
	 * @see ValidationStatus
	 */
	public ValidationStatus getValidationStatus();

	/**
	 * The operation returns ordered List of
	 * <code>DeployValidationBatchResult</code> or
	 * <code>UndeployValidationBatchResult</code> for every batch, depending of
	 * batch items.
	 * 
	 * @return <code>ValidationBatchResult[]</code> An array with validation
	 *         batch results.
	 */
	public ValidationResult[] getBatchResults();

	/**
	 * Retrieve description for the result.
	 * 
	 * @return Text message
	 */
	public String getDescription();
}
