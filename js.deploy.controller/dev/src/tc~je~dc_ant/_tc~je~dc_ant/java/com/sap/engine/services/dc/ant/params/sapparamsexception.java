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
package com.sap.engine.services.dc.ant.params;

/**
 * 
 * This class represents an exception delivered to the output of the parameters
 * task
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public class SAPParamsException extends Exception {

	private static final long serialVersionUID = 3521501447425256606L;

	/**
	 * @param message
	 */
	public SAPParamsException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SAPParamsException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SAPParamsException(String message, Throwable cause) {
		super(message, cause);
	}
}
