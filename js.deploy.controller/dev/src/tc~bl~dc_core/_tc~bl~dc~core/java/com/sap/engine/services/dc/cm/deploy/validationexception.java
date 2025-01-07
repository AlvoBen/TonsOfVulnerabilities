package com.sap.engine.services.dc.cm.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-1-21
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class ValidationException extends DeploymentException {

	private static final long serialVersionUID = 7438708699498957366L;

	/**
	 * @param errMessage
	 */
	public ValidationException(String errMessage) {
		super(errMessage);
	}

	/**
	 * @param errMessage
	 * @param throwable
	 */
	public ValidationException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
