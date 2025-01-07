package com.sap.engine.services.dc.api.deploy;

import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>The exception is rised when Validation phase fails.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-1-25</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class ValidationException extends DeployException {

	/**
	 * Constructs a ValidationException object.
	 * 
	 * @param orderedDeploymentItems
	 *            ordered list with all admitted deployment items
	 * @param deploymentItems
	 *            list with all passed for deployment items
	 * @param location
	 *            for logging purposes
	 * @param patternKey
	 *            message key
	 * @param parameters
	 *            message parameres
	 * @param cause
	 *            for the exception
	 */
	public ValidationException(DeployItem[] orderedDeploymentItems,
			DeployItem[] deploymentItems, Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(orderedDeploymentItems, deploymentItems, location, patternKey,
				parameters, cause);
	}

}