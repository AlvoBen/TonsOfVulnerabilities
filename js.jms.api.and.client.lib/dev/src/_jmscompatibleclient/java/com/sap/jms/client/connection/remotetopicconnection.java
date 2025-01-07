/*
 * Created on 2004-11-21
 *
 */
package com.sap.jms.client.connection;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Topic;
import javax.jms.TopicSession;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteTopicConnection extends RemoteConnection implements javax.jms.TopicConnection { //$JL-SER$ - see RemoteConnection
	
	public RemoteTopicConnection(long connectionID, String serverInstance, JMSRemoteServer server, ThreadSystem threadSystem, String clientID,boolean supportsOptimization) {
	  super(connectionID, serverInstance, server, threadSystem, clientID,supportsOptimization);
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
