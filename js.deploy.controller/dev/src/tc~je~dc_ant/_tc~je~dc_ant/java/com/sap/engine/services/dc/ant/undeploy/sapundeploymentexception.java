/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.ant.undeploy;

/**
 * 
 * This class represents an undeployment exception delivered to the output of th
 * undeploy task.
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public class SAPUndeploymentException extends Exception {

	private static final long serialVersionUID = 4533041896109277347L;

	/**
	 * @param message
	 */
	public SAPUndeploymentException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SAPUndeploymentException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SAPUndeploymentException(String message, Throwable cause) {
		super(message, cause);
	}
}
