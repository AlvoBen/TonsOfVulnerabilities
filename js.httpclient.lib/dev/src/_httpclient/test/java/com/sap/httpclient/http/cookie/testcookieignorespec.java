package com.sap.httpclient.http.cookie;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.HttpClientTestBase;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

/**
 * Test cases for ignore cookie apec
 */
public class TestCookieIgnoreSpec extends HttpClientTestBase {

  public TestCookieIgnoreSpec(final String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(TestCookieIgnoreSpec.class);
  }

  private class BasicAuthService implements HttpService {

    public BasicAuthService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      HttpVersion ver = request.getRequestLine().getHttpVersion();
      response.setStatusLine(ver, HttpStatus.SC_OK);
      response.addHeader(new Header("Connection", "close"));
      response.addHeader(new Header("Set-Cookie",
              "custno = 12345; comment=test; version=1," +
              " name=John; version=1; max-age=600; secure; domain=.sap.com"));
      return true;
    }
  }

  public void testIgnoreCookies() throws Exception {
    this.server.setHttpService(new BasicAuthService());
    GET httpget = new GET("/");
    httpget.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertEquals("Cookie parsing should have been disabled", 0, this.client.getState().getCookies().length);
  }

  public void testKeepCloverHappy() throws Exception {
    CookieSpec cookiespec = new IgnoreCookiesSpec();
    cookiespec.parseAttribute(null, null);
    cookiespec.parse("host", 80, "/", false, (String) null);
    cookiespec.parse("host", 80, "/", false, (Header) null);
    cookiespec.validate("host", 80, "/", false, null);
    cookiespec.match("host", 80, "/", false, (Cookie) null);
    cookiespec.match("host", 80, "/", false, (Cookie[]) null);
    cookiespec.domainMatch(null, null);
    cookiespec.pathMatch(null, null);
    cookiespec.match("host", 80, "/", false, (Cookie[]) null);
    cookiespec.formatCookie(null);
    cookiespec.formatCookies(null);
    cookiespec.formatCookieHeader((Cookie) null);
    cookiespec.formatCookieHeader((Cookie[]) null);
  }
}