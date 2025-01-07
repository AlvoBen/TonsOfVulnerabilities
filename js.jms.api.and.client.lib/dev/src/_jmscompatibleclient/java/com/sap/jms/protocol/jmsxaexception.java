package com.sap.jms.protocol;

import javax.transaction.xa.XAException;

public class JMSXAException extends javax.jms.JMSException {

	private static final long serialVersionUID = 7107636794359988725L;

	public JMSXAException(String reason, String code) {
		super(reason, code);
	}

	/*
	 * @param code - XA_* code 
	 */
	public JMSXAException(String reason, Integer code) {
		super(reason, code.toString());
	}
	
	public XAException getXaException() {
		// TODO provide sth more error tolerant e.g. when errorCode is not set etc
		XAException e = new XAException(Integer.parseInt(getErrorCode()));
		e.initCause(this.getCause());
		
		return e; 
	}
}
