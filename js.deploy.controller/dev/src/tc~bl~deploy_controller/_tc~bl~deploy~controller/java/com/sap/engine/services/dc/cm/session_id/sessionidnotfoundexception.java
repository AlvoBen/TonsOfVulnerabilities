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

/**
 * @author Ivan Mihalev
 */

public class SessionIDNotFoundException extends SessionIDException {

	private static final long serialVersionUID = -7900005123130802492L;

	public SessionIDNotFoundException(String patternKey) {
		super(patternKey);
	}

	public SessionIDNotFoundException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public SessionIDNotFoundException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public SessionIDNotFoundException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
