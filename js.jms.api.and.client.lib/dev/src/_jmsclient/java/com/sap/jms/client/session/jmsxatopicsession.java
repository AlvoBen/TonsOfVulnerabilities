package com.sap.jms.client.session;

import javax.jms.JMSException;
import javax.jms.TopicSession;
import javax.jms.XATopicSession;


public class JMSXATopicSession	extends JMSXASession implements XATopicSession {

	public JMSXATopicSession(JMSTopicSession session) throws JMSException {
		super(session);

	}

	public TopicSession getTopicSession() throws javax.jms.JMSException {
		return (TopicSession)getSession();
	}

}
