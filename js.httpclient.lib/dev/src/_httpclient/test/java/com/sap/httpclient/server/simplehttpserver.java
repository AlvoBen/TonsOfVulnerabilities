﻿package com.sap.httpclient.server;

import com.sap.tc.logging.Location;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple, but extensible HTTP server, mostly for testing purposes.
 */
public class SimpleHttpServer implements Runnable {
  private static final Location LOG = Location.getLocation(SimpleHttpServer.class);

  private String testname = "Simple test";
  private long count = 0;
  private ServerSocket listener = null;
  private Thread t;
  private ThreadGroup tg;
  private boolean stopped = false;

  private SimpleConnSet connections = new SimpleConnSet();

  private HttpRequestHandler requestHandler = null;

  /**
   * Creates a new HTTP server instance, using an arbitrary free TCP port
   *
   * @throws IOException if anything goes wrong during initialization
   */
  public SimpleHttpServer() throws IOException {
    this(null, 0);
  }

  /**
   * Creates a new HTTP server instance, using the specified socket
   * factory and the TCP port
   *
	 * @param socketfactory the socket factory
   * @param port Desired TCP port
   * @throws IOException if anything goes wrong during initialization
   */
  public SimpleHttpServer(SimpleSocketFactory socketfactory, int port) throws IOException {
    if (socketfactory == null) {
      socketfactory = new SimplePlainSocketFactory();
    }
    listener = socketfactory.createServerSocket(port);
    if (LOG.beDebug()) {
      LOG.debugT("Starting test HTTP server on port " + getLocalPort());
    }
    tg = new ThreadGroup("SimpleHttpServer thread group");
    t = new Thread(tg, this, "SimpleHttpServer listener");
    t.setDaemon(true);
    t.start();
  }

  /**
   * Creates a new HTTP server instance, using the specified TCP port
   *
   * @param port Desired TCP port
   * @throws IOException if anything goes wrong during initialization
   */
  public SimpleHttpServer(int port) throws IOException {
    this(null, port);
  }

  public String getTestname() {
    return this.testname;
  }

  public void setTestname(final String testname) {
    this.testname = testname;
  }

  /**
   * Returns the TCP port that this HTTP server instance is bound to.
   *
   * @return TCP port, or -1 if not running
   */
  public int getLocalPort() {
    return listener.getLocalPort();
  }

  /**
   * Returns the IP address that this HTTP server instance is bound to.
   *
   * @return String representation of the IP address or <code>null</code> if not running
   */
  public String getLocalAddress() {
    InetAddress address = listener.getInetAddress();
    // Ugly work-around for older JDKs
    byte[] octets = address.getAddress();
    if ((octets[0] == 0)
            && (octets[1] == 0)
            && (octets[2] == 0)
            && (octets[3] == 0)) {
      return "localhost";
    } else {
      return address.getHostAddress();
    }
  }

  /**
   * Checks if this HTTP server instance is running.
   *
   * @return true/false
   */
  public boolean isRunning() {
		return (t != null) && (t.isAlive());
	}

  /**
   * Stops this HTTP server instance.
   */
  public synchronized void destroy() {
    if (stopped) {
      return;
    }

    this.stopped = true;
    if (LOG.beDebug()) {
      LOG.debugT("Stopping test HTTP server on port " + getLocalPort());
    }
    tg.interrupt();

    if (listener != null) {
      try {
        listener.close();
      } catch (IOException e) {
        // $JL-EXC$
      }
    }
    this.connections.shutdown();
  }

  /**
   * Returns the currently used HttpRequestHandler by this SimpleHttpServer
   *
   * @return The used HttpRequestHandler, or null.
   */
  public HttpRequestHandler getRequestHandler() {
    return requestHandler;
  }

  /**
   * Sets the HttpRequestHandler to be used for this SimpleHttpServer.
   *
   * @param rh Request handler to be used, or null to disable.
   */
  public void setRequestHandler(HttpRequestHandler rh) {
    this.requestHandler = rh;
  }

  public void setHttpService(HttpService service) {
    setRequestHandler(new HttpServiceHandler(service));
  }

  public void run() {
    try {
      while (!this.stopped && !Thread.interrupted()) {
        Socket socket = listener.accept();
        try {
          if (this.requestHandler == null) {
            socket.close();
            break;
          }
          SimpleHttpServerConnection conn = new SimpleHttpServerConnection(socket);
          this.connections.addConnection(conn);

          Thread t = new SimpleConnectionThread(tg,
                  this.testname + " thread " + this.count,
                  conn,
                  this.connections,
                  this.requestHandler);
          t.setDaemon(true);
          t.start();
        } catch (IOException e) {
          // $JL-EXC$
          LOG.errorT("I/O error: " + e.getMessage());
        }
        this.count++;
        Thread.sleep(100);
      }
    } catch (InterruptedException accept) {
      // $JL-EXC$
    } catch (IOException e) {
      if (!stopped) {
        LOG.errorT("I/O error: " + e.getMessage());
      }
    } finally {
      destroy();
    }
  }
}
