package com.sap.engine.services.dc.lcm;

import com.sap.engine.services.dc.util.exception.DCRemoteException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class LCMException extends DCRemoteException {

	private static final long serialVersionUID = 7583410954863995004L;

	public LCMException(String errMessage) {
		super(errMessage);
	}

	public LCMException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
