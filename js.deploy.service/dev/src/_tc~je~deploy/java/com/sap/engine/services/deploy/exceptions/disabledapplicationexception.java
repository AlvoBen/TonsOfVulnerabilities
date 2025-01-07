/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.exceptions;

import com.sap.engine.services.deploy.server.ExceptionConstants;

/* This class belongs to the public API of the DeployService project. */
/**
 * This class provides an exception that is thrown when application is disabled
 * in filters and we attempt to start it.
 * 
 * @author Anton Georgiev
 */
public class DisabledApplicationException extends ServerDeploymentException {

	private static final long serialVersionUID = 2470105643463614652L;

	/**
	 * Constructs exception with the specified message.
	 * 
	 * @param applicationName
	 *            the application that we attempt to start
	 */
	public DisabledApplicationException(String applicationName, String message) {
		super(message, new Object[] { applicationName });
	}
	
}
