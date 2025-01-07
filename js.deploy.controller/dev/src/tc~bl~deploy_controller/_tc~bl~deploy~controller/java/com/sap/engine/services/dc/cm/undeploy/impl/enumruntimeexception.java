package com.sap.engine.services.dc.cm.undeploy.impl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-6-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
class EnumRuntimeException extends RuntimeException {
	// private static final long serialVersionUID = -1027375966496651187L;

	public EnumRuntimeException(String message) {
		super(message);
	}

	public EnumRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}