package com.sap.engine.services.dc.jstartup;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class JStartupException extends DCBaseException {

	private static final long serialVersionUID = 4562873546625705539L;

	public JStartupException(String patternKey) {
		super(patternKey);
	}

	public JStartupException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public JStartupException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
