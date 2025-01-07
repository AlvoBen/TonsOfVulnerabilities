/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4;


import com.sap.engine.services.rmi_p4.exception.*;
import com.sap.engine.services.rmi_p4.monitor.PingCall;
import com.sap.engine.interfaces.cross.CrossCall;
import com.sap.engine.interfaces.cross.ConnectionProperties;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.lib.lang.Convert;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Class for client side protocol implementation.
 * Client  receives a reference from that class as repliable
 * when remote object is requested ( stub is loaded ).
 *
 * @author Georgy Stanev
 * @version 7.0 
 */
public class ClientConnection implements Runnable, Connection {

  /**
   * indicates true when thread is running
   */
  private boolean isAlive = false;
  /**
   * the socket for the connection
   */
  public Socket socket;
  /**
   * input stream of the socket
   */
  public InputStream in;
  /**
   * output stream of the socket
   */
  public OutputStream out;
  /**
   * Buffer for the incoming request. parser set this
   * field in order to control incoming request size.
   */
  public byte[] request;

  protected byte[] headerBuffer = new byte[ProtocolHeader.HEADER_SIZE];
  /**
   * the parser for this repliable
   */
  public Parser parser;
  protected static byte[] MAGIC = {35, 112, 35, 52};
  public String connectionId;
  public String destinationId;
  public String underlyingProfile;
  public boolean newSession = true;
  public int clusterElementId = -1;
  public String host = null;
  public int port;
  public String type;
  public boolean accepted = false;

  private Date connStartTime = new Date();
  private Date connCloseTime;
  
  static int INITIALIZE_CONNECTION_TIMEOUT = 30000;
  static int RUNTIME_CONNECTION_TIMEOUT = 180000;

  /**
   * constructor for right restriction
   */
  protected ClientConnection() {

  }

  /**
   * creates new connection from socket
   *
   * @param socket the socket for this ClientConnection object
   */
  public ClientConnection(Socket socket, byte[] connId, String type) throws IOException {
    if ((type == null) || (type.equals(""))) {
      type = P4ObjectBroker.DEFAULT_COMMUNICATION_LAYER;
    }
    isAlive = true;
    this.socket = socket;
    in = socket.getInputStream();
    out = socket.getOutputStream();
    connectionId = new String(connId);
    this.type = type;
    port = socket.getPort();
    host = socket.getInetAddress().getHostAddress();
    initialize(connId);
    Parser.init(this);
  }

  public ClientConnection(Socket socket, byte[] connId, String type, boolean accepted) throws IOException {
    if ((type == null) || (type.equals(""))) {
      type = P4ObjectBroker.DEFAULT_COMMUNICATION_LAYER;
    }
    isAlive = true;
    this.socket = socket;
    in = socket.getInputStream();
    out = socket.getOutputStream();
    connectionId = new String(connId);
    this.type = type;
    port = socket.getPort();
    host = socket.getInetAddress().getHostAddress();
    this.accepted = accepted;
    initialize(connId);
    Parser.init(this);
  }

  protected void initialize() throws IOException {
    initialize(connectionId.getBytes());
  }

  protected void initialize(byte[] connId) throws IOException {
    out.write(P4ObjectBroker.PROTOCOL_VERSION);
    out.write((byte) connId.length + 4);
    out.write(MAGIC);
    out.write(connId);
    out.flush();
    byte b = 2;
    byte[] body = null;
    socket.setSoTimeout(INITIALIZE_CONNECTION_TIMEOUT);
    
    for (int i = 0; i < 2; i++) {
      int count = 0;
      body = new byte[b];
      int read = 0;

      while (count != b) {
        try {
          read = in.read(body, count, b - count);
        } catch (InterruptedIOException stex) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("ClientConnection.initialize(byte[])", P4Logger.exceptionTrace(stex));
          }
          in.close();
          socket.close();
          throw new IOException("Client connection timeout. Connection port is wrong or server process hangs"); // $JL-EXC$
        }

        if (read == -1) {
          in.close();
          socket.close();
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("ClientConnection.initialize(byte[])", "Cannot make connection. The stream is closed 1");
          }
          throw  (IOException) (P4ObjectBroker.init()).getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Cannot_Make_Connection, null);
        } else {
          count += read;
        }
      }
      
      switch (i) {
        case 0:
          {
            if (body[0] != P4ObjectBroker.PROTOCOL_VERSION[0] || body[1] != P4ObjectBroker.PROTOCOL_VERSION[1]) {
              in.close();
              socket.close();
              if (P4Logger.getLocation().bePath()) {
                P4Logger.getLocation().pathT("ClientConnection.initialize(byte[])", "Cannot make connection. The stream is closed. The protocol version is not correct");
              }
              throw  (IOException) (P4ObjectBroker.init()).getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Cannot_Make_Connection, null);
            }
            b = (byte) in.read();
            if (b == -1) {
              in.close();
              socket.close();
              if (P4Logger.getLocation().bePath()) {
                P4Logger.getLocation().pathT("ClientConnection.initialize(byte[])", "Cannot make connection. The stream is closed. Read from the socket msg for closing.");
              }
              throw  (IOException) (P4ObjectBroker.init()).getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Cannot_Make_Connection, null);
            }

            break;
          }
        case 1:
          {
            if (body[0] != '#' || body[1] != 'p' || body[2] != '#' || body[3] != '4') {
              in.close();
              socket.close();
              if (P4Logger.getLocation().bePath()) {
                P4Logger.getLocation().pathT("ClientConnection.initialize(byte[])", "Cannot make connection. The stream is closed. The header which indicate p4 communication isn't correct.");
              }
              throw  (IOException) (P4ObjectBroker.init()).getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Cannot_Make_Connection, null);
            }

            break;
          }
      }
    }
    socket.setSoTimeout(RUNTIME_CONNECTION_TIMEOUT);
    destinationId = new String(body, 4, body.length - 4);
    if (accepted) {
      try {
        StringTokenizer tokenizer = new StringTokenizer(destinationId, ":");
        tokenizer.nextToken(); //skip type
        host = tokenizer.nextToken();
        port = Integer.parseInt(tokenizer.nextToken());
        connectionId = getConnectionId(host, port, type);
      } catch (NoSuchElementException  nsee) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ClientConnection.initialize(byte[])", "Cannot make connection. The stream is closed. The header which indicate p4 communication isn't correct.");
        }
        throw  (IOException) (P4ObjectBroker.init()).getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Cannot_Make_Connection, null);
      } catch (NumberFormatException nfe) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ClientConnection.initialize(byte[])", "Cannot make connection. The stream is closed. The header which indicate p4 communication isn't correct.");
        }
        throw  (IOException) (P4ObjectBroker.init()).getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Cannot_Make_Connection, null);
      }

    } else {
    connectionId = getConnectionId(host, port, type);
    if (!(P4ObjectBroker.init().NATEnabled() || type.equals("SAPRouter"))&& !connectionId.substring(connectionId.indexOf(':')).equalsIgnoreCase(destinationId.substring(destinationId.indexOf(':')))) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("ClientConnection.initialize(byte[])", "Connection ID does not match destination ID, but NAT support is disabled. {connectionId=" + connectionId + ", destinationId=" + destinationId + "}");
      }
      throw new IOException("Connection ID does not match destination ID; NAT support is disabled");
    }
  }
  }

  public synchronized boolean isAlive() {
    return isAlive;
  }

    public byte[] getId() {
        return new byte[8];  //TODO
    }

    public long getIdAslong() {
        return 0;  //TODO
    }

    public void sendRequest(byte[] messageBody, int size, CrossCall call) throws IOException {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ClientConnection.sendRequest(byte[], int, CrossCall)", this + ": sending message with size " +(size) + " bytes");
          if (P4Logger.dumpMessages() && P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("Message body:\r\n" + Message.toString(messageBody, 0, size));
          }
        }
        if (!isAlive()) {
          if (P4ObjectBroker.getBroker() != null) {
          throw (P4IOException) P4ObjectBroker.getBroker().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Not_alive_connection, null);
          } else {
            throw new P4IOException("P4 Broker is closed");
          }
        }
        try {
          synchronized(this) {
            out.write(messageBody, 0, size);
            out.flush();
          }
        } catch (IOException ex) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("ClientConnection.reply(Message, byte[])", P4Logger.exceptionTrace(ex));
          }
          close(ex); //Out of synchronization to ClientConnection monitor
          throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.IO_error_occurs, ex);
        }
    }

    public void sendReply(byte[] messageBody, int size, byte[] requestId) throws IOException {
      sendRequest(messageBody, size, null);
    }

    public int getPeerId() {
        return 0;  //TODO
    }

    /**
     * Closes the connection thread. This makes the thread
     * main method to close the socket and streams by its own
     */
    public synchronized void close() {
      isAlive = false;
      connCloseTime = new Date();
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().debugT("ClientConnection.close()", "Closing connection " + this.toString());
      }
      try {
        socket.shutdownInput();
        socket.close();
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ClientConnection.close()", P4Logger.exceptionTrace(ex));
        }
      }
    }

    public void callCompleted(CrossCall call) {
        //TODO
    }

    public void setMetaData(Object metaData) {
        //TODO
    }

    public Object getMetaData() {
        return null;  //TODO
    }

    public synchronized boolean isClosed() {
        return !isAlive; 
    }

    public boolean isLocal() {
        return false;  //TODO
    }

    public CrossCall[] getCalls() {
        return new CrossCall[0];  //TODO
    }

    public void addRequestMonitor(Object monitor) // only put ExecutionMonitor here
    {
        //TODO
    }

    public ConnectionProperties getProperties() {
        return null;  //TODO
    }

    public void close(Exception ex) {
      if (P4Logger.getLocation().beWarning()) {
        P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_WARNING, this.toString(), ex);
      }
      close();
      Parser.disposeConnection(this, ex);
    }

  /**
   * thread main method
   */
  public void run() {
    boolean pingTimeout = false;
    while (isAlive) {
      try {
        int sum = 0;
        int size = request.length;
        int readed = 0;
        while (sum != size && readed >= 0) {
          try {
            readed = in.read(request, sum, size - sum);
            pingTimeout = false;
          } catch (SocketTimeoutException ste) {
            if (pingTimeout){
              if (P4Logger.getLocation().beInfo()) {
                P4Logger.getLocation().infoT("ClientConnection.run()", "Ping timeout exceeded while waiting for input from " + socket.toString());
              }
              throw new IOException("Ping timeout exceeded while waiting for input from " + socket.toString()); // $JL-EXC$
            } else {
              pingTimeout = true;
              PingCall pingCall = PingCall.getPingCall();
              byte[] message = new byte[ProtocolHeader.HEADER_SIZE + 8 + 1 + 2];
              pingCall.writeId(message, ProtocolHeader.HEADER_SIZE);
              message[ProtocolHeader.HEADER_SIZE + 8] = Message.PING_CONNECTION_MESSAGE; // ping message type
              if (P4Logger.getLocation().beInfo()) {
                P4Logger.getLocation().infoT("ClientConnection.run()", "Sending PING message to  " + this);
              }
              Convert.writeIntToByteArr(message, 6, 1111);
              Convert.writeIntToByteArr(message, 10, 1111);
              Convert.writeIntToByteArr(message, 2, message.length - 14);
              sendRequest(message, message.length, null);
            }
          }
          if (readed != -1) {
            sum += readed;
          } else {
            if (P4Logger.getLocation().beInfo()) {
              P4Logger.getLocation().infoT("ClientConnection.run()",  "End of stream is reached unexpectedly during input from " + socket.toString());
            }
            throw new EOFException("End of stream is reached unexpectedly during input from " + socket.toString());
          }
        }
        Parser.newRequest(this);
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ClientConnection.run()", P4Logger.exceptionTrace(ex));
        }
        close(ex);
        break;
      }
    }
  }


  public String getUnderlyingProfile() {
      return underlyingProfile;
  }

    public void setUnderlyingProfile(String profile) {
        underlyingProfile = profile;
    }

    private String getConnectionId(String host, int port, String type) {
      if (host.indexOf(':') != -1) {
        host = "#"+host.replace(':', '.');
      }
      StringBuffer _id = new StringBuffer(type);
      _id.append(":");
      _id.append(host);
      _id.append(":");
      _id.append(port);
      return _id.toString();
    }

  public String toString() {
    String connectionDisplay = "ClientConnection <" + Integer.toHexString(hashCode()) + "> to " + connectionId;
    if (underlyingProfile != null){
      //Underlying profiles are initialized only via NAT connection.
      connectionDisplay = connectionDisplay + " [" + underlyingProfile + "]";
    }
    connectionDisplay = connectionDisplay + " created on : " + connStartTime.toString();
    if (connCloseTime != null){
      connectionDisplay = connectionDisplay + " closed on : " + connCloseTime.toString();
    }
    return connectionDisplay;
  }
}

