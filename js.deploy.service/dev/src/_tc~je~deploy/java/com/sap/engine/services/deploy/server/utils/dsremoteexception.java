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
package com.sap.engine.services.deploy.server.utils;

import java.rmi.RemoteException;

/**
 * Was used to replace all inner exceptions with java.rmi.RemoteException in
 * order to prevent java.lang.ClassNotFoundException, caused from missing class
 * file.
 * 
 * NOTE : Only the WarningException, ServerDeploymentException and
 * DeploymentException are not overwritten.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DSRemoteException extends RemoteException {

	private static final long serialVersionUID = -4196409394048763012L;

	public DSRemoteException(String message) {
		super(message);
	}

	public DSRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
