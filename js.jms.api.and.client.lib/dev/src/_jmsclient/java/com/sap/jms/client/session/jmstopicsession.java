/**
 * TopicSession.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.Topic;
import javax.jms.TopicSession;

import com.sap.jms.client.connection.Connection;
import com.sap.jms.util.LogUtil;
import com.sap.jms.util.TaskManager;

public class JMSTopicSession extends JMSSession implements TopicSession {

	public JMSTopicSession(int sessionID, long msgIdBase, int ackMode, Connection connection, TaskManager taskManager) throws javax.jms.JMSException {
		super(sessionID, msgIdBase, ackMode, connection, taskManager);
	}

	public JMSTopicSubscriber createSubscriber(Topic topic) throws JMSException {
		return (JMSTopicSubscriber) createConsumer(topic);
	}

	public JMSTopicSubscriber createSubscriber(Topic topic, String selector,boolean noLocal) throws JMSException {
		return (JMSTopicSubscriber) createConsumer(topic, selector, noLocal);
	}
	public TopicPublisher createPublisher(Topic topic) throws JMSException {
		return (TopicPublisher) createProducer(topic);
	}

	public Queue createQueue(String name) throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking createQueue() on TopicSession is not supported.");
	}

	public TemporaryQueue createTemporaryQueue() throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking createTemporaryQueue() on TopicSession is not supported.");
    }

	public JMSQueueBrowser createBrowser(Queue queue) throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking createBrowser() on TopicSession is not supported.");
    }

	public JMSQueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking createBrowser() on TopicSession is not supported.");
    }    
}
