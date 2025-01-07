package com.sap.httpclient.http.cookie;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.httpclient.http.Header;
import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.exception.MalformedCookieException;


/**
 * Test cases for Netscape cookie draft
 */
public class TestCookieNetscapeDraft extends TestCookieBase {

  public TestCookieNetscapeDraft(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TestCookieNetscapeDraft.class);
  }

  public void testParseAttributeInvalidAttrib() throws Exception {
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookiespec.parseAttribute(null, null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseAttributeInvalidCookie() throws Exception {
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookiespec.parseAttribute(new NameValuePair("name", "value"), null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseAttributeInvalidCookieExpires() throws Exception {
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Cookie cookie = new Cookie();
    try {
      cookiespec.parseAttribute(new NameValuePair("expires", null), cookie);
      fail("MalformedCookieException must have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseWithNullHost() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=127.0.0.1; path=/; secure");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookieParse(cookiespec, null, 80, "/", false, header);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseWithBlankHost() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=127.0.0.1; path=/; secure");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookieParse(cookiespec, "  ", 80, "/", false, header);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseWithNullPath() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=127.0.0.1; path=/; secure");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookieParse(cookiespec, "127.0.0.1", 80, null, false, header);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseWithBlankPath() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=127.0.0.1; path=/; secure");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Cookie[] parsed = cookieParse(cookiespec, "127.0.0.1", 80, "  ", false, header);
    assertNotNull(parsed);
    assertEquals(1, parsed.length);
    assertEquals("/", parsed[0].getPath());
  }

  public void testParseWithNegativePort() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=127.0.0.1; path=/; secure");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookieParse(cookiespec, "127.0.0.1", -80, null, false, header);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseWithInvalidHeader1() throws Exception {
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookiespec.parse("127.0.0.1", 80, "/foo", false, (String) null);
      fail("IllegalArgumentException should have been thrown.");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseAbsPath() throws Exception {
    Header header = new Header("Set-Cookie", "name1=value1;Path=/path/");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Cookie[] parsed = cookieParse(cookiespec, "host", 80, "/path/", true, header);
    assertEquals("Found 1 cookies.", 1, parsed.length);
    assertEquals("Name", "name1", parsed[0].getName());
    assertEquals("Value", "value1", parsed[0].getValue());
    assertEquals("Domain", "host", parsed[0].getDomain());
    assertEquals("Path", "/path/", parsed[0].getPath());
  }

  public void testParseAbsPath2() throws Exception {
    Header header = new Header("Set-Cookie", "name1=value1;Path=/");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Cookie[] parsed = cookieParse(cookiespec, "host", 80, "/", true, header);
    assertEquals("Found 1 cookies.", 1, parsed.length);
    assertEquals("Name", "name1", parsed[0].getName());
    assertEquals("Value", "value1", parsed[0].getValue());
    assertEquals("Domain", "host", parsed[0].getDomain());
    assertEquals("Path", "/", parsed[0].getPath());
  }

  public void testParseRelativePath() throws Exception {
    Header header = new Header("Set-Cookie", "name1=value1;Path=whatever");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Cookie[] parsed = cookieParse(cookiespec, "host", 80, "whatever", true, header);
    assertEquals("Found 1 cookies.", 1, parsed.length);
    assertEquals("Name", "name1", parsed[0].getName());
    assertEquals("Value", "value1", parsed[0].getValue());
    assertEquals("Domain", "host", parsed[0].getDomain());
    assertEquals("Path", "whatever", parsed[0].getPath());
  }

  public void testParseWithIllegalNetscapeDomain1() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=.com");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookieParse(cookiespec, "a.com", 80, "/", false, header);
      fail("HttpException exception should have been thrown");
    } catch (HttpException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseWithWrongNetscapeDomain2() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=.y.z");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    try {
      cookieParse(cookiespec, "x.y.z", 80, "/", false, header);
      fail("HttpException exception should have been thrown");
    } catch (HttpException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests Netscape specific cookie formatting.
	 *
	 * @throws Exception if any exception occures
   */
  public void testNetscapeCookieFormatting() throws Exception {
    Header header = new Header("Set-Cookie", "name=value; path=/; domain=.mydomain.com");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Cookie[] cookies = cookiespec.parse("myhost.mydomain.com", 80, "/", false, header);
    cookiespec.validate("myhost.mydomain.com", 80, "/", false, cookies[0]);
    String s = cookiespec.formatCookie(cookies[0]);
    assertEquals("name=value", s);
  }

  /**
   * Tests Netscape specific expire attribute parsing.
	 *
	 * @throws Exception if any exception occures
   */
  public void testNetscapeCookieExpireAttribute() throws Exception {
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Header header = new Header("Set-Cookie",
            "name=value; path=/; domain=.mydomain.com; expires=Thu, 01-Jan-2070 00:00:10 GMT; comment=no_comment");
    Cookie[] cookies = cookiespec.parse("myhost.mydomain.com", 80, "/", false, header);
    cookiespec.validate("myhost.mydomain.com", 80, "/", false, cookies[0]);
    header = new Header("Set-Cookie",
            "name=value; path=/; domain=.mydomain.com; expires=Thu 01-Jan-2070 00:00:10 GMT; comment=no_comment");
    try {
      cookies = cookiespec.parse("myhost.mydomain.com", 80, "/", false, header);
      cookiespec.validate("myhost.mydomain.com", 80, "/", false, cookies[0]);
      fail("MalformedCookieException must have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests Netscape specific expire attribute without a time zone.
	 *
	 * @throws Exception if any exception occures
   */
  public void testNetscapeCookieExpireAttributeNoTimeZone() throws Exception {
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Header header = new Header("Set-Cookie", "name=value; expires=Thu, 01-Jan-2006 00:00:00 ");
    try {
      cookiespec.parse("myhost.mydomain.com", 80, "/", false, header);
      fail("MalformedCookieException should have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests if cookie values with embedded comma are handled correctly.
	 *
	 * @throws Exception if any exception occures
   */
  public void testCookieWithComma() throws Exception {
    Header header = new Header("Set-Cookie", "a=b,c");
    CookieSpec cookiespec = new NetscapeDraftSpec();
    Cookie[] cookies = cookiespec.parse("localhost", 80, "/", false, header);
    assertEquals("number of cookies", 1, cookies.length);
    assertEquals("a", cookies[0].getName());
    assertEquals("b,c", cookies[0].getValue());
  }

}