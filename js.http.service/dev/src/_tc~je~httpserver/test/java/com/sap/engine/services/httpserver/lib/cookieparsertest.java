package com.sap.engine.services.httpserver.lib;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.protocol.HeaderNames;
import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.server.ServiceContext;

/**
 * Tests RFC-2109 parser implementation
 * <pre>
 * 4.1  Syntax:  General

   The two state management headers, Set-Cookie and Cookie, have common
   syntactic properties involving attribute-value pairs.  The following
   grammar uses the notation, and tokens DIGIT (decimal digits) and
   token (informally, a sequence of non-special, non-white space
   characters) from the HTTP/1.1 specification [RFC 2068] to describe
   their syntax.

   av-pairs        =       av-pair *(";" av-pair)
   av-pair         =       attr ["=" value]        ; optional value
   attr            =       token
   value           =       word
   word            =       token | quoted-string

   Attributes (names) (attr) are case-insensitive.  White space is
   permitted between tokens.  Note that while the above syntax
   description shows value as optional, most attrs require them.

   NOTE: The syntax above allows whitespace between the attribute and
   the = sign.
 * </pre>
 * and
 * <pre>
 * 4.3.4  Sending Cookies to the Origin Server

   When it sends a request to an origin server, the user agent sends a
   Cookie request header to the origin server if it has cookies that are
   applicable to the request, based on
   * the request-host;
   * the request-URI;
   * the cookie's age.

   The syntax for the header is:

   cookie          =       "Cookie:" cookie-version
                           1*((";" | ",") cookie-value)
   cookie-value    =       NAME "=" VALUE [";" path] [";" domain]
   cookie-version  =       "$Version" "=" value
   NAME            =       attr
   VALUE           =       value
   path            =       "$Path" "=" value
   domain          =       "$Domain" "=" value
 * </pre>
 * @author I030733
 *
 */
public class CookieParserTest {
  @Before
  public void setUp() {
    // ServiceContext should be initialized because of the dependency 
    // Cookie class has on it, precisely HttpCoocie class reads HTTP Provider
    // property SystemCookieDataProtection from initialized ServiceContext.
    // Such a dependencies should be avoided
    ServiceContext sc = new ServiceContext(null);
    HttpProperties props = createMock(HttpProperties.class);
    expect(props.getSystemCookiesDataProtection()).andStubReturn(false);
    expect(props.getSystemCookiesHTTPSProtection()).andStubReturn(false);
    replay(props);
    sc.setHttpProperties(props);
    ServiceContext.setServiceContext(sc);
  }
  
  @Test
  public final void testParseCookie() {
    // Tests base cookies support
    Cookies cookies = new Cookies();
    MimeHeaders headers = new MimeHeaders();
    headers.addHeader(HeaderNames.request_header_cookie_,
      "name0=value0; name1=value1".getBytes());
    CookieParser.parseCookies(headers, false, cookies);
    assertTrue(cookies.getCookiesSize() == 2);
    assertEquals(cookies.getCookie("name0").getValue(), "value0");
    HttpCookie cookie1 = cookies.getCookie("name1");
    assertEquals(cookie1.getValue(), "value1");
    assertEquals(cookie1.getMaxAge(), -1);
    assertEquals(cookie1.getVersion(), 0);
    assertNull(cookie1.getDomain());
    assertNull(cookie1.getPath());
    assertNull(cookie1.getComment());
    assertEquals(cookie1.getSecure(), false);
    assertEquals(cookie1.isHttpOnly(), false);
    assertNull(cookies.getSessionCookie().getName());
    assertNull(cookies.getSessionCookie().getValue());
    assertTrue(cookies.getApplicationCookiesSize() == 0);

    // Tests attributes support
    cookies = new Cookies();
    headers = new MimeHeaders();
    headers.addHeader(HeaderNames.request_header_cookie_,
      "name0=value0; name1=value1; $Path=path; $Domain=domain".getBytes());
    CookieParser.parseCookies(headers, false, cookies);
    assertTrue(cookies.getCookiesSize() == 2);
    assertEquals(cookies.getCookie("name0").getValue(), "value0");
    cookie1 = cookies.getCookie("name1");
    assertEquals(cookie1.getValue(), "value1");
    assertEquals(cookie1.getMaxAge(), -1);
    assertEquals(cookie1.getVersion(), 0);
    assertEquals(cookie1.getDomain(), "domain");
    assertEquals(cookie1.getPath(), "path");
    assertNull(cookie1.getComment());
    assertEquals(cookie1.getSecure(), false);
    assertEquals(cookie1.isHttpOnly(), false);
    assertNull(cookies.getSessionCookie().getName());
    assertNull(cookies.getSessionCookie().getValue());
    assertTrue(cookies.getApplicationCookiesSize() == 0);
    
    // Tests $Version attribute support
    cookies = new Cookies();
    headers = new MimeHeaders();
    headers.addHeader(HeaderNames.request_header_cookie_,
      "$Version=1, name0=value0, name1=value1; $Path=path; $Domain=domain"
        .getBytes());
    CookieParser.parseCookies(headers, false, cookies);
    assertTrue(cookies.getCookiesSize() == 2);
    assertEquals(cookies.getCookie("name0").getValue(), "value0");
    cookie1 = cookies.getCookie("name1");
    assertEquals(cookie1.getValue(), "value1");
    assertEquals(cookie1.getMaxAge(), -1);
    assertEquals(cookie1.getVersion(), 1);
    assertEquals(cookie1.getDomain(), "domain");
    assertEquals(cookie1.getPath(), "path");
    assertNull(cookie1.getComment());
    assertEquals(cookie1.getSecure(), false);
    assertEquals(cookie1.isHttpOnly(), false);
    assertNull(cookies.getSessionCookie().getName());
    assertNull(cookies.getSessionCookie().getValue());
    assertTrue(cookies.getApplicationCookiesSize() == 0);
    
    // Tests that cookies with names equal to reserved words like Version, 
    // Domain, Path, Comment, etc. are ignored
    cookies = new Cookies();
    headers = new MimeHeaders();
    headers.addHeader(HeaderNames.request_header_cookie_,
      "name0=value0, Version=1, name1=value1; Path=path; Domain=domain; Comment=comment; Discard=discard; Expires=expires; Max-Age=age; Secure=insecure"
        .getBytes());
    CookieParser.parseCookies(headers, false, cookies);
    assertTrue(cookies.getCookiesSize() == 2);
    assertEquals(cookies.getCookie("name0").getValue(), "value0");
    cookie1 = cookies.getCookie("name1");
    assertEquals(cookie1.getValue(), "value1");
    assertEquals(cookie1.getMaxAge(), -1);
    assertEquals(cookie1.getVersion(), 0);
    assertNull(cookie1.getDomain());
    assertNull(cookie1.getPath());
    assertNull(cookie1.getComment());
    assertEquals(cookie1.getSecure(), false);
    assertEquals(cookie1.isHttpOnly(), false);
    assertNull(cookies.getSessionCookie().getName());
    assertNull(cookies.getSessionCookie().getValue());
    assertTrue(cookies.getApplicationCookiesSize() == 0);
    
    // Tests that only the first JSESSIONID cookie is used and all the 
    // application cookies are collected
    cookies = new Cookies();
    headers = new MimeHeaders();
    String header = "name0=value0, JSESSIONID=01234, name1=value1, "
      + CookieParser.app_cookie_prefix + "01234=01234, JSESSIONID=56789, "
      + CookieParser.app_cookie_prefix + "56789=56789";
    headers.addHeader(HeaderNames.request_header_cookie_, header.getBytes());
    CookieParser.parseCookies(headers, false, cookies);
    assertTrue(cookies.getCookiesSize() == 6);
    assertEquals(cookies.getCookie("name0").getValue(), "value0");
    cookie1 = cookies.getCookie("name1");
    assertEquals(cookie1.getValue(), "value1");
    assertEquals(cookie1.getMaxAge(), -1);
    assertEquals(cookie1.getVersion(), 0);
    assertNull(cookie1.getDomain());
    assertNull(cookie1.getPath());
    assertNull(cookie1.getComment());
    assertEquals(cookie1.getSecure(), false);
    assertEquals(cookie1.isHttpOnly(), false);
    assertEquals(cookies.getSessionCookie().getName(), "JSESSIONID");
    assertEquals(cookies.getSessionCookie().getValue(), "01234");
    assertTrue(cookies.getApplicationCookiesSize() == 2);
    
    // Tests that JSESSIONID cookie is rejected in case of URL session tracking
    cookies = new Cookies();
    headers = new MimeHeaders();
    header = "name0=value0, JSESSIONID=01234, name1=value1, "
      + CookieParser.app_cookie_prefix + "01234=01234, JSESSIONID=56789, "
      + CookieParser.app_cookie_prefix + "56789=56789";
    headers.addHeader(HeaderNames.request_header_cookie_, header.getBytes());
    CookieParser.parseCookies(headers, true, cookies);
    assertTrue(cookies.getCookiesSize() == 6);
    assertEquals(cookies.getCookie("name0").getValue(), "value0");
    cookie1 = cookies.getCookie("name1");
    assertEquals(cookie1.getValue(), "value1");
    assertEquals(cookie1.getMaxAge(), -1);
    assertEquals(cookie1.getVersion(), 0);
    assertNull(cookie1.getDomain());
    assertNull(cookie1.getPath());
    assertNull(cookie1.getComment());
    assertEquals(cookie1.getSecure(), false);
    assertEquals(cookie1.isHttpOnly(), false);
    assertNull(cookies.getSessionCookie().getName());
    assertNull(cookies.getSessionCookie().getValue());
    assertTrue(cookies.getApplicationCookiesSize() == 2);
    
    // Tests that attribute names are case insesitive
    
    // Tests that witespaces are allowed everywhere
  }
  
  @Test
  public void testParseCookiesFromURL() {
    // Tests session cookie in URL support
    Cookies cookies = new Cookies();
    MessageBytes url = new MessageBytes(
      "http://localhost:50000/test;jsessionid=01234?ala-bala=portokala"
        .getBytes());
    CookieParser.parseCookiesFromURL(url, cookies);
    assertTrue(cookies.getCookiesSize() == 0);
    assertEquals(cookies.getSessionCookie().getName(), "jsessionid");
    assertEquals(cookies.getSessionCookie().getValue(), "01234");
    assertTrue(cookies.getApplicationCookiesSize() == 0);
    
    // Tests load balancing cookie in URL support
    cookies = new Cookies();
    String paramName = CookieParser.app_cookie_prefix + "*";
    String _url = "http://localhost:50000/test;jsessionid=01234;" 
      + paramName + "=56789";
    url = new MessageBytes(_url.getBytes());
    CookieParser.parseCookiesFromURL(url, cookies);
    assertTrue(cookies.getCookiesSize() == 0);
    assertEquals(cookies.getSessionCookie().getName(), "jsessionid");
    assertEquals(cookies.getSessionCookie().getValue(), "01234");
    assertTrue(cookies.getApplicationCookiesSize() == 1);
    HttpCookie cookie = cookies.getApplicationCookie(paramName);
    assertNotNull(cookie);
    assertEquals(cookie.getValue(), "56789");
    
    // Tests load balancing cookie in URL with query string support
  }
}
