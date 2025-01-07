package com.sap.httpclient;

import java.util.Enumeration;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A TestDecorator that configures instances of HttpClientTestBase to use
 * a proxy server.
 */
public class ProxyTestDecorator extends TestSetup {

  /**
   * Iterates through all test cases included in the suite and adds
   * copies of them modified to use a proxy server.
   *
   * @param suite the suite
   */
  public static void addTests(TestSuite suite) {
    TestSuite ts2 = new TestSuite();
    addTest(ts2, suite);
    suite.addTest(ts2);
  }

  private static void addTest(TestSuite suite, Test t) {
    if (t instanceof HttpClientTestBase) {
      suite.addTest(new ProxyTestDecorator((HttpClientTestBase) t));
    } else if (t instanceof TestSuite) {
      Enumeration en = ((TestSuite) t).tests();
      while (en.hasMoreElements()) {
        addTest(suite, (Test) en.nextElement());
      }
    }
  }

  public ProxyTestDecorator(HttpClientTestBase test) {
    super(test);
  }

  protected void setUp() throws Exception {
    HttpClientTestBase base = (HttpClientTestBase) fTest;
    base.setUseProxy(true);
  }
}
