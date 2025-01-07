/**
 * NetworkAdapter.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import javax.jms.JMSException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.protocol.Packet;

/**
 * @author Margarit Kirov
 * @version 1.0
 */
public interface NetworkAdapter extends Runnable {
  
  /**
   * Method sendAndWait. Sends a packet to the server and waits for response
   * @param packet the packet to be sent
   * @return Packet the response from the server
   * @throws IOException thrown if an IO error occurs
   * @throws JMSException thrown if an internal error occurs
   */  
  Packet sendAndWait(Packet packet) throws java.io.IOException, JMSException;
  
  /**
   * Method send. Sends a packet to the server without waiting for request.
   * @param packet the packet to be sent to the server
   * @throws IOException thrown if an IO error occurs
   * @throws JMSException thrown if an internal error occurs
   */
  void send(Packet packet) throws java.io.IOException, javax.jms.JMSException;
  
  /**
   * Method setConnection. Sets a reference to the connection object to which this 
   * adapter is bound.
   * @param connection reference to the connection object
   */
  void setConnection(Connection connection);
  
  /**
   * Method close. Finalizes the work done by the adapter.
   * @throws IOException 
   */
  void close() throws java.io.IOException;
  
  boolean isClosed();
  
  void setThreadSystem(ThreadSystem threadSystem);
}
