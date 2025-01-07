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
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.uri.URI;

import java.net.InetAddress;

/**
 * Represents onfiguration for a Host
 *
 * @author Nikolai Neichev
 */
public class HostConfiguration implements Cloneable {

  /**
   * Any host configuration constant
   */
  public static final HostConfiguration ANY_HOST_CONFIGURATION = new HostConfiguration();

  /**
   * The host to use.
   */
  private HttpHost host = null;

  /**
   * The host name of the proxy server
   */
  private ProxyHost proxyHost = null;

  /**
   * The local address to use when creating the socket, or null to use the default
   */
  private InetAddress localAddress = null;

  /**
   * Parameters specific to this host
   */
  private HttpClientParameters params = new HttpClientParameters();

  /**
   * Constructor for HostConfiguration.
   */
  public HostConfiguration() {
    super();
  }

 /**
   * Constructs a host configuration with specified host, port and the default protocol "http"
   *
   * @param host the specified host
   * @param port the specified port
   */
  public HostConfiguration(String host, int port) {
    this();
    this.setHost(host, port);
  }

  /**
   * Constructs a host configuration with specified host, port and protocol
   *
   * @param host the specified host
   * @param port the specified port
   * @param protocol the specified net
   */
  public HostConfiguration(String host, int port, Protocol protocol) {
    this();
    if (protocol != null) {
      this.setHost(host, port, protocol);
    } else {
      this.setHost(host, port);
    }
  }

  /**
   * Copy constructor for HostConfiguration
   *
   * @param hostConfiguration the hostConfiguration to copy
   */
  public HostConfiguration(final HostConfiguration hostConfiguration) {
    synchronized (hostConfiguration) {
      try {
        if (hostConfiguration.host != null) {
          this.host = (HttpHost) hostConfiguration.host.clone();
        } else {
          this.host = null;
        }
        if (hostConfiguration.proxyHost != null) {
          this.proxyHost = (ProxyHost) hostConfiguration.proxyHost.clone();
        } else {
          this.proxyHost = null;
        }
        this.localAddress = hostConfiguration.getLocalAddress();
        this.params = (HttpClientParameters) hostConfiguration.getParams().clone();
      } catch (CloneNotSupportedException e) {
        throw new IllegalArgumentException("Host configuration could not be cloned");
      }
    }
  }

  @SuppressWarnings({"CloneDoesntCallSuperClone"})
	public Object clone() throws CloneNotSupportedException {
    return new HostConfiguration(this);
  }

  public synchronized String toString() {
    boolean appendComma = false;
    StringBuilder b = new StringBuilder(50);
    b.append("HostConfiguration[");
    if (this.host != null) {
      appendComma = true;
      b.append("host=").append(this.host);
    }
    if (this.proxyHost != null) {
      if (appendComma) {
        b.append(", ");
      } else {
        appendComma = true;
      }
      b.append("proxyHost=").append(this.proxyHost);
    }
    if (this.localAddress != null) {
      if (appendComma) {
        b.append(", ");
      }
      b.append("localAddress=").append(this.localAddress);
      b.append(", ");
      b.append("params=").append(this.params);
    }
    b.append("]");
    return b.toString();
  }

  /**
   * Tests if the host configuration equals the configuration set on the connection.
   *
   * @param connection the connection to test against
   * @return <code>true</code> if the connection's host, port, protpcol, local address and virtual addresshost match
   */
  public synchronized boolean hostEquals(final HttpConnection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("Connection is null");
    }
    if (this.host != null) {
      if (!this.host.getHostName().equalsIgnoreCase(connection.getHost())) {
        return false;
      }
      if (this.host.getPort() != connection.getPort()) {
        return false;
      }
      if (!this.host.getProtocol().equals(connection.getProtocol())) {
        return false;
      }
      if (this.localAddress != null) {
        if (!this.localAddress.equals(connection.getLocalAddress())) {
          return false;
        }
      } else {
        if (connection.getLocalAddress() != null) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Tests if the proxy configuration equals the configuration set on the connection.
   *
   * @param connection the connection to test against
   * @return <code>true</code> if the proxy host and proxy port are equal, <code>false</code> otherwise
   */
  public synchronized boolean proxyEquals(final HttpConnection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("Connection is null");
    }
    if (this.proxyHost != null) {
      return this.proxyHost.getHostName().equalsIgnoreCase(connection.getProxyHost())
             && this.proxyHost.getPort() == connection.getProxyPort();
    } else {
      return connection.getProxyHost() == null;
    }
  }

  /**
   * Sets the specified host
   *
   * @param host the host
   */
  public synchronized void setHost(final HttpHost host) {
    this.host = host;
  }

  /**
   * Sets the specified host, port and protocol
   *
   * @param host     the host(IP or DNS name)
   * @param port     The port
   * @param protocol The protocol as String.
   */
  public synchronized void setHost(final String host, int port, final String protocol) {
    this.host = new HttpHost(host, port, Protocol.getProtocol(protocol));
  }

  /**
   * Sets the specified host, port and protocol.
   *
   * @param host     the host(IP or DNS name)
   * @param port     The port
   * @param protocol the protocol
   */
  public synchronized void setHost(final String host, int port, final Protocol protocol) {
    if (host == null) {
      throw new IllegalArgumentException("host is null");
    }
    if (protocol == null) {
      throw new IllegalArgumentException("protocol is null");
    }
    this.host = new HttpHost(host, port, protocol);
  }

  /**
   * Sets the specified host and port.  Uses the default protocol "http".
   *
   * @param host the host(IP or DNS name)
   * @param port The port
   */
  public synchronized void setHost(final String host, int port) {
    setHost(host, port, Protocol.getProtocol("http"));
  }

  /**
   * Set the specified host. Uses the default protocol("http") and deafault port 80.
   *
   * @param host The host(IP or DNS name).
   */
  public synchronized void setHost(final String host) {
    Protocol defaultProtocol = Protocol.getProtocol("http");
    setHost(host, defaultProtocol.getDefaultPort(), defaultProtocol);
  }

  /**
   * Sets the net, host and port from the specified URI.
   *
   * @param uri the URI.
   */
  public synchronized void setHost(final URI uri) {
    try {
      if (params.getBoolean(Parameters.ENCODE_URLS, false)) {
        setHost(uri.getHost(), uri.getPort(), uri.getScheme());
      } else {
        setHost(uri.getHost_notStrict(), uri.getPort(), uri.getScheme());
      }
    } catch (URIException e) {
      throw new IllegalArgumentException(e.toString());
    }
  }

  /**
   * Return the host url.
   *
   * @return The host url.
   */
  public synchronized String getHostURL() {
    if (this.host == null) {
      throw new IllegalStateException("Host must be set to create a host URL");
    } else {
      return this.host.toURI();
    }
  }

  /**
   * Returns the host.
   *
   * @return the host, or <code>null</code> if not set
   */
  public synchronized String getHost() {
    if (this.host != null) {
      return this.host.getHostName();
    } else {
      return null;
    }
  }

  /**
   * Returns the port.
   *
   * @return the host port, or <code>-1</code> if not set
   */
  public synchronized int getPort() {
    if (this.host != null) {
      return this.host.getPort();
    } else {
      return -1;
    }
  }

  /**
   * Returns the protocol.
   *
   * @return The protocol.
   */
  public synchronized Protocol getProtocol() {
    if (this.host != null) {
      return this.host.getProtocol();
    } else {
      return null;
    }
  }

  /**
   * Sets the specified proxy host
   *
   * @param proxyHost the proxy host
   */
  public synchronized void setProxyHost(final ProxyHost proxyHost) {
    this.proxyHost = proxyHost;
  }

  /**
   * Set the proxy host and port.
   *
   * @param proxyHost The proxy host
   * @param proxyPort The proxy port
   */
  public synchronized void setProxy(final String proxyHost, int proxyPort) {
    this.proxyHost = new ProxyHost(proxyHost, proxyPort);
  }

  /**
   * Returns the proxy host.
   *
   * @return the proxy host, or <code>null</code> if not set
   */
  public synchronized String getProxyHost() {
    if (this.proxyHost != null) {
      return this.proxyHost.getHostName();
    } else {
      return null;
    }
  }

  /**
   * Returns the proxy port.
   *
   * @return the proxy port, or <code>-1</code> if not set
   */
  public synchronized int getProxyPort() {
    if (this.proxyHost != null) {
      return this.proxyHost.getPort();
    } else {
      return -1;
    }
  }

  /**
   * Set the local address to be used when creating connections.
   *
   * @param localAddress the local address to use
   */

  public synchronized void setLocalAddress(InetAddress localAddress) {
    this.localAddress = localAddress;
  }

  /**
   * Return the local address to be used when creating connections.
   *
   * @return the local address to be used when creating Sockets, or <code>null</code>
   */

  public synchronized InetAddress getLocalAddress() {
    return this.localAddress;
  }

  /**
   * Returns {@link HttpClientParameters HTTP net parameters} associated with this host.
   *
   * @return HTTP parameters.
   */
  public HttpClientParameters getParams() {
    return this.params;
  }

  /**
   * Sets {@link Parameters HTTP net parameters} to this host.
	 * @param params the parameters to set
	 */
  public void setParams(final HttpClientParameters params) {
    if (params == null) {
      throw new IllegalArgumentException("Parameters is null");
    }
    this.params = params;
  }

  public synchronized boolean equals(final Object o) {
    if (o instanceof HostConfiguration) {
      if (o == this) { // if it's the same object
        return true;
      }
      HostConfiguration that = (HostConfiguration) o;
      return areEqual(this.host, that.host)
             && areEqual(this.proxyHost, that.proxyHost)
             && areEqual(this.localAddress, that.localAddress);
    } else {
      return false;
    }

  }

  private boolean areEqual(final Object obj1, final Object obj2) {
    return obj1 == null ? obj2 == null : obj1.equals(obj2);
  }

  public synchronized int hashCode() {
    int hash = 0;
    if (this.host != null) {
      hash += this.host.hashCode();
    }
    if (this.proxyHost != null) {
      hash += this.proxyHost.hashCode();
    }
    if (this.localAddress != null) {
      hash += this.localAddress.hashCode();
    }
    return hash;
  }

}