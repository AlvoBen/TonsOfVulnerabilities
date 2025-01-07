package com.sap.bc.cts.tp.net;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Java Change Management May 13, 2004
 */
public interface ServerSocketFactory {
  public ServerSocket create(int port) throws IOException;
}
