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

import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>This exception indicates that the result for the deploy operation was not
 * found.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-29</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class DeployResultNotFoundException extends DeployException {
	/**
	 * Constructs a DeployResultNotFoundException object.
	 * 
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
	public DeployResultNotFoundException(DeployItem[] deploymentItems,
			Location location, String patternKey, String[] parameters) {
		super(null, deploymentItems, location, patternKey, parameters);
	}

	/**
	 * Constructs a DeployResultNotFoundException object.
	 * 
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
	public DeployResultNotFoundException(DeployItem[] deploymentItems,
			Location location, String patternKey, String[] parameters,
			Throwable cause) {
		super(null, deploymentItems, location, patternKey, parameters, cause);
	}
}