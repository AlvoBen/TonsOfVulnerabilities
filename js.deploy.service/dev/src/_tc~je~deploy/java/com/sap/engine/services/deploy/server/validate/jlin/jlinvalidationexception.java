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
 * @author Anton Georgiev
 * @version 7.1
 */
public class JLinValidationException extends DeploymentException {

	public JLinValidationException(String patternKey) {
		super(patternKey);
	}

	public JLinValidationException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public JLinValidationException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

	public JLinValidationException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

}
