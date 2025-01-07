package com.sap.httpclient.server;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple list of connections.
 */
public class SimpleConnList {

  private List<SimpleHttpServerConnection> connections = new ArrayList<SimpleHttpServerConnection>();

  public SimpleConnList() {
    super();
  }

  public synchronized void addConnection(final SimpleHttpServerConnection conn) {
    this.connections.add(conn);
  }

  public synchronized void removeConnection(final SimpleHttpServerConnection conn) {
    this.connections.remove(conn);
  }

  public synchronized SimpleHttpServerConnection removeLast() {
    int s = this.connections.size();
    if (s > 0) {
      return this.connections.remove(s - 1);
    } else {
      return null;
    }
  }

  public synchronized SimpleHttpServerConnection removeFirst() {
    int s = this.connections.size();
    if (s > 0) {
      return this.connections.remove(0);
    } else {
      return null;
    }
  }

  public synchronized void shutdown() {
		for (SimpleHttpServerConnection conn : this.connections) {
			conn.close();
		}
		this.connections.clear();
  }

}