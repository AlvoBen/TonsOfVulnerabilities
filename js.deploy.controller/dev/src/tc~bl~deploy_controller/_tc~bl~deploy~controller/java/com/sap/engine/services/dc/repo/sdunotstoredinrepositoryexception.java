package com.sap.engine.services.dc.repo;

/*
 * Created on 2006-8-22 by radoslav-i
 * Title:        Deploy Controller
 * Description:    
 * 
 * Copyright:    Copyright (c) 2006
 * Company:      SAP AG
 * Date:         2006-8-22
 * 
 * @author       Radoslav Ivanov
 * @version      1.0
 * @since        7.10
 *
 */
public class SduNotStoredInRepositoryException extends RepositoryException {

	private static final long serialVersionUID = -2227547428489032781L;

	public SduNotStoredInRepositoryException(String patternKey) {
		super(patternKey);
	}

	public SduNotStoredInRepositoryException(String patternKey,
			Object[] parameters) {
		super(patternKey, parameters);
	}

	public SduNotStoredInRepositoryException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public SduNotStoredInRepositoryException(String patternKey,
			Object[] parameters, Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
