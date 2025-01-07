package com.sap.engine.services.dc.cm.archive_mng;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-19
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class ArchiveManagementException extends CMException {

	private static final long serialVersionUID = -2105250188588212986L;

	public ArchiveManagementException(String errMessage) {
		super(errMessage);
	}

	public ArchiveManagementException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
