package com.sap.httpclient;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.HttpVersion;

/**
 * HTTP net versioning tests.
 */
public class TestEffectiveHttpVersion extends HttpClientTestBase {

  public TestEffectiveHttpVersion(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestEffectiveHttpVersion.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestEffectiveHttpVersion.class);
  }

  public void testClientLevelHttpVersion() throws IOException {
    this.server.setHttpService(new EchoService());
    HttpVersion testver = new HttpVersion(1, 10);
    this.client.getParams().setVersion(testver);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertEquals(testver, httpget.getEffectiveVersion());
  }

  public void testMethodLevelHttpVersion() throws IOException {
    this.server.setHttpService(new EchoService());
    HttpVersion globalver = new HttpVersion(1, 10);
    HttpVersion testver1 = new HttpVersion(1, 11);
    HttpVersion testver2 = new HttpVersion(1, 12);
    this.client.getParams().setVersion(globalver);
    GET httpget1 = new GET("/test/");
    httpget1.getParams().setVersion(testver1);
    try {
      this.client.executeMethod(httpget1);
    } finally {
      httpget1.releaseConnection();
    }
    assertEquals(testver1, httpget1.getEffectiveVersion());
    GET httpget2 = new GET("/test/");
    httpget2.getParams().setVersion(testver2);
    try {
      this.client.executeMethod(httpget2);
    } finally {
      httpget2.releaseConnection();
    }
    assertEquals(testver2, httpget2.getEffectiveVersion());
    GET httpget3 = new GET("/test/");
    try {
      this.client.executeMethod(httpget3);
    } finally {
      httpget3.releaseConnection();
    }
    assertEquals(globalver, httpget3.getEffectiveVersion());
  }

  public void testHostLevelHttpVersion() throws IOException {
    this.server.setHttpService(new EchoService());
    HttpVersion testver = new HttpVersion(1, 11);
    HttpVersion hostver = new HttpVersion(1, 12);
    this.client.getParams().setVersion(testver);
    GET httpget1 = new GET("/test/");
    httpget1.getParams().setVersion(testver);
    HostConfiguration hostconf = new HostConfiguration();
    hostconf.setHost(this.server.getLocalAddress(), this.server.getLocalPort(), "http");
    try {
      this.client.executeMethod(hostconf, httpget1);
    } finally {
      httpget1.releaseConnection();
    }
    assertEquals(testver, httpget1.getEffectiveVersion());
    GET httpget2 = new GET("/test/");
    hostconf.setHost(this.server.getLocalAddress(), this.server.getLocalPort(), "http");
    hostconf.getParams().setParameter(Parameters.PROTOCOL_VERSION, hostver);
    try {
      this.client.executeMethod(hostconf, httpget2);
    } finally {
      httpget2.releaseConnection();
    }
    assertEquals(hostver, httpget2.getEffectiveVersion());
  }
}