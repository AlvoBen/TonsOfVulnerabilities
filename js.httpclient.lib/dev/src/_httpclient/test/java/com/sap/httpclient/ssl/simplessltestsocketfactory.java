package com.sap.httpclient.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;

import com.sap.httpclient.exception.ConnectTimeoutException;
import com.sap.httpclient.net.TimeoutSocketFactory;
import com.sap.httpclient.net.SecureSocketFactory;
import com.sap.httpclient.server.SimpleSocketFactory;
import com.sap.httpclient.HttpClientParameters;
import com.sap.tc.logging.Location;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.TrustManagerFactory;

public class SimpleSSLTestSocketFactory implements SecureSocketFactory {

  private static final Location LOG = Location.getLocation(SimpleSSLTestSocketFactory.class);

  private static SSLContext SSLCONTEXT = null;

  private static SSLContext createSSLContext() {
    try {
      ClassLoader cl = SimpleSocketFactory.class.getClassLoader();
      URL url = cl.getResource("com/sap/httpclient/ssl/simpleserver.keystore");
      KeyStore keystore = KeyStore.getInstance("jks");
      keystore.load(url.openStream(), "nopassword".toCharArray());
      TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmfactory.init(keystore);
      TrustManager[] trustmanagers = tmfactory.getTrustManagers();
      SSLContext sslcontext = SSLContext.getInstance("TLS");
      sslcontext.init(null, trustmanagers, null);
      return sslcontext;
    } catch (Exception ex) {
      // this is not the way a sane exception handling should be done
      // but for our simple HTTP testing framework this will suffice
      LOG.errorT(ex.getMessage());
      throw new IllegalStateException(ex.getMessage());
    }

  }

  private static SSLContext getSSLContext() {
    if (SSLCONTEXT == null) {
      SSLCONTEXT = createSSLContext();
    }
    return SSLCONTEXT;
  }

  public SimpleSSLTestSocketFactory() {
    super();
  }

  public Socket createSocket(final String host,
                             final int port,
                             final InetAddress localAddress,
                             final int localPort,
                             final HttpClientParameters params) throws IOException {
    if (params == null) {
      throw new IllegalArgumentException("Parameters may not be null");
    }
    int timeout = params.getConnectionTimeout();
    if (timeout == 0) {
      return createSocket(host, port, localAddress, localPort);
    } else {
      // To be eventually deprecated when migrated to Java 1.4 or above
      return TimeoutSocketFactory.createSocket(this, host, port, localAddress, localPort, timeout);
    }
  }

  public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException {
    return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
  }

  public Socket createSocket(String host, int port) throws IOException {
    return getSSLContext().getSocketFactory().createSocket(host, port);
  }

  public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
    return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
  }
}