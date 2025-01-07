/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.converter.impl;

/**
 * This exception is thrown by EJBConvertor to show that it cannot convert the descriptors
 * found in conversion context becouse it cannot recognize the the version of the ejb-jar
 * descriptor.
 * @author Hristo Sabev
 */
public class UnknownVersionException extends Exception {

	/**
	 *
	 */
	public UnknownVersionException() {
		super();
	}
	/**
	 * 
	 */
	public UnknownVersionException(String message) {
		super(message);
	}
	/**
	 * 
	 */
	public UnknownVersionException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 *
	 */
	public UnknownVersionException(Throwable cause) {
		super(cause);
	}
}
