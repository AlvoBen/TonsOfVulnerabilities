package com.sap.engine.services.dc.cm.deploy;

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
public class DeploymentObserverException extends DeploymentException {

	private static final long serialVersionUID = -367147509019352206L;

	public DeploymentObserverException(String errMessage) {
		super(errMessage);
	}

	public DeploymentObserverException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}
}
