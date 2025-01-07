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
package com.sap.httpclient.auth;

/**
 * The class represents an authentication scope consisting of a host name,
 * a port number, a realm name and an authentication scheme name which
 * {@link com.sap.httpclient.auth.Credentials} apply to.
 *
 * @author Nikolai Neichev
 */
public class AuthScope {

  /**
   * The <tt>null</tt> value represents any host. In the future versions of
   * HttpClient the use of this parameter will be discontinued.
   */
  public static final String ANY_HOST = null;

  /**
   * The <tt>-1</tt> value represents any port.
   */
  public static final int ANY_PORT = -1;

  /**
   * The <tt>null</tt> value represents any realm.
   */
  public static final String ANY_REALM = null;

  /**
   * The <tt>null</tt> value represents any authentication scheme.
   */
  public static final String ANY_SCHEME = null;

  /**
   * Default scope matching any host, port, realm and authentication scheme.
   * In the future versions of HttpClient the use of this parameter will be discontinued.
   */
  public static final AuthScope ANY = new AuthScope(ANY_HOST, ANY_PORT, ANY_REALM, ANY_SCHEME);

  /**
   * The host the credentials apply to.
   */
  private String host = null;

  /**
   * The port the credentials apply to.
   */
  private int port = -1;

  /**
   * The realm the credentials apply to.
   */
  private String realm = null;

  /**
   * The authentication scheme the credentials apply to.
   */
  private String scheme = null;

  /**
   * Creates a new credentials scope for the specified
   * <tt>host</tt>, <tt>port</tt>, <tt>realm</tt>, and
   * <tt>authentication scheme</tt>.
   *
   * @param host   the host the credentials apply to. May be set
   *               to <tt>null</tt> if credenticals are applicable to any host.
   * @param port   the port the credentials apply to. May be set
   *               to negative value if credenticals are applicable to any port.
   * @param realm  the realm the credentials apply to. May be set
   *               to <tt>null</tt> if credenticals are applicable to any realm.
   * @param scheme the authentication scheme the credentials apply to.
   *               May be set to <tt>null</tt> if credenticals are applicable to any authentication scheme.
   */
  public AuthScope(final String host, int port, final String realm, final String scheme) {
    this.host = (host == null) ? ANY_HOST : host.toLowerCase();
    this.port = (port < 0) ? ANY_PORT : port;
    this.realm = (realm == null) ? ANY_REALM : realm;
    this.scheme = (scheme == null) ? ANY_SCHEME : scheme.toUpperCase();
  }

  /**
   * Creates a new credentials scope for the specified
   * <tt>host</tt>, <tt>port</tt>, <tt>realm</tt>, and any authentication scheme.
   *
   * @param host  the host the credentials apply to. May be set
   *              to <tt>null</tt> if credenticals are applicable to any host.
   * @param port  the port the credentials apply to. May be set
   *              to negative value if credenticals are applicable to any port.
   * @param realm the realm the credentials apply to. May be set
   *              to <tt>null</tt> if credenticals are applicable to any realm.
   */
  public AuthScope(final String host, int port, final String realm) {
    this(host, port, realm, ANY_SCHEME);
  }

  /**
   * Creates a new credentials scope for the specified
   * <tt>host</tt>, <tt>port</tt>, any realm name, and any
   * authentication scheme.
   *
   * @param host the host the credentials apply to. May be set
   *             to <tt>null</tt> if credenticals are applicable to any host.
   * @param port the port the credentials apply to. May be set
   *             to negative value if credenticals are applicable to any port.
   */
  public AuthScope(final String host, int port) {
    this(host, port, ANY_REALM, ANY_SCHEME);
  }

  /**
   * Creates a copy of the specified credentials scope.
	 * @param authscope a root auth scome
	 */
  public AuthScope(final AuthScope authscope) {
    super();
    if (authscope == null) {
      throw new IllegalArgumentException("Scope is null");
    }
    this.host = authscope.getHost();
    this.port = authscope.getPort();
    this.realm = authscope.getRealm();
    this.scheme = authscope.getScheme();
  }

  /**
   * @return the host
   */
  public String getHost() {
    return this.host;
  }

  /**
   * @return the port
   */
  public int getPort() {
    return this.port;
  }

  /**
   * @return the realm name
   */
  public String getRealm() {
    return this.realm;
  }

  /**
   * @return the scheme type
   */
  public String getScheme() {
    return this.scheme;
  }

  /**
   * Determines if the specified parameters are equal.
   *
   * @param p1 the parameter
   * @param p2 the other parameter
   * @return boolean true if the parameters are equal, otherwise false.
   */
  private static boolean paramsEqual(final String p1, final String p2) {
    if (p1 == null) {
			return p2 == null;     // true if both are null
		} else {
      return p1.equals(p2);
    }
  }

  /**
   * Determines if the specified parameters are equal.
   *
   * @param p1 the parameter
   * @param p2 the other parameter
   * @return boolean true if the parameters are equal, otherwise false.
   */
  private static boolean paramsEqual(int p1, int p2) {
    return p1 == p2;
  }

  /**
   * Tests if the authentication scopes match.
   *
	 * @param that the auth scope to match
	 *
   * @return the match factor. Negative value signifies no match.
   *         Non-negative signifies a match. The greater the returned value the closer the match.
   */
  public int match(final AuthScope that) {
    int factor = 0;
    if (paramsEqual(this.scheme, that.scheme)) {
      factor += 1;
    } else {
      if (this.scheme != ANY_SCHEME && that.scheme != ANY_SCHEME) {
        return -1;
      }
    }
    if (paramsEqual(this.realm, that.realm)) {
      factor += 2;
    } else {
      if (this.realm != ANY_REALM && that.realm != ANY_REALM) {
        return -1;
      }
    }
    if (paramsEqual(this.port, that.port)) {
      factor += 4;
    } else {
      if (this.port != ANY_PORT && that.port != ANY_PORT) {
        return -1;
      }
    }
    if (paramsEqual(this.host, that.host)) {
      factor += 8;
    } else {
      if (this.host != ANY_HOST && that.host != ANY_HOST) {
        return -1;
      }
    }
    return factor;
  }

  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (!(o instanceof AuthScope)) {
      return super.equals(o);
    }
    AuthScope that = (AuthScope) o;
    return paramsEqual(this.host, that.host)
        && paramsEqual(this.port, that.port)
        && paramsEqual(this.realm, that.realm)
        && paramsEqual(this.scheme, that.scheme);
  }

  public String toString() {
    StringBuilder buffer = new StringBuilder();
    if (this.scheme != null) {
      buffer.append(this.scheme.toUpperCase());
      buffer.append(' ');
    }
    if (this.realm != null) {
      buffer.append('\'');
      buffer.append(this.realm);
      buffer.append('\'');
    } else {
      buffer.append("<any realm>");
    }
    if (this.host != null) {
      buffer.append('@');
      buffer.append(this.host);
      if (this.port >= 0) {
        buffer.append(':');
        buffer.append(this.port);
      }
    }
    return buffer.toString();
  }

  /**
   * Calculates the hashcode
   */
  public int hashCode() {
    int hash = 0;
    if (this.host != null) {
      hash += this.host.hashCode();
    }
    hash += this.port;
    if (this.realm != null) {
      hash += this.realm.hashCode();
    }
    if (this.scheme != null) {
      hash += this.scheme.hashCode();
    }
    return hash;
  }
}