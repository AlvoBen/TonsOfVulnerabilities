package com.sap.engine.services.httpserver.server.properties;

import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.server.RequestUrlData;

/**
 * Implement ProxyMapping property. It is added for compatibility to deprecated property ProxyMappings
 * @author I044270
 *
 */
public class ProxyConfigurationOld implements IProxyConfiguration{
  private String host;
  private int port;
  private String scheme;
  private boolean override = false;
  private boolean complete = false;
  
  public ProxyConfigurationOld(String host, int port, String scheme, boolean override) {
    this.host = host;
    this.port = port;
    this.scheme = scheme;
    this.override = override;
    setComplete();
  }
  
  private void setComplete() {
    if (scheme != null && host != null && port != -1) {
      complete = true;
    } else {
      complete = false;
    }
  }
  
  public String getHost() {
    return host;
  }
  
  public int getPort() {
    return port;
  }
  
  public String getScheme() {
    return scheme;
  }
  
  public boolean isComplete() {
    return complete;
  }
  
  public boolean isOverride() {
    return override;
  }

  public int getPlainPort() {    
    return port;
  }

  public int getPort(MimeHeaders headers, int requestIcmPort) {
    return port;
  }

  public String getScheme(MimeHeaders headers, int requestIcmPort) {
    return scheme;
  }

  public int getSslPort() {
    return port;   
  }

  public RequestUrlData getPlainUrlData(String requestScheme) {    
    return new RequestUrlData(port, scheme, host, override);
  }

  public RequestUrlData getSslUrlData(String requestScheme) {
    return new RequestUrlData(port, scheme, host, override);
  }
}
