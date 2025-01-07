package com.sap.httpclient;

import java.io.IOException;
import java.net.UnknownHostException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.server.SimpleProxy;

/**
 * Tests basic HostConfiguration functionality.
 */
public class TestHostConfiguration extends HttpClientTestBase {

  public TestHostConfiguration(final String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(TestHostConfiguration.class);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHostConfiguration.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public void testRelativeURLHitWithDefaultHost() throws IOException {
    this.server.setHttpService(new EchoService());
    // Set default host
    this.client.getHostConfiguration().setHost(this.server.getLocalAddress(),
            this.server.getLocalPort(),
            Protocol.getProtocol("http"));

    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testRelativeURLHitWithoutDefaultHost() throws IOException {
    this.server.setHttpService(new EchoService());
    // reset default host configuration
    this.client.setHostConfiguration(new HostConfiguration());
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testAbsoluteURLHitWithoutDefaultHost() throws IOException {
    this.server.setHttpService(new EchoService());
    // reset default host configuration
    this.client.setHostConfiguration(new HostConfiguration());
    GET httpget = new GET("http://" +
            this.server.getLocalAddress() + ":" + this.server.getLocalPort() + "/test/");
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testAbsoluteURLOverridesClientDefaultHost() throws IOException {
    this.server.setHttpService(new EchoService());
    // Somewhere out there in pampa
    this.client.getHostConfiguration().setHost("somewhere.outthere.in.pampa", 9999);
    GET httpget = new GET("http://" +
            this.server.getLocalAddress() + ":" + this.server.getLocalPort() + "/test/");
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
    httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      fail("UnknownHostException should have been thrown");
    } catch (UnknownHostException is_OK) {
      // $JL-EXC$
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testAbsoluteURLOverridesDefaultHostParam() throws IOException {
    this.proxy = new SimpleProxy();
    this.server.setHttpService(new EchoService());
    // reset default host configuration
    HostConfiguration hostconfig = new HostConfiguration();
    hostconfig.setHost("somehwere.outthere.in.pampa", 9999);
    hostconfig.setProxy(this.proxy.getLocalAddress(), this.proxy.getLocalPort());

    GET httpget = new GET("http://" +
            this.server.getLocalAddress() + ":" + this.server.getLocalPort() + "/test/");
    try {
      this.client.executeMethod(hostconfig, httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      assertNotNull(httpget.getResponseHeader("Via"));
    } finally {
      httpget.releaseConnection();
    }
    httpget = new GET("/test/");
    try {
      this.client.executeMethod(hostconfig, httpget);
      assertEquals(HttpStatus.SC_NOT_FOUND, httpget.getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
  }

}