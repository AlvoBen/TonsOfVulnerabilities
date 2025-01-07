package com.sap.jms.interfaces;

import javax.jms.JMSException;

public interface JtaSynchronization {
	
	public void afterBegin()throws JMSException ;
	
	public void beforeCompletion() throws JMSException ;
	
	public void afterCompletion(int status) throws JMSException ;
	
}
