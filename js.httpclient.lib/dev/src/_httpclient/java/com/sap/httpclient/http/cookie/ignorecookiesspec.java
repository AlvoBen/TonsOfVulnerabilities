/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.http.cookie;

import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.exception.MalformedCookieException;
import com.sap.httpclient.http.Header;

import java.util.Collection;

/**
 * A cookie spec that does nothing.  Cookies are neither parsed, formatted nor matched.
 * It can be used to effectively disable cookies altogether.
 *
 * @author Nikolai Neichev
 */
public class IgnoreCookiesSpec implements CookieSpec {

  /**
   *
   */
  public IgnoreCookiesSpec() {
    super();
  }

  /**
   * Returns an empty {@link Cookie cookie} array.  All parameters are ignored.
   */
  public Cookie[] parse(String host, int port, String path, boolean secure, String header)
          throws MalformedCookieException {
    return new Cookie[0];
  }

  /**
   * @return <code>null</code>
   */
  public Collection getValidDateFormats() {
    return null;
  }

  /**
   * Does nothing.
   */
  public void setValidDateFormats(Collection datepatterns) {
  }

  /**
   * @return <code>null</code>
   */
  public String formatCookie(Cookie cookie) {
    return null;
  }

  /**
   * @return <code>null</code>
   */
  public Header formatCookieHeader(Cookie cookie) throws IllegalArgumentException {
    return null;
  }

  /**
   * @return <code>null</code>
   */
  public Header formatCookieHeader(Cookie[] cookies) throws IllegalArgumentException {
    return null;
  }

  /**
   * @return <code>null</code>
   */
  public String formatCookies(Cookie[] cookies) throws IllegalArgumentException {
    return null;
  }

  /**
   * @return <code>false</code>
   */
  public boolean match(String host, int port, String path, boolean secure, Cookie cookie) {
    return false;
  }

  /**
   * Returns an empty {@link Cookie cookie} array.  All parameters are ignored.
   */
  public Cookie[] match(String host, int port, String path, boolean secure, Cookie[] cookies) {
    return new Cookie[0];
  }

  /**
   * Returns an empty {@link Cookie cookie} array.  All parameters are ignored.
   */
  public Cookie[] parse(String host, int port, String path, boolean secure, Header header)
          throws MalformedCookieException, IllegalArgumentException {
    return new Cookie[0];
  }

  /**
   * Does nothing.
   */
  public void parseAttribute(NameValuePair attribute, Cookie cookie)
          throws MalformedCookieException, IllegalArgumentException {
  }

  /**
   * Does nothing.
   */
  public void validate(String host, int port, String path, boolean secure, Cookie cookie)
          throws MalformedCookieException, IllegalArgumentException {
  }

  /**
   * @return <code>false</code>
   */
  public boolean domainMatch(final String host, final String domain) {
    return false;
  }

  /**
   * @return <code>false</code>
   */
  public boolean pathMatch(final String path, final String topmostPath) {
    return false;
  }

}