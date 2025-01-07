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
package com.sap.engine.services.deploy.zdm;

import java.rmi.RemoteException;

/**
 * Can be thrown only if the update process cannot start and the state of the
 * instance before invoking the method and after throwing the exception is the
 * same
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public class DSRollingException extends RemoteException {

	private static final long serialVersionUID = -181314349045214750L;

	/**
	 * Constructs exception with the specified error message
	 * 
	 * @param errMessage
	 *            the error message
	 */
	public DSRollingException(String errMessage) {
		super(errMessage);
	}

	/**
	 * Constructs exception with the specified error message and nested
	 * <code>Throwable</code>
	 * 
	 * @param errMessage
	 *            the error message
	 * @param throwable
	 *            the nested <code>Throwable</code>
	 */
	public DSRollingException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
