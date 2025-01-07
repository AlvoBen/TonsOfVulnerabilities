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

import java.io.IOException;
import java.net.Socket;

/**
 * A ProtocolSocketFactory that is secure.
 *
 * @author Nikolai Neichev
 */
public interface SecureSocketFactory extends ProtocolSocketFactory {

  /**
   * Returns a socket connected to the specified host that is layered over an existing socket.
   * Used for creating secure sockets through proxies.
   *
   * @param socket    the existing socket
   * @param host      the host name/IP
   * @param port      the port on the host
   * @param autoClose a flag for closing the underlying socket when the created socket is closed
   * @return Socket a new socket
   * @throws IOException          if an I/O error occurs while creating the socket
   */
  Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException;

}