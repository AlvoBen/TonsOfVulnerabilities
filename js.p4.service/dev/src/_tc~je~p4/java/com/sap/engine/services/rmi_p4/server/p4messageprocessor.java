package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.interfaces.cross.*;
import com.sap.engine.services.rmi_p4.*;
import com.sap.engine.services.rmi_p4.P4IOException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.lib.lang.Convert;

import java.io.IOException;


public class P4MessageProcessor implements MessageProcessorExt {

  public int threads = 0;
  private int queueSize = 0;
  public volatile int busyThreads = 0;
  public volatile int tempThreads = 0;
  private P4SessionProcessor sessionProcessor;
  private FCAConnector fcaConnector;
  private P4ObjectBroker broker = null;

  public P4MessageProcessor(P4SessionProcessor sessionProcessor) {
    broker = P4ObjectBroker.init();
    this.sessionProcessor = sessionProcessor;
    this.threads = P4ServiceFrame.threads;
    this.queueSize = P4ServiceFrame.requestQueueSize;
  }

 public CrossMessage getMessage(byte[] data, int size, Connection connection) {
  P4Message message;
  message = new P4Message(this, sessionProcessor);
  message.setData(data, size);
  message.setConnection(connection);
  return message;
 }

  public void clientConnectionClosed(Connection connection){
     broker.disposeConnection(Convert.byteArrToLong(connection.getId(),0) + ":" + sessionProcessor.currentServerId);
  }

  public void clientConnectionClosed(Connection connection, String errorMsg) {
    clientConnectionClosed(connection);
  }

  public void setConnector(FCAConnector connector) {
    this.fcaConnector = connector;
  }

  public FCAConnector getConnector() {
    return fcaConnector;
  }

  public int getType() {
     return P4_PROCESSOR;
  }

  public int getNumberOfConcurrentThreads() {
    return threads;
  }

  public int getRequestQueueSize() {
    return queueSize;
  }

  public void clientConnectionAccepted(Connection connection) {
    //currently this method is called only for telnet connections
  }

  public Connection getConnection(String type, String host, int port, boolean force) throws P4IOException {
    byte transport = FCAConnector.TRANSPORT_PLAIN;
    if(type.equalsIgnoreCase("ssl")) {
      transport = FCAConnector.TRANSPORT_SSL;
    }
    try {
      String address = host;
      if (host.charAt(0) == '#') {
        address = host.substring(1).replace('.', ':');
      }

      Connection connection = null;

      try {
        connection = fcaConnector.openConnection((byte)P4_PROCESSOR, transport, port, address, force);
      } catch (IOException e) {
        if (force) {
          if(P4Logger.getLocation().bePath()){
            P4Logger.getLocation().pathT("P4MessageProcessor.getConnection(String, String, int, boolean)", "Opening forced connection for transport " + transport + " host "+address+":"+port + " failed. Will check is it possible to open/reuse a not forced connection");
          }

          connection = fcaConnector.openConnection((byte)P4_PROCESSOR, transport, port, address, false);
        } else {
          throw e;
        }
      }

      if (connection.getUnderlyingProfile() == null) {
          StringBuffer _id = new StringBuffer(type);
          _id.append(':');
          _id.append(host);
          _id.append(':');
          _id.append(port);
          connection.setUnderlyingProfile(_id.toString());
       }
      return connection;
    } catch (IOException ioex) {
      throw new P4IOException("",ioex);
    }
  }

  public Connection getConnection(int serverID) throws P4IOException {
    try {
      return fcaConnector.openConnection((byte)P4_PROCESSOR, serverID);
    } catch (IOException ioex) {
      throw new P4IOException("",ioex);
    }
  }

  public void releaseMessage(P4Message message) {
    fcaConnector.releaseBuffer(message.request);
  }


  public Connection getMsConnection(int serverID) throws IOException {
    return new MSConnection(sessionProcessor.getClusterOrganizer(), serverID); 
  }
}