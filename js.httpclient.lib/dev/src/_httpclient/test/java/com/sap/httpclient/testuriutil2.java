package com.sap.httpclient;

import com.sap.httpclient.uri.URIUtil;
import junit.framework.*;

/**
 * Tests the utils.URIUtil class.
 */
public class TestURIUtil2 extends TestCase {

  public TestURIUtil2(String s) {
    super(s);
  }

  public static Test suite() {
    return new TestSuite(TestURIUtil.class);
  }

  public void testEncodeWithinQuery() {
    String unescaped1 = "abc123+ %_?=&#.";
    try {
      String stringRet = URIUtil.encodeWithinQuery(unescaped1);
      assertEquals("abc123%2B%20%25_%3F%3D%26%23.", stringRet);
      stringRet = URIUtil.decode(stringRet);
      assertEquals(unescaped1, stringRet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}