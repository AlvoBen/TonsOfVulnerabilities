package com.sap.engine.services.dc.ant.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-9
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */
class SAPDeploymentException extends Exception {

	private static final long serialVersionUID = -1052497812748686245L;

	/**
	 * @param message
	 */
	public SAPDeploymentException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SAPDeploymentException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SAPDeploymentException(String message, Throwable cause) {
		super(message, cause);
	}
}
