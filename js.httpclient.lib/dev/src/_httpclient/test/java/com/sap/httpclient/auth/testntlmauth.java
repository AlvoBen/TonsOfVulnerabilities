package com.sap.httpclient.auth;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.FakeHttpMethod;
import com.sap.httpclient.HttpClientTestBase;
import com.sap.httpclient.HttpState;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.RequestLine;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;

/**
 * Test Methods for NTLM Authentication.
 */
public class TestNTLMAuth extends HttpClientTestBase {

  public TestNTLMAuth(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestNTLMAuth.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestNTLMAuth.class);
  }

  public void testNTLMAuthenticationResponse1() throws Exception {
    String challenge = "NTLM";
    String expected = "NTLM TlRMTVNTUAABAAAABlIAAAYABgAkAAAABAAEACAAAABIT1NURE9NQUlO";
    NTCredentials cred = new NTCredentials("username", "password", "host", "domain");
    FakeHttpMethod method = new FakeHttpMethod();
    AuthScheme authscheme = new NTLMScheme(challenge);
    authscheme.processChallenge(challenge);
    String response = authscheme.authenticate(cred, method);
    assertEquals(expected, response);
    assertFalse(authscheme.isComplete());
  }

  public void testNTLMAuthenticationResponse2() throws Exception {
    String challenge =
            "NTLM TlRMTVNTUAACAAAACgAKADAAAAAGgoEAPc4kP4LtCV8AAAAAAAAAAJ4AngA" +
            "6AAAASU5UUkFFUEhPWAIAFABJAE4AVABSAEEARQBQAEgATwBYAAEAEgBCAE8AQQB" +
            "SAEQAUgBPAE8ATQAEACgAaQBuAHQAcgBhAGUAcABoAG8AeAAuAGUAcABoAG8AeAA" +
            "uAGMAbwBtAAMAPABCAG8AYQByAGQAcgBvAG8AbQAuAGkAbgB0AHIAYQBlAHAAaAB" +
            "vAHgALgBlAHAAaABvAHgALgBjAG8AbQAAAAAA";

    String expected = "NTLM TlRMTVNTUAADAAAAGAAYAFIAAAAAAAAAagAAAAYABgB" +
            "AAAAACAAIAEYAAAAEAAQATgAAAAAAAABqAAAABlIAAERPTUFJTlVTRVJOQU1FSE" +
            "9TVAaC+vLxUEHnUtpItj9Dp4kzwQfd61Lztg==";
    NTCredentials cred = new NTCredentials("username", "password", "host", "domain");
    FakeHttpMethod method = new FakeHttpMethod();
    AuthScheme authscheme = new NTLMScheme(challenge);
    authscheme.processChallenge(challenge);
    String response = authscheme.authenticate(cred, method);
    assertEquals(expected, response);
    assertTrue(authscheme.isComplete());
  }

  private class NTLMAuthService implements HttpService {

    public NTLMAuthService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine requestLine = request.getRequestLine();
      HttpVersion ver = requestLine.getHttpVersion();
      Header auth = request.getFirstHeader("Authorization");
      if (auth == null) {
        response.setStatusLine(ver, HttpStatus.SC_UNAUTHORIZED);
        response.addHeader(new Header("WWW-Authenticate", "NTLM"));
        response.setBodyString("Authorization required");
        return true;
      } else {
        String authstr = auth.getValue();
        if (authstr.equals("NTLM TlRMTVNTUAABAAAABlIAAAYABgAkAAAABAAEACAAAABIT1NURE9NQUlO")) {
          response.setStatusLine(ver, HttpStatus.SC_UNAUTHORIZED);
          response.addHeader(new Header("WWW-Authenticate",
                  "NTLM TlRMTVNTUAACAAAAAAAAACgAAAABggAAU3J2Tm9uY2UAAAAAAAAAAA=="));
          response.setBodyString("Authorization required");
          return true;
        }
        if (authstr.equals("NTLM TlRMTVNTUAADAAAAGAAYAFIAAAAAAAAAagAAAAYABgBAAAAACAAIAEYAAAAEAAQATgAAAAAAAABqAAAABlIAAERPTUFJTlVTRVJOQU1FSE9TVJxndWIt46bHm11TPrt5Z6wrz7ziq04yRA==")) {
          response.setStatusLine(ver, HttpStatus.SC_OK);
          response.setBodyString("Authorization successful");
          return true;
        } else {
          response.setStatusLine(ver, HttpStatus.SC_UNAUTHORIZED);
          response.addHeader(new Header("WWW-Authenticate", "NTLM"));
          response.setBodyString("Authorization required");
          return true;
        }
      }
    }
  }


  public void testNTLMAuthenticationRetry() throws Exception {

    this.server.setHttpService(new NTLMAuthService());

    // configure the client
    this.client.getHostConfiguration().setHost(server.getLocalAddress(), server.getLocalPort(),
            Protocol.getProtocol("http"));

    this.client.getState().setCredentials(AuthScope.ANY,
            new NTCredentials("username", "password", "host", "domain"));

    FakeHttpMethod httpget = new FakeHttpMethod("/");
    try {
      client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNull(httpget.getResponseHeader("WWW-Authenticate"));
    assertEquals(200, httpget.getStatusCode());
  }

  private class PreemptiveNTLMAuthService implements HttpService {

    public PreemptiveNTLMAuthService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine requestLine = request.getRequestLine();
      HttpVersion ver = requestLine.getHttpVersion();
      Header auth = request.getFirstHeader("Authorization");
      if (auth == null) {
        response.setStatusLine(ver, HttpStatus.SC_BAD_REQUEST);
        response.setBodyString("Authorization header missing");
        return true;
      } else {
        String authstr = auth.getValue();
        if (authstr.indexOf("NTLM") != -1) {
          response.setStatusLine(ver, HttpStatus.SC_OK);
          return true;
        } else if (authstr.indexOf("Basic") != -1) {
          response.setStatusLine(ver, HttpStatus.SC_UNAUTHORIZED);
          response.addHeader(new Header("WWW-Authenticate", "Negotiate"));
          response.addHeader(new Header("WWW-Authenticate", "NTLM"));
          response.setBodyString("Authorization required");
          return true;
        } else {
          response.setStatusLine(ver, HttpStatus.SC_BAD_REQUEST);
          response.setBodyString("Unknown auth type: " + authstr);
          return true;
        }
      }
    }
  }

  /**
   * Make sure preemptive authorization works when the server requires NLM.
   *
   * @throws Exception if any exception occures
   */
  public void testPreemptiveAuthorization() throws Exception {

    NTCredentials creds = new NTCredentials("testuser", "testpass", "host", "domain");
    HttpState state = new HttpState();
    state.setCredentials(AuthScope.ANY, creds);
    this.client.setState(state);
    this.client.getParams().setAuthenticationPreemptive(true);
    this.server.setHttpService(new PreemptiveNTLMAuthService());
    GET httpget = new GET("/test/");
    try {
      this.client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
  }

}