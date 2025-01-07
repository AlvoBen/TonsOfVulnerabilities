package com.sap.engine.services.dc.cm.params;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class ParamsException extends CMException {

	private static final long serialVersionUID = 2860333663756893153L;

	public ParamsException(String errMessage) {
		super(errMessage);
	}

	public ParamsException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
