package com.sap.engine.transport;

import com.sap.engine.frame.cluster.transport.TransportFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Properties;

public class DefaultTransportFactory implements TransportFactory {

  public ServerSocket getServerSocket(int port) throws IOException {
    return new ServerSocket(port);
  }

  public ServerSocket getServerSocket(int port, int acceptSize, String bindAddr) throws IOException {
    return new ServerSocket(port, acceptSize, InetAddress.getByName(bindAddr));
  }

  public ServerSocket getServerSocket(int port, int acceptSize) throws IOException {
    return new ServerSocket(port, acceptSize);
  }

  public Socket getSocket(String host, int port) throws UnknownHostException, IOException {
    return new Socket(host, port);
  }

  public Socket getSocket(String host, int port, Properties props) throws UnknownHostException, IOException {
    return new Socket(host, port);
  }

  public void setFactory(TransportFactory factory) {
    // do nothing, this is a final factory
  }

}

