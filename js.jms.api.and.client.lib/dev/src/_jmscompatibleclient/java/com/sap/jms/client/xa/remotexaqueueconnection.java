/**
 * XAQueueConnection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.xa;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteXAQueueConnection
	extends RemoteXAConnection
	implements javax.jms.XAQueueConnection, QueueConnection { //$JL-SER$ - see RemoteConnection

	public RemoteXAQueueConnection(
		long connectionID,
		String serverInstance,
		JMSRemoteServer server,
		ThreadSystem threadSystem,
		String clientID,
		boolean supportsOptimization) {
		super(connectionID, serverInstance, server, threadSystem, clientID,supportsOptimization);
	}
	
  /**
   * Method createXAQueueSession. Creates an XAQueueSession object.
   * @return a newly created XAQueueSession
   * @throws JMSException  if the XAQueueConnection object fails to create an XA queue 
   * session due to some internal error.
   */
	public javax.jms.XAQueueSession createXAQueueSession() throws JMSException {
		return (javax.jms.XAQueueSession) createSession(true, Session.SESSION_TRANSACTED, SessionType.XA_QUEUE_SESSION);
	}
  
  /* (non-Javadoc)
   * @see javax.jms.QueueConnection#createQueueSession(boolean, int)
   */
  public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
	return (QueueSession) createSession(transacted, acknowledgeMode, SessionType.QUEUE_SESSION);
  }

  /* (non-Javadoc)
   * @see javax.jms.QueueConnection#createConnectionConsumer(Queue, String, ServerSessionPool, int)
   */
  public ConnectionConsumer createConnectionConsumer(Queue destination, String messageSelector, ServerSessionPool serverSessionPool, 
													 int maxMessages) throws JMSException {
	return super.createConnectionConsumer(destination, messageSelector, serverSessionPool, maxMessages);
  }
}
