package com.sap.jms.client.connection;

import java.io.IOException;

import javax.jms.JMSException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.Util;
import com.sap.jms.client.session.ThreadPool;
import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.server.remote.JMSRemoteClient;
import com.sap.jms.server.remote.JMSRemoteClientImpl;
import com.sap.jms.server.remote.JMSRemoteServer;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;

/**  
 * @author Desislav Bantchovski
 * @version 7.10 
 */

public class RemoteAdapter implements NetworkAdapter {
	
	private JMSRemoteClient client = null;
	private JMSRemoteServer server = null;	
	private Connection connection = null;	
	private transient LogService logService;
	private static final transient String LOG_COMPONENT = "connection.RemoteAdapter";	 	
	private long requestId = 0;
	
	private boolean isClosed = false;	
	private boolean runFlag = true;
	private boolean isRunning = true;
 	private transient ThreadSystem pool = null; 		
    private ClassLoader appClassLoader = null;    
    
	/**
	 * Creates a new RemoteAdapter, associated with some particular RemoteServer.
	 * @param server the remote provider to which this NetworkAdapter is bound.
	 */	
	public RemoteAdapter(JMSRemoteServer server) {
		this.server = server;	 
		logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	}
	
	public synchronized long getRequestID() {
		return requestId++;			
	}
	
	/**
	 * Returns the JMSRemoteServer, associated with some particular RemoteAdapter.
	 * @return the remote provider to which this NetworkAdapter is bound.
	 */
	protected JMSRemoteServer getServer() {
		return server;
	}
	
	/**
	 * Method sendAndWait. Sends a packet to the server and waits for response
	 * @param packet the packet to be sent
	 * @return Packet the response from the server
	 * @throws IOException thrown if an IO error occurs
	 * @throws JMSException thrown if an internal error occurs
	 */  
	
	public Packet sendAndWait(Packet packet) throws java.io.IOException, JMSException {
		Packet result = null;

		if (!isClosed) {
			try {			
				byte[] answer = null;  	    	
				packet.setRequestID(getRequestID());					
				int type = packet.getPacketType();			
				boolean connect = type == PacketTypes.CONNECTION_CREATE_REQUEST;
				long connectionId = connect ? 0 : ((RemoteConnection) connection).getID();
				answer = server.dispatchRequest(connectionId, packet.getBuffer(), packet.getOffset(), packet.getLength());			
				if (answer != null) {
					result = Util.createPacket(answer, 0, answer.length);	  	  
				}
//				logService.debug(LOG_COMPONENT, "sendAndWait request.type = {0} result.type = {1}", new Object[] {packet.getPacketTypeAsString(),(result != null ? result.getPacketTypeAsString() : ""));				
			} catch(Exception e) {    	
				logService.exception(LOG_COMPONENT, e);				
				JMSException x = new JMSException("Failed to create connection.");
				x.initCause(e);
				x.setLinkedException(e);					    		
				throw x;	  	
			}    
		} else {
			JMSException e = new JMSException("Cannot complete operation. Adapter closed.");
			throw e;		  	
		}
		return result;
		
	}	
	
	/**
	 * Used as a part to register a callback to this client on the provider.
	 * This callback will be used for asynchrous notifications later.
	 * @throws JMSException
	 */
	public void connect() throws JMSException {
		if (client == null) { 	
			try {
			    appClassLoader = Thread.currentThread().getContextClassLoader();				
				client = new JMSRemoteClientImpl(this);		
				long connectionId = connection != null ? ((RemoteConnection) connection).getID() : 0;	  		
				JMSRemoteServer dedicated = server.handshake(connectionId, client);
				server = dedicated;
			} catch(Exception e) {	  	
				logService.exception(LOG_COMPONENT, e);								
				JMSException x = new JMSException("Failed to create connection.");
				x.initCause(e);
				x.setLinkedException(e);				
				throw x;		  	  
			}
		} else {
			logService.errorTrace(LOG_COMPONENT, "RemoteAdapter.setCallback client is null");
		}		
	}	
  
	/**
	 * Method send. Sends a packet to the server without waiting for request.
	 * @param packet the packet to be sent to the server
	 * @throws IOException thrown if an IO error occurs
	 * @throws JMSException thrown if an internal error occurs
	 */

	public void send(Packet packet) throws java.io.IOException, javax.jms.JMSException {
		throw new JMSException("Do not use this method !");		
	}
  
	/**
	 * Method setConnection. Sets a reference to the connection object to which this 
	 * adapter is bound.
	 * @param connection reference to the connection object
	 */
  
	public void setConnection(Connection connection) {
		this.connection = connection;	
	}
  
	/**
	 * Method close. Finalizes the work done by the adapter.
	 * @throws IOException 
	 */
	public synchronized void close() throws java.io.IOException {
		logService.debug(LOG_COMPONENT, "Enter close()");
    
		if (isClosed) {
			logService.warningTrace(LOG_COMPONENT, "{0}:Socket already closed!", new Object[] {connection});
			return;
		}
				
		isClosed = true;
		runFlag = false;
		
		if (pool != null) {
			if (pool instanceof ThreadPool) {
				((ThreadPool) pool).freeMemory();
			}        
//			pool = null;
		}		
		
        appClassLoader = null;
        
        try {
        	server.closedConnection();
	    } catch(Exception e) {
		    logService.exception(LOG_COMPONENT, e);
	    }            
	}
  
	public boolean isClosed() {
		return this.isClosed;
	}
	
	public void setThreadSystem(ThreadSystem threadSystem) {
		if (!isClosed) { 
			pool = threadSystem;
		}
	}
	
	public ThreadSystem getThreadSystem() {
		return pool;  
	}	
	
	/**
	 * Receives asynchronously a packet from the provider.
	 * @param request packet from the provider
	 * @param offset packet offset - used to create a Packet.
	 * @param length packet length - used to create a Packet.
	 */
	
	public void receive(byte[] request, int offset, int length) {
        ClassLoader oldClassLoader = null;		
		try {
		    if (appClassLoader != null) {
		        oldClassLoader = Thread.currentThread().getContextClassLoader();
		        Thread.currentThread().setContextClassLoader(appClassLoader);
		    }
			
			Packet packet = Util.createPacket(request, offset, length);
			if ((connection != null) && (packet != null)) { 		  	
				connection.onPacketReceived(packet);
			}
		} catch (Exception e) {
		    if (isClosed) {
		    	return;
		    }		  
		    logService.exception(LogService.FATAL, LOG_COMPONENT, e);
		              
		    try {
		        if (connection != null && runFlag && ! connection.isClosed()) {
		        	JMSException jmse = new JMSException("Internal error");
		        	jmse.initCause(e);
		        	jmse.setLinkedException(e);
		        	connection.onException(jmse);
		        } 
		    } catch (Exception jmse) {
		        logService.warningTrace(LOG_COMPONENT, "Exception while dispatching an error to a jms connection ExceptionListener:");
				logService.exception(LOG_COMPONENT, jmse);		        
		    } finally {
		    	try {
		    		close();
		        } catch (java.io.IOException ioe) {
		            logService.exception(LOG_COMPONENT, ioe);
		        }
		    }
		} finally {
		  if (oldClassLoader != null) {		  	
		      Thread.currentThread().setContextClassLoader(oldClassLoader);
		  }			
		}
	}

	public void run() {
	}	
	
	public void unreferenced() {
        logService.infoTrace(LOG_COMPONENT, "Removing unreferenced JMSRemoteServer {0}", new Object[] {server});
        JMSException jmse = new JMSException("Connection is closed.");
        
		ExceptionListenerCaller exceptionListenerCaller = new ExceptionListenerCaller(connection, jmse, appClassLoader);        
    	if (getThreadSystem() != null) {    		
    		getThreadSystem().startThread(exceptionListenerCaller, false);
    	} else {
    		logService.infoTrace("ExceptionListener", "Exception Listener was invoked from the same application thread. The connection is {0} ", new Object[]{connection});
    		exceptionListenerCaller.invokeOnException();
    	} 
	}	
	 	 
	class ExceptionListenerCaller implements Runnable {
	      
        private Connection openConnection;
        private JMSException jmsException;
        private ClassLoader classLoader;
 
        ExceptionListenerCaller(Connection openConnection, JMSException jmsException, ClassLoader classLoaderToUse) {
        	this.openConnection = openConnection;
            this.jmsException = jmsException;
            this.classLoader = classLoaderToUse;
        }
 
        public void run() {
        	invokeOnException();
        }
        public void invokeOnException() {
            ClassLoader oldClassLoader = null;
            try {
                if (classLoader != null) {
                  oldClassLoader = Thread.currentThread().getContextClassLoader();
                  Thread.currentThread().setContextClassLoader(classLoader);
                }                 
                logService.infoTrace(LOG_COMPONENT, "Exception Listener from a new application thread will be called for JMS connection {0}, classloader will be set to {1} ", new Object[]{openConnection, classLoader});  
                openConnection.onException(jmsException);
            } finally {
                if (oldClassLoader != null) {
                  Thread.currentThread().setContextClassLoader(oldClassLoader);
                }
            }           	
        }
    }
	
	public String toString() {
		StringBuffer text = new StringBuffer();
		text.append("client = " + (client != null ? client.toString() : "null") + "\n");
		text.append("server = " + (server != null ? server.toString() : "null") + "\n");	  
		text.append("thread pool = " + (pool != null ? pool.toString() : "null") + "\n");  	  
		text.append("isClosed = " + isClosed + "\n");
		text.append("runFlag  = " + runFlag + "\n");
		text.append("isRunning = " + isRunning + "\n");	
		return text.toString();		
	}


}	