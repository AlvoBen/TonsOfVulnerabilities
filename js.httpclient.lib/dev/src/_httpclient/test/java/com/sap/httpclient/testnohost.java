package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests that don't require any external host.
 * I.e., that run entirely within this JVM.
 * <p/>
 * (True unit tests, by some definitions.)
 */
public class TestNoHost extends TestCase {

  public TestNoHost(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(TestAll.suite());
    return suite;
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestNoHost.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

}