package com.sap.httpclient;

import java.util.Enumeration;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.exception.CredentialsNotAvailableException;
import com.sap.httpclient.auth.*;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.methods.StringRequestData;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.server.AuthRequestHandler;
import com.sap.httpclient.server.HttpRequestHandlerChain;
import com.sap.httpclient.server.HttpServiceHandler;

/**
 * Tests for proxied connections.
 */
public class TestProxy extends HttpClientTestBase {

  public TestProxy(String testName) {
    super(testName);
    setUseProxy(true);
  }

  static class SSLDecorator extends TestSetup {

    public static void addTests(TestSuite suite) {
      TestSuite ts2 = new TestSuite();
      addTest(ts2, suite);
      suite.addTest(ts2);
    }

    private static void addTest(TestSuite suite, Test t) {
      if (t instanceof TestProxy) {
        suite.addTest(new SSLDecorator((TestProxy) t));
      } else if (t instanceof TestSuite) {
        Enumeration en = ((TestSuite) t).tests();
        while (en.hasMoreElements()) {
          addTest(suite, (Test) en.nextElement());
        }
      }
    }

    public SSLDecorator(TestProxy test) {
      super(test);
    }

    protected void setUp() throws Exception {
      TestProxy base = (TestProxy) getTest();
      base.setUseSSL(true);
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TestProxy.class);
    SSLDecorator.addTests(suite);
    return suite;
  }

  class GetItWrongThenGetItRight implements CredentialsProvider {

    private int hostcount = 0;
    private int proxycount = 0;

    public GetItWrongThenGetItRight() {
      super();
    }

    public Credentials getCredentials(AuthScheme scheme, String host, int port, boolean proxy)
            throws CredentialsNotAvailableException {
      if (!proxy) {
        this.hostcount++;
        return provideCredentials(this.hostcount);
      } else {
        this.proxycount++;
        return provideCredentials(this.proxycount);
      }
    }

    private Credentials provideCredentials(int count) {
      switch (count) {
        case 1:
          return new UserPassCredentials("testuser", "wrongstuff");
        case 2:
          return new UserPassCredentials("testuser", "testpass");
        default:
          return null;
      }
    }

  }

  /**
   * Tests GET via non-authenticating proxy
	 *
	 * @throws Exception if an exception occures
   */
  public void testSimpleGet() throws Exception {
    this.server.setHttpService(new FeedbackService());
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via non-authenticating proxy + host auth + connection keep-alive
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetHostAuthConnKeepAlive() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via non-authenticating proxy + host auth + connection close
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetHostAuthConnClose() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", false));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via non-authenticating proxy + invalid host auth
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetHostInvalidAuth() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.client.getState().setCredentials(AuthScope.ANY, new UserPassCredentials("testuser", "wrongstuff"));
    this.server.setRequestHandler(handlerchain);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_UNAUTHORIZED, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via non-authe* ticating proxy + interactive host auth + connection keep-alive
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetInteractiveHostAuthConnKeepAlive() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getParams().setParameter(CredentialsProvider.PROVIDER, new GetItWrongThenGetItRight());
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via non-authenticating proxy + interactive host auth + connection close
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetInteractiveHostAuthConnClose() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getParams().setParameter(CredentialsProvider.PROVIDER, new GetItWrongThenGetItRight());
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", false));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via authenticating proxy + host auth + connection keep-alive
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetProxyAuthHostAuthConnKeepAlive() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via authenticating proxy
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetAuthProxy() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    this.server.setHttpService(new FeedbackService());
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via authenticating proxy + host auth + connection close
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetProxyAuthHostAuthConnClose() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", false));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via authenticating proxy + invalid host auth
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetProxyAuthHostInvalidAuth() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.client.getState().setCredentials(AuthScope.ANY, new UserPassCredentials("testuser", "wrongstuff"));
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_UNAUTHORIZED, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via authenticating proxy + interactive host and proxy auth + connection keep-alive
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetInteractiveProxyAuthHostAuthConnKeepAlive() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getParams().setParameter(CredentialsProvider.PROVIDER, new GetItWrongThenGetItRight());
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via authenticating proxy + interactive host and proxy auth + connection close
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetInteractiveProxyAuthHostAuthConnClose() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getParams().setParameter(CredentialsProvider.PROVIDER, new GetItWrongThenGetItRight());
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", false));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy
	 *
	 * @throws Exception if an exception occures
   */
  public void testSimplePost() throws Exception {
    this.server.setHttpService(new FeedbackService());
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy + host auth + connection keep-alive
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostHostAuthConnKeepAlive() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy + host auth + connection close
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostHostAuthConnClose() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", false));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy + invalid host auth
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostHostInvalidAuth() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.client.getState().setCredentials(AuthScope.ANY, new UserPassCredentials("testuser", "wrongstuff"));
    this.server.setRequestHandler(handlerchain);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_UNAUTHORIZED, post.getStatusCode());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy + interactive host auth + connection keep-alive
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostInteractiveHostAuthConnKeepAlive() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getParams().setParameter(CredentialsProvider.PROVIDER, new GetItWrongThenGetItRight());
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy + interactive host auth + connection close
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostInteractiveHostAuthConnClose() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getParams().setParameter(CredentialsProvider.PROVIDER, new GetItWrongThenGetItRight());
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", false));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via authenticating proxy
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostAuthProxy() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    this.server.setHttpService(new FeedbackService());
    this.proxy.requireAuthentication(creds, "test", true);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via authenticating proxy + host auth + connection keep-alive
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostProxyAuthHostAuthConnKeepAlive() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via authenticating proxy + host auth + connection close
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostProxyAuthHostAuthConnClose() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", false));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy + invalid host auth
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostProxyAuthHostInvalidAuth() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.client.getState().setCredentials(AuthScope.ANY, new UserPassCredentials("testuser", "wrongstuff"));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_UNAUTHORIZED, post.getStatusCode());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy + interactive host auth + connection keep-alive
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostInteractiveProxyAuthHostAuthConnKeepAlive() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getParams().setParameter(CredentialsProvider.PROVIDER, new GetItWrongThenGetItRight());
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  /**
   * Tests POST via non-authenticating proxy + interactive host auth + connection close
	 *
	 * @throws Exception if an exception occures
   */
  public void testPostInteractiveProxyAuthHostAuthConnClose() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getParams().setParameter(CredentialsProvider.PROVIDER, new GetItWrongThenGetItRight());
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", false));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", true);
    POST post = new POST("/");
    post.setRequestData(new StringRequestData("Like tons of stuff"));
    try {
      this.client.executeMethod(post);
      assertEquals(HttpStatus.SC_OK, post.getStatusCode());
      assertNotNull(post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
  }

  public void testPreemptiveAuthProxy() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    this.client.getParams().setAuthenticationPreemptive(true);
    this.server.setHttpService(new FeedbackService());
    this.proxy.requireAuthentication(creds, "test", true);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
      if (isUseSSL()) {
        assertNull(get.getRequestHeader("Proxy-Authorization"));
      } else {
        assertNotNull(get.getRequestHeader("Proxy-Authorization"));
      }
    } finally {
      get.releaseConnection();
    }
  }

  /**
   * Tests GET via authenticating proxy + host auth + HTTP/1.0
	 *
	 * @throws Exception if an exception occures
   */
  public void testGetProxyAuthHostAuthHTTP10() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    this.client.getState().setCredentials(AuthScope.ANY, creds);
    this.client.getState().setProxyCredentials(AuthScope.ANY, creds);
    this.client.getParams().setVersion(HttpVersion.HTTP_1_0);
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test", true));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    this.proxy.requireAuthentication(creds, "test", false);
    GET get = new GET("/");
    try {
      this.client.executeMethod(get);
      assertEquals(HttpStatus.SC_OK, get.getStatusCode());
    } finally {
      get.releaseConnection();
    }
  }

}