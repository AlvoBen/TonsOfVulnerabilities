package com.sap.jms.client.session;

import javax.jms.JMSException;

import com.sap.jms.client.message.JMSMessage;

public interface AckHandler {
	
	void acknowledge(Long consumerId, JMSMessage message) throws JMSException;
	void scheduleForAcknowledge(Long consumerId, JMSMessage message) throws JMSException;
	
	void sendRollback()throws JMSException;
	void sendRecover()throws JMSException;
}
