package com.sap.httpclient.params;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.*;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

/**
 * HTTP preference framework tests.
 */
public class TestHttpParams extends HttpClientTestBase {

  public TestHttpParams(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttpParams.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHttpParams.class);
  }

  private class SimpleService implements HttpService {

    public SimpleService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      String uri = request.getRequestLine().getUri();
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      if ("/miss/".equals(uri)) {
        response.setStatusLine(httpversion, HttpStatus.SC_MOVED_TEMPORARILY);
        response.addHeader(new Header("Location", "/hit/"));
        response.setBodyString("Missed!");
      } else if ("/hit/".equals(uri)) {
        response.setStatusLine(httpversion, HttpStatus.SC_OK);
        response.setBodyString("Hit!");
      } else {
        response.setStatusLine(httpversion, HttpStatus.SC_NOT_FOUND);
        response.setBodyString(uri + " not found");
      }
      return true;
    }
  }

  public void testDefaultHeaders() throws IOException {
    this.server.setHttpService(new SimpleService());
    ArrayList<Header> defaults = new ArrayList<Header>();
    defaults.add(new Header("this-header", "value1"));
    defaults.add(new Header("that-header", "value1"));
    defaults.add(new Header("that-header", "value2"));
    defaults.add(new Header("User-Agent", "test"));
    HostConfiguration hostconfig = new HostConfiguration();
    hostconfig.setHost(this.server.getLocalAddress(), this.server.getLocalPort(), Protocol.getProtocol("http"));
    hostconfig.getParams().setParameter(Parameters.DEFAULT_HEADERS, defaults);
    GET httpget = new GET("/miss/");
    try {
      this.client.executeMethod(hostconfig, httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
    ArrayList<Header> thisheader = httpget.getRequestHeaders("this-header");
    assertEquals(1, thisheader.size());
    ArrayList<Header> thatheader = httpget.getRequestHeaders("that-header");
    assertEquals(2, thatheader.size());
    assertEquals("test", httpget.getRequestHeader("User-Agent").getValue());
  }

  public void testDefaults() throws IOException {
    this.server.setHttpService(new SimpleService());
    this.client.getParams().setParameter(Parameters.USER_AGENT, "test");
    HostConfiguration hostconfig = new HostConfiguration();
    hostconfig.setHost(this.server.getLocalAddress(), this.server.getLocalPort(), Protocol.getProtocol("http"));
    GET httpget = new GET("/miss/");
    try {
      this.client.executeMethod(hostconfig, httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
    assertEquals("test", httpget.getRequestHeader("User-Agent").getValue());
    assertEquals("test", httpget.getParams().getParameter(Parameters.USER_AGENT));
    assertEquals("test", hostconfig.getParams().getParameter(Parameters.USER_AGENT));
    assertEquals("test", client.getParams().getParameter(Parameters.USER_AGENT));
  }
}