/**
 * RemoteXAConnection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.xa;

import javax.jms.JMSException;
import javax.jms.Session;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.connection.RemoteConnection;
import com.sap.jms.client.session.JMSSession.SessionType;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteXAConnection
	extends RemoteConnection
	implements javax.jms.XAConnection { //$JL-SER$ - see RemoteConnection

	public RemoteXAConnection(
		long connectionID,
		String serverInstance,
		JMSRemoteServer server,
		ThreadSystem threadSystem,
		String clientID,
		boolean supportsOptimization) {
		super(connectionID, serverInstance, server, threadSystem, clientID,supportsOptimization);
	}
	
  /**
   * Method createXASession. Creates an XASession object.
   * @return XASession a newly created XASession
   * @throws JMSException if the XAConnection object fails to create an XASession 
   * due to some internal error.
   */
	public javax.jms.XASession createXASession() throws JMSException {
		return (javax.jms.XASession) createSession(true, Session.SESSION_TRANSACTED, SessionType.XA_GENERIC_SESSION);
	}

}
