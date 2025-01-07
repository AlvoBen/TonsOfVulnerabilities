package com.sap.httpclient.http.cookie;

import junit.framework.TestCase;

import com.sap.httpclient.http.Header;
import com.sap.httpclient.exception.MalformedCookieException;

/**
 * Test cases for Cookie
 */
public class TestCookieBase extends TestCase {

  public TestCookieBase(String name) {
    super(name);
  }

  public static Cookie[] cookieParse(final CookieSpec parser,
                                     String host,
                                     int port,
                                     String path,
                                     boolean isSecure,
                                     Header setHeader)
          throws MalformedCookieException {
    Cookie[] cookies = parser.parse(host, port, path, isSecure, setHeader);
    if (cookies != null) {
			for (Cookie cooky : cookies) {
				parser.validate(host, port, path, isSecure, cooky);
			}
		}
    return cookies;
  }
}