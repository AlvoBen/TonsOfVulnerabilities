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
import com.sap.httpclient.utils.ParameterFormatter;

/**
 * <p>RFC 2109 specific cookie management functions
 *
 * @author Nikolai Neichev
 */

public class RFC2109Spec extends CookieSpecBase {

  private final ParameterFormatter formatter;

  /**
   * Default constructor
   */
  public RFC2109Spec() {
    super();
    this.formatter = new ParameterFormatter();
    this.formatter.setUseQuotes(true);
  }

  /**
   * Parse RFC 2109 specific cookie attribute and update the corresponsing {@link Cookie} properties.
   *
   * @param attribute {@link NameValuePair} cookie attribute from the <tt>Set- Cookie</tt>
   * @param cookie    {@link Cookie} to be updated
   * @throws MalformedCookieException if an exception occurs during parsing
   */
  public void parseAttribute(final NameValuePair attribute, final Cookie cookie) throws MalformedCookieException {
    if (attribute == null) {
      throw new IllegalArgumentException("attribute is null");
    }
    if (cookie == null) {
      throw new IllegalArgumentException("cookie is null");
    }
    final String paramName = attribute.getName().toLowerCase();
    final String paramValue = attribute.getValue();
    if (paramName.equals("path")) {
      if (paramValue == null) {
        throw new MalformedCookieException("Missing value for path attribute");
      }
      if (paramValue.trim().equals("")) {
        throw new MalformedCookieException("Blank value for path attribute");
      }
      cookie.setPath(paramValue);
      cookie.setPathAttributeSpecified(true);
    } else if (paramName.equals("version")) {
      if (paramValue == null) {
        throw new MalformedCookieException("Missing value for version attribute");
      }
      try {
        cookie.setVersion(Integer.parseInt(paramValue));
      } catch (NumberFormatException e) {
        throw new MalformedCookieException("Invalid version : " + paramValue + ". Error : " + e.getMessage());
      }
    } else {
      super.parseAttribute(attribute, cookie);
    }
  }

  /**
   * Performs RFC 2109 compliant {@link Cookie} validation
   *
   * @param host   the host from which the {@link Cookie} was received
   * @param port   the port from which the {@link Cookie} was received
   * @param path   the path from which the {@link Cookie} was received
   * @param secure <tt>true</tt> when the {@link Cookie} was received using a secure connection
   * @param cookie The cookie to validate
   * @throws MalformedCookieException if an exception occurs during validation
   */
  public void validate(String host, int port, String path, boolean secure, final Cookie cookie)
          throws MalformedCookieException {

    super.validate(host, port, path, secure, cookie); // validating

    // RFC 2109 specific validation
    if (cookie.getName().indexOf(' ') != -1) {
      throw new MalformedCookieException("Cookie name may not contain blanks");
    }
    if (cookie.getName().startsWith("$")) {
      throw new MalformedCookieException("Cookie name may not start with $");
    }
    if (cookie.isDomainAttributeSpecified() && (!cookie.getDomain().equals(host))) {
      // domain must start with dot
      if (!cookie.getDomain().startsWith(".")) {
        throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain()
                + "\" violates RFC 2109: domain must start with a dot");
      }
      // domain must have at least one embedded dot
      int dotIndex = cookie.getDomain().indexOf('.', 1);
      if (dotIndex < 0 || dotIndex == cookie.getDomain().length() - 1) {
        throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain()
                + "\" violates RFC 2109: domain must contain an embedded dot");
      }
      host = host.toLowerCase();
      if (!host.endsWith(cookie.getDomain())) {
        throw new MalformedCookieException("Illegal domain attribute \"" + cookie.getDomain()
                + "\". Domain of origin: \"" + host + "\"");
      }
      // host minus domain may not contain any dots
      String hostWithoutDomain = host.substring(0, host.length() - cookie.getDomain().length());
      if (hostWithoutDomain.indexOf('.') != -1) {
        throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain()
                + "\" violates RFC 2109: host minus domain may not contain any dots");
      }
    }
  }

  /**
   * Performs domain-match as defined by the RFC2109.
   *
   * @param host   The target host.
   * @param domain The cookie domain attribute.
   * @return true if the specified host matches the specified domain.
   */
  public boolean domainMatch(String host, String domain) {
		return host.equals(domain) || (domain.startsWith(".") && host.endsWith(domain));
  }

  /**
   * Return a name/value string suitable for sending in a <tt>"Cookie"</tt> header as defined in
   * RFC 2109 for backward compatibility with cookie version 0
   *
   * @param buffer  The string buffer to use for outgoing
   * @param param   The parameter.
   * @param version The cookie version
   */
  private void formatParam(final StringBuilder buffer, final NameValuePair param, int version) {
    if (version < 1) {
      buffer.append(param.getName());
      buffer.append("=");
      if (param.getValue() != null) {
        buffer.append(param.getValue());
      }
    } else {
      this.formatter.format(buffer, param);
    }
  }

  /**
   * Return a string suitable for sending in a <tt>"Cookie"</tt> header as defined in
   * RFC 2109 for backward compatibility with cookie version 0
   *
   * @param buffer  The string buffer to use for outgoing
   * @param cookie  The {@link Cookie} to be formatted as string
   * @param version The version to use.
   */
  private void formatCookieAsVer(final StringBuilder buffer, final Cookie cookie, int version) {
    String value = cookie.getValue();
    if (value == null) {
      value = "";
    }
    formatParam(buffer, new NameValuePair(cookie.getName(), value), version);
    if ((cookie.getPath() != null) && cookie.isPathAttributeSpecified()) {
      buffer.append("; ");
      formatParam(buffer, new NameValuePair("$Path", cookie.getPath()), version);
    }
    if ((cookie.getDomain() != null) && cookie.isDomainAttributeSpecified()) {
      buffer.append("; ");
      formatParam(buffer, new NameValuePair("$Domain", cookie.getDomain()), version);
    }
  }

  /**
   * Return a string suitable for sending in a <tt>"Cookie"</tt> header as defined in RFC 2109
   *
   * @param cookie a {@link Cookie} to be formatted as string
   * @return a string suitable for sending in a <tt>"Cookie"</tt> header.
   */
  public String formatCookie(Cookie cookie) {
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie is null");
    }
    int version = cookie.getVersion();
    StringBuilder buffer = new StringBuilder();
    formatParam(buffer, new NameValuePair("$Version", Integer.toString(version)), version);
    buffer.append("; ");
    formatCookieAsVer(buffer, cookie, version);
    return buffer.toString();
  }

  /**
   * Create a RFC 2109 compliant <tt>"Cookie"</tt> header value containing all
   * {@link Cookie}s in <i>cookies</i> suitable for sending in a <tt>"Cookie"</tt> header
   *
   * @param cookies an array of {@link Cookie}s to be formatted
   * @return a string suitable for sending in a Cookie header.
   */
  public String formatCookies(Cookie[] cookies) {
    int version = Integer.MAX_VALUE;
    // pick the lowerest common denominator
    for (Cookie cookie : cookies) {
      if (cookie.getVersion() < version) {
        version = cookie.getVersion();
      }
    }
    final StringBuilder buffer = new StringBuilder();
    formatParam(buffer, new NameValuePair("$Version", Integer.toString(version)), version);
    for (Cookie cookie : cookies) {
      buffer.append("; ");
      formatCookieAsVer(buffer, cookie, version);
    }
    return buffer.toString();
  }

}