package com.sap.httpclient;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.server.HttpRequestHandler;
import com.sap.httpclient.server.RequestLine;
import com.sap.httpclient.server.ResponseWriter;
import com.sap.httpclient.server.SimpleHttpServer;
import com.sap.httpclient.server.SimpleHttpServerConnection;
import com.sap.httpclient.server.SimpleRequest;

/**
 * Tests HttpClient's behaviour when receiving more response data than expected.
 * <p/>
 * A very simple HTTP Server will be setup on a free port during testing, which
 * returns an incorrect response Content-Length, sending surplus response data,
 * which may contain malicious/fake response headers.
 * </p>
 */
public class TestBadContentLength extends TestCase {
  private HttpClient client = null;
  private SimpleHttpServer server = null;

  public TestBadContentLength(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestBadContentLength.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestBadContentLength.class);
  }

  public void setUp() throws IOException {
    client = new HttpClient();
    server = new SimpleHttpServer(); // use arbitrary port
    server.setTestname(getName());
    server.setRequestHandler(new MyHttpRequestHandler());
  }

  public void tearDown() throws IOException {
    client = null;

    server.destroy();
  }

  /**
   * HttpClient connects to the test server and performs two subsequent
   * requests to the same URI in <u>lenient</u> mode.
   * <p/>
   * Expected behavior:
   * For both requests, status code 200 and a response body of "12345" should be returned.
   *
   * @throws IOException if an IO error occures
   */
  public void test1Lenient() throws IOException {
    client.getParams().makeLenient();
    GET m = new GET("http://localhost:" + server.getLocalPort() + "/");
    client.executeMethod(m);
    assertEquals(200, m.getStatusCode());
    assertEquals("12345", m.getResponseBodyAsString());
    m = new GET("http://localhost:" + server.getLocalPort() + "/");
    client.executeMethod(m);
    assertEquals(200, m.getStatusCode());
    assertEquals("12345", m.getResponseBodyAsString());
    m.releaseConnection();
  }

  /**
   * HttpClient connects to the test server and performs two subsequent
   * requests to the same URI in <u>strict</u> mode.
   * <p/>
   * The first response body will be read with getResponseBodyAsString(),
   * which returns null if an error occured.
   * </p>
   * <p/>
   * The second response body will be read using an InputStream, which
   * throws an IOException if something went wrong.
   * </p>
   * Expected behavior:
   * For both requests, status code 200 should be returned.<br />
   * For request 1, a <code>null</code> response body should be returned.<br />
   * For request 2, a {@link com.sap.httpclient.exception.ProtocolException} is expected.
   *
   * @throws IOException if an IO error occures
   */
  public void test1Strict() throws IOException {
    client.getParams().makeStrict();
    GET m = new GET("http://localhost:" + server.getLocalPort() + "/");
    client.executeMethod(m);
    assertEquals(200, m.getStatusCode());
    assertEquals("12345", m.getResponseBodyAsString());
    m = new GET("http://localhost:" + server.getLocalPort() + "/");
    client.executeMethod(m);
    assertEquals(200, m.getStatusCode());
    InputStream in = m.getResponseBodyAsStream();
    while (in.read() != -1) {
    }
    m.releaseConnection();
  }

  public void enableThisTestForDebuggingOnly() throws InterruptedException {
    while (server.isRunning()) {
      Thread.sleep(100);
    }
  }

  private class MyHttpRequestHandler implements HttpRequestHandler {
    private int requestNo = 0;

    public boolean processRequest(final SimpleHttpServerConnection conn,
                                  final SimpleRequest request) throws IOException {
      RequestLine requestLine = request.getRequestLine();
      ResponseWriter out = conn.getWriter();
      if ("GET".equals(requestLine.getMethod()) && "/".equals(requestLine.getUri())) {
        requestNo++;
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: 5");
        out.println("Connection: keep-alive");
        out.println();
        out.println("12345"); // send exactly 5 bytes
        // and some more garbage!
        out.println("AND SOME MORE\r\nGARBAGE!");
        out.println("HTTP/1.0 404 Not Found");
        out.println("Content-Type: text/plain");
        out.println("");
        out.println("THIS-IS-A-FAKE-RESPONSE!");
        out.flush();
        // process max. 2 subsequents requests per connection
        if (requestNo < 2) {
          conn.setKeepAlive(true);
        }
      }
      return true;
    }
  }
}