package com.sap.engine.services.dc.repo;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-15
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public class RepositoryException extends DCBaseException {

	private static final long serialVersionUID = -6874707147124325037L;

	public RepositoryException(String patternKey) {
		super(patternKey);
	}

	public RepositoryException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public RepositoryException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public RepositoryException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
