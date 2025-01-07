package com.sap.httpclient.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Defines a socket factory interface
 */
public interface SimpleSocketFactory {

  ServerSocket createServerSocket(int port) throws IOException;

}
