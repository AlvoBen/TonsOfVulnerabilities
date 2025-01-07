﻿package com.sap.httpclient.auth;

import java.io.IOException;
import java.util.Map;

import com.sap.httpclient.FakeHttpMethod;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.HttpClient;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.exception.MalformedChallengeException;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.server.HttpService;
import com.sap.httpclient.server.RequestLine;
import com.sap.httpclient.server.SimpleHttpServer;
import com.sap.httpclient.server.SimpleRequest;
import com.sap.httpclient.server.SimpleResponse;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test Methods for DigestScheme Authentication.
 */
public class TestDigestAuth extends TestCase {

  public TestDigestAuth(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestDigestAuth.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestDigestAuth.class);
  }

  public void testDigestAuthenticationWithNoRealm() throws Exception {
    String challenge = "Digest";
    try {
      AuthScheme authscheme = new DigestScheme();
      authscheme.processChallenge(challenge);
      fail("Should have thrown MalformedChallengeException");
    } catch (MalformedChallengeException is_OK) {
      // $JL-EXC$
    }
  }

  public void testDigestAuthenticationWithNoRealm2() throws Exception {
    String challenge = "Digest ";
    try {
      AuthScheme authscheme = new DigestScheme();
      authscheme.processChallenge(challenge);
      fail("Should have thrown MalformedChallengeException");
    } catch (MalformedChallengeException is_OK) {
      // $JL-EXC$
    }
  }

  public void testDigestAuthenticationWithDefaultCreds() throws Exception {
    String challenge = "Digest realm=\"realm1\", nonce=\"f2a3f18799759d4f1a1c068b92b573cb\"";
    FakeHttpMethod method = new FakeHttpMethod("/");
    UserPassCredentials cred = new UserPassCredentials("username", "password");
    AuthScheme authscheme = new DigestScheme();
    authscheme.processChallenge(challenge);
    String response = authscheme.authenticate(cred, method);
    Map table = AuthChallengeParser.extractParams(response);
    assertEquals("username", table.get("username"));
    assertEquals("realm1", table.get("realm"));
    assertEquals("/", table.get("uri"));
    assertEquals("f2a3f18799759d4f1a1c068b92b573cb", table.get("nonce"));
    assertEquals("e95a7ddf37c2eab009568b1ed134f89a", table.get("response"));
  }

  public void testDigestAuthentication() throws Exception {
    String challenge = "Digest realm=\"realm1\", nonce=\"f2a3f18799759d4f1a1c068b92b573cb\"";
    UserPassCredentials cred = new UserPassCredentials("username", "password");
    FakeHttpMethod method = new FakeHttpMethod("/");
    AuthScheme authscheme = new DigestScheme();
    authscheme.processChallenge(challenge);
    String response = authscheme.authenticate(cred, method);
    Map table = AuthChallengeParser.extractParams(response);
    assertEquals("username", table.get("username"));
    assertEquals("realm1", table.get("realm"));
    assertEquals("/", table.get("uri"));
    assertEquals("f2a3f18799759d4f1a1c068b92b573cb", table.get("nonce"));
    assertEquals("e95a7ddf37c2eab009568b1ed134f89a", table.get("response"));
  }

  public void testDigestAuthenticationWithMultipleRealms() throws Exception {
    String challenge1 = "Digest realm=\"realm1\", nonce=\"abcde\"";
    String challenge2 = "Digest realm=\"realm2\", nonce=\"123546\"";
    UserPassCredentials cred = new UserPassCredentials("username", "password");
    UserPassCredentials cred2 = new UserPassCredentials("uname2", "password2");
    FakeHttpMethod method = new FakeHttpMethod("/");
    AuthScheme authscheme1 = new DigestScheme();
    authscheme1.processChallenge(challenge1);
    String response1 = authscheme1.authenticate(cred, method);
    Map table = AuthChallengeParser.extractParams(response1);
    assertEquals("username", table.get("username"));
    assertEquals("realm1", table.get("realm"));
    assertEquals("/", table.get("uri"));
    assertEquals("abcde", table.get("nonce"));
    assertEquals("786f500303eac1478f3c2865e676ed68", table.get("response"));
    AuthScheme authscheme2 = new DigestScheme();
    authscheme2.processChallenge(challenge2);
    String response2 = authscheme2.authenticate(cred2, method);
    table = AuthChallengeParser.extractParams(response2);
    assertEquals("uname2", table.get("username"));
    assertEquals("realm2", table.get("realm"));
    assertEquals("/", table.get("uri"));
    assertEquals("123546", table.get("nonce"));
    assertEquals("0283edd9ef06a38b378b3b74661391e9", table.get("response"));
  }

  /**
   * Test digest authentication using the MD5-sess algorithm.
	 *
	 * @throws Exception if any exception occures
	 */
  public void testDigestAuthenticationMD5Sess() throws Exception {
    // Example using Digest auth with MD5-sess

    String realm = "realm";
    String username = "username";
    String password = "password";
    String nonce = "e273f1776275974f1a120d8b92c5b3cb";
    String challenge = "Digest realm=\"" + realm + "\", "
            + "nonce=\"" + nonce + "\", "
            + "opaque=\"SomeString\", "
            + "stale=false, "
            + "algorithm=MD5-sess, "
            + "qop=\"auth,auth-int\""; // we pass both but expect auth to be used

    UserPassCredentials cred = new UserPassCredentials(username, password);
    FakeHttpMethod method = new FakeHttpMethod("/");
    AuthScheme authscheme = new DigestScheme();
    authscheme.processChallenge(challenge);
    String response = authscheme.authenticate(cred, method);
    assertTrue(response.indexOf("nc=00000001") > 0); // test for quotes
    assertTrue(response.indexOf("qop=auth") > 0); // test for quotes
    Map table = AuthChallengeParser.extractParams(response);
    assertEquals(username, table.get("username"));
    assertEquals(realm, table.get("realm"));
    assertEquals("MD5-sess", table.get("algorithm"));
    assertEquals("/", table.get("uri"));
    assertEquals(nonce, table.get("nonce"));
    assertEquals(1, Integer.parseInt((String) table.get("nc"), 16));
    assertTrue(null != table.get("cnonce"));
    assertEquals("SomeString", table.get("opaque"));
    assertEquals("auth", table.get("qop"));
    assertTrue(null != table.get("response"));
  }

  /**
   * Test digest authentication using the MD5-sess algorithm.
	 *
	 * @throws Exception if any exception occures
   */
  public void testDigestAuthenticationMD5SessNoQop() throws Exception {
    // Example using Digest auth with MD5-sess
    String realm = "realm";
    String username = "username";
    String password = "password";
    String nonce = "e273f1776275974f1a120d8b92c5b3cb";

    String challenge = "Digest realm=\"" + realm + "\", "
            + "nonce=\"" + nonce + "\", "
            + "opaque=\"SomeString\", "
            + "stale=false, "
            + "algorithm=MD5-sess";

    UserPassCredentials cred = new UserPassCredentials(username, password);
    FakeHttpMethod method = new FakeHttpMethod("/");
    AuthScheme authscheme = new DigestScheme();
    authscheme.processChallenge(challenge);
    String response = authscheme.authenticate(cred, method);
    Map table = AuthChallengeParser.extractParams(response);
    assertEquals(username, table.get("username"));
    assertEquals(realm, table.get("realm"));
    assertEquals("MD5-sess", table.get("algorithm"));
    assertEquals("/", table.get("uri"));
    assertEquals(nonce, table.get("nonce"));
    assertTrue(null == table.get("nc"));
    assertEquals("SomeString", table.get("opaque"));
    assertTrue(null == table.get("qop"));
    assertTrue(null != table.get("response"));
  }

  /**
   * Test digest authentication with invalud qop value
	 *
	 * @throws Exception if any exception occures
   */
  public void testDigestAuthenticationMD5SessInvalidQop() throws Exception {
    // Example using Digest auth with MD5-sess

    String realm = "realm";
    String username = "username";
    String password = "password";
    String nonce = "e273f1776275974f1a120d8b92c5b3cb";
    String challenge = "Digest realm=\"" + realm + "\", "
            + "nonce=\"" + nonce + "\", "
            + "opaque=\"SomeString\", "
            + "stale=false, "
            + "algorithm=MD5-sess, "
            + "qop=\"nwn\""; // nwn is an invalid qop value

    new UserPassCredentials(username, password);
    try {
      AuthScheme authscheme = new DigestScheme();
      authscheme.processChallenge(challenge);
      fail("MalformedChallengeException exception expected due to invalid qop value");
    } catch (MalformedChallengeException is_OK) {
      // $JL-EXC$
    }
  }

  private class StaleNonceService implements HttpService {

    public StaleNonceService() {
      super();
    }

    public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException {
      RequestLine requestLine = request.getRequestLine();
      HttpVersion ver = requestLine.getHttpVersion();
      Header auth = request.getFirstHeader("Authorization");
      if (auth == null) {
        response.setStatusLine(ver, HttpStatus.SC_UNAUTHORIZED);
        response.addHeader(new Header("WWW-Authenticate", "Digest realm=\"realm1\", nonce=\"ABC123\""));
        response.setBodyString("Authorization required");
        return true;
      } else {
        Map table = AuthChallengeParser.extractParams(auth.getValue());
        String nonce = (String) table.get("nonce");
        if (nonce.equals("ABC123")) {
          response.setStatusLine(ver, HttpStatus.SC_UNAUTHORIZED);
          response.addHeader(new Header("WWW-Authenticate",
                  "Digest realm=\"realm1\", nonce=\"321CBA\", stale=\"true\""));
          response.setBodyString("Authorization required");
          return true;
        } else {
          response.setStatusLine(ver, HttpStatus.SC_OK);
          response.setBodyString("Authorization successful");
          return true;
        }
      }
    }
  }

  public void testDigestAuthenticationWithStaleNonce() throws Exception {
    // configure the server
    SimpleHttpServer server = new SimpleHttpServer(); // use arbitrary port
    server.setTestname(getName());
    server.setHttpService(new StaleNonceService());
    // configure the client
    HttpClient client = new HttpClient();
    client.getHostConfiguration().setHost(server.getLocalAddress(), server.getLocalPort(),
            Protocol.getProtocol("http"));

    client.getState().setCredentials(AuthScope.ANY, new UserPassCredentials("username", "password"));
    FakeHttpMethod httpget = new FakeHttpMethod("/");
    try {
      client.executeMethod(httpget);
    } finally {
      httpget.releaseConnection();
    }
    assertNotNull(httpget.getStatusLine());
    assertEquals(HttpStatus.SC_OK, httpget.getStatusLine().getStatusCode());
    Map table = AuthChallengeParser.extractParams(httpget.getRequestHeader("Authorization").getValue());
    assertEquals("username", table.get("username"));
    assertEquals("realm1", table.get("realm"));
    assertEquals("/", table.get("uri"));
    assertEquals("321CBA", table.get("nonce"));
    assertEquals("7f5948eefa115296e9279225041527b3", table.get("response"));
    server.destroy();
  }

}