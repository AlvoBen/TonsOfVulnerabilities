package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.sap.httpclient.exception.ProtocolException;
import com.sap.httpclient.http.HttpVersion;

/**
 * Test cases for HTTP version class
 */
public class TestHttpVersion extends TestCase {

  public TestHttpVersion(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TestHttpVersion.class);
  }

  public void testHttpVersionInvalidConstructorInput() throws Exception {
    try {
      new HttpVersion(-1, -1);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
    try {
      new HttpVersion(0, -1);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testHttpVersionParsing() throws Exception {
    String s = "HTTP/1.1";
    HttpVersion version = HttpVersion.parse(s);
    assertEquals("HTTP major version number", 1, version.getMajor());
    assertEquals("HTTP minor version number", 1, version.getMinor());
    assertEquals("HTTP version number", s, version.toString());
    s = "HTTP/123.4567";
    version = HttpVersion.parse(s);
    assertEquals("HTTP major version number", 123, version.getMajor());
    assertEquals("HTTP minor version number", 4567, version.getMinor());
    assertEquals("HTTP version number", s, version.toString());
  }

  public void testInvalidHttpVersionParsing() throws Exception {
    try {
      HttpVersion.parse(null);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
    try {
      HttpVersion.parse("crap");
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    }
    try {
      HttpVersion.parse("HTTP/crap");
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    }
    try {
      HttpVersion.parse("HTTP/1");
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    }
    try {
      HttpVersion.parse("HTTP/1234   ");
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    }
    try {
      HttpVersion.parse("HTTP/1.");
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    }
    try {
      HttpVersion.parse("HTTP/1.1 crap");
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    }
    try {
      HttpVersion.parse("HTTP/whatever.whatever whatever");
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    }
    try {
      HttpVersion.parse("HTTP/1.whatever whatever");
      fail("ProtocolException should have been thrown");
    } catch (ProtocolException is_OK) {
      // $JL-EXC$
    }
  }

  public void testHttpVersionEquality() throws Exception {
    HttpVersion ver1 = new HttpVersion(1, 1);
    HttpVersion ver2 = new HttpVersion(1, 1);
    assertEquals(ver1.hashCode(), ver2.hashCode());
    assertTrue(ver1.isEqualTo(ver1));
    assertTrue(ver1.isEqualTo(ver2));
    assertTrue(ver1.equals(ver1));
    assertTrue(ver1.equals(ver2));
    assertFalse(ver1.equals(new Float(1.1)));
    try {
      ver1.isEqualTo(null);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
    assertTrue((new HttpVersion(0, 9)).isEqualTo(HttpVersion.HTTP_0_9));
    assertTrue((new HttpVersion(1, 0)).isEqualTo(HttpVersion.HTTP_1_0));
    assertTrue((new HttpVersion(1, 1)).isEqualTo(HttpVersion.HTTP_1_1));
    assertFalse((new HttpVersion(1, 1)).isEqualTo(HttpVersion.HTTP_1_0));
  }

  public void testHttpVersionComparison() {
    assertTrue(HttpVersion.HTTP_0_9.lessEquals(HttpVersion.HTTP_1_1));
    assertTrue(HttpVersion.HTTP_0_9.greaterEquals(HttpVersion.HTTP_0_9));
    assertFalse(HttpVersion.HTTP_0_9.greaterEquals(HttpVersion.HTTP_1_0));
    assertTrue(HttpVersion.HTTP_1_0.compareTo((Object) HttpVersion.HTTP_1_1) < 0);
    assertTrue(HttpVersion.HTTP_1_0.compareTo((Object) HttpVersion.HTTP_0_9) > 0);
    assertTrue(HttpVersion.HTTP_1_0.compareTo((Object) HttpVersion.HTTP_1_0) == 0);
  }
}

