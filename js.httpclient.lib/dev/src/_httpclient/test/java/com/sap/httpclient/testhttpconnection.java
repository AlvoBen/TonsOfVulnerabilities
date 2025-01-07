package com.sap.httpclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.net.ProtocolSocketFactory;
import com.sap.httpclient.net.TimeoutSocketFactory;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.exception.ConnectTimeoutException;

/**
 * Unit tests for {@link HttpConnection}.
 */
public class TestHttpConnection extends HttpClientTestBase {

  public TestHttpConnection(String testName) throws Exception {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttpConnection.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHttpConnection.class);
  }

  public void testConstructThenClose() {
    this.server.setHttpService(new EchoService());
    Protocol testProtocol = Protocol.getProtocol("http");
    HttpConnection conn = new HttpConnection(this.server.getLocalAddress(), this.server.getLocalPort(), testProtocol);
    conn.close();
    assertTrue(!conn.isOpen());
  }

  public void testConnTimeoutRelease() {
    this.server.setHttpService(new EchoService());
    // create a custom net that will delay for 500 milliseconds
    Protocol testProtocol = new Protocol("timeout",
            new DelayedProtocolSocketFactory(500,
                    Protocol.getProtocol("http").getSocketFactory()),
            this.server.getLocalPort());

    NoHostHttpConnectionManager connectionManager = new NoHostHttpConnectionManager();
    connectionManager.setConnection(new HttpConnection(this.server.getLocalAddress(), this.server.getLocalPort(), testProtocol));
    this.client.setHttpConnectionManager(connectionManager);
    client.getHostConfiguration().setHost(this.server.getLocalAddress(), this.server.getLocalPort(), testProtocol);
    client.getHttpConnectionManager().getParams().setConnectionTimeout(1);

    try {
      GET get = new GET();
      client.executeMethod(get);
      fail("Should have timed out");
    } catch (IOException e) {
      /* should fail */
      assertTrue(e instanceof ConnectTimeoutException);
      assertTrue(connectionManager.isConnectionReleased());
    }
  }

  public void testConnTimeout() {

    // create a custom net that will delay for 500 milliseconds
    Protocol testProtocol = new Protocol("timeout",
            new DelayedProtocolSocketFactory(500,
                    Protocol.getProtocol("http").getSocketFactory()),
            this.server.getLocalPort());

    HttpConnection conn = new HttpConnection(this.server.getLocalAddress(), this.server.getLocalPort(), testProtocol);
    // 1 ms is short enough to make this fail
    conn.getParams().setConnectionTimeout(1);
    try {
      conn.open();
      fail("Should have timed out");
    } catch (IOException e) {
      assertTrue(e instanceof ConnectTimeoutException);
      /* should fail */
    }
  }

  public void testForIllegalStateExceptions() {
    HttpConnection conn = new HttpConnection(this.server.getLocalAddress(), this.server.getLocalPort());
    try {
      conn.getOutputStream();
      fail("getOutputStream did not throw the expected exception");
    } catch (IllegalStateException is_OK) {
      // $JL-EXC$
    } catch (IOException ex) {
      fail("getOutputStream did not throw the expected exception");
    }
    try {
      new ChunkedOutputStream(conn.getOutputStream());
      fail("getOutputStream(true) did not throw the expected exception");
    } catch (IllegalStateException is_OK) {
      // $JL-EXC$
    } catch (IOException ex) {
      fail("getOutputStream(true) did not throw the expected exception");
    }
    try {
      conn.getInputStream();
      fail("getInputStream() did not throw the expected exception");
    } catch (IllegalStateException is_OK) {
      // $JL-EXC$
    } catch (IOException ex) {
      fail("getInputStream() did not throw the expected exception");
    }

  }

  /**
   * A ProtocolSocketFactory that delays before creating a socket.
   */
  class DelayedProtocolSocketFactory implements ProtocolSocketFactory {

    private int delay;
    private ProtocolSocketFactory realFactory;

    public DelayedProtocolSocketFactory(int delay, ProtocolSocketFactory realFactory) {
      this.delay = delay;
      this.realFactory = realFactory;
    }

    public Socket createSocket(String host,
                               int port,
                               InetAddress localAddress,
                               int localPort) throws IOException {

      synchronized (this) {
        try {
          this.wait(delay);
        } catch (InterruptedException is_OK) {
          // $JL-EXC$
        }
      }
      return realFactory.createSocket(host, port, localAddress, localPort);
    }

    public Socket createSocket(final String host,
                               final int port,
                               final InetAddress localAddress,
                               final int localPort,
                               final HttpClientParameters params) throws IOException {

      if (params == null) {
        throw new IllegalArgumentException("Parameters may not be null");
      }
      int timeout = params.getConnectionTimeout();
      TimeoutSocketFactory.SocketTask task = new TimeoutSocketFactory.SocketTask() {
        public void doit() throws IOException {
          synchronized (this) {
            try {
              this.wait(delay);
            } catch (InterruptedException e) {
              // $JL-EXC$
            }
          }
          setSocket(realFactory.createSocket(host, port, localAddress, localPort));
        }
      };
      return TimeoutSocketFactory.createSocket(task, timeout);
    }

    public Socket createSocket(String host, int port) throws IOException {
      synchronized (this) {
        try {
          this.wait(delay);
        } catch (InterruptedException is_OK) {
          // $JL-EXC$
        }
      }
      return realFactory.createSocket(host, port);
    }

  }

}