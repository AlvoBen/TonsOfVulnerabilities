package com.sap.httpclient.params;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.*;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.HttpRequestHandler;
import com.sap.httpclient.server.SimpleHttpServerConnection;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

/**
 * Tunnelling proxy configuration.
 */
public class TestSSLTunnelParams extends HttpClientTestBase {

  public TestSSLTunnelParams(final String testName) {
    super(testName);
    setUseProxy(true);
    setUseSSL(true);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestSSLTunnelParams.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
		return new TestSuite(TestSSLTunnelParams.class);
  }

  static class HttpVersionHandler implements HttpRequestHandler {

    public HttpVersionHandler() {
      super();
    }

    public boolean processRequest(final SimpleHttpServerConnection conn,
                                  final SimpleRequest request) throws IOException {

      HttpVersion ver = request.getRequestLine().getHttpVersion();
      if (ver.isEqualTo(HttpVersion.HTTP_1_0)) {
        return false;
      } else {
        SimpleResponse response = new SimpleResponse();
        response.setStatusLine(ver, HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED);
        response.addHeader(new Header("Proxy-Connection", "close"));
        conn.setKeepAlive(false);
        // Make sure the request body is fully consumed
        request.getBodyBytes();
        conn.writeResponse(response);
        return true;
      }
    }
  }

  /**
   * Tests correct proparation of HTTP params from the client to CONNECT method.
	 *
	 * @throws IOException if any IOException occures
   */
  public void testTunnellingParamsAgentLevel() throws IOException {
    this.proxy.addHandler(new HttpVersionHandler());
    this.server.setHttpService(new FeedbackService());
    this.client.getParams().setVersion(HttpVersion.HTTP_1_1);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertNotNull(httpget.getStatusLine());
      assertEquals(HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED,
              httpget.getStatusLine().getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
    this.client.getParams().setVersion(HttpVersion.HTTP_1_0);
    httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertNotNull(httpget.getStatusLine());
      assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
  }

  /**
   * Tests correct proparation of HTTP params from the host config to CONNECT method.
	 *
	 * @throws IOException if any IOException occures
   */
  public void testTunnellingParamsHostLevel() throws IOException {
    this.proxy.addHandler(new HttpVersionHandler());
    this.server.setHttpService(new FeedbackService());
    this.client.getHostConfiguration().getParams().setParameter(Parameters.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertNotNull(httpget.getStatusLine());
      assertEquals(HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED,
              httpget.getStatusLine().getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
    this.client.getHostConfiguration().getParams().setParameter(Parameters.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
    httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertNotNull(httpget.getStatusLine());
      assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
  }

  /**
   * Tests ability to use HTTP/1.0 to execute CONNECT method and HTTP/1.1 to
   * execute methods once the tunnel is established.
	 *
	 * @throws IOException if any IOException occures
   */
  public void testTunnellingParamsHostHTTP10AndMethodHTTP11() throws IOException {
    this.proxy.addHandler(new HttpVersionHandler());
    this.server.setHttpService(new FeedbackService());
    this.client.getHostConfiguration().getParams().setParameter(Parameters.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
    GET httpget = new GET("/test/");
    httpget.getParams().setVersion(HttpVersion.HTTP_1_1);
    try {
      this.client.executeMethod(httpget);
      assertNotNull(httpget.getStatusLine());
      assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
      assertEquals(HttpVersion.HTTP_1_1, httpget.getEffectiveVersion());
    } finally {
      httpget.releaseConnection();
    }
  }
}