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
package com.sap.httpclient.uri;

import com.sap.httpclient.exception.URIException;

/**
 * Represents a HTTPS URL.
 *
 * @author Nikolai Neichev
 */
public class HttpsURL extends HttpURL {

  /**
   * Default scheme for HTTPS URL.
   */
  public static final char[] DEFAULT_SCHEME = {'h', 't', 't', 'p', 's'};

  /**
   * Default port for HTTPS URL.
   */
  public static final int DEFAULT_PORT = 443;

  /**
   * Create an instance as an internal use.
   */
  protected HttpsURL() {
  }

  /**
   * Construct a HTTPS URL as an escaped form of a character array with the
   * specified charset to do escape encoding.
   *
   * @param escaped the HTTPS URL character sequence
   * @param charset the charset to do escape encoding
   * @throws URIException         If validation fails
   * @throws NullPointerException if <code>escaped</code> is <code>null</code>
   */
  public HttpsURL(char[] escaped, String charset) throws URIException, NullPointerException {
    protocolCharset = charset;
    parseUriReference(new String(escaped), true);
    checkValid();
  }

  /**
   * Construct a HTTPS URL as an escaped form of a character array.
   *
   * @param escaped the HTTPS URL character sequence
   * @throws URIException         If validation fails
   * @throws NullPointerException if <code>escaped</code> is <code>null</code>
   */
  public HttpsURL(char[] escaped) throws URIException, NullPointerException {
    parseUriReference(new String(escaped), true);
    checkValid();
  }

  /**
   * Construct a HTTPS URL from a specified string with the specified charset to do escape encoding.
   *
   * @param original the HTTPS URL string
   * @param charset  the charset to do escape encoding
   * @throws URIException If validation fails
   */
  public HttpsURL(String original, String charset) throws URIException {
    protocolCharset = charset;
    parseUriReference(original, false);
    checkValid();
  }

  /**
   * Construct a HTTPS URL from a specified string.
   *
   * @param original the HTTPS URL string
   * @throws URIException If validation fails
   */
  public HttpsURL(String original) throws URIException {
    parseUriReference(original, false);
    checkValid();
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param host the host string
   * @param port the port number
   * @param path the path string
   * @throws URIException If validation fails
   */
  public HttpsURL(String host, int port, String path) throws URIException {
    this(null, host, port, path, null, null);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param host  the host string
   * @param port  the port number
   * @param path  the path string
   * @param query the query string
   * @throws URIException If validation fails
   */
  public HttpsURL(String host, int port, String path, String query) throws URIException {
    this(null, host, port, path, query, null);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @throws URIException If validation fails
   */
  public HttpsURL(String user, String password, String host) throws URIException {
    this(user, password, host, -1, null, null, null);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @param port     the port number
   * @throws URIException If validation fails
   */
  public HttpsURL(String user, String password, String host, int port) throws URIException {
    this(user, password, host, port, null, null, null);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @throws URIException If validation fails
   */
  public HttpsURL(String user, String password, String host, int port, String path) throws URIException {
    this(user, password, host, port, path, null, null);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    The query string.
   * @throws URIException If validation fails
   */
  public HttpsURL(String user, String password, String host, int port, String path, String query)
          throws URIException {

    this(user, password, host, port, path, query, null);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param host     the host string
   * @param path     the path string
   * @param query    the query string
   * @param fragment the fragment string
   * @throws URIException If validation fails
   */
  public HttpsURL(String host, String path, String query, String fragment) throws URIException {
    this(null, host, -1, path, query, fragment);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param userinfo the userinfo string whose parts are URL escaped
   * @param host     the host string
   * @param path     the path string
   * @param query    the query string
   * @param fragment the fragment string
   * @throws URIException If validation fails
   */
  public HttpsURL(String userinfo, String host, String path, String query, String fragment) throws URIException {
    this(userinfo, host, -1, path, query, fragment);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param userinfo the userinfo string whose parts are URL escaped
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @throws URIException If validation fails
   */
  public HttpsURL(String userinfo, String host, int port, String path) throws URIException {
    this(userinfo, host, port, path, null, null);
  }

  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param userinfo the userinfo string whose parts are URL escaped
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    the query string
   * @throws URIException If validation fails
   */
  public HttpsURL(String userinfo, String host, int port, String path, String query) throws URIException {
    this(userinfo, host, port, path, query, null);
  }


  /**
   * Construct a HTTPS URL from specified components.
   *
   * @param userinfo the userinfo string whose parts are URL escaped
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    the query string
   * @param fragment the fragment string
   * @throws URIException If validation fails
   */
  public HttpsURL(String userinfo, String host, int port, String path, String query, String fragment)
    throws URIException {

    // validate and contruct the URI character sequence
    StringBuilder buff = new StringBuilder();
    if (userinfo != null || host != null || port != -1) {
      _scheme = DEFAULT_SCHEME; // in order to verify the own net
      buff.append(DEFAULT_SCHEME);
      buff.append("://");
      if (userinfo != null) {
        buff.append(userinfo);
        buff.append('@');
      }
      if (host != null) {
        buff.append(URIUtil.encode(host, URI.allowed_host));
        if (port != -1 && port != DEFAULT_PORT) {
          buff.append(':');
          buff.append(port);
        }
      }
    }
    if (path != null) {  // accept empty path
      if (scheme != null && !path.startsWith("/")) {
        throw new URIException(URIException.PARSING, "abs_path requested");
      }
      buff.append(URIUtil.encode(path, URI.allowed_abs_path));
    }
    if (query != null) {
      buff.append('?');
      buff.append(URIUtil.encode(query, URI.allowed_query));
    }
    if (fragment != null) {
      buff.append('#');
      buff.append(URIUtil.encode(fragment, URI.allowed_fragment));
    }
    parseUriReference(buff.toString(), true);
    checkValid();
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    the query string
   * @param fragment the fragment string
   * @throws URIException If validation fails
   */
  public HttpsURL(String user, String password, String host, int port,
                  String path, String query, String fragment) throws URIException {
    this(HttpURL.toUserinfo(user, password), host, port, path, query, fragment);
  }

  /**
   * Construct a HTTPS URL with a specified relative HTTPS URL string.
   *
   * @param base     the base HttpsURL
   * @param relative the relative HTTPS URL string
   * @throws URIException If validation fails
   */
  public HttpsURL(HttpsURL base, String relative) throws URIException {
    this(base, new HttpsURL(relative));
  }

  /**
   * Construct a HTTPS URL with a specified relative URL.
   *
   * @param base     the base HttpsURL
   * @param relative the relative HttpsURL
   * @throws URIException If validation fails
   */
  public HttpsURL(HttpsURL base, HttpsURL relative) throws URIException {
    super(base, relative);
    checkValid();
  }

  /**
   * Get the scheme.  You can get the scheme explicitly.
   *
   * @return the scheme null if empty or undefined
   */
  public String getScheme() {
    return (_scheme == null) ? null : new String(HttpsURL.DEFAULT_SCHEME);
  }

  /**
   * Get the port number.
   *
   * @return the port number
   */
  public int getPort() {
    return (_port == -1) ? HttpsURL.DEFAULT_PORT : _port;
  }

  /**
   * Verify the valid class use for construction.
   *
   * @throws URIException the wrong scheme use
   */
  protected void checkValid() throws URIException {
    // could be explicit net or undefined.
    if (!(equals(_scheme, DEFAULT_SCHEME) || _scheme == null)) {
      throw new URIException(URIException.PARSING, "wrong class use");
    }
  }

}