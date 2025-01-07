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
package com.sap.engine.services.dc.util.exception;

import java.rmi.RemoteException;
import java.text.MessageFormat;



/**
 * Was used to replace all inner exceptions with java.rmi.RemoteException in
 * order to prevent java.lang.ClassNotFoundException, caused from missing class
 * file.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DCRemoteException extends RemoteException {

	private static final long serialVersionUID = 1559148348590177397L;
	
	private static final String defaultMessageID = "ASJ.dpl_dc.000001";
	private static final String defaultDcName = "Deploy Controller";

	private String messageID = null;
	private String dcName = null;
	private String csnComponent = null;
	private String exceptionMessage = null;

	public DCRemoteException(String message) {
		super(message);
		setDefaults();
	}

	public DCRemoteException(String message, Throwable cause) {
		super(message, cause);
		setDefaults();
	}

	public DCRemoteException(String messageID, String message) {
		this(messageID, message, null, null, (Object[]) null);
	}

	public DCRemoteException(String messageID, String message, Object[] args) {
		this(messageID, message, null, null, args);
	}

	public DCRemoteException(String messageID, String message, String dcName,
			String csnComponent) {
		this(messageID, message, dcName, csnComponent, (Object[]) null);
	}

	public DCRemoteException(String messageID, String message, String dcName,
			String csnComponent, Object[] args) {
		super(
				buildExceptionText(messageID, message, dcName, csnComponent,
						args));
		init(messageID, message, dcName, csnComponent, args);
	}

	public DCRemoteException(String messageID, String message, Throwable cause) {
		this(messageID, message, null, null, cause, null);
	}

	public DCRemoteException(String messageID, String message, Throwable cause,
			Object[] args) {
		this(messageID, message, null, null, cause, args);
	}

	public DCRemoteException(String messageID, String message, String dcName,
			String csnComponent, Throwable cause) {
		this(messageID, message, dcName, csnComponent, cause, null);
	}

	public DCRemoteException(String messageID, String message, String dcName,
			String csnComponent, Throwable cause, Object[] args) {
		super(
				buildExceptionText(messageID, message, dcName, csnComponent,
						args), cause);
		init(messageID, message, dcName, csnComponent, args);
	}

	private void init(String messageID, String message, String dcName,
			String csnComponent, Object[] args) {
		this.messageID = messageID;
		this.exceptionMessage = buildExceptionMessage(message, dcName,
				csnComponent, args);
		this.dcName = dcName;
		this.csnComponent = csnComponent;
	}

	public String getMessageID() {
		return this.messageID;
	}

	public String getExceptionMessage() {
		return this.exceptionMessage;
	}

	public String getExceptionText() {
		return this.messageID + " " + this.exceptionMessage;
	}

	public String getDcName() {
		return this.dcName;
	}

	public String getCsnComponent() {
		return this.csnComponent;
	}

	private static String buildExceptionMessage(String message, String dcName,
			String csnComponent, Object[] args) {
		if (args != null) {
			try {
				message = MessageFormat.format(message, args);
			} catch (IllegalArgumentException e) {
				message = e.toString() + ", where message=[" + message
						+ "] and args=[" + args + "].";
			}
		}

		String failedIn = "";
		if (dcName != null && csnComponent != null) {
			failedIn = "(Failed in component: " + dcName + ", " + csnComponent
					+ ") ";
		}

		return failedIn + " " + message;
	}

	private static String buildExceptionText(String messageID, String message,
			String dcName, String csnComponent, Object[] args) {
		return messageID + " "
				+ buildExceptionMessage(message, dcName, csnComponent, args);
	}

	public DCRemoteException setMessageID(String messageID) {
		if (messageID == null) {
			throw new NullPointerException(
					"The Message ID is set to null. Hint:\n 1)Search in the stack trace for the component causing this exception and file a bug report to the caller.");
		} else {
			this.messageID = messageID;
		}
		return this;
	}


	public DCRemoteException setDcName(String dcName) {
		if (dcName == null) {
			throw new NullPointerException("The DC name is set to null. Hint:\n 1)Search in the stack trace for the component causing this exception and file a bug report to the caller.");
		} else {
			this.dcName = dcName;
		}
		return this;
	}
			
	private void setDefaults() {
		this.messageID = defaultMessageID;
		this.messageID = defaultDcName;
	}

}
