/**
 * QueueSession.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.session;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import com.sap.jms.util.TaskManager;
import com.sap.jms.client.connection.Connection;
import com.sap.jms.util.LogUtil;

public class JMSQueueSession extends JMSSession implements QueueSession {

	public JMSQueueSession(int sessionID, long msgIdBase, int ackMode, Connection connection, TaskManager taskManager) throws JMSException {
		super(sessionID, msgIdBase, ackMode, connection, taskManager);
	}

	public JMSTopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking createDurableSubscriber() on QueueSession is not supported.");
    }

	public JMSTopicSubscriber createDurableSubscriber(Topic topic, String name, String msgSelector, boolean noLocal) throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking createDurableSubscriber() on QueueSession is not supported.");
    }

	public Topic createTopic(String name) throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking createTopic() on QueueSession is not supported.");
    }

	public TemporaryTopic createTemporaryTopic() throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking createTemporaryTopic() on QueueSession is not supported.");
	}

	public void unsubscribe(String name) throws JMSException {
        throw new IllegalStateException(LogUtil.getFailedInComponentByCaller() + "Invoking unsubscribe() on QueueSession is not supported.");
	}

	public JMSQueueReceiver createReceiver(Queue queue) throws JMSException {
		return (JMSQueueReceiver)createConsumer(queue);
	}

	public JMSQueueReceiver createReceiver(Queue queue, String selector) throws JMSException {
		return (JMSQueueReceiver)createConsumer(queue, selector);
	}

	public javax.jms.QueueSender createSender(Queue queue) throws JMSException {
		return (QueueSender)createProducer(queue);
	}
}
