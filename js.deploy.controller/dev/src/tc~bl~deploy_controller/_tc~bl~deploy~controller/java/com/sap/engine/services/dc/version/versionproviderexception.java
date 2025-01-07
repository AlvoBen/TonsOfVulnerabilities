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
package com.sap.engine.services.dc.version;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class VersionProviderException extends DCBaseException {

	private static final long serialVersionUID = 3428187225509888545L;

	public VersionProviderException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

}
