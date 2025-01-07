package com.sap.engine.services.httpserver.server.properties;

import java.util.HashMap;

import com.sap.bc.proj.jstartup.sadm.ShmAccessPoint;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.engine.lib.util.ConcurrentHashMapIntObject;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.RequestUrlData;

/**
 * Contain reverse proxy data variables used for determining the reverse proxy.
 * This information is used in getScheme(), getPort(), getHost() methods, redirect methods
 * @author I044270
 *
 */
public class ReverseProxyMappings {
  
  /**
   * collection with all proxy configurations
   */
  //private HashMap<String, ProxyConfiguration> proxyConfigurations = new HashMap<String, ProxyConfiguration>();

  /**
   * collection with icm port and proxy configuration order
   */
  public static HashMap<String, ProxyConfiguration[]> icmPortConfigurationOrder = new HashMap<String, ProxyConfiguration[]>();
  
  //these are necessary for old proxy configurations
  public static ProxyConfigurationOld plainPortOld = null;
  public static ProxyConfigurationOld sslPortOld = null;
  
  /**
   * variable containing the last reverse proxy nested property
   * it is added because of updating proxy mapping configuration
   */
  //public static NestedProperties reverseProxyMappingsNestedP = null;
  public static ConcurrentHashMapIntObject oldConfigurationsPorts;  

  /**
   * previous value associated with specified key, null  if there was no value for key. A null return can also indicate that the HashMap previously associated null with the specified key.
   * @param key
   * @param portValues
   * @return
   */
  public static ProxyConfiguration[] putIcmConfigurationOrder(String key, ProxyConfiguration[] portValues) {
    ProxyConfiguration[] icmPortConf = icmPortConfigurationOrder.put(key, portValues);
    
    return icmPortConf;
  }
  
  public static IProxyConfiguration findProxyConfiguration(MimeHeaders headers, String icmPort) {
    ProxyConfiguration[] proxyConfigurations = (ProxyConfiguration[])(icmPortConfigurationOrder.get(icmPort));
    
    if (proxyConfigurations == null) {
      return null;
    }
    for (int i = 0; i < proxyConfigurations.length; i++) {
      if (proxyConfigurations[i].check(headers)) {
        return proxyConfigurations[i];
      }
    }
    
    return null;
  }
  
  public static IProxyConfiguration findOldProxyMapping(int icmport) {
    if (oldConfigurationsPorts == null) {
      return null;
    }
    return (IProxyConfiguration)oldConfigurationsPorts.get(icmport);
  }
  
  public static RequestUrlData getIcmPlainPortHostScheme() {
    try {
      ShmAccessPoint[] accessPoints =
        ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_HTTP);
      if (accessPoints.length > 0) {
        return new RequestUrlData(accessPoints[0].getPort(), "http",
            accessPoints[0].getAddress().getHostName());
      }
    } catch (ShmException e) {
      Log.logWarning("ASJ.http.000382", "Could not define default proxy mappings.", e, null, null, null);
    }

    return null;
  }
  
  public static RequestUrlData getIcmSslPortHostScheme() {
    try {
      ShmAccessPoint[] accessPoints =
        ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_HTTPS);
      if (accessPoints.length > 0) {
        return new RequestUrlData(accessPoints[0].getPort(), 
            "https", accessPoints[0].getAddress().getHostName());
      }
    } catch (ShmException e) {
      Log.logError("ASJ.http.000383", "Could not define default proxy mappings.", e, null, null, null);
    }

    // This is a workaround when there isn't any https access point.
    // The returned proxy mapping has the host of the http access point,
    // the default https port 443 and https scheme
    try {
      ShmAccessPoint[] accessPoints =
        ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_HTTP);
      if (accessPoints.length > 0) {
        return new RequestUrlData(443, "https", accessPoints[0].getAddress().getHostName());
      }
    } catch (ShmException e) {
      Log.logError("ASJ.http.000384", "Could not define default proxy mappings.", e, null, null, null);
    }

    return null;
  }  
}
