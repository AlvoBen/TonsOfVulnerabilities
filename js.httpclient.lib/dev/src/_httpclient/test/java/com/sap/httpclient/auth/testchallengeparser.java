package com.sap.httpclient.auth;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Map;

import com.sap.httpclient.exception.MalformedChallengeException;

/**
 * Unit tests for {@link AuthChallengeParser}.
 */
public class TestChallengeParser extends TestCase {

  public TestChallengeParser(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestChallengeParser.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }


  public static Test suite() {
    return new TestSuite(TestChallengeParser.class);
  }


  public void testParsingChallenge() {
    String challenge = "Basic realm=\"realm1\", test, test1 =  stuff, test2 =  \"stuff, stuff\", test3=\"crap";
    String scheme = null;
    Map elements = null;
    try {
      scheme = AuthChallengeParser.extractScheme(challenge);
      elements = AuthChallengeParser.extractParams(challenge);
    } catch (MalformedChallengeException e) {
      fail("Unexpected exception: " + e.toString());
    }
    assertEquals("basic", scheme);
    assertEquals("realm1", elements.get("realm"));
    assertEquals(null, elements.get("test"));
    assertEquals("stuff", elements.get("test1"));
    assertEquals("stuff, stuff", elements.get("test2"));
    assertEquals("\"crap", elements.get("test3"));
  }
}