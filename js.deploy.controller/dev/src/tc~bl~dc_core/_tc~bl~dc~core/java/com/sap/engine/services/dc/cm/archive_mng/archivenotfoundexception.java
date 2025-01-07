package com.sap.engine.services.dc.cm.archive_mng;

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
public class ArchiveNotFoundException extends ArchiveManagementException {

	private static final long serialVersionUID = -4833337512807892824L;

	public ArchiveNotFoundException(String errMessage) {
		super(errMessage);
	}

	public ArchiveNotFoundException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
