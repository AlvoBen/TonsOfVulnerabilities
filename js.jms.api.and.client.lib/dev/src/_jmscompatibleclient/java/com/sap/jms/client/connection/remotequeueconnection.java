/*
 * Created on 2004-11-21
 *
 */
package com.sap.jms.client.connection;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteQueueConnection extends RemoteConnection implements javax.jms.QueueConnection { //$JL-SER$ - see RemoteConnection
	
	public RemoteQueueConnection(long connectionID, String serverInstance, JMSRemoteServer server, ThreadSystem threadSystem, String clientID,boolean supportsOptimization) {
		  super(connectionID, serverInstance, server, threadSystem, clientID,supportsOptimization);
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
