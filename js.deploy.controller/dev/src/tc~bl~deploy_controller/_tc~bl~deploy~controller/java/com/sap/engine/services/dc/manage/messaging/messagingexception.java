package com.sap.engine.services.dc.manage.messaging;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-14
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class MessagingException extends Exception {

	public MessagingException(String errMessage) {
		super(errMessage);
	}

	public MessagingException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
