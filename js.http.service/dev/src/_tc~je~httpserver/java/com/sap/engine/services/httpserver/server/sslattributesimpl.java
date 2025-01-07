/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_REQUEST;
import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_SSL_ATTRIBUTES;

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.engine.lib.security.Base64;
import com.sap.engine.lib.util.ArrayObject;
import com.sap.engine.lib.util.HashMapIntObject;
import com.sap.engine.services.httpserver.interfaces.client.SslAttributes;
import com.sap.engine.services.httpserver.interfaces.properties.ProxyServersProperties;

import iaik.security.ssl.CipherSuite;
import iaik.security.ssl.CipherSuiteList;

import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.net.ssl.SSLPeerUnverifiedException;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

public class SslAttributesImpl implements SslAttributes {
  /**
   * Holds correspondence between cipher suite ID (int) and cipher suite name (String)
   */
  private static final HashMapIntObject cipherSuiteMap;
  
  private static CertificateFactory certFactory = null;
  /**
   * Contains the end-user's client certificate chain.   
   * 
   * certificates[0] is the end-user's client certificate
   * certificates[1] is the first certificate in the chain 
   * certificates[n] is the last certificate in the chain before the root certificate
   */
  private X509Certificate[] certificates = null;
  /** Cipher suite name */
  private String cipherSuite = null;
  private int keySize = -1;

  private Client client = null;
  
  // Fills the cipher suite id to name map 
  static {
    cipherSuiteMap = new HashMapIntObject();
    CipherSuiteList csl = new CipherSuiteList(CipherSuiteList.L_ALL);
    CipherSuite[] css = csl.toArray();
    for (int i = 0; i < css.length; i++) {
      cipherSuiteMap.put(css[i].getID(), css[i].getName());
    }
  }

  public SslAttributesImpl() {
  }

  private void initCertFactory() {
    try {
      certFactory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException ce) {
      Log.logWarning("ASJ.http.000095", 
        "Cannot get an instance of the CertificateFactory." + ce, null, null, null);
    }
  }
  
  public void reset() {
      certificates = null;
      cipherSuite = null;
      keySize = -1;
  }

  public void init(SslAttributesImpl oldAttributes, Client newClient) {
    certificates = oldAttributes.certificates;
    cipherSuite = oldAttributes.cipherSuite;
    client = newClient;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public X509Certificate[] getCertificates() {
    return certificates;
  }
  
  public void setCertificates(X509Certificate[] certificates) {
    this.certificates = certificates;
  }

  public String getCipherSuite() {
    return cipherSuite;
  }
  
  public void setCipherSuite(byte[] cipherSuiteId) {
    cipherSuite = getCipherSuiteName(cipherSuiteId);;
  }

  public int getKeySize() {
    return keySize;
  }
  
  public void setKeySize(int keySize) {
    this.keySize = keySize;
  }
  
  /**
   * Loads the SSL Data from the request headers.
   */
  public void loadSSLAttributesFromHeaders() {
    ProxyServersProperties proxyServersProperties = ServiceContext.getServiceContext().getHttpProperties().getProxyServersProperties();

    initCipherSuiteAndKeySize(proxyServersProperties);
    
    String certChainHeaderPrefix = proxyServersProperties.getClientCertificateChainHeaderPrefix();
    byte[] certHeaderName = proxyServersProperties.getClientCertificateHeaderName();
    if (certHeaderName != null) {
      byte[] cert = client.getRequest().getHeaders().getHeader(certHeaderName);      
      if (cert != null) {
        //because Client certificate header is found, load all certificate data only from headers:        
        initCertificateAndChain(proxyServersProperties, certHeaderName, cert, certChainHeaderPrefix);
      }
    }
  }
  
  private String getString(byte[] certHeaderName) {
    return (certHeaderName != null ? new String(certHeaderName) : "null");
  }

  /**
   * Initializes the certificate factory, generates the certificate object
   * and each certificate from the chain (if there are certificate chain request headers). 
   */
  private void initCertificateAndChain(ProxyServersProperties proxyServersProperties, 
      byte[] certHeaderName, byte[] cert, String certChainHeaderPrefix) {
    
    if (certFactory == null) {
      initCertFactory();
      if (certFactory == null) {
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES,
                  "Will not load certificate chains because initialization of certificate factory has failed.",
                  client.getClientId(), "SslAttributesImpl", "initCertificateAndChain()");
        }
        return;
      }
    }

    ArrayObject chainCerts = null;

    if (certChainHeaderPrefix != null) {
      for (int i = 0; i < client.getRequest().getHeaders().size(); i++) {
        String value = client.getRequest().getHeaders().getHeader(certChainHeaderPrefix + (i + 1));
        if (value == null) {
          break;
        }
        if (chainCerts == null) {
          chainCerts = new ArrayObject(client.getRequest().getHeaders().size());
        }
        chainCerts.add(value.trim().getBytes());
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, "Certificate [" + value.trim() + 
              "] added to chainCertificates", client.getClientId(), "SslAttributesImpl", "initCertificates()");
        }
      }// for
    } else {
      if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
        Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES,
                "Cannot load certificate chain from SSL headers: the certificate chain header prefix parameter cannot be read. "
                    + getPropertiesTrcMsg(proxyServersProperties), client
                    .getClientId(), "SslAttributesImpl", "initCertificates()");
      }
    }
    if (chainCerts == null) {
      certificates = new X509Certificate[1];
      if (!initNextCertificate(cert, 0)) {
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES,
              "Decoding or generation of certificate from [" + new String(certHeaderName)
                  + "] request header failed: certificates[0]="
                  + certificates[0], client.getClientId(), "SslAttributesImpl", "initCertificates()");
        }
      } else {
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES,
              "Decode only the certificate from [" + new String(certHeaderName)
                  + "] request header", client.getClientId(), "SslAttributesImpl", "initCertificates()");
        }
      }
    } else {
      certificates = new X509Certificate[1 + chainCerts.size()];
      boolean successfull = initNextCertificate(cert, 0);
      for (int i = 0; i < certificates.length - 1; i++) {
        successfull = successfull
            && initNextCertificate((byte[]) chainCerts.elementAt(i), i + 1);
      }
      if (!successfull) {
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES,
              "Decoding or generation of certificate from ["
                  + new String(certHeaderName) + "] request header and from ["
                  + certChainHeaderPrefix + "] certificate chain failed!",
              client.getClientId(), "SslAttributesImpl", "initCertificates()");
        }
      } else {
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES,
              "Decode the certificate from [" + new String(certHeaderName)
                  + "] request header and from [" + certChainHeaderPrefix
                  + "] certificate chain", client.getClientId(),
              "SslAttributesImpl", "initCertificates()");
        }
      }
    }
  } //initCertificateAndChain


  // ------------------------ PRIVATE ------------------------

  private boolean initNextCertificate(byte[] cert, int certificatesOffset) {
    boolean successfull = false;
    try {
      cert = Base64.decode(cert);
    } catch (Exception t) {
      Log.logWarning("ASJ.http.000096", "Proxy server certificate decoding failed.", t, null, null, null);
      if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
	  Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, "Certificate decoding failed", 
		  t, client.getClientId(), "SslAttributesImpl", "initNextCertificate(byte[], int)");
      }
    }
    ByteArrayInputStream bais = new ByteArrayInputStream(cert);
    try {
      certificates[certificatesOffset] = (X509Certificate) certFactory.generateCertificate(bais);
      successfull = true;
    } catch (CertificateException ce) {
      Log.logWarning("ASJ.http.000097", "Certificate generation failed. {0}", new Object[]{ce}, null, null, null);
      if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
	  Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, "Certificate generation failed.", 
		  ce, client.getClientId(), "SslAttributesImpl", "initNextCertificate(byte[], int)");
      }
    }
    return successfull;
  }

  /**
   * Reads and initializes the cipher suite and key size from request headers
   */
  private void initCipherSuiteAndKeySize(ProxyServersProperties proxyServersProperties) {
    
    byte[] cipherSuiteBytes = null;
    byte[] cipherSuiteHeaderName = proxyServersProperties.getClientCipherSuiteHeaderName();
    if (cipherSuiteHeaderName != null) {
      cipherSuiteBytes = client.getRequest().getHeaders().getHeader(cipherSuiteHeaderName);
    }
    if (cipherSuiteBytes != null) {
      cipherSuite = new String(cipherSuiteBytes);
    } else {
      cipherSuite = null;
    }
    byte[] keySizeBytes = null;
    byte[] keySizeHeaderName = proxyServersProperties.getClientKeySizeHeaderName();
    if (keySizeHeaderName != null) {
      keySizeBytes = client.getRequest().getHeaders().getHeader(keySizeHeaderName);
    }
    if (keySizeBytes != null) {
      try {
        keySize = new Integer(new String(keySizeBytes).trim()).intValue(); 
      } catch (NumberFormatException e) {
        Log.logWarning("ASJ.http.000098", 
          "Received unexpected client key size value in header [{0}]. " +
          "The value is [{1}]. Client key size value must be an integer.", 
          new Object[]{new String(keySizeHeaderName), new String(keySizeBytes)}, e, client.getIP(), null, null);
      }
    }
    //When one of them is found in headers but not both, log an error message:
    if ((cipherSuiteBytes == null && keySizeBytes != null) || (keySizeBytes == null && cipherSuiteBytes != null)) {
      Log.logError("ASJ.http.000411", 
          "Inconsistent SSL headers - one of the cipher suite ({0}) or key size ({1}) is missing - they should be both present or not.", 
          new Object[]{cipherSuite, keySizeBytes}, null, null, null);
    }
    //Trace the values:
    if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
      Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, "cipherSuite value [" + cipherSuite + "] read from [" + 
          getString(cipherSuiteHeaderName) + "] request header; keySize value [" + keySize + "] read from [" + 
          getString(keySizeHeaderName) + "] request header", 
        client.getClientId(), "SslAttributesImpl", "initSipherSuiteAndKeySize(ProxyServersProperties)");
    }
  }

  
  /**
   * Defines the string representation of cpher suite from its byte
   * representation
   * 
   * @param idAsBytes
   * byte representation of cipher suite
   * 
   * @return
   * Returns the string representation of cipher suite from passed bytes
   * 
   * @throws
   * <code>NullPointerException</code> if input parameter is <code>null</code>
   */
  private String getCipherSuiteName(byte[] idAsBytes) {
    int idAsInt = 0;
    int length = (idAsBytes.length > 4) ? 4 : idAsBytes.length;
    for (int i = 0; i < length; i++) {
      int shift = (length - 1 - i) * 8;
      idAsInt += (idAsBytes[i] & 0x000000FF) << shift;
    }

    String name = (String)cipherSuiteMap.get(idAsInt);
    if (name == null) {
      name = "Unknown Ciphersuite with ID 0x" + Integer.toHexString(idAsInt);
    }
    
    return name;
  }
  
  public void loadSSLAttributesFromFCA() {
      FCAConnection connection = client.getConnection();
      int client_id = client.getClientId();
	  this.setKeySize(connection.getKeySize());
	  //Uses the cipherSuiteId from FCA to set the name using the mapping from iaik:
	  this.setCipherSuite(connection.getCipherSuiteId());
	  try {
	    //FCA returns the identity of the peer which was established as part of defining the session.
      //certificateChain[0] must be the end-user's client certificate;
      //Throws SSLPeerUnverifiedException and CertificateException in the last version and FCAException in previous BCOs.
      //The SSLPeerUnverifiedException tells that there is no certificate data available, and 
      //The CertificateException tells that the provided data does not resemble a valid certificate. 
      this.setCertificates(connection.getPeerCertificateChain());
      if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
        Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, 
            "Ssl attributes read from the socket." + getConnectionTrcMsg(connection), 
            client_id, "SslAttributesImpl", "loadSSLAttributesFromFCA(FCAConnection, int, int)");
      }
      if (LOCATION_HTTP_REQUEST.bePath()) {
        Log.tracePath(LOCATION_HTTP_REQUEST, 
            "Ssl attributes read from the socket." + getConnectionTrcMsg(connection), 
            client_id, "SslAttributesImpl", "loadSSLAttributesFromFCA(FCAConnection, int, int)");
      }
	  } catch (IOException fcae) { 
	    //former FCAException , after change in BCO46 /2007 -  SSLPeerUnverifiedException
	    // both exceptions extend IOException, and because there is the case when sometime it
	    // needs to be switched to an old BCO(QAs investigate performance issues), we decided to catch IOException	    	  
	    
	    //SSLPeerUnverifiedException means there is no certificate data available, log warning:
	    Log.logWarning("ASJ.http.000346", "Client certificate error.", fcae, null, null, null);
	    if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
	      Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, 
	          "Client certificate error. " + getConnectionTrcMsg(connection) + " " + getTraceMsg(), 
	          fcae, client_id, "SslAttributesImpl", "loadSSLAttributesFromFCA(FCAConnection, int, int)");
	      }
	  } catch (CertificateException ce) {
	    this.setCertificates(new X509Certificate[]{}); //in order to read the keysize and cipher suite later in wc, see HttpServletRequestFacade.init(...)
	    Log.logError("ASJ.http.000362", "Client certificate error.", ce, null, null, null);
	    Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, 
	        "Client certificate error. " + getConnectionTrcMsg(connection) + " " + getTraceMsg(), 
	        ce, client_id, "SslAttributesImpl", "loadSSLAttributesFromFCA(FCAConnection, int, int)");
	  }
  }

  private String getConnectionTrcMsg(FCAConnection connection) {
    String result = "connection.isSecure() =  " + connection.isSecure() + "; sslAttributes = " + this +
    "; connection.getKeySize() = " + connection.getKeySize() + 
    "; connection.getCipherSuiteId() = " + connection.getCipherSuiteId() + 
    "; connection.getPeerCertificateChain() = ";
    try {
      result += connection.getPeerCertificateChain();
    } catch (Exception e) {
      result += "exception: " + e.getMessage();
    }
    return result;
  }
  
  private String getPropertiesTrcMsg(ProxyServersProperties proxyServersProperties) {
    return "Current proxy server properties are: " + "clientCertificateHeaderName=" + proxyServersProperties.getClientCertificateHeaderName() +
      ", clientCertificateChainHeaderPrefix=" + proxyServersProperties.getClientCertificateChainHeaderPrefix() +
      ", clientKeySizeHeaderName=" + proxyServersProperties.getClientKeySizeHeaderName() +
      ", clientCipherSuiteHeaderName=" + proxyServersProperties.getClientCipherSuiteHeaderName();
  }
  
  private String getTraceMsg() {
    String result = "sslAttributes = " + this + 
      ", this.keySize = " + keySize +
      ", this.cipherSuite = " + cipherSuite +
      ", this.certificates = " + certificates;
    if (this.certificates != null) {
      result += "(size = " + certificates.length + ")";
    }
    return result;
  }
}
