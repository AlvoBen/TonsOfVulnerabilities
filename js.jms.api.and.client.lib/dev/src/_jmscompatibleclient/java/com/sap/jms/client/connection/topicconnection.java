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

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.session.JMSSession.SessionType;

/**
 * @author Margarit Kirov
 * @version 1.0
 */
public class TopicConnection extends Connection implements javax.jms.TopicConnection {


  public TopicConnection(long connectionID, String serverInstance, NetworkAdapter networkAdapter, ThreadSystem threadSystem) {
    super(connectionID, serverInstance, networkAdapter, threadSystem);
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
