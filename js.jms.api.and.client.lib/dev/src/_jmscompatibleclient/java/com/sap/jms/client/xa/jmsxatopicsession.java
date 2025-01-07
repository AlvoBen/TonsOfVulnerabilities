package com.sap.jms.client.xa;

import javax.jms.JMSException;
import javax.jms.TopicSession;
import javax.jms.XATopicSession;

import com.sap.jms.client.session.JMSTopicSession;

public class JMSXATopicSession	extends JMSXASession implements XATopicSession {

	public JMSXATopicSession(JMSTopicSession session) throws JMSException {
		super(session);

	}

	public TopicSession getTopicSession() throws javax.jms.JMSException {
		return (TopicSession)getSession();
	}

}
