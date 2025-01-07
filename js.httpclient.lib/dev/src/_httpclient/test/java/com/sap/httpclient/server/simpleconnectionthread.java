package com.sap.httpclient.server;

import com.sap.tc.logging.Location;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Simple HTTP connection thread.
 */
public class SimpleConnectionThread extends Thread {

  private static final Location LOG = Location.getLocation(SimpleConnectionThread.class);

  public static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";

  private SimpleHttpServerConnection conn = null;
  private SimpleConnSet connpool = null;
  private HttpRequestHandler handler = null;
  transient boolean stopped;

  public SimpleConnectionThread(final ThreadGroup tg,
                                final String name,
                                final SimpleHttpServerConnection conn,
                                final SimpleConnSet connpool,
                                final HttpRequestHandler handler) {
    super(tg, name);
    if (conn == null) {
      throw new IllegalArgumentException("Connection may not be null");
    }
    if (connpool == null) {
      throw new IllegalArgumentException("Connection pool not be null");
    }
    if (handler == null) {
      throw new IllegalArgumentException("Request handler may not be null");
    }
    this.conn = conn;
    this.connpool = connpool;
    this.handler = handler;
    this.stopped = false;
  }

	public synchronized void destroy() {
    if (this.stopped) {
      return;
    }
    this.stopped = true;
    if (conn != null) {
      conn.close();
      conn = null;
    }
    interrupt();
  }

  public void run() {
    try {
      do {
        this.conn.setKeepAlive(false);
        SimpleRequest request = this.conn.readRequest();
        if (request != null) {
          this.handler.processRequest(this.conn, request);
        }
      } while (this.conn.isKeepAlive());
    } catch (InterruptedIOException e) {
      // $JL-EXC$
    } catch (IOException e) {
      if (!this.stopped && !isInterrupted() && LOG.beWarning()) {
        LOG.warningT("[" + getName() + "] I/O error: " + e.getMessage());
      }
    } finally {
      destroy();
      this.connpool.removeConnection(this.conn);
    }
  }

}