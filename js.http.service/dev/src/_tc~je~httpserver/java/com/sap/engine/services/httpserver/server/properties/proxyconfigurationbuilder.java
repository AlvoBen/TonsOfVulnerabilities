package com.sap.engine.services.httpserver.server.properties;

import com.sap.engine.services.httpserver.server.Log;

/**
 * Builder class for ProxyConfiguration
 * @author I044270
 *
 */
public class ProxyConfigurationBuilder {

  String proxyAlias; 
  int httpProxyPort=-1;  
  int httpsProxyPort=-1;  
  String proxyHost;  
  int icmHttpPort=-1;  
  int icmHttpsPort=-1;  
  String sslProxyRedirect;
  String sslProxyReplace; 
  String httpProxyRedirect;
  String httpProxyReplace;
  boolean override=false;  
  ViaHeader viaHeader;  
  ClientProtocolHeader clientProtocol;
  
  
  public ProxyConfigurationBuilder() {
    
  }
  
  public ProxyConfigurationBuilder setProxyAlias(String proxyAlias) {
    if (proxyAlias == null || proxyAlias.equals("")){
      throw new IllegalArgumentException("Reverse Proxy Mappings property. Proxy alias could not be null");
    }
    
    this.proxyAlias = proxyAlias;
    return this;
  }
  
  public ProxyConfigurationBuilder setHttpProxyPort(String httpProxyPort) {
    try {
      this.httpProxyPort = Integer.parseInt(httpProxyPort);
    }
    catch (NumberFormatException ex) {
      Log.logWarning("ASJ.http.000370", "Can not parse httpProxyPort of reverse proxy configuration.",
          ex, null, null, null);     
    }
    return this;
  }
  
  
  public ProxyConfigurationBuilder setHttpsProxyPort(String httpsProxyPort) {
    try {
      this.httpsProxyPort = Integer.parseInt(httpsProxyPort);
    }
    catch (NumberFormatException ex) {
      Log.logWarning("ASJ.http.000371", "Can not parse httpsProxyPort of reverse proxy configuration.",
          ex, null, null, null);      
    }
    return this;
  }
  
  public ProxyConfigurationBuilder setProxyHost(String proxyHost) {
    if (proxyHost == null) {      
      this.proxyHost = "";
    }
    else {
      this.proxyHost = proxyHost;
    }
    return this;
  }
  
  /**
   * if icmHttpPort is -1, there is no icmHttpPort set in the property configuration
   * @param icmHttpPort
   * @return
   */
  public ProxyConfigurationBuilder setIcmHttpPort(String icmHttpPort) {
    try {
      this.icmHttpPort = Integer.parseInt(icmHttpPort);
    }
    catch (NumberFormatException ex){
      Log.logWarning("ASJ.http.000372", "Can not parse icmHttpPort of reverse proxy configuration.",
          ex, null, null, null);      
    }
    return this;
  }
  
  /**
   * if icmHttpsPort is -1, there is no icmHttpsPort set in the property configuration
   * @param icmHttpsPort
   * @return
   */
  public ProxyConfigurationBuilder setIcmHttpsPort(String icmHttpsPort) {
    try {
      this.icmHttpsPort = Integer.parseInt(icmHttpsPort);
    }
    catch (NumberFormatException ex){
      Log.logWarning("ASJ.http.000369", "Can not parse icmHttpsPort of reverse proxy configuration.",
          ex, null, null, null);      
    }
    return this;
  }
  
  /**
   * if sslProxyRedirect is null or empty string there is no sslProxyRedirect in the property configuration
   * @param sslProxyRedirect
   * @return
   */
  public ProxyConfigurationBuilder setSslProxyRedirect(String sslProxyRedirect) {
    if (sslProxyRedirect == null) {
      this.sslProxyRedirect = "";
    }
    else {
      this.sslProxyRedirect = sslProxyRedirect;
    }
    return this;
  }
  
  /**
   * if sslProxyReplace is null or empty string there is no sslProxyReplace in the property configuration
   * @param sslProxyReplace
   * @return
   */
  public ProxyConfigurationBuilder setSslProxyReplace(String sslProxyReplace) {
    if (sslProxyReplace == null) {
      this.sslProxyReplace = "";
    }
    else {
      this.sslProxyReplace = sslProxyReplace;
    }
    return this;
  }
  
  public ProxyConfigurationBuilder setOverride(boolean override) {
    this.override = override;
    return this;
  }
  
  /**
   * If viaHeader is null there is no viaHeader in the property configuration
   * @param viaHeader
   * @return
   */
  public ProxyConfigurationBuilder setViaHeader(ViaHeader viaHeader) {
    this.viaHeader = viaHeader;
    return this;
  }
  
  /**
   * If clientProtocol is null there is no client protocol in the property configuration
   * @param clientProtocol
   * @return
   */
  public ProxyConfigurationBuilder setClientProtocol(ClientProtocolHeader clientProtocol) {
    this.clientProtocol = clientProtocol;
    return this;
  }
  
  public ProxyConfiguration build() {  
    return new ProxyConfiguration(this);  
  }

  public ProxyConfigurationBuilder setHttpProxyRedirect(String httpProxyRedirect) {
    if (httpProxyRedirect == null) {
      this.httpProxyRedirect = "";
    }
    else {
      this.httpProxyRedirect = httpProxyRedirect;
    }
    return this;
  }

  public ProxyConfigurationBuilder setHttpProxyReplace(String httpProxyReplace) {
    if (httpProxyReplace == null) {
      this.httpProxyReplace = "";
    }
    else {
      this.httpProxyReplace = httpProxyReplace;
    }
    return this;
  } 
}
