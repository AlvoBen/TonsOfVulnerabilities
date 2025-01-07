package com.sap.engine.transport;

import java.net.*;
import java.util.Properties;
import java.io.IOException;

/**
 * @author  Nickolay Neychev
 */
public interface TransportFrame {

  /**
   * Opens layered clinet's ServerSocket
   *
   * @param   port  the ServerSocket's port
   * @param   queue  the layers queue of the ServerSocket
   * @param   cpm  reference to a ClientPortsManager
   * @return     the layered client's ServerSocket
   * @exception   IOException   if an I/O error occurs when opening the socket
   */
  public ServerSocket getServerSocket(int port, String queue, ClientPortsManager cpm) throws IOException;


  /**
   *
   *
   * @param   host  the Socket's host
   * @param   port  the Socket's port
   * @param   queue  the layers queue of the Socket
   * @param   cpm  reference to a ClientPortsManager
   * @param   props  properties for extra data
   * @return     the layered client's Socket
   * @exception   IOException   if an I/O error occurs when opening the socket
   */
  public Socket getSocket(String host, int port, String queue, ClientPortsManager cpm, Properties props) throws IOException;

}

