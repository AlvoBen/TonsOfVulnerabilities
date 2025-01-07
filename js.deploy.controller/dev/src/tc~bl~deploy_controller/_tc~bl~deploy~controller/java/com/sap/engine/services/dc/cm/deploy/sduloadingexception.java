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
public class SduLoadingException extends DeploymentException {

	private static final long serialVersionUID = 6987814078526835446L;

	public SduLoadingException(String errMessage) {
		super(errMessage);
	}

	public SduLoadingException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
