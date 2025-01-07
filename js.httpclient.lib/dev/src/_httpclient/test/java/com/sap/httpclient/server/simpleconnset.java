package com.sap.httpclient.server;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple set of connections.
 */
public class SimpleConnSet {

  private Set<SimpleHttpServerConnection> connections = new HashSet<SimpleHttpServerConnection>();

  public SimpleConnSet() {
    super();
  }

  public synchronized void addConnection(final SimpleHttpServerConnection conn) {
    this.connections.add(conn);
  }

  public synchronized void removeConnection(final SimpleHttpServerConnection conn) {
    this.connections.remove(conn);
  }

  public synchronized void shutdown() {
		for (SimpleHttpServerConnection conn : connections) {
			conn.close();
		}
	}

}