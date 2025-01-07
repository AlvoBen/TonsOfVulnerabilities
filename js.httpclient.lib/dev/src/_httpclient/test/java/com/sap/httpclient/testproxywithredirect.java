package com.sap.httpclient;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.auth.AuthScope;
import com.sap.httpclient.auth.UserPassCredentials;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.RequestLine;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

/**
 * Tests for proxied connections.
 */
public class TestProxyWithRedirect extends HttpClientTestBase {

  public TestProxyWithRedirect(String testName) {
    super(testName);
    setUseProxy(true);
  }

  public static Test suite() {
    return new TestSuite(TestProxyWithRedirect.class);
  }

  private class BasicRedirectService extends EchoService {

    private String location = null;

    public BasicRedirectService(final String location) {
      super();
      this.location = location;
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine reqline = request.getRequestLine();
      HttpVersion ver = reqline.getHttpVersion();
      if (reqline.getUri().equals("/redirect/")) {
        response.setStatusLine(ver, HttpStatus.SC_MOVED_TEMPORARILY);
        response.addHeader(new Header("Location", this.location));
        response.addHeader(new Header("Connection", "Close"));
        return true;
      } else {
        return super.process(request, response);
      }
    }
  }

  public void testAuthProxyWithRedirect() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    this.server.setHttpService(new BasicRedirectService("/"));
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/redirect/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  public void testAuthProxyWithCrossSiteRedirect() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    this.server.setHttpService(new BasicRedirectService("http://127.0.0.1:" + this.server.getLocalPort()));
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/redirect/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  public void testPreemptiveAuthProxyWithCrossSiteRedirect() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    this.client.getParams().setAuthenticationPreemptive(true);
    this.server.setHttpService(new BasicRedirectService("http://127.0.0.1:" + this.server.getLocalPort()));
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/redirect/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

}