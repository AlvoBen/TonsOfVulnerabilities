package com.sap.httpclient;

import java.io.IOException;

import junit.framework.*;
import com.sap.httpclient.http.methods.*;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.HttpRequestHandler;
import com.sap.httpclient.server.ResponseWriter;
import com.sap.httpclient.server.SimpleHttpServerConnection;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.exception.HttpException;

/**
 * Tests handling of non-compliant responses.
 */
public class TestNoncompliant extends HttpClientTestBase {

  public TestNoncompliant(String s) {
    super(s);
  }

  public static Test suite() {
		return new TestSuite(TestNoncompliant.class);
  }

  /**
   * Tests if client is able to recover gracefully when HTTP server or proxy fails to send
	 * 100 status code when expected. The client should resume sending the request body
	 * after a defined timeout without having received "continue" code.
	 *
	 * @throws Exception if an exception occures
   */
  public void testNoncompliantPostMethodString() throws Exception {
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println("Connection: close");
        out.println("Content-Length: 0");
        out.println();
        out.flush();
        return true;
      }
    });
    POST method = new POST("/");
    method.getParams().setParameter(Parameters.USE_EXPECT_CONTINUE, true);
    method.setRequestData(new StringRequestData("This is data to be sent in the body of an HTTP POST."));
    client.executeMethod(method);
    assertEquals(200, method.getStatusCode());
  }

  /**
   * Tests that a response status line containing \r and \n is handled.
   */
  public void testNoncompliantStatusLine() {
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 444 This status message contains\n"
                + " a newline and a\r"
                + " carrage return but that should be OK.");
        out.println("Connection: close");
        out.println("Content-Length: 0");
        out.println();
        out.flush();
        return true;
      }
    });
    GET method = new GET("/");
    try {
      client.executeMethod(method);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception: " + e.toString());
    }
    assertEquals(444, method.getStatusCode());
  }

  /**
   * Test if a response to HEAD method from non-compliant server that contains
   * an unexpected body content can be correctly redirected
	 *
	 * @throws Exception if an exception occures
   */
  public void testNoncompliantHeadWithResponseBody() throws Exception {
    final String body = "Test body";
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn,
                                    SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println("Connection: close");
        out.println("Content-Length: " + body.length());
        out.println();
        out.print(body);
        out.flush();
        return true;
      }
    });
    HEAD method = new HEAD("/");
    method.getParams().setParameter(Parameters.HEAD_BODY_CHECK_TIMEOUT, 50);
    client.executeMethod(method);
    assertEquals(200, method.getStatusCode());
    method.releaseConnection();
  }

  /**
   * Test if a response to HEAD method from non-compliant server causes an HttpException to be thrown
	 *
	 * @throws Exception if an exception occures
   */
  public void testNoncompliantHeadStrictMode() throws Exception {
    final String body = "Test body";
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 200 OK");
        out.println("Connection: close");
        out.println("Content-Length: " + body.length());
        out.println();
        out.print(body);
        out.flush();
        return true;
      }
    });
    client.getParams().setParameter(Parameters.REJECT_HEAD_BODY, true);
    HEAD method = new NoncompliantHeadMethod("/");
    method.getParams().setParameter(Parameters.HEAD_BODY_CHECK_TIMEOUT, 50);
    try {
      client.executeMethod(method);
      fail("HttpException should have been thrown");
    } catch (HttpException is_OK) {
      // $JL-EXC$
    }
    method.releaseConnection();
  }

  /**
   * Tests if client is able to handle gracefully malformed responses
   * that may not include response body.
	 *
	 * @throws Exception if an exception occures
   */
  public void testMalformed304Response() throws Exception {
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        conn.setSocketTimeout(20000);
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 304 OK");
        out.println("Connection: keep-alive");
        out.println("Content-Length: 100");
        out.println();
        out.flush();
        conn.setKeepAlive(true);
        return true;
      }
    });

    GET method = new GET("/");
    method.getParams().setSoTimeout(1000);
    client.executeMethod(method);
    assertEquals(HttpStatus.SC_NOT_MODIFIED, method.getStatusCode());
    method.getResponseBody();
  }

  public void testMalformed204Response() throws Exception {
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
        conn.setSocketTimeout(20000);
        ResponseWriter out = conn.getWriter();
        out.println("HTTP/1.1 204 OK");
        out.println("Connection: close");
        out.println("Content-Length: 100");
        out.println();
        out.flush();
        conn.setKeepAlive(true);
        return true;
      }
    });
    GET method = new GET("/");
    method.getParams().setSoTimeout(1000);
    client.executeMethod(method);
    assertEquals(HttpStatus.SC_NO_CONTENT, method.getStatusCode());
    method.getResponseBody();
  }

}