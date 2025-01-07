package com.sap.httpclient.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sap.httpclient.exception.AuthChallengeException;
import com.sap.httpclient.exception.AuthenticationException;
import com.sap.httpclient.HttpClientParameters;
import com.sap.httpclient.Parameters;

/**
 * Unit tests for ParsingChallenge.
 */
public class TestChallengeProcessor extends TestCase {

  public TestChallengeProcessor(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestChallengeProcessor.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestChallengeProcessor.class);
  }

  public void testChallengeSelection() throws Exception {
    List<String> authPrefs = new ArrayList<String>(3);
    authPrefs.add(AuthPolicy.NTLM);
    authPrefs.add(AuthPolicy.DIGEST);
    authPrefs.add(AuthPolicy.BASIC);
    HttpClientParameters httpparams = new HttpClientParameters();
    httpparams.setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
    AuthChallengeProcessor processor = new AuthChallengeProcessor(httpparams);
    Map<String, String> map = new HashMap<String, String>();
    map.put("unknown", "unknown realm=\"whatever\"");
    map.put("basic", "basic realm=\"whatever\"");
    AuthScheme authscheme = processor.selectAuthScheme(map);
    assertTrue(authscheme instanceof BasicScheme);
  }

  public void testInvalidChallenge() throws Exception {
    List<String> authPrefs = new ArrayList<String>(3);
    authPrefs.add("unsupported1");
    authPrefs.add("unsupported2");
    Parameters httpparams = new HttpClientParameters();
    httpparams.setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
    AuthChallengeProcessor processor = new AuthChallengeProcessor(httpparams);
    Map<String, String> map = new HashMap<String, String>();
    map.put("unsupported1", "unsupported1 realm=\"whatever\"");
    map.put("unsupported2", "unsupported2 realm=\"whatever\"");
    try {
      processor.selectAuthScheme(map);
      fail("AuthChallengeException should have been thrown");
    } catch (AuthChallengeException is_OK) {
      // $JL-EXC$
    }
  }

  public void testUnsupportedChallenge() throws Exception {
    List<String> authPrefs = new ArrayList<String>(3);
    authPrefs.add(AuthPolicy.NTLM);
    authPrefs.add(AuthPolicy.BASIC);
    authPrefs.add(AuthPolicy.DIGEST);
    Parameters httpparams = new HttpClientParameters();
    httpparams.setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
    AuthChallengeProcessor processor = new AuthChallengeProcessor(httpparams);
    Map<String, String> map = new HashMap<String, String>();
    map.put("unsupported1", "unsupported1 realm=\"whatever\"");
    map.put("unsupported2", "unsupported2 realm=\"whatever\"");
    try {
      processor.selectAuthScheme(map);
      fail("AuthChallengeException should have been thrown");
    } catch (AuthChallengeException is_OK) {
      // $JL-EXC$
    }
  }

  public void testChallengeProcessing() throws Exception {
    Parameters httpparams = new HttpClientParameters();
    AuthChallengeProcessor processor = new AuthChallengeProcessor(httpparams);
    Map<String, String> map = new HashMap<String, String>();
    map.put("basic", "basic realm=\"whatever\", param=\"value\"");
    AuthState authstate = new AuthState();
    AuthScheme authscheme = processor.processChallenge(authstate, map);
    assertTrue(authscheme instanceof BasicScheme);
    assertEquals("whatever", authscheme.getRealm());
    assertEquals(authscheme, authstate.getAuthScheme());
    assertEquals("value", authscheme.getParameter("param"));
  }

  public void testInvalidChallengeProcessing() throws Exception {
    Parameters httpparams = new HttpClientParameters();
    AuthChallengeProcessor processor = new AuthChallengeProcessor(httpparams);
    Map<String, String> map = new HashMap<String, String>();
    map.put("basic", "basic realm=\"whatever\", param=\"value\"");
    AuthState authstate = new AuthState();
    AuthScheme authscheme = processor.processChallenge(authstate, map);
    assertTrue(authscheme instanceof BasicScheme);
    assertEquals("whatever", authscheme.getRealm());
    assertEquals(authscheme, authstate.getAuthScheme());
    assertEquals("value", authscheme.getParameter("param"));
    Map<String, String> map2 = new HashMap<String, String>();
    map2.put("ntlm", "NTLM");
    try {
      // Basic authentication scheme expected
			authscheme = processor.processChallenge(authstate, map2);
      fail("AuthenticationException should have been thrown");
    } catch (AuthenticationException is_OK) {
      // $JL-EXC$
    }
  }
}