package com.sap.engine.services.dc.repo.migration;

import com.sap.engine.services.dc.util.exception.DCRemoteException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-2
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class MigrationException extends DCRemoteException {

	public MigrationException(String errMessage) {
		super(errMessage);
	}

	public MigrationException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
