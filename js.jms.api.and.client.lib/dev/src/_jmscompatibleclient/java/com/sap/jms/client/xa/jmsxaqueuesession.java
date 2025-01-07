package com.sap.jms.client.xa;

import javax.jms.JMSException;
import javax.jms.QueueSession;
import javax.jms.XAQueueSession;

import com.sap.jms.client.session.JMSQueueSession;

public class JMSXAQueueSession extends JMSXASession implements XAQueueSession {

	public JMSXAQueueSession(JMSQueueSession session) throws JMSException {
		super(session);

	}

	public QueueSession getQueueSession() throws javax.jms.JMSException {
		return (QueueSession)getSession();
	}
}
