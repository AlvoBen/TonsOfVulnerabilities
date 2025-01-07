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
 * The HTTP URL.
 *
 * @author Nikolai Neichev
 */
public class HttpURL extends URI {

  /**
   * Default scheme for HTTP URL.
   */
  public static final char[] DEFAULT_SCHEME = {'h', 't', 't', 'p'};

  /**
   * Default port for HTTP URL.
   */
  public static final int DEFAULT_PORT = 80;

  /**
   * Create an instance as an internal use.
   */
  protected HttpURL() {
  }

  /**
   * Construct a HTTP URL as an escaped form of a character array with the
   * specified charset to do escape encoding.
   *
   * @param escaped the HTTP URL character sequence
   * @param charset the charset string to do escape encoding
   * @throws URIException         If validation fails
   * @throws NullPointerException if <code>escaped</code> is <code>null</code>
   */
  public HttpURL(char[] escaped, String charset) throws URIException, NullPointerException {
    protocolCharset = charset;
    parseUriReference(new String(escaped), true);
    checkValid();
  }

  /**
   * Construct a HTTP URL as an escaped form of a character array.
   *
   * @param escaped the HTTP URL character sequence
   * @throws URIException         If validation fails
   * @throws NullPointerException if <code>escaped</code> is <code>null</code>
   */
  public HttpURL(char[] escaped) throws URIException, NullPointerException {
    parseUriReference(new String(escaped), true);
    checkValid();
  }

  /**
   * Construct a HTTP URL from a specified string with the specified charset to do escape encoding.
   *
   * @param original the HTTP URL string
   * @param charset  the charset string to do escape encoding
   * @throws URIException If validation fails
   */
  public HttpURL(String original, String charset) throws URIException {
    protocolCharset = charset;
    parseUriReference(original, false);
    checkValid();
  }

  /**
   * Construct a HTTP URL from a specified string.
   *
   * @param original the HTTP URL string
   * @throws URIException If validation fails
   */
  public HttpURL(String original) throws URIException {
    parseUriReference(original, false);
    checkValid();
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param host the host string
   * @param port the port number
   * @param path the path string
   * @throws URIException If validation fails
   */
  public HttpURL(String host, int port, String path) throws URIException {
    this(null, null, host, port, path, null, null);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param host  the host string
   * @param port  the port number
   * @param path  the path string
   * @param query the query string
   * @throws URIException If validation fails
   */
  public HttpURL(String host, int port, String path, String query) throws URIException {
    this(null, null, host, port, path, query, null);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @throws URIException If validation fails
   */
  public HttpURL(String user, String password, String host) throws URIException {
    this(user, password, host, -1, null, null, null);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @param port     the port number
   * @throws URIException If validation fails
   */
  public HttpURL(String user, String password, String host, int port) throws URIException {
    this(user, password, host, port, null, null, null);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @throws URIException If validation fails
   */
  public HttpURL(String user, String password, String host, int port, String path) throws URIException {
    this(user, password, host, port, path, null, null);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param user     the user name
   * @param password his or her password
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    The query string.
   * @throws URIException If validation fails
   */
  public HttpURL(String user, String password, String host, int port, String path, String query) throws URIException {
    this(user, password, host, port, path, query, null);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param host     the host string
   * @param path     the path string
   * @param query    the query string
   * @param fragment the fragment string
   * @throws URIException If validation fails
   */
  public HttpURL(String host, String path, String query, String fragment) throws URIException {
    this(null, null, host, -1, path, query, fragment);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param userinfo the userinfo string whose parts are URL escaped
   * @param host     the host string
   * @param path     the path string
   * @param query    the query string
   * @param fragment the fragment string
   * @throws URIException If validation fails
   */
  public HttpURL(String userinfo, String host, String path, String query, String fragment) throws URIException {
    this(userinfo, host, -1, path, query, fragment);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param userinfo the userinfo string whose parts are URL escaped
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @throws URIException If validation fails
   */
  public HttpURL(String userinfo, String host, int port, String path) throws URIException {
    this(userinfo, host, port, path, null, null);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param userinfo the userinfo string whose parts are URL escaped
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    the query string
   * @throws URIException If validation fails
   */
  public HttpURL(String userinfo, String host, int port, String path, String query) throws URIException {
    this(userinfo, host, port, path, query, null);
  }

  /**
   * Construct a HTTP URL from specified components.
   *
   * @param userinfo the userinfo string whose parts are URL escaped
   * @param host     the host string
   * @param port     the port number
   * @param path     the path string
   * @param query    the query string
   * @param fragment the fragment string
   * @throws URIException If validation fails
   */
  public HttpURL(String userinfo, String host, int port, String path, String query, String fragment)
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
        if ((port != -1) && (port != DEFAULT_PORT)) {
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
  public HttpURL(String user, String password, String host, int port,
                 String path, String query, String fragment) throws URIException {
    this(toUserinfo(user, password), host, port, path, query, fragment);
  }

  protected static String toUserinfo(String user, String password) {
    if (user == null) return null;
    StringBuilder usrinfo = new StringBuilder(20); //sufficient for real world
    usrinfo.append(URIUtil.encode(user, URI.allowed_within_userinfo));
    if (password == null) return usrinfo.toString();
    usrinfo.append(':');
    usrinfo.append(URIUtil.encode(password, URI.allowed_within_userinfo));
    return usrinfo.toString();
  }

  /**
   * Construct a HTTP URL with a specified relative URL string.
   *
   * @param base     the base HttpURL
   * @param relative the relative HTTP URL string
   * @throws URIException If validation fails
   */
  public HttpURL(HttpURL base, String relative) throws URIException {
    this(base, new HttpURL(relative));
  }

  /**
   * Construct a HTTP URL with a specified relative URL.
   *
   * @param base     the base HttpURL
   * @param relative the relative HttpURL
   * @throws URIException If validation fails
   */
  public HttpURL(HttpURL base, HttpURL relative) throws URIException {
    super(base, relative);
    checkValid();
  }

  /**
   * Get the scheme.  You can get the scheme explicitly.
   *
   * @return the scheme null if empty or undefined
   */
  public String getScheme() {
    return (_scheme == null) ? null : new String(HttpURL.DEFAULT_SCHEME);
  }

  /**
   * Get the port number.
   *
   * @return the port number
   */
  public int getPort() {
    return (_port == -1) ? HttpURL.DEFAULT_PORT : _port;
  }

  /**
   * Set the raw-escaped user and password.
   *
   * @param escapedUser     the raw-escaped user
   * @param escapedPassword the raw-escaped password; could be null
   * @throws URIException escaped user not valid or user required; escaped
   *                      password not valid or username missed
   */
  public void setRawUserinfo(char[] escapedUser, char[] escapedPassword) throws URIException {
    if (escapedUser == null || escapedUser.length == 0) {
      throw new URIException(URIException.PARSING, "user required");
    }
    if (!validate(escapedUser, within_userinfo)
            || ((escapedPassword != null)
            && !validate(escapedPassword, within_userinfo))) {
      throw new URIException(URIException.ESCAPING, "escaped userinfo not valid");
    }
    String username = new String(escapedUser);
    String password = (escapedPassword == null) ? null : new String(escapedPassword);
    String userinfo = username + ((password == null) ? "" : ":" + password);
    String hostname = new String(getRawHost());
    String hostport = (_port == -1) ? hostname : hostname + ":" + _port;
    String authority = userinfo + "@" + hostport;
    _userinfo = userinfo.toCharArray();
    _authority = authority.toCharArray();
    setURI();
  }

  /**
   * Get the raw-escaped password.
   *
   * @return the raw-escaped password
   */
  public char[] getRawPassword() {
    int from = indexFirstOf(_userinfo, ':');
    if (from == -1) {
      return null; // null or only user.
    }
    int len = _userinfo.length - from - 1;
    char[] result = new char[len];
    System.arraycopy(_userinfo, from + 1, result, 0, len);
    return result;
  }

  /**
   * Get the raw-escaped current hierarchy level.
   *
   * @return the raw-escaped current hierarchy level
   * @throws URIException If {@link #getRawCurrentHierPath(char[])} fails.
   */
  public char[] getRawCurrentHierPath() throws URIException {
    return (_path == null || _path.length == 0) ? rootPath : super.getRawCurrentHierPath(_path);
  }

  /**
   * Get the level above the this hierarchy level.
   *
   * @return the raw above hierarchy level
   * @throws URIException If {@link #getRawCurrentHierPath(char[])} fails.
   */
  public char[] getRawAboveHierPath() throws URIException {
    char[] path = getRawCurrentHierPath();
    return (path == null || path.length == 0) ? rootPath : getRawCurrentHierPath(path);
  }

  /**
   * Get the raw escaped path.
   *
   * @return the path '/' if empty or undefined
   */
  public char[] getRawPath() {
    char[] path = super.getRawPath();
    return (path == null || path.length == 0) ? rootPath : path;
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