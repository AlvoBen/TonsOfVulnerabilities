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
package com.sap.engine.services.dc.cm.lock;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DCAlreadyLockedException extends DCLockException {

	private static final long serialVersionUID = -6683358648691755768L;

	public DCAlreadyLockedException(String errMessage) {
		super(errMessage);
	}

	public DCAlreadyLockedException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
