package com.sap.engine.services.dc.cm;

import com.sap.engine.services.dc.util.exception.DCRemoteException;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-1
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public class CMException extends DCRemoteException {

	private static final long serialVersionUID = -5326327445435538427L;

	public CMException(String errMessage) {
		super(errMessage);
	}

	public CMException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

	public CMException(String messageID, String message) {
		super(messageID, message);
	}

	public CMException(String messageID, String message, Object[] args) {
		super(messageID, message, args);
	}

	public CMException(String messageID, String message, Throwable cause) {
		super(messageID, message, cause);
	}

	public CMException(String messageID, String message, Throwable cause,
			Object[] args) {
		super(messageID, message, cause, args);
	}

	public CMException(String messageID, String message, String dcName,
			String csnComponent) {
		super(messageID, message, dcName, csnComponent);
	}

	public CMException(String messageID, String message, String dcName,
			String csnComponent, Object[] args) {
		super(messageID, message, dcName, csnComponent, args);
	}

	public CMException(String messageID, String message, String dcName,
			String csnComponent, Throwable cause) {
		super(messageID, message, dcName, csnComponent, cause);
	}

	public CMException(String messageID, String message, String dcName,
			String csnComponent, Throwable cause, Object[] args) {
		super(messageID, message, dcName, csnComponent, cause, args);
	}
}
