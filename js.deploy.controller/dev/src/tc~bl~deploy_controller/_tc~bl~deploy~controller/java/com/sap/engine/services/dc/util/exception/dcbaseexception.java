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
package com.sap.engine.services.dc.util.exception;

import com.sap.exception.BaseException;

/**
 * Local universal implementation of com.sap.exception.BaseException.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DCBaseException extends BaseException {

	private static final long serialVersionUID = 4604130220622569538L;

	public DCBaseException(String patternKey) {
		this(patternKey, null, null);
	}

	public DCBaseException(String patternKey, Throwable cause) {
		this(patternKey, null, cause);
	}

	public DCBaseException(String patternKey, Object[] parameters) {
		this(patternKey, parameters, null);
	}

	public DCBaseException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(null, DCResourceAccessor.getInstance(), patternKey, parameters,
				cause);
	}

}
