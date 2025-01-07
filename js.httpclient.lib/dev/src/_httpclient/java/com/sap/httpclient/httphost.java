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
package com.sap.httpclient;

import com.sap.httpclient.exception.URIException;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.uri.URI;

/**
 * Represents a http host.
 *
 *
 * @author Nikolai Neichev
 */
public class HttpHost implements Cloneable {

  /**
   * The host to use.
   */
  private String hostName = null;

  /**
   * The port to use.
   */
  private int port = -1;

  /**
   * The net
   */
  private Protocol protocol = null;

  /**
   * Constructor for HttpHost.
   *
   * @param hostname the hostname. Can't be <code>null</code>.
   * @param port     the port. Value <code>-1</code> can be used for default port
   * @param protocol the protocol. Can't be <code>null</code>.
   */
  public HttpHost(final String hostname, int port, final Protocol protocol) {
    super();
    if (hostname == null) {
      throw new IllegalArgumentException("Host name is null");
    }
    if (protocol == null) {
      throw new IllegalArgumentException("Protocol is null");
    }
    this.hostName = hostname;
    this.protocol = protocol;
    if (port >= 0) {
      this.port = port;
    } else {
      this.port = this.protocol.getDefaultPort();
    }
  }

  /**
   * Constructor for HttpHost with the default protocol "http".
   *
   * @param hostname the hostname . Can't be <code>null</code>.
   * @param port     the port. Value <code>-1</code> can be used for default port
   */
  public HttpHost(final String hostname, int port) {
    this(hostname, port, Protocol.getProtocol("http"));
  }

  /**
   * Constructor for HttpHost with deafault protocol "http" and default port 80.
   *
   * @param hostname the hostname (IP or DNS name). Can be <code>null</code>.
   */
  public HttpHost(final String hostname) {
    this(hostname, -1, Protocol.getProtocol("http"));
  }

  /**
   * URI constructor for HttpHost.
   *
   * @param uri the URI.
	 * @throws com.sap.httpclient.exception.URIException if the uri is wrong
   */
  public HttpHost(final URI uri) throws URIException {
    this(uri.getHost(), uri.getPort(), Protocol.getProtocol(uri.getScheme()));
  }

  /**
   * URI constructor for HttpHost(not strict).
	 * Called when Parameters.ENCODE_URLS is set to "true"
   *
   * @param uri the URI.
	 * @param not_strict always TRUE
	 * @throws com.sap.httpclient.exception.URIException if the uri is invalid
   */
  public HttpHost(final URI uri, boolean not_strict) throws URIException {
    this(uri.getHost_notStrict(), uri.getPort(), Protocol.getProtocol(uri.getScheme()));
  }

  /**
   * Copy constructor for HttpHost
   *
   * @param httphost the HTTP host to copy details from
   */
  public HttpHost(final HttpHost httphost) {
    super();
    this.hostName = httphost.hostName;
    this.port = httphost.port;
    this.protocol = httphost.protocol;
  }

  @SuppressWarnings({"CloneDoesntCallSuperClone"})
	public Object clone() throws CloneNotSupportedException {
    return new HttpHost(this);
  }

  /**
   * Returns the host name.
   *
   * @return the host name, <code>null</code> if not set
   */
  public String getHostName() {
    return this.hostName;
  }

  /**
   * Returns the port.
   *
   * @return the host port, <code>-1</code> if not set
   */
  public int getPort() {
    return this.port;
  }

  /**
   * Returns the protocol.
   *
   * @return The protocol.
   */
  public Protocol getProtocol() {
    return this.protocol;
  }

  /**
   * Return the host uri as String.
   *
   * @return The host uri.
   */
  public String toURI() {
    StringBuilder buffer = new StringBuilder(50);
    if (this.protocol != null) {
      buffer.append(this.protocol.getScheme());
      buffer.append("://");
    }
    buffer.append(this.hostName);
    if (this.port != this.protocol.getDefaultPort()) {
      buffer.append(':');
      buffer.append(this.port);
    }
    return buffer.toString();
  }

  public String toString() {
    return toURI();
  }

  public boolean equals(final Object o) {
    if (o instanceof HttpHost) {
      // shortcut if we're comparing with ourselves
      if (o == this) {
        return true;
      }
      HttpHost that = (HttpHost) o;
      if (!this.hostName.equalsIgnoreCase(that.hostName)) {
        return false;
      }
      if (this.port != that.port) {
        return false;
      }
      if (!this.protocol.equals(that.protocol)) {
        return false;
      }
      // everything matches
      return true;
    } else {
      return false;
    }
  }

  public int hashCode() {
    int hash = 0;
    if (this.hostName != null) {
      hash += this.hostName.hashCode();
    }
    hash += this.port;
    if (this.protocol != null) {
      hash += this.protocol.hashCode();
    }
    return hash;
  }

}