/**
 * XATopicConnection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.xa;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.JMSConstants;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteXATopicConnection
	extends RemoteXAConnection
	implements javax.jms.XATopicConnection, TopicConnection { //$JL-SER$ - see RemoteConnection

	public RemoteXATopicConnection(
		long connectionID,
		String serverInstance,
		JMSRemoteServer server,
		ThreadSystem threadSystem,
		String clientID,
		boolean supportsOptimization) {
		super(connectionID, serverInstance, server, threadSystem, clientID,supportsOptimization);
	}	

	/**
   * Method createXAQueueSession. Creates an XATopicSession object.
   * @return a newly created XATopicSession
   * @throws JMSException  if the XATopicConnection object fails to create an XA topic 
   * session due to some internal error.
   */
	public javax.jms.XATopicSession createXATopicSession() throws JMSException {
			return (javax.jms.XATopicSession) createSession(true, Session.SESSION_TRANSACTED, SessionType.XA_TOPIC_SESSION);
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
