/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.client;

import com.sap.engine.services.iiop.system.CommunicationLayer;
import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.Profile;
import com.sap.engine.services.iiop.client.portable.*;
import com.sap.engine.services.iiop.csiv2.CSIIOP.*;
import com.sap.engine.services.iiop.internal.util.IDFactoryItem;
import com.sap.engine.interfaces.csiv2.SSLSocketProvider;
import com.sap.engine.lib.util.ConcurrentHashMapLongObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.net.Socket;



/*

 * @author Ivan Atanassov
 * @version 4.0
 */
public class CommunicationLayerImpl extends CommunicationLayer implements Runnable {
  String [] urls;
  ServerSocket listenterServerSocket;
  ServerSocket listenterSSLServerSocket;
  SSLSocketProvider sslProvider = null;

  int sslPort;
  boolean sslThread = false;

  public CommunicationLayerImpl() {
    try {
      listenterServerSocket = new ServerSocket(0);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      urls = new String[] {"" + "@" + InetAddress.getLocalHost().getHostAddress() + ":" + listenterServerSocket.getLocalPort()};
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    Thread thisThread = new Thread(this);
    thisThread.setDaemon(true);
    thisThread.start();
  }

  public void setSSLProvider(String sslProviderClass, String[] sslProviderParams) {
    if (sslProviderClass != null) {
      try {
        if (sslProviderParams == null) {
          sslProvider = (SSLSocketProvider) Class.forName(sslProviderClass).newInstance();
        } else {
          final Class[] constrParams = new Class[] {String.class, String.class};
          sslProvider = (SSLSocketProvider) Class.forName(sslProviderClass).getConstructor(constrParams).newInstance(sslProviderParams);
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  private ConcurrentHashMapLongObject connectionHash = new ConcurrentHashMapLongObject();

  void connectionFailed (long connectionId) {
    connectionHash.remove(connectionId);
  }

  public org.omg.CORBA.portable.Delegate getDelegate(IOR ior) {
    Profile profile = ior.getProfile();

    String host;
    int port;

    boolean useSSL = (profile.getPort() == 0);

    if (useSSL) {
      TransportAddress sslAddress = parseSSLTransportAddress(ior.getORB(), profile);
      host = sslAddress.host_name;
      port = sslAddress.port;
    } else {
      host = profile.getHost();
      port = profile.getPort();
    }

    long key = calculateKey(host.hashCode(), port);
    try {
      if (!connectionHash.containsKey(key)) {
        synchronized (this) {
          if (!connectionHash.containsKey(key)) {
            if (useSSL) {
              if (sslProvider != null) {
                Connection con = new Connection(key, sslProvider.getSSLClientSocket(host, port), this);
                Thread connthr = new Thread(con);
                connthr.setDaemon(true);
                connthr.start();
                connectionHash.put(key, con);
              } else {
               // da hvurlia exc moje bi??
              }
            } else {
              Connection con = new Connection(key, new Socket(host, port), this);
              Thread connthr = new Thread(con);
              connthr.setDaemon(true);
              connthr.start();
              connectionHash.put(key, con);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    int versionMinor = ior.getProfile().getVersionMinor();
    Delegate delegate;
    switch (versionMinor) {
      case 0: {
        delegate = new Delegate_1_0(ior);
        break;
      }
      case 1: {
        delegate = new Delegate_1_1(ior);
        break;
      }
      case 2: {
        delegate = new Delegate_1_2(ior);
        break;
      }
      default: {
        delegate = new Delegate_1_0(ior);
      }
    }
    delegate.sender = this;
    delegate.setHostPort(host, port);

    return delegate;
  }

  public String[] getURL()  {
    return urls;
  }

  public void send(IDFactoryItem item, byte[] data, int pos, int len, String host, int port) throws IOException {
    long key = calculateKey(host.hashCode(), port);
    Connection con;
    if (connectionHash.containsKey(key)) {
      con = (Connection) connectionHash.get(key);
    } else {
      con = new Connection(key, new Socket(host, port), this);
      Thread thread = new Thread(con);
      thread.setDaemon(true);
      thread.start();
      connectionHash.put(key, con);
    }
    con.send(item, data, pos, len);
  }

  public int openSSLServerSocket(int port) throws IOException {
    if (listenterSSLServerSocket == null) {
      listenterSSLServerSocket = sslProvider.getSSLServerSocket(port);
      sslPort = listenterSSLServerSocket.getLocalPort();

      if (listenterSSLServerSocket != null) {
        Thread thisThread = new Thread(this);
        thisThread.setDaemon(true);
        thisThread.start();
      }
    }

    return sslPort;
  }

  public void run() {
    boolean isSSLThread = false;
    synchronized (this) {
      isSSLThread = sslThread;
      if (!sslThread) {
        sslThread = true;
      }
    }

    if (isSSLThread) {
      if (listenterSSLServerSocket != null) {
        while (true) {
          try {
            Socket socket = listenterSSLServerSocket.accept();
            (new Thread(new Connection(socket))).start();
          } catch (IOException e) {
            continue;
          }
        }
      }
    } else {
      while (true) {
        try {
          Socket socket = listenterServerSocket.accept();
          (new Thread(new Connection(socket))).start();
        } catch (IOException e) {
          continue;
        }
      }
    }
  }

  private long calculateKey(int int1, int int2) {
    long result = int1;
    result = result << 32;
    result = result | int2;
    return result;
  }

}
