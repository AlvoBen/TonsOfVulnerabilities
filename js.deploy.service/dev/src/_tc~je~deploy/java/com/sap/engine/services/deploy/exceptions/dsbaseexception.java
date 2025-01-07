/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.exceptions;

import com.sap.engine.services.deploy.DeployResourceAccessor;
import com.sap.exception.BaseException;

/* This class belongs to the public API of the DeployService project. */
/**
 * Local universal implementation of com.sap.exception.BaseException.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
// TODO BaseException to be replaced by Exception
public class DSBaseException extends BaseException {

	/**
	 * Constructs exception with the given pattern key
	 * 
	 * @param patternKey
	 *            error key code - it is taken from ExceptionConstants
	 */
	public DSBaseException(String patternKey) {
		this(patternKey, null, null);
	}

	/**
	 * Constructs exception with the given pattern key and
	 * <code>Throwable</code> cause
	 * 
	 * @param patternKey
	 *            error key code - it is taken from ExceptionConstants
	 * @param cause
	 *            <code>Throwable</code>
	 */
	public DSBaseException(String patternKey, Throwable cause) {
		this(patternKey, null, cause);
	}

	/**
	 * Constructs exception with the given pattern key and array of parameters
	 * 
	 * @param patternKey
	 *            error key code - it is taken from ExceptionConstants
	 * @param parameters
	 *            error message parameters
	 */
	public DSBaseException(String patternKey, Object[] parameters) {
		this(patternKey, parameters, null);
	}

	/**
	 * Constructs exception with the given pattern key, array of parameters and
	 * <code>Throwable</code> cause
	 * 
	 * @param patternKey
	 *            error key code - it is taken from ExceptionConstants
	 * @param parameters
	 *            error message parameters
	 * @param cause
	 *            <code>Throwable</code>
	 */
	public DSBaseException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(null, DeployResourceAccessor.getResourceAccessor(), patternKey,
				parameters, cause);
	}

}
