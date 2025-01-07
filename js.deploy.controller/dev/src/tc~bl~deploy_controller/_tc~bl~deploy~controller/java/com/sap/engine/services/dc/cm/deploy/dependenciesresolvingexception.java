package com.sap.engine.services.dc.cm.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-1-21
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class DependenciesResolvingException extends DeploymentException {

	private static final long serialVersionUID = -5618845226867923066L;

	public DependenciesResolvingException(String errMessage) {
		super(errMessage);
	}

	public DependenciesResolvingException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
