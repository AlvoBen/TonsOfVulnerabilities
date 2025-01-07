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
package com.sap.engine.services.dc.cm.utils.filters;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class RemoteBatchFilterFactoryException extends CMException {

	static final long serialVersionUID = -3712325668874637885L;

	public RemoteBatchFilterFactoryException(String errMessage) {
		super(errMessage);
	}

	public RemoteBatchFilterFactoryException(String errMessage,
			Throwable throwable) {
		super(errMessage, throwable);
	}

}
