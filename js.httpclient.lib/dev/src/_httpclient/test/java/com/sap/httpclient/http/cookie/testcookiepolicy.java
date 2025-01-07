package com.sap.httpclient.http.cookie;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Test cases for Cookie Policy
 */
public class TestCookiePolicy extends TestCookieBase {

  public TestCookiePolicy(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TestCookiePolicy.class);
  }

  public void testRegisterNullPolicyId() {
    try {
      CookiePolicy.registerCookieSpec(null, null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testRegisterNullPolicy() {
    try {
      CookiePolicy.registerCookieSpec("whatever", null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testUnregisterNullPolicy() {
    try {
      CookiePolicy.unregisterCookieSpec(null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testGetPolicyNullId() {
    try {
      CookiePolicy.getCookieSpec(null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testRegisterUnregister() {
    CookiePolicy.registerCookieSpec("whatever", CookieSpecBase.class);
    CookiePolicy.unregisterCookieSpec("whatever");
    try {
      CookiePolicy.getCookieSpec("whatever");
      fail("IllegalStateException must have been thrown");
    } catch (IllegalStateException is_OK) {
      // $JL-EXC$
    }
  }

  public void testGetDefaultPolicy() {
    assertNotNull(CookiePolicy.getDefaultSpec());
  }
}