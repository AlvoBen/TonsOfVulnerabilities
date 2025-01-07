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

import com.sap.engine.interfaces.csiv2.SSLSocketProvider;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import iaik.security.ssl.*;
import iaik.security.jsse.net.JSSESessionManager;
import iaik.pkcs.pkcs12.PKCS12;
import iaik.pkcs.pkcs12.CertificateBag;


/*

 * @author Ivan Atanassov
 * @version 4.0
 */
public class IAIKSSLSocketProvider implements SSLSocketProvider {
  public static boolean debug = false;

  private boolean initialized = false;
  private SSLServerContext sslClientContext = null;
  private static CipherSuite[] cipherSuites = null;

  private String pkcs12_fname;
  private String password;

  public IAIKSSLSocketProvider() {
  }

  public IAIKSSLSocketProvider(String pkcs12_fname, String password) {
    this.pkcs12_fname = pkcs12_fname;
    this.password = password;
  }

  public Socket getSSLClientSocket(String host, int port) throws IOException {
    if (!initialized) {
      cipherSuites = (new CipherSuiteList(CipherSuiteList.L_ALL)).toArray();
      sslClientContext = new SSLServerContext();
      sslClientContext.setSessionManager(new JSSESessionManager());
      sslClientContext.setChainVerifier(null);
      sslClientContext.setEnabledCipherSuites(cipherSuites);
  //    sslClientContext.updateCipherSuites();
      initialized = true;
    }

    SSLSocket sslsocket = new SSLSocket(new Socket(host, port), sslClientContext, host, port);
    if (debug) {
      sslsocket.setDebugStream(System.out);
    }
    sslsocket.startHandshake();
    return sslsocket;
  }

  public ServerSocket getSSLServerSocket(int port) throws IOException {
    try {

      PKCS12 pkcs12 = new PKCS12(new FileInputStream(pkcs12_fname));
      pkcs12.decrypt(password.toCharArray());
      PrivateKey pk = pkcs12.getKeyBag().getPrivateKey();
      CertificateBag[] certs = pkcs12.getCertificateBags();
      X509Certificate cert =  certs[0].getCertificate();

      KeyAndCert credentials = new KeyAndCert(new X509Certificate[]{cert}, pk);
      CipherSuite[] cipherSuites = (new CipherSuiteList(CipherSuiteList.L_ALL)).toArray();
      SSLServerContext sslContext = new SSLServerContext();
      sslContext.addClientCredentials(credentials);
      sslContext.addServerCredentials(credentials);
      sslContext.setSessionManager(new JSSESessionManager());
      sslContext.setChainVerifier(null);
      sslContext.setEnabledCipherSuites(cipherSuites);
      sslContext.updateCipherSuites();
      if (debug) {
        sslContext.setDebugStream(System.out);
      }
      SSLServerSocket ss =  new SSLServerSocket(port, sslContext);
      return ss;
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IAIKSSLSocketProvider.getSSLServerSocket(int)", "Exception while opening SSL server socket, caused by : \n" + LoggerConfigurator.exceptionTrace(e));
      }
      return null;
    }
  }
}
