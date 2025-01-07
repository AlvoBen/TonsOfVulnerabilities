package com.sap.httpclient;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

/**
 * HTTP net versioning tests.
 */
public class TestVirtualHost extends HttpClientTestBase {

  public TestVirtualHost(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestVirtualHost.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestVirtualHost.class);
  }

  private class VirtualService implements HttpService {

    public VirtualService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      HttpVersion httpversion = request.getRequestLine().getHttpVersion();
      Header hostheader = request.getFirstHeader("Host");
      if (hostheader == null) {
        response.setStatusLine(httpversion, HttpStatus.SC_BAD_REQUEST);
        response.setBodyString("Host header missing");
      } else {
        response.setStatusLine(httpversion, HttpStatus.SC_OK);
        response.setBodyString(hostheader.getValue());
      }
      return true;
    }
  }

  public void testVirtualHostHeader() throws IOException {
    this.server.setHttpService(new VirtualService());
    GET httpget = new GET("/test/");
    HostConfiguration hostconf = new HostConfiguration();
    hostconf.setHost(this.server.getLocalAddress(), this.server.getLocalPort(), "http");
    hostconf.getParams().setVirtualHost("somehost");
    try {
      this.client.executeMethod(hostconf, httpget);
      String hostheader = "somehost:" + this.server.getLocalPort();
      assertEquals(hostheader, httpget.getResponseBodyAsString());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testNoVirtualHostHeader() throws IOException {
    this.server.setHttpService(new VirtualService());
    GET httpget = new GET("/test/");
    HostConfiguration hostconf = new HostConfiguration();
    hostconf.setHost(this.server.getLocalAddress(), this.server.getLocalPort(), "http");
    hostconf.getParams().setVirtualHost(null);
    try {
      this.client.executeMethod(hostconf, httpget);
      String hostheader = this.server.getLocalAddress() + ":" + this.server.getLocalPort();
      assertEquals(hostheader, httpget.getResponseBodyAsString());
    } finally {
      httpget.releaseConnection();
    }
  }
}