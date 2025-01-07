package com.sap.engine.services.dc.api.deploy;

import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The exception is risen during the deployment if all given components are
 * marked as AlreadyDeployed.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2006-8-23</DD>
 *</DL>
 * 
 * @author Radoslav Ivanov
 * @version 1.0
 * @since 7.1
 * 
 */
public class AllItemsAlreadyDeployedValidaionException extends
		ValidationException {

	/**
	 * Constructs an AllItemsAlreadyDeployedValidaionException object.
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
	public AllItemsAlreadyDeployedValidaionException(
			DeployItem[] orderedDeploymentItems, DeployItem[] deploymentItems,
			Location location, String patternKey, String[] parameters,
			Throwable cause) {
		super(orderedDeploymentItems, deploymentItems, location, patternKey,
				parameters, cause);
	}

}