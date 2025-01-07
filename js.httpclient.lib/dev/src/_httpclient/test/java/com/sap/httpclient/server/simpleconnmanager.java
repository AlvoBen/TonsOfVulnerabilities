package com.sap.httpclient.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple connection manager.
 */
public class SimpleConnManager {

  private Map<SimpleHost, SimpleConnList> connsets = new HashMap<SimpleHost, SimpleConnList>();

  public SimpleConnManager() {
    super();
  }

  public synchronized SimpleHttpServerConnection openConnection(final SimpleHost host) throws IOException {
    if (host == null) {
      throw new IllegalArgumentException("Host may not be null");
    }
    SimpleHttpServerConnection conn = null;
    SimpleConnList connlist = this.connsets.get(host);
    if (connlist != null) {
      conn = connlist.removeFirst();
      if (conn != null && !conn.isOpen()) {
        conn = null;
      }
    }
    if (conn == null) {
      Socket socket = new Socket(host.getHostName(), host.getPort());
      conn = new SimpleHttpServerConnection(socket);
    }
    return conn;
  }

  public synchronized void releaseConnection(final SimpleHost host,
                                             final SimpleHttpServerConnection conn) throws IOException {
    if (host == null) {
      throw new IllegalArgumentException("Host may not be null");
    }
    if (conn == null) {
      return;
    }
    if (!conn.isKeepAlive()) {
      conn.close();
    }
    if (conn.isOpen()) {
      SimpleConnList connlist = this.connsets.get(host);
      if (connlist == null) {
        connlist = new SimpleConnList();
        this.connsets.put(host, connlist);
      }
      connlist.addConnection(conn);
    }
  }

  public synchronized void shutdown() {
		for (SimpleConnList connlist : this.connsets.values()) {
			connlist.shutdown();
		}
		this.connsets.clear();
  }

}