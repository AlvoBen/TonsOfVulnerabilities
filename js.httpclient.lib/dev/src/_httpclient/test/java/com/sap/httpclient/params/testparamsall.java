package com.sap.httpclient.params;

import junit.framework.*;

public class TestParamsAll extends TestCase {

  public TestParamsAll(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(TestHttpParams.suite());
    suite.addTest(TestSSLTunnelParams.suite());
    return suite;
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestParamsAll.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

}
