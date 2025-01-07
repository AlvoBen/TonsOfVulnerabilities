package com.sap.httpclient.http.cookie;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.exception.MalformedCookieException;

/**
 * Test cases for RFC2109 cookie spec
 */
public class TestCookieRFC2109Spec extends TestCookieBase {

  public TestCookieRFC2109Spec(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TestCookieRFC2109Spec.class);
  }

  public void testParseAttributeInvalidAttrib() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookiespec.parseAttribute(null, null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseAttributeInvalidCookie() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookiespec.parseAttribute(new NameValuePair("name", "value"), null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseAttributeNullPath() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      Cookie cookie = new Cookie();
      cookiespec.parseAttribute(new NameValuePair("path", null), cookie);
      fail("MalformedCookieException must have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseAttributeBlankPath() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      Cookie cookie = new Cookie();
      cookiespec.parseAttribute(new NameValuePair("path", "   "), cookie);
      fail("MalformedCookieException must have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseAttributeNullVersion() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      Cookie cookie = new Cookie();
      cookiespec.parseAttribute(new NameValuePair("version", null), cookie);
      fail("MalformedCookieException must have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseAttributeInvalidVersion() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      Cookie cookie = new Cookie();
      cookiespec.parseAttribute(new NameValuePair("version", "nonsense"), cookie);
      fail("MalformedCookieException must have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  public void testParseVersion() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; version=1");
    CookieSpec cookiespec = new RFC2109Spec();
    Cookie[] parsed = cookieParse(cookiespec, "127.0.0.1", 80, "/", false, header);
    assertEquals("Found 1 cookie.", 1, parsed.length);
    assertEquals("Name", "cookie-name", parsed[0].getName());
    assertEquals("Value", "cookie-value", parsed[0].getValue());
    assertEquals("Version", 1, parsed[0].getVersion());
  }

  /**
   * Test domain equals host
	 *
	 * @throws Exception if any exception occures
   */
  public void testParseDomainEqualsHost() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=www.b.com; version=1");
    CookieSpec cookiespec = new RFC2109Spec();
    Cookie[] parsed = cookieParse(cookiespec, "www.b.com", 80, "/", false, header);
    assertNotNull(parsed);
    assertEquals(1, parsed.length);
    assertEquals("www.b.com", parsed[0].getDomain());
  }

  /**
   * Domain does not start with a dot
	 *
	 * @throws Exception if any exception occures
   */
  public void testParseWithIllegalDomain1() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=a.b.com; version=1");
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookieParse(cookiespec, "www.a.b.com", 80, "/", false, header);
      fail("MalformedCookieException should have been thrown");
    } catch (MalformedCookieException e) {  // $JL-EXC$
    }
  }

  /**
   * Domain must have alt least one embedded dot
	 *
	 * @throws Exception if any exception occures
   */
  public void testParseWithIllegalDomain2() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=.com; version=1");
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookieParse(cookiespec, "b.com", 80, "/", false, header);
      fail("MalformedCookieException should have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Domain must have alt least one embedded dot
	 *
	 * @throws Exception if any exception occures
   */
  public void testParseWithIllegalDomain3() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=.com.; version=1");
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookieParse(cookiespec, "b.com", 80, "/", false, header);
      fail("HttpException exception should have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Host minus domain may not contain any dots
	 *
	 * @throws Exception if any exception occures
   */
  public void testParseWithIllegalDomain4() throws Exception {
    Header header = new Header("Set-Cookie", "cookie-name=cookie-value; domain=.c.com; version=1");
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookieParse(cookiespec, "a.b.c.com", 80, "/", false, header);
      fail("MalformedCookieException should have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests if that invalid second domain level cookie gets rejected in the strict mode,
	 * but gets accepted in the browser compatibility mode.
	 *
	 * @throws Exception if any exception occures
   */
  public void testSecondDomainLevelCookie() throws Exception {
    Cookie cookie = new Cookie(".sourceforge.net", "name", null, "/", null, false);
    cookie.setDomainAttributeSpecified(true);
    cookie.setPathAttributeSpecified(true);
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookiespec.validate("sourceforge.net", 80, "/", false, cookie);
      fail("MalformedCookieException should have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  public void testSecondDomainLevelCookieMatch() throws Exception {
    Cookie cookie = new Cookie(".sourceforge.net", "name", null, "/", null, false);
    cookie.setDomainAttributeSpecified(true);
    cookie.setPathAttributeSpecified(true);
    CookieSpec cookiespec = new RFC2109Spec();
    assertFalse(cookiespec.match("sourceforge.net", 80, "/", false, cookie));
  }

  public void testParseWithWrongPath() throws Exception {
    Header header = new Header("Set-Cookie",
            "cookie-name=cookie-value; domain=127.0.0.1; path=/not/just/root");
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookieParse(cookiespec, "127.0.0.1", 80, "/", false, header);
      fail("HttpException exception should have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests if cookie constructor rejects cookie name containing blanks.
   */
  public void testCookieNameWithBlanks() throws Exception {
    Header setcookie = new Header("Set-Cookie", "invalid name=");
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookieParse(cookiespec, "127.0.0.1", 80, "/", false, setcookie);
      fail("MalformedCookieException exception should have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests if cookie constructor rejects cookie name starting with $.
	 *
	 * @throws Exception if any exception occures
   */
  public void testCookieNameStartingWithDollarSign() throws Exception {
    Header setcookie = new Header("Set-Cookie", "$invalid_name=");
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookieParse(cookiespec, "127.0.0.1", 80, "/", false, setcookie);
      fail("MalformedCookieException exception should have been thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests if default cookie validator rejects cookies originating from a host without domain
   * where domain attribute does not match the host of origin
	 *
	 * @throws Exception if any exception occures
   */
  public void testInvalidDomainWithSimpleHostName() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    Header header = new Header("Set-Cookie", "name=\"value\"; version=\"1\"; path=\"/\"; domain=\".mydomain.com\"");
    Cookie[] cookies = cookiespec.parse("host", 80, "/", false, header);
    try {
      cookiespec.validate("host", 80, "/", false, cookies[0]);
      fail("MalformedCookieException must have thrown");
    } catch (MalformedCookieException is_OK) {
      // $JL-EXC$
    }
    header = new Header("Set-Cookie", "name=\"value\"; version=\"1\"; path=\"/\"; domain=\"host1\"");
    cookies = cookiespec.parse("host2", 80, "/", false, header);
    try {
      cookiespec.validate("host2", 80, "/", false, cookies[0]);
      fail("MalformedCookieException must have thrown");
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
    CookieSpec cookiespec = new RFC2109Spec();
    Cookie[] cookies = cookiespec.parse("localhost", 80, "/", false, header);
    assertEquals("number of cookies", 2, cookies.length);
    assertEquals("a", cookies[0].getName());
    assertEquals("b", cookies[0].getValue());
    assertEquals("c", cookies[1].getName());
    assertEquals(null, cookies[1].getValue());
  }

  public void testFormatInvalidCookies() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    try {
      cookiespec.formatCookie(null);
      fail("IllegalArgumentException must have been thrown");
    } catch (IllegalArgumentException is_OK) {
      // $JL-EXC$
    }
  }

  /**
   * Tests RFC 2109 compiant cookie formatting.
	 *
	 * @throws Exception if any exception occures
   */
  public void testRFC2109CookieFormatting() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    Header header = new Header("Set-Cookie", "name=\"value\"; version=\"1\"; path=\"/\"; domain=\".mydomain.com\"");
    Cookie[] cookies = cookiespec.parse("myhost.mydomain.com", 80, "/", false, header);
    cookiespec.validate("myhost.mydomain.com", 80, "/", false, cookies[0]);
    String s1 = cookiespec.formatCookie(cookies[0]);
    assertEquals(s1, "$Version=\"1\"; name=\"value\"; $Path=\"/\"; $Domain=\".mydomain.com\"");
    header = new Header("Set-Cookie", "name=value; path=/; domain=.mydomain.com");
    cookies = cookiespec.parse("myhost.mydomain.com", 80, "/", false, header);
    cookiespec.validate("myhost.mydomain.com", 80, "/", false, cookies[0]);
    String s2 = cookiespec.formatCookie(cookies[0]);
    assertEquals(s2, "$Version=0; name=value; $Path=/; $Domain=.mydomain.com");
  }

  public void testRFC2109CookiesFormatting() throws Exception {
    CookieSpec cookiespec = new RFC2109Spec();
    Header header = new Header("Set-Cookie",
            "name1=value1; path=/; domain=.mydomain.com, " +
            "name2=\"value2\"; version=\"1\"; path=\"/\"; domain=\".mydomain.com\"");

    Cookie[] cookies = cookieParse(cookiespec, "myhost.mydomain.com", 80, "/", false, header);
    assertNotNull(cookies);
    assertEquals(2, cookies.length);
    String s1 = cookiespec.formatCookies(cookies);
    assertEquals(s1,
            "$Version=0; name1=value1; $Path=/; $Domain=.mydomain.com; " +
            "name2=value2; $Path=/; $Domain=.mydomain.com");

    header = new Header("Set-Cookie",
            "name1=value1; version=1; path=/; domain=.mydomain.com, " +
            "name2=\"value2\"; version=\"1\"; path=\"/\"; domain=\".mydomain.com\"");
    cookies = cookieParse(cookiespec, "myhost.mydomain.com", 80, "/", false, header);
    assertNotNull(cookies);
    assertEquals(2, cookies.length);
    String s2 = cookiespec.formatCookies(cookies);
    assertEquals(s2,
            "$Version=\"1\"; name1=\"value1\"; $Path=\"/\"; $Domain=\".mydomain.com\"; " +
            "name2=\"value2\"; $Path=\"/\"; $Domain=\".mydomain.com\"");
  }

  /**
   * Tests if null cookie values are handled correctly.
   */
  public void testNullCookieValueFormatting() {
    Cookie cookie = new Cookie(".whatever.com", "name", null, "/", null, false);
    cookie.setDomainAttributeSpecified(true);
    cookie.setPathAttributeSpecified(true);
    CookieSpec cookiespec = new RFC2109Spec();
    String s = cookiespec.formatCookie(cookie);
    assertEquals("$Version=0; name=; $Path=/; $Domain=.whatever.com", s);
    cookie.setVersion(1);
    s = cookiespec.formatCookie(cookie);
    assertEquals("$Version=\"1\"; name=\"\"; $Path=\"/\"; $Domain=\".whatever.com\"", s);
  }

  public void testCookieNullDomainNullPathFormatting() {
    Cookie cookie = new Cookie(null, "name", null, "/", null, false);
    cookie.setDomainAttributeSpecified(true);
    cookie.setPathAttributeSpecified(true);
    CookieSpec cookiespec = new RFC2109Spec();
    String s = cookiespec.formatCookie(cookie);
    assertEquals("$Version=0; name=; $Path=/", s);
    cookie.setDomainAttributeSpecified(false);
    cookie.setPathAttributeSpecified(false);
    s = cookiespec.formatCookie(cookie);
    assertEquals("$Version=0; name=", s);
  }

}