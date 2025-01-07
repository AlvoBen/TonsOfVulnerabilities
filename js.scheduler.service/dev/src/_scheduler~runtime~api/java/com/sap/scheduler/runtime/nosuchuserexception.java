/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.runtime;


/**
 * This exception is thrown if the specified user for a runAs
 * execution request does not exist.
 * 
 */
public class NoSuchUserException extends Exception {

	public NoSuchUserException() {
		super();
	}

	public NoSuchUserException(String arg0) {
		super(arg0);
	}

}
