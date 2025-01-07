package com.sap.httpclient;

import java.io.IOException;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.methods.StringRequestData;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.HttpRequestHandler;
import com.sap.httpclient.server.SimpleHttpServerConnection;
import com.sap.httpclient.server.SimpleProxy;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Connection persistence tests
 */
public class TestConnectionPersistence extends HttpClientTestBase {

  public TestConnectionPersistence(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestConnectionPersistence.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestConnectionPersistence.class);
  }

  public void testConnPersisenceHTTP10() throws Exception {
    this.server.setHttpService(new EchoService());

    AccessibleHttpConnectionManager connman = new AccessibleHttpConnectionManager();
    this.client.getParams().setVersion(HttpVersion.HTTP_1_0);
    this.client.setHttpConnectionManager(connman);
    POST httppost = new POST("/test/");
    httppost.setRequestData(new StringRequestData("stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertFalse(connman.getConection().isOpen());
    httppost = new POST("/test/");
    httppost.setRequestData(new StringRequestData("more stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertFalse(connman.getConection().isOpen());
  }

  public void testConnPersisenceHTTP11() throws Exception {
    this.server.setHttpService(new EchoService());
    AccessibleHttpConnectionManager connman = new AccessibleHttpConnectionManager();
    this.client.getParams().setVersion(HttpVersion.HTTP_1_1);
    this.client.setHttpConnectionManager(connman);
    POST httppost = new POST("/test/");
    httppost.setRequestData(new StringRequestData("stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertTrue(connman.getConection().isOpen());
    httppost = new POST("/test/");
    httppost.setRequestData(new StringRequestData("more stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertTrue(connman.getConection().isOpen());
  }

  public void testConnClose() throws Exception {
    this.server.setHttpService(new EchoService());
    AccessibleHttpConnectionManager connman = new AccessibleHttpConnectionManager();
    this.client.getParams().setVersion(HttpVersion.HTTP_1_1);
    this.client.setHttpConnectionManager(connman);
    POST httppost = new POST("/test/");
    httppost.setRequestData(new StringRequestData("stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertTrue(connman.getConection().isOpen());
    httppost = new POST("/test/");
    httppost.setRequestHeader("Connection", "close");
    httppost.setRequestData(new StringRequestData("more stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertFalse(connman.getConection().isOpen());
  }

  public void testConnKeepAlive() throws Exception {
    this.server.setHttpService(new EchoService());
    AccessibleHttpConnectionManager connman = new AccessibleHttpConnectionManager();
    this.client.getParams().setVersion(HttpVersion.HTTP_1_0);
    this.client.setHttpConnectionManager(connman);
    POST httppost = new POST("/test/");
    httppost.setRequestData(new StringRequestData("stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertFalse(connman.getConection().isOpen());
    httppost = new POST("/test/");
    httppost.setRequestHeader("Connection", "keep-alive");
    httppost.setRequestData(new StringRequestData("more stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertTrue(connman.getConection().isOpen());
  }

  public void testRequestConnClose() throws Exception {
    this.server.setRequestHandler(new HttpRequestHandler() {
      public boolean processRequest(final SimpleHttpServerConnection conn,
                                    final SimpleRequest request) throws IOException {

        // Make sure the request if fully consumed
        request.getBodyBytes();
        SimpleResponse response = new SimpleResponse();
        response.setStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK);
        response.setBodyString("stuff back");
        conn.setKeepAlive(true);
        conn.writeResponse(response);
        return true;
      }

    });

    AccessibleHttpConnectionManager connman = new AccessibleHttpConnectionManager();
    this.client.getParams().setVersion(HttpVersion.HTTP_1_0);
    this.client.setHttpConnectionManager(connman);
    POST httppost = new POST("/test/");
    httppost.setRequestHeader("Connection", "close");
    httppost.setRequestData(new StringRequestData("stuff"));
    try {
      this.client.executeMethod(httppost);
    } finally {
      httppost.releaseConnection();
    }
    assertFalse(connman.getConection().isOpen());
  }

  public void testProxyConnClose() throws Exception {
    this.server.setHttpService(new EchoService());
    this.proxy = new SimpleProxy();
    this.client.getHostConfiguration().setProxy(proxy.getLocalAddress(), proxy.getLocalPort());
    AccessibleHttpConnectionManager connman = new AccessibleHttpConnectionManager();
    this.client.setHttpConnectionManager(connman);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertTrue(connman.getConection().isOpen());
    httpget = new GET("/test/");
    httpget.setRequestHeader("Proxy-Connection", "Close");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertFalse(connman.getConection().isOpen());
    assertEquals("Close", httpget.getRequestHeader("Proxy-Connection").getValue());
  }

}