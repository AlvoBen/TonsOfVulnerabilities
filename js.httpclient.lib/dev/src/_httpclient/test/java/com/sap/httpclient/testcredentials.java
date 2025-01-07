package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.sap.httpclient.auth.UserPassCredentials;
import com.sap.httpclient.auth.NTCredentials;
import com.sap.httpclient.auth.Credentials;

/**
 * Unit tests for {@link Credentials}.
 */
public class TestCredentials extends TestCase {

  public TestCredentials(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestCredentials.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestCredentials.class);
  }

  public void testCredentialConstructors() {
    try {
      new UserPassCredentials(null, null);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
    try {
      new NTCredentials("user", "password", null, null);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
    try {
      new NTCredentials("user", "password", "host", null);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
    NTCredentials creds = new NTCredentials("user", null, "host", "domain");
    assertNotNull(creds.getUserName());
    assertNull(creds.getPassword());
    assertNotNull(creds.getDomain());
    assertNotNull(creds.getHost());
  }

  /**
   * Verifies that credentials report equal when they should.
   */
  public void testCredentialEquals() {
    Credentials creds1 = new UserPassCredentials("user1", "password1");
    Credentials creds1Again = new UserPassCredentials("user1", "password1");
    Credentials creds2 = new UserPassCredentials("user2", "password2");
    Credentials creds3 = new UserPassCredentials("user3", null);
    Credentials creds3Again = new UserPassCredentials("user3", null);
    assertEquals(creds1, creds1Again);
    assertNotSame(creds1, creds2);
    assertEquals(creds3, creds3Again);
    Credentials ntCreds1 = new NTCredentials("user1", "password1", "host1", "domain1");
    Credentials ntCreds1Again = new NTCredentials("user1", "password1", "host1", "domain1");
    Credentials ntCreds2 = new NTCredentials("user1", "password2", "host1", "domain1");
    Credentials ntCreds3 = new NTCredentials("user1", "password1", "host2", "domain1");
    Credentials ntCreds4 = new NTCredentials("user1", "password1", "host1", "domain2");
    assertEquals(ntCreds1, ntCreds1Again);
    assertNotSame(ntCreds1, creds1);
    assertNotSame(creds1, ntCreds1);
    assertNotSame(ntCreds1, ntCreds2);
    assertNotSame(ntCreds1, ntCreds3);
    assertNotSame(ntCreds1, ntCreds4);
  }
}