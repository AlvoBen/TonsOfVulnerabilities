package com.sap.httpclient;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.methods.StringRequestData;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.RequestLine;
import com.sap.httpclient.server.SimpleHttpServer;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import com.sap.httpclient.exception.CircularRedirectException;
import com.sap.httpclient.exception.RedirectException;
import com.sap.httpclient.http.cookie.Cookie;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.uri.URI;

/**
 * Redirection test cases.
 */
public class TestRedirects extends HttpClientTestBase {

  public TestRedirects(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestRedirects.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TestRedirects.class);
    ProxyTestDecorator.addTests(suite);
    return suite;
  }

  private class BasicRedirectService implements HttpService {
    private int statuscode = HttpStatus.SC_MOVED_TEMPORARILY;
    private String host = null;
    private int port;

    public BasicRedirectService(final String host, int port, int statuscode) {
      super();
      this.host = host;
      this.port = port;
      if (statuscode > 0) {
        this.statuscode = statuscode;
      }
    }

    public BasicRedirectService(final String host, int port) {
      this(host, port, -1);
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine reqline = request.getRequestLine();
      HttpVersion ver = reqline.getHttpVersion();
      if (reqline.getUri().equals("/oldlocation/")) {
        response.setStatusLine(ver, this.statuscode);
        response.addHeader(new Header("Location", "http://" + this.host + ":" + this.port + "/newlocation/"));
        response.addHeader(new Header("Connection", "close"));
      } else if (reqline.getUri().equals("/newlocation/")) {
        response.setStatusLine(ver, HttpStatus.SC_OK);
        response.setBodyString("Successful redirect");
      } else {
        response.setStatusLine(ver, HttpStatus.SC_NOT_FOUND);
      }
      return true;
    }
  }

  private class CircularRedirectService implements HttpService {

    private int invocations = 0;

    public CircularRedirectService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine reqline = request.getRequestLine();
      HttpVersion ver = reqline.getHttpVersion();
      if (reqline.getUri().startsWith("/circular-oldlocation")) {
        response.setStatusLine(ver, HttpStatus.SC_MOVED_TEMPORARILY);
        response.addHeader(new Header("Location", "/circular-location2?invk=" + (++this.invocations)));
      } else if (reqline.getUri().startsWith("/circular-location2")) {
        response.setStatusLine(ver, HttpStatus.SC_MOVED_TEMPORARILY);
        response.addHeader(new Header("Location", "/circular-oldlocation?invk=" + (++this.invocations)));
      } else {
        response.setStatusLine(ver, HttpStatus.SC_NOT_FOUND);
      }
      return true;
    }
  }

  private class RelativeRedirectService implements HttpService {

    public RelativeRedirectService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine reqline = request.getRequestLine();
      HttpVersion ver = reqline.getHttpVersion();
      if (reqline.getUri().equals("/oldlocation/")) {
        response.setStatusLine(ver, HttpStatus.SC_MOVED_TEMPORARILY);
        response.addHeader(new Header("Location", "/relativelocation/"));
      } else if (reqline.getUri().equals("/relativelocation/")) {
        response.setStatusLine(ver, HttpStatus.SC_OK);
        response.setBodyString("Successful redirect");
      } else {
        response.setStatusLine(ver, HttpStatus.SC_NOT_FOUND);
      }
      return true;
    }
  }

  private class BogusRedirectService implements HttpService {
    private String url;

    public BogusRedirectService(String redirectUrl) {
      super();
      this.url = redirectUrl;
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine reqline = request.getRequestLine();
      HttpVersion ver = reqline.getHttpVersion();
      if (reqline.getUri().equals("/oldlocation/")) {
        response.setStatusLine(ver, HttpStatus.SC_MOVED_TEMPORARILY);
        response.addHeader(new Header("Location", url));
      } else if (reqline.getUri().equals("/relativelocation/")) {
        response.setStatusLine(ver, HttpStatus.SC_OK);
        response.setBodyString("Successful redirect");
      } else {
        response.setStatusLine(ver, HttpStatus.SC_NOT_FOUND);
      }
      return true;
    }
  }

  public void testBasicRedirect300() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port, HttpStatus.SC_MULTIPLE_CHOICES));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(false);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_MULTIPLE_CHOICES, httpget.getStatusCode());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(new URI("/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testBasicRedirect301() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port, HttpStatus.SC_MOVED_PERMANENTLY));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      assertEquals("/newlocation/", httpget.getPath());
      assertEquals(host, httpget.getURI().getHost());
      assertEquals(port, httpget.getURI().getPort());
      assertEquals(new URI("http://" + host + ":" + port + "/newlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testBasicRedirect302() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port, HttpStatus.SC_MOVED_TEMPORARILY));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      // NWN - should be the old location, because it's only temporarily redirect.
      // The method is executed and redirected, but not changed(i.e. the next execution will be at the oldlocation)

//      assertEquals("/newlocation/", httpget.getPath());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(host, httpget.getURI().getHost());
      assertEquals(port, httpget.getURI().getPort());
//      assertEquals(new URI("http://" + host + ":" + port + "/newlocation/", false), httpget.getURI());
      assertEquals(new URI("http://" + host + ":" + port + "/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testBasicRedirect303() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port, HttpStatus.SC_SEE_OTHER));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      // NWN same as the previous test(302)
//      assertEquals("/newlocation/", httpget.getPath());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(host, httpget.getURI().getHost());
      assertEquals(port, httpget.getURI().getPort());
//      assertEquals(new URI("http://" + host + ":" + port + "/newlocation/", false), httpget.getURI());
      assertEquals(new URI("http://" + host + ":" + port + "/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testBasicRedirect304() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port, HttpStatus.SC_NOT_MODIFIED));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_NOT_MODIFIED, httpget.getStatusCode());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(new URI("/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testBasicRedirect305() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port, HttpStatus.SC_USE_PROXY));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_USE_PROXY, httpget.getStatusCode());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(new URI("/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testBasicRedirect307() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port, HttpStatus.SC_TEMPORARY_REDIRECT));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      // NWN - same as 302 (temporary)
//      assertEquals("/newlocation/", httpget.getPath());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(host, httpget.getURI().getHost());
      assertEquals(port, httpget.getURI().getPort());
//      assertEquals(new URI("http://" + host + ":" + port + "/newlocation/", false), httpget.getURI());
      assertEquals(new URI("http://" + host + ":" + port + "/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testNoRedirect() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(false);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, httpget.getStatusCode());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(new URI("/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testMaxRedirectCheck() throws IOException {
    this.server.setHttpService(new CircularRedirectService());
    GET httpget = new GET("/circular-oldlocation/");
    try {
      this.client.getParams().setParameter(Parameters.ALLOW_CIRCULAR_REDIRECTS, true);
      this.client.getParams().setParameter(Parameters.MAX_REDIRECTS, 5);
      this.client.executeMethod(httpget);
      fail("RedirectException exception should have been thrown");
    } catch (RedirectException is_OK) {
      // $JL-EXC$
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testCircularRedirect() throws IOException {
    this.server.setHttpService(new CircularRedirectService());
    GET httpget = new GET("/circular-oldlocation/");
    try {
      this.client.getParams().setParameter(Parameters.ALLOW_CIRCULAR_REDIRECTS, false);
      this.client.executeMethod(httpget);
      fail("CircularRedirectException exception should have been thrown");
    } catch (CircularRedirectException is_OK) {
      // $JL-EXC$
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testPostRedirect() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port));
    POST httppost = new POST("/oldlocation/");
    httppost.setRequestData(new StringRequestData("stuff"));
    try {
      this.client.executeMethod(httppost);
      assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, httppost.getStatusCode());
      assertEquals("/oldlocation/", httppost.getPath());
      assertEquals(new URI("/oldlocation/", false), httppost.getURI());
    } finally {
      httppost.releaseConnection();
    }
  }

  public void testRelativeRedirect() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new RelativeRedirectService());
    this.client.getParams().setParameter(Parameters.REJECT_RELATIVE_REDIRECT, false);
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      // NWN - this is also temporary (see testBasicRedirect302() & testBasicRedirect307())
//      assertEquals("/relativelocation/", httpget.getPath());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(host, httpget.getURI().getHost());
      assertEquals(port, httpget.getURI().getPort());
//      assertEquals(new URI("http://" + host + ":" + port + "/relativelocation/", false), httpget.getURI());
      assertEquals(new URI("http://" + host + ":" + port + "/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testRejectRelativeRedirect() throws IOException {
    this.server.getLocalAddress();
    this.server.getLocalPort();
    this.server.setHttpService(new RelativeRedirectService());
    this.client.getParams().setParameter(Parameters.REJECT_RELATIVE_REDIRECT, true);
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, httpget.getStatusCode());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(new URI("/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testRejectBogusRedirectLocation() throws IOException {
    this.server.getLocalAddress();
    this.server.getLocalPort();
    this.server.setHttpService(new BogusRedirectService("xxx://bogus"));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      fail("BogusRedirectService should have been thrown");
    } catch (IllegalStateException is_OK) {
      // $JL-EXC$
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testCrossSiteRedirect() throws IOException {
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    SimpleHttpServer thatserver = new SimpleHttpServer();
    this.server.setHttpService(new BasicRedirectService(host, port));
    thatserver.setHttpService(new BasicRedirectService(host, port));
    thatserver.setTestname(getName());
    HostConfiguration hostconfig = new HostConfiguration();
    hostconfig.setHost(thatserver.getLocalAddress(),
            thatserver.getLocalPort(),
            Protocol.getProtocol("http"));

    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(hostconfig, httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      // NWN - this is also moved temporary
//      assertEquals("/newlocation/", httpget.getPath());
      assertEquals("/oldlocation/", httpget.getPath());
      assertEquals(host, httpget.getURI().getHost());
      assertEquals(port, httpget.getURI().getPort());
//      assertEquals(new URI("http://" + host + ":" + port + "/newlocation/", false), httpget.getURI());
      assertEquals(new URI("http://" + host + ":" + port + "/oldlocation/", false), httpget.getURI());
    } finally {
      httpget.releaseConnection();
    }
    thatserver.destroy();
  }

  public void testRedirectWithCookie() throws IOException {
    client.getState().addCookie(new Cookie("localhost", "name", "value", "/", -1, false));
    String host = this.server.getLocalAddress();
    int port = this.server.getLocalPort();
    this.server.setHttpService(new BasicRedirectService(host, port));
    GET httpget = new GET("/oldlocation/");
    httpget.setFollowRedirects(true);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      // NWN - this is also moved_temporary
//      assertEquals("/newlocation/", httpget.getPath());
      assertEquals("/oldlocation/", httpget.getPath());
      ArrayList<Header> headers = httpget.getRequestHeaders();
      int cookiecount = 0;
      for (Header header : headers) {
        if ("cookie".equalsIgnoreCase(header.getName())) {
          ++cookiecount;
        }
      }
      assertEquals("There can only be one (cookie)", 1, cookiecount);
    } finally {
      httpget.releaseConnection();
    }
  }
}