/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.licensing;

import java.util.Vector;

import com.sap.exception.BaseException;

/**
 * @author Jochen Mueller
 * @version 1.1
 */
public class LicensingException extends BaseException {
	Vector errorMessage = new Vector();

	/**
	 * Constructor
	 * @param errorMessage
	 */
	public LicensingException(Vector errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Returns an error message
	 * @return Vector
	 */
	public Vector getErrorMessage() {
		return errorMessage;
	}
}
