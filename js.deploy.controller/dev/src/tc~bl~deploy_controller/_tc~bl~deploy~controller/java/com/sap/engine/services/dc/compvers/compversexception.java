package com.sap.engine.services.dc.compvers;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class CompVersException extends DCBaseException {

	private static final long serialVersionUID = 3428187225509888545L;

	public CompVersException(String patternKey) {
		super(patternKey);
	}

	public CompVersException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public CompVersException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public CompVersException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
