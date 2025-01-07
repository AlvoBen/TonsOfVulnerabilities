/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.validate.jlin;

import com.sap.engine.services.deploy.container.DeploymentException;

/**
 * 
 * 
 * @author anton-g
 * @version 7.1
 */
public class JLinExecException extends DeploymentException {
	private static final long serialVersionUID = -4017745828062662791L;

	public JLinExecException(String patternKey) {
		super(patternKey);
	}

	public JLinExecException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public JLinExecException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

	public JLinExecException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

}
