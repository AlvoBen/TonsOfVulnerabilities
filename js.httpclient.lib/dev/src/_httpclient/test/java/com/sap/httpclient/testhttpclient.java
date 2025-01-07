package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.http.cookie.Cookie;
import com.sap.httpclient.auth.AuthScope;

import java.util.Date;

public class TestHttpClient extends HttpClientTestBase {

  private Cookie[] cookies = new Cookie[] {new Cookie("domain1", "cookie1", "value1"), new Cookie("domain2", "cookie2", "value2")};

  public TestHttpClient(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttpClient.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHttpClient.class);
  }

  public void testConnectionMangerConstructor() throws Exception {
    this.client = new HttpClient(new AccessibleHttpConnectionManager());
  }

  public void testAddGetCookies() throws Exception {
    this.client.addCookie(cookies[0]);
    assertEquals(cookies[0], this.client.getCookies()[0]);
    this.client.addCookies(cookies);
    Cookie[] cookiess = this.client.getCookies();
    assertEquals(cookies[0], cookiess[0]);
    assertEquals(cookies[1], cookiess[1]);
    this.client.clear();
  }

  public void testPurgeExpiredCookies() throws Exception {
    Date expire = new Date();
    Cookie tempCookie = new Cookie("domainTemp", "nameTemp", "valueTemp");
    tempCookie.setExpiryDate(expire);
    this.client.addCookie(tempCookie);
    this.client.purgeExpiredCookies();
    Cookie[] cookiess = this.client.getCookies();
    assertTrue(cookiess.length == 0);
  }

  public void testSetGetCredentials() throws Exception {
    this.client.setCredentials(AuthScope.ANY, null);
    this.client.setProxyCredentials(AuthScope.ANY, null);
    assertNull(this.client.getCredentials(AuthScope.ANY));
    assertNull(this.client.getProxyCredentials(AuthScope.ANY));
  }

}
