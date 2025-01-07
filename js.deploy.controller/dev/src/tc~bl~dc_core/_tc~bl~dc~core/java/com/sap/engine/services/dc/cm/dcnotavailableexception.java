/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Feb 22, 2006
 */
package com.sap.engine.services.dc.cm;

/**
 * 
 * Title: J2EE Deployment Team Description: Marks the case when the DC is binded
 * and working but is performing un/deployment after the ofline phase or the
 * repository is still initializing
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-6
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.10
 * 
 */
public class DCNotAvailableException extends CMException {

	private static final long serialVersionUID = -6033328446311379195L;

	public DCNotAvailableException(String errMessage) {
		super(errMessage);
	}

	public DCNotAvailableException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
