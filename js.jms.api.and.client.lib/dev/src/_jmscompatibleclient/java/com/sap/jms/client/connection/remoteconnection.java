/*
 * Created on 2004-11-21
 *
 */
package com.sap.jms.client.connection;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.server.remote.JMSRemoteServer;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 * 
 * The instances of RemoteConnection are created on the server and sent to the client.
 * But actually we serialize the corresponding ConnectionProxy, not the RemoteConnection itself.
 * This RemoteConnection stay on the server until it is carbage collected. 
 * As a part of finalization we close the connection. @see Connection.finalize()
 * This will cause newly created connection on the server side to be closed.
 * At that moment 2 instances with the same connectionId exist, and if we close one of them
 * this will closes both of them.
 * To prevent that, when we deserialize a ConnectionProxy and crete a new RemoteConnection
 * on the client side we always call connect() - to indicate that this connection is in use. 
 * The RemoteConnection instances that exists on server side are never connected and used
 * and when they are carbage collected they don't call close.  
 * 
 */

/* 
 * There is a specific logic for serialization via ConnectionProxy, 
 * so we will hide Non-Serializable JMSRemoteServer from JLIN
 */ 
public class RemoteConnection extends com.sap.jms.client.connection.Connection implements java.io.Serializable { //$JL-SER$
	
	private JMSRemoteServer server = null;
	private boolean isUsed = false;
	private String clientId = null;
	private boolean supportsOptimization = false; 
	

	public RemoteConnection(long connectionID, String serverInstance, JMSRemoteServer server, ThreadSystem threadSystem, String clientId, boolean supportsOptimization) { 		
        super(connectionID, serverInstance, new RemoteAdapter(server), threadSystem);
        this.server = server;	
        this.clientId = clientId;
        this.supportsOptimization = supportsOptimization;
	}
	
	protected long getID() {
		return connectionID;
	}
	
	public void close() throws javax.jms.JMSException {
  		if (isUsed)
		  super.close();
	}
	
	/**
	 * Used to serialize this RemoteConnection
	 * @see java.io.Serializable.
	 * @return RemoteConnection
	 * @throws java.io.ObjectStreamException
	 */
	public Object writeReplace() throws java.io.ObjectStreamException { 
  	  	return new ConnectionProxy(getClass().getName(), getID(), getServerInstance(), server, clientId,supportsOptimization);
	}
	
    protected void attemptToUse() throws IllegalStateException {
    	check();
    	super.attemptToUse();
    }
    
    protected void check() {
    	if (!isUsed) {
    		isUsed = true;
    		init();
    		try {
                //  note : if clientId is set, that will prevent further setting.  According to the spec a 
                //  flag will be raised inside the connection and we will prevent further setting, 
                //  that's why we must set it only here in case it is present and a real value was set in the factory.
                //  we will perform the check here instead of the connection class where it should have been
                //  in order to ensure full backward compatibility with the 6.40 client. 
                if (clientId != null && clientId.length() > 0) {
    		        setClientID(clientId);
                }    
    		} catch (JMSException e) {
    		    logService.exception(LNAME, e);		        			
    		}    		  
    	}    	
    }
	
	protected void init() {	
		networkAdapter.setConnection(this);
		networkAdapter.setThreadSystem(getThreadSystem());
        // It is vital to start this, since we want to have this thread running for remote clients as well
        AsyncCloser.getInstance().start(getThreadSystem());        
		try {
			((RemoteAdapter) networkAdapter).connect();
		} catch (JMSException e) {
		    logService.exception(LNAME, e);		    
		}
	}
	
	public String toString() {
		return super.toString() + " isUsed = " + isUsed;
	}
	
    protected void finalize() throws Throwable {// $JL-FINALIZE$
        // we must exit here if !isUsed - this is the second (parasite) object
        // created by the p4 integration
        // otherwise we will invoke the asynchronous closing thread and print
        // warnings in the trace file that someone has leaked
        if (!isUsed) {
            return;
        }
        super.finalize();
    }

	public boolean supportsOptimization() {
		return supportsOptimization;
	}
}
