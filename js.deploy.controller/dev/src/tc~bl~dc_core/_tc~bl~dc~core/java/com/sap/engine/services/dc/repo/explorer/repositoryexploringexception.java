package com.sap.engine.services.dc.repo.explorer;

import com.sap.engine.services.dc.util.exception.DCRemoteException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class RepositoryExploringException extends DCRemoteException {

	public RepositoryExploringException(String errMessage) {
		super(errMessage);
	}

	public RepositoryExploringException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
