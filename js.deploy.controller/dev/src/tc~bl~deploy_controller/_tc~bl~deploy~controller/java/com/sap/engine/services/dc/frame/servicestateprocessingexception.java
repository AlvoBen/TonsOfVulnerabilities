package com.sap.engine.services.dc.frame;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-13
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class ServiceStateProcessingException extends DCBaseException {

	static final long serialVersionUID = 6241578919820860300L;

	public ServiceStateProcessingException(String patternKey) {
		super(patternKey);
	}

	public ServiceStateProcessingException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public ServiceStateProcessingException(String patternKey,
			Object[] parameters, Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
