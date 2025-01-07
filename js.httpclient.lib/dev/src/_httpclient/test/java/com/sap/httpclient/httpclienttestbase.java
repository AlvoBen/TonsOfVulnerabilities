package com.sap.httpclient;

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.server.SimpleHttpServer;
import com.sap.httpclient.server.SimplePlainSocketFactory;
import com.sap.httpclient.server.SimpleProxy;
import com.sap.httpclient.server.SimpleSocketFactory;
import com.sap.httpclient.ssl.SimpleSSLSocketFactory;
import com.sap.httpclient.ssl.SimpleSSLTestSocketFactory;

/**
 * Base class for test cases using {@link com.sap.httpclient.server.SimpleHttpServer} based testing framework.
 */
public class HttpClientTestBase extends TestCase {

  protected HttpClient client = null;
  protected SimpleHttpServer server = null;
  protected SimpleProxy proxy = null;
  private boolean useProxy = false;
  private boolean useSSL = false;

  public HttpClientTestBase(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {HttpClientTestBase.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(HttpClientTestBase.class);
  }

  public void setUseProxy(boolean useProxy) {
    this.useProxy = useProxy;
  }

  public void setUseSSL(boolean b) {
    this.useSSL = b;
  }

  public boolean isUseSSL() {
    return this.useSSL;
  }

  public void setUp() throws IOException {
    // Configure socket factories
    SimpleSocketFactory serversocketfactory;
    Protocol testhttp;
    if (this.useSSL) {
      serversocketfactory = new SimpleSSLSocketFactory();
      testhttp = new Protocol("https", new SimpleSSLTestSocketFactory(), 443);
    } else {
      serversocketfactory = new SimplePlainSocketFactory();
      testhttp = Protocol.getProtocol("http");
    }
    this.server = new SimpleHttpServer(serversocketfactory, 0); // use arbitrary port
    this.server.setTestname(getName());
    this.client = new HttpClient();
    this.client.getHostConfiguration().setHost(this.server.getLocalAddress(),
            this.server.getLocalPort(),
            testhttp);

    if (this.useProxy) {
      this.proxy = new SimpleProxy();
      client.getHostConfiguration().setProxy(proxy.getLocalAddress(), proxy.getLocalPort());
    }
  }

  public void tearDown() throws IOException {
    this.client = null;
    this.server.destroy();
    this.server = null;
    if (proxy != null) {
      proxy.destroy();
      proxy = null;
    }
  }
}