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
package com.sap.scheduler.spi;

import java.io.Serializable;

/**
 * Exception thrown by most JXBP methods in case there is a technical
 * problem.
 * 
 * @author Dirk Marwinski
 */
public class JXBPException extends Exception implements Serializable {

    static final long serialVersionUID = -5354899581050924919L;
    
	/**
	 * 
	 */
	public JXBPException() {
		super();
	}

	/**
	 * @param message 
	 */
	public JXBPException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param arg1
	 */
	public JXBPException(String message, Throwable arg1) {
		super(message, arg1);
	}

	/**
	 * @param arg0
	 */
	public JXBPException(Throwable arg0) {
		super(arg0);
	}

}
