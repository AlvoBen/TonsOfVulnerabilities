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

import com.sap.engine.services.dc.api.APIException;
import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The class <code>DeployException</code> is a form of
 * <code>APIException</code> that indicates Deploy exception. All deploy related
 * exceptions are subclasses of <code>DeployException</code>.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD></DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class DeployException extends APIException {
	private final DeployItem[] deploymentItems;
	private final DeployItem[] orderedDeploymentItems;

	/**
	 * Constructs a DeployException object.
	 * 
	 * @param orderedDeploymentItems
	 *            an array of ordered DeployItems in the batch
	 * @param deploymentItems
	 *            an array of DeployItems in the batch
	 * @param location
	 *            a Location object
	 * @param patternKey
	 *            a String key from
	 *            com.sap.engine.services.dc.api.util.exception
	 *            .resources.exceptions.properties
	 * @param parameters
	 *            a String array of parameters witch are substituted in the
	 *            exception message
	 */
	public DeployException(DeployItem[] orderedDeploymentItems,
			DeployItem[] deploymentItems, Location location, String patternKey,
			String[] parameters) {
		super(location, patternKey, parameters);
		this.orderedDeploymentItems = orderedDeploymentItems;
		this.deploymentItems = deploymentItems;
	}

	/**
	 * Constructs a DeployException object.
	 * 
	 * @param orderedDeploymentItems
	 *            an array of ordered DeployItems in the batch
	 * @param deploymentItems
	 *            an array of DeployItems in the batch
	 * @param location
	 *            a Location object
	 * @param patternKey
	 *            a String key from
	 *            com.sap.engine.services.dc.api.util.exception
	 *            .resources.exceptions.properties
	 * @param parameters
	 *            a String array of parameters witch are substituted in the
	 *            exception message
	 * @param cause
	 *            a Throwable object cause for the exception
	 */
	public DeployException(DeployItem[] orderedDeploymentItems,
			DeployItem[] deploymentItems, Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
		this.deploymentItems = deploymentItems;
		this.orderedDeploymentItems = orderedDeploymentItems;
	}

	/**
	 * Returns the deployment items in the batch.
	 * 
	 * @return deployment items
	 */
	public DeployItem[] getDeploymentItems() {
		return this.deploymentItems;
	}

	/**
	 * Returns the ordered deployment items in the batch.
	 * 
	 * @return ordered deployment items
	 */
	public DeployItem[] getOrderedDeploymentItems() {
		return this.orderedDeploymentItems;
	}
}