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
import com.sap.httpclient.http.HeaderElement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * <P>Netscape cookie draft specific cookie management functions
 *
 * @author Nikolai Neichev
 */

public class NetscapeDraftSpec extends CookieSpecBase {

  /**
   * Default constructor
   */
  public NetscapeDraftSpec() {
    super();
  }

  /**
   * Parses the Set-Cookie value into an array of <tt>Cookie</tt>s.
   * <p/>
   * <p>Syntax of the Set-Cookie HTTP Response Header:</p>
   * <p/>
   * <p>This is the format a CGI script would use to add to
   * the HTTP headers a new piece of data which is to be stored by
   * the client for later retrieval.</p>
   * <p/>
   * <PRE>
   * Set-Cookie: NAME=VALUE; expires=DATE; path=PATH; domain=DOMAIN_NAME; secure
   * </PRE>
   * <p/>
   * <p>Please note that Netscape draft specification does not fully
   * conform to the HTTP header format. Netscape draft does not specify
   * whether multiple cookies may be sent in one header. Hence, comma
   * character may be present in unquoted cookie value or unquoted
   * parameter value.</p>
   *
   * @param host   the host from which the <tt>Set-Cookie</tt> value was
   *               received
   * @param port   the port from which the <tt>Set-Cookie</tt> value was
   *               received
   * @param path   the path from which the <tt>Set-Cookie</tt> value was
   *               received
   * @param secure <tt>true</tt> when the <tt>Set-Cookie</tt> value was
   *               received over secure conection
   * @param header the <tt>Set-Cookie</tt> received from the server
   * @return an array of <tt>Cookie</tt>s parsed from the Set-Cookie value
   * @throws MalformedCookieException if an exception occurs during parsing
   * @link http://wp.netscape.com/newsref/std/cookie_spec.html
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
        //Do not remove the very first slash
        lastSlashIndex = 1;
      }
      defaultPath = defaultPath.substring(0, lastSlashIndex);
    }
    HeaderElement headerelement = new HeaderElement(header.toCharArray());
    Cookie cookie = new Cookie(host,
            headerelement.getName(),
            headerelement.getValue(),
            defaultPath,
            null,
            false);
    // cycle through the parameters
    NameValuePair[] parameters = headerelement.getParameters();
    // could be null. In case only a header element and no parameters.
    if (parameters != null) {
      for (NameValuePair parameter : parameters) {
        parseAttribute(parameter, cookie);
      }
    }
    return new Cookie[]{cookie};
  }

  /**
   * Parse the cookie attribute and update the corresponsing {@link Cookie}
   * properties as defined by the Netscape draft specification
   *
   * @param attribute {@link NameValuePair} cookie attribute from the <tt>Set- Cookie</tt>
   * @param cookie    {@link Cookie} to be updated
   * @throws MalformedCookieException if an exception occurs during parsing
   */
  public void parseAttribute(final NameValuePair attribute, final Cookie cookie) throws MalformedCookieException {
    if (attribute == null) {
      throw new IllegalArgumentException("Attribute is null.");
    }
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie is null.");
    }
    final String paramName = attribute.getName().toLowerCase();
    final String paramValue = attribute.getValue();

    if (paramName.equals("expires")) {
      if (paramValue == null) {
        throw new MalformedCookieException("Missing value for expires attribute");
      }
      try {
        DateFormat expiryFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
        Date date = expiryFormat.parse(paramValue);
        cookie.setExpiryDate(date);
      } catch (ParseException e) {
        throw new MalformedCookieException("Invalid expires attribute: " + e.getMessage());
      }
    } else {
      super.parseAttribute(attribute, cookie);
    }
  }

  /**
   * Performs domain-match as described in the Netscape draft.
   *
   * @param host   The target host.
   * @param domain The cookie domain attribute.
   * @return true if the specified host matches the specified domain.
   */
  public boolean domainMatch(final String host, final String domain) {
    return host.endsWith(domain);
  }

  /**
   * Performs Netscape draft compliant {@link Cookie} validation
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

    LOG.debugT("enterNetscapeDraftCookieProcessor RCF2109CookieProcessor.validate(Cookie)");
    // Perform generic validation
    super.validate(host, port, path, secure, cookie);
    // Perform Netscape Cookie draft specific validation
    if (host.indexOf(".") >= 0) {
      int domainParts = new StringTokenizer(cookie.getDomain(), ".").countTokens();
      if (isSpecialDomain(cookie.getDomain())) {
        if (domainParts < 2) {
          throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain()
                  + "\" violates the Netscape cookie specification for special domains");
        }
      } else {
        if (domainParts < 3) {
          throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain()
                  + "\" violates the Netscape cookie specification");
        }
      }
    }
  }

  /**
   * Checks if the specified domain is in one of the seven special
   * top level domains defined by the Netscape cookie specification.
   *
   * @param domain The domain.
   * @return True if the specified domain is "special"
   */
  private static boolean isSpecialDomain(final String domain) {
    final String ucDomain = domain.toUpperCase();
		return ucDomain.endsWith(".COM") ||
					 ucDomain.endsWith(".EDU") ||
					 ucDomain.endsWith(".NET") ||
					 ucDomain.endsWith(".GOV") ||
					 ucDomain.endsWith(".MIL") ||
					 ucDomain.endsWith(".ORG") ||
					 ucDomain.endsWith(".INT");
	}
}