package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sap.httpclient.auth.AuthScope;
import com.sap.httpclient.auth.UserPassCredentials;
import com.sap.httpclient.http.methods.GET;

/**
 * Simple tests for HTTPS support in HttpClient.
 * <p/>
 * To run this test you'll need:
 * + a JSSE implementation installed (see README.txt)
 * + the java.net.handler.pkgs system property set
 * for your provider.  e.g.:
 * -Djava.net.handler.pkgs=com.sun.net.ssl.internal.www.net
 * (see build.xml)
 */
public class TestHttps extends TestCase {

  private String _urlWithPort = null;
  private String _urlWithoutPort = null;
  private final String PROXY_HOST = System.getProperty("httpclient.test.proxyHost");
  private final String PROXY_PORT = System.getProperty("httpclient.test.proxyPort");
  private final String PROXY_USER = System.getProperty("httpclient.test.proxyUser");
  private final String PROXY_PASS = System.getProperty("httpclient.test.proxyPass");

  public TestHttps(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttps.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHttps.class);
  }

  public void setUp() throws Exception {
    _urlWithPort = "https://www.verisign.com:443/";
    _urlWithoutPort = "https://www.verisign.com/";
  }

  public void testHttpsGet() {
    HttpClient client = new HttpClient();
    if (PROXY_HOST != null) {
      if (PROXY_USER != null) {
        HttpState state = client.getState();
        state.setProxyCredentials(AuthScope.ANY, new UserPassCredentials(PROXY_USER, PROXY_PASS));
      }
      client.getHostConfiguration().setProxy(PROXY_HOST, Integer.parseInt(PROXY_PORT));
    }
    GET method = new GET(_urlWithPort);

    try {
      client.executeMethod(method);
    } catch (Throwable t) {
      t.printStackTrace();
      fail("Exception thrown during HTTPS GET: " + t.toString());
    }
    try {
      String data = method.getResponseBodyAsString();
      // This enumeration musn't be empty
      assertTrue("No data returned.", (data.length() > 0));
    } catch (Throwable t) {
      t.printStackTrace();
      fail("Exception thrown while retrieving data : " + t.toString());
    }
  }

  public void testHttpsGetNoPort() {
    HttpClient client = new HttpClient();
    if (PROXY_HOST != null) {
      if (PROXY_USER != null) {
        HttpState state = client.getState();
        state.setProxyCredentials(AuthScope.ANY, new UserPassCredentials(PROXY_USER, PROXY_PASS));
      }
      client.getHostConfiguration().setProxy(PROXY_HOST, Integer.parseInt(PROXY_PORT));
    }
    GET method = new GET(_urlWithoutPort);
    try {
      client.executeMethod(method);
    } catch (Throwable t) {
      t.printStackTrace();
      fail("Exception thrown during HTTPS GET: " + t.toString());
    }
    try {
      String data = method.getResponseBodyAsString();
      // This enumeration musn't be empty
      assertTrue("No data returned.", (data.length() > 0));
    } catch (Throwable t) {
      t.printStackTrace();
      fail("Exception thrown while retrieving data : " + t.toString());
    }
  }
}