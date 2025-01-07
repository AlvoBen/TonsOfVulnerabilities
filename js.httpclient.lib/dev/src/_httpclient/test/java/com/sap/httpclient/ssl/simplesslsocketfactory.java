package com.sap.httpclient.ssl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.security.KeyStore;
import javax.net.ServerSocketFactory;

import com.sap.httpclient.server.SimpleSocketFactory;
import com.sap.tc.logging.Location;
import com.sun.net.ssl.KeyManager;
import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;

/**
 * Defines a SSL socket factory
 */
public class SimpleSSLSocketFactory implements SimpleSocketFactory {

  private static final Location LOG = Location.getLocation(SimpleSocketFactory.class);

  private static SSLContext SSLCONTEXT = null;

  private static SSLContext createSSLContext() {
    try {
      ClassLoader cl = SimpleSocketFactory.class.getClassLoader();
      URL url = cl.getResource("com/sap/httpclient/ssl/simpleserver.keystore");
      KeyStore keystore = KeyStore.getInstance("jks");
      keystore.load(url.openStream(), "nopassword".toCharArray());
      KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmfactory.init(keystore, "nopassword".toCharArray());
      KeyManager[] keymanagers = kmfactory.getKeyManagers();
      SSLContext sslcontext = SSLContext.getInstance("TLS");
      sslcontext.init(keymanagers, null, null);
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

  public SimpleSSLSocketFactory() {
    super();
  }

  public ServerSocket createServerSocket(int port) throws IOException {
    ServerSocketFactory socketfactory = getSSLContext().getServerSocketFactory();
    return socketfactory.createServerSocket(port);
  }

}