/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.csiv2.sapprovider;

import com.sap.engine.services.keystore.interfaces.KeyStoreProvider;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.interfaces.csiv2.SSLSocketProvider;

import java.net.Socket;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.io.IOException;

import iaik.security.ssl.*;
import iaik.security.jsse.net.JSSESessionManager;

import javax.naming.Context;

/*

 * @author Jako Blagoev
 * @version 4.0
 */
public class SAPSSLProvider implements SSLSocketProvider {

  private boolean initialized = false;
  private SSLServerContext sslClientContext = null;
  private static CipherSuite[] cipherSuites = null;

  private void init() {
    KeyAndCert credentials = null;
    try {
      cipherSuites = (new CipherSuiteList(CipherSuiteList.L_ALL)).toArray();

      Properties prop = new Properties();
      prop.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
      prop.put("appclient", "true");
      KeyStore ks = KeyStoreProvider.getKeyStore(KeyStoreProvider.KV_ALIAS_DEFAULT, prop);
      if (ks != null) {
        PrivateKey key = (PrivateKey) ks.getKey("appclient-ssl-credential", new char[0]);
        Certificate[] cert  = ks.getCertificateChain("appclient-ssl-credential");
        credentials = new KeyAndCert((X509Certificate[]) cert, key);
      }
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beWarning()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).warningT("SAPSSLProvider.init()", "SAPSSLProvider can't get credentials, caused by : \n" + LoggerConfigurator.exceptionTrace(ex));
      }
    }

    sslClientContext = new SSLServerContext();
    if (credentials != null) {
      sslClientContext.addClientCredentials(credentials);
      sslClientContext.addServerCredentials(credentials);
      sslClientContext.setSessionManager(new JSSESessionManager());
      sslClientContext.setChainVerifier(null);
      sslClientContext.setEnabledCipherSuites(cipherSuites);
      sslClientContext.updateCipherSuites();
    }
  }

  public Socket getSSLClientSocket(String host, int port) throws IOException {
    if (!initialized) {
      init();
    }

    SSLSocket sslsocket = new SSLSocket(new Socket(host, port), sslClientContext, host, port);
    sslsocket.setDebugStream(System.out);
    sslsocket.startHandshake();
    return sslsocket;
  }

  public ServerSocket getSSLServerSocket(int port) throws IOException {
    return null;
  }
}
