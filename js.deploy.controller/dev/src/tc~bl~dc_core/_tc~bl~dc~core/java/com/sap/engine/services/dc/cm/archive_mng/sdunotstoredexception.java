package com.sap.engine.services.dc.cm.archive_mng;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2006 Company: SAP AG Date: 2006-8-22
 * 
 * @author Radoslav Ivanov
 * @version 1.0
 * @since 7.1
 * 
 */
public class SduNotStoredException extends ArchiveManagementException {

	private static final long serialVersionUID = 3866789597663735605L;

	public SduNotStoredException(String errMessage) {
		super(errMessage);
	}

	public SduNotStoredException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
