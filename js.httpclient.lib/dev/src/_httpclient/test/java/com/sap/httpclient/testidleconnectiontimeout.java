package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sap.httpclient.utils.IdleConnectionStorrage;
import com.sap.httpclient.utils.IdleConnectionDisposer;
import com.sap.httpclient.exception.ConnectionPoolTimeoutException;
import com.sap.httpclient.net.connection.HttpConnectionManager;
import com.sap.httpclient.net.connection.HttpConnection;

/**
 */
public class TestIdleConnectionTimeout extends TestCase {
  /**
   *
   */
  public TestIdleConnectionTimeout() {
    super();
  }

  public TestIdleConnectionTimeout(String arg0) {
    super(arg0);
  }

  public static Test suite() {
    return new TestSuite(TestIdleConnectionTimeout.class);
  }

  /**
   * Tests that the IdleConnectionStorrage correctly closes connections.
   */
  public void testHandler() {

    TimeoutHttpConnection connection = new TimeoutHttpConnection();
    IdleConnectionStorrage storrage = new IdleConnectionStorrage();
    storrage.add(connection);
    synchronized (this) {
      try {
        this.wait(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    storrage.closeIdleConnections(100);
    assertTrue("Connection not closed", connection.isClosed());
    connection.setClosed(false);
    storrage.remove(connection);
    synchronized (this) {
      try {
        this.wait(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    storrage.closeIdleConnections(100);
    assertFalse("Connection closed", connection.isClosed());
  }

  /**
   * Tests that the IdleConnectionDisposer works correctly.
   */
  public void testTimeoutThread() {

    TimeoutHttpConnectionManager cm = new TimeoutHttpConnectionManager();
    IdleConnectionDisposer timeoutThread = new IdleConnectionDisposer();
    timeoutThread.registerConnectionManager(cm);
    timeoutThread.setTimeoutInterval(100);
    timeoutThread.start();
    synchronized (this) {
      try {
        this.wait(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertTrue("closeIdleConnections() not called", cm.closed);
    timeoutThread.unregisterConnectionManager(cm);
    cm.closed = false;
    synchronized (this) {
      try {
        this.wait(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    assertFalse("closeIdleConnections() called", cm.closed);

    timeoutThread.shutdown();
  }

  private static class TimeoutHttpConnectionManager implements HttpConnectionManager {

    public boolean closed = false;

    public void closeIdleConnections(long idleTimeout) {
      this.closed = true;
    }

    public HttpConnection getConnection(HostConfiguration hostConfiguration) {
      return null;
    }

    public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration,
                                                   long timeout) throws ConnectionPoolTimeoutException {
      return null;
    }

    public HttpClientParameters getParams() {
      return null;
    }

    public void releaseConnection(HttpConnection conn) {
    }

    public void setParams(HttpClientParameters params) {
    }
  }

  private static class TimeoutHttpConnection extends HttpConnection {

    private boolean closed = false;

    public TimeoutHttpConnection() {
      super("fake-host", 80);
    }

    /**
     * @return Returns the closed.
     */
    public boolean isClosed() {
      return closed;
    }

    /**
     * @param closed The closed to set.
     */
    public void setClosed(boolean closed) {
      this.closed = closed;
    }

    public void close() {
      closed = true;
    }
  }

}