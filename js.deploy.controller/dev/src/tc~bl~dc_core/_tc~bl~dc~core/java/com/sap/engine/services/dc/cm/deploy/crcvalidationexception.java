package com.sap.engine.services.dc.cm.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-08
 * 
 * @author Radoslav Ivanov
 * @version 1.0
 * @since 7.0
 * 
 */
public class CrcValidationException extends ValidationException {

	private static final long serialVersionUID = -2795216957926475601L;

	/**
	 * @param errMessage
	 */
	public CrcValidationException(String errMessage) {
		super(errMessage);
	}

	/**
	 * @param errMessage
	 * @param throwable
	 */
	public CrcValidationException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
