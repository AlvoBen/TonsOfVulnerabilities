package com.sap.engine.services.dc.cm.security.authorize;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-6
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class AuthorizationException extends CMException {

	private static final long serialVersionUID = -3822510665989579261L;

	public AuthorizationException(String errMessage) {
		super(errMessage);
	}

	public AuthorizationException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
