package com.sap.engine.services.dc.cm.undeploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class UndeploymentObserverException extends UndeploymentException {

	private static final long serialVersionUID = 4306029214985232452L;

	public UndeploymentObserverException(String errMessage) {
		super(errMessage);
	}

	public UndeploymentObserverException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}
}
