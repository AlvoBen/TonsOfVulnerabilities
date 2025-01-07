package com.sap.httpclient.http.cookie;

import junit.framework.*;

public class TestCookieAll extends TestCase {

  public TestCookieAll(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(TestCookie.suite());
    suite.addTest(TestCookieCompatibilitySpec.suite());
    suite.addTest(TestCookieRFC2109Spec.suite());
    suite.addTest(TestCookieNetscapeDraft.suite());
    suite.addTest(TestCookieIgnoreSpec.suite());
    suite.addTest(TestCookiePolicy.suite());
    suite.addTest(TestDateParser.suite());
    return suite;
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestCookieAll.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

}