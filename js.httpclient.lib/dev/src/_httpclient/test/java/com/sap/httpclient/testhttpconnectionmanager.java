package com.sap.httpclient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.net.SecureSocketFactory;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.net.connection.HttpConnectionManagerImpl;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import com.sap.httpclient.exception.ConnectionPoolTimeoutException;
import com.sap.httpclient.exception.ConnectTimeoutException;
import com.sap.httpclient.exception.HttpException;

/**
 * Unit tests for {@link com.sap.httpclient.net.connection.HttpConnectionManager}.
 */
public class TestHttpConnectionManager extends HttpClientTestBase {

  public TestHttpConnectionManager(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttpConnectionManager.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHttpConnectionManager.class);
  }

  /**
   * Test that the CONNECT correctly releases connections when CONNECT fails.
	 *
	 * @throws Exception if an exception occures
   */
  public void testConnectMethodFailureRelease() throws Exception {

    HttpConnectionManagerImpl mgr = new HttpConnectionManagerImpl();
    mgr.getParams().setParameter(Parameters.MAX_TOTAL_CONNECTIONS, 1);
    client.setHttpConnectionManager(mgr);
    this.server.setHttpService(new RejectConnectService());

    // we're going to execute a connect method against the localhost, assuming
    // that CONNECT is not supported.  This should test the setConnectResponse()
    // code on HttpMethodImpl.
    client.getHostConfiguration().setProxy(server.getLocalAddress(), server.getLocalPort());
    // we must set the host to a secure destination or the CONNECT method
    // will not be used
    client.getHostConfiguration().setHost("notARealHost",
            1234,
            new Protocol("https", new FakeSecureSocketFactory(), 443));

    GET get = new GET("/");
    try {
      assertTrue(client.executeMethod(get) != 200);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Error executing connect: " + e);
    }
    // this should calling releaseConnection() releases the connection
    try {
      get.releaseConnection();
      mgr.getConnectionWithTimeout(client.getHostConfiguration(), 1).releaseConnection();
    } catch (ConnectTimeoutException e1) {
      fail("Connection should have been available.");
    }
    get = new GET("/");
    try {
      assertTrue(client.executeMethod(get) != 200);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Error executing connect: " + e);
    }
    // make sure reading the response fully releases the connection
    try {
      get.getResponseBodyAsString();
      mgr.getConnectionWithTimeout(client.getHostConfiguration(), 1).releaseConnection();
    } catch (ConnectTimeoutException e1) {
      fail("Connection should have been available.");
    }
    get = new GET("/");
    try {
      assertTrue(client.executeMethod(get) != 200);
    } catch (IOException e) { // $JL-EXC$
      e.printStackTrace();
      fail("Error executing connect: " + e);
    }
    // make sure closing the outgoing stream releases the connection
    try {
      get.getResponseBodyAsStream().close();
      mgr.getConnectionWithTimeout(client.getHostConfiguration(), 1).releaseConnection();
    } catch (ConnectTimeoutException e) {
      fail("Connection should have been available.");
    } catch (IOException e) {
      e.printStackTrace();
      fail("Close connection failed: " + e);
    }
  }

  public void testGetConnection() {
    HttpConnectionManagerImpl mgr = new HttpConnectionManagerImpl();
    HostConfiguration hostConfiguration = new HostConfiguration();
    hostConfiguration.setHost("www.nosuchserver.com", 80, "http");
    // Create a new connection
    HttpConnection conn = mgr.getConnection(hostConfiguration);
    // Validate the connection properties
    assertEquals("Host", "www.nosuchserver.com", conn.getHost());
    assertEquals("Port", 80, conn.getPort());
    // Release the connection
    mgr.releaseConnection(conn);
    // Create a new connection
    hostConfiguration.setHost("www.nosuchserver.com", -1, "https");
    conn = mgr.getConnection(hostConfiguration);
    // Validate the connection properties
    assertEquals("Host", "www.nosuchserver.com", conn.getHost());
    assertEquals("Port", 443, conn.getPort());
    // Release the connection
    mgr.releaseConnection(conn);
    // Create a new connection
    hostConfiguration.setHost("www.nowhere.org", 8080, "http");
    conn = mgr.getConnection(hostConfiguration);
    // Validate the connection properties
    assertEquals("Host", "www.nowhere.org", conn.getHost());
    assertEquals("Port", 8080, conn.getPort());
    // Release the connection
    mgr.releaseConnection(conn);

  }

  public void testDroppedThread() throws Exception {

    this.server.setHttpService(new EchoService());
    HttpConnectionManagerImpl mthcm = new HttpConnectionManagerImpl();
    client.setHttpConnectionManager(mthcm);
    WeakReference<HttpConnectionManagerImpl> wr = new WeakReference<HttpConnectionManagerImpl>(mthcm);
    GET method = new GET("/");
    client.executeMethod(method);
    method.releaseConnection();
    mthcm = null;
    client = null;
    method = null;

    // start getting memory in order to triger garbage collection,
    // because I'm now allowed to use System.gc() -> zaradi shibania Jlin prio 2
    try {
      int big = 1024*1024*1024; // 1 GB
      while (big > 0) {
        byte[] b = new byte[big];
        big = big*2;
      }
    } catch (OutOfMemoryError oome) {
      // $JL-EXC$
    }

    // this sleep appears to be necessary in order to give the JVM
    // time to clean up the miscellaneous pointers to the connection manager
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      fail("shouldn't be interrupted.");
    }
    Object connectionManager = wr.get();
    assertNull("connectionManager should be null", connectionManager);
  }

  public void testWriteRequestReleaseConnection() {

    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    client.setHttpConnectionManager(connectionManager);
    GET get = new GET("/") {
      protected boolean writeRequestBody(HttpState state, HttpConnection conn) throws IOException {
        throw new IOException("Oh no!!");
      }
    };
    try {
      client.executeMethod(get);
      fail("An exception should have occurred.");
    } catch (HttpException e) {
      e.printStackTrace();
      fail("HttpException should not have occurred: " + e);
    } catch (IOException is_OK) {
      // $JL-EXC$
    }
    try {
      connectionManager.getConnectionWithTimeout(client.getHostConfiguration(), 1);
    } catch (ConnectTimeoutException e) {
      e.printStackTrace();
      fail("Connection was not released: " + e);
    }

  }

  public void testReleaseConnection() {

    this.server.setHttpService(new EchoService());
    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    client.setHttpConnectionManager(connectionManager);
    // we shouldn't have to wait if a connection is available
    client.getParams().setConnectionManagerTimeout(1);
    GET getMethod = new GET("/");
    try {
      client.executeMethod(getMethod);
    } catch (Exception e) {
      fail("error reading from server: " + e);
    }
    try {
      // this should fail quickly since the connection has not been released
      client.executeMethod(getMethod);
      fail("a httpConnection should not be available");
    } catch (ConnectTimeoutException is_OK) {
      // $JL-EXC$
    } catch (HttpException e) {
      fail("error reading from server; " + e);
    } catch (IOException e) {
      e.printStackTrace();
      fail("error reading from server; " + e);
    }
    // this should release the connection
    getMethod.releaseConnection();
    getMethod = new GET("/");
    try {
      // this should fail quickly if the connection has not been released
      client.executeMethod(getMethod);
    } catch (HttpException e) {
      fail("httpConnection does not appear to have been released: " + e);
    } catch (IOException e) {
      fail("error reading from server; " + e);
    }

  }

  /**
   * Makes sure that a connection gets released after the content of the body is read.
	 *
	 * @throws Exception if an exception occures
   */
  public void testResponseAutoRelease() throws Exception {

    this.server.setHttpService(new EchoService());
    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    client.setHttpConnectionManager(connectionManager);
    // we shouldn't have to wait if a connection is available
    client.getParams().setConnectionManagerTimeout(1);
    GET getMethod = new GET("/");
    try {
      client.executeMethod(getMethod);
    } catch (Exception e) {
      fail("error reading from server: " + e);
    }
    // this should release the connection
    getMethod.getResponseBody();
    getMethod = new GET("/");
    try {
      // this should fail quickly if the connection has not been released
      client.executeMethod(getMethod);
    } catch (HttpException e) {
      fail("httpConnection does not appear to have been released: " + e);
    } catch (IOException e) {
      fail("error reading from server; " + e);
    }

  }

  /**
   * Tests the MultipleConnectionManager's ability to reclaim unused
   * connections.
   */
  public void testConnectionReclaiming() {

    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    connectionManager.getParams().setMaxTotalConnections(1);
    HostConfiguration host1 = new HostConfiguration();
    host1.setHost("host1", -1, "http");
    HostConfiguration host2 = new HostConfiguration();
    host2.setHost("host2", -1, "http");
    HttpConnection connection = connectionManager.getConnection(host1);
    // now release this connection
    connection.releaseConnection();
    connection = null;
    try {
      // the connection from host1 should be reclaimed
      connection = connectionManager.getConnectionWithTimeout(host2, 100);
    } catch (ConnectTimeoutException e) {
      e.printStackTrace();
      fail("a httpConnection should have been available: " + e);
    }
  }

  /**
   * Tests that {@link HttpConnectionManagerImpl#shutdownAll()} closes all resources
   * and makes all connection mangers unusable.
   */
  public void testShutdownAll() {

    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    connectionManager.getParams().setMaxTotalConnections(1);
    HostConfiguration host1 = new HostConfiguration();
    host1.setHost("host1", -1, "http");
    // hold on to the only connection
    HttpConnection connection = connectionManager.getConnection(host1);
    // wait for a connection on another thread
    GetConnectionThread getConn = new GetConnectionThread(host1, connectionManager, 0);
    getConn.start();
    HttpConnectionManagerImpl.shutdownAll();
    // now release this connection, this should close the connection, but have no other effect
    connection.releaseConnection();
    connection = null;
    try {
      getConn.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // this thread should have caught an exception without getting a connection
    assertNull("Not connection should have been checked out", getConn.getConnection());
    assertNotNull("There should have been an exception", getConn.getException());
    try {
      connectionManager.getConnection(host1);
      fail("An exception should have occurred");
    } catch (Exception is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests that {@link HttpConnectionManagerImpl#shutdown()} closes all resources
   * and makes the connection manger unusable.
   */
  public void testShutdown() {

    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    connectionManager.getParams().setMaxTotalConnections(1);
    HostConfiguration host1 = new HostConfiguration();
    host1.setHost("host1", -1, "http");
    // hold on to the only connection
    HttpConnection connection = connectionManager.getConnection(host1);
    // wait for a connection on another thread
    GetConnectionThread getConn = new GetConnectionThread(host1, connectionManager, 0);
    getConn.start();
    connectionManager.shutdown();
    // now release this connection, this should close the connection, but have no other effect
    connection.releaseConnection();
    connection = null;
    try {
      getConn.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // this thread should have caught an exception without getting a connection
    assertNull("Not connection should have been checked out", getConn.getConnection());
    assertNotNull("There should have been an exception", getConn.getException());
    try {
      connectionManager.getConnection(host1);
      fail("An exception should have occurred");
    } catch (Exception is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests the MultipleConnectionManager's ability to restrict the maximum number
   * of connections.
   */
  public void testMaxConnections() {

    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    connectionManager.getParams().setMaxTotalConnections(2);
    HostConfiguration host1 = new HostConfiguration();
    host1.setHost("host1", -1, "http");
    HostConfiguration host2 = new HostConfiguration();
    host2.setHost("host2", -1, "http");
    connectionManager.getConnection(host1);
    HttpConnection connection2 = connectionManager.getConnection(host2);
    try {
      // this should fail quickly since the connection has not been released
      connectionManager.getConnectionWithTimeout(host2, 100);
      fail("ConnectionPoolTimeoutException should not be available");
    } catch (ConnectionPoolTimeoutException is_OK) {
      // $JL-EXC$
    }
    // release one of the connections
    connection2.releaseConnection();
    connection2 = null;
    try {
      // there should be a connection available now
      connection2 = connectionManager.getConnectionWithTimeout(host2, 100);
    } catch (ConnectionPoolTimeoutException e) {
      e.printStackTrace();
      fail("a httpConnection should have been available: " + e);
    }
  }

  /**
   * Tests the MultipleConnectionManager's ability to restrict the maximum number
   * of connections per host.
	 *
	 * @throws Exception if an exception occures
   */
  public void testMaxConnectionsPerHost() throws Exception {

    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    connectionManager.getParams().setMaxTotalConnections(100);
    HostConfiguration host1 = new HostConfiguration();
    host1.setHost("host1", -1, "http");
    HostConfiguration host2 = new HostConfiguration();
    host2.setHost("host2", -1, "http");
    HostConfiguration host3 = new HostConfiguration();
    host3.setHost("host3", -1, "http");
    connectionManager.getParams().setMaxConnectionsPerHost(host1, 3);
    connectionManager.getParams().setMaxConnectionsPerHost(host2, 2);
    // Host1
    connectionManager.getConnectionWithTimeout(host1, 1000);
    connectionManager.getConnectionWithTimeout(host1, 1000);
    connectionManager.getConnectionWithTimeout(host1, 1000);
    try {
      // this should fail quickly since the connection has not been released
      connectionManager.getConnectionWithTimeout(host1, 100);
      fail("Connections should not be available, and a ConnectionPoolTimeoutException should be thrown");
    } catch (ConnectionPoolTimeoutException is_OK) {
      // $JL-EXC$
    }
    // Host2
    connectionManager.getConnectionWithTimeout(host2, 1000);
    connectionManager.getConnectionWithTimeout(host2, 1000);
    try {
      // this should fail quickly since the connection has not been released
      connectionManager.getConnectionWithTimeout(host2, 100);
      fail("Connections should not be available, and a ConnectionPoolTimeoutException should be thrown");
    } catch (ConnectionPoolTimeoutException is_OK) {
      // $JL-EXC$
    }
    // Host3 (should use the default per host value)
    connectionManager.getConnectionWithTimeout(host3, 1000);
    try {
      // this should fail quickly since the connection has not been released
      connectionManager.getConnectionWithTimeout(host3, 100);
      fail("Connections should not be available, and a ConnectionPoolTimeoutException should be thrown");
    } catch (ConnectionPoolTimeoutException is_OK) {
      // $JL-EXC$
    }
  }

  public void testHostReusePreference() {

    final HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    connectionManager.getParams().setMaxTotalConnections(1);
    final HostConfiguration host1 = new HostConfiguration();
    host1.setHost("host1", -1, "http");
    final HostConfiguration host2 = new HostConfiguration();
    host2.setHost("host2", -1, "http");
    HttpConnection connection = connectionManager.getConnection(host1);
    GetConnectionThread getHost1 = new GetConnectionThread(host1, connectionManager, 200);
    GetConnectionThread getHost2 = new GetConnectionThread(host2, connectionManager, 200);
    getHost2.start();
    getHost1.start();
    // give the threads some time to startup
    try {
      Thread.sleep(100);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    // after the connection to host1 is released it should be given to getHost1
    connection.releaseConnection();
    connection = null;
    try {
      getHost1.join();
      getHost2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertNotSame("Connection should have been given to someone", getHost1.getConnection(), getHost2.getConnection());
    assertNotNull("Connection should have been given to host1", getHost1.getConnection());
    assertNull("Connection should NOT have been given to host2", getHost2.getConnection());

  }

  public void testMaxConnectionsPerServer() {

    this.server.setHttpService(new EchoService());
    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setDefaultMaxConnectionsPerHost(1);
    client.setHttpConnectionManager(connectionManager);
    // we shouldn't have to wait if a connection is available
    client.getParams().setConnectionManagerTimeout(1);
    GET getMethod = new GET("/");
    try {
      client.executeMethod(getMethod);
    } catch (Exception e) {
      fail("error reading from server: " + e);
    }
    GET getMethod2 = new GET("/");
    try {
      // this should fail quickly since the connection has not been released
      client.executeMethod(getMethod2);
      fail("a httpConnection should not be available");
    } catch (ConnectTimeoutException is_OK) {
      // $JL-EXC$
    } catch (HttpException e) {
      fail("error reading from server; " + e);
    } catch (IOException e) {
      fail("error reading from server; " + e);
    }

  }

  public void testDeleteClosedConnections() {

    HttpConnectionManagerImpl manager = new HttpConnectionManagerImpl();
    HttpConnection conn = manager.getConnection(client.getHostConfiguration());
    assertEquals("connectionsInPool", manager.getConnectionsInPool(), 0);
    assertEquals("connectionsInPool(host)", manager.getConnectionsInPool(client.getHostConfiguration()), 0);
    conn.close();
    conn.releaseConnection();
    assertEquals("connectionsInPool", manager.getConnectionsInPool(), 1);
    assertEquals("connectionsInPool(host)", manager.getConnectionsInPool(client.getHostConfiguration()), 1);
    manager.removeClosedConnections();
    assertEquals("connectionsInPool", manager.getConnectionsInPool(), 0);
    assertEquals("connectionsInPool(host)", manager.getConnectionsInPool(client.getHostConfiguration()), 0);
  }

  public void testReclaimUnusedConnection() {

    this.server.setHttpService(new EchoService());
    HttpConnectionManagerImpl connectionManager = new HttpConnectionManagerImpl();
    connectionManager.getParams().setParameter(Parameters.MAX_TOTAL_CONNECTIONS, 1);
    client.setHttpConnectionManager(connectionManager);
    // we shouldn't have to wait if a connection is available
    client.getParams().setConnectionManagerTimeout(30000);
    GET getMethod = new GET("/");
    try {
      client.executeMethod(getMethod);
    } catch (Exception e) {
      fail("error reading from server: " + e);
    }
    getMethod = new GET("/");
    // we didn't explicitly release the connection, but it should be
    // reclaimed by the garbage collector
    Runtime.getRuntime().gc();
    try {
      client.executeMethod(getMethod);
    } catch (HttpException e) {
      fail("httpConnection does not appear to have been reclaimed by the GC: " + e);
    } catch (IOException e) {
      fail("error reading from server; " + e);
    }

  }

  public void testGetFromMultipleThreads() {

    this.server.setHttpService(new EchoService());
    client.setHttpConnectionManager(new HttpConnectionManagerImpl());
    ExecuteMethodThread[] threads = new ExecuteMethodThread[10];
    for (int i = 0; i < threads.length; i++) {
      GET method = new GET("/");
      method.setFollowRedirects(true);
      threads[i] = new ExecuteMethodThread(method, client);
      threads[i].start();
    }
		for (ExecuteMethodThread thread : threads) {
			try {
				// wait until this thread finishes. we'll give it 10 seconds,
				// but it shouldn't take that long
				thread.join(10000);
			} catch (InterruptedException e) {
				// $JL-EXC$
			}
			// make sure an exception did not occur
			Exception e = thread.getException();
			if (e != null) {
				fail("An error occured in the get: " + e);
			}
			// we should have a 200 status
			assertEquals(thread.getMethod().getStatusCode(), HttpStatus.SC_OK);
		}
	}

  public void testTimeout() {
    HttpConnectionManagerImpl mgr = new HttpConnectionManagerImpl();
    mgr.getParams().setDefaultMaxConnectionsPerHost(2);
    try {
      HostConfiguration hostConfig = new HostConfiguration();
      hostConfig.setHost("www.nosuchserver.com", 80, "http");
      mgr.getConnection(hostConfig);
      mgr.getConnection(hostConfig);
      mgr.getConnectionWithTimeout(hostConfig, 1000);
      fail("Expected an HttpException.");
    } catch (ConnectTimeoutException is_OK) {
      // $JL-EXC$
    }
  }

  static class FakeSecureSocketFactory implements SecureSocketFactory {

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException {
      throw new IllegalStateException("createSocket() should never have been called.");
    }

    public Socket createSocket(String host, int port) throws IOException {
      throw new IllegalStateException("createSocket() should never have been called.");
    }

    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
            throws IOException {
      throw new IllegalStateException("createSocket() should never have been called.");
    }

    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort,
                               HttpClientParameters params)
            throws IOException {
      throw new IllegalStateException("createSocket() should never have been called.");
    }
  }

  static class RejectConnectService extends EchoService {
    public boolean process(SimpleRequest request, SimpleResponse response) throws IOException {
      if (request.getRequestLine().getMethod().equalsIgnoreCase("CONNECT")) {
        response.setStatusLine(request.getRequestLine().getHttpVersion(), HttpStatus.SC_METHOD_NOT_ALLOWED);
        response.setHeader(new Header("Connection", "close"));
        return true;
      } else {
        return super.process(request, response);
      }
    }
  }

  static class GetConnectionThread extends Thread {

    private HostConfiguration hostConfiguration;
    private HttpConnectionManagerImpl connectionManager;
    private HttpConnection connection;
    private long timeout;
    private Exception exception;

    public GetConnectionThread(HostConfiguration hostConfiguration,
                               HttpConnectionManagerImpl connectionManager,
                               long timeout) {
      this.hostConfiguration = hostConfiguration;
      this.connectionManager = connectionManager;
      this.timeout = timeout;
    }

    public void run() {
      try {
        connection = connectionManager.getConnectionWithTimeout(hostConfiguration, timeout);
      } catch (Exception e) {
        this.exception = e;
      }
    }

    public Exception getException() {
      return exception;
    }

    public HttpConnection getConnection() {
      return connection;
    }

  }

}