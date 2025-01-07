package com.sap.engine.services.dc.cm.deploy.impl.sorters;

import com.sap.engine.services.dc.cm.deploy.DeploymentException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class SortException extends DeploymentException {

	private static final long serialVersionUID = -4777866132253374845L;

	public SortException(String errMessage) {
		super(errMessage);
	}

	public SortException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
