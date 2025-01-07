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
package com.sap.engine.services.dc.cmd.telnet.impl;

import com.sap.engine.services.dc.cmd.CMDException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class WrongParameterException extends CMDException {

	private static final long serialVersionUID = 2123222704051601227L;

	public WrongParameterException(String patternKey) {
		super(patternKey);
	}

	public WrongParameterException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public WrongParameterException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public WrongParameterException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
