package com.sap.engine.services.dc.cm.undeploy.impl.sorters;

import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-30
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class SortException extends UndeploymentException {

	private static final long serialVersionUID = -1303057523980622846L;

	public SortException(String errMessage) {
		super(errMessage);
	}

	public SortException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
