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

import com.sap.httpclient.net.Protocol;

/**
 * Holds all of the variables needed to describe an HTTP connection to a proxy.
 *
 * @author Nikolai Neichev
 */
public class ProxyHost extends HttpHost {

  /**
   * Copy constructor for HttpHost
   *
   * @param httpproxy the HTTP host to copy details from
   */
  public ProxyHost(final ProxyHost httpproxy) {
    super(httpproxy);
  }

  /**
   * Constructor for ProxyHost.
   *
   * @param hostname the hostname (IP or DNS name). Can be <code>null</code>.
   * @param port     the port. Value <code>-1</code> can be used to set default net port
   */
  public ProxyHost(final String hostname, int port) {
    super(hostname, port, Protocol.getProtocol("http"));
  }

  /**
   * Constructor for HttpHost.
   *
   * @param hostname the hostname (IP or DNS name). Can be <code>null</code>.
   */
  public ProxyHost(final String hostname) {
    this(hostname, -1);
  }

  @SuppressWarnings({"CloneDoesntCallSuperClone"})
	public Object clone() throws CloneNotSupportedException {
    return new ProxyHost(this);
  }

}