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
import com.sap.httpclient.exception.DateParseException;
import com.sap.httpclient.exception.MalformedCookieException;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HeaderElement;
import com.sap.httpclient.utils.DateParser;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Cookie management functions shared by all specification.
 *
 * @author Nikolai Neichev
 */
public class CookieSpecBase implements CookieSpec {

  /**
   * Log object
   */
  protected static final Location LOG = Location.getLocation(CookieSpec.class);

  /**
   * Valid date patterns
   */
  private Collection<String> datepatterns = null;

  /**
   * Default constructor
   */
  public CookieSpecBase() {
    super();
  }

  /**
   * Parses the Set-Cookie value into an array of <tt>Cookie</tt>s.
   *
   * @param host   the host from which the <tt>Set-Cookie</tt> value was received
   * @param port   the port from which the <tt>Set-Cookie</tt> value was received
   * @param path   the path from which the <tt>Set-Cookie</tt> value was received
   * @param secure <tt>true</tt> when the <tt>Set-Cookie</tt> value was received over secure conection
   * @param header the <tt>Set-Cookie</tt> received from the server
   * @return an array of <tt>Cookie</tt>s parsed from the Set-Cookie value
   * @throws MalformedCookieException if an exception occurs during parsing
   */
  public Cookie[] parse(String host, int port, String path, boolean secure, final String header)
          throws MalformedCookieException {

    if (host == null) {
      throw new IllegalArgumentException("Host of origin is null");
    }
    if (host.trim().equals("")) {
      throw new IllegalArgumentException("Host of origin may not be blank");
    }
    if (port < 0) {
      throw new IllegalArgumentException("Invalid port: " + port);
    }
    if (path == null) {
      throw new IllegalArgumentException("Path of origin is null.");
    }
    if (header == null) {
      throw new IllegalArgumentException("Header is null.");
    }
    if (path.trim().equals("")) {
      path = PATH_DELIM;
    }
    host = host.toLowerCase();
    String defaultPath = path;
    int lastSlashIndex = defaultPath.lastIndexOf(PATH_DELIM);
    if (lastSlashIndex >= 0) {
      if (lastSlashIndex == 0) {
        lastSlashIndex = 1; //skip the first slash
      }
      defaultPath = defaultPath.substring(0, lastSlashIndex);
    }
    HeaderElement[] headerElements;
    boolean isNetscapeCookie = false;
    int i1 = header.toLowerCase().indexOf("expires=");
    if (i1 != -1) {
      i1 += "expires=".length();
      int i2 = header.indexOf(";", i1);
      if (i2 == -1) {
        i2 = header.length();
      }
      try {
        DateParser.parse(header.substring(i1, i2), this.datepatterns);
        isNetscapeCookie = true;
      } catch (DateParseException e) { // $JL-EXC$
        // not a valid expiry date, and still not a Netscape Cookie
      }
    }
    if (isNetscapeCookie) {
      headerElements = new HeaderElement[]{new HeaderElement(header.toCharArray())};
    } else {
      headerElements = HeaderElement.parseElements(header.toCharArray());
    }
    Cookie[] cookies = new Cookie[headerElements.length];
    for (int i = 0; i < headerElements.length; i++) {
      HeaderElement headerelement = headerElements[i];
      Cookie cookie;
      try {
        cookie = new Cookie(host, headerelement.getName(), headerelement.getValue(), defaultPath, null, false);
      } catch (IllegalArgumentException e) {
        throw new MalformedCookieException(e.getMessage());
      }
      NameValuePair[] parameters = headerelement.getParameters();
      if (parameters != null) {
        for (NameValuePair param : parameters) {
          parseAttribute(param, cookie);
        }
      }
      cookies[i] = cookie;
    }
    return cookies;
  }

  /**
   * Parse the <tt>"Set-Cookie"</tt> {@link Header} into an array of {@link Cookie}s.
   *
   * @param host   the host from which the <tt>Set-Cookie</tt> header was received
   * @param port   the port from which the <tt>Set-Cookie</tt> header was received
   * @param path   the path from which the <tt>Set-Cookie</tt> header was received
   * @param secure <tt>true</tt> when the <tt>Set-Cookie</tt> header was received over secure conection
   * @param header the <tt>Set-Cookie</tt> received from the server
   * @return an array of <tt>Cookie</tt>s parsed from the <tt>"Set-Cookie"</tt> header
   * @throws MalformedCookieException if an exception occurs during parsing
   */
  public Cookie[] parse(String host, int port, String path, boolean secure, final Header header)
          throws MalformedCookieException {

    if (header == null) {
      throw new IllegalArgumentException("Header is null.");
    }
    return parse(host, port, path, secure, header.getValue());
  }

  /**
   * Parse the cookie attribute and update the corresponsing {@link Cookie} properties.
   *
   * @param attribute {@link HeaderElement} cookie attribute from the <tt>Set- Cookie</tt>
   * @param cookie    {@link Cookie} to be updated
   * @throws MalformedCookieException if an exception occurs during parsing
   */

  public void parseAttribute(final NameValuePair attribute, final Cookie cookie)
          throws MalformedCookieException {

    if (attribute == null) {
      throw new IllegalArgumentException("Attribute is null.");
    }
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie is null.");
    }
    final String paramName = attribute.getName().toLowerCase();
    String paramValue = attribute.getValue();
    if (paramName.equals("path")) {
      if ((paramValue == null) || (paramValue.trim().equals(""))) {
        paramValue = "/";
      }
      cookie.setPath(paramValue);
      cookie.setPathAttributeSpecified(true);
    } else if (paramName.equals("domain")) {
      if (paramValue == null) {
        throw new MalformedCookieException("Missing value for domain attribute");
      }
      if (paramValue.trim().equals("")) {
        throw new MalformedCookieException("Blank value for domain attribute");
      }
      cookie.setDomain(paramValue);
      cookie.setDomainAttributeSpecified(true);
    } else if (paramName.equals("max-age")) {
      if (paramValue == null) {
        throw new MalformedCookieException("Missing value for max-age attribute");
      }
      int age;
      try {
        age = Integer.parseInt(paramValue);
      } catch (NumberFormatException e) {
        throw new MalformedCookieException("Invalid max-age attribute: " + e.getMessage());
      }
      cookie.setExpiryDate(new Date(System.currentTimeMillis() + age * 1000L));
    } else if (paramName.equals("secure")) {
      cookie.setSecure(true);
    } else if (paramName.equals("comment")) {
      cookie.setComment(paramValue);
    } else if (paramName.equals("expires")) {
      if (paramValue == null) {
        throw new MalformedCookieException("Missing value for expires attribute");
      }
      try {
        cookie.setExpiryDate(DateParser.parse(paramValue, this.datepatterns));
      } catch (DateParseException dpe) {
        LOG.traceThrowableT(Severity.DEBUG, "Error parsing cookie date", dpe);
        throw new MalformedCookieException("Unable to parse expiration date parameter: " + paramValue);
      }
    } else {
      if (LOG.beDebug()) {
        LOG.debugT("Unrecognized cookie attribute: " + attribute.toString());
      }
    }
  }

  public Collection getValidDateFormats() {
    return this.datepatterns;
  }

  public void setValidDateFormats(final Collection<String> datepatterns) {
    this.datepatterns = datepatterns;
  }

  /**
   * Performs most common {@link Cookie} validation
   *
   * @param host   the host from which the {@link Cookie} was received
   * @param port   the port from which the {@link Cookie} was received
   * @param path   the path from which the {@link Cookie} was received
   * @param secure <tt>true</tt> when the {@link Cookie} was received using a secure connection
   * @param cookie The cookie to validate.
   * @throws MalformedCookieException if an exception occurs during validation
   */

  public void validate(String host, int port, String path, boolean secure, final Cookie cookie)
          throws MalformedCookieException {

    if (host == null) {
      throw new IllegalArgumentException("Host of origin is null");
    }
    if (host.trim().equals("")) {
      throw new IllegalArgumentException("Host of origin may not be blank");
    }
    if (port < 0) {
      throw new IllegalArgumentException("Invalid port: " + port);
    }
    if (path == null) {
      throw new IllegalArgumentException("Path of origin is null.");
    }
    if (path.trim().equals("")) {
      path = PATH_DELIM;
    }
    host = host.toLowerCase();
    // check version
    if (cookie.getVersion() < 0) {
      throw new MalformedCookieException("Illegal version number " + cookie.getValue());
    }
    if (host.indexOf(".") >= 0) {
      if (!host.endsWith(cookie.getDomain())) {
        String s = cookie.getDomain();
        if (s.startsWith(".")) {
          s = s.substring(1, s.length());
        }
        if (!host.equals(s)) {
          throw new MalformedCookieException("Illegal domain attribute \"" + cookie.getDomain()
                  + "\". Domain of origin: \"" + host + "\"");
        }
      }
    } else {
      if (!host.equals(cookie.getDomain())) {
        throw new MalformedCookieException("Illegal domain attribute \"" + cookie.getDomain()
                + "\". Domain of origin: \"" + host + "\"");
      }
    }
    if (!path.startsWith(cookie.getPath())) {
      throw new MalformedCookieException("Illegal path attribute \"" + cookie.getPath()
              + "\". Path of origin: \"" + path + "\"");
    }
  }

  /**
   * Return <tt>true</tt> if the cookie should be submitted with a request
   * with specified attributes, <tt>false</tt> otherwise.
   *
   * @param host   the host to which the request is being submitted
   * @param port   the port to which the request is being submitted (ignored)
   * @param path   the path to which the request is being submitted
   * @param secure <tt>true</tt> if the request is using a secure connection
   * @param cookie {@link Cookie} to be matched
   * @return true if the cookie matches the criterium
   */

  public boolean match(String host, int port, String path, boolean secure, final Cookie cookie) {
    if (host == null) {
      throw new IllegalArgumentException("Host of origin is null");
    }
    if (host.trim().equals("")) {
      throw new IllegalArgumentException("Host of origin may not be blank");
    }
    if (port < 0) {
      throw new IllegalArgumentException("Invalid port: " + port);
    }
    if (path == null) {
      throw new IllegalArgumentException("Path of origin is null.");
    }
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie is null");
    }
    if (path.trim().equals("")) {
      path = PATH_DELIM;
    }
    host = host.toLowerCase();
    if (cookie.getDomain() == null) {
      LOG.warningT("Invalid cookie state: domain not specified");
      return false;
    }
    if (cookie.getPath() == null) {
      LOG.warningT("Invalid cookie state: path not specified");
      return false;
    }
    return  (cookie.getExpiryDate() == null || cookie.getExpiryDate().after(new Date())) // not expired
            && (domainMatch(host, cookie.getDomain())) // the domain pattern matches
            && (pathMatch(path, cookie.getPath())) // the path is null or matching
            && (cookie.getSecure() ? secure : true); // if secure, only if the request is actually secure
  }

  /**
   * Performs domain-match as implemented in common browsers.
   *
   * @param host   The target host.
   * @param domain The cookie domain attribute.
   * @return true if the specified host matches the specified domain.
   */
  public boolean domainMatch(final String host, String domain) {
    if (host.equals(domain)) {
      return true;
    }
    if (!domain.startsWith(".")) {
      domain = "." + domain;
    }
    return host.endsWith(domain) || host.equals(domain.substring(1));
  }

  /**
   * Performs path-match as implemented in common browsers.
   *
   * @param path The target path.
   * @param topmostPath The cookie path attribute.
   * @return true if the paths match
   */
  public boolean pathMatch(final String path, final String topmostPath) {
    boolean match = path.startsWith(topmostPath);
    if (match && path.length() != topmostPath.length()) {
      if (!topmostPath.endsWith(PATH_DELIM)) {
        match = (path.charAt(topmostPath.length()) == PATH_DELIM_CHAR);
      }
    }
    return match;
  }

  /**
   * Return an array of {@link Cookie}s that should be submitted with a
   * request with specified attributes, <tt>false</tt> otherwise.
   *
   * @param host    the host to which the request is being submitted
   * @param port    the port to which the request is being submitted (currently ignored)
   * @param path    the path to which the request is being submitted
   * @param secure  <tt>true</tt> if the request is using a secure net
   * @param cookies an array of <tt>Cookie</tt>s to be matched
   * @return an array of <tt>Cookie</tt>s matching the criterium
   */
  public Cookie[] match(String host, int port, String path, boolean secure, final Cookie cookies[]) {
    if (cookies == null) {
      return null;
    }
    List<Cookie> matching = new LinkedList<Cookie>();
    for (Cookie cookie : cookies) {
      if (match(host, port, path, secure, cookie)) {
        addInPathOrder(matching, cookie);
      }
    }
    return matching.toArray(new Cookie[matching.size()]);
  }

  /**
   * Adds the specified cookie into the specified list in descending path order. That is,
   * more specific path to least specific paths.  This may not be the fastest algorythm,
   * but it'll work OK for the small number of cookies we're generally dealing with.
   *
   * @param list the list to add the cookie to
   * @param addCookie the Cookie to add to list
   */
  private static void addInPathOrder(List<Cookie> list, Cookie addCookie) {
    int i;
    for (i = 0; i < list.size(); i++) {
      Cookie c = list.get(i);
      if (addCookie.compare(addCookie, c) > 0) {
        break;
      }
    }
    list.add(i, addCookie);
  }

  /**
   * Return a string suitable for sending in a <tt>"Cookie"</tt> header
   *
   * @param cookie a {@link Cookie} to be formatted as string
   * @return a string suitable for sending in a <tt>"Cookie"</tt> header.
   */
  public String formatCookie(Cookie cookie) {
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie is null");
    }
    StringBuilder buf = new StringBuilder();
    buf.append(cookie.getName());
    buf.append("=");
    String s = cookie.getValue();
    if (s != null) {
      buf.append(s);
    }
    return buf.toString();
  }

  /**
   * Create a <tt>"Cookie"</tt> header value containing all {@link Cookie}s in
   * <i>cookies</i> suitable for sending in a <tt>"Cookie"</tt> header
   *
   * @param cookies an array of {@link Cookie}s to be formatted
   * @return a string suitable for sending in a Cookie header.
   * @throws IllegalArgumentException if an incoming parameter is illegal
   */
  public String formatCookies(Cookie[] cookies) throws IllegalArgumentException {
    if (cookies == null) {
      throw new IllegalArgumentException("Cookie array is null");
    }
    if (cookies.length == 0) {
      throw new IllegalArgumentException("Cookie array may not be empty");
    }
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < cookies.length; i++) {
      if (i > 0) {
        buffer.append("; ");
      }
      buffer.append(formatCookie(cookies[i]));
    }
    return buffer.toString();
  }

  /**
   * Create a <tt>"Cookie"</tt> {@link Header} containing all {@link Cookie}s in <i>cookies</i>.
   *
   * @param cookies an array of {@link Cookie}s to be formatted as a <tt>"Cookie"</tt> header
   * @return a <tt>"Cookie"</tt> {@link com.sap.httpclient.http.Header}.
   */
  public Header formatCookieHeader(Cookie[] cookies) {
    return new Header(Header.COOKIE, formatCookies(cookies));
  }

  /**
   * Create a <tt>"Cookie"</tt> {@link Header} containing the {@link Cookie}.
   *
   * @param cookie <tt>Cookie</tt>s to be formatted as a <tt>Cookie</tt> header
   * @return a Cookie header.
   */
  public Header formatCookieHeader(Cookie cookie) {
    return new Header(Header.COOKIE, formatCookie(cookie));
  }

}