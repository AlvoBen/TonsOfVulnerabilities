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

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class CMDRegisterException extends CMDException {

	private static final long serialVersionUID = 3727412100192583607L;

	public CMDRegisterException(String patternKey) {
		super(patternKey);
	}

	public CMDRegisterException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public CMDRegisterException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public CMDRegisterException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
