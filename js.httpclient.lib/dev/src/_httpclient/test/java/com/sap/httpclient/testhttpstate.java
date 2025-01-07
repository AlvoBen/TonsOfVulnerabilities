package com.sap.httpclient;

import com.sap.httpclient.auth.AuthScope;
import com.sap.httpclient.auth.UserPassCredentials;
import com.sap.httpclient.auth.Credentials;
import com.sap.httpclient.http.cookie.Cookie;
import junit.framework.*;

/**
 * Simple tests for {@link HttpState}.
 */
public class TestHttpState extends TestCase {

  public final static Credentials CREDS1 = new UserPassCredentials("user1", "pass1");
  public final static Credentials CREDS2 = new UserPassCredentials("user2", "pass2");

  public final static AuthScope SCOPE1 = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "realm1");
  public final static AuthScope SCOPE2 = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "realm2");
  public final static AuthScope BOGUS = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "bogus");
  public final static AuthScope DEFSCOPE = new AuthScope("host", AuthScope.ANY_PORT, "realm");

  public TestHttpState(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestHttpState.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestHttpState.class);
  }

  public void testHttpStateCredentials() {
    HttpState state = new HttpState();
    state.setCredentials(SCOPE1, CREDS1);
    state.setCredentials(SCOPE2, CREDS2);
    assertEquals(CREDS1, state.getCredentials(SCOPE1));
    assertEquals(CREDS2, state.getCredentials(SCOPE2));
  }

  public void testToString() {
    HttpState state = new HttpState();
    assertNotNull(state.toString());
    state.addCookie(new Cookie("foo", "bar", "yeah"));
    assertNotNull(state.toString());
    state.addCookie(new Cookie("flub", "duck", "yuck"));
    assertNotNull(state.toString());
    state.setCredentials(SCOPE1, CREDS1);
    assertNotNull(state.toString());
    state.setProxyCredentials(SCOPE2, CREDS2);
    assertNotNull(state.toString());
  }

  public void testHttpStateNoCredentials() {
    HttpState state = new HttpState();
    assertEquals(null, state.getCredentials(BOGUS));
  }

  public void testHttpStateDefaultCredentials() {
    HttpState state = new HttpState();
    state.setCredentials(AuthScope.ANY, CREDS1);
    state.setCredentials(SCOPE2, CREDS2);
    assertEquals(CREDS1, state.getCredentials(BOGUS));
  }

  public void testHttpStateProxyCredentials() {
    HttpState state = new HttpState();
    state.setProxyCredentials(SCOPE1, CREDS1);
    state.setProxyCredentials(SCOPE2, CREDS2);
    assertEquals(CREDS1, state.getProxyCredentials(SCOPE1));
    assertEquals(CREDS2, state.getProxyCredentials(SCOPE2));
  }

  public void testHttpStateProxyNoCredentials() {
    HttpState state = new HttpState();
    assertEquals(null, state.getProxyCredentials(BOGUS));
  }

  public void testHttpStateProxyDefaultCredentials() {
    HttpState state = new HttpState();
    state.setProxyCredentials(AuthScope.ANY, CREDS1);
    state.setProxyCredentials(SCOPE2, CREDS2);
    assertEquals(CREDS1, state.getProxyCredentials(BOGUS));
  }

  public void testDefaultCredentials() throws Exception {
    HttpState state = new HttpState();
    Credentials expected = new UserPassCredentials("name", "pass");
    state.setCredentials(AuthScope.ANY, expected);
    Credentials got = state.getCredentials(DEFSCOPE);
    assertEquals(got, expected);
  }

  public void testRealmCredentials() throws Exception {
    HttpState state = new HttpState();
    Credentials expected = new UserPassCredentials("name", "pass");
    state.setCredentials(DEFSCOPE, expected);
    Credentials got = state.getCredentials(DEFSCOPE);
    assertEquals(expected, got);
  }

  public void testHostCredentials() throws Exception {
    HttpState state = new HttpState();
    Credentials expected = new UserPassCredentials("name", "pass");
    state.setCredentials(new AuthScope("host", AuthScope.ANY_PORT, AuthScope.ANY_REALM), expected);
    Credentials got = state.getCredentials(DEFSCOPE);
    assertEquals(expected, got);
  }

  public void testWrongHostCredentials() throws Exception {
    HttpState state = new HttpState();
    Credentials expected = new UserPassCredentials("name", "pass");
    state.setCredentials(new AuthScope("host1", AuthScope.ANY_PORT, "realm"), expected);
    Credentials got = state.getCredentials(new AuthScope("host2", AuthScope.ANY_PORT, "realm"));
    assertNotSame(expected, got);
  }

  public void testWrongRealmCredentials() throws Exception {
    HttpState state = new HttpState();
    Credentials cred = new UserPassCredentials("name", "pass");
    state.setCredentials(new AuthScope("host", AuthScope.ANY_PORT, "realm1"), cred);
    Credentials got = state.getCredentials(new AuthScope("host", AuthScope.ANY_PORT, "realm2"));
    assertNotSame(cred, got);
  }

  public void testScopeMatching() {
    AuthScope authscope1 = new AuthScope("somehost", 80, "somerealm", "somescheme");
    AuthScope authscope2 = new AuthScope("someotherhost", 80, "somerealm", "somescheme");
    assertTrue(authscope1.match(authscope2) < 0);
    int m1 = authscope1.match(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, "somescheme"));
    int m2 = authscope1.match(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "somerealm", AuthScope.ANY_SCHEME));
    assertTrue(m2 > m1);
    m1 = authscope1.match(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, "somescheme"));
    m2 = authscope1.match(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "somerealm", AuthScope.ANY_SCHEME));
    assertTrue(m2 > m1);
    m1 = authscope1.match(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "somerealm", "somescheme"));
    m2 = authscope1.match(new AuthScope(AuthScope.ANY_HOST, 80, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME));
    assertTrue(m2 > m1);
    m1 = authscope1.match(new AuthScope(AuthScope.ANY_HOST, 80, "somerealm", "somescheme"));
    m2 = authscope1.match(new AuthScope("somehost", AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME));
    assertTrue(m2 > m1);
    m1 = authscope1.match(AuthScope.ANY);
    m2 = authscope1.match(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, "somescheme"));
    assertTrue(m2 > m1);
  }

  public void testCredentialsMatching() {
    Credentials creds1 = new UserPassCredentials("name1", "pass1");
    Credentials creds2 = new UserPassCredentials("name2", "pass2");
    Credentials creds3 = new UserPassCredentials("name3", "pass3");
    AuthScope scope1 = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
    AuthScope scope2 = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "somerealm");
    AuthScope scope3 = new AuthScope("somehost", AuthScope.ANY_PORT, AuthScope.ANY_REALM);
    HttpState state = new HttpState();
    state.setCredentials(scope1, creds1);
    state.setCredentials(scope2, creds2);
    state.setCredentials(scope3, creds3);
    Credentials got = state.getCredentials(new AuthScope("someotherhost", 80, "someotherrealm", "basic"));
    Credentials expected = creds1;
    assertEquals(expected, got);
    got = state.getCredentials(new AuthScope("someotherhost", 80, "somerealm", "basic"));
    expected = creds2;
    assertEquals(expected, got);
    got = state.getCredentials(new AuthScope("somehost", 80, "someotherrealm", "basic"));
    expected = creds3;
    assertEquals(expected, got);
  }
}