package com.sap.engine.services.httpserver.server.properties;

import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.server.RequestUrlData;

/**
 * Proxy configuration interface - combines common methods for old (ProxyMapping property)
 * and new Proxy configurtations - ReverseProxyMappings property
 * @author I044270
 *
 */
public interface IProxyConfiguration {
  
  public boolean isOverride();
  
  public String getHost();
  
  public int getPort(MimeHeaders headers, int requestIcmPort);
  
  public String getScheme(MimeHeaders headers, int requestIcmPort);
  
  public int getSslPort();
  
  public int getPlainPort();
  
  public RequestUrlData getSslUrlData(String requestScheme);
  
  public RequestUrlData getPlainUrlData(String requestScheme);
}
