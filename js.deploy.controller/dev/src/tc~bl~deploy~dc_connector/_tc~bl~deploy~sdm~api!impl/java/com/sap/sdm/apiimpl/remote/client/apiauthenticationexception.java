package com.sap.sdm.apiimpl.remote.client;

import com.sap.sdm.api.remote.AuthenticationException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-6
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
class APIAuthenticationException extends AuthenticationException {

	/**
	 * @param errMessage
	 * @param throwable
	 */
	public APIAuthenticationException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

	/**
	 * @param errMessage
	 */
	public APIAuthenticationException(String errMessage) {
		super(errMessage);
	}

}
