package com.sap.httpclient.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Defines a plain socket factory
 */
public class SimplePlainSocketFactory implements SimpleSocketFactory {

  public SimplePlainSocketFactory() {
    super();
  }

  public ServerSocket createServerSocket(int port) throws IOException {
    return new ServerSocket(port);
  }

}