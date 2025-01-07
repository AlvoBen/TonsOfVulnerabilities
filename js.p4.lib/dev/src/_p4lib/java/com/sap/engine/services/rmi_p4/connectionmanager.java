package com.sap.engine.services.rmi_p4;

import com.sap.engine.interfaces.cross.CrossObjectBroker;
import com.sap.engine.interfaces.cross.io.transport.PortManager;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.exception.P4BaseIOException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Manager for the connections. It is created in client side
 * broker implementation in order to create new Repliables
 * either from incoming connection or from client request
 * for connection. It maintains all connections the way they
 * perform multiplexing.There is only one reference of that
 * class in every VM
 *
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public class ConnectionManager implements Runnable {

  /**
   * this manager
   */
  /**
   * indicates true when class thread is running
   */
  public boolean isRunning = false;
  /**
   * the broker
   */
  public P4ObjectBroker broker;
  /**
   * Hashtable with repliable objects
   */
  public Hashtable connections = new Hashtable();
  /**
   * socket listener
   */
  public ServerSocket ss;
  /**
   * port for listening
   */
  public int port = 0;
  /**
   * the host for listening
   */
  public byte[] host = null;
  public String hostName;
  public String hostNameIP;
  public String hosts;
  public String profileId;
  public static PortManager portManager;

  

  /**
   * constructs the manager
   *
   * @param broker the broker
   * @throws IOException
   */
  public ConnectionManager(P4ObjectBroker broker) throws IOException {
    this(broker, 0);
    CrossObjectBroker.init();
    portManager = CrossObjectBroker.getPortManager();
  }


  public ConnectionManager(P4ObjectBroker broker, int serPort) throws IOException {
    isRunning = true;
    this.broker = broker;
    CrossObjectBroker.init();
    portManager = CrossObjectBroker.getPortManager();
    if (!P4ObjectBroker.transportType.equals(P4ObjectBroker.DEFAULT_COMMUNICATION_LAYER)) {
      ss = portManager.getRealServerSocket((serPort == 0 ? port : serPort), P4ObjectBroker.transportType);
    } else {
      ss = new ServerSocket((serPort == 0 ? port : serPort));
    }

    port = ss.getLocalPort();
    try {
      hostNameIP = InetAddress.getLocalHost().getHostAddress();
      hostName = InetAddress.getLocalHost().getHostName();
      hosts = new String();
      InetAddress[] multi = InetAddress.getAllByName(hostName);
      for (int i = 0; i < multi.length; i++) {
        hosts = hosts + multi[i].getHostAddress() + "@";
      }
      host = new byte[2 * hosts.length()];
      Convert.writeUStringToByteArr(host, 0, hosts);
    } catch (UnknownHostException unknownHost) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ConnectionManager(P4ObjectBroker, int)", "P4ObjectBrokerClient Impl: Error in getting host address." + P4Logger.exceptionTrace(unknownHost));
      }
      try {
        hostNameIP = InetAddress.getByName(P4ObjectBroker.IPv4_LOCALHOST).getHostAddress();
        hostName = InetAddress.getByName(hostNameIP).getHostName();
        hosts = new String();
        InetAddress[] multi = InetAddress.getAllByName(hostName);
        for (int i = 0; i < multi.length; i++) {
          hosts = hosts + multi[i].getHostAddress() + "@";
        }
        host = new byte[2 * hosts.length()];
        Convert.writeUStringToByteArr(host, 0, hosts);
      } catch (UnknownHostException unHost) {
        try {
          hostNameIP = InetAddress.getByName("0:0:0:0:0:0:0:1").getHostAddress();
          hostName = InetAddress.getByName(hostNameIP).getHostName();
          hosts = new String();
          InetAddress[] multi = InetAddress.getAllByName(hostName);
          for (int i = 0; i < multi.length; i++) {
            hosts = hosts + multi[i].getHostAddress() + "@";
          }
          host = new byte[2 * hosts.length()];
          Convert.writeUStringToByteArr(host, 0, hosts);
        } catch (UnknownHostException uhe) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("ConnectionManager(P4ObjectBroker, int)", "P4ObjectBrokerClientImpl: Error while getting local host address. " + P4Logger.exceptionTrace(unHost));
          }
          throw new IOException("P4ObjectBrokerClientImpl: Error while getting local host address");
        }
      }
    }
    if (hostNameIP.indexOf(':') != -1) {
      hostNameIP = "#"+hostNameIP.replace(':', '.');
    }
    profileId = P4ObjectBroker.transportType + ":" + hostNameIP + ":" + port;
    Thread thr = new Thread(this);
    thr.setDaemon(true);
    thr.start();
  }

  /**
   * The broker uses that method for obtaining repliable
   * reference
   *
   * @param host the host for the repliable
   * @param port the port for the repliable
   * @return a p4 connection
   */
  public ClientConnection getConnection(String type, String host, int port, Properties properties) throws P4IOException {
    if (properties == null) {
      properties = P4ObjectBroker.props;
    }
    
    if (type == null || type.equals("")) {
      type = P4ObjectBroker.DEFAULT_COMMUNICATION_LAYER;
    }
    ClientConnection con = null;
    try {
      String realHost = host;
      if (host.charAt(0) == '#') {
        realHost = host.substring(1).replace('.', ':');
      }
      StringBuilder _id = new StringBuilder(type);
      _id.append(":");
      _id.append(host);
      _id.append(":");
      _id.append(port);
      String id1 = _id.toString();
      String id = "";
      Socket s;
      synchronized (this) {
        if (!isRunning) {
          throw new P4IOException("Connection manager is closed");
        }
        con = (ClientConnection) connections.get(id1);
        if (con == null || !con.isAlive()) {
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("ConnectionManager.getConnection()", "Opening connection to " + type + ":" + realHost + ":" + port + " by ConnectionManager <" + Integer.toHexString(hashCode()) + "> from P4ObjectBroker <" + Integer.toHexString(broker.hashCode()) + ">");
            if (con != null){
              P4Logger.getLocation().pathT("ConnectionManager.getConnection()", "Cannot reuse connection " + con + " appears as not alive");
              connections.remove(id1);
            }
          }
          s = getSocket(type, realHost, port, properties);
          if(P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("ConnectionManager.getConnection()","Socket created, starting initialization");
          }
          _id = new StringBuilder(type);
          _id.append(":");
          _id.append(this.hostNameIP);
          _id.append(":");
          _id.append(this.port);
          id = _id.toString();
          con = new ClientConnection(s, id.getBytes(), type);
          Thread thr = new Thread(con);
          thr.setDaemon(true);
          thr.start();
          if(P4Logger.getLocation().bePath()) {
            if(P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("ConnectionManager.getConnection()", "Expected id: " + id1 + " received id: " + con.destinationId);
            }
            P4Logger.getLocation().pathT("ConnectionManager.getConnection()", "Connection established: " + con + " client profile: " + id);
          }
          connections.put(id1, con);
          if(!id1.equals(con.destinationId)) {
            //Check for already established connection to multiple IP machine. 
            //If already established - reuse established connection and ignore new one, it will be closed by remote side.
            Object established = connections.get(con.destinationId);
            if (established!= null){
              connections.remove(id1);
              return (ClientConnection) established;
            }
            //Not already established connection => NAT
            connections.put(con.destinationId, con) ;
          }
        } else {
          if(P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("ConnectionManager.getConnection(String, String, int, Properties)", "Reusing connection " + con);
          }
        }
      }
    } catch (ThreadDeath thd) {
        throw thd;
    } catch (StackOverflowError sofe) {
        throw sofe;
    } catch (OutOfMemoryError oom) {
        throw oom;
    } catch (Throwable  th) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ConnectionManager.getConnection(String, String, int, Properties)", "type=" + type + ", host=" + host + ", port=" + port + " < >"  + P4Logger.exceptionTrace(th));
      }
      if(type.equals("SAPRouter")) {
        throw new P4IOException("Unable to establish a SAPRouter connection with string \"" + P4ObjectBroker.props.get("SAPRouterString") + "\"");
      } else {
        if(host.charAt(0) == '#') {
          host = host.substring(1).replace('.', ':');
        }
        throw (P4IOException) P4ObjectBroker.getBroker().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Unable_to_Open_Connection, th, new Object[]{host, String.valueOf(port)});
      }
    }
    return con;
  }

  /**
   * The broker uses that method to get connection for ping
   *
   * @param host the host for the repliable
   * @param port the port for the repliable
   * @return existing p4 connection or null
   */
  public ClientConnection takeConnection(String type, String host, int port) throws P4IOException {
    StringBuilder _id = new StringBuilder(type);
    _id.append(":");
    _id.append(host);
    _id.append(":");
    _id.append(port);
    String id = _id.toString();
    return (ClientConnection) connections.get(id);
  }


  /**
   * Closes all connection registered in the manager
   */
  public void closeConnections() {
    Collection c = connections.values();
    Object[] connections_ = c.toArray();

    for (int i = 0; i < connections_.length; i++) {
      ((ClientConnection) connections_[i]).close();
    }

    connections.clear();
  }

  /**
   * Closes the manager.This makes the manager to invoke
   * closeConnections()
   */
  public synchronized void close() {
    isRunning = false;
    try {
      ss.close();
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ConnectionManager.close()", P4Logger.exceptionTrace(e));
      }
    }
    closeConnections();
  }

  public String getTransportProtokol() {
    return P4ObjectBroker.transportType;
  }

  public Socket getSocket(String _type, String host, int port, Properties properties) throws IOException {
    Socket s;
    if (!_type.equals("") && !_type.equalsIgnoreCase(P4ObjectBroker.DEFAULT_COMMUNICATION_LAYER)) {
      if (_type.equals("https")) {
        properties.put("Host", host);
        properties.put("Port", String.valueOf(port));
      }
      s = portManager.getRealSocket(host, port, _type, properties);
    } else {
      s = new Socket(host, port);
    }

    return s;
  }

  protected synchronized void removeConnection(ClientConnection c) {
    String key;

    for (Enumeration enumeration = connections.keys(); enumeration.hasMoreElements();) {
      key = (String) enumeration.nextElement();

      if (c.equals(connections.get(key))) {
        connections.remove(key);
      }
    }
  }

  /**
   * Thread main method.
   * Acts like a listener on host:port
   */
  public void run() {
    while (isRunning) {
      try {
        Socket socket = ss.accept();
         if(P4Logger.getLocation().beInfo()) {
           P4Logger.getLocation().infoT("ConnectionManager.accept()","New connection accepted from " + socket.getInetAddress().getHostAddress()+ ":" + socket.getPort() + " starting initialization...");
         }
        if (!isRunning) {
          socket.close();
        }

        StringBuffer _id = new StringBuffer(P4ObjectBroker.transportType);
        _id.append(":");
        _id.append(hostNameIP);
        _id.append(":");
        _id.append(port);
        String id = _id.toString();
        ClientConnection con = new ClientConnection(socket, id.getBytes(), P4ObjectBroker.transportType, true);
        synchronized(this) {
          if (!isRunning) {
            con.close();
            return;
          }
          if (connections.containsKey(con.connectionId) && !con.connectionId.equals(this.profileId)){
            con.close();
            P4Logger.getLocation().debugT("ConnectionManager.run()","Closed connection with duplicate connection id "+ con.destinationId);
          } else {
            if(P4Logger.getLocation().bePath()) {
              P4Logger.getLocation().pathT("ConnectionManager.accept()","New connection accepted: " + con);
            }
            connections.put(con.connectionId, con);
            Thread thr = new Thread(con);
            thr.setDaemon(true);
            thr.start();
          }
        }
      } catch (IOException ioex) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ConnectionManager.run()", P4Logger.exceptionTrace(ioex));
        }
        // ok
        continue;
      }
    }
  }

}

