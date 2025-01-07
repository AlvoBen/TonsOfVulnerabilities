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
package com.sap.engine.services.deploy.exceptions;

/* This class belongs to the public API of the DeployService project. */
/**
 * This exception is thrown by the Converter when it will not convert the EAR
 * because it does not need to be converted. Please use it <b>only</b> in this
 * particular case.
 * 
 * @author Georgi Danov
 * @version 6.30
 */

public class EarWontBeConvertedException extends BaseEarException {

	/**
	 * Constructs exception with the specified message.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 */
	public EarWontBeConvertedException(String s) {
		super(s);
	}

	/**
	 * Constructs exception with the specified message and nested
	 * <code>Throwable</code>.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 * @param t
	 *            the nested <code>Throwable</code>.
	 */
	public EarWontBeConvertedException(String s, Throwable t) {
		super(s, t);
	}

	/**
	 * Constructs exception with the specified message, parameters and nested
	 * <code>Throwable</code>.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 * @param args
	 *            the parameters.
	 * @param t
	 *            the nested <code>Throwable</code>.
	 */
	public EarWontBeConvertedException(String s, Object[] args, Throwable t) {
		super(s, args, t);
	}

	/**
	 * Constructs exception with the specified message and parameters.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 * @param args
	 *            the parameters.
	 */
	public EarWontBeConvertedException(String s, Object[] args) {
		super(s, args);
	}

}
