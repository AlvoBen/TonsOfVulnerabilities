/**
 * SocketWrapper.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import javax.jms.JMSException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.lib.util.ConcurrentHashMapLongObject;
import com.sap.jms.client.Util;
import com.sap.jms.client.session.ThreadPool;
import com.sap.jms.protocol.Packet;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.util.logging.LogService;

/**
 * Wrapper of a TCP socket
 * @author Margarit Kirov
 * @version 1.0
 */
public class SocketWrapper implements NetworkAdapter {
  private ThreadSystem pool = null;
  private static int instanceCount = 0;
  private static final String LOG_COMPONENT = "connection.SocketWrapper";
  
  private Socket socket = null;
  private BufferedInputStream input = null;
  private BufferedOutputStream output = null;
  private WeakReference connection = null;
  private ConcurrentHashMapLongObject waitTable = null; //holds the locks of the waiting objects or the returned results
  private boolean runFlag = true;
  private boolean isRunning = true;
  private String host = null;
  private int port = 0;
  private boolean isClosed = false;
  private long waitID = Long.MIN_VALUE;
  private Object destroySocketLock = new Object();
  private Exception closeException = null;
  private long connectionID = 0;
  private byte[] packet_length_buffer;
  private LogService logService;
  
  /**
   * Method SocketWrapper. Constructor for SocketWrapper.
   * @param host name of machine to which the socket will connect
   * @param port port of machine to which the socket will connect
   * @param maxBufferSize
   * @throws UnknownHostException
   * @throws IOException
   */
  public SocketWrapper(String host, int port) throws java.net.UnknownHostException, IOException {
    logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
    socket = new Socket(host, port);
    input = new BufferedInputStream(socket.getInputStream());
    output = new BufferedOutputStream(socket.getOutputStream());
    
    /*
     * Disable socket communication security since NW04(s) doesn't 
     * provide such checks 
     */
    // output.write(JMSConstants.SOCKET_INIT_MAGIC_NUMBER);
    // output.flush();
    socket.setKeepAlive(true);
    logService.debug(LOG_COMPONENT, "established socket connection to {0} keepAlive is - {1} TcpNoDelay is - {2}", new Object[] { socket, new Boolean(socket.getKeepAlive()), new Boolean(socket.getTcpNoDelay()) });    
    waitTable = new ConcurrentHashMapLongObject();
    this.host = host;
    this.port = port;
    packet_length_buffer = Util.getMemoryAllocator().malloc(Packet.SIZEOF_INT);
        
    synchronized (this.getClass()) {
      instanceCount++;
    }
  }
  
  /**
   * Method sendAndWait. Sends a packet to the server and waits for response
   * @param packet the packet to be sent
   * @return Packet the response from the server
   * @throws IOException thrown if an IO error occurs
   * @throws JMSException thrown if an internal error occurs
   */
  public Packet sendAndWait(Packet packet) throws java.io.IOException, JMSException {
	logService.path(LOG_COMPONENT, "Enter sendAndWait()");
    checkIfClosed(); 
    long waitID;
    
    synchronized (this) {
      waitID = ++this.waitID;
    }
    
    packet.setRequestID(waitID);

    Object waitLock = new Object(); //maybe object pool should be considered if left like this
    Packet response = null;
    synchronized(waitLock) {
    	logService.debug(LOG_COMPONENT, "{0}:Send message and wait for answer. WaitID: {1}", new Object[] { new Long(connectionID), new Long(waitID) });
        
      waitTable.put(waitID, waitLock);
      send(packet);
      
      while (waitLock.equals(waitTable.get(waitID))) {
        try {
        	waitLock.wait();
        	logService.debug(LOG_COMPONENT, "{0}:Response received. WaitID: {1}", new Object[] { new Long(connectionID), new Long(waitID) });
        } catch (InterruptedException e) {
          logService.exception(LogService.FATAL, LOG_COMPONENT, e);
        }
      }
            
      response = (Packet)waitTable.get(waitID);
      waitTable.remove(waitID);
    }
     
    logService.path(LOG_COMPONENT, "Exit sendAndWait()");
    return response;
  }
  
  /**
   * Method send. Sends a packet to the server without waiting for request.
   * @param packet the packet to be sent to the server
   * @throws IOException thrown if an IO error occurs
   * @throws JMSException thrown if an internal error occurs
   */
  public void send(Packet packet) throws java.io.IOException, javax.jms.JMSException {
	  logService.path(LOG_COMPONENT, "Enter send()");
	  checkIfClosed(); 
	  output.write(packet.getBuffer(), packet.getOffset(), packet.getLength());
	  output.flush();

	  logService.debug(LOG_COMPONENT, "{0}:Message sent.\n{1}", new Object[] { new Long(connectionID), packet});
	  logService.path(LOG_COMPONENT, "Exit send()");
  }
  
  public void run() {
    long requestID = 0;
    Object waitLock = null;
    Object ref = null;
    	 
	    try {
	      while (runFlag) {
    		logService.debug(LOG_COMPONENT,"{0}:Begin receive of new message!", new Object[]{ new Long(connectionID) });

	    	  Packet packet = Util.createPacket(input, packet_length_buffer);
	    	  requestID = packet.getRequestID();

    		logService.debug(LOG_COMPONENT, "{0}:Received packet:\n{1}", new Object[] { new Long(connectionID), packet});

		        if (waitTable.containsKey(requestID)) {
		          waitLock = waitTable.get(requestID);
		          synchronized (waitLock) {
		            waitTable.put(requestID, packet);
		            waitLock.notify();
		          }          
		        } else if (connection == null) {
		          logService.debug(LOG_COMPONENT, "JMS message received before the connection has been fully initialized! All other activity will be suspended!");
		          throw new JMSException("Internal error");
		        } else {
		          ref = connection.get();
		          
		          if (ref != null) {
		            ((Connection) ref).onPacketReceived(packet);
		          } else {
		            break;
		          }
		        }
	      }
	    } catch (Exception e) {
	      closeException = e;
	            
	      if (isClosed) {
	        return;
	      }
	      
	      logService.exception(LogService.FATAL, LOG_COMPONENT, e);
	              
	      try {
	        ref = null;
	
	        if (connection != null) {
	          ref = connection.get();
	        }
	
	        if (ref != null && runFlag && !((Connection) ref).isClosed()) {
	          JMSException jmse = new JMSException("Internal error");
	          jmse.initCause(e);
	          jmse.setLinkedException(e);
	          ((Connection) ref).onException(jmse);
	        } 
	      } catch (Exception jmse) {
	        logService.warningTrace(LOG_COMPONENT, "Exception while dispatching an error to a jms connection ExceptionListener");
	      }
	    } finally {
	      synchronized (destroySocketLock) {
	        isRunning = false;
	        destroySocketLock.notify();
	      }
	      
	      try {
	        close();
	      } catch (java.io.IOException ioe) {
	        logService.exception(LogService.DEBUG, LOG_COMPONENT, ioe);
	      }
	      
	    }
  }
  
  /**
   * Method setConnection. Sets a reference to the connection object to which this 
   * socket is bound.
   * @param connection reference to the connection object
   */
  public void setConnection(Connection connection) {
    this.connection = new WeakReference(connection);
    
    try {
      connectionID = connection.getConnectionID();
    } catch (JMSException jmse) {
      connectionID = -1;
    }
  }
  
  /**
   * Method destroySocket. Finalizes the work done by the socket, closes all streams
   * and the socket.
   * @throws IOException
   */
  public synchronized void close() throws java.io.IOException {
    logService.path(LOG_COMPONENT, "Enter close()");
    
    if (isClosed) {
      logService.debug(LOG_COMPONENT, "{0}:Socket already closed!", new Object[] { new Long(connectionID) });
      logService.path(LOG_COMPONENT, "Exit close()");
      return;
    }
    
    isClosed = true;
    runFlag = false;
    socket.shutdownInput();
    socket.shutdownOutput();
    input.close();
    output.close();
    socket.close();
    
    synchronized (this.getClass()) {
      instanceCount--;
      
      if (instanceCount == 0 && pool != null) {
        if (pool instanceof ThreadPool) {
          ((ThreadPool) pool).freeMemory();
        }
        
        pool = null;
      }
    }
        
    synchronized (destroySocketLock) {
      while (isRunning) {
        try {
          destroySocketLock.wait();
        } catch (InterruptedException e) {
          logService.warningTrace(LOG_COMPONENT, "JMS client socket could not be closed correctly because of interrupted thread!");
          logService.exception(LogService.DEBUG, LOG_COMPONENT, e);
        }
      }
    }
    
    long[] keys = waitTable.getAllKeys();
    Object value = null;
    
    for (int i = 0; i < keys.length; i++) {
      value = waitTable.get(keys[i]);
      if (!(value instanceof Packet)) {
        synchronized (value) {

          waitTable.remove(keys[i]);
          value.notify();
        }
      }
    }
    
    Object ref = null;
    
    if (connection != null) {
      ref = connection.get();
    }

    
    connection = null;
    packet_length_buffer = null;
    logService.path(LOG_COMPONENT, "Exit close()");
  }
  
  private final void checkIfClosed() throws IOException {
    if (closeException != null) {
      if (closeException instanceof IOException) {
        throw (IOException) closeException;
      } else {
        IOException ioe = new IOException(" Socket was closed due to " + closeException.getMessage());
        logService.exception(LogService.ERROR, LOG_COMPONENT, closeException);
        throw ioe;
      }
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
    
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "SocketWrapper to host: ");
    buffer.append(host);
    buffer.append(" port ");
    buffer.append(port);
    return buffer.toString();
  }
  
}
