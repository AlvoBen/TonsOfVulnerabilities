/**
 * TopicConnection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Topic;
import javax.jms.TopicSession;

import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.util.TaskManager;

/**
 * @author Margarit Kirov
 * @version 1.0
 */
public class TopicConnection extends Connection implements javax.jms.TopicConnection {

	public TopicConnection(long connectionId, ServerFacade serverFacade, ClientFacade clientFacade, String vpName, String clientId, TaskManager taskManager, boolean supportsOptimization) {
		super(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);
  }
  
  /* (non-Javadoc)
   * @see javax.jms.TopicConnection#createTopicSession(boolean, int)
   */
  public TopicSession createTopicSession(boolean transacted, int acknowledgeMode) throws JMSException {
    return (TopicSession) createSession(transacted, acknowledgeMode, SessionType.TOPIC_SESSION);
  }

  /* (non-Javadoc)
   * @see javax.jms.TopicConnection#createConnectionConsumer(Topic, String, ServerSessionPool, int)
   */
  public ConnectionConsumer createConnectionConsumer(Topic destination, String messageSelector, ServerSessionPool serverSessionPool, 
                                                     int maxMessages) throws JMSException {
    return super.createConnectionConsumer(destination, messageSelector, serverSessionPool, maxMessages);
  }
  
}
