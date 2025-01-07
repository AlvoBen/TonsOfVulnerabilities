package com.sap.engine.services.dc.cm.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2006-4-27
 * 
 * @author Radoslav Ivanov
 * @version 1.0
 * @since 7.1
 * 
 */
public class AllItemsFilteredValidaionException extends ValidationException {

	private static final long serialVersionUID = -8063088653987800820L;

	/**
	 * @param errMessage
	 */
	public AllItemsFilteredValidaionException(String errMessage) {
		super(errMessage);
	}

	/**
	 * @param errMessage
	 * @param throwable
	 */
	public AllItemsFilteredValidaionException(String errMessage,
			Throwable throwable) {
		super(errMessage, throwable);
	}

}
