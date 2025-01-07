/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.injection;

/**
 * Indicates problems during injection execution
 *
 * @author  Vesselin Mitrov, vesselin.mitrov@sap.com
 * @version 7.10
 */
public class InjectionException extends Exception {

	/**
	 * Constructor
	 * 
	 * @param message the detail message
	 * @param cause the cause for this InjectionException
	 */
	public InjectionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructor
	 * 
	 * @param message the detail message
	 */
	public InjectionException(String message) {
		super(message);
	}
	
}