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
package com.sap.sdo.impl.util;

public class VisitorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2123205986512989934L;

	public VisitorException() {
		super();
	}

	public VisitorException(String message) {
		super(message);
	}

	public VisitorException(String message, Throwable cause) {
		super(message, cause);
	}

	public VisitorException(Throwable cause) {
		super(cause);
	}

}
