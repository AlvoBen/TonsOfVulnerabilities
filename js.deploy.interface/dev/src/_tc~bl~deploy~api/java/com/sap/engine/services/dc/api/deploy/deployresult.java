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

import com.sap.engine.services.dc.api.util.measurement.DAMeasurement;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Describes DeployResult for single deploy action.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-5</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor#deploy(DeployItem[])
 */
public interface DeployResult {
	/**
	 * Returns the status of the deploy result.
	 * 
	 * @return status of the deploy result
	 * @see DeployResultStatus
	 */
	public DeployResultStatus getDeployResultStatus();

	/**
	 * Returns array with the items which are passed for deploy for this
	 * transaction.
	 * 
	 * @return array with the items which are passed for deploy for this
	 *         transaction
	 */
	public DeployItem[] getDeploymentItems();

	/**
	 * Returns array sorted array with admitted deployment items. If there is
	 * SCA, new fake deployItem is created for each contained SDAs.
	 * 
	 * @return array sorted array with admitted deployment items
	 */
	public DeployItem[] getSortedDeploymentItems();

	/**
	 * Returns deploy result description.
	 * 
	 * @return deploy result description
	 */
	public String getDescription();
	
	/**
	 * Returns deploy measurement
	 * 
	 * @return deploy measurement or <code>null<code> if there is no any
	 */
	public DAMeasurement getMeasurement();

	public String toString();

}