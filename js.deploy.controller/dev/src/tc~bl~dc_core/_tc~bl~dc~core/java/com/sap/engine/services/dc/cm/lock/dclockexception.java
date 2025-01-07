package com.sap.engine.services.dc.cm.lock;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-6
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class DCLockException extends CMException {

	private static final long serialVersionUID = 2127607950070741019L;

	public DCLockException(String errMessage) {
		super(errMessage);
	}

	public DCLockException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
