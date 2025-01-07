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
package com.sap.engine.services.dc.cm.params;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class ParamsFactoryException extends DCBaseException {

	public ParamsFactoryException(String patternKey) {
		super(patternKey);
	}

	public ParamsFactoryException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public ParamsFactoryException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public ParamsFactoryException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
