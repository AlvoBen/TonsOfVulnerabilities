package com.sap.httpclient;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.server.HttpRequestHandler;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.ResponseWriter;
import com.sap.httpclient.server.SimpleHttpServerConnection;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

/**
 * Tests for reading response headers.
 */
public class TestResponseHeaders extends HttpClientTestBase {

  private AccessibleHttpConnectionManager connectionManager;

  public TestResponseHeaders(final String testName) {
    super(testName);
  }

  public void setUp() throws IOException {
    super.setUp();
    this.connectionManager = new AccessibleHttpConnectionManager();
    this.client.setHttpConnectionManager(connectionManager);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestResponseHeaders.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestResponseHeaders.class);
  }

  public void testHeaders() throws Exception {
    final String body = "XXX\r\nYYY\r\nZZZ";
    this.server.setHttpService(new HttpService() {
      public boolean process(SimpleRequest request, SimpleResponse response) {
        response.setStatusLine(request.getRequestLine().getHttpVersion(), 200);
        response.addHeader(new Header("Connection", "close"));
        response.addHeader(new Header("Content-Length", Integer.toString(body.length())));
        response.addHeader(new Header("Content-Type", "text/xml; charset=utf-8"));
        response.addHeader(new Header("Date", "Wed, 28 Mar 2001 05:05:04 GMT"));
        response.addHeader(new Header("Server", "UserLand Frontier/7.0-WinNT"));
        response.setBodyString(body);
        return true;
      }
    });

    HttpMethod method = new GET();
    client.executeMethod(method);
    assertEquals("close", method.getResponseHeader("Connection").getValue());
    assertEquals(body.length(), Integer.parseInt(method.getResponseHeader("Content-Length").getValue()));
    assertEquals("text/xml; charset=utf-8", method.getResponseHeader("Content-Type").getValue());
    assertEquals("Wed, 28 Mar 2001 05:05:04 GMT", method.getResponseHeader("Date").getValue());
    assertEquals("UserLand Frontier/7.0-WinNT", method.getResponseHeader("Server").getValue());
  }

  /**
   * Tests that having a duplicate content length causes no problems.
	 *
	 * @throws Exception if an exception occures
   */
  public void testDuplicateContentLength() throws Exception {
    final String body = "XXX\r\nYYY\r\nZZZ";
    this.server.setHttpService(new HttpService() {
      public boolean process(SimpleRequest request, SimpleResponse response) {
        response.setStatusLine(request.getRequestLine().getHttpVersion(), 200);
        response.addHeader(new Header("Content-Length", Integer.toString(body.length())));
        response.addHeader(new Header("Content-Length", Integer.toString(body.length())));
        response.setBodyString(body);
        return true;
      }
    });
    HttpMethod method = new GET();
    client.executeMethod(method);
    assertNotNull("Response body is null.", method.getResponseBodyAsStream());
  }

  public void testDuplicateConnection() throws Exception {
    this.server.setHttpService(new HttpService() {
      public boolean process(SimpleRequest request, SimpleResponse response) {
        response.setStatusLine(request.getRequestLine().getHttpVersion(), 200);
        response.addHeader(new Header("Connection", "close"));
        response.addHeader(new Header("Connection", "close"));
        return true;
      }
    });
    GET method = new GET("/");
    client.executeMethod(method);
    method.getResponseBodyAsString();
    assertFalse(connectionManager.getConection().isOpen());
    this.server.setHttpService(new HttpService() {
      public boolean process(SimpleRequest request, SimpleResponse response) {
        response.setStatusLine(HttpVersion.HTTP_1_0, 200);
        response.addHeader(new Header("Connection", "keep-alive"));
        response.addHeader(new Header("Connection", "keep-alive"));
        response.setBodyString("aa");
        return true;
      }
    });
    method = new GET("/");
    client.executeMethod(method);
    method.getResponseBodyAsString();
    assertTrue(connectionManager.getConection().isOpen());
  }

  public void testNoContentLength() throws Exception {
    // test with connection header
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println("Connection: keep-alive");
        out.println();
        out.println("12345");
        out.flush();
        return true;
      }
    });
    GET method = new GET("/");
    client.executeMethod(method);
    method.getResponseBodyAsString();
    assertFalse(connectionManager.getConection().isOpen());
    // test without connection header
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println();
        out.println("12345");
        out.flush();
        return true;
      }
    });
    // test with connection header
    method = new GET("/");
    client.executeMethod(method);
    method.getResponseBodyAsString();
    assertFalse(connectionManager.getConection().isOpen());
  }

  public void testInvalidContentLength1() throws Exception {
    this.server.setHttpService(new HttpService() {
      public boolean process(SimpleRequest request, SimpleResponse response) {
        response.setStatusLine(request.getRequestLine().getHttpVersion(), 200);
        response.addHeader(new Header("Content-Length", "5"));
        response.addHeader(new Header("Content-Length", "stuff"));
        response.setBodyString("12345");
        return true;
      }
    });
    GET method = new GET("/");
    client.executeMethod(method);
    assertEquals(5, method.getResponseContentLength());
  }

  public void testInvalidContentLength2() throws Exception {
    this.server.setHttpService(new HttpService() {
      public boolean process(SimpleRequest request, SimpleResponse response) {
        response.setStatusLine(request.getRequestLine().getHttpVersion(), 200);
        response.addHeader(new Header("Content-Length", "stuff"));
        response.addHeader(new Header("Content-Length", "5"));
        response.setBodyString("12345");
        return true;
      }
    });
    GET method = new GET("/");
    client.executeMethod(method);
    assertEquals(5, method.getResponseContentLength());
  }

  public void testProxyNoContentLength() throws Exception {
    // test with proxy-connection header
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println("proxy-connection: keep-alive");
        out.println();
        out.println("12345");
        out.flush();
        return true;
      }
    });
    client.getHostConfiguration().setProxy(server.getLocalAddress(), server.getLocalPort());
    GET method = new GET("/");
    client.executeMethod(method);
    method.getResponseBodyAsString();
    assertFalse(connectionManager.getConection().isOpen());
    // test without proxy-connection header
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println();
        out.println("12345");
        out.flush();
        return true;
      }
    });
    method = new GET("/");
    client.executeMethod(method);
    method.getResponseBodyAsString();
    assertFalse(connectionManager.getConection().isOpen());
  }

  public void testNullHeaders() throws Exception {
    this.server.setHttpService(new HttpService() {
      public boolean process(SimpleRequest request, SimpleResponse response) {
        response.setStatusLine(request.getRequestLine().getHttpVersion(), 200);
        response.addHeader(new Header("Connection", "close"));
        response.setBodyString("XXX\r\nYYY\r\nZZZ");
        return true;
      }
    });
    HttpMethod method = new GET("/");
    client.executeMethod(method);
    assertEquals(null, method.getResponseHeader(null));
    assertEquals(null, method.getResponseHeader("bogus"));
  }

  public void testFoldedHeaders() throws Exception {
    final String body = "XXX\r\nYYY\r\nZZZ";
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println("Connection: close");
        out.println("Content-Length: " + body.length());
        out.println("Content-Type: text/xml; charset=utf-8");
        out.println("\tboundary=XXXX");
        out.println("Date: Wed, 28 Mar 2001");
        out.println(" 05:05:04 GMT");
        out.println("Server: UserLand Frontier/7.0-WinNT");
        out.println();
        out.println(body);
        out.flush();
        return true;
      }
    });
    HttpMethod method = new GET("/");
    client.executeMethod(method);
    assertEquals("close", method.getResponseHeader("Connection").getValue());
    assertEquals(body.length(), Integer.parseInt(method.getResponseHeader("Content-Length").getValue()));
    assertEquals("text/xml; charset=utf-8 boundary=XXXX", method.getResponseHeader("Content-Type").getValue());
    assertEquals("Wed, 28 Mar 2001 05:05:04 GMT", method.getResponseHeader("Date").getValue());
    assertEquals("UserLand Frontier/7.0-WinNT", method.getResponseHeader("Server").getValue());
    assertTrue(method.getResponseHeader("Content-Type").toString().indexOf("boundary") != -1);
  }

  public void testForceCloseConnection() throws Exception {
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: garbage");
        out.println();
        out.println("stuff");
        out.flush();
        return true;
      }
    });
    FakeHttpMethod method = new FakeHttpMethod();
    client.executeMethod(method);
    assertTrue("Connection should be closed", method.shouldCloseConnection(connectionManager.getConection()));
    assertTrue("Connection should be force-closed", method.isConnectionCloseForced());
  }

  public void testForceCloseConnection2() throws Exception {
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: garbage");
        out.println("Connection: close");
        out.println();
        out.println("stuff");
        out.flush();
        return true;
      }
    });
    FakeHttpMethod method = new FakeHttpMethod();
    client.executeMethod(method);
    assertTrue("Connection should be closed", method.shouldCloseConnection(connectionManager.getConection()));
    assertFalse("Connection should NOT be closed", method.isConnectionCloseForced());
  }

  public void testNoContent() throws Exception {
    // test with connection header
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 204 NO CONTENT");
        out.println();
        out.flush();
        return true;
      }
    });
    GET method = new GET("/");
		try {
			client.executeMethod(method);
		} catch (java.net.SocketException se) { // $JL-EXC$
			// ignoring test if the socket is closed by other software
			assertTrue(true);
      return; // the connection is closed, so return
    }
		method.getResponseBodyAsString();
    assertTrue(connectionManager.getConection().isOpen());
    // test without connection header
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 204 NO CONTENT");
        out.println("Connection: keep-alive");
        out.println();
        out.flush();
        return true;
      }
    });

    // test with connection header
    method = new GET("/");
		try {
			client.executeMethod(method);
		} catch (java.net.SocketException se) { // $JL-EXC$			
			// ignoring test if the socket is closed by other software
      assertTrue(true);
      return; // the connection is closed, so return
    }
		method.getResponseBodyAsString();
    assertTrue(connectionManager.getConection().isOpen());
  }

}