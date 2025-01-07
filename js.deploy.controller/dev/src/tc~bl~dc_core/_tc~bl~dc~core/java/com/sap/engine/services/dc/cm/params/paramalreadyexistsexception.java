package com.sap.engine.services.dc.cm.params;

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
public class ParamAlreadyExistsException extends ParamsException {

	private static final long serialVersionUID = 5862095449263893529L;

	public ParamAlreadyExistsException(String errMessage) {
		super(errMessage);
	}

	public ParamAlreadyExistsException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
