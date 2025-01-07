package com.sap.engine.services.dc.cm.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2006 Company: SAP AG Date: 2006-08-23
 * 
 * @author Radoslav Ivanov
 * @version 1.0
 * @since 7.0
 * 
 */
public class AllItemsAlreadyDeployedValidationException extends
		ValidationException {

	private static final long serialVersionUID = -7054316232702562136L;

	/**
	 * @param errMessage
	 */
	public AllItemsAlreadyDeployedValidationException(String errMessage) {
		super(errMessage);
	}

	/**
	 * @param errMessage
	 * @param throwable
	 */
	public AllItemsAlreadyDeployedValidationException(String errMessage,
			Throwable throwable) {
		super(errMessage, throwable);
	}

}
