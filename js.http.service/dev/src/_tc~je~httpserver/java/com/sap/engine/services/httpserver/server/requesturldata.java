package com.sap.engine.services.httpserver.server;

/**
 * This class is used for reverse proxy mapping settings. It contains data for the scheme, port and host
 * of the proxy
 * @author I044270
 *
 */
public class RequestUrlData {
  private int port =-1;
  private String scheme;
  private String host;
  private boolean override;
  
  public RequestUrlData(int port, String scheme, String host) {
    this(port, scheme, host, false);
  }

  public RequestUrlData(int port, String scheme, String host, boolean override) {
    this.port = port;
    if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
      this.scheme = scheme;
    } else {
      this.scheme = "http";
    }
    
    this.host = host;
    this.override = override;
  }
  
  public int getPort() {
    return port;
  }

  public String getScheme() {
    return scheme;
  }

  public String getHost() {
    return host;
  }

  public boolean isOverride() {
    return override;
  }
}
