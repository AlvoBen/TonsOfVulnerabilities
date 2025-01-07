/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.properties;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_SSL_ATTRIBUTES;

import com.sap.bc.proj.jstartup.JStartupFramework;
import com.sap.engine.services.httpserver.interfaces.properties.ProxyServersProperties;
import com.sap.engine.services.httpserver.server.Log;

public class ProxyServersPropertiesImpl implements ProxyServersProperties {  
  //Profile Parameters - currently we will read them only on service startup
  //TODO: this may lead to inconsistency if only ICM is restarted
  private static final String CLIENT_CERT_HEADER_NAME_ICMPARAM = "icm/HTTPS/client_certificate_header_name";
  private static final String CLIENT_CERT_CHAIN_HEADER_PREFIX_ICMPARAM = "icm/HTTPS/client_certificate_chain_header_prefix";
  private static final String CLIENT_KEY_SIZE_HEADER_NAME_ICMPARAM = "icm/HTTPS/client_key_size_header_name";
  private static final String CLIENT_CIPHER_SUITE_HEADER_NAME_ICMPARAM = "icm/HTTPS/client_cipher_suite_header_name";
  
  /** Header with value HTTP or HTTPS depending on the client protocol */
  private String protocolHeaderName = "ClientProtocol";
  private byte[] clientCertificateHeaderName;
  private String clientCertificateChainHeaderPrefix;
  private byte[] clientKeySizeHeaderName;
  private byte[] clientCipherSuiteHeaderName;
  
  public ProxyServersPropertiesImpl() {
    loadICMProperties();
  }

  public void loadICMProperties() {
    String previousValues = null;
    if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
      previousValues = "Previous values are: " + getValuesMsg();
    }
    
    clientCertificateHeaderName = getProfileParamBytes(CLIENT_CERT_HEADER_NAME_ICMPARAM);  	  
    clientCertificateChainHeaderPrefix = 
      JStartupFramework.getParam(CLIENT_CERT_CHAIN_HEADER_PREFIX_ICMPARAM);
    clientKeySizeHeaderName = getProfileParamBytes(CLIENT_KEY_SIZE_HEADER_NAME_ICMPARAM);
    clientCipherSuiteHeaderName = getProfileParamBytes(CLIENT_CIPHER_SUITE_HEADER_NAME_ICMPARAM);
    
    //They should not be null - if not set, default values should be used in ICM: 
    if (clientCertificateHeaderName == null || clientCertificateChainHeaderPrefix == null ||
      clientKeySizeHeaderName == null || clientCipherSuiteHeaderName == null) { 
      Log.logError("ASJ.http.000361", 
          "Failed to load profile parameters for proxy server properties. Current values are {0}.",
          new Object[]{getValuesMsg()},
          null, null, null);
    }
    
    if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
      Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, 
          "SSL properties reloaded from profile. " + previousValues +
          ". Current values are: " + getValuesMsg(),
          -1, "ProxyServersPropertiesImpl", "loadICMProperties");
    }
  }
  
  private byte[] getProfileParamBytes(String paramName) {
      String profileParameter = JStartupFramework.getParam(paramName);
      if (profileParameter != null) {
        return profileParameter.getBytes();
      } else {
        return null;
      }
  }

  public byte[] getClientCertificateHeaderName() {
    return clientCertificateHeaderName;
  }

  public String getClientCertificateChainHeaderPrefix() {
    return clientCertificateChainHeaderPrefix;
  }

  public byte[] getClientKeySizeHeaderName() {
    return clientKeySizeHeaderName;
  }

  public byte[] getClientCipherSuiteHeaderName() {
    return clientCipherSuiteHeaderName;
  }

  public String getProtocolHeaderName() {
    return protocolHeaderName;
  }
  
  public void setProtocolHeaderName(String protocolHeaderName) {
    this.protocolHeaderName = protocolHeaderName;
  }
  
  private String getString(byte[] value) {
    if (value == null) {
      return "null";
    } else {
      return new String(value);
    }
  }
  
  private String getValuesMsg() {
    return "clientCertificateHeaderName=" + getString(clientCertificateHeaderName) +
      ", clientCertificateChainHeaderPrefix=" + clientCertificateChainHeaderPrefix +
      ", clientKeySizeHeaderName=" + getString(clientKeySizeHeaderName) +
      ", clientCipherSuiteHeaderName=" + getString(clientCipherSuiteHeaderName);
  }
}
