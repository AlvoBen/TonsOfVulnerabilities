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
package com.sap.engine.services.dc.cm.session_id;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class SessionIDException extends DCBaseException {

	private static final long serialVersionUID = 7713413534779020147L;

	public SessionIDException(String patternKey) {
		super(patternKey);
	}

	public SessionIDException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public SessionIDException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public SessionIDException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
