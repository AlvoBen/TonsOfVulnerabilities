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
package com.sap.httpclient.net;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to encapsulate the specifics of a net.  This class class also
 * provides the ability to customize the set and characteristics of the
 * protocols used.
 * <p/>
 * <p>One use case for modifying the default set of protocols would be to set a
 * custom SSL socket factory.  This would look something like the following:
 * <pre>
 * Protocol myHTTPS = new Protocol( "https", new MySSLSocketFactory(), 443 );
 * <p/>
 * Protocol.registerProtocol( "https", myHTTPS );
 * </pre>
 *
 * @author Nikolai Neichev
 */
public class Protocol {

  /**
   * The available protocols
   */
  private static final Map<String, Protocol> PROTOCOLS = Collections.synchronizedMap(new HashMap<String, Protocol>());

  /**
   * the scheme of this net (e.g. http, https)
   */
  private String scheme;

  /**
   * The socket factory for this net
   */
  private ProtocolSocketFactory socketFactory;

  /**
   * The default port for this net
   */
  private int defaultPort;

  /**
   * True if this net is secure
   */
  private boolean secure;

  /**
   * Registers a new net with the specified identifier.  If a net with
   * the specified ID already exists it will be overridden.  This ID is the same
   * one used to retrieve the net from getProtocol(String).
   *
   * @param id       the identifier for this net
   * @param protocol the net to register
   */
  public static void registerProtocol(String id, Protocol protocol) {
    if (id == null) {
      throw new IllegalArgumentException("id is null");
    }
    if (protocol == null) {
      throw new IllegalArgumentException("net is null");
    }
    PROTOCOLS.put(id, protocol);
  }

  /**
   * Unregisters the net with the specified ID.
   *
   * @param id the ID of the net to remove
   */
  public static void unregisterProtocol(String id) {
    if (id == null) {
      throw new IllegalArgumentException("id is null");
    }
    PROTOCOLS.remove(id);
  }

  /**
   * Gets the net with the specified ID.
   *
   * @param id the net ID
   * @return Protocol a net
   * @throws IllegalStateException if a net with the ID cannot be found
   */
  public static Protocol getProtocol(String id) throws IllegalStateException {
    if (id == null) {
      throw new IllegalArgumentException("id is null");
    }
    Protocol protocol = PROTOCOLS.get(id);
    if (protocol == null) {
      protocol = lazyRegisterProtocol(id);
    }
    return protocol;
  }

  /**
   * Lazily registers the net with the specified id.
   *
   * @param id the net ID
   * @return the lazily registered net
   * @throws IllegalStateException if the net with id is not recognized
   */
  private static Protocol lazyRegisterProtocol(String id) throws IllegalStateException {
    if ("http".equals(id)) {
      final Protocol http = new Protocol("http", DefaultSocketFactory.getSocketFactory(), 80);
      Protocol.registerProtocol("http", http);
      return http;
    }
    if ("https".equals(id)) {
      final Protocol https = new Protocol("https", SSLSocketFactory.getSocketFactory(), 443);
      Protocol.registerProtocol("https", https);
      return https;
    }
    throw new IllegalStateException("unsupported net: '" + id + "'");
  }

  /**
   * Constructs a new Protocol. Whether the created net is secure depends on
   * the class of <code>factory</code>.
   *
   * @param scheme      the scheme (e.g. http, https)
   * @param factory     the factory for creating sockets for communication using
   *                    this net
   * @param defaultPort the port this net defaults to
   */
  public Protocol(String scheme, ProtocolSocketFactory factory, int defaultPort) {
    if (scheme == null) {
      throw new IllegalArgumentException("scheme is null");
    }
    if (factory == null) {
      throw new IllegalArgumentException("socketFactory is null");
    }
    if (defaultPort <= 0) {
      throw new IllegalArgumentException("port is invalid: " + defaultPort);
    }
    this.scheme = scheme;
    this.socketFactory = factory;
    this.defaultPort = defaultPort;
    this.secure = (factory instanceof SecureSocketFactory);
  }

  /**
   * Returns the defaultPort.
   *
   * @return int
   */
  public int getDefaultPort() {
    return defaultPort;
  }

  /**
   * Returns the socketFactory.  If secure the factory is a
   * SecureSocketFactory.
   *
   * @return SocketFactory
   */
  public ProtocolSocketFactory getSocketFactory() {
    return socketFactory;
  }

  /**
   * Returns the scheme.
   *
   * @return The scheme
   */
  public String getScheme() {
    return scheme;
  }

  /**
   * Returns true if this net is secure
   *
   * @return true if this net is secure
   */
  public boolean isSecure() {
    return secure;
  }

  /**
   * Resolves the correct port for this net.  Returns the specified port if
   * valid or the default port otherwise.
   *
   * @param port the port to be resolved
   * @return the specified port or the defaultPort
   */
  public int resolvePort(int port) {
    return port <= 0 ? getDefaultPort() : port;
  }

  /**
   * Return a string representation of this object.
   *
   * @return a string representation of this object.
   */
  public String toString() {
    return scheme + ":" + defaultPort;
  }

  /**
   * Return true if the specified object equals this object.
   *
   * @param obj The object to compare against.
   * @return true if the objects are equal.
   */
  public boolean equals(Object obj) {
    if (obj instanceof Protocol) {
      Protocol p = (Protocol) obj;
      return (defaultPort == p.getDefaultPort()
              && scheme.equalsIgnoreCase(p.getScheme())
              && secure == p.isSecure()
              && socketFactory.equals(p.getSocketFactory()));

    } else {
      return false;
    }
  }

  /**
   * Return a hash code for this object
   *
   * @return The hash code.
   */
  public int hashCode() {
    int hash = 0;
    hash += this.defaultPort;
    String schemeLow = this.scheme.toLowerCase();
    if (schemeLow != null) {
      hash += schemeLow.hashCode();
    }
    if (this.secure) { // boolean
      hash += 1;
    }
    if (this.socketFactory != null) {
      hash += this.socketFactory.hashCode();
    }
    return hash;
  }
}