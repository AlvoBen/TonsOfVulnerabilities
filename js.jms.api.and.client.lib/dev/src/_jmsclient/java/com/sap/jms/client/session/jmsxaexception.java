package com.sap.jms.client.session;

import javax.transaction.xa.XAException;

public class JMSXAException extends XAException {

	private static final long serialVersionUID = 7107636794359988725L;
	
	public JMSXAException(String message) {
		super(message);
	}

	public JMSXAException(String message, Throwable cause) {  
		super(message);
		super.initCause(cause);
	}

	public JMSXAException(Throwable cause) {
		this("", cause);
	}

	public JMSXAException(int code) {
		super(code);
	}	

	/*
	 * @param code - XA_* code 
	 */
	public JMSXAException(Throwable cause, int code) {		
		super(code);
		super.initCause(cause);
	}
	
	public JMSXAException(String message, int code) {
		super(message);
		super.errorCode = code;
	}
	
	public JMSXAException(String message, Throwable cause, int code) {
		this(message, code);	
		super.initCause(cause);		
	}
	
	
}
