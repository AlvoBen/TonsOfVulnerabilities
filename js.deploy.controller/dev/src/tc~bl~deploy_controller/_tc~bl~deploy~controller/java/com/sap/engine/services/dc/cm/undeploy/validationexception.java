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
package com.sap.engine.services.dc.cm.undeploy;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ValidationException extends UndeploymentException {

	private static final long serialVersionUID = 2458702693494957369L;

	/**
	 * @param errMessage
	 */
	public ValidationException(String errMessage) {
		super(errMessage);
	}

	/**
	 * @param errMessage
	 * @param throwable
	 */
	public ValidationException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
