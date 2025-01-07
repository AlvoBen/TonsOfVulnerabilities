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
package com.sap.engine.services.dc.cmd;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class CMDException extends DCBaseException {

	private static final long serialVersionUID = 2168092833989781165L;

	public CMDException(String patternKey) {
		super(patternKey);
	}

	public CMDException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public CMDException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public CMDException(String patternKey, Object[] parameters, Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
