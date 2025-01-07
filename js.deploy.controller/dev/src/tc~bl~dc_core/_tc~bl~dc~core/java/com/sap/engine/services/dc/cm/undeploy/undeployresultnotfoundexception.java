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
package com.sap.engine.services.dc.cm.undeploy;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class UndeployResultNotFoundException extends CMException {

	static final long serialVersionUID = -7531797417329832659L;

	public UndeployResultNotFoundException(String errMessage) {
		super(errMessage);
	}

	public UndeployResultNotFoundException(String errMessage,
			Throwable throwable) {
		super(errMessage, throwable);
	}

}
