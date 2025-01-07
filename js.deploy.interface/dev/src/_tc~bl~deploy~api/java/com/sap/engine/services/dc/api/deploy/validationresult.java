/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.deploy;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Keeps result of the validation transaction.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-16</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor#validate(DeployItem[])
 */
public interface ValidationResult {
	/**
	 * Returns the validation status value.
	 * 
	 * @return validation status.
	 * @see ValidationStatus
	 */
	public ValidationStatus getValidationStatus();

	/**
	 * Returns true if there is a deployItem for offline deploy, otherwise false
	 * 
	 * @return true if there is a deployItem for offline deploy, otherwise false
	 */
	public boolean isOfflinePhaseScheduled();

	/**
	 * Returns the sorted deployment batch items.
	 * 
	 * @return array with DeployItems which are sorted in order to deploy
	 */
	public DeployItem[] getSortedDeploymentBatchItems();

	/**
	 * Returns the deployment batch items.
	 * 
	 * @return array with the given for validating deployItems
	 */
	public DeployItem[] getDeploymentBatchItems();

}