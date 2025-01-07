package com.sap.httpclient.auth;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.EchoService;
import com.sap.httpclient.FeedbackService;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.HttpClientTestBase;
import com.sap.httpclient.HttpState;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.ProxyTestDecorator;
import com.sap.httpclient.uri.EncodingUtil;
import com.sap.httpclient.exception.MalformedChallengeException;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.methods.HEAD;
import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.methods.PUT;
import com.sap.httpclient.http.methods.StringRequestData;
import com.sap.httpclient.server.AuthRequestHandler;
import com.sap.httpclient.server.HttpRequestHandlerChain;
import com.sap.httpclient.server.HttpServiceHandler;

/**
 * Basic authentication test cases.
 */
public class TestBasicAuth extends HttpClientTestBase {

  public TestBasicAuth(final String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestBasicAuth.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TestBasicAuth.class);
    ProxyTestDecorator.addTests(suite);
    return suite;
  }

  public void testBasicAuthenticationWithNoCreds() throws IOException {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertNotNull(httpget.getStatusLine());
      assertEquals(HttpStatus.SC_UNAUTHORIZED, httpget.getStatusLine().getStatusCode());
      AuthState authstate = httpget.getHostAuthState();
      assertNotNull(authstate.getAuthScheme());
      assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
      assertEquals("test", authstate.getRealm());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testBasicAuthenticationWithNoCredsRetry() throws IOException {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
      assertNotNull(httpget.getStatusLine());
      assertEquals(HttpStatus.SC_UNAUTHORIZED, httpget.getStatusLine().getStatusCode());
      AuthState authstate = httpget.getHostAuthState();
      assertNotNull(authstate.getAuthScheme());
      assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
      assertEquals("test", authstate.getRealm());
    } finally {
      httpget.releaseConnection();
    }
    // now try with credentials
    httpget = new GET("/test/");
    try {
      this.client.getState().setCredentials(AuthScope.ANY, creds);
      this.client.executeMethod(httpget);
      assertNotNull(httpget.getStatusLine());
      assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testBasicAuthenticationWithNoRealm() {
    String challenge = "Basic";
    try {
      AuthScheme authscheme = new BasicScheme();
      authscheme.processChallenge(challenge);
      fail("Should have thrown MalformedChallengeException");
    } catch (MalformedChallengeException is_OK) {
      // $JL-EXC$
    }
  }

  public void testBasicAuthenticationWith88591Chars() throws Exception {
    int[] germanChars = {0xE4, 0x2D, 0xF6, 0x2D, 0xFc};
    StringBuilder buffer = new StringBuilder();
		for (int germanChar : germanChars) {
			buffer.append((char) germanChar);
		}
		UserPassCredentials credentials = new UserPassCredentials("dh", buffer.toString());
    assertEquals("Basic ZGg65C32Lfw=", BasicScheme.authenticate(credentials, "ISO-8859-1"));
  }

  public void testBasicAuthenticationWithDefaultCreds() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    state.setCredentials(AuthScope.ANY, creds);
    this.client.setState(state);
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    Header auth = httpget.getRequestHeader("Authorization");
    assertNotNull(auth);
    String expected = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser:testpass"));
    assertEquals(expected, auth.getValue());
    AuthState authstate = httpget.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test", authstate.getRealm());
  }

  public void testBasicAuthentication() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    AuthScope authscope = new AuthScope(this.server.getLocalAddress(), this.server.getLocalPort(), "test");
    state.setCredentials(authscope, creds);
    this.client.setState(state);
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    Header auth = httpget.getRequestHeader("Authorization");
    assertNotNull(auth);
    String expected = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser:testpass"));
    assertEquals(expected, auth.getValue());
    AuthState authstate = httpget.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test", authstate.getRealm());
  }

  public void testBasicAuthenticationWithInvalidCredentials() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    AuthScope authscope = new AuthScope(this.server.getLocalAddress(),
            this.server.getLocalPort(),
            "test");
    state.setCredentials(authscope, new UserPassCredentials("test", "stuff"));
    this.client.setState(state);
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_UNAUTHORIZED, httpget.getStatusLine().getStatusCode());
    AuthState authstate = httpget.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test", authstate.getRealm());
  }

  public void testBasicAuthenticationWithMutlipleRealms1() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    AuthScope realm1 = new AuthScope(this.server.getLocalAddress(),
            this.server.getLocalPort(),
            "test");
    AuthScope realm2 = new AuthScope(this.server.getLocalAddress(),
            this.server.getLocalPort(),
            "test2");
    state.setCredentials(realm1, new UserPassCredentials("testuser", "testpass"));
    state.setCredentials(realm2, new UserPassCredentials("testuser2", "testpass2"));
    this.client.setState(state);
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    Header auth = httpget.getRequestHeader("Authorization");
    assertNotNull(auth);
    String expected = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser:testpass"));
    assertEquals(expected, auth.getValue());
    AuthState authstate = httpget.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test", authstate.getRealm());
  }

  public void testBasicAuthenticationWithMutlipleRealms2() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser2", "testpass2");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds, "test2"));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    AuthScope realm1 = new AuthScope(this.server.getLocalAddress(),
            this.server.getLocalPort(),
            "test");
    AuthScope realm2 = new AuthScope(this.server.getLocalAddress(),
            this.server.getLocalPort(),
            "test2");
    state.setCredentials(realm1, new UserPassCredentials("testuser", "testpass"));
    state.setCredentials(realm2, new UserPassCredentials("testuser2", "testpass2"));
    this.client.setState(state);
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test2/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    Header auth = httpget.getRequestHeader("Authorization");
    assertNotNull(auth);
    String expected = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser2:testpass2"));
    assertEquals(expected, auth.getValue());
    AuthState authstate = httpget.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test2", authstate.getRealm());
  }

  public void testPreemptiveAuthorizationTrueWithCreds() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    state.setCredentials(AuthScope.ANY, creds);
    this.client.setState(state);
    this.client.getParams().setAuthenticationPreemptive(true);
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    Header auth = httpget.getRequestHeader("Authorization");
    assertNotNull(auth);
    String expected = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser:testpass"));
    assertEquals(expected, auth.getValue());
    AuthState authstate = httpget.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertNull(authstate.getRealm());
    assertTrue(authstate.isPreemptive());
  }

  public void testPreemptiveAuthorizationTrueWithoutCreds() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    this.client.setState(state);
    this.client.getParams().setAuthenticationPreemptive(true);
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_UNAUTHORIZED, httpget.getStatusLine().getStatusCode());
    Header auth = httpget.getRequestHeader("Authorization");
    assertNull(auth);
    AuthState authstate = httpget.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertNotNull(authstate.getRealm());
    assertTrue(authstate.isPreemptive());
  }

  public void testCustomAuthorizationHeader() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    String authResponse = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser:testpass"));
    httpget.addRequestHeader(new Header("Authorization", authResponse));
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
  }

  public void testHeadBasicAuthentication() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    AuthScope authscope = new AuthScope(this.server.getLocalAddress(), this.server.getLocalPort(), "test");
    state.setCredentials(authscope, creds);
    this.client.setState(state);
    this.server.setRequestHandler(handlerchain);
    HEAD head = new HEAD("/test/");
    try {
      this.client.executeMethod(head);
    } finally {
      head.releaseConnection();
    }
    assertNotNull(head.getStatusLine());
    assertEquals(HttpStatus.SC_OK, head.getStatusLine().getStatusCode());
    Header auth = head.getRequestHeader("Authorization");
    assertNotNull(auth);
    String expected = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser:testpass"));
    assertEquals(expected, auth.getValue());
    AuthState authstate = head.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test", authstate.getRealm());
  }

  public void testPostBasicAuthentication() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new EchoService()));
    HttpState state = new HttpState();
    AuthScope authscope = new AuthScope(this.server.getLocalAddress(), this.server.getLocalPort(), "test");
    state.setCredentials(authscope, creds);
    this.client.setState(state);
    this.server.setRequestHandler(handlerchain);
    POST post = new POST("/test/");
    post.setRequestData(new StringRequestData("Test body"));
    try {
      this.client.executeMethod(post);
      assertEquals("Test body", post.getResponseBodyAsString());
    } finally {
      post.releaseConnection();
    }
    assertNotNull(post.getStatusLine());
    assertEquals(HttpStatus.SC_OK, post.getStatusLine().getStatusCode());
    Header auth = post.getRequestHeader("Authorization");
    assertNotNull(auth);
    String expected = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser:testpass"));
    assertEquals(expected, auth.getValue());
    AuthState authstate = post.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test", authstate.getRealm());
  }

  public void testPutBasicAuthentication() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new EchoService()));
    HttpState state = new HttpState();
    AuthScope authscope = new AuthScope(this.server.getLocalAddress(), this.server.getLocalPort(), "test");
    state.setCredentials(authscope, creds);
    this.client.setState(state);
    this.server.setRequestHandler(handlerchain);
    PUT put = new PUT("/test/");
    put.setRequestData(new StringRequestData("Test body"));
    try {
      this.client.executeMethod(put);
      assertEquals("Test body", put.getResponseBodyAsString());
    } finally {
      put.releaseConnection();
    }
    assertNotNull(put.getStatusLine());
    assertEquals(HttpStatus.SC_OK, put.getStatusLine().getStatusCode());
    Header auth = put.getRequestHeader("Authorization");
    assertNotNull(auth);
    String expected = "Basic " + EncodingUtil.getBase64EncodedString(EncodingUtil.getASCIIBytes("testuser:testpass"));
    assertEquals(expected, auth.getValue());
    AuthState authstate = put.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test", authstate.getRealm());
  }

  public void testPreemptiveAuthorizationFailure() throws Exception {
    UserPassCredentials creds = new UserPassCredentials("testuser", "testpass");
    UserPassCredentials wrongcreds = new UserPassCredentials("testuser", "garbage");
    HttpRequestHandlerChain handlerchain = new HttpRequestHandlerChain();
    handlerchain.appendHandler(new AuthRequestHandler(creds));
    handlerchain.appendHandler(new HttpServiceHandler(new FeedbackService()));
    HttpState state = new HttpState();
    state.setCredentials(AuthScope.ANY, wrongcreds);
    this.client.setState(state);
    this.client.getParams().setAuthenticationPreemptive(true);
    this.server.setRequestHandler(handlerchain);
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_UNAUTHORIZED, httpget.getStatusLine().getStatusCode());
    AuthState authstate = httpget.getHostAuthState();
    assertNotNull(authstate.getAuthScheme());
    assertTrue(authstate.getAuthScheme() instanceof BasicScheme);
    assertEquals("test", authstate.getRealm());
    assertTrue(authstate.isPreemptive());
  }

}